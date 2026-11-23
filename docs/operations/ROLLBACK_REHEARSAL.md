# Independent Service Rollback Rehearsal

**Status:** Procedure only; provider-specific commands, approvals, and rehearsal evidence must be supplied by the production platform owner.

## Preconditions

1. Use a non-production environment representative of the approved deployment platform. Name one service under test, its owner, incident/change approver, current revision, and known-good target revision.
2. Confirm the target image is available and trusted, and its configuration and required secrets are compatible.
3. Review database migration compatibility. Do not roll back an application across an incompatible schema change; use an approved forward-fix or recovery plan instead.
4. For Transfer, Payment, or Ledger, establish how in-flight and unresolved operations will be protected and reconciled. A deployment rollback must not trigger unapproved financial retries.
5. Define owner-approved health, business-smoke, and stop criteria before starting.

## Rehearse

1. Capture the current Deployment revision, image digest, replica state, configuration reference, and service health.
2. Deploy a reversible test revision to the selected service using the approved release process. Do not change unrelated services or shared database ownership.
3. Verify rollout and health endpoints. If the test revision meets the pre-agreed rollback condition, obtain the required approval and use the platform’s supported rollback mechanism for this service only.
4. Verify the prior known-good revision is active, all expected replicas are ready, and non-destructive service/downstream smoke checks pass.
5. Confirm the exercise did not mutate financial outcomes, bypass idempotency, or apply an incompatible schema change.

## Evidence and cleanup

Record service, environment, before/after image digests and revisions, schema/migration version, approvals, start/end timestamps, health and smoke results, observed impact, and follow-up work. Restore the rehearsal environment to its agreed baseline and verify it is healthy.

Do not treat this procedure as a passed rehearsal until the platform and service owners review the evidence. Production registry, cluster topology, rollout mechanism, and rollback objectives remain owner decisions.
