package com.example.api.rest;

import com.example.api.application.InvalidRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidRequestExceptionMapper implements ExceptionMapper<InvalidRequestException> {

    @Override
    public Response toResponse(final InvalidRequestException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiError.invalidRequest(exception.field(), exception.getMessage()))
                .build();
    }
}
