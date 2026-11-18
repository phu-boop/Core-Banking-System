# Service Template

Minimal, independently buildable Spring Boot service used to establish the Phase 1 foundation. It contains no banking domain, business API or business migration. It connects to a local PostgreSQL database to demonstrate configuration and schema ownership.

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

The tests include PostgreSQL 17.12 integration checks via Testcontainers and require Docker. PostgreSQL 17.12 is selected for local development and foundation tests because it is within the PostgreSQL major versions supported by the Flyway version managed by Spring Boot; it does not select a PostgreSQL production version. The template uses `spring-boot-starter-test` for its own tests and does not select a project-wide testing policy for later services.

## Database ownership proof

`database/bootstrap/service-template-ownership.sql` is a one-time provisioning script for a fresh database. It creates the service migration/runtime roles and the `service_template_schema` owned by the migration role. Passwords are read from process environment variables by `psql`; no password literals are stored in the script. It is not idempotent and only runs when PostgreSQL initializes a fresh data directory. Changing the Kubernetes Secret later does not rotate existing PostgreSQL role passwords automatically.

Supply credentials outside the script using the deployment's approved secret-management process. Flyway migrations must run explicitly as `service_template_migrator`; the application connects as `service_template_app`. The runtime role receives `USAGE` on its schema and only the table/sequence privileges granted by each migration. It cannot access another service's schema, modify schema objects, or read Flyway history.

The integration test provisions an ephemeral PostgreSQL database, runs the bootstrap and test-only Flyway migration, then verifies owned-schema DML, denied cross-schema access and DDL, service-local Flyway history, and absence of cross-schema foreign keys. It does not create production schemas or business migrations.

## Reliability foundation proof

The test migration adds infrastructure-only `reliability_probe`, `outbox_event`, and `processed_event` tables. It does not add a banking domain migration or event producer/consumer. Integration tests prove that an outbox record and a domain-change probe can commit or roll back together, and that `(event_id, consumer_name)` prevents a consumer from applying the same event twice.

The proof deliberately does not select a broker. Delivery retry, backoff, dead-letter behavior and broker-specific ordering remain open until an event broker and operational policy are approved.

## Run locally

The application requires all three database settings; it fails at startup if any are missing. To run the JVM directly on the host, first provide `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` through a local secret source and ensure PostgreSQL is reachable (for example, forward the Minikube database port). Do not put real values in command history.

```sh
./mvnw -f services/service-template/pom.xml spring-boot:run
```

Useful endpoints:

- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`

Readiness includes the database connection; liveness does not depend on database availability. Only the health actuator endpoint is exposed over HTTP. Health details are hidden.

## Logging and local observability

The console uses Spring structured JSON logs. Request logs include `serviceName`, correlation ID, HTTP method, path, response status and elapsed milliseconds. The filter excludes the query string and removes its MDC values after each request. Do not add request bodies, PII, secrets or tokens to log fields.

The local actuator also exposes `/actuator/metrics` and `/actuator/prometheus` for minimal Micrometer/Prometheus metrics. This is a local foundation; Prometheus backend, Grafana dashboards, retention, SLOs and alert thresholds remain open. The event/trace backend is not selected or exported in production.

For HTTP requests, the service accepts `X-Correlation-Id` containing 1–64 ASCII letters, digits, dots, underscores or hyphens. If omitted, it creates a UUID. A malformed supplied value receives `400 Bad Request`. The value is added to the response and logging MDC, then removed from the thread after request processing.

## Configuration and secrets

`SERVER_PORT` can be supplied as an environment variable; it defaults to `8080`. Local development uses Ubuntu 24.04, Docker and Minikube. PostgreSQL 17.12 runs in Minikube. Local database credentials are supplied through a Kubernetes Secret and injected as environment variables; never commit secret values or Secret manifests containing real credentials.

The template's runtime database password comes from a Kubernetes Secret. The production direction is an external secret store integrated through External Secrets Operator, with least-privilege IAM/RBAC; the exact Kubernetes provider and choice between AWS Secrets Manager and Vault remain open. See [ADR-011](../../docs/adr/ADR-011-phase-based-secret-management.md).

## Build and run in local Kubernetes

On Ubuntu 24.04, start the accepted Minikube local cluster with Docker:

```sh
minikube start --driver=docker
```

Create an ignored local credentials file and replace every example value with a unique random password:

```sh
cp .env.example .env.local
```

Create/update the Kubernetes Secret without printing its values or committing it:

```sh
kubectl create secret generic service-template-db-credentials \
  --from-env-file=.env.local \
  --dry-run=client -o yaml | kubectl apply -f -
```

Create/update the bootstrap ConfigMap from the reviewed local bootstrap files:

```sh
kubectl create configmap service-template-db-bootstrap \
  --from-file=00-validate-local-credentials.sh=services/service-template/database/bootstrap/00-validate-local-credentials.sh \
  --from-file=service-template-ownership.sql=services/service-template/database/bootstrap/service-template-ownership.sql \
  --dry-run=client -o yaml | kubectl apply -f -
```

Start PostgreSQL and wait for its one-time initialization to complete:

```sh
kubectl apply -f services/service-template/deploy/kubernetes/postgres-local.yaml
kubectl rollout status statefulset/service-template-postgres
```

Build the image after packaging:

```sh
./mvnw -f services/service-template/pom.xml package
docker build -t service-template:local services/service-template
minikube image load service-template:local
```

Apply the service manifest and forward the service port:

```sh
kubectl apply -f services/service-template/deploy/kubernetes/local.yaml
kubectl rollout status deployment/service-template
kubectl port-forward service/service-template 8080:8080
```

The manifest does not install or create Minikube. Do not use the local image tag or manifest as a production deployment configuration.
