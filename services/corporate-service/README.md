# Corporate Service

Corporate Organization and policy-administration slices for Phase 2.

## Scope

- `POST /v1/organizations` accepts `partyId`, asks the Party owner API to validate it, and creates an `ACTIVE` Organization.
- `GET /v1/organizations/{organizationId}` returns an Organization visible to the caller.
- Organization-scoped policy administrators can create and list versioned approval policies and explicitly activate a draft or inactive version.
- Only one policy version per organization/operation type can be active; policy version fields and approval levels are immutable to the runtime role.
- Policy creation and activation write an organization audit record atomically.
- `corporate_schema.organization.party_id` is unique; it is a logical reference and has no cross-schema foreign key.
- Organization creation and its audit record commit in one database transaction.
- V2 creates Corporate-owned policy, request, step and decision-history tables.
- V3 creates the 24-hour decision idempotency table.
- V4 creates Corporate Membership and Mandate tables.
- Request submission and ordered decision execution are implemented. Approval decisions validate active Membership and Mandate records in Corporate against the current level.
- Legal profile data and financial execution are not implemented.
- The approved Corporate Approval behavior baseline is documented in [ADR-007](../../docs/adr/ADR-007-configurable-corporate-approval.md); this slice implements policy administration only.

## Authorization

Business requests require an OIDC access token validated against the configured issuer, JWKS and audience. This first slice reads authorization from validated claims:

- `corporate_permissions`: global strings `organization:create` and `organization:read`.
- `corporate_grants`: organization-scoped grants with `permission`, `scopeType: "ORGANIZATION"` and `scopeId`.

These claim names and permission vocabulary are provisional pending the full Identity/Keycloak permission decision. A denied organization read returns `404` to avoid disclosing resource existence.

Policy management requires the organization-scoped `approval-policy:manage` permission, represented through the same provisional `corporate_grants` shape. Approval decisions must additionally validate an active Membership and Mandate for the level's role/mandate scope; those owner interfaces are not yet implemented and are a prerequisite for production decision APIs.

Policy endpoints:

- `POST /v1/organizations/{organizationId}/approval-policies` creates a draft version with one or more ordered levels.
- `GET /v1/organizations/{organizationId}/approval-policies?operationType=...` lists policies for the organization, optionally filtered by opaque operation type.
- `POST /v1/organizations/{organizationId}/approval-policies/{policyId}/activate` activates a draft or inactive version and deactivates the currently active version for that operation type.

Approval workflow endpoints:

- `POST /v1/organizations/{organizationId}/approval-requests` creates a pending request pinned to the active policy for the opaque operation type.
- `GET /v1/organizations/{organizationId}/approval-requests/{approvalId}` reads a request and its current step.
- `POST /v1/organizations/{organizationId}/approval-requests/{approvalId}/decisions` approves or rejects the current step. Send `Idempotency-Key` as a header or body field.
- `GET /v1/organizations/{organizationId}/approval-requests/{approvalId}/history` reads append-only decision history.

The `ApproverEligibilityClient` is implemented by `JdbcApproverEligibilityClient`. It requires all of the following: matching organization, exact authenticated subject, active Membership role, and active Mandate scope. It must not derive approver eligibility from caller-supplied JWT role or mandate values.

Membership endpoints:

- `POST /v1/organizations/{organizationId}/members` creates an active Membership.
- `GET /v1/organizations/{organizationId}/members` lists Membership records.
- `POST /v1/organizations/{organizationId}/members/{membershipId}/mandates` creates an active Mandate.

Operation type values are preserved as supplied (1–64 non-whitespace characters). Role codes and mandate scopes are trimmed and stored uppercase. Policy administration does not submit or decide approval requests.

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

Use `database/bootstrap/corporate-service-ownership.sql` to create service roles and `corporate_schema`, then run Flyway as `corporate_service_migrator`. Application startup does not run migrations. The runtime role can access only its granted Corporate tables, cannot mutate policy version fields or policy levels, and cannot access migration history or other schemas.

## Build and tests

```sh
./mvnw -f services/corporate-service/pom.xml verify
```
