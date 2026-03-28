package com.example.api.model;

import java.util.List;

/**
 * Error response for REST API
 */
public class ApiErrorResponse {

    private String code;
    private String message;
    private List<ApiErrorResponseViolations> violations;

    public ApiErrorResponse() {
        // Default constructor for JSON-B deserialization
    }

    public ApiErrorResponse(final String code, final String message,
                            final List<ApiErrorResponseViolations> violations) {
        this.code = code;
        this.message = message;
        this.violations = violations;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<ApiErrorResponseViolations> getViolations() {
        return violations;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ApiErrorResponse that = (ApiErrorResponse) obj;
        return java.util.Objects.equals(code, that.code)
            && java.util.Objects.equals(message, that.message)
            && java.util.Objects.equals(violations, that.violations);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(code, message, violations);
    }

    @Override
    public String toString() {
        return "ApiErrorResponse{"
                + "code='" + code + '\''
                + ", message='" + message + '\''
                + ", violations=" + violations
                + '}';
    }
}
