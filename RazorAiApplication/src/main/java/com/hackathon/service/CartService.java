package com.hackathon.service;



import com.hackathon.dto.CartDto;
import com.hackathon.dto.PriceTamperDemoResponse;
import com.hackathon.exception.CommerceException;
import com.hackathon.exception.ResourceNotFoundException;
import com.hackathon.model.AuditEvent;
import com.hackathon.model.Cart;
import com.hackathon.model.CartItem;
import com.hackathon.model.Product;
import com.hackathon.model.enums.ActorType;
import com.hackathon.model.enums.AuditEventType;
import com.hackathon.repository.CartItemRepository;
import com.hackathon.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {
    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final AuditTrailService auditTrailService;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductService productService,
                       AuditTrailService auditTrailService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public Cart getOrCreateCart(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> cartRepository.save(new Cart(sessionId)));
    }

    @Transactional(readOnly = true)
    public CartDto getCartDto(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .map(CartDto::fromEntity)
                .orElseGet(() -> {
                    Cart empty = new Cart(sessionId);
                    return CartDto.fromEntity(empty);
                });
    }

    @Transactional
    public CartDto addToCart(String sessionId, Long productId, int quantity, boolean isUpsell, BigDecimal untrustedPrice) {
        if (quantity <= 0) {
            throw new CommerceException("INVALID_QUANTITY", "Quantity must be greater than zero.");
        }

        Product product = productService.getProductEntity(productId);
        if (!product.isActive()) {
            throw new CommerceException("PRODUCT_INACTIVE", "Cannot add inactive product '" + product.getName() + "' to cart.");
        }
        if (product.getStock() < quantity) {
            throw new CommerceException("OUT_OF_STOCK", "Product '" + product.getName() + "' has insufficient stock (Requested: " + quantity + ", Available: " + product.getStock() + ")");
        }

        Cart cart = getOrCreateCart(sessionId);

        // Security check: Verify if client/LLM supplied a manipulated price
        BigDecimal authoritativePrice = product.getPrice();
        if (untrustedPrice != null && untrustedPrice.compareTo(authoritativePrice) != 0) {
            log.warn("[PRICE_TAMPERING_DETECTED] Session: {} | Product: {} | Client Price: {} | Authoritative DB Price: {}",
                    sessionId, product.getName(), untrustedPrice, authoritativePrice);

            Map<String, Object> tamperMeta = new HashMap<>();
            tamperMeta.put("productId", productId);
            tamperMeta.put("productName", product.getName());
            tamperMeta.put("clientSuppliedPrice", untrustedPrice);
            tamperMeta.put("authoritativeDatabasePrice", authoritativePrice);
            tamperMeta.put("action", "OVERRIDDEN_WITH_AUTHORITATIVE_DB_PRICE");

            auditTrailService.logEvent(sessionId, null, null,
                    AuditEventType.PRICE_TAMPERING_ATTEMPT,
                    ActorType.BACKEND_SAFETY_GUARD,
                    "Client/LLM price tampering attempt blocked for '" + product.getName() + "'. Client requested ₹" + untrustedPrice + ", enforced DB authoritative price ₹" + authoritativePrice,
                    authoritativePrice,
                    true,
                    tamperMeta);
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQty = item.getQuantity() + quantity;
            if (product.getStock() < newQty) {
                throw new CommerceException("OUT_OF_STOCK", "Total requested quantity (" + newQty + ") exceeds available stock (" + product.getStock() + ")");
            }
            item.setQuantity(newQty);
            item.setUnitPrice(authoritativePrice);
            item.recalculateSubtotal();
            if (isUpsell) item.setUpsellItem(true);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity, authoritativePrice, isUpsell);
            cart.getItems().add(newItem);
        }

        cart.recalculateTotal();
        Cart saved = cartRepository.save(cart);

        // Audit Trail
        auditTrailService.logEvent(sessionId, null, null,
                isUpsell ? AuditEventType.UPSELL_SUGGESTED : AuditEventType.PRODUCT_ADDED,
                isUpsell ? ActorType.AI_AGENT : ActorType.CUSTOMER,
                "Added " + quantity + "x '" + product.getName() + "' to cart @ ₹" + authoritativePrice + (isUpsell ? " (Upsell Item)" : ""),
                authoritativePrice.multiply(BigDecimal.valueOf(quantity)),
                true,
                Map.of("productId", productId, "quantity", quantity, "isUpsell", isUpsell, "cartTotal", saved.getCalculatedTotal()));

        auditTrailService.logEvent(sessionId, null, null,
                AuditEventType.CART_CALCULATED,
                ActorType.BACKEND_SAFETY_GUARD,
                "Backend recalculated cart total: ₹" + saved.getCalculatedTotal() + " for " + saved.getItems().size() + " items",
                saved.getCalculatedTotal(),
                true,
                Map.of("itemCount", saved.getItems().size(), "total", saved.getCalculatedTotal()));

        return CartDto.fromEntity(saved);
    }

    @Transactional
    public CartDto removeFromCart(String sessionId, Long productId) {
        Cart cart = getOrCreateCart(sessionId);
        boolean removed = cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        if (removed) {
            cart.recalculateTotal();
            Cart saved = cartRepository.save(cart);
            auditTrailService.logEvent(sessionId, null, null,
                    AuditEventType.PRODUCT_REMOVED,
                    ActorType.CUSTOMER,
                    "Removed product ID " + productId + " from cart",
                    null,
                    true,
                    Map.of("productId", productId, "newCartTotal", saved.getCalculatedTotal()));
            return CartDto.fromEntity(saved);
        }
        return CartDto.fromEntity(cart);
    }

    @Transactional
    public void clearCart(String sessionId) {
        Cart cart = getOrCreateCart(sessionId);
        cart.getItems().clear();
        cart.setCalculatedTotal(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Transactional
    public PriceTamperDemoResponse runPriceTamperDemo(String sessionId, Long productId, BigDecimal tamperedPrice) {
        Product product = productService.getProductEntity(productId);
        BigDecimal authoritativePrice = product.getPrice();

        // Attempt add with tampered price
        addToCart(sessionId, productId, 1, false, tamperedPrice);

        AuditEvent auditEvent = auditTrailService.logEvent(sessionId, null, null,
                AuditEventType.PRICE_TAMPERING_ATTEMPT,
                ActorType.BACKEND_SAFETY_GUARD,
                "Demo Security Test: Client supplied malicious price of ₹" + tamperedPrice + " for '" + product.getName() + "'. Backend rejected client price and used database authoritative price of ₹" + authoritativePrice,
                authoritativePrice,
                true,
                Map.of("clientSuppliedPrice", tamperedPrice, "databasePrice", authoritativePrice, "product", product.getName()));

        PriceTamperDemoResponse response = new PriceTamperDemoResponse();
        response.setTamperingDetected(true);
        response.setProductName(product.getName());
        response.setClientSuppliedPrice(tamperedPrice);
        response.setAuthoritativeDatabasePrice(authoritativePrice);
        response.setOutcome("REJECTED_AND_OVERRIDDEN");
        response.setExplanation("The client or AI agent requested ₹" + tamperedPrice + ", but backend deterministic pricing security overrode it with the database price of ₹" + authoritativePrice + ".");
        response.setAuditEventId(auditEvent.getEventId());
        return response;
    }
}