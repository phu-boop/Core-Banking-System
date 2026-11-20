# Corporate Service

Corporate Organization first slice for Phase 2.

## Scope

- `POST /v1/organizations` accepts `partyId`, asks the Party owner API to validate it, and creates an `ACTIVE` Organization.
- `GET /v1/organizations/{organizationId}` returns an Organization visible to the caller.
- `corporate_schema.organization.party_id` is unique; it is a logical reference and has no cross-schema foreign key.
- Organization creation and its audit record commit in one database transaction.
- Legal profile data, Membership, Mandate, ApprovalPolicy, approval history and approval execution are not included.

## Authorization

Business requests require an OIDC access token validated against the configured issuer, JWKS and audience. This first slice reads authorization from validated claims:

- `corporate_permissions`: global strings `organization:create` and `organization:read`.
- `corporate_grants`: organization-scoped grants with `permission`, `scopeType: "ORGANIZATION"` and `scopeId`.

These claim names and permission vocabulary are provisional pending the full Identity/Keycloak permission decision. A denied organization read returns `404` to avoid disclosing resource existence.

## Party owner API

Corporate calls `GET /v1/customers/{partyId}` using a Keycloak Client Credentials token. The client must be configured so that:

- the token audience includes `PARTY_TOKEN_AUDIENCE`;
- the validated token provides `party:read` to Party Service (for example via the configured `party_permissions` claim);
- the `party.read` client scope and client permission are assigned in Keycloak.

Configure these values through secret/environment management; never put client secrets in source:

| Environment variable | Purpose |
|---|---|
| `KEYCLOAK_ISSUER_URI` | Expected inbound token issuer |
| `KEYCLOAK_JWK_SET_URI` | HTTPS JWKS endpoint |
| `CORPORATE_TOKEN_AUDIENCE` | Corporate API audience |
| `KEYCLOAK_TOKEN_URI` | HTTPS OAuth token endpoint |
| `CORPORATE_PARTY_CLIENT_ID` | Service identity client ID |
| `CORPORATE_PARTY_CLIENT_SECRET` | Service identity secret |
| `CORPORATE_PARTY_SCOPE` | Requested client scope; defaults to `party.read` |
| `PARTY_SERVICE_BASE_URL` | Party Service base URL |
| `CORPORATE_DATASOURCE_URL` | PostgreSQL JDBC URL |
| `CORPORATE_DATASOURCE_USERNAME` | `corporate_service_app` runtime role |
| `CORPORATE_DATASOURCE_PASSWORD` | Runtime role secret |

If Party returns `404`, the Organization request is rejected. Token, connectivity, or Party API failures return `503`; no Organization is created.

## Database

Use `database/bootstrap/corporate-service-ownership.sql` to create service roles and `corporate_schema`, then run Flyway as `corporate_service_migrator`. Application startup does not run migrations. The runtime role can read/insert owned Organization and audit rows but cannot access migration history or other schemas.

## Build and tests

```sh
./mvnw -f services/corporate-service/pom.xml verify
```
