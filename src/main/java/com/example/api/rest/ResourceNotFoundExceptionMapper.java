package com.example.api.rest;

import com.example.api.application.ResourceNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    @Override
    public Response toResponse(final ResourceNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiError.notFound(exception.getMessage()))
                .build();
    }
}
