package com.example.api.graphql;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.api.model.CustomerProfileView;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for Customer Profile GraphQL API.
 * These tests run against the packaged application using @QuarkusIntegrationTest.
 */
@QuarkusIntegrationTest
class CustomerProfileGraphQLIT {

    @Test
    void shouldReturnAggregatedProfileOverGraphql() {
        final String customerId = "CUST-GQL-IT-001";
        
        // First create the customer profile
        final String createMutation = "mutation($input: CreateCustomerProfileInput!) { "
                + "createCustomerProfile(input: $input) { customerId } }";
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", createMutation,
                        "variables", Map.of("input", Map.of(
                                "customerId", customerId,
                                "givenName", "John",
                                "familyName", "Doe",
                                "segment", "RETAIL",
                                "baseCurrency", "USD",
                                "availableBalance", 50000.00
                        ))
                ))
                .when()
                .post("/graphql")
                .then()
                .statusCode(200);

        // Then query the profile
        final String query = "query($customerId: String!) { "
                + "customerProfile(customerId: $customerId) { "
                + "customerId fullName segment baseCurrency availableBalance "
                + "totalExposure exposures { productCode currency notional } } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", query,
                        "variables", Map.of("customerId", customerId)
                ))
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        final CustomerProfileView response = jsonPath.getObject("data.customerProfile", CustomerProfileView.class);

        // StubExposureGateway returns hardcoded exposures: FX-FWD ($250k) + IRS ($800k) = $1,050,000
        assertAll(
                () -> assertEquals(customerId, response.getCustomerId()),
                () -> assertEquals("John Doe", response.getFullName()),
                () -> assertEquals("RETAIL", response.getSegment()),
                () -> assertEquals(0, new BigDecimal("50000.00").compareTo(response.getAvailableBalance())),
                () -> assertEquals(0, new BigDecimal("1050000.00").compareTo(response.getTotalExposure())),
                () -> assertEquals(2, response.getExposures().size())
        );
    }

    @Test
    void shouldCreateCustomerProfileOverGraphql() {
        final String mutation = "mutation($input: CreateCustomerProfileInput!) { "
                + "createCustomerProfile(input: $input) { "
                + "customerId fullName segment baseCurrency availableBalance "
                + "totalExposure exposures { productCode currency notional } } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", mutation,
                        "variables", Map.of("input", Map.of(
                                "customerId", "CUST-GQL-IT-NEW",
                                "givenName", "Alice",
                                "familyName", "Johnson",
                                "segment", "WEALTH",
                                "baseCurrency", "GBP",
                                "availableBalance", 250000.00
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
                () -> assertEquals("CUST-GQL-IT-NEW", response.getCustomerId()),
                () -> assertEquals("Alice Johnson", response.getFullName()),
                () -> assertEquals("WEALTH", response.getSegment()),
                () -> assertEquals("GBP", response.getBaseCurrency()),
                () -> assertEquals(0, new BigDecimal("250000.00").compareTo(response.getAvailableBalance()))
        );
    }

    @Test
    void shouldUpdateAvailableBalanceOverGraphql() {
        final String customerId = "CUST-GQL-IT-BAL";
        
        // First create the customer
        final String createMutation = "mutation($input: CreateCustomerProfileInput!) { "
                + "createCustomerProfile(input: $input) { customerId } }";
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", createMutation,
                        "variables", Map.of("input", Map.of(
                                "customerId", customerId,
                                "givenName", "Balance",
                                "familyName", "Test",
                                "segment", "RETAIL",
                                "baseCurrency", "USD",
                                "availableBalance", 100000.00
                        ))
                ))
                .when()
                .post("/graphql")
                .then()
                .statusCode(200);

        // Then update the balance
        final String updateMutation = "mutation($customerId: String!, $availableBalance: BigDecimal!) { "
                + "updateAvailableBalance(customerId: $customerId, availableBalance: $availableBalance) { "
                + "customerId fullName segment baseCurrency availableBalance } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", updateMutation,
                        "variables", Map.of(
                                "customerId", customerId,
                                "availableBalance", 150000.00
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
                () -> assertEquals(customerId, response.getCustomerId()),
                () -> assertEquals("Balance Test", response.getFullName()),
                () -> assertEquals("RETAIL", response.getSegment()),
                () -> assertEquals("USD", response.getBaseCurrency()),
                () -> assertEquals(0, new BigDecimal("150000.00").compareTo(response.getAvailableBalance()))
        );
    }

    @Test
    void shouldUpdateNameOverGraphql() {
        final String customerId = "CUST-GQL-IT-NAME";
        
        // First create the customer
        final String createMutation = "mutation($input: CreateCustomerProfileInput!) { "
                + "createCustomerProfile(input: $input) { customerId } }";
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", createMutation,
                        "variables", Map.of("input", Map.of(
                                "customerId", customerId,
                                "givenName", "Original",
                                "familyName", "Name",
                                "segment", "WEALTH",
                                "baseCurrency", "EUR",
                                "availableBalance", 50000.00
                        ))
                ))
                .when()
                .post("/graphql")
                .then()
                .statusCode(200);

        // Then update the name
        final String updateMutation = "mutation($customerId: String!, $givenName: String!, $familyName: String!) { "
                + "updateName(customerId: $customerId, givenName: $givenName, familyName: $familyName) { "
                + "customerId fullName segment baseCurrency availableBalance } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", updateMutation,
                        "variables", Map.of(
                                "customerId", customerId,
                                "givenName", "Updated",
                                "familyName", "Name"
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
                () -> assertEquals(customerId, response.getCustomerId()),
                () -> assertEquals("Updated Name", response.getFullName()),
                () -> assertEquals("WEALTH", response.getSegment()),
                () -> assertEquals("EUR", response.getBaseCurrency()),
                () -> assertEquals(0, new BigDecimal("50000.00").compareTo(response.getAvailableBalance()))
        );
    }

    @Test
    void shouldRejectCreateCustomerProfileWithBlankGivenName() {
        final String mutation = "mutation($input: CreateCustomerProfileInput!) { "
                + "createCustomerProfile(input: $input) { customerId } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", mutation,
                        "variables", Map.of(
                                "customerId", "CUST-BLANK-IT",
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
                                "customerId", "CUST-NEG-IT",
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
    void shouldRejectQueryWithNotFoundCustomer() {
        final String query = "query($customerId: String!) { "
                + "customerProfile(customerId: $customerId) { customerId } }";
        final JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "query", query,
                        "variables", Map.of("customerId", "NON-EXISTENT-IT")
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
