package com.example.api.downstream.rest;

import com.example.api.downstream.CustomerCoreGateway;
import com.example.api.model.CustomerCoreProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Objects;

/**
 * REST implementation of CustomerCoreGateway.
 *
 * This example shows how to replace the stub gateway with a real REST client.
 * Configure the downstream service URL via configuration:
 *
 * <pre>
 * # application.properties
 * app.downstream.customer-core.base-url=http://localhost:8081
 * </pre>
 *
 * To use this implementation:
 * 1. Add the @Alternative annotation to StubCustomerCoreGateway
 * 2. Remove @Alternative from this class
 * 3. Configure the downstream service URL
 *
 * @see com.example.api.downstream.stub.StubCustomerCoreGateway
 */
@ApplicationScoped
@jakarta.enterprise.inject.Alternative
public class RestCustomerCoreGateway implements CustomerCoreGateway {

    private final Client client;
    private final String baseUrl;

    public RestCustomerCoreGateway() {
        this.client = ClientBuilder.newClient();
        // In production, inject this via @ConfigProperty
        this.baseUrl = System.getProperty("app.downstream.customer-core.base-url", 
                                          "http://localhost:8081");
    }

    @Override
    public CustomerCoreProfile fetchCustomerProfile(final String customerId) {
        Objects.requireNonNull(customerId, "customerId must not be null");

        try (Response response = client
                .target(baseUrl)
                .path("api")
                .path("customers")
                .path(customerId)
                .path("profile")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get()) {

            if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                return null;
            }

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw new WebApplicationException(
                    "Failed to fetch customer profile: " + response.getStatus(),
                    response.getStatus()
                );
            }

            return response.readEntity(CustomerCoreProfile.class);

        } catch (Exception e) {
            throw new WebApplicationException(
                "Error calling downstream customer service: " + e.getMessage(),
                Response.Status.BAD_GATEWAY
            );
        }
    }

    @Override
    public CustomerCoreProfile createCustomerProfile(final CustomerCoreProfile profile) {
        Objects.requireNonNull(profile, "profile must not be null");
        Objects.requireNonNull(profile.customerId(), "customerId must not be null");

        try (Response response = client
                .target(baseUrl)
                .path("api")
                .path("customers")
                .path("profile")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(profile))) {

            if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                throw new com.example.api.application.ConflictException(
                    "Customer profile already exists: " + profile.customerId()
                );
            }

            if (response.getStatus() != Response.Status.CREATED.getStatusCode() &&
                response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw new WebApplicationException(
                    "Failed to create customer profile: " + response.getStatus(),
                    response.getStatus()
                );
            }

            return response.readEntity(CustomerCoreProfile.class);

        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new WebApplicationException(
                "Error calling downstream customer service: " + e.getMessage(),
                Response.Status.BAD_GATEWAY
            );
        }
    }

    @Override
    public CustomerCoreProfile updateCustomerProfile(final CustomerCoreProfile profile) {
        Objects.requireNonNull(profile, "profile must not be null");
        Objects.requireNonNull(profile.customerId(), "customerId must not be null");

        try (Response response = client
                .target(baseUrl)
                .path("api")
                .path("customers")
                .path(profile.customerId())
                .path("profile")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(profile))) {

            if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new com.example.api.application.ResourceNotFoundException(
                    "Customer profile not found: " + profile.customerId()
                );
            }

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw new WebApplicationException(
                    "Failed to update customer profile: " + response.getStatus(),
                    response.getStatus()
                );
            }

            return response.readEntity(CustomerCoreProfile.class);

        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new WebApplicationException(
                "Error calling downstream customer service: " + e.getMessage(),
                Response.Status.BAD_GATEWAY
            );
        }
    }
}
