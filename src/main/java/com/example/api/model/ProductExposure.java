package com.example.api.model;

import java.math.BigDecimal;

public record ProductExposure(String productCode, String currency, BigDecimal notional) {
}
