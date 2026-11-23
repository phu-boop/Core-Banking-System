# Feature Specification: Channels, Audit, Notification and Reporting

**Feature Branch**: `phase-7-channels-operations`
**Created**: 2026-11-22
**Status**: Draft
**Input**: Phase 7 - Internet Banking, Admin, Notification, Audit and Reporting

## Scope Baseline

- **Discovery method**: Reviewed ADR-003, existing Phase 7 roadmap, API contracts, event contracts, bounded contexts, open decisions and the implemented Identity, Corporate, Account, Ledger, Transfer and Payment boundaries.
- **Items in scope**:
  - Channel-aware authorization for Internet Banking, Admin and Teller.
  - Organization, account, branch and resource scope enforcement in owning services.
  - Audit record creation and audit record retrieval with explicit read authorization.
  - Notification requests, durable delivery attempts and delivery status.
  - Reporting projections and bounded exports that do not become authoritative financial records.
- **Items deferred**:
  - API Gateway product selection.
  - Event broker selection and broker-specific delivery, partitioning and dead-letter behavior.
  - Frontend implementation and channel-specific UI design.
  - Notification provider and PII/retention policy selection.
  - Production reporting data volume and retention targets.

## User Scenarios & Testing

### User Story 1 - Internet Banking Customer Operates Within Resource Scope (Priority: P1)

An authenticated customer uses the Internet Banking channel to view permitted accounts and transactions and submit permitted transfer operations.

**Why this priority**: Customer-facing resource access is the primary channel and must not expose other customers' data.

**Independent Test**: A customer can access owned resources and receives a denied result for another customer's resource.

**Acceptance Scenarios**:

1. **Given** an authenticated Internet Banking customer, **When** the customer requests an owned account or transaction, **Then** the owning service returns it.
2. **Given** an authenticated Internet Banking customer, **When** the customer requests a resource outside account or organization ownership, **Then** access is denied.
3. **Given** a customer request, **When** channel and resource scope conflict, **Then** the least-privileged applicable scope is enforced.

### User Story 2 - Teller Operates Within Branch Scope (Priority: P1)

A Teller performs permitted account and transfer operations only in the assigned transaction-processing branch.

**Why this priority**: Branch confinement is required for staff channel safety and auditability.

**Independent Test**: The same Teller request succeeds inside the authorized branch and is denied outside that branch.

**Acceptance Scenarios**:

1. **Given** an authenticated Teller with branch permission, **When** the Teller operates in an assigned branch, **Then** the operation is authorized.
2. **Given** an assigned Teller and an out-of-scope branch, **When** the Teller requests an operation, **Then** the request is denied.
3. **Given** a Teller operation, **When** it is executed or denied, **Then** channel, branch and actor context are recorded in audit evidence.

### User Story 3 - Admin Performs Explicitly Authorized Administration (Priority: P1)

An authenticated Administrator performs only explicitly permitted configuration or investigation actions.

**Why this priority**: Administrative access must be separate from ordinary business operations and must remain auditable.

**Independent Test**: A permitted Admin action succeeds once and an action outside the Admin's explicit grant is denied.

**Acceptance Scenarios**:

1. **Given** an Admin with explicit organization or system permission, **When** a permitted administration operation is submitted, **Then** it is accepted.
2. **Given** an Admin with no matching permission, **When** an administration operation is submitted, **Then** it is denied.
3. **Given** an administrative read, **When** organization or branch filters are used, **Then** the result contains only permitted scope.

### User Story 4 - Sensitive Operations Produce Durable Audit Records (Priority: P1)

The system records sensitive channel actions and financial state changes with actor, action, resource, result, correlation and timestamp.

**Why this priority**: Audit evidence must exist independently of UI or reporting layers.

**Independent Test**: Repeated delivery of the same audit command produces one durable audit record.

**Acceptance Scenarios**:

1. **Given** a sensitive operation, **When** success or denial is known, **Then** an audit record is durably recorded before success is reported.
2. **Given** the same audit command delivered twice, **When** deduplication is applied, **Then** one audit record exists.
3. **Given** an audit record, **When** a secret or protected PII field is present, **Then** it is omitted or redacted according to the audit contract.

### User Story 5 - Notifications Are Requested, Attempted and Observed (Priority: P2)

The system converts approved domain events into notification requests and tracks delivery attempts without changing financial state.

**Why this priority**: Customers and staff need communication, while notifications must remain non-authoritative.

**Independent Test**: Duplicate notification requests create one delivery workflow and repeated delivery outcomes do not duplicate notifications.

**Acceptance Scenarios**:

1. **Given** an eligible domain event, **When** a notification request is created, **Then** recipient, template, channel and correlation reference are recorded.
2. **Given** a notification delivery attempt, **When** it succeeds or fails, **Then** attempt status is recorded once and remains queryable.
3. **Given** a notification failure, **When** retry eligibility applies, **Then** retry does not change financial state or create a duplicate side effect.

### User Story 6 - Reporting Provides Scoped Projections (Priority: P2)

Authorized users generate account, transaction, audit or operational reports from projections and receive a clear source watermark.

**Why this priority**: Reporting adds operational and customer value without becoming a balance or Ledger authority.

**Independent Test**: An authorized report succeeds within the allowed range, and the same request outside scope or beyond limits is denied.

**Acceptance Scenarios**:

1. **Given** an authorized report request, **When** valid filters are supplied, **Then** only permitted rows are returned.
2. **Given** a report request outside organization, account, branch or channel scope, **Then** it is denied.
3. **Given** a reporting projection, **When** source data changes, **Then** the response or export records its generation time and source watermark.

### Edge Cases

- Audit write failure must prevent the protected operation from reporting success when durable audit is mandatory.
- Gateway availability must not become the only source of authorization decisions.
- Duplicate notification delivery must be harmless.
- A stale or out-of-order reporting projection must not return Ledger balances as authoritative values.
- Deleted or disabled users must lose channel access without transfer or Ledger bypasses.
- A cross-channel request must require all applicable channel, organization, account and branch scopes.

## Requirements

### Functional Requirements

- **REQ-001**: The system MUST carry channel context for Internet Banking, Admin and Teller operations.
- **REQ-002**: Internet Banking MUST enforce account and organization ownership before returning customer data or accepting a transfer.
- **REQ-003**: Teller operations MUST enforce the transaction-processing branch and applicable Teller permissions.
- **REQ-004**: Admin operations MUST require explicit administration permission and must remain inside organization or system scope.
- **REQ-005**: Owning services MUST enforce channel, organization, account and branch authorization rather than relying only on a gateway.
- **REQ-006**: The system MUST durably record sensitive operations with actor, action, resource, result, correlation ID and timestamp.
- **REQ-007**: Audit record ingestion MUST deduplicate repeated commands and must redact secrets and unauthorized PII.
- **REQ-008**: Audit reads MUST require explicit audit-read permission and enforce organization and branch scope.
- **REQ-009**: Notification requests MUST be durable, correlated and deduplicated by stable notification identity.
- **REQ-010**: Notification delivery attempts MUST preserve attempted, successful or failed status without changing financial state.
- **REQ-011**: Reporting MUST read from explicitly defined projections and MUST NOT become an authoritative account balance or Ledger source.
- **REQ-012**: Reporting filters MUST enforce allowlists, date boundaries, pagination limits and caller scope.
- **REQ-013**: Export jobs MUST reject unsupported formats or ranges and MUST protect duplicate export submissions.
- **REQ-014**: All cross-service event consumers MUST tolerate at-least-once delivery and deduplicate events before side effects.
- **REQ-015**: Channel, notification, audit and reporting implementations MUST remain provider-neutral until OPEN decisions are approved.

## Key Entities

- **Channel Context**: Internet Banking, Admin or Teller identity plus applicable organization and branch scope.
- **Audit Record**: Actor, action, resource, result, correlation, timestamp and redaction metadata.
- **Notification**: Stable notification identity, recipient reference, template reference, channel and delivery state.
- **Notification Attempt**: One delivery attempt with provider-independent status and correlation.
- **Report Projection**: Scoped read model derived from authoritative domain changes and carrying a source watermark.
- **Export Job**: Bounded report request, format, status and deduplicated job identity.

## Success Criteria

### Measurable Outcomes

- **SC-001**: 100% of tested cross-customer, cross-organization and cross-branch access attempts are denied outside permitted scope.
- **SC-002**: 100% of sensitive operations produce one durable audit record or are reported unsuccessful.
- **SC-003**: 100% of duplicate audit and notification commands produce no duplicate record or delivery workflow side effect.
- **SC-004**: 100% of tested report responses expose only caller-permitted rows and include a generation time and source watermark.
- **SC-005**: 100% of notification events or failures remain unable to alter account balance, Transfer status or Ledger state.
- **SC-006**: 100% of invalid report ranges and unsupported export formats are rejected before producing a report or export.

## Assumptions

- REST remains the first-slice synchronous API convention.
- Keycloak remains the Identity Provider and service authorization remains in owning services.
- Internet Banking, Admin and Teller are channel contexts, not a replacement for resource-level authorization.
- No frontend application is required in this implementation slice.
- Event broker selection remains OPEN; notification and audit contracts describe domain behavior rather than broker-specific transport.
- Reporting data is derived from authoritative services and is never used to post or reverse financial state.

## NEEDS CLARIFICATION

- [NEEDS CLARIFICATION: Which notification channel or provider is approved for the first executable delivery?]
- [NEEDS CLARIFICATION: Which event broker is approved for Notification, Audit and Reporting delivery?]
- [NEEDS CLARIFICATION: Which API Gateway technology is approved for external routing and TLS?]

## Requirements Quality Checklist

- [x] All requirements use REQ-XXX format.
- [x] IDs are unique and sequential.
- [x] Acceptance scenarios use Given-When-Then form.
- [x] Scope baseline covers channels, audit, notification and reporting.
- [x] Functional requirements cover ownership and authorization.
- [x] Success criteria are measurable.
- [x] Open provider and transport decisions are not inferred.
