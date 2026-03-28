package com.example.api.downstream.stub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.api.model.ProductExposure;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StubExposureGatewayTest {

    @Inject
    StubExposureGateway gateway;

    @Test
    void shouldFetchExposures() {
        final List<ProductExposure> exposures = gateway.fetchExposures("CUST-001");

        assertNotNull(exposures);
        assertEquals(2, exposures.size());
    }

    @Test
    void shouldFetchExposuresWithCorrectValues() {
        final List<ProductExposure> exposures = gateway.fetchExposures("CUST-001");

        final ProductExposure fxExposure = exposures.get(0);
        assertEquals("FX-FWD", fxExposure.getProductCode());
        assertEquals("USD", fxExposure.getCurrency());
        assertEquals(new BigDecimal("250000.00"), fxExposure.getNotional());

        final ProductExposure irsExposure = exposures.get(1);
        assertEquals("IRS", irsExposure.getProductCode());
        assertEquals("USD", irsExposure.getCurrency());
        assertEquals(new BigDecimal("800000.00"), irsExposure.getNotional());
    }

    @Test
    void shouldFetchExposuresForAnyCustomerId() {
        final List<ProductExposure> exposures1 = gateway.fetchExposures("CUST-A");
        final List<ProductExposure> exposures2 = gateway.fetchExposures("CUST-B");
        final List<ProductExposure> exposures3 = gateway.fetchExposures("ANY-ID");

        assertEquals(exposures1, exposures2);
        assertEquals(exposures2, exposures3);
    }
}
