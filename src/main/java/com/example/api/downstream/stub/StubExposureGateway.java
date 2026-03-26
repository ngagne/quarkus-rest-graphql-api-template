package com.example.api.downstream.stub;

import com.example.api.downstream.ExposureGateway;
import com.example.api.model.ProductExposure;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class StubExposureGateway implements ExposureGateway {

    @Override
    public List<ProductExposure> fetchExposures(final String customerId) {
        return List.of(
                new ProductExposure("FX-FWD", "USD", new BigDecimal("250000.00")),
                new ProductExposure("IRS", "USD", new BigDecimal("800000.00"))
        );
    }
}

