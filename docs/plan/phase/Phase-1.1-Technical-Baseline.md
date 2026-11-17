# Phase 1.1 — Technical Baseline

- **Status:** APPROVED — recorded in [ADR-010](../../adr/ADR-010-phase-1-technical-baseline.md).
- **Scope:** Decide only the minimum foundation needed for the Phase 1 service template. This is not authorization to create services or configuration.

## Current repository and environment

- The repository currently contains architecture/planning documents and no application source, build manifest, database setup or service implementation.
- Java 25 is available in the current environment; Maven and Gradle are not on `PATH`.
- Docker CLI is available. Docker daemon availability was not checked.
- These observations describe this workspace only; they do not determine the project's supported runtime.

## Recommended baseline

| Area | Baseline | Status / boundary |
|---|---|---|
| Language/runtime | Java 21 | ACCEPTED; minor/patch version not fixed |
| Framework | Spring Boot 3 | ACCEPTED; minor/patch version not fixed |
| Build | Maven with Maven Wrapper | ACCEPTED |
| Repository layout | Monorepo, one independently buildable/deployable service directory per service | ACCEPTED |
| Orchestration | Kubernetes for local development and production | ACCEPTED; distribution, provider, topology and rollout details remain OPEN |
| Database | Shared PostgreSQL, schema-per-service | ACCEPTED; PostgreSQL major version remains OPEN |
| Migration tool | Flyway, migrations owned and run by each service | ACCEPTED; this does not authorize a migration |
| Configuration | Externalized environment-based config; fail fast on missing required values; no committed credentials | PROPOSED baseline practice |
| Secrets | Inject locally through ignored environment files or shell environment; production secret store remains OPEN | PROPOSED for local only |
| Dependency/version policy | Pin wrapper and dependency versions; use Spring Boot dependency management if approved; update through reviewed changes | PROPOSED |
| Logging/diagnostics | Structured logs, health/readiness endpoints and correlation ID; avoid secrets and unnecessary PII | Required Phase 1 foundation; implementation libraries remain OPEN |
| Broker, gateway, identity provider, internal protocol | Defer until a phase requires them | OPEN; not needed to create an empty service skeleton |

## Approved repository layout

The approved monorepo approach can use a structure such as:

```text
services/
  <service-name>/
    src/
    pom.xml
platform/
  <shared-build-or-test-support-only-if-needed>/
```

Avoid shared domain models or a common runtime library that couples service releases. The exact Maven module strategy needs a small build proof; no code or directories are created by this decision record.

## Decision outcome

The project owner approved the baseline on 2026-11-17. See [ADR-010](../../adr/ADR-010-phase-1-technical-baseline.md) for the recorded decision and consequences.

PostgreSQL major version, Kubernetes distribution/provider/topology, broker, API Gateway, Identity Provider and REST-vs-gRPC remain intentionally unselected. This approval does not authorize creating a service, configuration or database migration.

## References

- [Phase 1 plan](./Phase-1-Technical-Foundation.md)
- [Phase roadmap](../Phases.md)
- [Architecture overview](../../architecture/BANKING_ARCHITECTURE.md)
- [Open decisions](../../architecture/OPEN-DECISIONS.md)
