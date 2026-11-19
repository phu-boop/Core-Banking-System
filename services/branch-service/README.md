# Branch Service

Branch master-data service for Phase 2. Branch IDs are logical references; this service does not manage customer assignments, Teller credentials, or transaction-processing branches.

## Endpoints

- `POST /v1/branches` creates an `ACTIVE` branch; requires `branch:admin`.
- `GET /v1/branches/{branchId}` returns a branch visible to the caller.
- `GET /v1/branches` returns a status-filterable page for the caller's visible branches.
- Codes are trimmed, uppercased, and unique without case sensitivity. Names are trimmed.
- List defaults are `page=0`, `size=20`; size is limited to 1–100.
- Hidden branch resources return `404` to avoid revealing their existence.

## Authorization claim boundary

All business endpoints require a Keycloak-issued OIDC access token validated by issuer, audience, signature, and token-time checks. Permission claims are mapped from the validated JWT only; request-body roles/scopes are never accepted.

The current first-slice claim convention is provisional because the full permission vocabulary and Keycloak claim mapping remain OPEN:

- `branch_permissions`: global permission strings such as `branch:admin` and `branch:read`.
- `branch_grants`: scoped grant objects with `permission`, `scopeType: "BRANCH"` and `scopeId` (branch UUID).

A scoped `branch:read` grant limits both single-branch reads and list results. Branch creation requires global `branch:admin`.

## Configuration and database

| Environment variable | Purpose |
|---|---|
| `KEYCLOAK_ISSUER_URI` | Expected OIDC issuer |
| `KEYCLOAK_JWK_SET_URI` | HTTPS JWKS endpoint |
| `BRANCH_TOKEN_AUDIENCE` | Required access-token audience |
| `BRANCH_DATASOURCE_URL` | PostgreSQL JDBC URL |
| `BRANCH_DATASOURCE_USERNAME` | `branch_service_app` runtime role |
| `BRANCH_DATASOURCE_PASSWORD` | Runtime role secret |

Use `database/bootstrap/branch-service-ownership.sql` to create the service roles/schema, then apply Flyway as `branch_service_migrator`. Application startup does not run migrations. The runtime role has read/insert permission only on owned domain and audit tables and cannot access Flyway history or other schemas.

## Build and tests

```sh
./mvnw -f services/branch-service/pom.xml verify
```
