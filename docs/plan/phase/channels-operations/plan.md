# Implementation Plan: Channels, Audit, Notification and Reporting

**Date**: 2026-11-22 | **Status**: Planned | **Spec**: [Feature spec](../../../specs/phase-7-channels-operations/feature-spec.md)

## Summary

Implement a bounded Phase 7 slice for channel-aware authorization, durable audit records, provider-neutral notification delivery state and scoped reporting projections. Internet Banking, Admin and Teller remain channel contexts; owning services enforce resource and branch scope. Broker, gateway and external notification provider choices remain deferred.

## Technical Context

**Language/Version**: Java 21
**Primary Dependencies**: Spring Boot 3.5, Spring Security OAuth2, JDBC/PostgreSQL, Flyway
**Storage**: `audit_schema`, `notification_schema` and projection/read model owned by each reporting producer or Reporting service
**Testing**: JUnit, Spring Boot MockMvc, PostgreSQL Testcontainers
**Target Platform**: Independent Spring Boot services and existing internal REST services
**Performance Goals**: Scope-constrained reads, bounded report ranges and idempotent event commands
**Constraints**:

- In-service authorization is mandatory; no API Gateway-only authorization.
- Audit, Notification and Reporting do not change financial outcome.
- Reporting never becomes the authoritative balance or Ledger source.
- At-least-once delivery must be safe through event-id deduplication.
- External provider/broker/gateway protocols are excluded until decisions are approved.

## Constitution Check

No repository constitution file exists. The plan follows the accepted architecture.

| Principle | Result | Plan alignment |
|---|---|---|
| Bounded-context ownership | PASS | Channel authorization stays in owning services; Audit/Notification/Reporting own their records |
| Schema isolation | PASS | Separate service schemas and no cross-service direct SQL access |
| Least privilege | PASS | Channel plus organization/account/branch/resource scope |
| Auditability | PASS | Durable record and deduplication identity |
| Financial correctness | PASS | Reports and notifications cannot post, reverse or alter financial state |
| Idempotency | PASS | Stable command/event identities and replayed read behavior |

## Applied Guidelines

- ADR-003 multi-channel scope.
- ADR-009 branch scope.
- ADR-012 REST and OAuth2 first-slice conventions.
- Existing API and event contract conventions.
- Transactional Outbox and consumer idempotency design from the event contracts.

## Implementation Steps

### Step 1: Scaffold channel and audit boundaries

- **Requirements**: REQ-001, REQ-004, REQ-006, REQ-007, REQ-008
- **Description**: Define a provider-neutral channel context, Audit submission/retrieval APIs and audit persistence with stable deduplication IDs.
- **Tasks**:
  - [x] T001 [Plan:1.1] Create the Phase 7 design skeleton in `docs/specs/phase-7-channels-operations/` and `docs/plan/phase/channels-operations/`.
  - [x] T002 [Plan:1.1] Add channel context and audit contracts to `docs/api/API-CONTRACTS.md`.
  - [x] T003 [Plan:1.1] Scaffold `services/audit-service/pom.xml`, application configuration and migrations for `audit_schema`.

### Step 2: Implement channel-aware authorization

- **Requirements**: REQ-001, REQ-002, REQ-003, REQ-004, REQ-005
- **Description**: Add reusable server-side channel/resource scope checks and apply them to Account, Transfer, Corporate and existing Admin/Teller routes.
- **Tasks**:
  - [ ] T004 [Plan:2.1] Add channel and scope types under `services/*/src/main/java/com/banking/platform/*/application/`.
  - [ ] T005 [Plan:2.1] Enforce Internet Banking account ownership in Account and Transfer endpoints.
  - [ ] T006 [Plan:2.1] Enforce Teller branch scope and explicit Admin permission on existing management endpoints.

### Step 3: Implement durable Audit

- **Requirements**: REQ-006, REQ-007, REQ-008
- **Description**: Implement Audit record ingestion, deduplication, redaction and scoped audit reads without gateway-only trust.
- **Tasks**:
  - [x] T007 [Plan:3.1] Implement Audit domain types and JDBC repositories under `services/audit-service/src/main/java/com/banking/platform/audit/`.
  - [x] T008 [Plan:3.1] Implement `POST /internal/audit/records` and `GET /v1/audit/records` in `services/audit-service/src/main/java/com/banking/platform/audit/web/`.
  - [ ] T009 [Plan:3.1] Add unit and PostgreSQL Testcontainers tests under `services/audit-service/src/test/java/com/banking/platform/audit/`.

### Step 4: Implement provider-neutral Notification

- **Requirements**: REQ-009, REQ-010, REQ-015
- **Description**: Add durable notification requests and delivery attempts with mock delivery and no financial side effects.
- **Tasks**:
  - [ ] T010 [Plan:4.1] Scaffold `services/notification-service/` with `notification_schema`.
  - [ ] T011 [Plan:4.1] Implement Notification and NotificationAttempt persistence under `services/notification-service/src/main/java/com/banking/platform/notification/`.
  - [ ] T012 [Plan:4.1] Implement provider-neutral delivery boundary and mock provider adapter.
  - [ ] T013 [Plan:4.1] Add duplicate notification and non-financial-side-effect tests under `services/notification-service/src/test/java/`.

### Step 5: Implement scoped Reporting projections

- **Requirements**: REQ-011, REQ-012, REQ-013
- **Description**: Add bounded reporting queries and export jobs backed by projections with source watermarks.
- **Tasks**:
  - [ ] T014 [Plan:5.1] Scaffold `services/reporting-service/` and reporting projection migrations.
  - [ ] T015 [Plan:5.1] Implement allowlisted report queries and organization/account/branch/channel scope checks.
  - [ ] T016 [Plan:5.1] Implement bounded export job deduplication and format validation.
  - [ ] T017 [Plan:5.1] Add Reporting tests proving projection lag is visible and balances remain Ledger-authoritative.

### Step 6: Wire cross-service evidence and verify

- **Requirements**: REQ-006, REQ-014, REQ-015
- **Description**: Add provider-neutral event consumers/outbox behavior and run workspace verification.
- **Tasks**:
  - [ ] T018 [Plan:6.1] Add stable event-id deduplication behavior to cross-service consumer boundaries.
  - [ ] T019 [Plan:6.1] Add smoke coverage for the Internet Banking, Teller, Admin, Audit, Notification and Reporting journeys.
  - [ ] T020 [Plan:6.1] Run `./mvnw verify` and record the Phase 7 smoke-test verdict.

## Project Structure

```text
docs/specs/phase-7-channels-operations/
docs/plan/phase/channels-operations/
services/audit-service/
services/notification-service/
services/reporting-service/
services/{account,corporate,transfer}/src/main/java/.../
```

## Testing Strategy

- **appType**: API-only Spring Boot services.
- **Critical user journeys**:
  1. Internet Banking user reads only owned resources.
  2. Teller operates only in assigned branch scope.
  3. Admin action requires explicit permission and produces audit evidence.
  4. Duplicate audit and notification commands are deduplicated.
  5. Reports expose only permitted projected rows and a source watermark.
- **primaryValidationStack**: Maven Wrapper, JUnit, Spring MockMvc, PostgreSQL Testcontainers.
- **fallbackMatrix**:
  - `infra-tier`: PostgreSQL Testcontainers; no substitute for schema ownership and migration acceptance.
  - `browser-tier`: Not applicable because no frontend is in scope.
- **Environment requirements**: Java 21 and Docker for Testcontainers.
- **knownGaps**: API Gateway, event broker, notification provider and frontend behavior remain unavailable.
- **Test data strategy**: UUID identities, synthetic actor/account/organization/branch data and isolated schemas.
- **Acceptance criteria**: Scope denial, one audit/notification side effect, non-authoritative reports and successful workspace verification.

## Deferred Decisions

- API Gateway technology.
- Kafka or RabbitMQ and broker-specific delivery semantics.
- Notification provider and channel.
- PII/retention policy and report retention.
- Frontend technology and channel-specific UI.

## Requirement Mapping

| REQ ID | Description | Plan Items | Implementation Evidence |
|--------|-------------|------------|------------------------|
| REQ-001 | Channel context for Internet Banking, Admin and Teller | P1.1, P2.1 | Channel types and channel-aware authorization tests |
| REQ-002 | Internet Banking account/organization ownership | P2.1 | Account and Transfer scope enforcement |
| REQ-003 | Teller branch and permission scope | P2.1 | Teller branch authorization tests |
| REQ-004 | Explicit Admin permission | P1.1, P2.1 | Admin authorization tests |
| REQ-005 | Owning-service authorization | P2.1 | Service-level scope guards |
| REQ-006 | Durable sensitive-operation audit | P1.1, P3.1, P6.1 | Audit persistence and tests |
| REQ-007 | Audit deduplication and redaction | P1.1, P3.1 | Audit repository and duplicate tests |
| REQ-008 | Scoped audit reads | P1.1, P3.1 | Audit read endpoint tests |
| REQ-009 | Durable deduplicated notifications | P4.1 | Notification persistence and replay tests |
| REQ-010 | Delivery attempt status without financial side effects | P4.1 | NotificationAttempt and non-financial tests |
| REQ-011 | Reporting is projection-only | P5.1 | Reporting projection and Ledger-authority tests |
| REQ-012 | Allowlisted scoped report filters | P5.1 | Report query validation and scope tests |
| REQ-013 | Bounded deduplicated exports | P5.1 | Export format/range and duplicate tests |
| REQ-014 | At-least-once consumer safety | P6.1 | Event-id deduplication tests |
| REQ-015 | Provider-neutral implementation boundaries | P4.1, P6.1 | Mock notification/delivery adapters and deferred transport |
