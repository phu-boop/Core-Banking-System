# Phase 7 Smoke Test Verdict

**Date**: 2026-11-22
**Status**: PASS

## Command

```bash
./mvnw clean verify
```

## Evidence

| Module | Tests | Result |
|--------|------:|--------|
| service-template | 16 | PASS |
| identity-service | 12 | PASS |
| party-service | 3 | PASS |
| branch-service | 4 | PASS |
| corporate-service | 21 | PASS |
| account-service | 5 | PASS |
| ledger-service | 4 | PASS |
| transfer-service | 14 | PASS |
| payment-service | 6 | PASS |
| audit-service | 3 | PASS |
| api-docs-service | 2 | PASS |
| notification-service | 5 | PASS |
| reporting-service | 6 | PASS |
| **Total** | **101** | **PASS** |

The Maven reactor completed with `BUILD SUCCESS`.

## Journey Coverage

- Internet Banking and cross-customer authorization: Account and Transfer tests pass.
- Teller and Admin scope boundaries: Channel authorization tests pass.
- Audit ingestion and replay deduplication: Audit tests pass.
- Notification durable state and duplicate replay: Notification tests pass.
- Reporting scope, range, export format and projection watermark: Reporting tests pass.
- Generic at-least-once consumer deduplication: Service-template processed-event integration coverage passes.

## Deduplication Boundaries

Audit, Notification and Reporting own `processed_events` tables inside their existing schemas:

- `audit_schema.processed_events`
- `notification_schema.processed_events`
- `reporting_schema.processed_events`

Each primary key is `(event_id, consumer_name)`, matching at-least-once replay semantics.

## Deferred Scope

Live API Gateway routing, event broker delivery, production notification provider behavior and Docker-backed runtime probes remain deferred until the corresponding decisions are approved.
