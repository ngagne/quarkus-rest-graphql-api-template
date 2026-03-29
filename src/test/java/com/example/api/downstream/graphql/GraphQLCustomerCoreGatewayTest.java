package com.example.api.downstream.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.api.model.CustomerCoreProfile;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for GraphQL-based CustomerCoreGateway implementation.
 *
 * These tests verify the gateway's input mapping and null validation logic
 * without requiring a running GraphQL server.
 */
class GraphQLCustomerCoreGatewayTest {

    @Test
    void shouldRejectCreateWithNullProfile() {
        final CustomerCoreGraphQLClient mockClient = Mockito.mock(CustomerCoreGraphQLClient.class);
        final GraphQLCustomerCoreGateway gateway = new GraphQLCustomerCoreGateway(mockClient);

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> gateway.createCustomerProfile(null)
        );

        assertEquals("profile must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectCreateWithNullCustomerId() {
        final CustomerCoreGraphQLClient mockClient = Mockito.mock(CustomerCoreGraphQLClient.class);
        final GraphQLCustomerCoreGateway gateway = new GraphQLCustomerCoreGateway(mockClient);

        final CustomerCoreProfile input = new CustomerCoreProfile(
                null,
                "John",
                "Doe",
                "RETAIL",
                "USD",
                new BigDecimal("10000.00")
        );

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> gateway.createCustomerProfile(input)
        );

        assertEquals("customerId must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectUpdateWithNullProfile() {
        final CustomerCoreGraphQLClient mockClient = Mockito.mock(CustomerCoreGraphQLClient.class);
        final GraphQLCustomerCoreGateway gateway = new GraphQLCustomerCoreGateway(mockClient);

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> gateway.updateCustomerProfile(null)
        );

        assertEquals("profile must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectUpdateWithNullCustomerId() {
        final CustomerCoreGraphQLClient mockClient = Mockito.mock(CustomerCoreGraphQLClient.class);
        final GraphQLCustomerCoreGateway gateway = new GraphQLCustomerCoreGateway(mockClient);

        final CustomerCoreProfile input = new CustomerCoreProfile(
                null,
                "John",
                "Doe",
                "RETAIL",
                "USD",
                new BigDecimal("10000.00")
        );

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> gateway.updateCustomerProfile(input)
        );

        assertEquals("customerId must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectFetchWithNullCustomerId() {
        final CustomerCoreGraphQLClient mockClient = Mockito.mock(CustomerCoreGraphQLClient.class);
        final GraphQLCustomerCoreGateway gateway = new GraphQLCustomerCoreGateway(mockClient);

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> gateway.fetchCustomerProfile(null)
        );

        assertEquals("customerId must not be null", exception.getMessage());
    }

    @Test
    void shouldMapCreateProfileToGraphQLInput() {
        final CustomerCoreGraphQLClient mockClient = Mockito.mock(CustomerCoreGraphQLClient.class);
        final GraphQLCustomerCoreGateway gateway = new GraphQLCustomerCoreGateway(mockClient);

        final CustomerCoreProfile input = new CustomerCoreProfile(
                "CUST-001",
                "John",
                "Doe",
                "RETAIL",
                "USD",
                new BigDecimal("50000.00")
        );

        when(mockClient.createCustomerProfile(Mockito.any())).thenReturn(input);

        gateway.createCustomerProfile(input);

        verify(mockClient).createCustomerProfile(Mockito.any(
                CustomerCoreGraphQLClient.CreateCustomerCoreProfileInput.class));
    }

    @Test
    void shouldMapUpdateProfileToGraphQLInput() {
        final CustomerCoreGraphQLClient mockClient = Mockito.mock(CustomerCoreGraphQLClient.class);
        final GraphQLCustomerCoreGateway gateway = new GraphQLCustomerCoreGateway(mockClient);

        final CustomerCoreProfile input = new CustomerCoreProfile(
                "CUST-001",
                "John",
                "Doe",
                "RETAIL",
                "USD",
                new BigDecimal("50000.00")
        );

        when(mockClient.updateCustomerProfile(Mockito.any())).thenReturn(input);

        gateway.updateCustomerProfile(input);

        verify(mockClient).updateCustomerProfile(Mockito.any(
                CustomerCoreGraphQLClient.UpdateCustomerCoreProfileInput.class));
    }

    @Test
    void shouldMapFetchProfileToGraphQLQuery() {
        final CustomerCoreGraphQLClient mockClient = Mockito.mock(CustomerCoreGraphQLClient.class);
        final GraphQLCustomerCoreGateway gateway = new GraphQLCustomerCoreGateway(mockClient);

        final CustomerCoreProfile expected = new CustomerCoreProfile(
                "CUST-001",
                "John",
                "Doe",
                "RETAIL",
                "USD",
                new BigDecimal("50000.00")
        );

        when(mockClient.customerProfile("CUST-001")).thenReturn(expected);

        final CustomerCoreProfile result = gateway.fetchCustomerProfile("CUST-001");

        assertEquals(expected, result);
        verify(mockClient).customerProfile("CUST-001");
    }
}
