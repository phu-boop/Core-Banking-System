# Service Recovery Runbook

**Status:** Provider-neutral procedure. Production cluster commands, approvals, and recovery objectives must be tailored to the approved environment before use.

Record approved recovery objectives in [Operational Targets](./OPERATIONAL_TARGETS.md).

## Safety checks

- Name the affected service, environment, incident lead, service owner, and authorized change approver.
- Determine whether the incident involves Transfer, Payment, or Ledger. If financial outcomes are uncertain, stop retries and coordinate reconciliation with the authoritative service before restart, rollback, or replay.
- Preserve logs, correlation IDs, timestamps, deployment revision, and sanitized diagnostics. Do not include secrets or unnecessary PII.
- Do not change database rows, delete messages, bypass authorization, or roll back a schema manually.

## Diagnose

1. Check the workload’s rollout state, pod events, restart count, and recent application logs using read-only cluster tooling.
2. Probe `/actuator/health`, `/actuator/health/liveness`, and `/actuator/health/readiness`. Capture status and timestamps; do not treat liveness alone as proof of readiness.
3. Check required dependencies and configuration sources, including database connectivity, OIDC provider availability, and downstream service reachability.
4. Compare the first failure with recent image, configuration, secret, infrastructure, and database migration changes.
5. Identify whether the issue is isolated to one replica, one service, or a shared dependency before changing replicas or restarting workloads.

## Recover

1. Agree on a recovery action with the service owner and change approver; record the reason, expected impact, and rollback/stop condition.
2. Prefer restoring a known-good compatible application configuration or image using the environment’s approved deployment process.
3. Before an application rollback, verify database migration compatibility. Flyway schema changes must not be reversed by ad-hoc SQL; use an approved forward-fix or recovery plan when compatibility is uncertain.
4. Restart or reschedule only the affected workload using the approved platform procedure. Avoid simultaneous restarts of dependent services.
5. If an external dependency or secret is implicated, restore connectivity or rotate credentials through the approved secret-management path; never put secret values in commands, logs, or manifests.
6. Do not replay financial commands or payment outcomes as a recovery shortcut. Confirm stable idempotency identity and authoritative outcome before any approved retry.

## Verify and hand off

1. Confirm all expected replicas are available and readiness remains healthy for the owner-approved observation window.
2. Exercise a non-destructive health/smoke check and verify affected downstream interactions.
3. For Transfer, Payment, or Ledger incidents, reconcile in-flight or unresolved operations with the owning service before declaring recovery.
4. Record the image/configuration revision, approvals, evidence, remaining risks, and follow-up actions in the incident record.

## Environment-specific details to approve

- Cluster namespace/context, deployment tooling, rollout/rollback procedure, and required access role.
- Service owners, change approvers, supported smoke checks, recovery observation windows, and RPO/RTO.
- Database migration compatibility policy and service-specific reconciliation procedures.
