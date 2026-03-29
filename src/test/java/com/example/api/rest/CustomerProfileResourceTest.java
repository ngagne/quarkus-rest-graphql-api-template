package com.example.api.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.api.downstream.CustomerCoreGateway;
import com.example.api.downstream.ExposureGateway;
import com.example.api.model.CustomerCoreProfile;
import com.example.api.model.CustomerProfileView;
import com.example.api.model.ProductExposure;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CustomerProfileResourceTest {

    @InjectMock
    CustomerCoreGateway customerCoreGateway;

    @InjectMock
    ExposureGateway exposureGateway;

    @Test
    void shouldReturnAggregatedProfileOverRest() {
        when(customerCoreGateway.fetchCustomerProfile("CUST-REST")).thenReturn(new CustomerCoreProfile(
                "CUST-REST",
                "Alex",
                "Morgan",
                "WEALTH",
                "USD",
                new BigDecimal("750000.00")
        ));
        when(exposureGateway.fetchExposures("CUST-REST")).thenReturn(List.of(
                new ProductExposure("MBS", "USD", new BigDecimal("125000.00")),
                new ProductExposure("UST", "USD", new BigDecimal("225000.00"))
        ));

        final CustomerProfileView response = given()
                .pathParam("customerId", "CUST-REST")
                .when()
                .get("/v1/api/customers/{customerId}/profile")
                .then()
                .statusCode(200)
                .extract()
                .as(CustomerProfileView.class);

        assertAll(
                () -> assertEquals("CUST-REST", response.getCustomerId()),
                () -> assertEquals("Alex Morgan", response.getFullName()),
                () -> assertEquals("WEALTH", response.getSegment()),
                () -> assertEquals(new BigDecimal("750000.00"), response.getAvailableBalance()),
                () -> assertEquals(new BigDecimal("350000.00"), response.getTotalExposure())
        );
    }

    @Test
    void shouldRejectOverlongCustomerId() {
        final String customerId = "C".repeat(65);

        given()
                .pathParam("customerId", customerId)
                .when()
                .get("/v1/api/customers/{customerId}/profile")
                .then()
                .statusCode(400)
                .body("code", equalTo("VALIDATION_FAILED"))
                .body("message", equalTo("Request validation failed"))
                .body("violations.field", hasItem("customerId"))
                .body("violations.message", hasItem("size must be between 0 and 64"));
    }

    @Test
    void shouldCreateCustomerProfileOverRest() {
        when(customerCoreGateway.createCustomerProfile(any(CustomerCoreProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(customerCoreGateway.fetchCustomerProfile("CUST-NEW")).thenReturn(new CustomerCoreProfile(
                "CUST-NEW",
                "Jordan",
                "Smith",
                "RETAIL",
                "USD",
                new BigDecimal("25000.00")
        ));
        when(exposureGateway.fetchExposures("CUST-NEW")).thenReturn(List.of());

        final Map<String, Object> requestBody = Map.of(
                "customerId", "CUST-NEW",
                "givenName", "Jordan",
                "familyName", "Smith",
                "segment", "RETAIL",
                "baseCurrency", "USD",
                "availableBalance", 25000.00
        );

        final CustomerProfileView response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v1/api/customers/profile")
                .then()
                .statusCode(201)
                .extract()
                .as(CustomerProfileView.class);

        assertAll(
                () -> assertEquals("CUST-NEW", response.getCustomerId()),
                () -> assertEquals("Jordan Smith", response.getFullName()),
                () -> assertEquals("RETAIL", response.getSegment()),
                () -> assertEquals("USD", response.getBaseCurrency()),
                () -> assertEquals(new BigDecimal("25000.00"), response.getAvailableBalance())
        );
    }

    @Test
    void shouldUpdateCustomerProfileOverRest() {
        when(customerCoreGateway.fetchCustomerProfile("CUST-UPD")).thenReturn(new CustomerCoreProfile(
                "CUST-UPD",
                "Casey",
                "Brown",
                "WEALTH",
                "EUR",
                new BigDecimal("100000.00")
        ));
        when(customerCoreGateway.updateCustomerProfile(any(CustomerCoreProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(customerCoreGateway.fetchCustomerProfile("CUST-UPD")).thenReturn(new CustomerCoreProfile(
                "CUST-UPD",
                "Casey",
                "Brown",
                "WEALTH",
                "EUR",
                new BigDecimal("150000.00")
        ));
        when(exposureGateway.fetchExposures("CUST-UPD")).thenReturn(List.of());

        final Map<String, Object> requestBody = Map.of(
                "customerId", "CUST-UPD",
                "availableBalance", 150000.00
        );

        final CustomerProfileView response = given()
                .contentType(ContentType.JSON)
                .pathParam("customerId", "CUST-UPD")
                .body(requestBody)
                .when()
                .put("/v1/api/customers/{customerId}/profile")
                .then()
                .statusCode(200)
                .extract()
                .as(CustomerProfileView.class);

        assertAll(
                () -> assertEquals("CUST-UPD", response.getCustomerId()),
                () -> assertEquals("Casey Brown", response.getFullName()),
                () -> assertEquals("WEALTH", response.getSegment()),
                () -> assertEquals("EUR", response.getBaseCurrency()),
                () -> assertEquals(new BigDecimal("150000.00"), response.getAvailableBalance())
        );
    }
}
