# Centralized API Documentation Implementation Report

**Date**: 2026-11-22
**Status**: Implemented for local/Kubernetes-local design

## Implemented components

- Added Springdoc `2.10.1` to nine REST-enabled Spring Boot modules.
- Added OpenAPI titles, descriptions and versions to existing application classes.
- Added JWT bearer security metadata to services that already configure OAuth2/JWT:
  - Identity
  - Party
  - Branch
  - Corporate
  - Transfer
  - Payment
  - Audit
- Added no bearer security metadata to Account or Ledger, consistent with their current source configuration.
- Added `services/api-docs-service`.
- Added same-origin `/specs/{serviceName}` proxy routes to avoid browser CORS dependency.
- Added Swagger UI dropdown entries for nine services.
- Added Dockerfile and Kubernetes local Deployment/Service manifest.
- Added developer documentation.
- Added API docs CI verification job.

## Modified modules

- `services/identity-service`
- `services/party-service`
- `services/branch-service`
- `services/corporate-service`
- `services/account-service`
- `services/ledger-service`
- `services/transfer-service`
- `services/payment-service`
- `services/audit-service`
- `services/api-docs-service`
- root Maven reactor POM
- GitHub Actions CI

## Documentation URLs

With `api-docs-service` started locally:

- Swagger UI: `http://localhost:8090/swagger-ui/index.html`
- Central OpenAPI document: `http://localhost:8090/v3/api-docs`
- Identity proxy: `http://localhost:8090/specs/identity`
- Party proxy: `http://localhost:8090/specs/party`
- Branch proxy: `http://localhost:8090/specs/branch`
- Corporate proxy: `http://localhost:8090/specs/corporate`
- Account proxy: `http://localhost:8090/specs/account`
- Ledger proxy: `http://localhost:8090/specs/ledger`
- Transfer proxy: `http://localhost:8090/specs/transfer`
- Payment proxy: `http://localhost:8090/specs/payment`
- Audit proxy: `http://localhost:8090/specs/audit`

Each business service exposes `/v3/api-docs` after Springdoc integration.

## Tests executed

| Command | Result |
|---|---|
| `./mvnw -f services/identity-service/pom.xml clean test` | PASS, 12 tests |
| `./mvnw -f services/api-docs-service/pom.xml test` | PASS, 2 tests |
| `./mvnw clean verify` | PASS, 85 tests |
| Start `api-docs-service` and probe health/UI/docs | PASS: health, Swagger UI and docs service `/v3/api-docs` returned HTTP 200 |
| Probe `/specs/identity` with business services stopped | 502 expected; proxy correctly reported unavailable upstream |

The docs service runtime was started with the packaged JAR and stopped after probes.

## Security considerations

- Swagger UI remains enabled by default in the docs service; no public ingress was added.
- The Kubernetes manifest is `ClusterIP` and requires port-forwarding for local access.
- The proxy reads upstream OpenAPI JSON server-side.
- No CORS configuration was added because Swagger UI and proxy routes use the same origin.
- Protected endpoint authentication still follows existing OAuth2/JWT configuration.
- Account and Ledger remain without inferred bearer security because source security is absent.
- Production exposure remains disabled as a network/deployment policy rather than being claimed by this implementation.

## Remaining limitations

- Central dropdown loading was not end-to-end verified with all nine business services running.
- Docker image was not built.
- Kubernetes manifest was not applied to a live cluster.
- No public production route or Ingress was added.
- The Springdoc version should remain reviewed when Spring Boot minor/patch versions change.

## Rollback

- Remove the `api-docs-service` module from the root POM.
- Remove the Springdoc dependency and OpenAPI annotations from the nine services.
- Remove the docs Dockerfile and Kubernetes manifest.
- Remove the CI job.
- No database migration or business API rollback is required.
