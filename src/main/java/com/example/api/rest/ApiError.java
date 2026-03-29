package com.example.api.rest;

import java.util.List;

public record ApiError(String code, String message, List<ApiViolation> violations) {

    public ApiError {
        violations = violations == null ? List.of() : List.copyOf(violations);
    }

    public static ApiError invalidRequest(final String field, final String message) {
        return new ApiError("INVALID_REQUEST", message, List.of(new ApiViolation(field, message)));
    }

    public static ApiError validationFailed(final List<ApiViolation> violations) {
        return new ApiError("VALIDATION_FAILED", "Request validation failed", violations);
    }

    public static ApiError notFound(final String message) {
        return new ApiError("NOT_FOUND", message, List.of());
    }

    public static ApiError conflict(final String message) {
        return new ApiError("CONFLICT", message, List.of());
    }
}
