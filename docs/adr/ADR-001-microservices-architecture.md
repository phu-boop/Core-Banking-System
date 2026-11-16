# ADR-001: Microservices Architecture

- **Status:** ACCEPTED
- **Date:** Not recorded; historical approval date was not supplied.

## Context

Banking Clone targets Retail and Corporate Banking, multiple channels, and multiple distinct business contexts. The project owner confirmed a microservices architecture.

## Decision

Use **Microservices Architecture**. The domain service boundaries in [BOUNDED_CONTEXTS.md](../domain/BOUNDED_CONTEXTS.md) are proposed decomposition and are not evidence that services exist or that every internal design detail is approved.

## Alternatives

- Modular monolith.
- Single application with shared domain/data model.

## Consequences

- Services need explicit ownership and contracts.
- Independent data ownership and failure/retry behavior must be designed.
- Operational complexity, distributed workflows and observability increase.
- The decision does not select runtime, service count beyond proposed contexts, broker, gateway or deployment platform.

## Dependencies

- ADR-002, ADR-003 define business audiences and channels.
- ADR-004 defines PostgreSQL/schema ownership.
- Service boundaries and communication protocol remain subject to design review.
