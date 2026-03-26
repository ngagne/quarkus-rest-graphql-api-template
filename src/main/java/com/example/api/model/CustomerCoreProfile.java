package com.example.api.model;

import java.math.BigDecimal;

public record CustomerCoreProfile(
        String customerId,
        String givenName,
        String familyName,
        String segment,
        String baseCurrency,
        BigDecimal availableBalance
) {
}

