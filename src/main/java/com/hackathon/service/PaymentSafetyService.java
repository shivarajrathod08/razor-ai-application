package com.hackathon.service;



import com.hackathon.config.AppProperties;
import com.hackathon.dto.SafetyCheckDetailDto;
import com.hackathon.exception.CommerceException;
import com.hackathon.exception.PaymentSafetyException;
import com.hackathon.model.Cart;
import com.hackathon.model.CartItem;
import com.hackathon.model.Order;
import com.hackathon.model.Product;
import com.hackathon.model.enums.OrderStatus;
import com.hackathon.model.enums.SafetyCheckVerdict;
import com.hackathon.repository.OrderRepository;
import com.hackathon.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentSafetyService {
    private static final Logger log = LoggerFactory.getLogger(PaymentSafetyService.class);
    private final AppProperties appProperties;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public PaymentSafetyService(AppProperties appProperties,
                                ProductService productService,
                                OrderRepository orderRepository,
                                PaymentRepository paymentRepository) {
        this.appProperties = appProperties;
        this.productService = productService;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    public SafetyCheckDetailDto performPrecheck(Cart cart) {
        SafetyCheckDetailDto result = new SafetyCheckDetailDto();
        BigDecimal maxLimit = appProperties.getPayment().getMaxTransactionLimit();
        result.setMaxTransactionLimit(maxLimit);
        List<String> reasons = new ArrayList<>();

        // 1. Active Cart Check
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            result.setActiveCartCheck(false);
            reasons.add("Cart is empty or not found");
        } else {
            result.setActiveCartCheck(true);
        }

        // 2, 3, 4, 5. Product Existence, Active Status, Stock, Server-side Price
        boolean allProductsExist = true;
        boolean allProductsActive = true;
        boolean allStockAvailable = true;
        boolean priceVerified = true;
        BigDecimal recalculatedSubtotal = BigDecimal.ZERO;

        if (cart != null && cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                try {
                    Product dbProduct = productService.getProductEntity(item.getProduct().getId());
                    if (!dbProduct.isActive()) {
                        allProductsActive = false;
                        reasons.add("Product '" + dbProduct.getName() + "' is inactive");
                    }
                    if (dbProduct.getStock() < item.getQuantity()) {
                        allStockAvailable = false;
                        reasons.add("Insufficient stock for '" + dbProduct.getName() + "' (Available: " + dbProduct.getStock() + ", Requested: " + item.getQuantity() + ")");
                    }
                    BigDecimal itemTotal = dbProduct.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    recalculatedSubtotal = recalculatedSubtotal.add(itemTotal);
                } catch (Exception e) {
                    allProductsExist = false;
                    reasons.add("Product ID " + item.getProduct().getId() + " not found in database catalog");
                }
            }
        }

        result.setProductExistenceCheck(allProductsExist);
        result.setProductActiveCheck(allProductsActive);
        result.setStockAvailabilityCheck(allStockAvailable);
        result.setServerSidePriceVerification(priceVerified);
        result.setCalculatedAmount(recalculatedSubtotal);

        // 6. Transaction Limit Check
        if (recalculatedSubtotal.compareTo(maxLimit) > 0) {
            result.setTransactionLimitCheck(false);
            reasons.add("Transaction amount (₹" + recalculatedSubtotal + ") exceeds permitted limit (₹" + maxLimit + "). Additional verification required.");
        } else {
            result.setTransactionLimitCheck(true);
        }

        // Precheck doesn't require confirmation yet
        result.setCustomerConfirmationCheck(false);
        result.setIdempotencyCheck(true);

        boolean passed = result.isActiveCartCheck() && result.isProductExistenceCheck() &&
                result.isProductActiveCheck() && result.isStockAvailabilityCheck() &&
                result.isServerSidePriceVerification() && result.isTransactionLimitCheck();

        result.setVerdict(passed ? SafetyCheckVerdict.APPROVED : SafetyCheckVerdict.BLOCKED);
        result.setReasons(reasons);
        return result;
    }

    public SafetyCheckDetailDto validateOrderExecution(Order order, boolean customerConfirmed, String idempotencyKey) {
        SafetyCheckDetailDto result = new SafetyCheckDetailDto();
        BigDecimal maxLimit = appProperties.getPayment().getMaxTransactionLimit();
        result.setMaxTransactionLimit(maxLimit);
        List<String> reasons = new ArrayList<>();

        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            result.setActiveCartCheck(false);
            reasons.add("Order items are missing");
            result.setVerdict(SafetyCheckVerdict.BLOCKED);
            result.setReasons(reasons);
            throw new PaymentSafetyException("EMPTY_ORDER", "Order has no items", result);
        } else {
            result.setActiveCartCheck(true);
        }

        // Check customer confirmation
        result.setCustomerConfirmationCheck(customerConfirmed);
        if (!customerConfirmed) {
            reasons.add("Explicit customer confirmation is required before payment authorization");
        }

        // Check if order already paid
        if (order.getStatus() == OrderStatus.PAID) {
            reasons.add("Order " + order.getOrderNumber() + " is already paid");
        }

        // Idempotency check: Look for duplicate payment with same idempotency key
        if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {
            boolean duplicate = paymentRepository.findByIdempotencyKey(idempotencyKey.trim())
                    .filter(p -> p.getOrder() != null && !p.getOrder().getId().equals(order.getId()))
                    .isPresent();
            if (duplicate) {
                result.setIdempotencyCheck(false);
                reasons.add("Duplicate payment request detected for idempotency key: " + idempotencyKey);
            } else {
                result.setIdempotencyCheck(true);
            }
        } else {
            result.setIdempotencyCheck(true);
        }

        // Recalculate amount strictly from database prices
        BigDecimal authoritativeTotal = BigDecimal.ZERO;
        boolean allProductsExist = true;
        boolean allProductsActive = true;
        boolean allStockAvailable = true;

        for (var item : order.getItems()) {
            try {
                Product dbProduct = productService.getProductEntity(item.getProduct().getId());
                if (!dbProduct.isActive()) {
                    allProductsActive = false;
                    reasons.add("Product '" + dbProduct.getName() + "' is inactive");
                }
                if (dbProduct.getStock() < item.getQuantity()) {
                    allStockAvailable = false;
                    reasons.add("Product '" + dbProduct.getName() + "' has insufficient stock");
                }
                authoritativeTotal = authoritativeTotal.add(dbProduct.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            } catch (Exception e) {
                allProductsExist = false;
                reasons.add("Product ID " + item.getProduct().getId() + " not found");
            }
        }

        result.setProductExistenceCheck(allProductsExist);
        result.setProductActiveCheck(allProductsActive);
        result.setStockAvailabilityCheck(allStockAvailable);
        result.setServerSidePriceVerification(true);
        result.setCalculatedAmount(authoritativeTotal);

        if (authoritativeTotal.compareTo(maxLimit) > 0) {
            result.setTransactionLimitCheck(false);
            reasons.add("Transaction total ₹" + authoritativeTotal + " exceeds permitted threshold of ₹" + maxLimit);
        } else {
            result.setTransactionLimitCheck(true);
        }

        boolean approved = result.isActiveCartCheck() &&
                result.isProductExistenceCheck() &&
                result.isProductActiveCheck() &&
                result.isStockAvailabilityCheck() &&
                result.isServerSidePriceVerification() &&
                result.isTransactionLimitCheck() &&
                result.isCustomerConfirmationCheck() &&
                result.isIdempotencyCheck() &&
                order.getStatus() != OrderStatus.PAID;

        result.setVerdict(approved ? SafetyCheckVerdict.APPROVED : SafetyCheckVerdict.BLOCKED);
        result.setReasons(reasons);

        if (!approved) {
            log.warn("[PAYMENT_SAFETY_GUARD_BLOCKED] Order: {} | Amount: {} | Violations: {}",
                    order.getOrderNumber(), authoritativeTotal, String.join("; ", reasons));
            throw new PaymentSafetyException("PAYMENT_SAFETY_VIOLATION",
                    "Payment blocked by Safety Guard: " + String.join(". ", reasons),
                    result);
        }

        return result;
    }
}
