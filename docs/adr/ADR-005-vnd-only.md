# ADR-005: VND Only

- **Status:** ACCEPTED
- **Date:** Not recorded; historical approval date was not supplied.

## Context

MVP currency scope needs to be explicit to avoid unintended FX and multi-currency behavior.

## Decision

The MVP supports **VND only**. FX is not part of MVP.

## Alternatives

- Multi-currency from MVP.
- Add currencies incrementally.

## Consequences

- MVP financial operations use VND.
- No FX conversion workflow is in scope.
- Numeric precision/scale, rounding rules, display formatting and future multi-currency migration are not decided by this ADR.

## Dependencies

- ADR-006 transfer scope applies to VND operations.
- Ledger and API design must preserve exact decimal values.
- See [Business Rules](../domain/BUSINESS-RULES.md) for mandatory `BigDecimal` / `NUMERIC` design rules.
