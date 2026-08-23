package com.hackathon.service;



import com.hackathon.dto.CartDto;
import com.hackathon.model.Product;
import com.hackathon.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DemoScenarioService {
    private static final Logger log = LoggerFactory.getLogger(DemoScenarioService.class);
    private final CartService cartService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final AuditEventRepository auditEventRepository;
    private final AgentSessionRepository sessionRepository;
    private final AgentMessageRepository messageRepository;

    public DemoScenarioService(CartService cartService,
                               ProductService productService,
                               ProductRepository productRepository,
                               CartRepository cartRepository,
                               OrderRepository orderRepository,
                               PaymentRepository paymentRepository,
                               AuditEventRepository auditEventRepository,
                               AgentSessionRepository sessionRepository,
                               AgentMessageRepository messageRepository) {
        this.cartService = cartService;
        this.productService = productService;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.auditEventRepository = auditEventRepository;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public Map<String, Object> loadDemoCart(String sessionId) {
        cartService.clearCart(sessionId);

        // Find Backpack (₹1,499) and USB-C Hub (₹799)
        List<Product> products = productRepository.findAll();
        Product backpack = products.stream().filter(p -> p.getName().contains("Backpack")).findFirst().orElse(null);
        Product hub = products.stream().filter(p -> p.getName().contains("Hub")).findFirst().orElse(null);

        if (backpack != null) {
            cartService.addToCart(sessionId, backpack.getId(), 1, false, null);
        }
        if (hub != null) {
            cartService.addToCart(sessionId, hub.getId(), 1, true, null);
        }

        CartDto cart = cartService.getCartDto(sessionId);
        Map<String, Object> res = new HashMap<>();
        res.put("message", "Demo cart successfully initialized with Urban Laptop Backpack (₹1,499) + USB-C Hub (₹799)");
        res.put("cart", cart);
        return res;
    }

    @Transactional
    public Map<String, Object> resetDemoState() {
        log.info("Resetting demo state...");
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        sessionRepository.deleteAll();
        auditEventRepository.deleteAll();
        productService.seedCatalogIfEmpty();

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Demo environment reset. Products restored, carts and test orders cleared.");
        return res;
    }
}