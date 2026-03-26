package com.example.api.downstream.stub;

import com.example.api.downstream.CustomerCoreGateway;
import com.example.api.model.CustomerCoreProfile;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

@ApplicationScoped
public class StubCustomerCoreGateway implements CustomerCoreGateway {

    @Override
    public CustomerCoreProfile fetchCustomerProfile(final String customerId) {
        return new CustomerCoreProfile(
                customerId,
                "Avery",
                "Stone",
                "COMMERCIAL_BANKING",
                "USD",
                new BigDecimal("1250000.00")
        );
    }
}

