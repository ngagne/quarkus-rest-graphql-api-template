package com.example.api.downstream.stub;

import com.example.api.application.ConflictException;
import com.example.api.application.ResourceNotFoundException;
import com.example.api.downstream.CustomerCoreGateway;
import com.example.api.model.CustomerCoreProfile;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class StubCustomerCoreGateway implements CustomerCoreGateway {

    private final Map<String, CustomerCoreProfile> profiles = new ConcurrentHashMap<>();

    public StubCustomerCoreGateway() {
    }

    @Override
    public CustomerCoreProfile fetchCustomerProfile(final String customerId) {
        return profiles.get(customerId);
    }

    @Override
    public CustomerCoreProfile createCustomerProfile(final CustomerCoreProfile profile) {
        Objects.requireNonNull(profile, "profile must not be null");
        Objects.requireNonNull(profile.customerId(), "customerId must not be null");

        if (profiles.containsKey(profile.customerId())) {
            throw new ConflictException("Customer profile already exists: " + profile.customerId());
        }

        profiles.put(profile.customerId(), profile);
        return profile;
    }

    @Override
    public CustomerCoreProfile updateCustomerProfile(final CustomerCoreProfile profile) {
        Objects.requireNonNull(profile, "profile must not be null");
        Objects.requireNonNull(profile.customerId(), "customerId must not be null");

        if (!profiles.containsKey(profile.customerId())) {
            throw new ResourceNotFoundException("Customer profile not found: " + profile.customerId());
        }

        final CustomerCoreProfile existing = profiles.get(profile.customerId());
        final CustomerCoreProfile updated = new CustomerCoreProfile(
                profile.customerId(),
                profile.givenName() != null ? profile.givenName() : existing.givenName(),
                profile.familyName() != null ? profile.familyName() : existing.familyName(),
                profile.segment() != null ? profile.segment() : existing.segment(),
                profile.baseCurrency() != null ? profile.baseCurrency() : existing.baseCurrency(),
                profile.availableBalance() != null ? profile.availableBalance() : existing.availableBalance()
        );

        profiles.put(profile.customerId(), updated);
        return updated;
    }
}

