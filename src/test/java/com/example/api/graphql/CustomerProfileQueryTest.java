package com.example.api.graphql;

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
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CustomerProfileQueryTest {

    @InjectMock
    CustomerCoreGateway customerCoreGateway;

    @InjectMock
    ExposureGateway exposureGateway;

    @Test
    void shouldReturnAggregatedProfileOverGraphql() {
        when(customerCoreGateway.fetchCustomerProfile("CUST-GQL")).thenReturn(new CustomerCoreProfile(
                "CUST-GQL",
                "Casey",
                "Nguyen",
                "PAYMENTS",
                "USD",
                new BigDecimal("910000.00")
        ));
        when(exposureGateway.fetchExposures("CUST-GQL")).thenReturn(List.of(
                new ProductExposure("SEPA", "USD", new BigDecimal("40000.00")),
                new ProductExposure("ACH", "USD", new BigDecimal("10000.00"))
        ));

        final String query = "query($customerId: String!) { "
                + "customerProfile(customerId: $customerId) { "
                + "customerId fullName segment baseCurrency availableBalance "
                + "totalExposure exposures { productCode currency notional } } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", query,
                        "variables", Map.of("customerId", "CUST-GQL")
                ))
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        final CustomerProfileView response = jsonPath.getObject("data.customerProfile", CustomerProfileView.class);

        assertAll(
                () -> assertEquals("CUST-GQL", response.customerId()),
                () -> assertEquals("Casey Nguyen", response.fullName()),
                () -> assertEquals("PAYMENTS", response.segment()),
                () -> assertEquals(0, new BigDecimal("910000.00").compareTo(response.availableBalance())),
                () -> assertEquals(0, new BigDecimal("50000.00").compareTo(response.totalExposure())),
                () -> assertEquals(2, response.exposures().size())
        );
    }
}
