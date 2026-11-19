# ADR-003: Multi-channel Banking

- **Status:** ACCEPTED
- **Date:** Not recorded; historical approval date was not supplied.

## Context

Banking services are used by customers and staff through different channels with different permissions.

## Decision

Support **Internet Banking, Admin and Teller** channels.

## Alternatives

- Single channel.
- Internet Banking only.
- Staff-only channels.

## Consequences

- Channel context must be available for authorization and audit.
- Teller access must be constrained by branch permissions (see ADR-009).
- The decision does not specify frontend applications, API gateway, login flows or channel-specific feature parity.

## Dependencies

- ADR-002 establishes Retail and Corporate audiences.
- ADR-009 establishes multi-branch scope.
- The initial Identity Provider and protocol are defined in [ADR-012](./ADR-012-phase-2-identity-party-corporate-branch.md); API Gateway technology remains OPEN.
