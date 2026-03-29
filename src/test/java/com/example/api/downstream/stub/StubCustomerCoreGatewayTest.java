package com.example.api.downstream.stub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.api.application.ConflictException;
import com.example.api.application.ResourceNotFoundException;
import com.example.api.model.CustomerCoreProfile;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StubCustomerCoreGatewayTest {

    @Inject
    StubCustomerCoreGateway gateway;

    @BeforeEach
    void setUp() {
        // Clear any existing profiles from previous tests
        // Note: In a real scenario, you'd want to reset the gateway state
    }

    @Test
    void shouldReturnNullForNonExistentProfile() {
        final CustomerCoreProfile profile = gateway.fetchCustomerProfile("CUST-NEW-001");

        assertEquals(null, profile);
    }

    @Test
    void shouldCreateAndFetchCustomerProfile() {
        final CustomerCoreProfile input = new CustomerCoreProfile(
                "CUST-001",
                "John",
                "Doe",
                "RETAIL",
                "USD",
                new BigDecimal("10000.00")
        );

        final CustomerCoreProfile created = gateway.createCustomerProfile(input);
        assertNotNull(created);
        assertEquals("CUST-001", created.customerId());

        final CustomerCoreProfile fetched = gateway.fetchCustomerProfile("CUST-001");
        assertNotNull(fetched);
        assertEquals("John", fetched.givenName());
        assertEquals("Doe", fetched.familyName());
    }

    @Test
    void shouldRejectCreateWithNullProfile() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> gateway.createCustomerProfile(null)
        );

        assertEquals("profile must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectCreateWithNullCustomerId() {
        final CustomerCoreProfile input = new CustomerCoreProfile(
                null,
                "John",
                "Doe",
                "RETAIL",
                "USD",
                new BigDecimal("10000.00")
        );

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> gateway.createCustomerProfile(input)
        );

        assertEquals("customerId must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectCreateDuplicateProfile() {
        final CustomerCoreProfile input = new CustomerCoreProfile(
                "CUST-DUP",
                "Jane",
                "Smith",
                "INSTITUTIONAL",
                "EUR",
                new BigDecimal("50000.00")
        );

        gateway.createCustomerProfile(input);

        final ConflictException exception = assertThrows(
                ConflictException.class,
                () -> gateway.createCustomerProfile(input)
        );

        assertEquals("Customer profile already exists: CUST-DUP", exception.getMessage());
    }

    @Test
    void shouldUpdateCustomerProfile() {
        final CustomerCoreProfile initial = new CustomerCoreProfile(
                "CUST-UPD",
                "John",
                "Doe",
                "RETAIL",
                "USD",
                new BigDecimal("10000.00")
        );
        gateway.createCustomerProfile(initial);

        final CustomerCoreProfile update = new CustomerCoreProfile(
                "CUST-UPD",
                "John",
                "Doe-Updated",
                "RETAIL",
                "USD",
                new BigDecimal("20000.00")
        );

        final CustomerCoreProfile result = gateway.updateCustomerProfile(update);
        assertNotNull(result);
        assertEquals("Doe-Updated", result.familyName());
        assertEquals(new BigDecimal("20000.00"), result.availableBalance());

        final CustomerCoreProfile fetched = gateway.fetchCustomerProfile("CUST-UPD");
        assertEquals("Doe-Updated", fetched.familyName());
    }

    @Test
    void shouldUpdateProfileWithNullFieldsPreservingExistingValues() {
        final CustomerCoreProfile initial = new CustomerCoreProfile(
                "CUST-PARTIAL",
                "John",
                "Doe",
                "RETAIL",
                "USD",
                new BigDecimal("10000.00")
        );
        gateway.createCustomerProfile(initial);

        final CustomerCoreProfile partialUpdate = new CustomerCoreProfile(
                "CUST-PARTIAL",
                null,
                null,
                null,
                null,
                null
        );

        final CustomerCoreProfile result = gateway.updateCustomerProfile(partialUpdate);
        assertNotNull(result);
        assertEquals("John", result.givenName());
        assertEquals("Doe", result.familyName());
        assertEquals("RETAIL", result.segment());
        assertEquals("USD", result.baseCurrency());
        assertEquals(new BigDecimal("10000.00"), result.availableBalance());
    }

    @Test
    void shouldRejectUpdateWithNullProfile() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> gateway.updateCustomerProfile(null)
        );

        assertEquals("profile must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectUpdateWithNullCustomerId() {
        final CustomerCoreProfile input = new CustomerCoreProfile(
                null,
                "John",
                "Doe",
                "RETAIL",
                "USD",
                new BigDecimal("10000.00")
        );

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> gateway.updateCustomerProfile(input)
        );

        assertEquals("customerId must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectUpdateNonExistentProfile() {
        final CustomerCoreProfile update = new CustomerCoreProfile(
                "CUST-MISSING",
                "John",
                "Doe",
                "RETAIL",
                "USD",
                new BigDecimal("10000.00")
        );

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> gateway.updateCustomerProfile(update)
        );

        assertEquals("Customer profile not found: CUST-MISSING", exception.getMessage());
    }
}
