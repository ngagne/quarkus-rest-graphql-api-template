package com.example.api.graphql;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void shouldUpdateAvailableBalanceOverGraphql() {
        when(customerCoreGateway.fetchCustomerProfile("CUST-GQL-BAL")).thenReturn(new CustomerCoreProfile(
                "CUST-GQL-BAL",
                "Sam",
                "Wilson",
                "WEALTH",
                "USD",
                new BigDecimal("200000.00")
        ));
        when(customerCoreGateway.updateCustomerProfile(any(CustomerCoreProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(customerCoreGateway.fetchCustomerProfile("CUST-GQL-BAL")).thenReturn(new CustomerCoreProfile(
                "CUST-GQL-BAL",
                "Sam",
                "Wilson",
                "WEALTH",
                "USD",
                new BigDecimal("250000.00")
        ));
        when(exposureGateway.fetchExposures("CUST-GQL-BAL")).thenReturn(List.of());

        final String mutation = "mutation($customerId: String!, $availableBalance: BigDecimal!) { "
                + "updateAvailableBalance(customerId: $customerId, availableBalance: $availableBalance) { "
                + "customerId fullName segment baseCurrency availableBalance "
                + "totalExposure exposures { productCode currency notional } } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", mutation,
                        "variables", Map.of(
                                "customerId", "CUST-GQL-BAL",
                                "availableBalance", 250000.00
                        )
                ))
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        final CustomerProfileView response = jsonPath.getObject("data.updateAvailableBalance",
                CustomerProfileView.class);

        assertAll(
                () -> assertEquals("CUST-GQL-BAL", response.getCustomerId()),
                () -> assertEquals("Sam Wilson", response.getFullName()),
                () -> assertEquals("WEALTH", response.getSegment()),
                () -> assertEquals("USD", response.getBaseCurrency()),
                () -> assertEquals(0, new BigDecimal("250000.00").compareTo(response.getAvailableBalance()))
        );
    }

    @Test
    void shouldUpdateNameOverGraphql() {
        when(customerCoreGateway.fetchCustomerProfile("CUST-GQL-NAME")).thenReturn(new CustomerCoreProfile(
                "CUST-GQL-NAME",
                "Sam",
                "Wilson",
                "WEALTH",
                "USD",
                new BigDecimal("200000.00")
        ));
        when(customerCoreGateway.updateCustomerProfile(any(CustomerCoreProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(customerCoreGateway.fetchCustomerProfile("CUST-GQL-NAME")).thenReturn(new CustomerCoreProfile(
                "CUST-GQL-NAME",
                "Sam",
                "Wilson-Chen",
                "WEALTH",
                "USD",
                new BigDecimal("200000.00")
        ));
        when(exposureGateway.fetchExposures("CUST-GQL-NAME")).thenReturn(List.of());

        final String mutation = "mutation($customerId: String!, $givenName: String!, $familyName: String!) { "
                + "updateName(customerId: $customerId, givenName: $givenName, familyName: $familyName) { "
                + "customerId fullName segment baseCurrency availableBalance "
                + "totalExposure exposures { productCode currency notional } } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", mutation,
                        "variables", Map.of(
                                "customerId", "CUST-GQL-NAME",
                                "givenName", "Sam",
                                "familyName", "Wilson-Chen"
                        )
                ))
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        final CustomerProfileView response = jsonPath.getObject("data.updateName",
                CustomerProfileView.class);

        assertAll(
                () -> assertEquals("CUST-GQL-NAME", response.getCustomerId()),
                () -> assertEquals("Sam Wilson-Chen", response.getFullName()),
                () -> assertEquals("WEALTH", response.getSegment()),
                () -> assertEquals("USD", response.getBaseCurrency()),
                () -> assertEquals(0, new BigDecimal("200000.00").compareTo(response.getAvailableBalance()))
        );
    }

    @Test
    void shouldRejectCreateCustomerProfileWithBlankGivenName() {
        // GraphQL validates non-null fields at the variable coercion level
        final String mutation = "mutation($input: CreateCustomerProfileInput!) { "
                + "createCustomerProfile(input: $input) { customerId } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", mutation,
                        "variables", Map.of(
                                "customerId", "CUST-BLANK",
                                "givenName", "",
                                "familyName", "Test",
                                "segment", "RETAIL",
                                "baseCurrency", "USD",
                                "availableBalance", 1000.00
                        )
                ))
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        final List<Map<String, Object>> errors = jsonPath.getList("errors");
        assertNotNull(errors);
        assertTrue(errors.size() > 0, "Expected errors in response for blank givenName");
    }

    @Test
    void shouldRejectCreateCustomerProfileWithNegativeBalance() {
        final String mutation = "mutation($input: CreateCustomerProfileInput!) { "
                + "createCustomerProfile(input: $input) { customerId } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", mutation,
                        "variables", Map.of(
                                "customerId", "CUST-NEG",
                                "givenName", "Test",
                                "familyName", "User",
                                "segment", "RETAIL",
                                "baseCurrency", "USD",
                                "availableBalance", -100.00
                        )
                ))
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        final List<Map<String, Object>> errors = jsonPath.getList("errors");
        assertNotNull(errors);
        assertTrue(errors.size() > 0, "Expected errors in response for negative balance");
    }

    @Test
    void shouldRejectUpdateNameWithBlankCustomerId() {
        final String mutation = "mutation($customerId: String!, $givenName: String!, "
                + "$familyName: String!) { "
                + "updateName(customerId: $customerId, givenName: $givenName, "
                + "familyName: $familyName) { customerId } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", mutation,
                        "variables", Map.of(
                                "customerId", "",
                                "givenName", "Test",
                                "familyName", "User"
                        )
                ))
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        final List<Map<String, Object>> errors = jsonPath.getList("errors");
        assertNotNull(errors);
        assertTrue(errors.size() > 0, "Expected errors in response for blank customerId");
    }

    @Test
    void shouldRejectUpdateBalanceWithNotFoundCustomer() {
        when(customerCoreGateway.fetchCustomerProfile("NON-EXISTENT")).thenReturn(null);

        final String mutation = "mutation($customerId: String!, "
                + "$availableBalance: BigDecimal!) { "
                + "updateAvailableBalance(customerId: $customerId, "
                + "availableBalance: $availableBalance) { customerId } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", mutation,
                        "variables", Map.of(
                                "customerId", "NON-EXISTENT",
                                "availableBalance", 50000.00
                        )
                ))
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        final List<Map<String, Object>> errors = jsonPath.getList("errors");
        assertNotNull(errors);
        assertTrue(errors.size() > 0, "Expected errors in response for non-existent customer");
    }
}
