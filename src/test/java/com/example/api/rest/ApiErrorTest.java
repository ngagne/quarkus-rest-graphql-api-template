package com.example.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ApiErrorTest {

    @Test
    void shouldCreateApiErrorWithViolations() {
        final List<ApiViolation> violations = List.of(new ApiViolation("field", "message"));
        final ApiError error = new ApiError("CODE", "message", violations);

        assertEquals("CODE", error.code());
        assertEquals("message", error.message());
        assertEquals(1, error.violations().size());
    }

    @Test
    void shouldCreateApiErrorWithNullViolations() {
        final ApiError error = new ApiError("CODE", "message", null);

        assertEquals("CODE", error.code());
        assertEquals("message", error.message());
        assertNotNull(error.violations());
        assertTrue(error.violations().isEmpty());
    }

    @Test
    void shouldCreateApiErrorWithEmptyViolations() {
        final ApiError error = new ApiError("CODE", "message", List.of());

        assertEquals("CODE", error.code());
        assertEquals("message", error.message());
        assertTrue(error.violations().isEmpty());
    }

    @Test
    void shouldCreateInvalidRequestError() {
        final ApiError error = ApiError.invalidRequest("customerId", "must not be blank");

        assertEquals("INVALID_REQUEST", error.code());
        assertEquals("must not be blank", error.message());
        assertEquals(1, error.violations().size());
        assertEquals("customerId", error.violations().get(0).field());
    }

    @Test
    void shouldCreateValidationFailedError() {
        final List<ApiViolation> violations = List.of(
                new ApiViolation("field1", "message1"),
                new ApiViolation("field2", "message2")
        );
        final ApiError error = ApiError.validationFailed(violations);

        assertEquals("VALIDATION_FAILED", error.code());
        assertEquals("Request validation failed", error.message());
        assertEquals(2, error.violations().size());
    }

    @Test
    void shouldCreateValidationFailedErrorWithEmptyViolations() {
        final ApiError error = ApiError.validationFailed(List.of());

        assertEquals("VALIDATION_FAILED", error.code());
        assertEquals("Request validation failed", error.message());
        assertTrue(error.violations().isEmpty());
    }
}
