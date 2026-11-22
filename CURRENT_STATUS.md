# CURRENT_STATUS.md

**Last audited**: 2026-11-22
**Git branch**: `feat/corporate-approval-core`
**Classification**: AT RISK

## Current phase

Phase 7 — Channels, Audit, Notification and Reporting.

Implementation is not complete: an Audit module scaffold and unit tests have been added, while channel-aware owner authorization, PostgreSQL integration coverage, Notification and Reporting remain unfinished.

## Current task

Complete the highest-priority security slice:

1. Apply and test owner-side channel/resource/organization/branch authorization in existing Account and Transfer routes.
2. Add PostgreSQL Testcontainers coverage for Audit persistence and schema isolation.

Do not start Notification or Reporting until the current channel/Audit slice passes.

## Completed evidence

- Phase 1: Service template, schema ownership and foundation work.
- Phase 2: Identity, Party, Branch and Corporate bounded first slices.
- Phase 3: Account and Ledger services.
- Phase 4: Retail transfer and Ledger execution.
- Phase 5: Corporate approval and approval-enforced Transfer.
- Phase 6: Provider-neutral Payment sandbox and unresolved-outcome behavior.
- Workspace `./mvnw verify`: 72 tests pass, 0 fail.
- Audit module `./mvnw -f services/audit-service/pom.xml test`: 2 tests pass.

## Incomplete work

- Channel context and owner-side authorization are not consistently applied to Account/Transfer.
- Audit has no PostgreSQL Testcontainers acceptance test yet.
- Notification service is not implemented.
- Reporting service is not implemented.
- Real interbank provider and settlement/reconciliation are OPEN.
- API Gateway, event broker and production deployment decisions remain OPEN.
- Phase 8 is not started.

## Blockers

- API Gateway, event broker and notification provider decisions require owner approval.
- Interbank provider/protocol and settlement/reversal/reconciliation rules require owner approval.
- Production Kubernetes and external secret-store decisions require owner approval.
- Docker availability is required for remaining PostgreSQL integration acceptance gates.

## Working tree

The worktree contains substantial pre-existing implementation and documentation changes. Changes must be preserved and audited individually.

The highest-priority next action is not adding another plan: implement one verified security slice and update this status with test evidence.
