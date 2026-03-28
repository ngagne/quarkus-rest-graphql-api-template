package com.example.api.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.api.downstream.CustomerCoreGateway;
import com.example.api.downstream.ExposureGateway;
import com.example.api.model.CustomerCoreProfile;
import com.example.api.graphql.generated.CustomerProfileView;
import com.example.api.model.ProductExposure;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class CustomerProfileServiceTest {

    @Test
    void shouldAssembleProfileFromDownstreamResponses() {
        final CustomerCoreGateway customerCoreGateway = mock(CustomerCoreGateway.class);
        final ExposureGateway exposureGateway = mock(ExposureGateway.class);
        final CustomerProfileService service = new CustomerProfileService(customerCoreGateway, exposureGateway);

        when(customerCoreGateway.fetchCustomerProfile("CUST-001")).thenReturn(new CustomerCoreProfile(
                "CUST-001",
                "Jordan",
                "Lee",
                "INSTITUTIONAL",
                "USD",
                new BigDecimal("5000000.00")
        ));
        when(exposureGateway.fetchExposures("CUST-001")).thenReturn(List.of(
                new ProductExposure("FX-OPTION", "USD", new BigDecimal("1200000.00")),
                new ProductExposure("CDS", "USD", new BigDecimal("300000.00"))
        ));

        final CustomerProfileView result = service.getCustomerProfile("CUST-001");

        assertAll(
                () -> assertEquals("CUST-001", result.getCustomerId()),
                () -> assertEquals("Jordan Lee", result.getFullName()),
                () -> assertEquals("INSTITUTIONAL", result.getSegment()),
                () -> assertEquals("USD", result.getBaseCurrency()),
                () -> assertEquals(new BigDecimal("5000000.00"), result.getAvailableBalance()),
                () -> assertEquals(new BigDecimal("1500000.00"), result.getTotalExposure()),
                () -> assertEquals(2, result.getExposures().size())
        );
    }

    @Test
    void shouldTrimCustomerIdBeforeCallingDownstreams() {
        final CustomerCoreGateway customerCoreGateway = mock(CustomerCoreGateway.class);
        final ExposureGateway exposureGateway = mock(ExposureGateway.class);
        final CustomerProfileService service = new CustomerProfileService(customerCoreGateway, exposureGateway);

        when(customerCoreGateway.fetchCustomerProfile("CUST-TRIM")).thenReturn(new CustomerCoreProfile(
                "CUST-TRIM",
                "Robin",
                "Singh",
                "COMMERCIAL_BANKING",
                "USD",
                new BigDecimal("1000.00")
        ));
        when(exposureGateway.fetchExposures("CUST-TRIM")).thenReturn(List.of());

        final CustomerProfileView result = service.getCustomerProfile("  CUST-TRIM  ");

        assertEquals("CUST-TRIM", result.getCustomerId());
        verify(customerCoreGateway).fetchCustomerProfile("CUST-TRIM");
        verify(exposureGateway).fetchExposures("CUST-TRIM");
    }

    @Test
    void shouldRejectBlankCustomerId() {
        final CustomerCoreGateway customerCoreGateway = mock(CustomerCoreGateway.class);
        final ExposureGateway exposureGateway = mock(ExposureGateway.class);
        final CustomerProfileService service = new CustomerProfileService(customerCoreGateway, exposureGateway);

        final InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> service.getCustomerProfile("   ")
        );

        assertEquals("customerId must not be blank", exception.getMessage());
    }
}
