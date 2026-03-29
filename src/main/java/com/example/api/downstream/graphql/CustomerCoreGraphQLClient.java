package com.example.api.downstream.graphql;

import com.example.api.downstream.CustomerCoreGateway;
import com.example.api.model.CustomerCoreProfile;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Typesafe GraphQL client interface for the downstream Customer Core service.
 *
 * This interface defines the GraphQL operations that this service will call
 * on the downstream Customer Core system.
 *
 * Configuration:
 * <pre>
 * # application.properties
 * quarkus.smallrye-graphql-client.customer-core.url=http://localhost:8081/graphql
 * </pre>
 */
@GraphQLClientApi(configKey = "customer-core")
public interface CustomerCoreGraphQLClient {

    CustomerCoreProfile customerProfile(String customerId);

    CustomerCoreProfile createCustomerProfile(CreateCustomerCoreProfileInput input);

    CustomerCoreProfile updateCustomerProfile(UpdateCustomerCoreProfileInput input);

    /**
     * Input type for creating a customer profile via GraphQL.
     */
    record CreateCustomerCoreProfileInput(
        String customerId,
        String givenName,
        String familyName,
        String segment,
        String baseCurrency,
        BigDecimal availableBalance
    ) {
    }

    /**
     * Input type for updating a customer profile via GraphQL.
     */
    record UpdateCustomerCoreProfileInput(
        String customerId,
        String givenName,
        String familyName,
        String segment,
        String baseCurrency,
        BigDecimal availableBalance
    ) {
    }
}

/**
 * GraphQL implementation of CustomerCoreGateway using SmallRye GraphQL Typesafe Client.
 *
 * This example shows how to replace the stub gateway with a real GraphQL client.
 * Configure the downstream service URL via configuration:
 *
 * <pre>
 * # application.properties
 * quarkus.smallrye-graphql-client.customer-core.url=http://localhost:8081/graphql
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
class GraphQLCustomerCoreGateway implements CustomerCoreGateway {

    private final CustomerCoreGraphQLClient client;

    GraphQLCustomerCoreGateway(final CustomerCoreGraphQLClient client) {
        this.client = client;
    }

    @Override
    public CustomerCoreProfile fetchCustomerProfile(final String customerId) {
        Objects.requireNonNull(customerId, "customerId must not be null");
        return client.customerProfile(customerId);
    }

    @Override
    public CustomerCoreProfile createCustomerProfile(final CustomerCoreProfile profile) {
        Objects.requireNonNull(profile, "profile must not be null");
        Objects.requireNonNull(profile.customerId(), "customerId must not be null");

        final CustomerCoreGraphQLClient.CreateCustomerCoreProfileInput input =
            new CustomerCoreGraphQLClient.CreateCustomerCoreProfileInput(
                profile.customerId(),
                profile.givenName(),
                profile.familyName(),
                profile.segment(),
                profile.baseCurrency(),
                profile.availableBalance()
            );

        return client.createCustomerProfile(input);
    }

    @Override
    public CustomerCoreProfile updateCustomerProfile(final CustomerCoreProfile profile) {
        Objects.requireNonNull(profile, "profile must not be null");
        Objects.requireNonNull(profile.customerId(), "customerId must not be null");

        final CustomerCoreGraphQLClient.UpdateCustomerCoreProfileInput input =
            new CustomerCoreGraphQLClient.UpdateCustomerCoreProfileInput(
                profile.customerId(),
                profile.givenName(),
                profile.familyName(),
                profile.segment(),
                profile.baseCurrency(),
                profile.availableBalance()
            );

        return client.updateCustomerProfile(input);
    }
}
