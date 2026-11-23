# Interbank Transfer Research

## Decisions

### Scope decision

- **Decision**: Implement only a provider-neutral Payment boundary and unresolved outcome state.
- **Rationale**: ADR-006 and the repository business rules explicitly leave provider, protocol and settlement OPEN.
- **Alternatives considered**: Mock-only provider implementation and immediate Ledger integration were rejected as inferred business behavior.

### Outcome safety

- **Decision**: Timeout remains unresolved; failure and settlement require verified outcome evidence.
- **Rationale**: An unavailable provider response cannot prove either success or failure.
- **Alternatives considered**: Treat timeout as failure or settled was rejected.

## Unresolved Clarifications

- External network/provider/protocol.
- Settlement finality and Ledger posting/reversal timing.
- Callback/query/reconciliation mechanism and timeout policy.
