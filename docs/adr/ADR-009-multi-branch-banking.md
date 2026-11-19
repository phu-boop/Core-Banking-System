# ADR-009: Multi-branch Banking

- **Status:** ACCEPTED
- **Date:** Not recorded; historical approval date was not supplied.

## Context

The system must support operations across more than one bank branch.

## Decision

Support **Multi-branch Banking**. Managing branch and transaction processing branch are distinct concepts. Teller access is restricted by branch permissions.

## Alternatives

- Single branch only.
- Defer branch scope.
- Treat all staff as having global access.

## Consequences

- Branch is an owned domain context and other services reference branch IDs.
- Authorization and audit need branch scope where applicable.
- A customer has one primary managing branch at a time; it is distinct from the transaction-processing branch. Reassignment is explicit and audited.
- Detailed reassignment workflow/effective-date rules and cross-branch operations remain OPEN. See [ADR-012](./ADR-012-phase-2-identity-party-corporate-branch.md).

## Dependencies

- ADR-003 includes Teller channel.
- ADR-004 prohibits cross-schema table access/FKs.
- See [Open Decisions](../architecture/OPEN-DECISIONS.md) for remaining branch workflow details.
