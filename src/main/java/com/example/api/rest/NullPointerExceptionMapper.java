package com.example.api.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException> {

    @Override
    public Response toResponse(final NullPointerException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiError.internalError("An unexpected null reference was encountered"))
                .build();
    }
}
