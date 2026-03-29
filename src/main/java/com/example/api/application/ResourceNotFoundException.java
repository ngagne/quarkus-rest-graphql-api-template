package com.example.api.application;

/**
 * Exception thrown when a requested resource is not found.
 * Maps to HTTP 404 for REST APIs.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(final String message) {
        super(message);
    }
}
