package com.example.api.rest;

import com.example.api.application.CustomerProfileService;
import com.example.api.model.CustomerProfileView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

@Path("/api/customers")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerProfileResource {

    private final CustomerProfileService customerProfileService;

    public CustomerProfileResource(final CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @GET
    @Path("/{customerId}/profile")
    @Operation(summary = "Get an aggregated customer profile")
    public CustomerProfileView getCustomerProfile(
            @PathParam("customerId")
            @Parameter(description = "Opaque customer identifier")
            @NotBlank
            @Size(max = 64)
            final String customerId
    ) {
        return customerProfileService.getCustomerProfile(customerId);
    }
}
