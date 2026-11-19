# ADR-010: Phase 1 Technical Baseline

- **Status:** ACCEPTED
- **Date:** 2026-11-17
- **Amendment:** 2026-11-17 — Kubernetes approved for local and production environments.

## Context

Phase 1 needs a repeatable service foundation. The architecture previously listed Java 21, Spring Boot 3 and Flyway as proposals, while build tool, repository layout and local runtime were open.

## Decision

For the initial technical foundation:

- Use Java 21 and Spring Boot 3. Minor/patch versions are not fixed by this ADR.
- Use Maven Wrapper for repeatable builds.
- Keep services in a monorepo, with a separate, independently buildable/deployable directory for each service.
- Use Kubernetes as the orchestration platform for both local development and production.
- Use Flyway for service-owned database migrations.
- Keep the accepted shared PostgreSQL, schema-per-service ownership rule. Each service writes only to its own schema; no cross-schema foreign keys or direct cross-service table access.

## Alternatives

- Gradle Wrapper.
- Separate repository per service.
- Docker Compose or another non-Kubernetes local runtime.
- A different production orchestration platform.
- A different migration tool.

## Consequences

- Build setup and the service template should use Java 21, Spring Boot 3 and Maven Wrapper.
- Monorepo structure must preserve independent service ownership and deployment; avoid shared domain models or coupling releases through common runtime code.
- Kubernetes is selected for local and production orchestration. Local development uses Minikube on Ubuntu 24.04 per [ADR-011](./ADR-011-phase-based-secret-management.md); production provider, cluster topology and rollout process remain open.
- Flyway is selected as the migration tool, but this ADR does not authorize or define any database migration.
- Exact Java/Spring Boot patch versions, PostgreSQL major version, directory names and dependency policy details remain to be pinned during implementation planning.

## Dependencies

- ADR-001 establishes microservices architecture.
- ADR-004 establishes shared PostgreSQL with schema-per-service.
- [Phase 1 plan](../plan/phase/Phase-1-Technical-Foundation.md) guides implementation.
- Production Kubernetes provider/topology and rollout details, broker and API Gateway remain OPEN. Keycloak/OIDC and REST for synchronous service calls in the initial Phase 2 slice are accepted by [ADR-012](./ADR-012-phase-2-identity-party-corporate-branch.md). The local Kubernetes distribution and phase-based secret-management direction are recorded in [ADR-011](./ADR-011-phase-based-secret-management.md).
