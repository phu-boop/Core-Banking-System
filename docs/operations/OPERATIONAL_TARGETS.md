# Operational Targets — Owner Inputs

**Status:** OPEN. No numeric SLO, alert, or retention target is approved by this document. Production readiness requires accountable owners to fill in and approve each applicable value.

Use the service/environment-specific values in the deployed monitoring and data-retention configuration. Do not treat blank fields, examples, or development settings as production defaults.

| Target area | Owner-approved input required | Approved value / policy | Accountable owner | Approval date / evidence | Status |
|---|---|---|---|---|---|
| Service SLO | Availability objective, measurement window, exclusions, and service scope for each production service | Not set | Not assigned | Not recorded | OPEN |
| Request latency and errors | Percentiles, error-rate objective, measurement window, and eligible request classes | Not set | Not assigned | Not recorded | OPEN |
| Alert thresholds | Trigger, duration, severity, notification route, and responder for each alert | Not set | Not assigned | Not recorded | OPEN |
| Database health | Connection saturation, storage, replication/backup health, and alert thresholds | Not set | Not assigned | Not recorded | OPEN |
| Event/outbox delivery | Backlog depth/age, retry, quarantine, and recovery thresholds for each deployed publisher/consumer | Not set | Not assigned | Not recorded | OPEN |
| Recovery objectives | RPO, RTO, recovery observation window, and scope by service/data class | Not set | Not assigned | Not recorded | OPEN |
| Audit and security records | Retention, archive, access, and deletion policy | Not set | Not assigned | Not recorded | OPEN |
| Business and reporting data | Retention and deletion policy by data class and jurisdiction | Not set | Not assigned | Not recorded | OPEN |
| Metrics and operational logs | Retention, access controls, and protected-data redaction requirements | Not set | Not assigned | Not recorded | OPEN |

## Approval record

For every row that applies, record the approved value or policy, accountable owner, approval date, and durable evidence link. Mark a row **NOT APPLICABLE** only with an owner and rationale. Until then, alerts, runbooks, backup/restore rehearsals, and production release gates must not assume a threshold.
