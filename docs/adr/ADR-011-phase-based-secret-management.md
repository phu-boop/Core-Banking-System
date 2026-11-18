# ADR-011: Phase-based Configuration and Secret Management

- **Status:** ACCEPTED
- **Date:** 2026-11-18

## Context

The service template needs a safe configuration and secret-delivery approach for local development and a production Kubernetes environment. The local workflow and production direction have been discussed, but exact production providers remain alternatives.

## Decision

### Phase 1 — Local development

- Use Ubuntu 24.04, Docker and Minikube.
- Store local Kubernetes credentials as Kubernetes Secrets; do not commit secret values or rendered Secret manifests.
- Spring Boot applications read injected configuration and secret values from environment variables.
- Use PostgreSQL 17.12 for this local development and test baseline; this does not select a production PostgreSQL version.

### Phase 2 — Production

- Run on AWS EKS or an equivalent Kubernetes platform.
- Use AWS Secrets Manager or Vault as the external secret store, integrated through External Secrets Operator.
- Apply least-privilege IAM/RBAC to secret access and workload permissions.
- Keep the exact production Kubernetes provider and external secret store open until selected.

## Alternatives

- Commit secret values in YAML manifests or source-controlled environment files — rejected.
- Use Kubernetes Secrets alone as the production secret source — not selected as the production direction.
- Select AWS EKS and AWS Secrets Manager as the only production providers — not selected; equivalent Kubernetes and Vault remain options.

## Consequences

- Secret values must be provisioned outside version control.
- Services consume secrets through environment variables; each service defines and validates only the variables it actually needs.
- The service template uses PostgreSQL credentials as its first real secret-consuming integration. Future secrets must be added only for real dependencies.
- Before production rollout, select the exact Kubernetes and secret-store providers and define encryption-at-rest and access-policy details.

## Dependencies

- [ADR-010](./ADR-010-phase-1-technical-baseline.md) selects Kubernetes for local and production environments.
- [Phase 1 plan](../plan/phase/Phase-1-Technical-Foundation.md), especially items 1.2 and 1.4.
- Production provider and secret store remain tracked in [Open Decisions](../architecture/OPEN-DECISIONS.md).
