package com.example.api.graphql;

import com.example.api.application.ConflictException;
import com.example.api.application.CustomerProfileService;
import com.example.api.application.InvalidRequestException;
import com.example.api.application.ResourceNotFoundException;
import com.example.api.mapper.CustomerProfileMapper;
import com.example.api.model.CreateCustomerProfileInput;
import com.example.api.model.CustomerProfileView;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

/**
 * GraphQL API for customer profile operations.
 * <p>
 * Translates application-layer exceptions into GraphQL-specific exceptions
 * to provide structured error responses with error codes.
 */
@GraphQLApi
@ApplicationScoped
public class CustomerProfileQuery {

    private final CustomerProfileService customerProfileService;
    private final CustomerProfileMapper mapper;

    public CustomerProfileQuery(final CustomerProfileService customerProfileService,
                                final CustomerProfileMapper mapper) {
        this.customerProfileService = customerProfileService;
        this.mapper = mapper;
    }

    @Query("customerProfile")
    public CustomerProfileView customerProfile(
            @Name("customerId")
            final String customerId
    ) {
        try {
            return customerProfileService.getCustomerProfile(customerId);
        } catch (ResourceNotFoundException e) {
            throw new GraphQLNotFoundException(e.getMessage());
        } catch (InvalidRequestException e) {
            throw new GraphQLInvalidRequestException(e.field(), e.getMessage());
        }
    }

    @Mutation("createCustomerProfile")
    public CustomerProfileView createCustomerProfile(
            final CreateCustomerProfileInput input
    ) {
        try {
            return customerProfileService.createCustomerProfile(
                    mapper.toCustomerCoreProfile(input));
        } catch (ConflictException e) {
            throw new GraphQLConflictException(e.getMessage());
        } catch (InvalidRequestException e) {
            throw new GraphQLInvalidRequestException(e.field(), e.getMessage());
        }
    }

    @Mutation("updateAvailableBalance")
    public CustomerProfileView updateAvailableBalance(
            @Name("customerId") final String customerId,
            @Name("availableBalance") final BigDecimal availableBalance
    ) {
        try {
            return customerProfileService.updateAvailableBalance(customerId, availableBalance);
        } catch (ResourceNotFoundException e) {
            throw new GraphQLNotFoundException(e.getMessage());
        } catch (InvalidRequestException e) {
            throw new GraphQLInvalidRequestException(e.field(), e.getMessage());
        }
    }

    @Mutation("updateName")
    public CustomerProfileView updateName(
            @Name("customerId") final String customerId,
            @Name("givenName") final String givenName,
            @Name("familyName") final String familyName
    ) {
        try {
            return customerProfileService.updateName(customerId, givenName, familyName);
        } catch (ResourceNotFoundException e) {
            throw new GraphQLNotFoundException(e.getMessage());
        } catch (InvalidRequestException e) {
            throw new GraphQLInvalidRequestException(e.field(), e.getMessage());
        }
    }
}
