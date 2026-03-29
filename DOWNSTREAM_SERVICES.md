# Downstream Service Integration Guide

This guide shows how to integrate downstream services using Quarkus REST and GraphQL clients.

## Architecture

The template uses a **Gateway pattern** to isolate downstream service integrations:

```
API Layer (REST/GraphQL)
    ↓
Application Layer (Service/Orchestration)
    ↓
Gateway Interface (CustomerCoreGateway, ExposureGateway)
    ↓
Gateway Implementation (Stub or REST/GraphQL client)
```

## Gateway Implementations

### Stub Implementations (Default)

Stub implementations provide in-memory data for local development and testing:

- `StubCustomerCoreGateway` - In-memory customer profile storage
- `StubExposureGateway` - Returns hardcoded exposure data

**Benefits:**
- No external dependencies for local development
- Fast test execution
- Predictable behavior

### REST Client Implementations

Declarative REST clients for calling downstream HTTP services:

- `RestCustomerCoreGateway` + `CustomerCoreRestClient`
- `RestExposureGateway` + `ExposureRestClient`

**Benefits:**
- Type-safe HTTP client generated at build time
- Automatic JSON serialization/deserialization
- Built-in resilience patterns (retry, timeout, circuit breaker)
- Integrated with Quarkus observability (tracing, metrics)

### GraphQL Client Implementations

For downstream GraphQL services (example in template):

- `CustomerCoreGraphQLClient` - GraphQL client with schema-based types

## Enabling REST Client Implementations

### Step 1: Configure the downstream service URL

Add to `application.properties`:

```properties
# Customer Core REST Client
customer-core.base-url=http://localhost:8081
%dev.customer-core.base-url=http://localhost:8081
%test.customer-core.base-url=http://localhost:8080

# Exposure Service REST Client
exposure-service.base-url=http://localhost:8082
%dev.exposure-service.base-url=http://localhost:8082
%test.exposure-service.base-url=http://localhost:8080
```

### Step 2: Switch gateway implementations

**Option A: Use `@AlternativePriority` (Recommended)**

In `StubCustomerCoreGateway.java`:
```java
@AlternativePriority(1)  // Lower priority - used as fallback
public class StubCustomerCoreGateway implements CustomerCoreGateway {
```

In `RestCustomerCoreGateway.java`:
```java
@AlternativePriority(0)  // Higher priority - used by default
public class RestCustomerCoreGateway implements CustomerCoreGateway {
```

**Option B: Use `@DefaultBean` for stub**

Keep stub as `@DefaultBean` and remove `@Alternative` from REST implementation:

```java
// StubCustomerCoreGateway.java
@DefaultBean  // Used when no other implementation is available
public class StubCustomerCoreGateway {

// RestCustomerCoreGateway.java
// No @Alternative annotation - becomes default when stub is not @DefaultBean
public class RestCustomerCoreGateway {
```

### Step 3: Test the integration

```bash
# Run with stub (default)
mvn quarkus:dev

# Run with REST client (after switching implementations)
mvn quarkus:dev
```

## Creating New REST Clients

### Step 1: Define the client interface

```java
@RegisterRestClient(configKey = "my-service")
@RegisterClientHeaders
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MyServiceRestClient {

    @GET
    @Path("/api/resource/{id}")
    MyResource getResource(@PathParam("id") String id);

    @POST
    @Path("/api/resource")
    MyResource createResource(MyResource resource);
}
```

### Step 2: Create the gateway implementation

```java
@ApplicationScoped
public class RestMyServiceGateway implements MyServiceGateway {

    private final MyServiceRestClient restClient;

    @Inject
    public RestMyServiceGateway(@RestClient MyServiceRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public MyResource fetchResource(String id) {
        try {
            return restClient.getResource(id);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new ResourceNotFoundException("Resource not found: " + id);
            }
            throw e;
        } catch (Exception e) {
            throw new WebApplicationException(
                "Error calling downstream service: " + e.getMessage(),
                Response.Status.BAD_GATEWAY
            );
        }
    }
}
```

### Step 3: Add configuration

```properties
# application.properties
my-service.base-url=http://localhost:9000
%dev.my-service.base-url=http://localhost:9000
%test.my-service.base-url=http://localhost:9000
```

## Advanced Configuration

### Timeouts

```properties
# Connection timeout (ms)
my-service/connect-timeout=5000
# Read timeout (ms)
my-service/read-timeout=10000
```

### Retry Policy

```properties
# Enable retry
my-service/mp-fault-tolerance/retry/enabled=true
# Max 3 attempts
my-service/mp-fault-tolerance/retry/max-retries=2
# Delay between retries (ms)
my-service/mp-fault-tolerance/retry/delay=1000
```

### Circuit Breaker

```properties
# Enable circuit breaker
my-service/mp-fault-tolerance/circuit-breaker/enabled=true
# Open circuit after 5 failures
my-service/mp-fault-tolerance/circuit-breaker/failure-ratio=5.0
# Close after 30 seconds
my-service/mp-fault-tolerance/circuit-breaker/delay=30000
```

### Custom Headers

Create a `ClientHeadersFactory`:

```java
@Provider
public class AuthHeadersFactory implements ClientHeadersFactory {

    @Override
    public MultivaluedMap<String, String> update(
            MultivaluedMap<String, String> incomingHeaders,
            MultivaluedMap<String, String> clientOutgoingHeaders) {
        
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        String authToken = getAuthToken(); // Your logic here
        result.add("Authorization", "Bearer " + authToken);
        return result;
    }
}
```

Add to client interface:
```java
@RegisterClientHeaders(AuthHeadersFactory.class)
public interface MyServiceRestClient {
```

## Error Handling Patterns

### Map HTTP errors to application exceptions

```java
try {
    return restClient.getResource(id);
} catch (WebApplicationException e) {
    switch (e.getResponse().getStatus()) {
        case 404:
            throw new ResourceNotFoundException("Resource not found: " + id);
        case 409:
            throw new ConflictException("Resource already exists: " + id);
        case 400:
            throw new InvalidRequestException("Invalid resource data");
        default:
            throw e;
    }
} catch (Exception e) {
    // Network errors, timeouts, etc.
    throw new WebApplicationException(
        "Service unavailable: " + e.getMessage(),
        Response.Status.BAD_GATEWAY
    );
}
```

## Testing REST Clients

### Unit test with Mock

```java
@QuarkusTest
class RestCustomerCoreGatewayTest {

    @InjectMock
    @RestClient
    CustomerCoreRestClient restClient;

    @Inject
    RestCustomerCoreGateway gateway;

    @Test
    void shouldFetchCustomerProfile() {
        CustomerCoreProfile mockProfile = new CustomerCoreProfile("C123", "John", "Doe", ...);
        Mockito.when(restClient.getCustomerProfile("C123")).thenReturn(mockProfile);

        CustomerCoreProfile result = gateway.fetchCustomerProfile("C123");

        assertEquals("C123", result.customerId());
    }
}
```

### Integration test with WireMock

```java
@QuarkusTest
@RegisterRestClients
class CustomerCoreRestClientIT {

    @ConfigProperty(name = "customer-core.base-url")
    String baseUrl;

    @Inject
    @RestClient
    CustomerCoreRestClient restClient;

    @Test
    void shouldFetchProfile(@WireMockRuntimeInfo WireMockRuntimeInfo wm) {
        stubFor(get("/api/customers/C123/profile")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"customerId\":\"C123\",\"givenName\":\"John\"}")));

        CustomerCoreProfile profile = restClient.getCustomerProfile("C123");

        assertEquals("John", profile.givenName());
    }
}
```

## Observability

REST clients automatically integrate with Quarkus observability:

- **Tracing**: Each HTTP call creates spans with trace context propagation
- **Metrics**: Request counts, latencies, and error rates
- **Logging**: Structured logs with trace/span IDs

View traces in Jaeger, Datadog, or your OTLP-compatible backend.

## References

- [Quarkus REST Client Guide](https://quarkus.io/guides/rest-client)
- [MicroProfile REST Client](https://microprofile.io/project/eclipse/microprofile-rest-client/)
- [Quarkus Fault Tolerance](https://quarkus.io/guides/smallrye-fault-tolerance)
