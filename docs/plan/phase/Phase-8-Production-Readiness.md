# Phase 8 - Production Readiness

## Status

- **Current status:** Active — first release-gate batch in progress.
- **Completed in this batch:** full-reactor CI verification and per-service test summary; container builds for services with Dockerfiles; initial release checklist.
- **Not completed:** production deployment manifests for all services, secret-store integration, approved SLO/RPO/RTO, operational runbooks, recovery rehearsals and owner release approval.
- **Detailed implementation plan:** [Production Readiness plan](./production-readiness/plan.md).
- **Feature specification:** [Phase 8 feature spec](../../specs/phase-8-production-readiness/feature-spec.md).

## Scope

- Full Maven reactor verification and per-service CI evidence.
- Container build validation for modules that currently provide a Dockerfile.
- Provider-neutral Kubernetes health, configuration and rollout baseline.
- Secret handling, security checks and sensitive-log review.
- Operational SLO, alert, outbox/retry and recovery runbooks.
- Concurrency, replay, backup/restore and rollback evidence.
- Final release checklist and owner go/no-go.

## Acceptance

- The release gate runs the full workspace Maven verification.
- Test counts are reported per service.
- Existing service Dockerfiles build successfully; missing Dockerfiles are not silently treated as deployable.
- Kubernetes manifests contain no secret values and preserve independent service ownership.
- Recovery and replay tests show no duplicate financial side effects.
- Provider-specific production choices and target thresholds are approved before final release.

## Decisions Still Open

- Production Kubernetes provider/topology and rollout policy.
- External secret store and workload identity.
- API Gateway and event broker products.
- SLO, alert thresholds, retention and backup/restore RPO/RTO.
- External interbank provider and reconciliation behavior.
