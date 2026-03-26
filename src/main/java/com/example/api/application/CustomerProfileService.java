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
import java.util.stream.Stream;

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
        final String normalizedCustomerId = normalizeCustomerId(customerId);
        final CustomerCoreProfile coreProfile = Objects.requireNonNull(
                customerCoreGateway.fetchCustomerProfile(normalizedCustomerId),
                "customerCoreGateway returned null"
        );
        final List<ProductExposure> exposures = List.copyOf(Objects.requireNonNull(
                exposureGateway.fetchExposures(normalizedCustomerId),
                "exposureGateway returned null"
        ));
        final BigDecimal totalExposure = exposures.stream()
                .map(ProductExposure::notional)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CustomerProfileView(
                coreProfile.customerId(),
                formatFullName(coreProfile),
                coreProfile.segment(),
                coreProfile.baseCurrency(),
                coreProfile.availableBalance(),
                totalExposure,
                exposures
        );
    }

    private String normalizeCustomerId(final String customerId) {
        Objects.requireNonNull(customerId, "customerId must not be null");

        final String normalizedCustomerId = customerId.trim();
        if (normalizedCustomerId.isEmpty()) {
            throw new InvalidRequestException("customerId", "customerId must not be blank");
        }
        return normalizedCustomerId;
    }

    private String formatFullName(final CustomerCoreProfile coreProfile) {
        return Stream.of(coreProfile.givenName(), coreProfile.familyName())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(namePart -> !namePart.isEmpty())
                .reduce((left, right) -> left + " " + right)
                .orElse(coreProfile.customerId());
    }
}
