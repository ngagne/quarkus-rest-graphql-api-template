package com.example.api.application;

/**
 * Exception thrown when a resource conflict occurs (e.g., resource already exists).
 * Maps to HTTP 409 for REST APIs.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(final String message) {
        super(message);
    }
}
