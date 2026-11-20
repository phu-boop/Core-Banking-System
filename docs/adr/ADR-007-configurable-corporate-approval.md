# ADR-007: Configurable Corporate Approval

- **Status:** ACCEPTED
- **Date:** Original decision date not recorded; MVP behavior update approved 2026-11-20.

## Context

Corporate users may require controlled authorization for financial operations, with policies varying by organization or operation.

## Decision

Corporate approval is **configurable**, supports **multiple approval levels**, uses **Maker–Checker**, and retains **approval history**.

## Alternatives

- No corporate approval.
- One fixed approval level.
- Fixed approval rules not configurable by policy.

## Consequences

- Corporate organization, membership, mandate, policy, approval steps and history need explicit ownership.
- Approval must be enforced by the service handling the business action, not only at an API gateway.
- An approval request is bound to the immutable policy version selected when it is submitted; later policy versions do not change pending requests.
- Multi-level steps run sequentially, and every required level must approve for the request to be approved.
- A rejection is terminal for that request. A resubmission creates a new request ID and retains the prior request/history.
- The maker cannot approve their own request.
- Expiry and delegation are not supported in the MVP.
- Policy administration uses organization-scoped `approval-policy:manage`; policies are explicitly activated, with at most one active version per organization and operation type.
- Each approval level declares a role/mandate scope; Corporate checks active Membership and Mandate at decision time.
- Approval requests carry an opaque operation type/reference supplied by the owning service. Corporate records decisions but does not execute the referenced operation.
- Detailed thresholds, role/mandate catalogs, Membership/Mandate API contracts, and exact Keycloak claim mapping remain OPEN.

## Dependencies

- ADR-002 includes Corporate Banking.
- ADR-003 defines Admin/Teller channels, but channel permissions remain separately designed.
- See [Open Decisions](../architecture/OPEN-DECISIONS.md) for remaining approval thresholds, role/mandate catalog, and integration details.
