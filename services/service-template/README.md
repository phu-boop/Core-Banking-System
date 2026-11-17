# Service Template

Minimal, independently buildable Spring Boot service used to establish the Phase 1 foundation. It contains no banking domain, database, broker, authentication provider or business API.

## Baseline

- Java 21 source/bytecode target.
- Spring Boot 3.5.16 for this template.
- Maven Wrapper using Maven 3.9.16.
- Actuator health, liveness and readiness endpoints.
- `X-Correlation-Id` validation/generation and response propagation.
- Kubernetes manifest is for local development only; production overlays, registry, cluster and rollout policy are not defined here.

The project-wide decision is recorded in [ADR-010](../../docs/adr/ADR-010-phase-1-technical-baseline.md). Spring Boot/Maven patch versions here are implementation pins, not a policy for all future service versions.

## Build and test

Run from the repository root:

```sh
./mvnw -f services/service-template/pom.xml verify
```

The tests include PostgreSQL 17.12 integration checks via Testcontainers and require Docker. PostgreSQL 17.12 is selected only for Phase 1.3 tests because it is within the PostgreSQL major versions supported by the Flyway version managed by Spring Boot; it does not select a PostgreSQL production version. The template uses `spring-boot-starter-test` for its own tests and does not select a project-wide testing policy for later services.

## Database ownership proof

`database/bootstrap/service-template-ownership.sql` is a one-time DBA provisioning script for a fresh database. It creates the service migration/runtime roles and the `service_template_schema` owned by the migration role. It intentionally has no passwords and is not idempotent; do not rerun it against an already provisioned database.

Supply credentials outside the script using the deployment's approved secret-management process. Flyway migrations must run explicitly as `service_template_migrator`; the application connects as `service_template_app`. The runtime role receives `USAGE` on its schema and only the table/sequence privileges granted by each migration. It cannot access another service's schema, modify schema objects, or read Flyway history.

The integration test provisions an ephemeral PostgreSQL database, runs the bootstrap and test-only Flyway migration, then verifies owned-schema DML, denied cross-schema access and DDL, service-local Flyway history, and absence of cross-schema foreign keys. It does not create production schemas or business migrations.

## Run locally

```sh
./mvnw -f services/service-template/pom.xml spring-boot:run
```

Useful endpoints:

- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`

Only the health actuator endpoint is exposed over HTTP. Health details are hidden.

For HTTP requests, the service accepts `X-Correlation-Id` containing 1–64 ASCII letters, digits, dots, underscores or hyphens. If omitted, it creates a UUID. A malformed supplied value receives `400 Bad Request`. The value is added to the response and logging MDC, then removed from the thread after request processing.

## Build and run in local Kubernetes

Build the image after packaging:

```sh
./mvnw -f services/service-template/pom.xml package
docker build -t service-template:local services/service-template
```

Load the image into the local Kubernetes distribution if required, then apply the local-only manifest:

```sh
kubectl apply -f services/service-template/deploy/kubernetes/local.yaml
kubectl port-forward service/service-template 8080:8080
```

The manifest does not create a cluster or choose a local Kubernetes distribution. Do not use the local image tag or manifest as a production deployment configuration.
