package com.hackathon.dto;


import java.math.BigDecimal;

public class PriceTamperDemoResponse {
    private boolean tamperingDetected;
    private String productName;
    private BigDecimal clientSuppliedPrice;
    private BigDecimal authoritativeDatabasePrice;
    private String outcome;
    private String explanation;
    private String auditEventId;

    public PriceTamperDemoResponse() {}

    public boolean isTamperingDetected() { return tamperingDetected; }
    public void setTamperingDetected(boolean tamperingDetected) { this.tamperingDetected = tamperingDetected; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getClientSuppliedPrice() { return clientSuppliedPrice; }
    public void setClientSuppliedPrice(BigDecimal clientSuppliedPrice) { this.clientSuppliedPrice = clientSuppliedPrice; }
    public BigDecimal getAuthoritativeDatabasePrice() { return authoritativeDatabasePrice; }
    public void setAuthoritativeDatabasePrice(BigDecimal authoritativeDatabasePrice) { this.authoritativeDatabasePrice = authoritativeDatabasePrice; }
    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getAuditEventId() { return auditEventId; }
    public void setAuditEventId(String auditEventId) { this.auditEventId = auditEventId; }
}