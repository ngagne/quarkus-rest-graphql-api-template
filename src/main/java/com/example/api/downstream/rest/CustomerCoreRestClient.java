package com.example.api.downstream.rest;

import com.example.api.model.CustomerCoreProfile;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Declarative REST client for Customer Core service.
 *
 * This interface defines the HTTP contract for calling the downstream Customer Core service.
 * Quarkus automatically generates the implementation at build time.
 *
 * Configuration:
 * <pre>
 * # application.properties
 * customer-core.base-url=http://localhost:8081
 * </pre>
 *
 * @see RestCustomerCoreGateway for the gateway implementation that uses this client
 */
@RegisterRestClient(configKey = "customer-core")
@RegisterClientHeaders
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CustomerCoreRestClient {

    @GET
    @Path("/api/customers/{customerId}/profile")
    CustomerCoreProfile getCustomerProfile(@PathParam("customerId") String customerId);

    @POST
    @Path("/api/customers/profile")
    CustomerCoreProfile createCustomerProfile(CustomerCoreProfile profile);

    @PUT
    @Path("/api/customers/{customerId}/profile")
    CustomerCoreProfile updateCustomerProfile(
        @PathParam("customerId") String customerId,
        CustomerCoreProfile profile
    );
}
