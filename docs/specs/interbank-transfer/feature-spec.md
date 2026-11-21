# Feature Specification: Provider-Neutral Interbank Transfer

**Feature Branch**: `phase-6-interbank-transfer`
**Created**: 2026-11-21
**Status**: Draft
**Input**: Provider-neutral Phase 6 interbank transfer boundary

## Scope Baseline

- **Discovery method**: Reviewed ADR-006, ADR-012, bounded contexts, business rules, API contracts, event contracts, open decisions and the Phase 5 transfer implementation.
- **Total items discovered**: Interbank provider integration, external outcome reconciliation, settlement, reversal and timeout behavior.
- **Items in scope**: Provider-neutral submission boundary, external outcome tracking, idempotency, and unresolved-state handling. Provider/network-specific protocol and final settlement rules remain blocked.

## User Scenarios & Testing

### User Story 1 - Submit an Interbank Transfer (Priority: P1)

An authorized caller submits an approved interbank transfer. The system creates one durable payment submission, associates it with the transfer, and exposes a submission status without claiming provider success or settlement.

**Why this priority**: Submission is the required entry point for an external interbank transfer.

**Independent Test**: Submit the same valid operation twice with the same idempotency scope and verify one payment submission and one replayed response.

**Acceptance Scenarios**:

1. **Given** an approved transfer with a valid external destination and VND amount, **When** the caller submits it to the interbank boundary, **Then** the system records a pending external submission.
2. **Given** a duplicate retry with the same actor, operation and business payload, **When** it is submitted, **Then** the system returns the prior payment state without creating a duplicate submission.
3. **Given** a duplicate retry with a conflicting payload, **When** it is submitted, **Then** the system rejects the retry with a conflict.

### User Story 2 - Keep Unknown Outcomes Unresolved (Priority: P1)

When a provider submission times out or no verified response is available, the system preserves an unresolved external outcome instead of reporting failure or success.

**Why this priority**: A timeout must not become an authoritative financial result.

**Independent Test**: Simulate a provider timeout and verify the transfer and payment remain unresolved, with no failure or reversal event.

**Acceptance Scenarios**:

1. **Given** a provider timeout without a verified external result, **When** the submission returns an unresolved response, **Then** the payment and transfer remain unresolved.
2. **Given** an unresolved payment, **When** no verified provider result exists, **Then** the system does not publish transfer failure or perform reversal.

### User Story 3 - Verify an External Outcome (Priority: P2)

An authorized reconciliation or provider callback establishes a verified settled or failed external outcome. The result is applied once and preserved in audit history.

**Why this priority**: External outcomes need authoritative verification before downstream state changes.

**Independent Test**: Deliver duplicate callbacks for the same payment and verify the first result is applied once and duplicate delivery is ignored.

**Acceptance Scenarios**:

1. **Given** a verified provider settlement, **When** the payment outcome is recorded, **Then** the payment becomes settled and the verified result is linked to the transfer.
2. **Given** a verified provider rejection, **When** the payment outcome is recorded, **Then** the payment is failed only after verification.
3. **Given** the same provider callback twice, **When** both are processed, **Then** only one outcome side effect is applied.

### User Story 4 - Reconcile Outstanding Submissions (Priority: P3)

Operations staff or an approved reconciliation process can identify unresolved submissions and verify them through a supported outcome-source.

**Why this priority**: A durable unresolved state requires a process to obtain a final verified result.

**Independent Test**: Mark a submission unresolved, run reconciliation against the selected outcome-source contract, and verify the resulting state transition.

**Acceptance Scenarios**:

1. **Given** an unresolved submission, **When** reconciliation is requested and no outcome is available, **Then** the submission remains unresolved.
2. **Given** an unresolved submission with a verified settled result, **When** reconciliation applies the result, **Then** settlement is recorded once.

### Edge Cases

- Same idempotency key with the same payload returns the original submission.
- Same idempotency key with a conflicting payload returns `409`.
- A callback arriving before a submission is durable must not create an orphaned authoritative outcome.
- A stale callback or reconciliation result must not overwrite a terminal verified state.
- Provider rejection is different from no-response timeout.
- Provider reference absence must not be inferred as success or failure.
- Duplicate callbacks and repeated reconciliation queries must be harmless.

## Requirements

### Functional Requirements

- **REQ-001**: The system MUST keep Payment as the provider-integration boundary and Transfer as the transfer orchestration owner.
- **REQ-002**: The system MUST submit only an approved, eligible transfer to the external interbank boundary.
- **REQ-003**: The system MUST distinguish pending submission, unresolved outcome, verified settled, and verified failed states.
- **REQ-004**: The system MUST remain unresolved after timeout or unavailable provider response until a verified outcome exists.
- **REQ-005**: The system MUST NOT publish a transfer failure or execute reversal solely because an external request timed out.
- **REQ-006**: Submission and retry commands MUST use idempotency scoped by actor, operation and business scope.
- **REQ-007**: Provider callbacks and reconciliation results MUST be durable, deduplicated and unable to overwrite a terminal verified state with stale data.
- **REQ-008**: Payment MUST persist provider references only when supplied by an authorized outcome-source.
- **REQ-009**: Payment MUST NOT access or write Transfer, Ledger or any other service-owned schema directly.
- **REQ-010**: External outcome history MUST record submission, outcome source, correlation ID, timestamps and provider reference when available.
- **REQ-011**: A verified settlement or failure MUST be reported to the owning transfer state through an auditable boundary.
- **REQ-012**: VND and the existing approved transfer authorization/corporate approval boundaries MUST remain in effect.
- **REQ-013**: Ledger posting timing, hold/reservation, fees, reversal and reconciliation MUST NOT be inferred from provider response availability.

### Key Entities

- **Transfer**: Corporate or retail transfer instruction and its orchestration state.
- **Payment**: Provider-neutral external payment submission owned by the Payment boundary.
- **External Outcome**: Verified or unresolved provider-facing result associated with a payment.
- **Provider Reference**: Opaque identifier supplied by the external provider or outcome-source.
- **Submission History**: Durable submission, callback or reconciliation evidence.

## Success Criteria

### Measurable Outcomes

- **SC-001**: 100% of tested duplicate submission retries create no additional payment submission.
- **SC-002**: 100% of tested timeout cases remain unresolved and produce no failure or reversal side effect.
- **SC-003**: 100% of duplicate callback/reconciliation deliveries apply no duplicate outcome side effect.
- **SC-004**: 100% of verified settlement and failure transitions include a correlation ID and outcome source.
- **SC-005**: No Payment test or integration can read or write another service-owned schema directly.
- **SC-006**: Every VND interbank submission is rejected or accepted according to the same authorization and approval boundary consistently across retries.

## Assumptions

- REST remains the first-slice synchronous internal API convention.
- Provider and protocol details are not known and do not become Transfer or Ledger domain concepts.
- External outcome cannot be verified solely by an HTTP timeout or absence of a callback.
- Settlement, posting and reversal timing require separate approved provider/network contracts.

## NEEDS CLARIFICATION

- [NEEDS CLARIFICATION: Which external interbank network/provider and protocol are approved for the first executable integration?]
- [NEEDS CLARIFICATION: What settlement finality and posting/reversal timing does the approved network contract require?]
- [NEEDS CLARIFICATION: Which callback/query/reconciliation mechanism and timeout policy verify external outcomes?]

## Constitution Alignment

- Bounded-context ownership: Payment owns provider integration; Transfer owns orchestration; Ledger owns financial posting.
- Schema isolation: No cross-schema foreign keys or direct writes.
- Unknown-outcome safety: Timeout is unresolved, never definitive failure.
- Idempotency and auditability: Submission and outcome commands are durable and deduplicated.
