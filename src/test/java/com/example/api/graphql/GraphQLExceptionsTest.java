package com.example.api.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for GraphQL exception classes.
 */
class GraphQLExceptionsTest {

    @Test
    void shouldCreateGraphQLInvalidRequestException() {
        final String field = "customerId";
        final String message = "customerId must not be blank";

        final GraphQLInvalidRequestException exception = new GraphQLInvalidRequestException(field, message);

        assertEquals(message, exception.getMessage());
        assertEquals(field, exception.field());
    }

    @Test
    void shouldCreateGraphQLNotFoundException() {
        final String message = "Customer profile not found: CUST-123";

        final GraphQLNotFoundException exception = new GraphQLNotFoundException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldCreateGraphQLConflictException() {
        final String message = "Customer profile already exists: CUST-123";

        final GraphQLConflictException exception = new GraphQLConflictException(message);

        assertEquals(message, exception.getMessage());
    }
}
