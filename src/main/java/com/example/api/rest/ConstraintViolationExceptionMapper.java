package com.example.api.rest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Comparator;
import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(final ConstraintViolationException exception) {
        final List<ApiViolation> violations = exception.getConstraintViolations().stream()
                .map(this::toApiViolation)
                .sorted(Comparator.comparing(ApiViolation::field).thenComparing(ApiViolation::message))
                .toList();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiError.validationFailed(violations))
                .build();
    }

    private ApiViolation toApiViolation(final ConstraintViolation<?> violation) {
        final String propertyPath = violation.getPropertyPath().toString();
        final int lastSeparator = Math.max(propertyPath.lastIndexOf('.'), propertyPath.lastIndexOf('/'));
        final String field = lastSeparator >= 0 ? propertyPath.substring(lastSeparator + 1) : propertyPath;
        return new ApiViolation(field, violation.getMessage());
    }
}
