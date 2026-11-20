# Implementation Plan: Corporate Approval Core

**Date**: 2026-11-20 | **Status**: Implementation in progress
**Source baseline**: [Feature spec](../../../specs/corporate-approval/feature-spec.md), [ADR-007](../../../adr/ADR-007-configurable-corporate-approval.md), [Business Rules](../../../domain/BUSINESS-RULES.md), [Phase 2 Decision Gate](../Phase-2-Decision-Gate.md), [API Contracts](../../../api/API-CONTRACTS.md), [Logical ERD](../../../database/ERD.md)

## Summary

Plan a Corporate-owned approval capability that supports configurable multi-level approval policies, Maker–Checker, and durable approval history. The first implementation scope is the Corporate policy and approval workflow only. Corporate Membership and Mandate are not implemented yet; their approved data/authorization contracts and owner-side interfaces are prerequisites for approver eligibility and must be delivered before the dependent workflow tasks. Transfer execution, Ledger posting, and payment settlement are excluded; the later Transfer owner must enforce the approval outcome before executing a financial operation.

The project owner approved the behavior and policy-management baseline on 2026-11-20 and it is recorded in [ADR-007](../../../adr/ADR-007-configurable-corporate-approval.md): each request pins an immutable policy version at submission; levels are sequential and all must approve; rejection is terminal; expiry and delegation are excluded; resubmission creates a new request ID; makers cannot self-approve; policy management uses an organization-scoped permission and explicit activation; each level declares role/mandate scope; requests carry an opaque operation reference from the owner service. Membership/Mandate interfaces and exact Keycloak claim mappings must be available before approver authorization can ship.

The Approval feature spec records the approved baseline and remaining integration prerequisites. Requirement IDs are `REQ-001` through `REQ-010`. No repository constitution file was found; reconcile against it if one is added.

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
| No inferred business rules | CONDITIONAL | Detailed API/claim mappings and Membership/Mandate owner interfaces must be finalized before dependent integration tasks start. |

## Applied Guidelines

No matching technology migration guidelines were found in the repository guideline directory. Existing Corporate service conventions and ADR-007/ADR-012 constraints govern the plan.

## Implementation Steps

### Step 1: Resolve Approval behavior and approve the feature baseline

- **Plan item**: P1.1
- **Requirements**: REQ-001, REQ-002, REQ-003, REQ-004, REQ-005, REQ-006, REQ-007, REQ-008, REQ-009, REQ-010
- **Design inputs**: ADR-007, `BUSINESS-RULES.md`, `Phase-2-Decision-Gate.md`
- **Description**: Confirm the first-slice boundary and record the accepted policy/workflow defaults in the feature spec and ADR. Finalize only the remaining detailed Membership/Mandate owner interfaces, role/mandate catalog, exact claim mapping, and route/DTO contract before dependent implementation tasks.
- **Tasks**:
  - [x] T001 [Plan:P1.1] Record the provisional `corporate_grants` representation of `approval-policy:manage` and the Membership/Mandate owner-interface dependency in `docs/specs/corporate-approval/feature-spec.md`, `docs/api/API-CONTRACTS.md`, and `services/corporate-service/README.md`.

### Step 2: Finalize Corporate approval data and API contracts

- **Plan item**: P1.2
- **Requirements**: REQ-001, REQ-002, REQ-003, REQ-008, REQ-009, REQ-010
- **Design inputs**: `ERD.md`, `API-CONTRACTS.md`, Corporate service first slice
- **Description**: Specify policy versioning/immutability, request and step state transitions, decision idempotency scope, history fields, authorization claims/permissions, error mapping, and route/DTO shapes. Keep cross-service references logical and specify owner-side validation. Record the approved behavior and keep Membership/Mandate and Identity mapping dependencies explicit.
- **Tasks**:
  - [x] T002 [Plan:P1.2] Finalize the approved Approval policy and data/API contract in `docs/database/ERD.md`, `docs/api/API-CONTRACTS.md`, `docs/adr/ADR-007-configurable-corporate-approval.md`, and `docs/architecture/OPEN-DECISIONS.md`; keep canonical Keycloak mapping and Membership/Mandate interfaces explicitly marked as integration prerequisites.

### Step 3: Add Corporate approval persistence and domain model

- **Plan item**: P2.1
- **Requirements**: REQ-001, REQ-002, REQ-008
- **Design inputs**: Approved feature spec, approved data/API contract, current `corporate_schema` migration
- **Description**: Add the Corporate-owned Flyway schema for immutable approval policy versions, approval requests, ordered steps, and append-only decision history, plus the policy domain and persistence types needed by policy administration. Add only constraints/indexes justified by the approved contract. Do not create cross-schema FKs to organization members, users, or transfers; validate those references through approved owner boundaries.
- **Tasks**:
  - [x] T003 [Plan:P2.1] Create `V2__create_approval_tables.sql` under `services/corporate-service/src/main/resources/db/migration/` for the approved policy, version, request, step, and history schema.
  - [x] T004 [Plan:P2.1] Add the approval policy status, ordered level and response domain types in `services/corporate-service/src/main/java/com/banking/platform/corporate/domain/`; add request, step and decision types with the workflow tasks that use them.
  - [x] T005 [Plan:P2.1] Add organization-scoped policy persistence/repository code under `services/corporate-service/src/main/java/com/banking/platform/corporate/persistence/` with no cross-schema access; request/decision persistence is completed with T011.

### Step 4: Implement policy administration

- **Plan item**: P3.1
- **Requirements**: REQ-001, REQ-002
- **Design inputs**: Approved policy lifecycle and API contract
- **Description**: Implement organization-scoped policy creation, versioning, activation/deactivation and retrieval using the approved lifecycle. Enforce authorization in Corporate and prevent mutation of policy version content through application behavior and database grants.
- **Tasks**:
  - [x] T006 [Plan:P3.1] Implement the approved policy lifecycle in `services/corporate-service/src/main/java/com/banking/platform/corporate/application/ApprovalPolicyService.java`.
  - [x] T007 [Plan:P3.1] Add policy request/response DTOs and organization-scoped policy endpoints in `services/corporate-service/src/main/java/com/banking/platform/corporate/web/ApprovalPolicyController.java`.
  - [x] T008 [Plan:P3.1] Add policy lifecycle, version immutability, and organization authorization integration tests in `services/corporate-service/src/test/java/com/banking/platform/corporate/web/OrganizationEndpointIntegrationTests.java`.

### Step 5: Implement approval request and decision workflow

- **Plan item**: P4.1
- **Requirements**: REQ-001, REQ-002, REQ-003, REQ-004, REQ-005, REQ-006, REQ-007, REQ-008, REQ-009, REQ-010
- **Design inputs**: Approved state machine, policy version rule, membership/mandate contract, decision API contract
- **Description**: Create approval requests against the selected immutable policy version, execute the configured levels in order, authorize each decision against Corporate-owned rules, enforce Maker–Checker, and record every decision/result in history in the same transaction. Implement only transitions and outcomes explicitly approved by the feature spec.
- **Tasks**:
  - [ ] T009 [Plan:P4.1] Implement approval request creation and ordered step progression in `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/application/ApprovalWorkflowService.java`.
  - [ ] T010 [Plan:P4.1] Add Corporate-side approver eligibility and Maker–Checker checks in `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/application/ApprovalAuthorizationService.java`, consuming only approved Membership/Mandate interfaces.
  - [ ] T011 [Plan:P4.1] Persist decision transitions and append-only history atomically through `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/persistence/ApprovalRepository.java`.

### Step 6: Expose approval APIs and enforce command safety

- **Plan item**: P4.2
- **Requirements**: REQ-003, REQ-004, REQ-005, REQ-007, REQ-008, REQ-009, REQ-010
- **Design inputs**: Approved API contract and security claim mapping
- **Description**: Implement the approved policy query/admin and approval decision endpoints in the Corporate web layer. Apply organization-scoped authorization, validate request ownership through the approved integration boundary, reject self-approval, and implement decision idempotency and error responses. Do not treat API Gateway authorization as sufficient.
- **Tasks**:
  - [ ] T012 [Plan:P4.2] Add the approved decision request/response DTOs and routes in `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/web/ApprovalDecisionController.java`.
  - [ ] T013 [Plan:P4.2] Implement decision idempotency, same-key replay, conflicting-payload detection, and error mapping in `services/corporate-service/src/main/java/com/banking/platform/corporate/approval/application/ApprovalDecisionService.java`; cover them in `services/corporate-service/src/test/java/com/banking/platform/corporate/approval/ApprovalDecisionServiceTests.java`.

### Step 7: Verify behavior, isolation, and audit evidence

- **Plan item**: P5.1
- **Requirements**: REQ-001, REQ-002, REQ-003, REQ-004, REQ-005, REQ-006, REQ-007, REQ-008, REQ-009, REQ-010
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
  application/         # organization services and policy lifecycle
  domain/              # organization and policy domain types
  persistence/         # repositories / SQL for corporate_schema
  web/                 # organization and policy endpoints
  config/              # existing security/configuration patterns
services/corporate-service/src/main/resources/db/migration/
  V2__create_approval_tables.sql
services/corporate-service/src/test/java/com/banking/platform/corporate/
  web/                 # PostgreSQL API, authorization, policy and schema-isolation tests
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
- **External dependency strategy**: Stub Membership/Mandate and operation-owner ports for isolated Corporate tests; use PostgreSQL Testcontainers for persistence; real owner-service integration is a separate acceptance gate after those services exist.
- **Test base classes**: Reuse the existing Spring Boot test configuration; add a shared Corporate approval integration-test base only if multiple tests need identical container/context setup.
- **Per-module test skeletons**: Approval policy tests cover lifecycle/versioning; workflow tests cover sequence and transitions; web tests cover auth/error contracts; persistence tests cover schema, transaction atomicity, and history.
- **Execution rule**: Run focused approval tests while iterating, then require `./mvnw -f services/corporate-service/pom.xml verify` before marking the batch complete.
- **knownGaps**: Membership/Mandate and Keycloak integrations are not currently available; stubbed owner ports are suitable for isolated tests only, not production readiness. Transfer integration remains deferred.
- **Test data strategy**: Synthetic organization/member/request IDs; isolate test rows and containers; never use real identities or secrets.
- **Acceptance criteria**: All approved scenarios pass; no cross-schema database access; no approval decision is committed without matching durable history; build/test succeeds.
- **Validation review expectations**: Confirm requirements traceability, approved state transitions, Maker–Checker enforcement, policy version binding, access control, and no financial execution side effects.

## Requirement Mapping

Task IDs below provide the full requirement-to-plan-to-task trace for the current feature-spec draft.

| REQ ID | Description | Plan Items | Tasks | Implementation Evidence |
|---|---|---|---|---|
| REQ-001 | Corporate supports organization-specific policies with multiple ordered levels, org-scoped policy management, explicit activation, and at most one active version per operation type. | P1.1, P1.2, P2.1, P3.1, P4.1, P5.1 | T001–T011, T014–T015 | Policy lifecycle and versioned persistence, approval workflow, API and tests |
| REQ-002 | A request pins the immutable policy version selected at submission. | P1.1, P1.2, P2.1, P3.1, P4.1, P5.1 | T001–T007, T009–T011, T014–T015 | Policy version reference, immutable version behavior and policy-change tests |
| REQ-003 | Approval levels run sequentially and all required levels must approve. | P1.1, P1.2, P4.1, P4.2, P5.1 | T001–T002, T009–T015 | Workflow state transitions and ordering tests |
| REQ-004 | The maker cannot approve their own request. | P1.1, P4.1, P4.2, P5.1 | T001, T009–T015 | Corporate authorization and self-approval denial tests |
| REQ-005 | A rejection at any level is terminal for that request. | P1.1, P4.1, P4.2, P5.1 | T001, T009–T015 | Terminal state handling and rejection tests |
| REQ-006 | Expiry and delegation are not supported in the MVP. | P1.1, P4.1, P5.1 | T001, T009–T011, T014–T015 | State model excludes these transitions and tests verify they are unavailable |
| REQ-007 | Resubmission creates a new request ID and preserves the rejected request/history. | P1.1, P4.1, P4.2, P5.1 | T001, T009–T015 | New request identity and immutable prior history tests |
| REQ-008 | Decision history is append-only and records actor, level, decision, time, and policy version. | P1.1, P1.2, P2.1, P4.1, P4.2, P5.1 | T001–T005, T009–T015 | History persistence, atomic decision recording and audit tests |
| REQ-009 | Corporate validates active Membership/Mandate against each level's role/mandate scope, not caller-supplied grants. | P1.1, P4.1, P4.2, P5.1 | T001, T009–T015 | Owner-side authorization, Membership/Mandate boundary and negative authorization tests |
| REQ-010 | Approval accepts an opaque owner-supplied operation type/reference and does not execute transfers, post Ledger entries, or settle payments. | P1.1, P1.2, P4.1, P4.2, P5.1 | T001–T002, T009–T015 | Service boundary and integration tests verify no financial side effects |

## Complexity Tracking

No constitution violations are currently known. The conditional business-rule gate is intentional and must be resolved before affected implementation tasks begin.
