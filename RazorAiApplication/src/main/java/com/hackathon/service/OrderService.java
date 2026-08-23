package com.hackathon.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.dto.CheckoutProposalDto;
import com.hackathon.dto.OrderDto;
import com.hackathon.dto.SafetyCheckDetailDto;
import com.hackathon.exception.CommerceException;
import com.hackathon.exception.ResourceNotFoundException;
import com.hackathon.model.*;
import com.hackathon.model.enums.ActorType;
import com.hackathon.model.enums.AuditEventType;
import com.hackathon.model.enums.OrderStatus;
import com.hackathon.repository.CartRepository;
import com.hackathon.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final PaymentSafetyService paymentSafetyService;
    private final ProductService productService;
    private final AuditTrailService auditTrailService;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        PaymentSafetyService paymentSafetyService,
                        ProductService productService,
                        AuditTrailService auditTrailService,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.paymentSafetyService = paymentSafetyService;
        this.productService = productService;
        this.auditTrailService = auditTrailService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CheckoutProposalDto createCheckoutProposal(String sessionId) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CommerceException("CART_NOT_FOUND", "No active cart found for session " + sessionId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new CommerceException("CART_EMPTY", "Cannot checkout an empty cart.");
        }

        SafetyCheckDetailDto precheck = paymentSafetyService.performPrecheck(cart);

        String idempotencyKey = "IDEMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String orderNumber = "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-" + (int)(Math.random() * 900 + 100);

        Order order = new Order(orderNumber, sessionId, precheck.getCalculatedAmount(), "INR", idempotencyKey);
        order.setStatus(OrderStatus.DRAFT);
        order.setCustomerConfirmed(false);

        for (CartItem cartItem : cart.getItems()) {
            Product dbProduct = productService.getProductEntity(cartItem.getProduct().getId());
            OrderItem orderItem = new OrderItem(order, dbProduct, dbProduct.getName(), cartItem.getQuantity(), dbProduct.getPrice(), cartItem.isUpsellItem());
            order.getItems().add(orderItem);
        }

        Order saved = orderRepository.save(order);

        auditTrailService.logEvent(sessionId, saved.getOrderNumber(), null,
                AuditEventType.CHECKOUT_PROPOSED,
                ActorType.AI_AGENT,
                "Checkout proposal created for Order " + saved.getOrderNumber() + " with total ₹" + saved.getTotalAmount() + ". Awaiting explicit customer confirmation.",
                saved.getTotalAmount(),
                true,
                Map.of("orderNumber", saved.getOrderNumber(), "idempotencyKey", idempotencyKey, "itemsCount", saved.getItems().size(), "total", saved.getTotalAmount()));

        CheckoutProposalDto proposal = new CheckoutProposalDto();
        proposal.setOrderNumber(saved.getOrderNumber());
        proposal.setSessionId(sessionId);
        proposal.setIdempotencyKey(idempotencyKey);
        proposal.setCalculatedSubtotal(saved.getTotalAmount());
        proposal.setCurrency("INR");
        proposal.setConfirmationRequired(true);
        proposal.setSafetyPrecheck(precheck);

        StringBuilder prompt = new StringBuilder("Your cart contains:\n\n");
        for (var item : saved.getItems()) {
            prompt.append("• ").append(item.getProductName()).append(" — ₹").append(item.getUnitPrice());
            if (item.getQuantity() > 1) {
                prompt.append(" (Qty: ").append(item.getQuantity()).append(")");
            }
            if (item.isUpsellItem()) {
                prompt.append(" [Upsell]");
            }
            prompt.append("\n");
        }
        prompt.append("\nTotal: ₹").append(saved.getTotalAmount()).append("\n\nShall I proceed with the Razorpay test payment?");
        proposal.setConfirmationPrompt(prompt.toString());

        return proposal;
    }

    @Transactional
    public Order confirmAndAuthorizeOrder(String sessionId, String idempotencyKey, boolean customerConfirmed) {
        Order order = orderRepository.findByIdempotencyKey(idempotencyKey)
                .orElseGet(() -> {
                    List<Order> list = orderRepository.findBySessionIdOrderByCreatedAtDesc(sessionId);
                    if (list.isEmpty()) {
                        throw new ResourceNotFoundException("No order found for session " + sessionId);
                    }
                    return list.get(0);
                });

        if (!customerConfirmed) {
            throw new CommerceException("CONFIRMATION_REQUIRED", "Customer confirmation was declined or missing.");
        }

        // Strict 8-point safety check
        SafetyCheckDetailDto safetyResult = paymentSafetyService.validateOrderExecution(order, customerConfirmed, idempotencyKey);

        order.setCustomerConfirmed(true);
        order.setCustomerConfirmedAt(LocalDateTime.now());
        order.setSafetyPassed(true);
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        try {
            order.setSafetyCheckSummary(objectMapper.writeValueAsString(safetyResult));
        } catch (Exception ignored) {}

        Order saved = orderRepository.save(order);

        auditTrailService.logEvent(sessionId, saved.getOrderNumber(), null,
                AuditEventType.CUSTOMER_CONFIRMATION,
                ActorType.CUSTOMER,
                "Customer explicitly confirmed checkout for Order " + saved.getOrderNumber() + " with total amount ₹" + saved.getTotalAmount(),
                saved.getTotalAmount(),
                true,
                Map.of("idempotencyKey", idempotencyKey, "confirmedAt", LocalDateTime.now().toString()));

        auditTrailService.logSafetyCheck(sessionId, saved.getOrderNumber(), safetyResult, true);

        return saved;
    }

    @Transactional
    public void markOrderPaid(Order order) {
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // Deduct inventory
        for (OrderItem item : order.getItems()) {
            try {
                productService.deductStock(item.getProduct().getId(), item.getQuantity());
            } catch (Exception e) {
                log.error("Failed to deduct stock for {}: {}", item.getProductName(), e.getMessage());
            }
        }

        // Clear cart for this session
        cartRepository.findBySessionId(order.getSessionId()).ifPresent(cart -> {
            cart.getItems().clear();
            cart.setCalculatedTotal(BigDecimal.ZERO);
            cartRepository.save(cart);
        });

        auditTrailService.logEvent(order.getSessionId(), order.getOrderNumber(), null,
                AuditEventType.PAYMENT_SUCCESS,
                ActorType.ORDER_SERVICE,
                "Order " + order.getOrderNumber() + " marked as PAID. Inventory deducted and cart cleared.",
                order.getTotalAmount(),
                true,
                Map.of("status", "PAID", "paidAt", LocalDateTime.now().toString()));
    }

    @Transactional
    public void markOrderFailed(Order order, String reason) {
        order.setStatus(OrderStatus.PAYMENT_FAILED);
        orderRepository.save(order);

        auditTrailService.logEvent(order.getSessionId(), order.getOrderNumber(), null,
                AuditEventType.PAYMENT_FAILED,
                ActorType.ORDER_SERVICE,
                "Order " + order.getOrderNumber() + " payment attempt failed: " + reason + ". Order marked PAYMENT_FAILED. No charge recorded.",
                order.getTotalAmount(),
                false,
                Map.of("status", "PAYMENT_FAILED", "reason", reason));
    }

    @Transactional(readOnly = true)
    public Order getOrderEntity(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(OrderDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Order " + orderNumber + " not found"));
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(OrderDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersBySession(String sessionId) {
        return orderRepository.findBySessionIdOrderByCreatedAtDesc(sessionId).stream()
                .map(OrderDto::fromEntity).collect(Collectors.toList());
    }
}