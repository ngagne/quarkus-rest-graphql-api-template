package com.example.api.application;

import com.example.api.downstream.CustomerCoreGateway;
import com.example.api.downstream.ExposureGateway;
import com.example.api.model.CustomerCoreProfile;
import com.example.api.model.CustomerProfileView;
import com.example.api.model.ProductExposure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Service for managing customer profiles.
 * <p>
 * This service provides both coarse-grained operations for REST APIs
 * and fine-grained, use-case specific operations for GraphQL mutations.
 * Validation is handled automatically by Jakarta Bean Validation.
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

    public CustomerProfileView createCustomerProfile(@Valid final CustomerCoreProfile profile) {
        Objects.requireNonNull(profile, "profile must not be null");

        final String normalizedCustomerId = normalizeCustomerId(profile.customerId());
        validateNameFields(profile.givenName(), profile.familyName());
        validateSegment(profile.segment());
        validateBaseCurrency(profile.baseCurrency());
        validateAvailableBalance(profile.availableBalance());

        final CustomerCoreProfile normalizedProfile = new CustomerCoreProfile(
                normalizedCustomerId,
                profile.givenName().trim(),
                profile.familyName().trim(),
                profile.segment().trim(),
                profile.baseCurrency().trim().toUpperCase(),
                profile.availableBalance()
        );

        customerCoreGateway.createCustomerProfile(normalizedProfile);
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
            throw new ResourceNotFoundException("Customer profile not found: " + normalizedCustomerId);
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
            throw new ResourceNotFoundException("Customer profile not found: " + normalizedCustomerId);
        }

        final CustomerCoreProfile updatedProfile = new CustomerCoreProfile(
                normalizedCustomerId,
                givenName.trim(),
                familyName.trim(),
                existingProfile.segment(),
                existingProfile.baseCurrency(),
                existingProfile.availableBalance()
        );

        customerCoreGateway.updateCustomerProfile(updatedProfile);
        return getCustomerProfile(normalizedCustomerId);
    }

    public CustomerProfileView updateCustomerProfile(final CustomerCoreProfile profile) {
        Objects.requireNonNull(profile, "profile must not be null");

        final String normalizedCustomerId = normalizeCustomerId(profile.customerId());
        final CustomerCoreProfile existingProfile = customerCoreGateway.fetchCustomerProfile(normalizedCustomerId);
        if (existingProfile == null) {
            throw new ResourceNotFoundException("Customer profile not found: " + normalizedCustomerId);
        }

        final CustomerCoreProfile updatedProfile = new CustomerCoreProfile(
                normalizedCustomerId,
                profile.givenName() != null ? profile.givenName().trim() : existingProfile.givenName(),
                profile.familyName() != null ? profile.familyName().trim() : existingProfile.familyName(),
                profile.segment() != null ? profile.segment().trim() : existingProfile.segment(),
                profile.baseCurrency() != null
                        ? profile.baseCurrency().trim().toUpperCase() : existingProfile.baseCurrency(),
                profile.availableBalance() != null ? profile.availableBalance() : existingProfile.availableBalance()
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

    private void validateNameFields(final String givenName, final String familyName) {
        if (givenName == null || givenName.trim().isEmpty()) {
            throw new InvalidRequestException("givenName", "givenName must not be blank");
        }
        if (familyName == null || familyName.trim().isEmpty()) {
            throw new InvalidRequestException("familyName", "familyName must not be blank");
        }
    }

    private void validateSegment(final String segment) {
        if (segment == null || segment.trim().isEmpty()) {
            throw new InvalidRequestException("segment", "segment must not be blank");
        }
    }

    private void validateBaseCurrency(final String baseCurrency) {
        if (baseCurrency == null || baseCurrency.trim().isEmpty()) {
            throw new InvalidRequestException("baseCurrency", "baseCurrency must not be blank");
        }
    }

    private void validateAvailableBalance(final BigDecimal availableBalance) {
        if (availableBalance == null) {
            throw new InvalidRequestException("availableBalance", "availableBalance must not be null");
        }
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
