# Kubernetes Deployment Catalog

This directory is the provider-neutral deployment catalog for Phase 8. The local and production provider overlays remain separate decisions.

## Service inventory

| Service | Default HTTP port | Dockerfile | Deployment manifest |
|---|---:|---|---|
| identity-service | 8080 | Available | `local/identity-service.yaml` |
| party-service | 8081 | Available | `local/party-service.yaml` |
| branch-service | 8082 | Available | `local/branch-service.yaml` |
| corporate-service | 8083 | Available | `local/corporate-service.yaml` |
| account-service | 8085 | Available | `local/account-service.yaml` |
| transfer-service | 8085 | Available | `local/transfer-service.yaml` |
| ledger-service | 8086 | Available | `local/ledger-service.yaml` |
| payment-service | 8086 | Available | `local/payment-service.yaml` |
| audit-service | 8087 | Available | `local/audit-service.yaml` |
| notification-service | 8088 | Available | `local/notification-service.yaml` |
| reporting-service | 8088 | Available | `local/reporting-service.yaml` |
| api-docs-service | 8090 | Available | `local/api-docs-service.yaml` |

`service-template` is a template and is not a production workload. The duplicate default ports are safe for separate Kubernetes pods, but host-process development needs distinct port overrides and matching downstream URLs.

## Runtime prerequisites

- PostgreSQL with one service-owned schema and least-privilege credentials per service.
- Keycloak/OIDC for services that validate user or service tokens.
- Secret values injected from Kubernetes Secrets in local Minikube. Do not commit rendered Secret manifests or secret values.
- Production external secret provider and workload identity are not selected; see ADR-011.
- Liveness/readiness endpoints: `/actuator/health/liveness` and `/actuator/health/readiness`.

## Local prerequisites

The local overlay expects these existing cluster resources:

- A PostgreSQL service named `postgres`, database `core_banking`, and separate schema-scoped login roles.
- A Keycloak service named `keycloak`, realm `core-banking`.
- The named Kubernetes Secrets listed below.
- Locally built images tagged `core-banking/<service>:ci`.

The PostgreSQL and Keycloak workloads are intentionally not provisioned by this service overlay. Their versions, bootstrap and local credentials must be configured separately.

## Secret references

Create each secret in the same namespace as the workloads. Only the listed keys are required; do not use a shared database role across services.

| Secret name | Required keys |
|---|---|
| `identity-service-db-credentials` | `username`, `password` |
| `party-service-db-credentials` | `username`, `password` |
| `branch-service-db-credentials` | `username`, `password` |
| `corporate-service-db-credentials` | `username`, `password` |
| `account-service-db-credentials` | `username`, `password` |
| `ledger-service-db-credentials` | `username`, `password` |
| `transfer-service-db-credentials` | `username`, `password` |
| `payment-service-db-credentials` | `username`, `password` |
| `audit-service-db-credentials` | `username`, `password` |
| `notification-service-db-credentials` | `username`, `password` |
| `reporting-service-db-credentials` | `username`, `password` |
| `corporate-party-client-credentials` | `client-id`, `client-secret` |
| `transfer-corporate-client-credentials` | `client-id`, `client-secret` |

Never commit Secret resources or secret values. Provision these through a local secret manager or `kubectl create secret` using ignored local files.

## Deploy locally

After a Minikube cluster, PostgreSQL, Keycloak, and the required Secrets are ready:

```bash
for image in \
  identity-service party-service branch-service corporate-service \
  account-service ledger-service transfer-service payment-service \
  audit-service notification-service reporting-service api-docs-service; do
  minikube image load "core-banking/${image}:ci"
done
kubectl apply -k deploy/kubernetes/local
kubectl get deployments,services,pods
```

The API docs UI is available by port-forwarding `api-docs-service` on port 8090.

There is no production overlay yet. Production registry, cluster topology, external secret provider, resource budgets and rollout policy remain owner decisions; this local overlay must not be treated as production-ready.
