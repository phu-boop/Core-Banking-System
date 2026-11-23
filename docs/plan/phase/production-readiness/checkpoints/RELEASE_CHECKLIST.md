# Phase 8 Release Checklist

Status: **IN PROGRESS — NOT APPROVED FOR PRODUCTION**

## Build and verification

- [ ] Full Maven reactor verification passes in CI. (Configured; no GitHub Actions run result is available locally.)
- [ ] CI publishes per-service test counts. (Configured in CI; no GitHub Actions run result is available locally.)
- [x] Container builds pass for every service with a Dockerfile.
- [ ] Every independently deployable service has a reviewed immutable image and deployment manifest. (Local manifests render successfully; immutable registry image tags and owner review are not available.)

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
- [x] Concurrency/replay tests prove no duplicate financial side effects for Ledger journal posting.

## Release approval

- [x] Available local and deferred evidence is recorded in the [smoke verdict](./SMOKE_TEST_VERDICT.md) and [rehearsal evidence](./REHEARSAL_EVIDENCE.md).
- [ ] No unresolved critical security or correctness issue remains.
- [ ] Project owner has recorded go/no-go approval.

Production release remains blocked until all required items are complete and owner approval is recorded.

## Owner approval record

| Approval | Project owner | Decision / evidence | Date |
|---|---|---|---|
| Production Kubernetes provider and cluster topology | Not assigned | Not recorded | Not recorded |
| External secret store and workload identity | Not assigned | Not recorded | Not recorded |
| SLO, alert, retention and recovery targets | Not assigned | Not recorded | Not recorded |
| Backup/restore, rollback and DR rehearsal acceptance | Not assigned | Not recorded | Not recorded |
| Final go/no-go release decision | Not assigned | NOT APPROVED | Not recorded |

## Evidence references

- [Smoke test verdict](./SMOKE_TEST_VERDICT.md)
- [Security/recovery/rollback evidence](./REHEARSAL_EVIDENCE.md)
- [Open owner decisions](../../../../architecture/OPEN-DECISIONS.md)
- [Operational target inputs](../../../../operations/OPERATIONAL_TARGETS.md)
