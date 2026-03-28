package com.example.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ConstraintViolationExceptionMapperTest {

    @jakarta.inject.Inject
    ConstraintViolationExceptionMapper mapper;

    @Test
    void shouldMapConstraintViolationExceptionToBadRequest() {
        @SuppressWarnings("unchecked")
        final ConstraintViolation<String> violation1 = Mockito.mock(ConstraintViolation.class);
        Mockito.when(violation1.getPropertyPath()).thenReturn(Mockito.mock(jakarta.validation.Path.class));
        Mockito.when(violation1.getPropertyPath().toString()).thenReturn("customerId");
        Mockito.when(violation1.getMessage()).thenReturn("must not be blank");

        final ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation1));

        final Response response = mapper.toResponse(exception);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        final ApiError error = (ApiError) response.getEntity();
        assertNotNull(error);
        assertEquals("VALIDATION_FAILED", error.code());
        assertEquals(1, error.violations().size());
    }

    @Test
    void shouldSortViolationsByFieldThenMessage() {
        @SuppressWarnings("unchecked")
        final ConstraintViolation<String> violation1 = Mockito.mock(ConstraintViolation.class);
        Mockito.when(violation1.getPropertyPath()).thenReturn(Mockito.mock(jakarta.validation.Path.class));
        Mockito.when(violation1.getPropertyPath().toString()).thenReturn("zField");
        Mockito.when(violation1.getMessage()).thenReturn("error z");

        @SuppressWarnings("unchecked")
        final ConstraintViolation<String> violation2 = Mockito.mock(ConstraintViolation.class);
        Mockito.when(violation2.getPropertyPath()).thenReturn(Mockito.mock(jakarta.validation.Path.class));
        Mockito.when(violation2.getPropertyPath().toString()).thenReturn("aField");
        Mockito.when(violation2.getMessage()).thenReturn("error a");

        @SuppressWarnings("unchecked")
        final ConstraintViolation<String> violation3 = Mockito.mock(ConstraintViolation.class);
        Mockito.when(violation3.getPropertyPath()).thenReturn(Mockito.mock(jakarta.validation.Path.class));
        Mockito.when(violation3.getPropertyPath().toString()).thenReturn("aField");
        Mockito.when(violation3.getMessage()).thenReturn("error b");

        final ConstraintViolationException exception = new ConstraintViolationException(
                Set.of(violation1, violation2, violation3)
        );

        final Response response = mapper.toResponse(exception);

        final ApiError error = (ApiError) response.getEntity();
        assertEquals("aField", error.violations().get(0).field());
        assertEquals("error a", error.violations().get(0).message());
        assertEquals("aField", error.violations().get(1).field());
        assertEquals("error b", error.violations().get(1).message());
        assertEquals("zField", error.violations().get(2).field());
    }

    @Test
    void shouldHandleNestedPropertyPath() {
        @SuppressWarnings("unchecked")
        final ConstraintViolation<String> violation = Mockito.mock(ConstraintViolation.class);
        Mockito.when(violation.getPropertyPath()).thenReturn(Mockito.mock(jakarta.validation.Path.class));
        Mockito.when(violation.getPropertyPath().toString()).thenReturn("request.customerId");
        Mockito.when(violation.getMessage()).thenReturn("must not be null");

        final ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        final Response response = mapper.toResponse(exception);

        final ApiError error = (ApiError) response.getEntity();
        assertEquals("customerId", error.violations().get(0).field());
    }
}
