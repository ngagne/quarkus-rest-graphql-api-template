package com.example.api.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.api.downstream.CustomerCoreGateway;
import com.example.api.downstream.ExposureGateway;
import com.example.api.model.CreateCustomerProfileInput;
import com.example.api.model.CustomerProfileView;
import com.example.api.model.CustomerCoreProfile;
import com.example.api.model.ProductExposure;
import com.example.api.model.UpdateCustomerProfileInput;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CustomerProfileServiceTest {

    @InjectMock
    CustomerCoreGateway customerCoreGateway;

    @InjectMock
    ExposureGateway exposureGateway;

    @Inject
    CustomerProfileService service;

    @Test
    void shouldAssembleProfileFromDownstreamResponses() {
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
        final InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> service.getCustomerProfile("   ")
        );

        assertEquals("customerId must not be blank", exception.getMessage());
    }

    @Test
    void shouldCreateCustomerProfile() {
        final CreateCustomerProfileInput input = new CreateCustomerProfileInput(
                "CUST-NEW",
                "Taylor",
                "Kim",
                "RETAIL",
                "EUR",
                new BigDecimal("50000.00")
        );

        when(customerCoreGateway.createCustomerProfile(org.mockito.ArgumentMatchers.any(CustomerCoreProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(customerCoreGateway.fetchCustomerProfile("CUST-NEW")).thenReturn(new CustomerCoreProfile(
                "CUST-NEW",
                "Taylor",
                "Kim",
                "RETAIL",
                "EUR",
                new BigDecimal("50000.00")
        ));
        when(exposureGateway.fetchExposures("CUST-NEW")).thenReturn(List.of());

        final CustomerProfileView result = service.createCustomerProfile(input);

        assertAll(
                () -> assertEquals("CUST-NEW", result.getCustomerId()),
                () -> assertEquals("Taylor Kim", result.getFullName()),
                () -> assertEquals("RETAIL", result.getSegment()),
                () -> assertEquals("EUR", result.getBaseCurrency()),
                () -> assertEquals(new BigDecimal("50000.00"), result.getAvailableBalance())
        );
        verify(customerCoreGateway).createCustomerProfile(org.mockito.ArgumentMatchers.any(CustomerCoreProfile.class));
    }

    @Test
    void shouldUpdateAvailableBalance() {
        final CustomerCoreProfile existingProfile = new CustomerCoreProfile(
                "CUST-BAL",
                "Morgan",
                "Davis",
                "WEALTH",
                "USD",
                new BigDecimal("100000.00")
        );

        final BigDecimal newBalance = new BigDecimal("150000.00");

        when(customerCoreGateway.fetchCustomerProfile("CUST-BAL")).thenReturn(existingProfile);
        when(customerCoreGateway.updateCustomerProfile(org.mockito.ArgumentMatchers.any(CustomerCoreProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(customerCoreGateway.fetchCustomerProfile("CUST-BAL")).thenReturn(new CustomerCoreProfile(
                "CUST-BAL",
                "Morgan",
                "Davis",
                "WEALTH",
                "USD",
                newBalance
        ));
        when(exposureGateway.fetchExposures("CUST-BAL")).thenReturn(List.of());

        final CustomerProfileView result = service.updateAvailableBalance("CUST-BAL", newBalance);

        assertAll(
                () -> assertEquals("CUST-BAL", result.getCustomerId()),
                () -> assertEquals("Morgan Davis", result.getFullName()),
                () -> assertEquals("WEALTH", result.getSegment()),
                () -> assertEquals("USD", result.getBaseCurrency()),
                () -> assertEquals(newBalance, result.getAvailableBalance())
        );
        verify(customerCoreGateway).updateCustomerProfile(org.mockito.ArgumentMatchers.any(CustomerCoreProfile.class));
    }

    @Test
    void shouldUpdateName() {
        final CustomerCoreProfile existingProfile = new CustomerCoreProfile(
                "CUST-NAME",
                "Morgan",
                "Davis",
                "WEALTH",
                "USD",
                new BigDecimal("100000.00")
        );

        final String newGivenName = "Morgan";
        final String newFamilyName = "Davis-Wilson";

        when(customerCoreGateway.fetchCustomerProfile("CUST-NAME")).thenReturn(existingProfile);
        when(customerCoreGateway.updateCustomerProfile(org.mockito.ArgumentMatchers.any(CustomerCoreProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(customerCoreGateway.fetchCustomerProfile("CUST-NAME")).thenReturn(new CustomerCoreProfile(
                "CUST-NAME",
                newGivenName,
                newFamilyName,
                "WEALTH",
                "USD",
                new BigDecimal("100000.00")
        ));
        when(exposureGateway.fetchExposures("CUST-NAME")).thenReturn(List.of());

        final CustomerProfileView result = service.updateName("CUST-NAME", newGivenName, newFamilyName);

        assertAll(
                () -> assertEquals("CUST-NAME", result.getCustomerId()),
                () -> assertEquals("Morgan Davis-Wilson", result.getFullName()),
                () -> assertEquals("WEALTH", result.getSegment()),
                () -> assertEquals("USD", result.getBaseCurrency()),
                () -> assertEquals(new BigDecimal("100000.00"), result.getAvailableBalance())
        );
        verify(customerCoreGateway).updateCustomerProfile(org.mockito.ArgumentMatchers.any(CustomerCoreProfile.class));
    }

    @Test
    void shouldRejectUpdateAvailableBalanceForNonExistentCustomer() {
        when(customerCoreGateway.fetchCustomerProfile("CUST-MISSING")).thenReturn(null);

        final IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateAvailableBalance("CUST-MISSING", new BigDecimal("100.00"))
        );

        assertEquals("Customer profile not found: CUST-MISSING", exception.getMessage());
    }

    @Test
    void shouldRejectUpdateNameForNonExistentCustomer() {
        when(customerCoreGateway.fetchCustomerProfile("CUST-MISSING")).thenReturn(null);

        final IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateName("CUST-MISSING", "Unknown", "User")
        );

        assertEquals("Customer profile not found: CUST-MISSING", exception.getMessage());
    }

    @Test
    void shouldUpdateCustomerProfileWithPartialFields() {
        final CustomerCoreProfile existingProfile = new CustomerCoreProfile(
                "CUST-PARTIAL",
                "Morgan",
                "Davis",
                "WEALTH",
                "USD",
                new BigDecimal("100000.00")
        );

        final UpdateCustomerProfileInput input = new UpdateCustomerProfileInput(
                "CUST-PARTIAL",
                null,
                "Davis-Wilson",
                null,
                null,
                null
        );

        when(customerCoreGateway.fetchCustomerProfile("CUST-PARTIAL")).thenReturn(existingProfile);
        when(customerCoreGateway.updateCustomerProfile(org.mockito.ArgumentMatchers.any(CustomerCoreProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(customerCoreGateway.fetchCustomerProfile("CUST-PARTIAL")).thenReturn(new CustomerCoreProfile(
                "CUST-PARTIAL",
                "Morgan",
                "Davis-Wilson",
                "WEALTH",
                "USD",
                new BigDecimal("100000.00")
        ));
        when(exposureGateway.fetchExposures("CUST-PARTIAL")).thenReturn(List.of());

        final CustomerProfileView result = service.updateCustomerProfile(input);

        assertAll(
                () -> assertEquals("CUST-PARTIAL", result.getCustomerId()),
                () -> assertEquals("Morgan Davis-Wilson", result.getFullName()),
                () -> assertEquals("WEALTH", result.getSegment()),
                () -> assertEquals("USD", result.getBaseCurrency()),
                () -> assertEquals(new BigDecimal("100000.00"), result.getAvailableBalance())
        );
        verify(customerCoreGateway).updateCustomerProfile(org.mockito.ArgumentMatchers.any(CustomerCoreProfile.class));
    }

    @Test
    void shouldRejectUpdateCustomerProfileForNonExistentCustomer() {
        final UpdateCustomerProfileInput input = new UpdateCustomerProfileInput(
                "CUST-MISSING",
                "John",
                "Doe",
                "RETAIL",
                "USD",
                new BigDecimal("1000.00")
        );

        when(customerCoreGateway.fetchCustomerProfile("CUST-MISSING")).thenReturn(null);

        final IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateCustomerProfile(input)
        );

        assertEquals("Customer profile not found: CUST-MISSING", exception.getMessage());
    }

    @Test
    void shouldRejectCreateWithNullInput() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> service.createCustomerProfile(null)
        );

        assertEquals("input must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectUpdateAvailableBalanceWithNullCustomerId() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> service.updateAvailableBalance(null, new BigDecimal("100.00"))
        );

        assertEquals("customerId must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectUpdateAvailableBalanceWithNullBalance() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> service.updateAvailableBalance("CUST-001", null)
        );

        assertEquals("availableBalance must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectUpdateNameWithNullCustomerId() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> service.updateName(null, "John", "Doe")
        );

        assertEquals("customerId must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectUpdateNameWithNullGivenName() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> service.updateName("CUST-001", null, "Doe")
        );

        assertEquals("givenName must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectUpdateNameWithNullFamilyName() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> service.updateName("CUST-001", "John", null)
        );

        assertEquals("familyName must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectUpdateCustomerProfileWithNullInput() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> service.updateCustomerProfile(null)
        );

        assertEquals("input must not be null", exception.getMessage());
    }
}
