# Identity Service

Read-only identity mapping, principal resolution, and the shared Role/Permission Catalog. Keycloak owns interactive authentication, password management and token issuance. This service validates Keycloak access tokens, resolves a pre-provisioned `(issuer, subject)` mapping and returns its internal principal and effective scoped grants.

## Current scope

- `GET /v1/identity/me` returns `userId`, optional `partyId` and scoped grants.
- Only pre-provisioned mappings with status `ACTIVE` are accepted. Unknown and `DISABLED` mappings receive `403 Forbidden`.
- The shared catalog contains the ten default global roles and permission status metadata. No role is assigned to a default user.
- Only `account:read` is `ACTIVE`, and the published `RETAIL_CUSTOMER` version grants it with `SELF` scope. Other seeded versions remain `DRAFT` until their API enforcement is implemented and verified.
- Role-derived grants are resolved from active, unexpired assignments to published role versions. Expired, revoked, draft-version and non-`ACTIVE` permissions are not returned. Assignment rows are the grant provenance; multiple assignments may resolve to the same effective grant without overwriting one another.
- Existing `authorization_grant` rows are preserved. Legacy permission codes missing from the catalog are inserted as `PENDING_IMPLEMENTATION`, so they remain stored but are not returned as effective grants.
- There are no API endpoints to create, update or disable identity mappings, catalog entries, role assignments or grants. Privileged provisioning remains deferred until an audited operator workflow is approved.
- The runtime database role is read-only. Mapping and grant provisioning requires a separately approved privileged process; that process and its audit trail are not implemented here.
- This service does not store passwords, issue tokens, infer grants from client-supplied claims, or trust client-submitted organization/branch/permission context.
- Supported assignment scopes are `SELF`, `ORGANIZATION` and `BRANCH`. There is no `GLOBAL` scope; system-wide privileges are not assignable in this catalog version.
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

Flyway is disabled at application startup. `V1` creates the issuer/subject mapping and legacy scoped-grant tables; `V2` creates Identity resolution audit; `V3` adds the Permission/Role/Role Version/Role Assignment schema; `V4` seeds the shared catalog and default Role Versions. Do not edit or rerun V1/V2 on an existing database. V4 is idempotent and does not update role assignments or existing grants. The runtime role receives `SELECT` on the catalog and assignment tables, not write access or access to Flyway history/other schemas.

For an existing database, run Flyway with the `identity_service_migrator` credentials after taking the normal database backup. Do not run the one-time ownership bootstrap again. For a fresh database, run the ownership bootstrap once, then apply Flyway as the migrator. Do not use the migration role as the runtime credential. Do not provision production mappings or role assignments until a controlled, auditable provisioning process is approved.

## Build and tests

Run from the repository root:

```sh
./mvnw -f services/identity-service/pom.xml verify
```

PostgreSQL integration tests use Testcontainers and require Docker. They verify principal resolution, denied unknown/disabled identities, unique issuer/subject mapping, catalog seed idempotency, legacy-data preservation, Role Version integrity/immutability, assignment expiry/revocation/provenance, rejected scopes, restricted-permission filtering and runtime schema permissions. Unit tests cover issuer, audience, subject and token-time validation.

## Container image

Package the service, then build the image:

```sh
./mvnw -f services/identity-service/pom.xml package
docker build -t identity-service:local services/identity-service
```

The local Keycloak realm and Minikube deployment scaffolding are under `deploy/local/` and `deploy/kubernetes/local/`. They are development-only and do not replace production identity-provider decisions.
