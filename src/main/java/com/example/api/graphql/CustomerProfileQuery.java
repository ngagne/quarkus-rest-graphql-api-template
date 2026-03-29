package com.example.api.graphql;

import com.example.api.application.CustomerProfileService;
import com.example.api.mapper.CustomerProfileMapper;
import com.example.api.model.CreateCustomerProfileInput;
import com.example.api.model.CustomerProfileView;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
@ApplicationScoped
public class CustomerProfileQuery {

    private final CustomerProfileService customerProfileService;
    private final CustomerProfileMapper mapper = CustomerProfileMapper.INSTANCE;

    public CustomerProfileQuery(final CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @Query("customerProfile")
    public CustomerProfileView customerProfile(
            @Name("customerId")
            final String customerId
    ) {
        return customerProfileService.getCustomerProfile(customerId);
    }

    @Mutation("createCustomerProfile")
    public CustomerProfileView createCustomerProfile(
            final CreateCustomerProfileInput input
    ) {
        return customerProfileService.createCustomerProfile(
                mapper.toCustomerCoreProfile(input));
    }

    @Mutation("updateAvailableBalance")
    public CustomerProfileView updateAvailableBalance(
            @Name("customerId") final String customerId,
            @Name("availableBalance") final BigDecimal availableBalance
    ) {
        return customerProfileService.updateAvailableBalance(customerId, availableBalance);
    }

    @Mutation("updateName")
    public CustomerProfileView updateName(
            @Name("customerId") final String customerId,
            @Name("givenName") final String givenName,
            @Name("familyName") final String familyName
    ) {
        return customerProfileService.updateName(customerId, givenName, familyName);
    }
}
