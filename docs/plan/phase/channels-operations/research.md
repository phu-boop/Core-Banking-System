# Phase 7 Spec-Level Research

## Decision: Keep Phase 7 provider-neutral

- **Status**: APPROVED boundary
- **Rationale**: API Gateway, event broker and notification provider remain OPEN. The implementation slice must preserve service-owned authorization and durable domain behavior without inventing their technology or delivery semantics.
- **Alternatives considered**:
  - Coupling Notification/Audit/Reporting to a named broker or provider - rejected.
  - Moving authorization only to a future gateway - rejected.
  - Making Reporting an authoritative balance source - rejected.

## Open clarification: notification provider

- **Decision**: Deferred.
- **Rationale**: Provider/recipient/PII contracts must be selected and approved before executable external delivery.
- **Plan impact**: Implement provider-independent Notification and delivery-attempt boundaries with mock/sandbox tests first.

## Open clarification: event broker

- **Decision**: Deferred.
- **Rationale**: Kafka vs RabbitMQ and broker-specific retry/dead-letter behavior remain unresolved.
- **Plan impact**: Implement durable outbox and consumer idempotency abstractions, but do not add a broker dependency in the first slice.

## Open clarification: API Gateway

- **Decision**: Deferred.
- **Rationale**: Gateway selection affects deployment routing only; it must not replace in-service authorization.
- **Plan impact**: Expose service-level REST contracts and test authorization locally before gateway selection.
