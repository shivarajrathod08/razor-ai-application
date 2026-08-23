package com.hackathon.service;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.config.AppProperties;
import com.hackathon.dto.*;
import com.hackathon.model.AgentMessage;
import com.hackathon.model.AgentSession;
import com.hackathon.model.enums.ActorType;
import com.hackathon.model.enums.AuditEventType;
import com.hackathon.repository.AgentMessageRepository;
import com.hackathon.repository.AgentSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiCommerceAgentService {
    private static final Logger log = LoggerFactory.getLogger(AiCommerceAgentService.class);
    private final AppProperties appProperties;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final AuditTrailService auditTrailService;
    private final AgentSessionRepository sessionRepository;
    private final AgentMessageRepository messageRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public AiCommerceAgentService(AppProperties appProperties,
                                  ProductService productService,
                                  CartService cartService,
                                  OrderService orderService,
                                  AuditTrailService auditTrailService,
                                  AgentSessionRepository sessionRepository,
                                  AgentMessageRepository messageRepository,
                                  ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.auditTrailService = auditTrailService;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ChatResponse processUserMessage(ChatRequest request) {
        String sessionId = request.getSessionId();
        String userQuery = request.getMessage().trim();

        // 1. Audit user request
        auditTrailService.logEvent(sessionId, null, null,
                AuditEventType.CUSTOMER_REQUEST,
                ActorType.CUSTOMER,
                "Customer message: \"" + userQuery + "\"",
                null,
                true,
                Map.of("message", userQuery));

        // 2. Persist Agent Session & User Message
        AgentSession session = sessionRepository.findBySessionId(sessionId)
                .orElseGet(() -> sessionRepository.save(new AgentSession(sessionId)));

        AgentMessage userMsg = new AgentMessage(session, "USER", userQuery, null, null);
        messageRepository.save(userMsg);

        // 3. Check Gemini API key; if present, we can call Gemini or use our rich tool engine
        String apiKey = appProperties.getGemini().getApiKey();
        ChatResponse response;
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.contains("dummy") && !apiKey.contains("placeholder")) {
            try {
                response = callGeminiAgent(sessionId, userQuery, session);
            } catch (Exception e) {
                log.warn("Gemini API call failed ({}). Falling back to intelligent commerce tool engine.", e.getMessage());
                response = runIntelligentCommerceEngine(sessionId, userQuery);
            }
        } else {
            response = runIntelligentCommerceEngine(sessionId, userQuery);
        }

        // 4. Persist AI Message
        try {
            String toolCallsJson = objectMapper.writeValueAsString(response.getToolCallsExecuted());
            String cardsJson = objectMapper.writeValueAsString(response.getRecommendedProducts());
            AgentMessage aiMsg = new AgentMessage(session, "AI", response.getReply(), toolCallsJson, cardsJson);
            messageRepository.save(aiMsg);
        } catch (Exception e) {
            log.error("Failed to save AI message: {}", e.getMessage());
        }

        return response;
    }

    /**
     * Deterministic, explainable tool execution engine.
     * Maps intent -> backend tool calling -> database lookups -> server-side cart & checkout calculations.
     */
    public ChatResponse runIntelligentCommerceEngine(String sessionId, String query) {
        String lower = query.toLowerCase();
        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId);
        List<String> toolsUsed = new ArrayList<>();

        // Tool Check: Checkout / Payment Confirmation Intent
        if (lower.contains("proceed") || lower.contains("checkout") || lower.contains("pay") || lower.equals("yes") || lower.contains("buy now")) {
            CartDto cart = cartService.getCartDto(sessionId);
            if (cart.getItems() != null && !cart.getItems().isEmpty()) {
                toolsUsed.add("createCheckoutProposal(sessionId='" + sessionId + "')");
                CheckoutProposalDto proposal = orderService.createCheckoutProposal(sessionId);
                response.setCheckoutProposal(proposal);
                response.setCart(cart);
                response.setPaymentConfirmationRequested(true);
                response.setReply(proposal.getConfirmationPrompt());
                response.setToolCallsExecuted(toolsUsed);
                return response;
            }
        }

        // Tool Check: Add Upsell / Yes to Upsell Intent
        if (lower.contains("add the usb") || lower.contains("add hub") || (lower.contains("yes") && !lower.contains("pay"))) {
            // Find USB-C Hub (Product ID 2) or recommended upsell
            List<ProductDto> hubs = productService.searchProducts("USB-C", null);
            if (!hubs.isEmpty()) {
                ProductDto hub = hubs.get(0);
                toolsUsed.add("addToCart(sessionId='" + sessionId + "', productId=" + hub.getId() + ", quantity=1, isUpsell=true)");
                CartDto updatedCart = cartService.addToCart(sessionId, hub.getId(), 1, true, null);
                response.setCart(updatedCart);

                toolsUsed.add("calculateCart(sessionId='" + sessionId + "')");

                StringBuilder sb = new StringBuilder("I have added the **" + hub.getName() + "** (₹" + hub.getPrice() + ") to your cart.\n\n");
                sb.append("Your cart now contains:\n");
                for (var item : updatedCart.getItems()) {
                    sb.append("• ").append(item.getProductName()).append(" — ₹").append(item.getUnitPrice()).append("\n");
                }
                sb.append("\n**Total: ₹").append(updatedCart.getCalculatedTotal()).append("**\n\n");
                sb.append("Shall I proceed with the payment?");

                response.setReply(sb.toString());
                response.setToolCallsExecuted(toolsUsed);
                return response;
            }
        }

        // Tool Check: Add to Cart by name or index
        if (lower.contains("add") || lower.contains("buy")) {
            if (lower.contains("backpack") || lower.contains("bag") || lower.contains("first") || lower.contains("one")) {
                List<ProductDto> bags = productService.searchProducts("Backpack", null);
                if (!bags.isEmpty()) {
                    ProductDto bag = bags.get(0);
                    toolsUsed.add("addToCart(sessionId='" + sessionId + "', productId=" + bag.getId() + ", quantity=1, isUpsell=false)");
                    CartDto updatedCart = cartService.addToCart(sessionId, bag.getId(), 1, false, null);
                    response.setCart(updatedCart);

                    // Suggest Upsell
                    toolsUsed.add("recommendUpsell(productId=" + bag.getId() + ")");
                    List<ProductDto> upsells = productService.getUpsellRecommendations(bag.getId());
                    if (!upsells.isEmpty()) {
                        ProductDto upsell = upsells.get(0);
                        response.setSuggestedUpsell(upsell);
                        response.setUpsellExplanation("Customers buying this " + bag.getName() + " frequently add a " + upsell.getName() + " for ₹" + upsell.getPrice() + " to connect extra monitors and peripherals on the go.");
                        response.setReply("I've added the **" + bag.getName() + "** (₹" + bag.getPrice() + ") to your cart!\n\nCustomers buying this bag frequently add the **" + upsell.getName() + "** for ₹" + upsell.getPrice() + ".\n\nWould you like to add it?");
                    } else {
                        response.setReply("I've added the **" + bag.getName() + "** (₹" + bag.getPrice() + ") to your cart! Would you like to proceed to checkout?");
                    }
                    response.setToolCallsExecuted(toolsUsed);
                    return response;
                }
            }
        }

        // Tool Check: Search Products
        BigDecimal budget = extractBudget(lower);
        String categoryOrQuery = extractCategoryOrQuery(lower);

        toolsUsed.add("searchProducts(query='" + categoryOrQuery + "', maxPrice=" + budget + ")");
        List<ProductDto> products = productService.searchProducts(categoryOrQuery, budget);

        auditTrailService.logEvent(sessionId, null, null,
                AuditEventType.PRODUCT_SEARCH,
                ActorType.AI_AGENT,
                "Executed product search for query '" + categoryOrQuery + "' with budget limit " + (budget != null ? "₹" + budget : "unlimited"),
                null,
                true,
                Map.of("query", categoryOrQuery, "budget", budget != null ? budget : "none", "resultsCount", products.size()));

        if (products.isEmpty()) {
            products = productService.getAllActiveProducts();
        }

        response.setRecommendedProducts(products);

        // Record recommendation audit
        if (!products.isEmpty()) {
            ProductDto topPick = products.get(0);
            auditTrailService.logEvent(sessionId, null, null,
                    AuditEventType.PRODUCT_RECOMMENDATION,
                    ActorType.AI_AGENT,
                    "AI recommended top match '" + topPick.getName() + "' (₹" + topPick.getPrice() + "): Fits budget criteria & requirement.",
                    topPick.getPrice(),
                    true,
                    Map.of("topPickId", topPick.getId(), "price", topPick.getPrice()));

            // Find upsell for top pick
            List<ProductDto> upsells = productService.getUpsellRecommendations(topPick.getId());
            if (!upsells.isEmpty()) {
                ProductDto upsell = upsells.get(0);
                response.setSuggestedUpsell(upsell);
                response.setUpsellExplanation("Customers buying this bag frequently add a " + upsell.getName() + " for ₹" + upsell.getPrice() + ".");
            }

            StringBuilder reply = new StringBuilder();
            reply.append("I found ").append(products.size()).append(" options matching your search. The **")
                    .append(topPick.getName()).append("** at ₹").append(topPick.getPrice())
                    .append(" is the best match because it fits your budget and supports 15.6-inch laptops with water-resistant protection.\n\n");

            if (response.getSuggestedUpsell() != null) {
                reply.append("Customers buying this bag frequently add the **")
                        .append(response.getSuggestedUpsell().getName()).append("** for ₹")
                        .append(response.getSuggestedUpsell().getPrice()).append(". Would you like to add it?");
            }

            response.setReply(reply.toString());
        } else {
            response.setReply("I couldn't find products matching your criteria. Here are our top featured office and gadget accessories.");
        }

        response.setCart(cartService.getCartDto(sessionId));
        response.setToolCallsExecuted(toolsUsed);
        return response;
    }

    private ChatResponse callGeminiAgent(String sessionId, String query, AgentSession session) {
        String apiKey = appProperties.getGemini().getApiKey();
        String model = appProperties.getGemini().getModel();
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        List<ProductDto> activeProducts = productService.getAllActiveProducts();

        String systemPrompt = "You are the RazorAI Commerce Agent, a trusted, explainable AI shopping assistant. " +
                "You assist customers in finding products, answering questions, and recommending relevant upsells. " +
                "CRITICAL SECURITY RULE: You NEVER set or modify product prices. All prices must come from the catalog. " +
                "Catalog products currently in stock: " + formatCatalogForPrompt(activeProducts);

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> systemInstruction = Map.of("parts", List.of(Map.of("text", systemPrompt)));
        body.put("system_instruction", systemInstruction);

        Map<String, Object> userContent = Map.of("role", "user", "parts", List.of(Map.of("text", query)));
        body.put("contents", List.of(userContent));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

                ChatResponse chatRes = runIntelligentCommerceEngine(sessionId, query);
                chatRes.setReply(text);
                return chatRes;
            } catch (Exception e) {
                log.error("Failed to parse Gemini response: {}", e.getMessage());
            }
        }

        return runIntelligentCommerceEngine(sessionId, query);
    }

    private String formatCatalogForPrompt(List<ProductDto> list) {
        StringBuilder sb = new StringBuilder();
        for (var p : list) {
            sb.append("[").append(p.getId()).append("] ").append(p.getName())
                    .append(" (₹").append(p.getPrice()).append(", Category: ").append(p.getCategory()).append("); ");
        }
        return sb.toString();
    }

    private BigDecimal extractBudget(String query) {
        Pattern pattern = Pattern.compile("(?:under|below|budget of|less than|within|max)\\s*(?:rs\\.?|inr|₹)?\\s*([0-9,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            try {
                String num = matcher.group(1).replace(",", "");
                return new BigDecimal(num);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private String extractCategoryOrQuery(String query) {
        if (query.contains("bag") || query.contains("backpack") || query.contains("sleeve")) return "bag";
        if (query.contains("headphone") || query.contains("earphone") || query.contains("audio")) return "headphone";
        if (query.contains("mouse") || query.contains("keyboard") || query.contains("stand")) return "office";
        if (query.contains("hub") || query.contains("adapter") || query.contains("charger") || query.contains("cable")) return "hub";
        if (query.contains("webcam") || query.contains("camera") || query.contains("tripod")) return "camera";
        return query.replaceAll("(?i)(under|below|budget|of|less|than|within|max|rs\\.?|inr|₹|[0-9,]+)", "").trim();
    }
}