# Centralized API Documentation Repository Audit

**Audit date**: 2026-11-22
**Git branch**: `feat/corporate-approval-core`
**Scope**: Repository-wide inspection for Springdoc/OpenAPI and centralized Swagger UI readiness

## 1. Service inventory

The Maven reactor contains 10 Spring Boot modules:

| Module | Spring application | Default port | Controller count | Security model |
|---|---|---:|---:|---|
| `service-template` | `service-template` | 8080 | No REST controller found | Template foundation |
| `identity-service` | `identity-service` | 8080 | 1 | OAuth2 JWT resource server |
| `party-service` | `party-service` | 8081 | 1 | OAuth2 JWT resource server |
| `branch-service` | `branch-service` | 8082 | 1 | OAuth2 JWT resource server |
| `corporate-service` | `corporate-service` | 8083 | 4 | OAuth2 JWT resource server |
| `account-service` | `account-service` | 8085 | 1 | No OAuth2 resource-server dependency/configuration found |
| `ledger-service` | `ledger-service` | 8086 | 1 | No OAuth2 resource-server dependency/configuration found |
| `transfer-service` | `transfer-service` | 8085 | 1 | OAuth2 JWT resource server |
| `payment-service` | `payment-service` | 8086 | 1 | OAuth2 JWT resource server |
| `audit-service` | `audit-service` | 8087 | 1 | OAuth2 JWT resource server |

`account-service` and `transfer-service` currently share default port 8085.

`ledger-service` and `payment-service` currently share default port 8086.

`identity-service` and `service-template` currently share default port 8080.

These collisions require `SERVER_PORT` overrides when services run concurrently.

## 2. Technology stack

All Spring Boot modules use Spring Boot `3.5.16`, Java `21`, Maven Wrapper and independent module POM files.

The repository root POM is a Maven reactor:

- `services/service-template`
- `services/identity-service`
- `services/party-service`
- `services/branch-service`
- `services/corporate-service`
- `services/account-service`
- `services/ledger-service`
- `services/transfer-service`
- `services/payment-service`
- `services/audit-service`

Common dependencies include Spring Web, Spring JDBC, Flyway and PostgreSQL where domain persistence is implemented. OAuth2 resource-server dependencies exist on Identity, Party, Branch, Corporate, Transfer, Payment and Audit. Account and Ledger POMs do not contain that configuration.

## 3. Existing endpoint inventory

The following endpoints are derived from actual `@RequestMapping` and HTTP mapping annotations.

### Identity

- `GET /v1/identity/me`

### Party

- `POST /v1/customers`
- `GET /v1/customers/{partyId}`
- `PATCH /v1/customers/{partyId}`

### Branch

- `POST /v1/branches`
- `GET /v1/branches/{branchId}`
- `GET /v1/branches`

### Corporate

- `POST /v1/organizations`
- `GET /v1/organizations/{organizationId}`
- `POST /v1/organizations/{organizationId}/approval-policies`
- `GET /v1/organizations/{organizationId}/approval-policies`
- `POST /v1/organizations/{organizationId}/approval-policies/{policyId}/activate`
- `POST /v1/organizations/{organizationId}/approval-requests`
- `GET /v1/organizations/{organizationId}/approval-requests/{approvalId}`
- `POST /v1/organizations/{organizationId}/approval-requests/{approvalId}/decisions`
- `GET /v1/organizations/{organizationId}/approval-requests/{approvalId}/history`
- `POST /v1/organizations/{organizationId}/members`
- `GET /v1/organizations/{organizationId}/members`
- `POST /v1/organizations/{organizationId}/members/{membershipId}/mandates`

### Account

- `POST /v1/accounts`
- `GET /v1/accounts/{accountId}`
- `POST /v1/accounts/{accountId}/block`
- `POST /v1/accounts/{accountId}/unblock`

### Ledger

- `POST /internal/ledger/journals`
- `POST /internal/ledger/journals/{journalId}/reversals`
- `GET /internal/ledger/journals/{journalId}`
- `GET /internal/ledger/accounts/{accountId}/balance`

### Transfer

- `POST /v1/transfers/internal`
- `GET /v1/transfers/internal/{transferId}`
- `POST /v1/transfers`
- `GET /v1/transfers/{transferId}`
- `POST /v1/transfers/{transferId}/approval/refresh`
- `POST /v1/transfers/{transferId}/payment/refresh`
- `POST /v1/transfers/{transferId}/execute`
- `POST /v1/transfers/{transferId}/cancel`

### Payment

- `POST /internal/payments`
- `GET /internal/payments/{paymentId}`
- `POST /internal/payments/{paymentId}/reconcile`
- `POST /internal/payments/{paymentId}/outcomes`

### Audit

- `POST /internal/audit/records`
- `GET /v1/audit/records`

## 4. Existing Springdoc / Swagger / OpenAPI

No `springdoc-openapi`, Swagger, OpenAPI annotation or UI dependency was found in POM files or source.

No `/v3/api-docs` or Swagger UI configuration was found.

No existing standalone API documentation service was found.

## 5. API Gateway and routing

No Spring Cloud Gateway or other API Gateway module/configuration was found.

The repository explicitly marks API Gateway technology as OPEN. `docs/architecture/OPEN-DECISIONS.md` also states that REST service authorization remains in owning services and gateway is not the only authorization boundary.

Therefore, there is no existing gateway suitable for aggregating Swagger specifications.

## 6. Authentication and authorization

Identity, Party, Branch, Corporate, Transfer, Payment and Audit use Spring Security OAuth2 JWT resource-server configurations.

Account and Ledger currently have no OAuth2 resource-server dependency/configuration in their POM and application configuration.

JWT issuer, JWK set and audience are environment-configured on secured services.

There is no repository-wide CORS configuration found; no `CorsConfiguration`, `WebMvcConfigurer`, `@CrossOrigin` or equivalent configuration was found.

Internal and public endpoint exposure is not currently separated in a gateway. Internal routes may be externally reachable if services are exposed.

## 7. Docker, Kubernetes and deployment

Dockerfiles exist for:

- `service-template`
- `identity-service`
- `party-service`
- `branch-service`
- `corporate-service`

No Dockerfile was found for Account, Ledger, Transfer, Payment or Audit.

No Docker Compose file was found.

Only `service-template/deploy/kubernetes/local.yaml` and `postgres-local.yaml` contain Kubernetes manifests.

The existing local deployment is service-template-specific and does not define a centralized documentation application or routes for all microservices.

## 8. Existing documentation and tests

The repository has API contracts in `docs/api/API-CONTRACTS.md`, architecture/event contracts and phase specifications.

There is no endpoint-contract script for a Swagger/API smoke check:

- `api-test.sh`
- `api-check.sh`
- `smoke.sh`
- `test-api.sh`

No endpoint was found named `swagger`, `openapi` or `/v3/api-docs`.

JUnit/Spring integration tests exist for several service APIs. The CI workflow builds and verifies service modules individually.

## 9. Security risks

Central Swagger UI must not expose internal endpoints or authorization rules to unauthenticated/public users without an explicit decision.

Swagger UI and `/v3/api-docs` must be absent or restricted in production.

If hosted centrally in a browser, CORS must be configured explicitly; no CORS policy exists now.

JWT configuration must not place bearer tokens or secrets in repository files or documentation examples.

Internal routes such as Ledger, Payment and Audit must remain distinguishable from customer-facing APIs.

## 10. Dependency compatibility

Spring Boot 3.5.16 / Spring Framework 6 uses the Spring MVC variant of springdoc:

- `org.springdoc:springdoc-openapi-starter-webmvc-ui`

The exact patch version must be compatible with the current Spring Boot minor version and should be pinned or managed consistently.

Maven dependency management is currently per-service POM and has no shared Springdoc version property. A centralized dependency decision is needed to avoid version drift.

## 11. Blockers and unresolved decisions

1. API Gateway technology is OPEN, so no existing gateway aggregation route exists.
2. Central Swagger UI is therefore not deployable through an existing gateway in this repository.
3. Production exposure policy for Swagger/OpenAPI is OPEN.
4. CORS policy is not defined.
5. Account and Ledger lack OAuth2 security configuration.
6. Docker/Kubernetes deployment coverage is incomplete for many services.
7. Default ports collide when services run concurrently.
8. Springdoc dependency version needs compatibility approval/pinning.
9. Internal vs public endpoint scope is not centrally classified.

## 12. Recommended Phase 2 decision options

### Option A — Minimal centralized local aggregator

Add one lightweight Spring Boot documentation application that serves Swagger UI and loads `/v3/api-docs` from known service URLs.

Advantages:

- Central dropdown and local developer interface.
- No gateway technology assumed.
- Independent of existing business services.

Costs:

- Introduces a new service, contrary to the preferred reuse-first approach.
- Requires CORS and network URL configuration.
- Needs deployment/CI coverage.

### Option B — Static Swagger UI in an existing developer/service template

Reuse `service-template` as a local documentation host.

Advantages:

- Reuses an existing module and deployment pattern.
- Avoids a standalone documentation-only service.

Costs:

- Couples documentation infrastructure to a template.
- Template is not currently a gateway or routing facade.
- Product responsibility may be unclear.

### Option C — Per-service Swagger UI only

Add springdoc to each applicable service and document separate URLs.

Advantages:

- Lowest architectural risk.
- No new central service.
- Works without gateway.

Costs:

- Not one centralized interface or dropdown.
- Internal services remain individually exposed.

The repository audit does not justify option A or B until the central exposure and deployment decision is approved.

## 13. Audit conclusion

The repository has 10 Spring Boot modules and several REST controllers, but no Springdoc dependency, Swagger UI, OpenAPI endpoint, API Gateway or CORS configuration.

Implementation can proceed only after reviewing the centralized architecture options and selecting a constrained local/development strategy.

No source code or existing configuration was modified during this audit.
