package com.hackathon.dto;


import java.math.BigDecimal;

public class PaymentOrderResponse {
    private String paymentNumber;
    private String orderNumber;
    private String razorpayOrderId;
    private BigDecimal amount;
    private String currency;
    private String razorpayKeyId;
    private boolean testMode;
    private String status;
    private boolean isDemoFailureSimulated;
    private SafetyCheckDetailDto safetyCheckResult;
    private String message;

    public PaymentOrderResponse() {}

    public String getPaymentNumber() { return paymentNumber; }
    public void setPaymentNumber(String paymentNumber) { this.paymentNumber = paymentNumber; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getRazorpayKeyId() { return razorpayKeyId; }
    public void setRazorpayKeyId(String razorpayKeyId) { this.razorpayKeyId = razorpayKeyId; }
    public boolean isTestMode() { return testMode; }
    public void setTestMode(boolean testMode) { this.testMode = testMode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isDemoFailureSimulated() { return isDemoFailureSimulated; }
    public void setDemoFailureSimulated(boolean demoFailureSimulated) { isDemoFailureSimulated = demoFailureSimulated; }
    public SafetyCheckDetailDto getSafetyCheckResult() { return safetyCheckResult; }
    public void setSafetyCheckResult(SafetyCheckDetailDto safetyCheckResult) { this.safetyCheckResult = safetyCheckResult; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}