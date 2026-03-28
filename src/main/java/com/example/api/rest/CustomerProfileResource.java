package com.example.api.rest;

import com.example.api.application.CustomerProfileService;
import com.example.api.graphql.generated.CustomerProfileView;
import com.example.api.rest.generated.CustomerProfileApi;

public class CustomerProfileResource implements CustomerProfileApi {

    private final CustomerProfileService customerProfileService;

    public CustomerProfileResource(final CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @Override
    public CustomerProfileView getCustomerProfile(final String customerId) {
        return customerProfileService.getCustomerProfile(customerId);
    }
}
