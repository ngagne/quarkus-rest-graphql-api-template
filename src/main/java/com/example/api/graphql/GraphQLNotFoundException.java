package com.example.api.graphql;

/**
 * Exception thrown to indicate a requested resource was not found in a GraphQL operation.
 * Maps to NOT_FOUND error code in GraphQL responses.
 */
public class GraphQLNotFoundException extends RuntimeException {

    public GraphQLNotFoundException(final String message) {
        super(message);
    }
}
