package com.hackathon.dto;



import jakarta.validation.constraints.NotBlank;

public class CheckoutConfirmRequest {
    @NotBlank
    private String sessionId;

    @NotBlank
    private String idempotencyKey;

    private boolean customerConfirmed = true;

    // Optional simulation flag for Demo Mode failure testing
    private boolean simulateFailure = false;

    public CheckoutConfirmRequest() {}

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public boolean isCustomerConfirmed() { return customerConfirmed; }
    public void setCustomerConfirmed(boolean customerConfirmed) { this.customerConfirmed = customerConfirmed; }
    public boolean isSimulateFailure() { return simulateFailure; }
    public void setSimulateFailure(boolean simulateFailure) { this.simulateFailure = simulateFailure; }
}