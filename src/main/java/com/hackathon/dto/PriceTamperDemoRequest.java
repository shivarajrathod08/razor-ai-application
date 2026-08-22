package com.hackathon.dto;



import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PriceTamperDemoRequest {
    @NotNull
    private String sessionId;

    @NotNull
    private Long productId;

    @NotNull
    private BigDecimal tamperedPrice;

    public PriceTamperDemoRequest() {}

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public BigDecimal getTamperedPrice() { return tamperedPrice; }
    public void setTamperedPrice(BigDecimal tamperedPrice) { this.tamperedPrice = tamperedPrice; }
}