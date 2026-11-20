# Feature Specification: Corporate Approval Core

**Feature Branch**: `phase-2-corporate-approval`
**Created**: 2026-11-20
**Status**: Approved behavior baseline — 2026-11-20
**Input**: User-approved Corporate Approval MVP defaults and existing ADR-007 / Business Rules

## Scope Baseline

- **Discovery method**: Reviewed the accepted Corporate Approval decision, business rules, Phase 2 Decision Gate, API contracts, ERD, and current Corporate service scope.
- **Total items discovered**: 1 approval capability.
- **Items in scope**: 1 — Corporate policy and approval workflow core.
- **Out of scope**: Transfer execution, Ledger posting, Payment settlement, and any cross-service financial side effect.

The policy-version, policy management, approver assignment, operation reference, sequence, rejection, expiry, delegation, resubmission, Maker–Checker, and history decisions below were explicitly accepted by the project owner on 2026-11-20.

## User Scenarios & Testing

### User Story 1 — Configure organization approval policy (Priority: P1)

An authorized organization policy administrator configures a policy with multiple ordered approval levels so that requests receive the intended level of control.

**Why this priority**: Configurable multi-level approval is the foundation of the accepted Corporate approval requirement.

**Independent Test**: Configure a policy with at least two levels and verify that it can be selected for a request without creating or executing a financial transaction.

**Acceptance Scenarios**:

1. **Given** an organization has a principal with the organization-scoped `approval-policy:manage` permission, **When** that principal creates and explicitly activates a policy, **Then** the policy is associated with that organization and its levels have a deterministic order.
2. **Given** a policy version is used by a pending request, **When** a later policy version is created, **Then** the pending request continues to use its original version.
3. **Given** a policy exists for an operation type, **When** a replacement version is activated, **Then** no more than one version for that organization and operation type is active.

### User Story 2 — Submit and progress an approval request (Priority: P1)

An organization member submits an eligible business operation for approval and authorized checkers process each required level in order.

**Why this priority**: A policy provides no control unless an approval request can be evaluated against it.

**Independent Test**: Submit a synthetic request against a two-level policy and verify that the second level cannot complete before the first, and the request is approved only after every required level approves.

**Acceptance Scenarios**:

1. **Given** an eligible request and a policy with multiple levels, **When** the request is submitted, **Then** it is bound to the policy version selected at submission.
2. **Given** a request with an unresolved earlier level, **When** an actor attempts to decide a later level, **Then** the later decision is not accepted.
3. **Given** all required levels have approved, **When** the final decision is recorded, **Then** the request reaches the approved outcome without posting to Ledger or settling a transfer.
4. **Given** a request is rejected at any level, **When** another decision is attempted on that request, **Then** the rejected request remains terminal.

### User Story 3 — Preserve Maker–Checker and approval history (Priority: P1)

An authorized checker makes an independent decision, while the organization can later verify who decided each level and which policy version applied.

**Why this priority**: Independent approval and an auditable decision trail are mandatory controls.

**Independent Test**: Attempt self-approval, approve with a distinct eligible checker, and verify the resulting history.

**Acceptance Scenarios**:

1. **Given** a request was created by a maker, **When** that same maker attempts to approve it, **Then** Corporate rejects the decision.
2. **Given** an authorized checker records a decision, **When** the approval history is read by an authorized caller, **Then** it identifies the actor, level, decision, timestamp, and applied policy version.
3. **Given** a rejected request is resubmitted, **When** the new request is created, **Then** it has a new request ID and the prior request/history remains unchanged.

### Edge Cases

- A policy version changes while a request is pending: the request retains its version selected at submission.
- A decision is attempted out of sequence: reject the attempt without advancing the request.
- The maker attempts to approve their own request: reject the attempt and retain the audit outcome as required by the approved audit contract.
- A rejected request is retried: it remains rejected; resubmission creates a new request.
- Expiry and delegation are not available in this MVP; do not silently add either transition.
- A checker is eligible only when the policy level's role/mandate scope matches the checker's active Corporate Membership and Mandate at decision time.
- A request carries an opaque operation type/reference supplied by the owning service. Corporate records decisions but does not execute that operation.

## Requirements

### Functional Requirements

- **REQ-001**: Corporate MUST support organization-specific configurable policies with multiple ordered approval levels. Policy management MUST require an organization-scoped `approval-policy:manage` permission; policies MUST be explicitly activated, and at most one version per organization and operation type may be active.
- **REQ-002**: Each approval request MUST be bound to the immutable policy version selected when the request is submitted; later policy versions MUST NOT change that request's approval rules.
- **REQ-003**: Approval levels MUST be processed sequentially, and the request MUST become approved only after every required level has approved.
- **REQ-004**: The maker MUST NOT approve their own request.
- **REQ-005**: A rejection at any level MUST terminate that request as rejected; additional decisions MUST NOT change its outcome.
- **REQ-006**: The MVP MUST NOT support approval expiry or delegation.
- **REQ-007**: Resubmission after rejection MUST create a new request ID and MUST preserve the rejected request and its history.
- **REQ-008**: Corporate MUST retain append-only decision history that identifies the actor, approval level, decision, timestamp, and applied policy version.
- **REQ-009**: Corporate MUST validate organization and approver authority using trusted owner-side context; each approval level MUST define a role/mandate scope, and the checker MUST have an active matching Membership and Mandate at decision time. Caller-supplied role, membership, mandate, or scope values MUST NOT grant authority.
- **REQ-010**: An approval request MUST include an opaque operation type and reference supplied by the owning service; Corporate MUST NOT execute the referenced operation, post Ledger entries, or settle payments.

### Key Entities

- **Approval Policy Version**: An organization-scoped set of ordered approval levels that can be selected for a request.
- **Approval Request**: A request for an eligible business operation, bound to one policy version and progressing through its required levels.
- **Approval Decision**: An approve/reject action by an eligible checker at a specific level.
- **Approval History**: Append-only record of request decisions and applied policy identity.

## Success Criteria

### Measurable Outcomes

- **SC-001**: 100% of pending requests retain the exact policy version selected at submission after a newer version is created.
- **SC-002**: 100% of requests with multiple levels reject out-of-order decisions and reach approved only after all required levels approve.
- **SC-003**: 100% of self-approval attempts are rejected.
- **SC-004**: 100% of rejected-request resubmissions receive a new request ID while the prior request and history remain unchanged.
- **SC-005**: 100% of completed decisions have a corresponding history record containing actor, level, decision, timestamp, and policy version.
- **SC-006**: No Approval API in this feature directly executes a transfer, posts a Ledger entry, or settles a payment.

## Assumptions

- Approval data and final approval authorization are owned by Corporate.
- The MVP is an API/service capability; no user interface is specified.
- The event transport remains undecided and is not required for this feature.
- Corporate Membership/Mandate implementation or owner interfaces must exist before approver-eligibility tasks can proceed.
- Exact Keycloak claim mapping, error envelope, route naming, and technical request/response fields are settled in the API contract, not here.
- No standalone project constitution was found; architecture and business-rule constraints are taken from the existing accepted ADRs and Phase 2 documents.
