package com.example.api.rest;

import com.example.api.application.CustomerProfileService;
import com.example.api.model.CreateCustomerProfileInput;
import com.example.api.model.CreateCustomerProfileRequest;
import com.example.api.model.CustomerProfileView;
import com.example.api.model.UpdateCustomerProfileInput;
import com.example.api.model.UpdateCustomerProfileRequest;
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

    @Override
    public CustomerProfileView createCustomerProfile(final CreateCustomerProfileRequest request) {
        final CreateCustomerProfileInput input = new CreateCustomerProfileInput(
                request.getCustomerId(),
                request.getGivenName(),
                request.getFamilyName(),
                request.getSegment(),
                request.getBaseCurrency(),
                request.getAvailableBalance()
        );
        return customerProfileService.createCustomerProfile(input);
    }

    @Override
    public CustomerProfileView updateCustomerProfile(final String customerId,
                                                     final UpdateCustomerProfileRequest request) {
        final UpdateCustomerProfileInput input = new UpdateCustomerProfileInput(
                request.getCustomerId(),
                request.getGivenName(),
                request.getFamilyName(),
                request.getSegment(),
                request.getBaseCurrency(),
                request.getAvailableBalance()
        );
        return customerProfileService.updateCustomerProfile(input);
    }
}
