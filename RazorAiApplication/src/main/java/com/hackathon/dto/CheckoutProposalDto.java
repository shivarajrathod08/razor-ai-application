package com.hackathon.dto;


import java.math.BigDecimal;
import java.util.List;

public class CheckoutProposalDto {
    private String orderNumber;
    private String sessionId;
    private List<CartItemDto> items;
    private BigDecimal calculatedSubtotal;
    private String currency;
    private boolean confirmationRequired = true;
    private String idempotencyKey;
    private String confirmationPrompt;
    private SafetyCheckDetailDto safetyPrecheck;

    public CheckoutProposalDto() {}

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) { this.items = items; }
    public BigDecimal getCalculatedSubtotal() { return calculatedSubtotal; }
    public void setCalculatedSubtotal(BigDecimal calculatedSubtotal) { this.calculatedSubtotal = calculatedSubtotal; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public boolean isConfirmationRequired() { return confirmationRequired; }
    public void setConfirmationRequired(boolean confirmationRequired) { this.confirmationRequired = confirmationRequired; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public String getConfirmationPrompt() { return confirmationPrompt; }
    public void setConfirmationPrompt(String confirmationPrompt) { this.confirmationPrompt = confirmationPrompt; }
    public SafetyCheckDetailDto getSafetyPrecheck() { return safetyPrecheck; }
    public void setSafetyPrecheck(SafetyCheckDetailDto safetyPrecheck) { this.safetyPrecheck = safetyPrecheck; }
}