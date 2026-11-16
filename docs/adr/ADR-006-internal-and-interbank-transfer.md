# ADR-006: Internal + Interbank Transfer

- **Status:** ACCEPTED
- **Date:** Not recorded; historical approval date was not supplied.

## Context

Transfer scope must include both transfers within Banking Clone and transfers across an external interbank boundary.

## Decision

Support **Internal Transfer and Interbank Transfer**.

## Alternatives

- Internal transfers only.
- Interbank transfers only.
- Defer interbank support.

## Consequences

- Transfer orchestration and the external Payment integration boundary are required in the design.
- Timeout/outcome-unknown must be distinct from confirmed failure.
- Provider, network, protocol, settlement, reversal and reconciliation are not selected by this ADR.

## Dependencies

- ADR-005 constrains MVP currency to VND.
- ADR-004 constrains cross-service data ownership.
- External network/provider and settlement rules remain OPEN in [OPEN-DECISIONS.md](../architecture/OPEN-DECISIONS.md).
