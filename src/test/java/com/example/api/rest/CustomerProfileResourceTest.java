package com.example.api.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.api.downstream.CustomerCoreGateway;
import com.example.api.downstream.ExposureGateway;
import com.example.api.model.CustomerCoreProfile;
import com.example.api.model.CustomerProfileView;
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
                () -> assertEquals("CUST-REST", response.customerId()),
                () -> assertEquals("Alex Morgan", response.fullName()),
                () -> assertEquals("WEALTH", response.segment()),
                () -> assertEquals(new BigDecimal("750000.00"), response.availableBalance()),
                () -> assertEquals(new BigDecimal("350000.00"), response.totalExposure())
        );
    }
}

