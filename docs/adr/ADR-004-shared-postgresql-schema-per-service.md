# ADR-004: Shared PostgreSQL, Schema-per-service

- **Status:** ACCEPTED
- **Date:** Not recorded; historical approval date was not supplied.

## Context

Services require independent data ownership while using a shared PostgreSQL platform.

## Decision

Use **Shared PostgreSQL with schema-per-service**. Each service owns and writes only its schema. Services must not access one another's tables directly, and no cross-schema foreign keys may be created.

## Alternatives

- Separate database deployment per service.
- Shared tables/schema across services.
- Shared PostgreSQL with separate schemas but direct cross-service table access.

## Consequences

- Database deployment is shared, but ownership and write boundaries remain per service.
- Cross-service references use IDs and service APIs/events, not cross-schema constraints.
- Backup, restore, permissions, capacity and schema migration coordination need operational design.
- This ADR does not choose PostgreSQL version or migration tooling.

## Dependencies

- ADR-001 establishes service boundaries.
- Logical schema mapping is proposed in [ERD.md](../database/ERD.md).
- Data retention, access grants and deployment operations remain to be designed.
