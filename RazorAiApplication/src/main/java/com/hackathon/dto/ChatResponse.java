package com.hackathon.dto;


import java.util.List;

public class ChatResponse {
    private String sessionId;
    private String reply;
    private List<ProductDto> recommendedProducts;
    private ProductDto suggestedUpsell;
    private String upsellExplanation;
    private CartDto cart;
    private CheckoutProposalDto checkoutProposal;
    private List<String> toolCallsExecuted;
    private boolean paymentConfirmationRequested;

    public ChatResponse() {}

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public List<ProductDto> getRecommendedProducts() { return recommendedProducts; }
    public void setRecommendedProducts(List<ProductDto> recommendedProducts) { this.recommendedProducts = recommendedProducts; }
    public ProductDto getSuggestedUpsell() { return suggestedUpsell; }
    public void setSuggestedUpsell(ProductDto suggestedUpsell) { this.suggestedUpsell = suggestedUpsell; }
    public String getUpsellExplanation() { return upsellExplanation; }
    public void setUpsellExplanation(String upsellExplanation) { this.upsellExplanation = upsellExplanation; }
    public CartDto getCart() { return cart; }
    public void setCart(CartDto cart) { this.cart = cart; }
    public CheckoutProposalDto getCheckoutProposal() { return checkoutProposal; }
    public void setCheckoutProposal(CheckoutProposalDto checkoutProposal) { this.checkoutProposal = checkoutProposal; }
    public List<String> getToolCallsExecuted() { return toolCallsExecuted; }
    public void setToolCallsExecuted(List<String> toolCallsExecuted) { this.toolCallsExecuted = toolCallsExecuted; }
    public boolean isPaymentConfirmationRequested() { return paymentConfirmationRequested; }
    public void setPaymentConfirmationRequested(boolean paymentConfirmationRequested) { this.paymentConfirmationRequested = paymentConfirmationRequested; }
}