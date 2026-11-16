# ADR-002: Retail + Corporate Banking

- **Status:** ACCEPTED
- **Date:** Not recorded; historical approval date was not supplied.

## Context

The system must model both individual-customer and business-customer banking needs.

## Decision

Support **Retail Banking and Corporate Banking**.

## Alternatives

- Retail-only scope.
- Corporate-only scope.
- Add one segment only in a later phase.

## Consequences

- Customer/party and organization concepts must be distinguished.
- Corporate membership, mandate and configurable approval needs are in scope.
- Organization-level authorization is required in the design.
- Specific customer types, legal profiles, onboarding checks and product rules are not defined by this ADR.

## Dependencies

- ADR-001 establishes the architecture style.
- ADR-007 defines configurable corporate approval.
- Customer/organization model details remain proposed in [BOUNDED_CONTEXTS.md](../domain/BOUNDED_CONTEXTS.md).
