package com.hackathon.dto;



public class ConfigStatusDto {
    private boolean isRazorpayConfigured;
    private boolean isGeminiConfigured;
    private String razorpayKeyId; // Public Key ID only for client checkout popup, NEVER Key Secret
    private boolean testMode;
    private double maxTransactionLimit;
    private String currency;

    public ConfigStatusDto() {}

    public boolean isRazorpayConfigured() { return isRazorpayConfigured; }
    public void setRazorpayConfigured(boolean razorpayConfigured) { isRazorpayConfigured = razorpayConfigured; }
    public boolean isGeminiConfigured() { return isGeminiConfigured; }
    public void setGeminiConfigured(boolean geminiConfigured) { isGeminiConfigured = geminiConfigured; }
    public String getRazorpayKeyId() { return razorpayKeyId; }
    public void setRazorpayKeyId(String razorpayKeyId) { this.razorpayKeyId = razorpayKeyId; }
    public boolean isTestMode() { return testMode; }
    public void setTestMode(boolean testMode) { this.testMode = testMode; }
    public double getMaxTransactionLimit() { return maxTransactionLimit; }
    public void setMaxTransactionLimit(double maxTransactionLimit) { this.maxTransactionLimit = maxTransactionLimit; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}