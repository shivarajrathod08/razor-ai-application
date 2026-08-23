package com.hackathon.dto;


import java.math.BigDecimal;

public class PaymentVerifyResponse {
    private boolean verified;
    private String paymentStatus;
    private String orderStatus;
    private String orderNumber;
    private String paymentNumber;
    private BigDecimal amount;
    private String message;
    private boolean isDemoFailure;

    public PaymentVerifyResponse() {}

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getPaymentNumber() { return paymentNumber; }
    public void setPaymentNumber(String paymentNumber) { this.paymentNumber = paymentNumber; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isDemoFailure() { return isDemoFailure; }
    public void setDemoFailure(boolean demoFailure) { isDemoFailure = demoFailure; }
}