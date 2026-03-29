package com.example.api.rest;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.HashMap;
import java.util.Map;

@Provider
public class CorsExceptionMapper implements ExceptionMapper<Throwable> {

    @ConfigProperty(name = "app.cors.allowed-origins", defaultValue = "*")
    String allowedOrigins;

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(final Throwable exception) {
        final Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", exception.getClass().getSimpleName());
        errorResponse.put("message", exception.getMessage());

        final Response.ResponseBuilder responseBuilder = Response
                .status(exception instanceof WebApplicationException
                        ? ((WebApplicationException) exception).getResponse().getStatus()
                        : Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .entity(errorResponse);

        addCorsHeaders(responseBuilder);

        return responseBuilder.build();
    }

    private void addCorsHeaders(final Response.ResponseBuilder builder) {
        builder.header("Access-Control-Allow-Origin", allowedOrigins);
        builder.header("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS");
        builder.header("Access-Control-Allow-Headers",
                "Content-Type, Authorization, X-Requested-With");
        builder.header("Access-Control-Expose-Headers", "Content-Disposition");
        builder.header("Access-Control-Max-Age", "86400");
    }
}
