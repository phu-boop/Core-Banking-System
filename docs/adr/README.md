# Architecture Decision Records

Các ADR dưới đây ghi nhận quyết định đã được chủ dự án xác nhận. Ngày của ADR-001 đến ADR-009 không được cung cấp nên được ghi là **Not recorded** trong từng ADR; ADR-010 ghi ngày quyết định được xác nhận.

| ADR | Title | Status |
|---|---|---|
| [ADR-001](./ADR-001-microservices-architecture.md) | Microservices Architecture | ACCEPTED |
| [ADR-002](./ADR-002-retail-corporate-banking.md) | Retail + Corporate Banking | ACCEPTED |
| [ADR-003](./ADR-003-multi-channel.md) | Multi-channel Banking | ACCEPTED |
| [ADR-004](./ADR-004-shared-postgresql-schema-per-service.md) | Shared PostgreSQL, Schema-per-service | ACCEPTED |
| [ADR-005](./ADR-005-vnd-only.md) | VND Only | ACCEPTED |
| [ADR-006](./ADR-006-internal-and-interbank-transfer.md) | Internal + Interbank Transfer | ACCEPTED |
| [ADR-007](./ADR-007-configurable-corporate-approval.md) | Configurable Corporate Approval | ACCEPTED |
| [ADR-008](./ADR-008-loan-excluded-from-mvp.md) | Loan Excluded from MVP | ACCEPTED; scope DEFERRED |
| [ADR-009](./ADR-009-multi-branch-banking.md) | Multi-branch Banking | ACCEPTED |
| [ADR-010](./ADR-010-phase-1-technical-baseline.md) | Phase 1 Technical Baseline | ACCEPTED |
| [ADR-011](./ADR-011-phase-based-secret-management.md) | Phase-based Configuration and Secret Management | ACCEPTED; exact production providers OPEN |
| [ADR-012](./ADR-012-phase-2-identity-party-corporate-branch.md) | Phase 2 Identity, Party, Corporate and Branch Decisions | ACCEPTED; implementation details remain bounded by the ADR |
| [ADR-013](./ADR-013-identity-authoritative-user-authorization.md) | Identity-Authoritative User Authorization | ACCEPTED for the first customer account-read slice |
| [ADR-014](./ADR-014-role-permission-and-identity-grant-framework.md) | Role, Permission and Identity Grant Framework | ACCEPTED and implemented for the initial shared catalog; management/provisioning APIs remain deferred |

Decision details remain intentionally bounded by the recorded decision. Implementation options are not approved merely because they appear in design documents.

Xem [architecture overview](../architecture/BANKING_ARCHITECTURE.md), [open decisions](../architecture/OPEN-DECISIONS.md), [logical ERD](../database/ERD.md) và [authorization data dictionary](../database/AUTHORIZATION-DATA-DICTIONARY.md).
