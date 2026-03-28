package com.example.api.application;

import com.example.api.downstream.CustomerCoreGateway;
import com.example.api.downstream.ExposureGateway;
import com.example.api.model.CreateCustomerProfileInput;
import com.example.api.model.CustomerProfileView;
import com.example.api.model.ProductExposure;
import com.example.api.model.UpdateCustomerProfileInput;
import com.example.api.model.CustomerCoreProfile;
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
        final List<ProductExposure> exposures = Objects.requireNonNull(
                exposureGateway.fetchExposures(normalizedCustomerId),
                "exposureGateway returned null"
        );

        final BigDecimal totalExposure = exposures.stream()
                .map(ProductExposure::getNotional)
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

    public CustomerProfileView createCustomerProfile(final CreateCustomerProfileInput input) {
        Objects.requireNonNull(input, "input must not be null");

        final String normalizedCustomerId = normalizeCustomerId(input.getCustomerId());
        final CustomerCoreProfile coreProfile = new CustomerCoreProfile(
                normalizedCustomerId,
                input.getGivenName(),
                input.getFamilyName(),
                input.getSegment(),
                input.getBaseCurrency(),
                input.getAvailableBalance()
        );

        customerCoreGateway.createCustomerProfile(coreProfile);
        return getCustomerProfile(normalizedCustomerId);
    }

    public CustomerProfileView updateCustomerProfile(final UpdateCustomerProfileInput input) {
        Objects.requireNonNull(input, "input must not be null");

        final String normalizedCustomerId = normalizeCustomerId(input.getCustomerId());
        final CustomerCoreProfile existingProfile = customerCoreGateway.fetchCustomerProfile(normalizedCustomerId);
        if (existingProfile == null) {
            throw new IllegalStateException("Customer profile not found: " + normalizedCustomerId);
        }

        final CustomerCoreProfile updatedProfile = new CustomerCoreProfile(
                normalizedCustomerId,
                input.getGivenName() != null ? input.getGivenName() : existingProfile.givenName(),
                input.getFamilyName() != null ? input.getFamilyName() : existingProfile.familyName(),
                input.getSegment() != null ? input.getSegment() : existingProfile.segment(),
                input.getBaseCurrency() != null ? input.getBaseCurrency() : existingProfile.baseCurrency(),
                input.getAvailableBalance() != null ? input.getAvailableBalance() : existingProfile.availableBalance()
        );

        customerCoreGateway.updateCustomerProfile(updatedProfile);
        return getCustomerProfile(normalizedCustomerId);
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
