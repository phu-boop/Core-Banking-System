# CURRENT_STATUS.md

**Last audited**: 2026-11-23
**Git branch**: `feat/corporate-approval-core`
**Classification**: ON TRACK

## Current phase

Phase 8 — Production Readiness implementation.

Phase 7 implementation is complete. Reporting, channel authorization, Audit and Notification have workspace verification evidence.
Phase 8 implementation is in progress. T001-T022 have been implemented; T023 remains gated on owner approval and unresolved evidence.

## Current task

T023 remains blocked until project-owner approval and required CI/rehearsal evidence are available. The local Kubernetes overlay now has fresh evidence: 25 resources render successfully.

## Completed evidence

- Phase 1: Service template, schema ownership and foundation work.
- Phase 2: Identity, Party, Branch and Corporate bounded first slices.
- Phase 3: Account and Ledger services.
- Phase 4: Retail transfer and Ledger execution.
- Phase 5: Corporate approval and approval-enforced Transfer.
- Phase 6: Provider-neutral Payment sandbox and unresolved-outcome behavior.
- Workspace `./mvnw -B -ntp clean verify`: 104 tests pass, 0 fail after Phase 8 T008.
- Audit module `./mvnw -f services/audit-service/pom.xml test`: 2 tests pass.
- Centralized API Documentation: `./mvnw clean verify` pass; Swagger UI and docs service health/API probes return HTTP 200.
- T004-T006: Account/Transfer channel and owner authorization tests pass.
- T009: Audit PostgreSQL Testcontainers integration passes.
- T010-T013: Notification persistence, mock delivery and tests pass.
- T014-T017: Reporting projection migrations, scoped queries, export deduplication and tests pass.
- T018-T020: processed-event deduplication boundaries and the Phase 7 smoke verdict pass.
- Phase 8 T001-T005: root reactor CI verification, test-count reporting, builds of existing Dockerfiles, release checklist, deployment catalog and actuator probes are implemented.
- Phase 8 T006-T007: local Deployment/Service manifests cover all 12 deployable services; secret references are documented without committing secret resources.
- Phase 8 T008: required datasource/client environment values and nonblank OIDC settings are validated; full workspace verify passes (104 tests, 0 failures).
- Phase 8 T009: CI scans full Git history for secrets with Gitleaks; findings cannot be suppressed by excluding production paths, and PR comments/artifact uploads are disabled.
- Phase 8 T010: focused tests verify Authorization headers and password parameters are absent from request-filter logs in the service template and Identity service; both target modules pass.
- Phase 8 T011: the open-decision register distinguishes the accepted External Secrets Operator integration pattern from the still-open provider and workload-identity choices.
- Phase 8 T012: CI smoke-tests API docs `/actuator/health`, liveness, readiness and JVM memory metrics in the built container; all four probes pass locally.
- Phase 8 T013-T014: alert response, service recovery, and outbox backlog/retry/replay procedures are documented; the outbox runbook explicitly says production publisher/broker operations are not implemented yet.
- Phase 8 T015-T017: owner-input-only SLO/alert/retention target register plus backup/restore and independent rollback rehearsal procedures are documented. No rehearsal evidence is claimed.
- Phase 8 T018: a concurrent duplicate Ledger journal command exposed and fixed a race that previously returned HTTP 500 after a uniqueness conflict; PostgreSQL `ON CONFLICT DO NOTHING` now returns the committed journal without duplicating entries/audit. Ledger suite passes 5 tests.
- Phase 8 T019: documented DR and dependency-failure smoke scenarios with explicit unrun/blocked statuses; broker/outbox test is blocked until those components exist.
- Phase 8 T020: full `./mvnw -B -ntp clean verify` passes 107 tests across 13 service modules, 0 failures/errors/skips; module counts are recorded in the checkpoints.
- Phase 8 T021-T022: security/recovery evidence and smoke verdict document what passed locally and clearly mark unrun production rehearsals/scans; verdict is NOT APPROVED FOR PRODUCTION.
- Commits include `53ff071` for the API docs service and `b459776` for the Audit service.

## Incomplete work

- Channel context and owner-side authorization are not consistently applied to Account/Transfer.
- Reporting PostgreSQL Testcontainers acceptance coverage is not yet complete.
- Real interbank provider and settlement/reconciliation are OPEN.
- API Gateway, event broker and production deployment decisions remain OPEN.
- Phase 8 secret scanning, operational runbooks and recovery evidence remain incomplete. Local Keycloak uses HTTP while several service validators require HTTPS; local deployment readiness is not confirmed.

## Blockers

- API Gateway, event broker and notification provider decisions require owner approval.
- Interbank provider/protocol and settlement/reversal/reconciliation rules require owner approval.
- Production Kubernetes and external secret-store decisions require owner approval.
- Docker availability is required for remaining PostgreSQL integration acceptance gates.

## Working tree

The worktree contains substantial pre-existing implementation and documentation changes. Changes must be preserved and audited individually.

The highest-priority next action is not adding another plan: implement one verified security slice and update this status with test evidence.
