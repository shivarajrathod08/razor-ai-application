package com.hackathon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "razorai")
public class AppProperties {
    private Payment payment = new Payment();
    private Razorpay razorpay = new Razorpay();
    private Gemini gemini = new Gemini();

    public static class Payment {
        private BigDecimal maxTransactionLimit = new BigDecimal("10000.00");
        private String currency = "INR";

        public BigDecimal getMaxTransactionLimit() { return maxTransactionLimit; }
        public void setMaxTransactionLimit(BigDecimal maxTransactionLimit) { this.maxTransactionLimit = maxTransactionLimit; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }

    public static class Razorpay {
        private String keyId = "rzp_test_demo_razorai";
        private String keySecret = "secret_demo_razorai_key";
        private boolean testMode = true;

        public String getKeyId() { return keyId; }
        public void setKeyId(String keyId) { this.keyId = keyId; }
        public String getKeySecret() { return keySecret; }
        public void setKeySecret(String keySecret) { this.keySecret = keySecret; }
        public boolean isTestMode() { return testMode; }
        public void setTestMode(boolean testMode) { this.testMode = testMode; }
    }

    public static class Gemini {
        private String apiKey;
        private String model = "gemini-1.5-flash";

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
    }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public Razorpay getRazorpay() { return razorpay; }
    public void setRazorpay(Razorpay razorpay) { this.razorpay = razorpay; }
    public Gemini getGemini() { return gemini; }
    public void setGemini(Gemini gemini) { this.gemini = gemini; }
}