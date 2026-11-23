# Implementation Plan: Production Readiness

**Date**: 2026-11-22 | **Status**: In Progress | **Spec**: [Feature spec](../../../specs/phase-8-production-readiness/feature-spec.md)

## Summary

Create a provider-neutral Phase 8 baseline that verifies release quality, deployment readiness, operational telemetry, recovery procedures and controlled rollout/rollback. Exact production platform values remain OPEN and are represented as configuration gates rather than assumptions.

## Technical Context

**Language/Version**: Java 21
**Primary Dependencies**: Spring Boot 3.5, Maven Wrapper, Kubernetes, Docker, PostgreSQL, Flyway
**Storage**: Service-owned schemas and externally managed backup/restore procedures
**Testing**: JUnit, Spring Boot, PostgreSQL Testcontainers, Maven verify
**Target Platform**: Kubernetes for local and production orchestration
**Performance Goals**: Replay-safe concurrency and bounded recovery behavior
**Constraints**:

- Kubernetes provider and external secret provider remain OPEN.
- No secret values in source-controlled manifests.
- Independent service deployment must preserve schema ownership.
- Ledger and payment financial outcomes remain authoritative domain boundaries.
- Audit and telemetry must not expose credentials or protected PII.

## Constitution Check

No repository constitution exists. The plan follows accepted architecture and security principles.

| Principle | Result | Plan alignment |
|---|---|---|
| Independent deployability | PASS | Provider-neutral Kubernetes manifests and service-level rollout |
| Schema isolation | PASS | Deployment/backup procedures preserve Flyway-owned schemas |
| Least privilege | PASS | Workload identity, RBAC and secret access are explicit gates |
| Auditability | PASS | Release evidence and sensitive-operation review remain traceable |
| Replay safety | PASS | Concurrency/recovery tests target duplicate financial side effects |
| Financial correctness | PASS | Production readiness cannot bypass Ledger or payment outcomes |

## Applied Guidelines

- ADR-010: Kubernetes orchestration for local and production.
- ADR-011: Phase-based secret management and environment-variable injection.
- Existing CI, Docker and actuator conventions.
- Existing Maven reactor and Testcontainers test strategy.

## Implementation Steps

### Step 1: Release and CI gate

- **Requirements**: REQ-001, REQ-005, REQ-012
- **Description**: Establish one workspace release command, per-service CI evidence and a go/no-go gate.
- **Tasks**:
  - [x] T001 [Plan:1.1] Add a reporting job for workspace verify and per-service test counts in `.github/workflows/ci.yml`.
  - [x] T002 [Plan:1.1] Verify Maven dependency resolution and build images for services with Dockerfiles in `.github/workflows/ci.yml`.
  - [x] T003 [Plan:1.1] Create `docs/plan/phase/production-readiness/checkpoints/RELEASE_CHECKLIST.md` with owner approval, security and test gates.

### Step 2: Deployment scaffolding

- **Requirements**: REQ-002, REQ-003, REQ-011
- **Description**: Add provider-neutral Kubernetes manifests and health probes for every deployable service.
- **Tasks**:
  - [x] T004 [Plan:2.1] Define a deployment manifest catalog under `deploy/kubernetes/`.
  - [x] T005 [P] [Plan:2.1] Add liveness/readiness probe configuration to all deployable Spring Boot service application settings.
  - [x] T006 [P] [Plan:2.1] Add independent local Deployment and Service manifests for each deployable service in `deploy/kubernetes/local/`; defer production overlay pending registry/provider decisions.
  - [x] T007 [Plan:2.1] Document required secret references without embedding secret values in `deploy/kubernetes/README.md`.

### Step 3: Secret and security baseline

- **Requirements**: REQ-004, REQ-006
- **Description**: Verify environment-only secret consumption and redaction controls.
- **Tasks**:
  - [x] T008 [Plan:3.1] Add secret configuration validation to every service that consumes credentials.
  - [x] T009 [P] [Plan:3.1] Add a CI secret-detection check that excludes no production secret value.
  - [x] T010 [P] [Plan:3.1] Add log/redaction tests proving tokens and passwords are not emitted.
  - [x] T011 [Plan:3.1] Document external secret-store and workload-identity decisions as OPEN in `docs/architecture/OPEN-DECISIONS.md`.

### Step 4: Operational evidence

- **Requirements**: REQ-007, REQ-010
- **Description**: Define observability, SLO, alert and outbox operational behavior.
- **Tasks**:
  - [x] T012 [Plan:4.1] Add structured health and metrics verification to the workspace smoke workflow.
  - [x] T013 [Plan:4.1] Create `docs/operations/` runbooks for alert response and service recovery.
  - [x] T014 [Plan:4.1] Create an outbox backlog, retry and controlled replay runbook.
  - [x] T015 [Plan:4.1] Record SLO/alert/retention targets as approved-owner inputs rather than defaults.

### Step 5: Recovery and replay verification

- **Requirements**: REQ-008, REQ-009
- **Description**: Add recovery rehearsals and replay/concurrency acceptance evidence.
- **Tasks**:
  - [x] T016 [Plan:5.1] Add a backup/restore rehearsal procedure under `docs/operations/`.
  - [x] T017 [P] [Plan:5.1] Add a rollback rehearsal procedure for independent service deployments.
  - [x] T018 [Plan:5.1] Add concurrency/replay tests that prove one financial side effect per stable event or command identity.
  - [x] T019 [P] [Plan:5.1] Add disaster-recovery and dependency-failure smoke scenarios to the test plan.

### Step 6: Final release evidence

- **Requirements**: REQ-001, REQ-012
- **Description**: Execute final workspace verification and record the Phase 8 release verdict.
- **Tasks**:
  - [x] T020 [Plan:6.1] Run the full workspace Maven verify and record module counts.
  - [x] T021 [Plan:6.1] Run or document security, recovery and rollback rehearsals.
  - [x] T022 [Plan:6.1] Create `docs/plan/phase/production-readiness/checkpoints/SMOKE_TEST_VERDICT.md`.
  - [ ] T023 [Plan:6.1] Complete `RELEASE_CHECKLIST.md` and mark Phase 8 complete only after owner approval.

## Project Structure

```text
.github/workflows/
deploy/kubernetes/
docs/operations/
docs/specs/phase-8-production-readiness/
docs/plan/phase/production-readiness/
services/*/src/main/resources/application.yml
```

## Testing Strategy

- **appType**: API-only Spring Boot microservices plus Kubernetes operational runtime.
- **Critical user journeys**:
  1. CI rejects an unsafe secret or failing workspace build.
  2. A service becomes ready only after required configuration and health dependencies pass.
  3. Duplicate replay produces no duplicate financial side effect.
  4. Rollback restores the prior deployable service without cross-service schema bypass.
  5. Restore/recovery preserves financial and audit data consistency.
- **primaryValidationStack**: Maven Wrapper, JUnit, Spring Boot Actuator, PostgreSQL Testcontainers, Docker/Kubernetes checks.
- **fallbackMatrix**:
  - `infra-tier`: PostgreSQL Testcontainers with Docker → local PostgreSQL or isolated schema fallback.
  - `browser-tier`: Not applicable.
- **Environment requirements**: Java 21, Maven Wrapper, Docker, optional Minikube/Kubernetes and PostgreSQL.
- **knownGaps**: Live cloud provider, external secret store and real event broker are not exercised until decisions are approved.
- **Test data strategy**: Isolated schemas, UUID identities and synthetic actors/resources.
- **Acceptance criteria**: 101 existing workspace tests plus all newly added production-readiness tests pass; no critical security finding remains.

## Deferred Decisions

- Production Kubernetes provider and topology.
- External secret store and workload identity.
- API Gateway and event broker.
- External interbank provider and reconciliation.
- SLO, alert, backup/restore and production retention targets.

## Requirement Mapping

| REQ ID | Description | Plan Items | Implementation Evidence |
|--------|-------------|------------|------------------------|
| REQ-001 | Workspace verification gate | P1.1, P6.1 | CI workflow, smoke verdict |
| REQ-002 | Safe Kubernetes manifests | P2.1 | Deployment manifest catalog |
| REQ-003 | Liveness/readiness probes | P2.1 | Actuator configuration and probes |
| REQ-004 | Environment/secret injection | P3.1 | Externalized configuration and secret references |
| REQ-005 | Dependency/container CI checks | P1.1 | CI workflow |
| REQ-006 | Secret-safe audit/logging | P3.1 | Redaction tests and log review |
| REQ-007 | SLO/alert/runbook | P4.1 | Operations runbooks |
| REQ-008 | Backup/restore/rollback evidence | P5.1 | Recovery rehearsal evidence |
| REQ-009 | Replay/concurrency safety | P5.1 | Idempotency/replay tests |
| REQ-010 | Outbox operational visibility | P4.1 | Outbox runbook and backlog checks |
| REQ-011 | Independent rollout/rollback | P2.1, P5.1 | Kubernetes service manifests |
| REQ-012 | Release checklist and approval | P1.1, P6.1 | Release checklist and verdict |
