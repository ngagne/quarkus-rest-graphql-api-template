package com.example.api.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.api.downstream.CustomerCoreGateway;
import com.example.api.downstream.ExposureGateway;
import com.example.api.model.CustomerCoreProfile;
import com.example.api.graphql.generated.CustomerProfileView;
import com.example.api.model.ProductExposure;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import java.math.BigDecimal;
import java.util.List;
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
                .get("/api/customers/{customerId}/profile")
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
                .get("/api/customers/{customerId}/profile")
                .then()
                .statusCode(400)
                .body("code", equalTo("VALIDATION_FAILED"))
                .body("message", equalTo("Request validation failed"))
                .body("violations.field", hasItem("customerId"))
                .body("violations.message", hasItem("size must be between 0 and 64"));
    }
}
