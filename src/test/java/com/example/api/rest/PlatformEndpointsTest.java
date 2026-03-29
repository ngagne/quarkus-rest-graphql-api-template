package com.example.api.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PlatformEndpointsTest {

    @Test
    void shouldExposeHealthEndpoint() {
        given()
                .when()
                .get("/q/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    void shouldExposeOpenApiDocument() {
        given()
                .when()
                .get("/q/openapi")
                .then()
                .statusCode(200)
                .body(containsString("/v1/api/customers/{customerId}/profile"));
    }
}
