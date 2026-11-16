# ADR-007: Configurable Corporate Approval

- **Status:** ACCEPTED
- **Date:** Not recorded; historical approval date was not supplied.

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
- Policy snapshot/version behavior while a request is pending remains OPEN.
- Limits, approval thresholds, rejection, expiry, delegation and resubmission rules are not determined here.

## Dependencies

- ADR-002 includes Corporate Banking.
- ADR-003 defines Admin/Teller channels, but channel permissions remain separately designed.
- See [Open Decisions](../architecture/OPEN-DECISIONS.md) for policy snapshot behavior.
