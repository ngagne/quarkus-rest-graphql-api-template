package com.example.api.rest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext responseContext) throws IOException {
        if (!responseContext.getHeaders().containsKey("Access-Control-Allow-Origin")) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        }
        if (!responseContext.getHeaders().containsKey("Access-Control-Allow-Methods")) {
            responseContext.getHeaders().add("Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, OPTIONS");
        }
        if (!responseContext.getHeaders().containsKey("Access-Control-Allow-Headers")) {
            responseContext.getHeaders().add("Access-Control-Allow-Headers",
                    "Content-Type, Authorization, X-Requested-With");
        }
        if (!responseContext.getHeaders().containsKey("Access-Control-Expose-Headers")) {
            responseContext.getHeaders().add("Access-Control-Expose-Headers",
                    "Content-Disposition");
        }
        if (!responseContext.getHeaders().containsKey("Access-Control-Max-Age")) {
            responseContext.getHeaders().add("Access-Control-Max-Age", "86400");
        }

        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            responseContext.setStatus(200);
        }
    }
}
