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

/**
 * Service for managing customer profiles.
 * <p>
 * This service provides both coarse-grained operations for REST APIs
 * and fine-grained, use-case specific operations for GraphQL mutations.
 */

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
        validateGivenName(input.getGivenName());
        validateFamilyName(input.getFamilyName());
        validateSegment(input.getSegment());
        validateBaseCurrency(input.getBaseCurrency());
        validateAvailableBalance(input.getAvailableBalance());

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

    /**
     * Updates the available balance for a customer profile.
     * GraphQL mutation: updateAvailableBalance
     */
    public CustomerProfileView updateAvailableBalance(final String customerId, final BigDecimal availableBalance) {
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(availableBalance, "availableBalance must not be null");

        final String normalizedCustomerId = normalizeCustomerId(customerId);
        final CustomerCoreProfile existingProfile = customerCoreGateway.fetchCustomerProfile(normalizedCustomerId);
        if (existingProfile == null) {
            throw new IllegalStateException("Customer profile not found: " + normalizedCustomerId);
        }

        final CustomerCoreProfile updatedProfile = new CustomerCoreProfile(
                normalizedCustomerId,
                existingProfile.givenName(),
                existingProfile.familyName(),
                existingProfile.segment(),
                existingProfile.baseCurrency(),
                availableBalance
        );

        customerCoreGateway.updateCustomerProfile(updatedProfile);
        return getCustomerProfile(normalizedCustomerId);
    }

    /**
     * Updates the given name and family name for a customer profile.
     * GraphQL mutation: updateName
     */
    public CustomerProfileView updateName(final String customerId, final String givenName, final String familyName) {
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(givenName, "givenName must not be null");
        Objects.requireNonNull(familyName, "familyName must not be null");

        final String normalizedCustomerId = normalizeCustomerId(customerId);
        final CustomerCoreProfile existingProfile = customerCoreGateway.fetchCustomerProfile(normalizedCustomerId);
        if (existingProfile == null) {
            throw new IllegalStateException("Customer profile not found: " + normalizedCustomerId);
        }

        final CustomerCoreProfile updatedProfile = new CustomerCoreProfile(
                normalizedCustomerId,
                givenName,
                familyName,
                existingProfile.segment(),
                existingProfile.baseCurrency(),
                existingProfile.availableBalance()
        );

        customerCoreGateway.updateCustomerProfile(updatedProfile);
        return getCustomerProfile(normalizedCustomerId);
    }

    /**
     * Updates a customer profile with provided fields.
     * REST API: PUT /api/customers/{customerId}/profile
     */
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

    private void validateGivenName(final String givenName) {
        Objects.requireNonNull(givenName, "givenName must not be null");
        if (givenName.trim().isEmpty()) {
            throw new InvalidRequestException("givenName", "givenName must not be blank");
        }
    }

    private void validateFamilyName(final String familyName) {
        Objects.requireNonNull(familyName, "familyName must not be null");
        if (familyName.trim().isEmpty()) {
            throw new InvalidRequestException("familyName", "familyName must not be blank");
        }
    }

    private void validateSegment(final String segment) {
        Objects.requireNonNull(segment, "segment must not be null");
        if (segment.trim().isEmpty()) {
            throw new InvalidRequestException("segment", "segment must not be blank");
        }
    }

    private void validateBaseCurrency(final String baseCurrency) {
        Objects.requireNonNull(baseCurrency, "baseCurrency must not be null");
        if (baseCurrency.trim().isEmpty()) {
            throw new InvalidRequestException("baseCurrency", "baseCurrency must not be blank");
        }
    }

    private void validateAvailableBalance(final BigDecimal availableBalance) {
        Objects.requireNonNull(availableBalance, "availableBalance must not be null");
        if (availableBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequestException("availableBalance", "availableBalance must not be negative");
        }
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
