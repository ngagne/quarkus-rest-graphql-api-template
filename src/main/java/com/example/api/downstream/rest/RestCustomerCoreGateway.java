package com.example.api.downstream.rest;

import com.example.api.application.ConflictException;
import com.example.api.application.ResourceNotFoundException;
import com.example.api.downstream.CustomerCoreGateway;
import com.example.api.model.CustomerCoreProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * REST implementation of CustomerCoreGateway using Quarkus Declarative REST Client.
 *
 * This example shows how to replace the stub gateway with a real REST client.
 * The declarative REST client is preferred over manual ClientBuilder usage:
 * - Automatic configuration via MicroProfile Config
 * - Built-in retry and fault tolerance support
 * - Easier testing and mocking
 * - Better integration with Quarkus observability (tracing, metrics)
 *
 * Configuration in application.properties:
 * <pre>
 * # Customer Core REST Client Configuration
 * customer-core.base-url=http://localhost:8081
 * %dev.customer-core.base-url=http://localhost:8081
 * %test.customer-core.base-url=http://localhost:8080
 * </pre>
 *
 * To enable this implementation instead of the stub:
 * 1. Add {@code @AlternativePriority(1)} to {@code StubCustomerCoreGateway}
 * 2. Remove {@code @Alternative} from this class
 * 3. Configure the downstream service URL for your environment
 *
 * @see CustomerCoreRestClient for the declarative client interface
 * @see com.example.api.downstream.stub.StubCustomerCoreGateway for the stub implementation
 */
@ApplicationScoped
public class RestCustomerCoreGateway implements CustomerCoreGateway {

    private final CustomerCoreRestClient restClient;

    @Inject
    public RestCustomerCoreGateway(@RestClient final CustomerCoreRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public CustomerCoreProfile fetchCustomerProfile(final String customerId) {
        try {
            return restClient.getCustomerProfile(customerId);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                return null;
            }
            throw e;
        } catch (Exception e) {
            throw new WebApplicationException(
                "Error calling downstream customer service: " + e.getMessage(),
                Response.Status.BAD_GATEWAY
            );
        }
    }

    @Override
    public CustomerCoreProfile createCustomerProfile(final CustomerCoreProfile profile) {
        try {
            return restClient.createCustomerProfile(profile);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                throw new ConflictException(
                    "Customer profile already exists: " + profile.customerId()
                );
            }
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
        try {
            return restClient.updateCustomerProfile(profile.customerId(), profile);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new ResourceNotFoundException(
                    "Customer profile not found: " + profile.customerId()
                );
            }
            throw e;
        } catch (Exception e) {
            throw new WebApplicationException(
                "Error calling downstream customer service: " + e.getMessage(),
                Response.Status.BAD_GATEWAY
            );
        }
    }
}
