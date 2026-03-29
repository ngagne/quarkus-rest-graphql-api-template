package com.example.api.downstream.rest;

import com.example.api.downstream.ExposureGateway;
import com.example.api.model.ProductExposure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Objects;

/**
 * REST implementation of ExposureGateway using Quarkus Declarative REST Client.
 *
 * This gateway fetches product exposures from a downstream service.
 * The declarative REST client provides:
 * - Automatic configuration via MicroProfile Config
 * - Built-in retry and fault tolerance support
 * - Better integration with Quarkus observability
 *
 * Configuration in application.properties:
 * <pre>
 * # Exposure Service REST Client Configuration
 * exposure-service.base-url=http://localhost:8082
 * %dev.exposure-service.base-url=http://localhost:8082
 * %test.exposure-service.base-url=http://localhost:8080
 * </pre>
 *
 * To enable this implementation:
 * 1. Add {@code @AlternativePriority(1)} to {@code StubExposureGateway}
 * 2. Configure the downstream service URL for your environment
 *
 * @see ExposureRestClient for the declarative client interface
 * @see com.example.api.downstream.stub.StubExposureGateway for the stub implementation
 */
@ApplicationScoped
@jakarta.enterprise.inject.Alternative
public class RestExposureGateway implements ExposureGateway {

    private final ExposureRestClient restClient;

    @Inject
    public RestExposureGateway(@RestClient final ExposureRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<ProductExposure> fetchExposures(final String customerId) {
        Objects.requireNonNull(customerId, "customerId must not be null");

        try {
            return restClient.getExposures(customerId);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                return List.of();
            }
            throw e;
        } catch (Exception e) {
            throw new WebApplicationException(
                "Error calling downstream exposure service: " + e.getMessage(),
                Response.Status.BAD_GATEWAY
            );
        }
    }
}
