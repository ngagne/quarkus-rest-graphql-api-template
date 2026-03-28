package com.example.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ApiViolationTest {

    @Test
    void shouldCreateApiViolation() {
        final ApiViolation violation = new ApiViolation("customerId", "must not be blank");

        assertEquals("customerId", violation.field());
        assertEquals("must not be blank", violation.message());
    }

    @Test
    void shouldCreateApiViolationWithEmptyValues() {
        final ApiViolation violation = new ApiViolation("", "");

        assertEquals("", violation.field());
        assertEquals("", violation.message());
    }
}
