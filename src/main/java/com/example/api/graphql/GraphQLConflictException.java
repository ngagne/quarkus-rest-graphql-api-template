package com.example.api.graphql;

/**
 * Exception thrown to indicate a resource conflict in a GraphQL operation.
 * Maps to CONFLICT error code in GraphQL responses.
 */
public class GraphQLConflictException extends RuntimeException {

    public GraphQLConflictException(final String message) {
        super(message);
    }
}
