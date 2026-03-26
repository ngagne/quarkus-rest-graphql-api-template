package com.example.api.graphql;

import com.example.api.application.CustomerProfileService;
import com.example.api.model.CustomerProfileView;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
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
    @Description("Aggregated customer profile assembled from multiple downstream systems")
    public CustomerProfileView customerProfile(@Name("customerId") final String customerId) {
        return customerProfileService.getCustomerProfile(customerId);
    }
}

