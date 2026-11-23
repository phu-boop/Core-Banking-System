# Implementation Plan: Provider-Neutral Interbank Transfer

**Date**: 2026-11-21 | **Status**: Planned — provider and settlement decisions deferred
**Source baseline**: [Feature spec](../../../specs/interbank-transfer/feature-spec.md), [ADR-006](../../../adr/ADR-006-internal-and-interbank-transfer.md), [Business Rules](../../../domain/BUSINESS-RULES.md), [Bounded Contexts](../../../domain/BOUNDED_CONTEXTS.md), [API Contracts](../../../api/API-CONTRACTS.md), [Event Contracts](../../../architecture/EVENT-CONTRACTS.md), [Open Decisions](../../../architecture/OPEN-DECISIONS.md)

## Summary

Define a bounded provider-neutral Interbank Transfer implementation slice. Transfer submits approved instructions to a Payment boundary, Payment owns provider submission/outcome evidence, and no provider-specific protocol or settlement rule enters Transfer or Ledger.

This plan does not implement an external provider, callback protocol, settlement finality, posting timing, reversal or reconciliation policy. Those decisions remain blocked in OPEN items 1 and 10.

## Technical Context

**Language/Version**: Java 21
**Primary Dependencies**: Spring Boot 3.5, Spring Security OAuth2, JDBC/PostgreSQL, Flyway, REST synchronous internal API
**Storage**: Payment-owned `payment_schema`; Transfer remains the transfer state owner
**Testing**: JUnit/Spring Boot, PostgreSQL Testcontainers, MockRestServiceServer or provider adapter contract tests
**Target Platform**: Independent Payment service plus existing Transfer service
**Constraints**:

- Payment owns provider submission and outcome evidence only.
- Transfer owns interbank transfer orchestration and unresolved status.
- No direct database access across Payment, Transfer, Ledger or other service schemas.
- Timeout or no-response cannot be treated as confirmed failure.
- Every submission and verified outcome must be idempotent and auditable.

## Constitution Check

No repository constitution file exists. The plan follows the accepted architecture.

| Principle | Result | Plan alignment |
|---|---|---|
| Bounded-context ownership | PASS | Payment is the provider boundary; Transfer retains orchestration state |
| Schema isolation | PASS | Separate service schemas and owner APIs |
| Unknown-outcome safety | PASS | Unresolved state is preserved until verification |
| Financial correctness | PASS | No Ledger posting or reversal is inferred in this slice |
| Idempotency | PASS | Submission and outcome commands are deduplicated |
| Auditability | PASS | Correlation, source and provider references are retained |

## Applied Guidelines

- Existing Spring Boot/JDBC/Flyway conventions.
- ADR-006 provider-neutral interbank boundary.
- ADR-012 REST, idempotency and audit conventions.
- Existing Event Contracts for proposed submission/outcome reporting.

## Implementation Steps

### Step 1: Finalize the provider-neutral boundary

- **Plan item**: P1.1
- **Requirements**: REQ-001, REQ-003, REQ-004, REQ-005, REQ-008, REQ-009, REQ-013
- **Design inputs**: Feature spec, ADR-006, bounded contexts, open decisions
- **Description**: Define the provider-neutral submission and outcome contracts. Explicitly exclude provider-specific protocol, settlement finality and posting/reversal behavior.
- **Tasks**:
  - [x] T001 [Plan:P1.1] Create `docs/specs/interbank-transfer/feature-spec.md` with the provider-neutral scope and unresolved decisions.
  - [x] T002 [Plan:P1.1] Add provider-neutral Payment and interbank status contracts to `docs/api/API-CONTRACTS.md`.
  - [x] T003 [Plan:P1.1] Update `docs/architecture/EVENT-CONTRACTS.md` with unresolved, settled and failed outcome semantics; do not define a broker transport.

### Step 2: Scaffold the Payment boundary

- **Plan item**: P2.1
- **Requirements**: REQ-001, REQ-006, REQ-009, REQ-010
- **Design inputs**: Existing Transfer service structure and Payment bounded-context contract
- **Description**: Add an independent Spring Boot Payment module with PostgreSQL/Flyway and service authorization.
- **Tasks**:
  - [x] T004 [Plan:P2.1] Create `services/payment-service/pom.xml`, application configuration and README.
  - [x] T005 [Plan:P2.1] Create `V1__create_payment_tables.sql` for payment submissions, outcome history and idempotency records.

### Step 3: Implement provider-neutral Payment domain and persistence

- **Plan item**: P3.1
- **Requirements**: REQ-003, REQ-004, REQ-006, REQ-007, REQ-008, REQ-010
- **Design inputs**: Payment API contract and unresolved-state rules
- **Description**: Implement durable payment states, submission identity, outcome history and idempotent commands without provider protocol assumptions.
- **Tasks**:
  - [x] T006 [Plan:P3.1] Add Payment domain types and state transitions under `services/payment-service/src/main/java/com/banking/platform/payment/domain/`.
  - [x] T007 [Plan:P3.1] Add JDBC repositories under `services/payment-service/src/main/java/com/banking/platform/payment/persistence/`.
  - [x] T008 [Plan:P3.1] Add REST submit/get routes under `services/payment-service/src/main/java/com/banking/platform/payment/web/`.

### Step 4: Integrate the Transfer orchestration gate

- **Plan item**: P4.1
- **Requirements**: REQ-002, REQ-003, REQ-004, REQ-005, REQ-011, REQ-012
- **Design inputs**: Existing Transfer approval gate and provider-neutral Payment API
- **Description**: Submit only approved eligible interbank transfers and record unresolved/payment outcomes without Ledger or settlement side effects.
- **Tasks**:
  - [x] T009 [Plan:P4.1] Add `PaymentSubmissionClient` in `services/transfer-service/src/main/java/com/banking/platform/transfer/integration/`.
  - [x] T010 [Plan:P4.1] Add interbank route handling to `ApprovalEnforcedTransferService` while preserving pending/rejected approval gates.
  - [x] T011 [Plan:P4.1] Extend Transfer persistence and API representations with unresolved interbank outcome references.

### Step 5: Enforce outcome and reconciliation safety

- **Plan item**: P5.1
- **Requirements**: REQ-003, REQ-004, REQ-005, REQ-007, REQ-010, REQ-013
- **Design inputs**: Feature edge cases and approved no-inference rules
- **Description**: Add outcome-source abstraction and reconciliation hooks. Keep timeout unresolved and prevent stale callbacks from overwriting terminal verified states.
- **Tasks**:
  - [x] T012 [Plan:P5.1] Implement a provider-neutral outcome source and callback/reconciliation adapter boundary in `services/payment-service/src/main/java/com/banking/platform/payment/integration/`.
  - [x] T013 [Plan:P5.1] Implement duplicate and stale outcome rejection in Payment repositories/application services.
  - [x] T014 [Plan:P5.1] Add audit history for submission, outcome source, provider reference and correlation ID.

### Step 6: Verify provider-neutral behavior

- **Plan item**: P6.1
- **Requirements**: REQ-001 through REQ-013
- **Design inputs**: Feature acceptance scenarios
- **Description**: Verify submission idempotency, unresolved timeout behavior, duplicate outcomes, schema isolation and no Ledger/settlement side effects.
- **Tasks**:
  - [x] T015 [Plan:P6.1] Add Payment unit and PostgreSQL Testcontainers tests.
  - [x] T016 [Plan:P6.1] Add Transfer mock-boundary tests for approved, pending and unresolved interbank operations.
  - [x] T017 [Plan:P6.1] Run `./mvnw verify` and record the provider-neutral smoke-test verdict.

## Project Structure

```text
services/payment-service/
  pom.xml
  README.md
  src/main/java/com/banking/platform/payment/
    application/
    domain/
    integration/
    persistence/
    web/
  src/main/resources/db/migration/
  src/test/java/com/banking/platform/payment/
services/transfer-service/
  src/main/java/com/banking/platform/transfer/integration/
  src/main/java/com/banking/platform/transfer/application/
docs/specs/interbank-transfer/
docs/plan/phase/interbank-transfer/
```

## Testing Strategy

- **appType**: API-only Spring Boot service plus internal REST integration.
- **Critical user journeys**:
  1. Approved interbank transfer submits once and returns pending/unresolved state.
  2. Duplicate submission replay returns the prior result; conflicting payload returns `409`.
  3. Timeout remains unresolved and creates no failure or reversal.
  4. Duplicate callbacks apply one verified outcome only.
  5. Stale reconciliation cannot overwrite a terminal verified state.
- **primaryValidationStack**: Maven Wrapper, JUnit, PostgreSQL Testcontainers, MockRestServiceServer.
- **fallbackMatrix**:
  - `infra-tier`: PostgreSQL Testcontainers; no substitute for schema and migration acceptance.
  - `browser-tier`: Not applicable.
- **Environment requirements**: Java 21 and Docker for Testcontainers.
- **knownGaps**: External provider protocol, callback authentication, settlement finality and reconciliation policy remain unavailable.
- **Test data strategy**: Synthetic transfer/payment/provider IDs; no real financial data or credentials.
- **Acceptance criteria**: No timeout is treated as failure; no duplicate submission/outcome; no cross-schema access; no Ledger/settlement side effect.

## Deferred Decisions

The plan intentionally leaves these decisions unresolved:

- External interbank network/provider/protocol.
- Settlement finality and posting/reversal timing.
- Callback/query/reconciliation mechanism.
- Timeout, retry and dead-letter policy.

## Requirement Mapping

| REQ ID | Description | Plan Items | Implementation Evidence |
|--------|-------------|------------|------------------------|
| REQ-001 | Payment owns provider boundary; Transfer owns orchestration | P1.1, P2.1, P4.1 | Payment module and PaymentSubmissionClient |
| REQ-002 | Only approved eligible transfer reaches external submission | P4.1 | Transfer approval gate and submission tests |
| REQ-003 | Pending, unresolved, settled and failed states are distinct | P1.1, P3.1, P4.1, P5.1 | Domain types and state-transition tests |
| REQ-004 | Timeout remains unresolved | P1.1, P4.1, P5.1 | Unresolved state tests |
| REQ-005 | Timeout does not cause failure or reversal | P1.1, P4.1, P5.1 | No failure/reversal side-effect tests |
| REQ-006 | Submission uses actor, operation and business-scope idempotency | P2.1, P3.1 | Idempotency persistence and replay tests |
| REQ-007 | Outcomes are durable, deduplicated and stale-safe | P3.1, P5.1 | Outcome repository and duplicate/stale tests |
| REQ-008 | Provider reference is retained only when supplied | P3.1 | Provider-reference persistence tests |
| REQ-009 | No cross-schema direct access | P2.1, P3.1 | Payment repository and isolation tests |
| REQ-010 | Submission/outcome history is audit evidence | P2.1, P3.1, P5.1 | Outcome history and audit tests |
| REQ-011 | Verified outcome reaches Transfer through audited boundary | P4.1, P5.1 | Payment client/Transfer outcome tests |
| REQ-012 | VND and Corporate approval remain enforced | P4.1 | Existing approval gate and currency tests |
| REQ-013 | Ledger/settlement/reversal behavior is not inferred | P1.1, P5.1 | Deferred boundary and no-adapter tests |

## Complexity Tracking

No constitution violations are intentionally introduced. Provider-specific behavior is deferred to prevent invented settlement and reversal rules.
