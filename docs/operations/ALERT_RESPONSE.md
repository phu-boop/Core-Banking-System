# Alert Response Runbook

**Status:** Operational baseline; service ownership, paging routes, severity mapping and SLO thresholds require owner approval.

Maintain approved thresholds and retention values in [Operational Targets](./OPERATIONAL_TARGETS.md).

## First response

1. Acknowledge the alert in the approved incident system and record its firing time, affected service/environment, alert name, and dashboard or log links.
2. Assign an incident lead and service owner using the approved on-call roster. Set severity using the organization’s incident policy; do not infer severity from an unapproved threshold.
3. Determine customer and financial impact. Escalate immediately for suspected data integrity issues, unknown payment/transfer outcomes, duplicate postings, or exposed credentials/PII.
4. Preserve relevant timestamps, correlation IDs, deployment revisions, and sanitized logs. Do not copy tokens, passwords, or unnecessary customer data into tickets or chat.

## Triage

1. Check the affected workload and recent rollout status using the approved cluster dashboard or read-only Kubernetes inspection.
2. Check the service’s `/actuator/health`, `/actuator/health/liveness`, and `/actuator/health/readiness` endpoints. A healthy liveness response does not prove dependencies or business flows are healthy.
3. Review available service metrics and logs for the incident window. Metrics endpoints and alert thresholds vary by service and must be confirmed in the deployed monitoring configuration.
4. Check dependencies relevant to the service, including PostgreSQL, Keycloak/OIDC, and downstream services. Separate a dependency outage from a service regression before taking action.
5. Compare the incident start time with deployments, configuration changes, secret rotations, migrations, and provider incidents.

## Response and recovery

- Follow [Service Recovery](./SERVICE_RECOVERY.md) for a service-level failure.
- Follow [Outbox Operations](./OUTBOX_OPERATIONS.md) only for a deployed outbox implementation whose owner and procedures have been approved.
- Do not change SLO thresholds, retry limits, or alert routing during an incident without the accountable owner’s approval.
- Do not manually edit financial records, bypass service authorization, or replay a command/event to clear an alert.
- For a suspected secret exposure, restrict access and follow the approved credential rotation and security incident process. Never paste the exposed value into the incident record.

## Resolution and handoff

1. Confirm service health and the affected customer/business flow with the service owner; record evidence and the observation window defined by the approved incident policy.
2. Confirm any uncertain financial outcomes have been reconciled by the authoritative service before closing the incident.
3. Record actions, approvals, deployment/configuration revisions, impact, and follow-up owners. Keep credentials and protected PII out of the report.
4. Close or downgrade alerts only after the alert owner’s recovery criteria are met. Schedule a post-incident review for material or repeated incidents.

## Owner inputs required

- On-call roster, escalation paths, severity definitions, and incident communication channels.
- Per-service dashboards, alert ownership, approved SLOs/thresholds, and recovery observation windows.
- Security incident and credential rotation procedures.
