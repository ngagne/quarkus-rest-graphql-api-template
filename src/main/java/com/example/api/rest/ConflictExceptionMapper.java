package com.example.api.rest;

import com.example.api.application.ConflictException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConflictExceptionMapper implements ExceptionMapper<ConflictException> {

    @Override
    public Response toResponse(final ConflictException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity(ApiError.conflict(exception.getMessage()))
                .build();
    }
}
