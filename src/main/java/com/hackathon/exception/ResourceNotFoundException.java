package com.hackathon.exception;


public class ResourceNotFoundException extends CommerceException {
    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }
}
