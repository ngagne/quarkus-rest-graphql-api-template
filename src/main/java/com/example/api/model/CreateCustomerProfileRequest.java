package com.example.api.model;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;

/**
 * Request to create a new customer profile via REST API
 */
public class CreateCustomerProfileRequest {

    @NotNull
    private String customerId;
    @NotNull
    private String givenName;
    @NotNull
    private String familyName;
    @NotNull
    private String segment;
    @NotNull
    private String baseCurrency;
    @NotNull
    private BigDecimal availableBalance;

    public CreateCustomerProfileRequest() {
        // Default constructor for JSON-B deserialization
    }

    public CreateCustomerProfileRequest(final String customerId, final String givenName, final String familyName,
                                        final String segment, final String baseCurrency,
                                        final BigDecimal availableBalance) {
        this.customerId = customerId;
        this.givenName = givenName;
        this.familyName = familyName;
        this.segment = segment;
        this.baseCurrency = baseCurrency;
        this.availableBalance = availableBalance;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getSegment() {
        return segment;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final CreateCustomerProfileRequest that = (CreateCustomerProfileRequest) obj;
        return Objects.equals(customerId, that.customerId)
            && Objects.equals(givenName, that.givenName)
            && Objects.equals(familyName, that.familyName)
            && Objects.equals(segment, that.segment)
            && Objects.equals(baseCurrency, that.baseCurrency)
            && Objects.equals(availableBalance, that.availableBalance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, givenName, familyName, segment, baseCurrency, availableBalance);
    }

    @Override
    public String toString() {
        return "CreateCustomerProfileRequest{"
                + "customerId='" + customerId + '\''
                + ", givenName='" + givenName + '\''
                + ", familyName='" + familyName + '\''
                + ", segment='" + segment + '\''
                + ", baseCurrency='" + baseCurrency + '\''
                + ", availableBalance=" + availableBalance
                + '}';
    }
}
