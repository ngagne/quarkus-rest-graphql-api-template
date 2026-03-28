package com.example.api.graphql;

import com.example.api.application.CustomerProfileService;
import com.example.api.graphql.generated.CustomerProfileView;
import com.example.api.graphql.generated.QueryQueryResolver;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
@ApplicationScoped
public class CustomerProfileQuery implements QueryQueryResolver {

    private final CustomerProfileService customerProfileService;

    public CustomerProfileQuery(final CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @Override
    @Query("customerProfile")
    public CustomerProfileView customerProfile(
            @Name("customerId")
            final String customerId
    ) {
        return customerProfileService.getCustomerProfile(customerId);
    }
}
