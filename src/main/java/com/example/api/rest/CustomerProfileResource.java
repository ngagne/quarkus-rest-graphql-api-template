package com.example.api.rest;

import com.example.api.application.CustomerProfileService;
import com.example.api.model.CreateCustomerProfileInput;
import com.example.api.model.CreateCustomerProfileRequest;
import com.example.api.model.CustomerProfileView;
import com.example.api.model.UpdateCustomerProfileInput;
import com.example.api.model.UpdateCustomerProfileRequest;
import com.example.api.rest.generated.CustomerProfileApi;
import jakarta.ws.rs.core.Response;

public class CustomerProfileResource implements CustomerProfileApi {

    private final CustomerProfileService customerProfileService;

    public CustomerProfileResource(final CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @Override
    public Response getCustomerProfile(final String customerId) {
        final CustomerProfileView profile = customerProfileService.getCustomerProfile(customerId);
        return Response.ok(profile).build();
    }

    @Override
    public Response createCustomerProfile(final CreateCustomerProfileRequest request) {
        final CreateCustomerProfileInput input = new CreateCustomerProfileInput(
                request.getCustomerId(),
                request.getGivenName(),
                request.getFamilyName(),
                request.getSegment(),
                request.getBaseCurrency(),
                request.getAvailableBalance()
        );
        final CustomerProfileView profile = customerProfileService.createCustomerProfile(input);
        return Response.status(Response.Status.CREATED).entity(profile).build();
    }

    @Override
    public Response updateCustomerProfile(final String customerId,
                                          final UpdateCustomerProfileRequest request) {
        final UpdateCustomerProfileInput input = new UpdateCustomerProfileInput(
                request.getCustomerId(),
                request.getGivenName(),
                request.getFamilyName(),
                request.getSegment(),
                request.getBaseCurrency(),
                request.getAvailableBalance()
        );
        final CustomerProfileView profile = customerProfileService.updateCustomerProfile(input);
        return Response.ok(profile).build();
    }
}
