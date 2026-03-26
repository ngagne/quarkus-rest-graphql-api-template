package com.example.api.application;

public class InvalidRequestException extends RuntimeException {

    private final String field;

    public InvalidRequestException(final String field, final String message) {
        super(message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}
