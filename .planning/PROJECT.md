# Project: Quarkus API Template

## What This Is

A minimal, production-minded Quarkus 3.34.1 template for Java 21 teams building REST and GraphQL microservices. This template provides a design-first API development approach with both REST and GraphQL endpoints sharing the same data models.

## What This Is Not

- Not a full-featured application with database, auth, or cloud integrations
- Not a code generator or scaffolding tool
- Not a domain-specific solution (intentionally generic for financial services)

## Core Value

Teams can fork this template and have a working API structure in minutes, with:
- Design-first contracts (OpenAPI + GraphQL schema as source of truth)
- Clean architecture layers (API → Application → Downstream)
- Observability built-in (OpenTelemetry + Datadog support)
- Test structure in place (REST Assured + Mockito)
- Docker Compose for local development

## Context

**Domain:** Financial services API orchestration

**Current State:** The template implements a customer profile orchestration service that:
- Exposes REST endpoints (`/api/customers/...`) for CRUD operations
- Exposes GraphQL endpoints for queries and mutations
- Orchestrates calls to downstream gateways (currently stub implementations)
- Maps between internal domain models and API DTOs

**Technology Stack:**
- Java 21
- Quarkus 3.34.1
- Maven
- SmallRye GraphQL
- OpenAPI / Swagger
- OpenTelemetry
- Docker Compose

**Architecture Pattern:** Gateway pattern with stub implementations for downstream integrations

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Design-first APIs | Contracts drive implementation, not vice versa | OpenAPI + GraphQL schema are source of truth |
| Shared data models | REST and GraphQL should use same DTOs | GraphQL schema generates models, OpenAPI reuses them |
| Stub downstream | Local dev shouldn't require real services | In-memory stubs replaceable with REST clients |
| No database | Template should stay minimal | Persistence added by teams as needed |
| Constructor injection | Clearer dependencies, easier testing | No field injection in template code |

## Requirements

### Validated

- ✓ Quarkus 3.34.1 with Java 21 — existing
- ✓ REST endpoints with OpenAPI spec — existing
- ✓ GraphQL endpoints with schema — existing
- ✓ Health endpoint — existing
- ✓ OpenTelemetry support — existing
- ✓ Docker Compose setup — existing
- ✓ Bruno collection for API testing — existing
- ✓ Checkstyle enforcement — existing

### Active

- [ ] Document template extension patterns for teams
- [ ] Add example of replacing stub with REST client
- [ ] Provide guidance on adding persistence when needed

### Out of Scope

- Database integration — template stays storage-agnostic
- Authentication/Authorization — teams add based on their needs
- Cloud-specific deployments — template is cloud-neutral
- Domain-specific business logic — intentionally generic

## Evolution

This document evolves at phase transitions and milestone boundaries.

**After each phase transition** (via `/gsd-transition`):
1. Requirements invalidated? → Move to Out of Scope with reason
2. Requirements validated? → Move to Validated with phase reference
3. New requirements emerged? → Add to Active
4. Decisions to log? → Add to Key Decisions
5. "What This Is" still accurate? → Update if drifted

**After each milestone** (via `/gsd-complete-milestone`):
1. Full review of all sections
2. Core Value check — still the right priority?
3. Audit Out of Scope — reasons still valid?
4. Update Context with current state

---
*Last updated: 2026-03-29 after initialization*
