# Quarkus API Template

Barebones Quarkus 3.34.1 template for Java 21 teams building REST and GraphQL microservices. The starter stays intentionally small, but it is shaped around a common financial-domain use case: orchestrating multiple downstream calls, mapping the results into a consumer-friendly contract, and returning that through a stable API.

## What is included

- Java 21
- Quarkus 3.34.1
- Maven
- REST endpoint support with Jackson
- GraphQL endpoint support with SmallRye GraphQL
- Health endpoint with SmallRye Health
- OpenTelemetry support
- Datadog APM support via OTLP export to a Datadog Agent
- Checkstyle
- Quarkus JUnit 5, REST Assured, and Mockito-based tests
- Dockerfile and Docker Compose for local development

## Template philosophy

This template is optimized for API orchestration services:

- Keep endpoint classes thin.
- Put downstream orchestration and mapping in an application service.
- Hide downstream concerns behind interfaces so teams can replace stubs with REST clients, messaging adapters, or SDK-backed integrations later.
- Prefer immutable DTOs and deterministic transformations.
- Add infrastructure only when the service actually needs it.

## Project layout

```text
src/main/java/com/example/api
├── application
├── downstream
│   └── stub
├── graphql
├── model
└── rest
```

- `application`: orchestration and mapping logic
- `downstream`: gateway interfaces and starter stub implementations
- `rest`: REST API surface
- `graphql`: GraphQL API surface
- `model`: shared API and downstream DTOs

## Endpoints

- REST: `GET /api/customers/{customerId}/profile`
- GraphQL: `POST /graphql`
- Health: `GET /q/health`
- GraphQL UI in dev/test: `GET /q/graphql-ui/`

Example REST call:

```bash
curl http://localhost:8080/api/customers/CUST-001/profile
```

Example GraphQL call:

```bash
curl http://localhost:8080/graphql \
  -H 'Content-Type: application/json' \
  -d '{
    "query": "query($customerId: String!) { customerProfile(customerId: $customerId) { customerId fullName segment baseCurrency availableBalance totalExposure exposures { productCode currency notional } } }",
    "variables": {
      "customerId": "CUST-001"
    }
  }'
```

## Running locally

### Prerequisites

- JDK 21 for local Maven builds
- Maven 3.9+
- Docker Desktop or compatible Docker runtime for container-based runs

### Dev mode

```bash
mvn quarkus:dev
```

### Verify

```bash
mvn clean verify
```

## Running with Docker Compose

Build and run the service only:

```bash
docker compose up --build
```

Run the service with Datadog Agent OTLP/APM intake enabled:

```bash
DD_API_KEY=your_api_key \
QUARKUS_OTEL_SDK_DISABLED=false \
docker compose --profile observability up --build
```

The application listens on `http://localhost:8080`.

## Observability

The template enables Quarkus OpenTelemetry support at build time and disables the SDK by default at runtime so local runs stay quiet until you opt in.

Key settings:

- `QUARKUS_OTEL_SDK_DISABLED=true` by default
- `QUARKUS_OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317` by default
- `DD_ENV`, `DD_SERVICE`, and `DD_VERSION` feed service metadata for Datadog/OpenTelemetry resource attributes

When the `observability` Compose profile is enabled:

- the app exports OTLP telemetry to the Datadog Agent on port `4317`
- the Datadog Agent exposes APM on `8126`
- the Datadog Agent exposes OTLP HTTP on `4318`

If your team prefers the Datadog Java tracer instead of OTLP export, keep this template structure and add the Java agent at runtime through `JAVA_OPTS_APPEND`.

## Next steps teams usually add

- downstream REST clients and retries
- request validation and domain-specific error handling
- authn/authz
- persistence or caching
- contract tests for downstream dependencies
- CI automation and deployment manifests

