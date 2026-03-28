package com.example.api.model;

import java.util.Objects;

/**
 * Violation details for API error responses
 */
public class ApiErrorResponseViolations {

    private String field;
    private String message;

    public ApiErrorResponseViolations() {
        // Default constructor for JSON-B deserialization
    }

    public ApiErrorResponseViolations(final String field, final String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ApiErrorResponseViolations that = (ApiErrorResponseViolations) obj;
        return Objects.equals(field, that.field)
            && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, message);
    }

    @Override
    public String toString() {
        return "ApiErrorResponseViolations{"
                + "field='" + field + '\''
                + ", message='" + message + '\''
                + '}';
    }
}
