# AGENTS.md

## Purpose

This repository is a minimal Quarkus API template. Keep it small, production-minded, and easy for teams to adapt across different financial-service domains.

## Working agreements

- Use Java 21 and Quarkus 3.34.1 unless the user explicitly asks for an upgrade.
- Prefer constructor injection over field injection.
- Keep REST and GraphQL entrypoints thin.
- Put orchestration and mapping logic in `application`.
- Keep downstream integrations behind interfaces in `downstream`.
- Favor immutable DTOs and predictable transformations.
- Do not add databases, auth, messaging, or cloud-specific tooling unless requested.

## API design

The project uses a design-first approach for both REST and GraphQL APIs:

- GraphQL schema is the source of truth for data models (`src/main/resources/graphql/schema.graphql`)
- OpenAPI spec defines REST endpoints (`src/main/resources/openapi/openapi.yaml`)
- Both API flavors share the same data models (generated from GraphQL schema)
- REST-specific request/response DTOs are created manually when needed

### Current API operations

**Customer Profile:**
- REST: `GET /api/customers/{customerId}/profile` - Retrieve profile
- REST: `POST /api/customers/profile` - Create profile
- REST: `PUT /api/customers/{customerId}/profile` - Update profile
- GraphQL: `customerProfile(customerId: String!)` - Query
- GraphQL: `createCustomerProfile(input: CreateCustomerProfileInput!)` - Mutation
- GraphQL: `updateAvailableBalance(customerId: String!, availableBalance: BigDecimal!)` - Mutation
- GraphQL: `updateName(customerId: String!, givenName: String!, familyName: String!)` - Mutation

## Testing expectations

- Keep endpoint coverage with `@QuarkusTest` and REST Assured.
- Keep orchestration logic easy to unit test with Mockito.
- Run Checkstyle before considering work complete.

## Local commands

```bash
mvn clean verify
mvn quarkus:dev
docker compose up --build
DD_API_KEY=your_api_key QUARKUS_OTEL_SDK_DISABLED=false docker compose --profile observability up --build
```

## Observability notes

- OpenTelemetry is the primary in-app tracing path.
- Datadog support is provided through OTLP export to a Datadog Agent.
- Keep trace/log correlation intact when changing logging config.

