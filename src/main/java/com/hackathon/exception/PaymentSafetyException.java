package com.hackathon.exception;



import com.hackathon.dto.SafetyCheckDetailDto;

public class PaymentSafetyException extends CommerceException {
    private final SafetyCheckDetailDto safetyDetails;

    public PaymentSafetyException(String errorCode, String message, SafetyCheckDetailDto safetyDetails) {
        super(errorCode, message);
        this.safetyDetails = safetyDetails;
    }

    public SafetyCheckDetailDto getSafetyDetails() {
        return safetyDetails;
    }
}