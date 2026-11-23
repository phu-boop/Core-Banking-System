# CURRENT_STATUS.md

**Last audited**: 2026-11-22
**Git branch**: `feat/corporate-approval-core`
**Classification**: ON TRACK

## Current phase

Phase 8 — Production Readiness planning.

Phase 7 implementation is complete. Reporting, channel authorization, Audit and Notification have workspace verification evidence.
Phase 8 planning artifacts are now created and await execution.

## Current task

Continue Phase 8 in the following order:

1. Begin Phase 8 T001-T023 after owner approval of provider-neutral deployment and operational target inputs.

## Completed evidence

- Phase 1: Service template, schema ownership and foundation work.
- Phase 2: Identity, Party, Branch and Corporate bounded first slices.
- Phase 3: Account and Ledger services.
- Phase 4: Retail transfer and Ledger execution.
- Phase 5: Corporate approval and approval-enforced Transfer.
- Phase 6: Provider-neutral Payment sandbox and unresolved-outcome behavior.
- Workspace `./mvnw clean verify`: 101 tests pass, 0 fail.
- Audit module `./mvnw -f services/audit-service/pom.xml test`: 2 tests pass.
- Centralized API Documentation: `./mvnw clean verify` pass; Swagger UI and docs service health/API probes return HTTP 200.
- T004-T006: Account/Transfer channel and owner authorization tests pass.
- T009: Audit PostgreSQL Testcontainers integration passes.
- T010-T013: Notification persistence, mock delivery and tests pass.
- T014-T017: Reporting projection migrations, scoped queries, export deduplication and tests pass.
- T018-T020: processed-event deduplication boundaries and the Phase 7 smoke verdict pass.
- Commits include `53ff071` for the API docs service and `b459776` for the Audit service.

## Incomplete work

- Channel context and owner-side authorization are not consistently applied to Account/Transfer.
- Reporting PostgreSQL Testcontainers acceptance coverage is not yet complete.
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
