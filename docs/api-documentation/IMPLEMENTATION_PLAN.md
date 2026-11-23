# Centralized API Documentation Implementation Plan

**Date**: 2026-11-22
**Status**: Planned, awaiting implementation approval
**Audit**: [AUDIT.md](./AUDIT.md)

## 1. Architecture decision

Use a dedicated `api-docs-service` module for the centralized Swagger UI and add `springdoc-openapi-starter-webmvc-ui` to each applicable Spring Boot microservice.

Each microservice exposes an independent `/v3/api-docs` specification. The docs service uses Springdoc Swagger UI multi-specification configuration to present a service dropdown.

This plan does not introduce an API Gateway. The existing microservices remain responsible for authentication and authorization.

The docs interface is enabled for local development and Kubernetes local. Production exposure remains a blocker and is disabled by default unless an explicit environment decision is approved.

## 2. Service scope

### Integrate Springdoc

- `identity-service`
- `party-service`
- `branch-service`
- `corporate-service`
- `account-service`
- `ledger-service`
- `transfer-service`
- `payment-service`
- `audit-service`

`service-template` is infrastructure code and will not be included as a business API unless it exposes a REST API that is intentionally useful to developers.

### Central service

Create:

- `services/api-docs-service`

Responsibilities:

- Host Swagger UI.
- Configure a named specification for every integrated service.
- Provide a local default URL and Kubernetes local URL configuration.
- Keep no domain data or business API behavior.

## 3. Required dependencies

Add the compatible Spring MVC Springdoc artifact to each applicable module:

- `org.springdoc:springdoc-openapi-starter-webmvc-ui`

Pin one compatible patch version for Spring Boot 3.5.16 at the Maven reactor level or a shared dependency-management convention. The exact patch version will be selected during implementation through Maven dependency resolution rather than guessed.

The docs service uses the same Springdoc artifact and Spring Web dependency.

## 4. Required configuration

### Per microservice

Configure a stable OpenAPI title, description and version through a local `OpenAPI` bean or application properties.

Configure the OpenAPI security scheme only for services with existing OAuth2/JWT resource-server authentication:

- Identity
- Party
- Branch
- Corporate
- Transfer
- Payment
- Audit

Do not configure bearer authentication for Account or Ledger unless OAuth2 security is separately implemented and approved.

Do not duplicate security configuration across controllers unless existing annotations are insufficient.

### Central docs service

Configure specification URLs for local and Kubernetes environments. URLs must be environment variables and must not be hard-coded to a developer-specific hostname.

Provide:

- Swagger UI URL.
- OpenAPI JSON URL.
- Named service dropdown entries.
- Independent specification loading.
- Development-only configuration controls.

### Security

The docs service must not expose bearer tokens or secret values.

Local-only profile:

- Swagger UI and `/v3/api-docs` may be available.
- Internal endpoints remain visible only to developers using the local environment.

Kubernetes local profile:

- Swagger UI and OpenAPI JSON use Kubernetes-local ClusterIP/Service DNS or port-forwarding.
- No public Ingress is introduced unless explicitly approved.
- Internal APIs must not be forwarded to public networks.

Production profile:

- Swagger UI and OpenAPI JSON are disabled by default.
- Public exposure requires an explicit security decision.

## 5. Files and modules

The implementation will modify or add only these areas:

```text
pom.xml
services/api-docs-service/
services/identity-service/pom.xml
services/party-service/pom.xml
services/branch-service/pom.xml
services/corporate-service/pom.xml
services/account-service/pom.xml
services/ledger-service/pom.xml
services/transfer-service/pom.xml
services/payment-service/pom.xml
services/audit-service/pom.xml
services/*/src/main/java/com/banking/platform/*/config/
services/*/src/main/resources/application.yml
docs/api-documentation/IMPLEMENTATION_REPORT.md
docs/api-documentation/README.md
```

Existing controllers and DTO classes remain unchanged unless OpenAPI metadata is required to accurately describe validation or response codes.

## 6. Implementation tasks

### T001 — Add dependency management

Add the pinned Springdoc version and Maven module entry.

Dependencies:

- None

### T002 — Add per-service OpenAPI configuration

Add compatible Springdoc dependencies and OpenAPI metadata to the nine applicable modules.

Dependencies:

- T001

### T003 — Add JWT bearer documentation

Configure OpenAPI bearer security on the seven services that already enforce OAuth2/JWT.

Dependencies:

- T002

### T004 — Create the docs service

Create `services/api-docs-service` with Springdoc Swagger UI and environment-configurable specification URLs.

Dependencies:

- T001

### T005 — Configure local and Kubernetes-local access

Add local profile configuration and Kubernetes-local deployment manifest/service.

Dependencies:

- T004

### T006 — Add CORS policy

Add only the minimum CORS configuration required for local Swagger UI-to-service specification loading.

Dependencies:

- T004
- T005

### T007 — Add OpenAPI contract tests

Add tests that verify `/v3/api-docs` JSON is present and can parse into valid OpenAPI 3 documents.

Dependencies:

- T002
- T003

### T008 — Add centralized loading test

Verify the docs service Swagger UI loads each configured specification URL.

Dependencies:

- T004
- T005
- T007

### T009 — Update developer documentation

Document access URLs, authentication test flow, adding a service and troubleshooting unavailable specifications.

Dependencies:

- T008

### T010 — Run regression verification

Run the root Maven reactor and relevant endpoint/OpenAPI probes.

Dependencies:

- T002 through T009

### T011 — Produce implementation report

Record implemented components, URLs, test evidence, security decisions and limitations.

Dependencies:

- T010

## 7. Task dependencies

```text
T001
 ├── T002 ── T003
 │          └── T007
 └── T004 ── T005 ── T006
                    └── T008 ── T009
T007 ─────────────────┘
T008
T009
T010
T011
```

## 8. Testing strategy

### Dependency and build checks

- Run `./mvnw verify`.
- Verify all module POMs resolve the selected Springdoc version.
- Verify no dependency conflict is introduced.

### Per-service checks

For each integrated service:

- Verify `/v3/api-docs` returns HTTP 200.
- Verify the response is parseable OpenAPI JSON.
- Verify expected service title and version.
- Verify known controller mappings appear.
- Verify DTO request/response schemas are present.
- Verify OAuth2 bearer security appears only where the service already uses JWT authentication.

### Centralized checks

- Verify the docs UI returns HTTP 200.
- Verify every configured specification appears in the dropdown.
- Verify selecting each specification loads the correct JSON.
- Verify request/response schemas render.
- Verify no undefined or fabricated endpoint appears.

### Security checks

- Verify protected endpoints remain protected by existing OAuth2 configuration.
- Verify Swagger UI can load public health/specification endpoints where allowed.
- Verify no public Ingress is added.
- Verify no bearer token or secret is committed or logged.
- Verify CORS permits only configured local origins.

### Docker/Kubernetes checks

- If Docker is available, build or verify `api-docs-service`.
- Verify the Kubernetes local manifest/service.
- Verify service DNS/specification URLs work inside the local cluster.
- If Docker/Kubernetes is unavailable, record the limitation rather than claiming runtime success.

## 9. Rollback considerations

- Keep the docs module outside existing business modules.
- Rollback consists of removing the docs module and reverting Springdoc dependencies/configuration.
- No database migration is required.
- Existing controller routes and business behavior remain unchanged.
- The Kubernetes docs Service/Deployment can be removed without altering business workloads.
- Keep production Swagger disablement as the default.

## 10. Acceptance criteria

- Every applicable service exposes a valid `/v3/api-docs`.
- The centralized docs service exposes one Swagger UI URL.
- The service dropdown contains accurate names and URLs.
- All existing controller mappings are represented from source metadata.
- JWT bearer security is shown only where configured.
- Internal/public exposure follows local/Kubernetes-local scope.
- CORS is constrained to configured origins.
- No existing business behavior is changed.
- Maven build and relevant tests pass.
- An implementation report records actual test evidence.

## 11. Blockers and decisions requiring approval

### Already approved

- Add a dedicated `api-docs-service`.
- Add per-service Springdoc OpenAPI.
- Support local and Kubernetes local access.

### Still blocked

- Exact Springdoc patch version remains an implementation-time compatibility decision.
- Exact Kubernetes namespace/service DNS convention requires the final local cluster layout.
- Production Swagger/OpenAPI exposure remains disabled pending security approval.
- Public CORS origins are not defined.
- Account and Ledger authentication is absent and must not be inferred.

## 12. Current implementation status

No implementation has started.

The next required step is explicit approval to implement this plan. Before implementation, a single batch should be limited to:

1. Root dependency management.
2. Central docs service scaffold.
3. Per-service Springdoc integration.

The implementation must stop and report if Maven cannot resolve a compatible Springdoc version or Kubernetes local configuration conflicts with existing service names/ports.
