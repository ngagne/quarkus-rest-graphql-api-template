package com.example.api.downstream.rest;

import com.example.api.model.ProductExposure;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * Declarative REST client for fetching product exposures from downstream service.
 *
 * Configuration:
 * <pre>
 * # application.properties
 * exposure-service.base-url=http://localhost:8082
 * </pre>
 *
 * @see RestExposureGateway for the gateway implementation
 */
@RegisterRestClient(configKey = "exposure-service")
@RegisterClientHeaders
@Produces(MediaType.APPLICATION_JSON)
public interface ExposureRestClient {

    @GET
    @Path("/api/customers/{customerId}/exposures")
    List<ProductExposure> getExposures(@PathParam("customerId") String customerId);
}
