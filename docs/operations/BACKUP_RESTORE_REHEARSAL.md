# Backup and Restore Rehearsal

**Status:** Procedure only; no production backup, restore, RPO/RTO, or rehearsal evidence is implied.

## Before the rehearsal

1. Obtain approval from the data/service owner and change approver. Record the service, environment, database/schema scope, backup point, rehearsal window, and rollback/stop conditions.
2. Confirm approved RPO/RTO and data-retention policy in [Operational Targets](./OPERATIONAL_TARGETS.md). If targets or backup integrity cannot be established, stop and resolve the gap before claiming readiness.
3. Use an isolated restore target with access controls equivalent to the approved rehearsal environment. Never restore over production for a rehearsal.
4. Confirm the backup is encrypted, access is authorized, and logs/evidence will not expose credentials or protected data.
5. Record the service-owned schema and expected Flyway migration version. Preserve schema ownership; do not combine services into a shared writable schema.

## Rehearse restore

1. Select the approved backup/PITR point and record its timestamp and evidence identifier.
2. Restore using the platform’s approved database procedure into the isolated target.
3. Verify database availability, service-owned schema boundaries, migration history, and application compatibility without applying unapproved migrations or manual data edits.
4. Run owner-approved integrity checks, including relevant financial and audit reconciliation checks. Compare only authorized, minimized evidence; do not copy production PII into ordinary test reports.
5. Start or validate the service against the isolated restore using approved credentials. Confirm readiness and non-destructive service-specific smoke checks.
6. Measure achieved recovery point and elapsed recovery time, then compare them with the approved RPO/RTO. Record any data gaps, failures, or deviations.

## Complete and clean up

1. Obtain service/data owner review of integrity and recovery evidence.
2. Record backup identifier, restore point, migration version, measured RPO/RTO, test results, approvers, and follow-up actions in the controlled rehearsal record.
3. Securely dispose of the isolated restored data according to its approved handling and retention policy.
4. Do not report the rehearsal as passed until the owner accepts the evidence and any deviations are resolved or formally accepted.

## Service-specific cautions

- Preserve Ledger as the financial posting authority; validate journals and reversals through approved domain checks.
- Reconcile unresolved Transfer/Payment operations with their authoritative outcomes before retrying or replaying.
- Preserve Audit records and access boundaries; a successful database restore alone does not establish audit retention or tamper-evidence.
- Never infer production RPO/RTO, retention, or backup guarantees from local development or Testcontainers.
