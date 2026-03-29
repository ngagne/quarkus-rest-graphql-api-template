package com.example.api.rest;

import com.example.api.application.CustomerProfileService;
import com.example.api.mapper.CustomerProfileMapper;
import com.example.api.model.CreateCustomerProfileRequest;
import com.example.api.model.CustomerProfileView;
import com.example.api.model.UpdateCustomerProfileRequest;
import com.example.api.rest.generated.CustomerProfileApi;
import jakarta.ws.rs.core.Response;

public class CustomerProfileResource implements CustomerProfileApi {

    private final CustomerProfileService customerProfileService;
    private final CustomerProfileMapper mapper;

    public CustomerProfileResource(final CustomerProfileService customerProfileService,
                                   final CustomerProfileMapper mapper) {
        this.customerProfileService = customerProfileService;
        this.mapper = mapper;
    }

    @Override
    public Response getCustomerProfile(final String customerId) {
        final CustomerProfileView profile = customerProfileService.getCustomerProfile(customerId);
        return Response.ok(profile).build();
    }

    @Override
    public Response createCustomerProfile(final CreateCustomerProfileRequest request) {
        final CustomerProfileView profile = customerProfileService.createCustomerProfile(
                mapper.toCustomerCoreProfile(request));
        return Response.status(Response.Status.CREATED).entity(profile).build();
    }

    @Override
    public Response updateCustomerProfile(final String customerId,
                                          final UpdateCustomerProfileRequest request) {
        final CustomerProfileView profile = customerProfileService.updateCustomerProfile(
                mapper.toCustomerCoreProfile(request));
        return Response.ok(profile).build();
    }
}
