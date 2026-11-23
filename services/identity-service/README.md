# Identity Service

Read-only identity mapping and principal resolution for Phase 2. Keycloak owns interactive authentication, password management and token issuance. This service validates Keycloak access tokens, resolves a pre-provisioned `(issuer, subject)` mapping and returns its internal principal and scoped grants.

## Current scope

- `GET /v1/identity/me` returns `userId`, optional `partyId` and scoped grants.
- Only pre-provisioned mappings with status `ACTIVE` are accepted. Unknown and `DISABLED` mappings receive `403 Forbidden`.
- There are no API endpoints to create, update or disable identity mappings or grants. Mutation endpoints remain deferred until a durable Audit write path is available.
- The runtime database role is read-only. Mapping and grant provisioning requires a separately approved privileged process; that process and its audit trail are not implemented here.
- This service does not store passwords, issue tokens, infer grants from client-supplied claims, or trust client-submitted organization/branch/permission context.
- The initial grant scope types are `SELF`, `ORGANIZATION` and `BRANCH`; the permission catalog and detailed resource authorization remain open.
- Account Service's initial self-service account-read slice consumes `account:read` with `scopeType=SELF` and `scopeId` equal to the mapped `partyId`. The service also compares that Party ID with the account owner. Other resource-service authorization paths are not yet migrated from their provisional JWT claim conventions; see [ADR-013](../../docs/adr/ADR-013-identity-authoritative-user-authorization.md).

## Configuration

When the service starts, Spring Boot automatically imports `services/identity-service/.env` when invoked from the repository root, or `.env` when invoked from the service directory. The file is optional and ignored by Git; operating-system environment variables and explicit Spring command-line arguments can still override its values. There is no need to `source` the file first.

Run from the repository root:

```sh
./mvnw -f services/identity-service/pom.xml spring-boot:run -Dspring-boot.run.profiles=local
```

All settings below are required at runtime. The `local` profile allows absolute HTTP or HTTPS OIDC issuer and JWKS URLs for local Keycloak; the default and `production` profiles require HTTPS. All profiles reject relative URLs and URLs containing credentials. Configure issuer and JWKS URLs for the same Keycloak realm; the audience must be present in issued access tokens.

Also set the database username/password in `.env` to match a provisioned database role; placeholder values are not usable credentials.

| Environment variable | Purpose |
|---|---|
| `KEYCLOAK_ISSUER_URI` | Exact expected `iss` claim for the realm |
| `KEYCLOAK_JWK_SET_URI` | HTTPS URL of the realm signing-key set |
| `IDENTITY_TOKEN_AUDIENCE` | Required audience for this service |
| `IDENTITY_DATASOURCE_URL` | PostgreSQL JDBC URL |
| `IDENTITY_DATASOURCE_USERNAME` | Read-only `identity_service_app` runtime role |
| `IDENTITY_DATASOURCE_PASSWORD` | Runtime role secret |

The service validates JWT signature, issuer, expiration, not-before, non-empty subject and configured audience. It does not expose Actuator endpoints other than health/liveness/readiness. Health endpoints are public; business routes require a bearer token.

## Database ownership

`database/bootstrap/identity-service-ownership.sql` creates the service migration and read-only runtime roles and the `identity_schema`. It is a one-time bootstrap for a fresh database and reads passwords from process environment variables:

- `IDENTITY_SERVICE_MIGRATOR_PASSWORD`
- `IDENTITY_SERVICE_APP_PASSWORD`

Apply migrations explicitly as `identity_service_migrator`:

```sh
./mvnw -f services/identity-service/pom.xml flyway:migrate
```

Flyway is disabled at application startup. `V1` creates the issuer/subject mapping and scoped-grant tables; `user_id` is UUID, `(issuer, subject)` is unique, and `party_id` is an optional logical reference with no cross-schema foreign key. The runtime role receives `SELECT` only on these domain tables, not on Flyway history or other schemas.

Do not use the migration role as the runtime credential. Do not provision production mappings until a controlled, auditable provisioning process is approved.

## Build and tests

Run from the repository root:

```sh
./mvnw -f services/identity-service/pom.xml verify
```

PostgreSQL integration tests use Testcontainers and require Docker. They verify principal resolution, denied unknown/disabled identities, unique issuer/subject mapping and runtime schema permissions. Unit tests cover issuer, audience, subject and token-time validation.

## Container image

Package the service, then build the image:

```sh
./mvnw -f services/identity-service/pom.xml package
docker build -t identity-service:local services/identity-service
```

The local Keycloak realm and Minikube deployment scaffolding are under `deploy/local/` and `deploy/kubernetes/local/`. They are development-only and do not replace production identity-provider decisions.
