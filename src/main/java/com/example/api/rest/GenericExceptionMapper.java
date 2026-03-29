package com.example.api.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(final Throwable exception) {
        LOG.error("Unhandled exception", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiError.internalError("An unexpected error occurred"))
                .build();
    }
}
