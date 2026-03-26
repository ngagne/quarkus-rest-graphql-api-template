package com.example.api.rest;

import com.example.api.application.CustomerProfileService;
import com.example.api.model.CustomerProfileView;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/customers")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerProfileResource {

    private final CustomerProfileService customerProfileService;

    public CustomerProfileResource(final CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @GET
    @Path("/{customerId}/profile")
    public CustomerProfileView getCustomerProfile(@PathParam("customerId") final String customerId) {
        return customerProfileService.getCustomerProfile(customerId);
    }
}

