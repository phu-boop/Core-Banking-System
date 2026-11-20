# Implementation Plan: Corporate Approval Core

**Date**: 2026-11-20 | **Status**: Draft for project-owner review
**Source baseline**: [ADR-007](../../../adr/ADR-007-configurable-corporate-approval.md), [Business Rules](../../../domain/BUSINESS-RULES.md), [Phase 2 Decision Gate](../Phase-2-Decision-Gate.md), [API Contracts](../../../api/API-CONTRACTS.md), [Logical ERD](../../../database/ERD.md)

## Summary

Plan a Corporate-owned approval capability that supports configurable multi-level approval policies, Maker–Checker, and durable approval history. The first implementation scope is the Corporate policy and approval workflow only. Corporate Membership and Mandate are not implemented yet; their approved data/authorization contracts and owner-side interfaces are prerequisites for approver eligibility and must be delivered before the dependent workflow tasks. Transfer execution, Ledger posting, and payment settlement are excluded; the later Transfer owner must enforce the approval outcome before executing a financial operation.

Proposed default: bind each pending approval request to an immutable policy version selected at submission. This is a recommendation, not yet a recorded ADR decision; formal owner approval is required before schema/API implementation. Rejection, expiry, delegation, resubmission, policy activation/editing, and step-assignment semantics remain explicit decision gates; implementation must not infer them.

This repository does not yet contain a standalone Approval feature specification or project constitution. The `REQ-APR-*` IDs below are plan-local trace labels derived only from accepted requirements in ADR-007 and `BUSINESS-RULES.md`; they are not a substitute for an approved feature spec. Resolve this documentation gap before implementation approval.

## Technical Context

**Language/Version**: Java 21
**Primary Dependencies**: Spring Boot 3.5, Maven Wrapper, Spring Security OAuth2 Resource Server
**Storage**: PostgreSQL, Corporate-owned `corporate_schema`, Flyway migrations
**Testing**: JUnit/Spring Boot tests and PostgreSQL Testcontainers, following the existing Corporate service
**Target Platform**: Existing Corporate service
**Constraints**:

- Corporate owns approval policies, requests, steps, and history. No cross-schema foreign keys or direct reads/writes to Transfer, Identity, Party, or Membership tables.
- Use owner APIs or validated principal context for external references. Do not trust caller-supplied actor, organization, role, or grant values.
- Approval decisions are authorization decisions only; they do not post to Ledger or settle a transfer.
- Enforce Maker–Checker at the Corporate approval boundary; the maker cannot approve their own request.
- Capture actor, action, resource, result, timestamp, correlation ID, and the applied policy version in auditable history. Do not record tokens/secrets or unnecessary PII.
- Preserve idempotency behavior for approval decision commands: same key and payload replays the prior result; a conflicting replay returns `409`.
- Do not select event transport or add broker-dependent behavior while the event-broker decision remains OPEN.

## Constitution Check

No repository constitution file was found. The following checks are grounded in the accepted architecture and business rules and must be reconciled with a constitution before implementation:

| Principle | Result | Plan alignment |
|---|---|---|
| Bounded-context ownership | PASS | Approval data and final approval authorization are owned by Corporate. |
| Schema isolation | PASS | All approval tables are in `corporate_schema`; external IDs are logical references only. |
| Security / least privilege | PASS | Corporate validates organization scope and the acting member's authority; request fields cannot grant permission. |
| Financial correctness | PASS | Approval is separate from execution and Ledger posting. |
| Auditability | PASS | Decisions and policy versions are recorded append-only with actor and correlation context. |
| No inferred business rules | CONDITIONAL | Policy lifecycle and rejection/expiry/delegation/resubmission decisions must be approved before affected implementation tasks start. |

## Applied Guidelines

No matching technology migration guidelines were found in the repository guideline directory. Existing Corporate service conventions and ADR-007/ADR-012 constraints govern the plan.

## Implementation Steps

### Step 1: Resolve Approval behavior and approve the feature baseline

- **Plan item**: P1.1
- **Requirements**: REQ-APR-001, REQ-APR-002, REQ-APR-003, REQ-APR-004, REQ-APR-005
- **Design inputs**: ADR-007, `BUSINESS-RULES.md`, `Phase-2-Decision-Gate.md`
- **Description**: Confirm the operations covered by the first slice; approve the policy-version behavior; define policy creation/edit/activation and effective dates; define how approval levels and eligible approvers are selected; decide rejection, expiry, delegation, resubmission, cancellation, and duplicate-decision behavior. Record accepted choices in ADR-007 / `OPEN-DECISIONS.md` and produce an Approval feature spec before implementation. This is a hard gate, not a coding task.
- **Tasks**:
  - [ ] T001 [Plan:P1.1] Create `docs/specs/corporate-approval/feature-spec.md` with owner-approved scope, policy lifecycle, workflow transitions, actor eligibility, and acceptance criteria; explicitly require the Corporate Membership/Mandate implementations or approved owner interfaces before workflow tasks.

### Step 2: Finalize Corporate approval data and API contracts

- **Plan item**: P1.2
- **Requirements**: REQ-APR-001, REQ-APR-003, REQ-APR-005
- **Design inputs**: `ERD.md`, `API-CONTRACTS.md`, Corporate service first slice
- **Description**: Specify policy versioning/immutability, request and step state transitions, decision idempotency scope, history fields, authorization claims/permissions, error mapping, and route/DTO shapes. Keep cross-service references logical and specify owner-side validation. Update the ERD and API contract after decisions are approved.
- **Tasks**:
  - [ ] T002 [Plan:P1.2] Update `docs/database/ERD.md`, `docs/api/API-CONTRACTS.md`, `docs/adr/ADR-007-configurable-corporate-approval.md`, and `docs/architecture/OPEN-DECISIONS.md` to reflect the approved Approval contract and policy-version behavior.

### Step 3: Add Corporate approval persistence and domain model

- **Plan item**: P2.1
- **Requirements**: REQ-APR-001, REQ-APR-003, REQ-APR-005
- **Design inputs**: Approved feature spec, approved data/API contract, current `corporate_schema` migration
- **Description**: Add a Flyway migration and domain/persistence types for immutable approval policy versions, approval requests, ordered steps, and append-only decision history. Add only constraints/indexes justified by the approved contract. Do not create cross-schema FKs to organization members, users, or transfers; validate those references through approved owner boundaries.
- **Tasks**:
  - [ ] T003 [Plan:P2.1] Create `V2__create_approval_tables.sql` under `services/corporate-service/src/main/resources/db/migration/` for the approved policy, version, request, step, and history schema.
  - [ ] T004 [Plan:P2.1] Add approval policy, request, step, decision, and history domain types under `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/domain/`.
  - [ ] T005 [Plan:P2.1] Add approval persistence/repository code under `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/persistence/` with organization-scoped queries and no cross-schema access.

### Step 4: Implement policy administration

- **Plan item**: P3.1
- **Requirements**: REQ-APR-001, REQ-APR-005
- **Design inputs**: Approved policy lifecycle and API contract
- **Description**: Implement organization-scoped policy creation, versioning, activation/deactivation and retrieval using the approved lifecycle. Enforce authorization in Corporate and prevent mutation of a policy version already referenced by a request. Route names and lifecycle transitions remain subject to Step 1/2 approval.
- **Tasks**:
  - [ ] T006 [Plan:P3.1] Implement the approved policy lifecycle in `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/application/ApprovalPolicyService.java`.
  - [ ] T007 [Plan:P3.1] Add policy request/response DTOs and organization-scoped policy endpoints in `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/web/ApprovalPolicyController.java`.
  - [ ] T008 [Plan:P3.1] Add policy lifecycle, version immutability, and organization authorization integration tests in `services/corporate-service/src/test/java/com/banking/platform/corporate/approval/web/ApprovalPolicyEndpointIntegrationTests.java`.

### Step 5: Implement approval request and decision workflow

- **Plan item**: P4.1
- **Requirements**: REQ-APR-001, REQ-APR-002, REQ-APR-003, REQ-APR-004, REQ-APR-005
- **Design inputs**: Approved state machine, policy version rule, membership/mandate contract, decision API contract
- **Description**: Create approval requests against the selected immutable policy version, execute the configured levels in order, authorize each decision against Corporate-owned rules, enforce Maker–Checker, and record every decision/result in history in the same transaction. Implement only transitions and outcomes explicitly approved by the feature spec.
- **Tasks**:
  - [ ] T009 [Plan:P4.1] Implement approval request creation and ordered step progression in `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/application/ApprovalWorkflowService.java`.
  - [ ] T010 [Plan:P4.1] Add Corporate-side approver eligibility and Maker–Checker checks in `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/application/ApprovalAuthorizationService.java`, consuming only approved Membership/Mandate interfaces.
  - [ ] T011 [Plan:P4.1] Persist decision transitions and append-only history atomically through `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/persistence/ApprovalRepository.java`.

### Step 6: Expose approval APIs and enforce command safety

- **Plan item**: P4.2
- **Requirements**: REQ-APR-002, REQ-APR-003, REQ-APR-004
- **Design inputs**: Approved API contract and security claim mapping
- **Description**: Implement the approved policy query/admin and approval decision endpoints in the Corporate web layer. Apply organization-scoped authorization, validate request ownership through the approved integration boundary, reject self-approval, and implement decision idempotency and error responses. Do not treat API Gateway authorization as sufficient.
- **Tasks**:
  - [ ] T012 [Plan:P4.2] Add the approved decision request/response DTOs and routes in `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/web/ApprovalDecisionController.java`.
  - [ ] T013 [Plan:P4.2] Implement decision idempotency, same-key replay, conflicting-payload detection, and error mapping in `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/application/ApprovalDecisionService.java`; cover them in `services/corporate-service/src/test/java/com/banking/platform/corporate/approval/ApprovalDecisionServiceTests.java`.

### Step 7: Verify behavior, isolation, and audit evidence

- **Plan item**: P5.1
- **Requirements**: REQ-APR-001, REQ-APR-002, REQ-APR-003, REQ-APR-004, REQ-APR-005
- **Design inputs**: Approved acceptance criteria and API/data contracts
- **Description**: Add focused unit and PostgreSQL integration tests for policy version immutability, multi-level progression, authorization, maker self-approval denial, decision replay/conflict, atomic decision-plus-history persistence, and absence of cross-schema access. Run `./mvnw -f services/corporate-service/pom.xml verify` and update Corporate README and Phase 2 status.
- **Tasks**:
  - [ ] T014 [Plan:P5.1] Add PostgreSQL Testcontainers coverage for multi-level decisions, Maker–Checker, policy changes, transactional history, and schema isolation under `services/corporate-service/src/test/java/com/banking/platform/corporate/approval/`.
  - [ ] T015 [Plan:P5.1] Run `./mvnw -f services/corporate-service/pom.xml verify`, update `services/corporate-service/README.md` and `docs/plan/phase/Phase-2-Identity-Party-Corporate-Branch.md`, and record verified scope and remaining dependencies.

### Deferred: Transfer enforcement and financial execution

This work is not included in this Corporate plan. Once Transfer is approved and implemented, a separate plan must define how Transfer submits an approval request, consumes the Corporate result, handles pending/rejected/expired requests, and prevents execution until approval is complete. Ledger posting and payment settlement remain owned by their respective services.

## Project Structure

```text
services/corporate-service/src/main/java/com/banking/platform/corporate/
  approval/
    application/       # policy lifecycle and approval workflow
    domain/            # policy version, request, step, decision, history
    persistence/       # repositories / SQL for corporate_schema
    web/               # approved policy and decision endpoints
  config/              # existing security/configuration patterns
services/corporate-service/src/main/resources/db/migration/
  V2__create_approval_tables.sql
services/corporate-service/src/test/java/com/banking/platform/corporate/
  approval/            # focused domain, API, authorization, and PostgreSQL tests
```

Final package names, schema columns, routes, and state values are design outputs of P1.2, not decisions made by this plan.

## Testing Strategy

- **appType**: API-only Spring Boot service.
- **Critical user journeys**:
  1. Authorized organization administrator creates and versions a policy using approved rules.
  2. An approval request is created and pins the policy version approved for that request.
  3. Eligible checkers complete configured levels in order; history identifies each actor, decision, time, and policy version.
  4. Maker cannot approve their own request; out-of-scope actors cannot decide for the organization.
  5. Retrying the same decision is idempotent; a conflicting replay is rejected and does not create duplicate history.
- **primaryValidationStack**: Maven Wrapper, JUnit/Spring Boot tests, PostgreSQL Testcontainers.
- **fallbackMatrix**:
  - `infra-tier`: PostgreSQL Testcontainers → no database substitute for final migration/constraint acceptance. If Docker is unavailable, unit tests can run but the persistence acceptance gate remains incomplete.
  - `browser-tier`: Not applicable; no browser UI is in this slice.
- **Environment requirements**: Java runtime, Maven Wrapper, Docker for Testcontainers integration tests.
- **knownGaps**: No real Keycloak, Membership/Mandate, or Transfer integration is included unless those dependencies are separately approved and available.
- **Test data strategy**: Synthetic organization/member/request IDs; isolate test rows and containers; never use real identities or secrets.
- **Acceptance criteria**: All approved scenarios pass; no cross-schema database access; no approval decision is committed without matching durable history; build/test succeeds.
- **Validation review expectations**: Confirm requirements traceability, approved state transitions, Maker–Checker enforcement, policy version binding, access control, and no financial execution side effects.

## Requirement Mapping

These plan-local IDs are derived from accepted source documents; replace/align them with the IDs in the standalone Approval feature spec when that spec is approved.

| REQ ID | Description | Plan Items | Implementation Evidence |
|---|---|---|---|
| REQ-APR-001 | Approval policies are configurable and support multiple approval levels. | P1.1, P1.2, P2.1, P3.1, P4.1, P5.1 | Approval feature spec, policy lifecycle service/API, versioned policy persistence, workflow tests |
| REQ-APR-002 | Approval enforces Maker–Checker; the maker cannot approve their own action. | P1.1, P4.1, P4.2, P5.1 | Approval authorization/domain service, decision endpoint, self-approval integration test |
| REQ-APR-003 | Approval decisions retain auditable approval history. | P1.1, P1.2, P2.1, P4.1, P4.2, P5.1 | Approval history persistence, atomic decision transaction, history API/verification tests |
| REQ-APR-004 | The service enforcing the business action validates approval authority; callers cannot grant themselves roles/scopes. | P1.1, P4.1, P4.2, P5.1 | Corporate authorization service, scoped decision checks, unauthorized-actor tests |
| REQ-APR-005 | A request records the policy/version used for its approval evaluation; pending-request change behavior is explicitly approved. | P1.1, P1.2, P2.1, P3.1, P4.1, P5.1 | Immutable policy version reference, request persistence, policy-change tests, approved ADR/spec |

## Complexity Tracking

No constitution violations are currently known. The conditional business-rule gate is intentional and must be resolved before affected implementation tasks begin.
