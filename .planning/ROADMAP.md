# Roadmap

**Project:** Quarkus API Template  
**Milestone:** v1.0.0 — Template Foundation  
**Status:** Ready for planning

---

## Phase 1: Template Documentation & Extension Guide

**Goal:** Document how teams should extend this template with real integrations

**Scope:**
- Add examples of replacing stub gateways with REST clients
- Document persistence options (JPA, Panache, reactive)
- Add guidance on auth integration patterns
- Create "Getting Started" guide for template users

**Exit Criteria:**
- README includes extension patterns section
- Example REST client implementation provided
- Persistence guide covers common scenarios
- Checkstyle and tests pass

---

## Phase 2: Enhanced Error Handling

**Goal:** Improve error responses and validation patterns

**Scope:**
- Standardize error response format across REST and GraphQL
- Add domain-specific exception types
- Improve validation error messages
- Add error handling examples to documentation

**Exit Criteria:**
- Consistent error payload structure
- GraphQL errors mapped to client-friendly format
- Validation examples in tests
- Documentation updated

---

## Phase 3: Testing Improvements

**Goal:** Strengthen test coverage and patterns

**Scope:**
- Add integration test examples with Testcontainers
- Improve mocking patterns for downstream services
- Add contract testing examples
- Enhance CI test automation

**Exit Criteria:**
- Testcontainers example for database testing
- Contract test structure in place
- Test coverage documentation
- All existing tests passing

---

## Milestone Completion Criteria

- [ ] All phases executed and verified
- [ ] Template remains under 5000 lines of code (excluding generated)
- [ ] Build time under 2 minutes
- [ ] Documentation complete and clear
- [ ] Bruno collection covers all endpoints

---

## Evolution

This roadmap evolves as the project progresses.

**After each phase:**
1. Move completed phases to "Completed Phases"
2. Update phase status
3. Add new phases if scope expands
4. Adjust milestone criteria if needed

---
*Last updated: 2026-03-29 — Initial roadmap created*
