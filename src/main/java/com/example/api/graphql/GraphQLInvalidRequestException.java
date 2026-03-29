package com.example.api.graphql;

/**
 * Exception thrown to indicate a GraphQL operation failed due to invalid input.
 * Maps to INVALID_REQUEST error code in GraphQL responses.
 */
public class GraphQLInvalidRequestException extends RuntimeException {

    private final String field;

    public GraphQLInvalidRequestException(final String field, final String message) {
        super(message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}
