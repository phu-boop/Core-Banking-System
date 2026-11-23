# Phase 8 Release Checklist

Status: **IN PROGRESS — NOT APPROVED FOR PRODUCTION**

## Build and verification

- [ ] Full Maven reactor verification passes in CI.
- [ ] CI publishes per-service test counts.
- [ ] Container builds pass for every service with a Dockerfile.
- [ ] Every independently deployable service has a reviewed image and deployment manifest.

## Security and configuration

- [ ] No committed secret values exist in source, environment examples or Kubernetes manifests.
- [ ] Critical/high security findings are resolved or explicitly accepted by the owner.
- [ ] Sensitive logs and audit records are reviewed for credential/PII leakage.
- [ ] Production Kubernetes provider, external secret store and workload identity are approved.

## Operations and recovery

- [ ] SLOs, alert thresholds and retention periods are approved.
- [ ] Backup and restore RPO/RTO are approved and rehearsal evidence is recorded.
- [ ] Rollback and disaster-recovery rehearsals pass.
- [ ] Outbox backlog, retry and controlled replay runbooks are reviewed.
- [ ] Concurrency/replay tests prove no duplicate financial side effects.

## Release approval

- [ ] All required evidence is attached to the Phase 8 smoke verdict.
- [ ] No unresolved critical security or correctness issue remains.
- [ ] Project owner has recorded go/no-go approval.

Production release remains blocked until all required items are complete and owner approval is recorded.
