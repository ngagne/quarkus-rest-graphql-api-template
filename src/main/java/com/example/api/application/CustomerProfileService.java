package com.example.api.application;

import com.example.api.downstream.CustomerCoreGateway;
import com.example.api.downstream.ExposureGateway;
import com.example.api.model.CustomerCoreProfile;
import com.example.api.model.CustomerProfileView;
import com.example.api.model.ProductExposure;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class CustomerProfileService {

    private final CustomerCoreGateway customerCoreGateway;
    private final ExposureGateway exposureGateway;

    public CustomerProfileService(
            final CustomerCoreGateway customerCoreGateway,
            final ExposureGateway exposureGateway
    ) {
        this.customerCoreGateway = customerCoreGateway;
        this.exposureGateway = exposureGateway;
    }

    public CustomerProfileView getCustomerProfile(final String customerId) {
        Objects.requireNonNull(customerId, "customerId must not be null");

        if (customerId.isBlank()) {
            throw new IllegalArgumentException("customerId must not be blank");
        }

        final CustomerCoreProfile coreProfile = customerCoreGateway.fetchCustomerProfile(customerId);
        final List<ProductExposure> exposures = List.copyOf(exposureGateway.fetchExposures(customerId));
        final BigDecimal totalExposure = exposures.stream()
                .map(ProductExposure::notional)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CustomerProfileView(
                coreProfile.customerId(),
                coreProfile.givenName() + " " + coreProfile.familyName(),
                coreProfile.segment(),
                coreProfile.baseCurrency(),
                coreProfile.availableBalance(),
                totalExposure,
                exposures
        );
    }
}
