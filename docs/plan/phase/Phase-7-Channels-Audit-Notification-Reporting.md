# Phase 7 - Channels, Audit, Notification and Reporting

## Status

- **Current status:** Active — partially implemented.
- **Completed:** centralized Springdoc/OpenAPI documentation and `api-docs-service`.
- **Completed foundation:** provider-neutral Audit service, migration and unit tests.
- **Completed in current batch:** Account/Transfer channel and owner authorization; Audit PostgreSQL Testcontainers.
- **Completed:** provider-neutral Notification service with delivery attempts and mock adapter.
- **Not completed:** channel-specific owner authorization, Audit PostgreSQL integration tests, Notification service and Reporting service.
- **Detailed implementation plan:** [Channels, Audit, Notification and Reporting plan](./channels-operations/plan.md).
- **Feature specification:** [Phase 7 feature spec](../../specs/phase-7-channels-operations/feature-spec.md).

## Scope

- Internet Banking, Admin and Teller are channel contexts.
- Owning services enforce organization, account, branch, channel and resource scope.
- Audit owns durable audit records and scoped audit reads.
- Notification owns notification requests and delivery attempt state.
- Reporting owns projections and bounded export jobs.
- Reporting never becomes the authoritative source for account balances or Ledger state.
- API Gateway, event broker and notification provider selections remain OPEN.
- No frontend application or public production routing is included in this phase.

## API

### Existing Audit API

- `POST /internal/audit/records`
- `GET /v1/audit/records`

### Central API Documentation

- `GET /swagger-ui/index.html`
- `GET /v3/api-docs`
- `GET /specs/{serviceName}`

### Planned Notification API

- Notification submit/status routes remain under the detailed implementation plan and are not implemented yet.
- Notification does not change financial state.

### Planned Reporting API

- Reporting query/export routes remain under the detailed implementation plan and are not implemented yet.
- Reporting reads projections and must expose a source watermark.

## Acceptance

- Internet Banking requests can access only owned or permitted resources.
- Teller requests are limited to the applicable processing branch.
- Admin requests require explicit administration permission.
- Authorization is enforced in the owning service, not only at a gateway.
- Sensitive operations have one durable audit record or report failure before success.
- Duplicate audit and notification commands do not create duplicate side effects.
- Notification delivery status cannot alter Transfer, Account, Ledger or Payment state.
- Reporting filters are allowlisted and scope-constrained.
- Reporting cannot return Ledger balances as an authoritative projection.
- Workspace build and relevant tests pass.

## Deferred Work

- API Gateway technology.
- Kafka vs RabbitMQ.
- Notification provider/channel.
- PII and audit/notification/reporting retention policy.
- Production Kubernetes provider/topology and public Swagger exposure.
