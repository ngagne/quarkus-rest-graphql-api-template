package com.example.api.graphql;

import static io.restassured.RestAssured.given;
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
                () -> assertEquals("CUST-GQL", response.getCustomerId()),
                () -> assertEquals("Casey Nguyen", response.getFullName()),
                () -> assertEquals("PAYMENTS", response.getSegment()),
                () -> assertEquals(0, new BigDecimal("910000.00").compareTo(response.getAvailableBalance())),
                () -> assertEquals(0, new BigDecimal("50000.00").compareTo(response.getTotalExposure())),
                () -> assertEquals(2, response.getExposures().size())
        );
    }

    @Test
    void shouldCreateCustomerProfileOverGraphql() {
        when(customerCoreGateway.createCustomerProfile(any(CustomerCoreProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(customerCoreGateway.fetchCustomerProfile("CUST-GQL-NEW")).thenReturn(new CustomerCoreProfile(
                "CUST-GQL-NEW",
                "Riley",
                "Johnson",
                "RETAIL",
                "GBP",
                new BigDecimal("35000.00")
        ));
        when(exposureGateway.fetchExposures("CUST-GQL-NEW")).thenReturn(List.of());

        final String mutation = "mutation($input: CreateCustomerProfileInput!) { "
                + "createCustomerProfile(input: $input) { "
                + "customerId fullName segment baseCurrency availableBalance "
                + "totalExposure exposures { productCode currency notional } } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", mutation,
                        "variables", Map.of("input", Map.of(
                                "customerId", "CUST-GQL-NEW",
                                "givenName", "Riley",
                                "familyName", "Johnson",
                                "segment", "RETAIL",
                                "baseCurrency", "GBP",
                                "availableBalance", 35000.00
                        ))
                ))
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        final CustomerProfileView response = jsonPath.getObject("data.createCustomerProfile",
                CustomerProfileView.class);

        assertAll(
                () -> assertEquals("CUST-GQL-NEW", response.getCustomerId()),
                () -> assertEquals("Riley Johnson", response.getFullName()),
                () -> assertEquals("RETAIL", response.getSegment()),
                () -> assertEquals("GBP", response.getBaseCurrency()),
                () -> assertEquals(0, new BigDecimal("35000.00").compareTo(response.getAvailableBalance()))
        );
    }

    @Test
    void shouldUpdateCustomerProfileOverGraphql() {
        when(customerCoreGateway.fetchCustomerProfile("CUST-GQL-UPD")).thenReturn(new CustomerCoreProfile(
                "CUST-GQL-UPD",
                "Sam",
                "Wilson",
                "WEALTH",
                "USD",
                new BigDecimal("200000.00")
        ));
        when(customerCoreGateway.updateCustomerProfile(any(CustomerCoreProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(customerCoreGateway.fetchCustomerProfile("CUST-GQL-UPD")).thenReturn(new CustomerCoreProfile(
                "CUST-GQL-UPD",
                "Sam",
                "Wilson",
                "WEALTH",
                "USD",
                new BigDecimal("250000.00")
        ));
        when(exposureGateway.fetchExposures("CUST-GQL-UPD")).thenReturn(List.of());

        final String mutation = "mutation($input: UpdateCustomerProfileInput!) { "
                + "updateCustomerProfile(input: $input) { "
                + "customerId fullName segment baseCurrency availableBalance "
                + "totalExposure exposures { productCode currency notional } } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", mutation,
                        "variables", Map.of("input", Map.of(
                                "customerId", "CUST-GQL-UPD",
                                "availableBalance", 250000.00
                        ))
                ))
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        final CustomerProfileView response = jsonPath.getObject("data.updateCustomerProfile",
                CustomerProfileView.class);

        assertAll(
                () -> assertEquals("CUST-GQL-UPD", response.getCustomerId()),
                () -> assertEquals("Sam Wilson", response.getFullName()),
                () -> assertEquals("WEALTH", response.getSegment()),
                () -> assertEquals("USD", response.getBaseCurrency()),
                () -> assertEquals(0, new BigDecimal("250000.00").compareTo(response.getAvailableBalance()))
        );
    }
}
