# PROJECT_ROADMAP.md

## Mục tiêu dự án

Xây dựng hệ thống Core Banking MVP cho Retail và Corporate Banking bằng kiến trúc microservices Java 21/Spring Boot 3, với:

- Identity, Party/Customer, Organization, Branch và Corporate authorization.
- Account và Ledger double-entry làm nguồn chuẩn nghiệp vụ tài chính.
- Retail internal transfer, Teller deposit/withdrawal và Corporate transfer approval.
- Provider-neutral interbank boundary.
- Internet Banking, Admin, Teller, Audit, Notification và Reporting.
- Schema-per-service, idempotency, owner-side authorization, audit và production readiness.

Loan nằm ngoài MVP.

## Trạng thái tổng thể

**AT RISK** — Workspace đã có build/test pass và các implementation slice đến Phase 6, nhưng tài liệu trạng thái chưa nhất quán, Phase 7 mới chỉ hoàn thành scaffold/batch đầu, còn nhiều quyết định production còn OPEN.

## Các phase

| Phase | Objective | Status | Dependency / evidence |
|---|---|---|---|
| 0 | Chốt contract, mô hình và MVP decisions | Complete for baseline; open decisions remain | Architecture, API, event, ERD và business rules |
| 1 | Technical foundation | Complete | Service template, schema isolation, Flyway, CI and observability foundation |
| 2 | Identity, Party, Corporate and Branch | Complete — bounded first slice | 12 Identity + 3 Party + 4 Branch + 21 Corporate tests in previous workspace evidence |
| 3 | Account and Ledger | Complete — bounded first slice | Account/Ledger services, 3 Account + 4 Ledger tests |
| 4 | Retail internal transfer and Teller | Complete — bounded first slice | Retail transfer and Ledger execution, 12 Transfer tests |
| 5 | Corporate approval and transfer enforcement | Complete — bounded first slice | Corporate approval and approval-enforced Transfer implementation |
| 6 | Interbank transfer | Complete — provider-neutral sandbox | 70-test smoke evidence + mock provider; real provider remains deferred |
| 7 | Channels, Audit, Notification and Reporting | In progress | Audit module scaffolded and 2 unit tests pass; channel authorization, Notification and Reporting remain |
| 8 | Production readiness | Not started | Kubernetes provider/topology, security, load/recovery and runbook work remain |
| Later | Loan | Deferred | Out of MVP |

## Phases 0-2

### Phase 0

- Objective: Establish accepted architecture and MVP boundaries.
- Acceptance: Contracts and decisions are consistent and blockers are recorded.
- Evidence: ADRs, architecture, API, event, ERD and business rules.

### Phase 1

- Objective: Service foundation and schema-per-service.
- Acceptance: Service template builds/tests, migration ownership and CI are verified.
- Evidence: `docs/plan/phase/Phase-1-Technical-Foundation.md`.

### Phase 2

- Objective: Identity, Party/Customer, Corporate and Branch first slice.
- Acceptance: Owner-side authorization, logical references and audit tests pass.
- Evidence: `docs/plan/phase/Phase-2-Identity-Party-Corporate-Branch.md`.

## Phases 3-6

### Phase 3

- Objective: Account lifecycle and Ledger double-entry.
- Acceptance: Debit/Credit balance, immutable posting and reversal.
- Evidence: `docs/plan/phase/Phase-3-Account-Ledger.md` and Ledger/Account tests.

### Phase 4

- Objective: Retail internal transfer and Teller.
- Acceptance: Idempotent transfer, Ledger posting and timeout as processing/unresolved.
- Evidence: `docs/plan/phase/Phase-4-Retail-Internal-Transfer.md` and Transfer tests.

### Phase 5

- Objective: Corporate approval and approval-enforced Transfer.
- Acceptance: Maker-Checker, immutable policy version, append-only history and no execution before approval.
- Evidence: Corporate approval and corporate transfer checkpoints.
- Dedicated implementation plan: `docs/plan/phase/corporate-approval/plan.md`.

### Phase 6

- Objective: Provider-neutral interbank boundary.
- Acceptance: Submission idempotency, unresolved timeout, outcome deduplication and no Ledger/settlement side effects.
- Evidence: `docs/plan/phase/interbank-transfer/checkpoints/SMOKE_TEST_VERDICT.md`.
- Historical scope: `docs/plan/phase/Phase-6-Interbank-Transfer.md`.

## Phase 7

### Objective

Complete channel-aware operations and provider-neutral Audit, Notification and Reporting behavior.

### Scope

- Internet Banking, Admin and Teller authorization.
- Service-owner resource, organization, account and branch scope.
- Durable and deduplicated Audit records.
- Provider-neutral Notification requests and delivery attempts.
- Scoped Reporting projections and bounded exports.

### Dependencies

- Phase 2-6 bounded slices.
- API Gateway technology remains OPEN.
- Event broker remains OPEN.
- Notification provider remains OPEN.

### Acceptance

- No cross-resource/channel/branch access.
- Duplicate audit and notification commands have no duplicate side effects.
- Reporting never becomes a balance or Ledger authority.
- Workspace verification remains green.

### Current evidence

- Phase 7 spec: `docs/specs/phase-7-channels-operations/feature-spec.md`.
- Phase 7 plan: `docs/plan/phase/channels-operations/plan.md`.
- Audit module unit tests pass.
- Full workspace currently passes 72 tests.

## Phase 8

- Objective: Production release readiness.
- Acceptance: Security review, load/concurrency tests, HA/DR, monitoring, backup/restore, deployment and rollback.
- Status: Not started.

## Critical dependencies and open decisions

| Decision | Status | Blocks |
|---|---|---|
| External interbank network/provider/protocol | OPEN | Real Phase 6 integration |
| Interbank settlement/reversal/reconciliation | OPEN | Financial production behavior |
| API Gateway | OPEN | Public channel routing |
| Event broker | OPEN | Notification/Audit/Reporting event transport |
| Transaction limits and business-day rules | OPEN | Production validation policy |
| Production Kubernetes provider/topology | OPEN | Production deployment |
| Production external secret store/workload identity | OPEN | Production secrets |

## Source-of-truth rules

- `CURRENT_STATUS.md`: current execution state and next action.
- `PROJECT_ROADMAP.md`: phase-level objectives, dependencies and milestones.
- `docs/adr/`: accepted architectural decisions and historical rationale.
- `docs/architecture/`: architecture and contract references.
- `docs/specs/<feature>/`: feature requirements.
- `docs/plan/phase/<feature>/`: implementation plan and checkpoint evidence for active/historical slices.

Detailed historical plans remain preserved; they must not override this roadmap or `CURRENT_STATUS.md` when their status text is stale.
