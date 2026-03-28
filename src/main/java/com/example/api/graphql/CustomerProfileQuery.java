package com.example.api.graphql;

import com.example.api.application.CustomerProfileService;
import com.example.api.model.CreateCustomerProfileInput;
import com.example.api.model.CustomerProfileView;
import com.example.api.model.UpdateCustomerProfileInput;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
@ApplicationScoped
public class CustomerProfileQuery {

    private final CustomerProfileService customerProfileService;

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
        return customerProfileService.createCustomerProfile(input);
    }

    @Mutation("updateCustomerProfile")
    public CustomerProfileView updateCustomerProfile(
            final UpdateCustomerProfileInput input
    ) {
        return customerProfileService.updateCustomerProfile(input);
    }
}
