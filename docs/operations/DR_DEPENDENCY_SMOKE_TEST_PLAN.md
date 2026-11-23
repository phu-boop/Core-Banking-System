# Disaster Recovery and Dependency-Failure Smoke Test Plan

**Status:** Planned; scenarios have not been executed against a production-like environment. Record approved thresholds and environment-specific procedures in [Operational Targets](./OPERATIONAL_TARGETS.md) before scheduling.

## Test controls

- Run only in an isolated environment with synthetic data and an approved test window.
- Obtain service-owner and platform-owner approval. Define stop conditions, notification route, and recovery owner before injecting a failure.
- Do not use production customer data or credentials. Do not trigger payment, transfer, reversal, or replay side effects unless explicitly approved and safely isolated.
- Capture start/end time, service revision, sanitized logs, health results, impact, recovery evidence, and approvers.
- Mark each scenario **NOT RUN**, **PASS**, **FAIL**, or **BLOCKED** with evidence; do not infer a pass from a successful deployment or unit test.

## Scenarios

| Scenario | Failure injection | Expected safe behavior | Recovery and evidence | Status |
|---|---|---|---|---|
| Database unavailable | In isolated environment, make the selected service’s PostgreSQL dependency unavailable using the platform-approved method. | Readiness reflects the dependency failure; liveness behavior remains consistent with the service’s probe policy; no cross-service schema writes occur. | Restore the dependency, confirm readiness and service-owned data integrity, record outage/recovery observations against approved targets. | NOT RUN |
| OIDC/Keycloak unavailable | Make the configured identity provider unavailable or unreachable in the test environment. | Authentication-dependent calls fail closed; no token validation is bypassed; health and error signals are captured. | Restore provider reachability, verify normal authenticated behavior with synthetic identities, record results. | NOT RUN |
| Downstream service unavailable | Block one approved internal dependency for a non-financial smoke call or isolated synthetic flow. | The caller surfaces an explicit failure; retries remain bounded by approved policy; no duplicate command or hidden success is reported. | Restore dependency, verify the flow and correlation across services, record any unresolved work. | NOT RUN |
| Failed application rollout | Deploy a known test revision that fails an agreed health/smoke check in a non-production environment. | Readiness prevents traffic to the failing revision; the deployment does not silently claim success. | Execute the approved single-service rollback rehearsal and verify the prior revision. | NOT RUN |
| Backup restore | Restore a selected approved backup to an isolated target using the rehearsal procedure. | Service-owned schema boundaries and authoritative financial/audit data remain intact; no restore overwrites production. | Complete integrity checks and record measured recovery point/time against approved objectives. | NOT RUN |
| Broker/outbox failure | Run only after a broker and outbox publisher are deployed in the test environment. | Delivery follows the approved retry/quarantine policy; duplicate delivery does not duplicate consumer side effects; backlog evidence is available. | Recover publisher/broker, verify controlled delivery and reconciliation. Current repository has no production broker/publisher, so this scenario is BLOCKED until provisioned. | BLOCKED |

## Exit criteria

The service/platform owner reviews each scenario’s evidence, unresolved risks, and deviations. Production readiness remains blocked for any critical scenario that is untested, failed, or lacks approved recovery objectives.

See [Alert Response](./ALERT_RESPONSE.md), [Service Recovery](./SERVICE_RECOVERY.md), [Backup and Restore Rehearsal](./BACKUP_RESTORE_REHEARSAL.md), [Rollback Rehearsal](./ROLLBACK_REHEARSAL.md), and [Outbox Operations](./OUTBOX_OPERATIONS.md) for the corresponding procedures.
