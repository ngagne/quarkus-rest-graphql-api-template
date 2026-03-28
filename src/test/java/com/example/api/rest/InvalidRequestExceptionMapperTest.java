package com.example.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.api.application.InvalidRequestException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

@QuarkusTest
class InvalidRequestExceptionMapperTest {

    @Inject
    InvalidRequestExceptionMapper mapper;

    @Test
    void shouldMapInvalidRequestExceptionToBadRequest() {
        final InvalidRequestException exception = new InvalidRequestException("customerId", "must not be blank");

        final Response response = mapper.toResponse(exception);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        final ApiError error = (ApiError) response.getEntity();
        assertNotNull(error);
        assertEquals("INVALID_REQUEST", error.code());
        assertEquals("must not be blank", error.message());
        assertEquals(1, error.violations().size());
        assertEquals("customerId", error.violations().get(0).field());
    }

    @Test
    void shouldMapInvalidRequestExceptionWithDifferentField() {
        final InvalidRequestException exception = new InvalidRequestException("givenName", "is required");

        final Response response = mapper.toResponse(exception);

        final ApiError error = (ApiError) response.getEntity();
        assertEquals("INVALID_REQUEST", error.code());
        assertEquals("is required", error.message());
        assertEquals("givenName", error.violations().get(0).field());
    }
}
