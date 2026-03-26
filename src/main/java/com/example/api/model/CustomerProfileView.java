package com.example.api.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record CustomerProfileView(
        String customerId,
        String fullName,
        String segment,
        String baseCurrency,
        BigDecimal availableBalance,
        BigDecimal totalExposure,
        List<ProductExposure> exposures
) {

    public CustomerProfileView {
        Objects.requireNonNull(exposures, "exposures must not be null");
        exposures = List.copyOf(exposures);
    }
}

