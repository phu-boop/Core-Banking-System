# Outbox Backlog, Retry, and Replay Runbook

**Status:** Design/operations guidance only. The repository defines the transactional outbox pattern, but does not currently provide a production outbox publisher, broker, backlog metric, or dead-letter workflow. Do not execute these procedures against a service until its deployed outbox implementation and owner-approved tooling are identified.

## Scope and guardrails

- The producer owns its outbox and domain data in the same service-owned database transaction. Consumers own their processed-event/idempotency records.
- Broker choice, retry limits/backoff, ordering, retention, dead-letter policy, and replay authorization remain open; use only values approved for the deployed implementation.
- Never update/delete outbox rows or broker messages directly, bypass consumer idempotency, or replay financial events to reduce a backlog.
- `PaymentSettled`, `TransferCompleted`, `TransferFailed`, `JournalPosted`, and `JournalReversed` require a verified authoritative outcome. A timeout is not evidence that an operation failed.

## Assess a backlog

1. Open an incident when an approved backlog/age alert fires; identify producer, consumer, event type, environment, and incident owner.
2. Use the deployed publisher, broker, and consumer dashboards or supported read-only tools to establish queue depth, oldest-event age, publish/consume rates, retry counts, and dead-letter counts.
3. Compare the signals with the approved service-specific thresholds. Do not invent a threshold or infer that a growing queue is safe.
4. Inspect sanitized error categories and correlation/event IDs. Do not copy payloads containing credentials or protected PII into incident notes.
5. Check producer database health, publisher health, broker availability, consumer readiness, schema compatibility, and recent deployments/configuration changes.

## Restore normal delivery

1. Identify whether failure is transient, capacity-related, schema/validation-related, or an unknown financial outcome.
2. Correct the confirmed cause through the approved deployment/configuration process. For transient errors, allow the configured publisher retry policy to operate; do not create a second publisher or manually resend messages.
3. For permanent or poison-message failures, quarantine or dead-letter only through the implementation’s supported, audited operation and owner approval. Preserve the event identity and failure evidence.
4. Observe backlog age/depth, retry rate, and consumer outcomes until the owner-approved recovery criteria are met.

## Controlled replay

Replay is a separate approved operation, not a generic backlog-clearing action.

1. Obtain producer and consumer owner approval and, for financial events, approval from the relevant Transfer, Payment, or Ledger owner.
2. Select explicit event IDs and establish the expected business outcome from the authoritative service. Confirm event schema/version and whether the target consumer has already applied the event.
3. Verify that the consumer’s stable event ID/business idempotency key and unique processed marker are active and retained for the replay window.
4. Use only the deployed replay tool with an auditable operator identity, bounded selection, and a documented stop condition. Never edit payloads or reuse an event ID for a different operation.
5. Monitor each replayed event through publish, consume, and resulting business state. Stop on duplicate side effects, inconsistent outcomes, or renewed errors and escalate.
6. Record approvers, event IDs, reason, tool/run identifier, outcomes, and follow-up actions without recording sensitive payloads.

## Implementation prerequisites

- Deployed outbox publisher and broker-specific dashboards/tools.
- Approved retry/backoff, quarantine/dead-letter, retention, replay authorization, and alert thresholds.
- Per-event idempotency and reconciliation evidence, plus a documented owner and stop condition.

The event delivery principles and unresolved broker decisions are documented in [Event Contracts](../architecture/EVENT-CONTRACTS.md).
