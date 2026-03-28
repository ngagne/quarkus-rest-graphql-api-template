package com.example.api.model;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;

/**
 * Request to update an existing customer profile via REST API
 */
public class UpdateCustomerProfileRequest {

    @NotNull
    private String customerId;
    private String givenName;
    private String familyName;
    private String segment;
    private String baseCurrency;
    private BigDecimal availableBalance;

    public UpdateCustomerProfileRequest() {
        // Default constructor for JSON-B deserialization
    }

    public UpdateCustomerProfileRequest(final String customerId, final String givenName, final String familyName,
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
        final UpdateCustomerProfileRequest that = (UpdateCustomerProfileRequest) obj;
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
        return "UpdateCustomerProfileRequest{"
                + "customerId='" + customerId + '\''
                + ", givenName='" + givenName + '\''
                + ", familyName='" + familyName + '\''
                + ", segment='" + segment + '\''
                + ", baseCurrency='" + baseCurrency + '\''
                + ", availableBalance=" + availableBalance
                + '}';
    }
}
