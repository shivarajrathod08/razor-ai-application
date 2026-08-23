package com.hackathon.exception;

public class CommerceException extends RuntimeException {
    private final String errorCode;

    public CommerceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}