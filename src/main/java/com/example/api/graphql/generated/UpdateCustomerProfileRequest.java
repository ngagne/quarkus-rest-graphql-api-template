package com.example.api.graphql.generated;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;

import jakarta.validation.constraints.NotNull;

@jakarta.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
    date = "2026-03-28T13:06:07.025361-04:00[America/New_York]"
)
public class UpdateCustomerProfileRequest {

    @NotNull
    private String customerId;
    private String givenName;
    private String familyName;
    private String segment;
    private String baseCurrency;
    private BigDecimal availableBalance;

    public UpdateCustomerProfileRequest() {
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
        StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
        if (customerId != null) {
            joiner.add("customerId=\"" + customerId + "\"");
        }
        if (givenName != null) {
            joiner.add("givenName=\"" + givenName + "\"");
        }
        if (familyName != null) {
            joiner.add("familyName=\"" + familyName + "\"");
        }
        if (segment != null) {
            joiner.add("segment=\"" + segment + "\"");
        }
        if (baseCurrency != null) {
            joiner.add("baseCurrency=\"" + baseCurrency + "\"");
        }
        if (availableBalance != null) {
            joiner.add("availableBalance=" + availableBalance);
        }
        return joiner.toString();
    }
}
