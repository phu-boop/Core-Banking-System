# Business Rules

Status dưới đây phân biệt requirement được xác nhận với thiết kế còn chờ duyệt. Mọi detail không có trong ACCEPTED/mandatory list không được suy diễn thành business requirement.

## Currency

- **ACCEPTED:** MVP chỉ sử dụng VND.
- **ACCEPTED:** Không triển khai FX trong MVP.
- **PROPOSED technical rule:** Java dùng `BigDecimal`, PostgreSQL dùng `NUMERIC`; scale/precision cụ thể cần chốt trước migration.

## Account

- **ACCEPTED:** MVP tập trung tài khoản thanh toán.
- **ACCEPTED:** Không tự thêm Savings Account hoặc Loan Account vào MVP.
- **PROPOSED boundary:** Account sở hữu vòng đời/status; Ledger sở hữu journal và số liệu tài chính.
- Các rule mở/đóng tài khoản, số dư tối thiểu, phí và điều kiện block/close chưa được xác nhận.

## Identity, authorization and customer data

- **ACCEPTED:** Keycloak is the initial Identity Provider, using OIDC. Authorization Code + PKCE is for interactive users; Client Credentials is for service identities. Keycloak owns login and token issuance; local services do not store passwords or mint tokens.
- **ACCEPTED:** Identity maps validated `(issuer, subject)` to a unique internal user identity. `partyId` is optional in the internal principal.
- **ACCEPTED:** authorization combines RBAC, scoped grants and resource-level checks. Initial scopes are `SELF`, `ORGANIZATION` and `BRANCH`; each resource owner enforces access. Detailed roles, grant inheritance and permissions remain OPEN.
- **ACCEPTED:** Party duplicate detection is soft-match with human review. Do not automatically merge/delete, or assume National ID, phone or email is globally unique. Match fields and thresholds remain OPEN.
- **ACCEPTED:** each Organization maps 1:1 to one Corporate-owned Organization aggregate and one Party of type `ORGANIZATION`, by logical ID reference without cross-schema FK.
- **OPEN:** approved PII profile fields, legal identity identifiers, jurisdiction-specific uniqueness and onboarding workflows.

## Idempotency and audit

- **ACCEPTED:** when an API uses idempotency, scope by actor, operation and business scope. Default retention is 24 hours. Same key/same payload returns the prior outcome; same key/different payload returns `409 Conflict`.
- Each command API documents whether idempotency is required and defines its business scope. Key format, persistence schema and operation-specific scopes remain implementation decisions; no shared global idempotency table is implied.
- **ACCEPTED:** sensitive-operation audit captures actor, action, resource type/ID, result, timestamp and correlation ID; add organization, channel and branch where applicable.
- Audit records are not editable via ordinary business APIs and require separate authorization for reads. Do not record credentials/tokens or unnecessary PII.
- Seven years is the project retention baseline, subject to jurisdiction-specific legal/compliance review before production. Storage, tamper-evidence and archival are not selected here.

## Transfer

- **ACCEPTED:** hỗ trợ internal và interbank transfer.
- **MANDATORY:** phải có idempotency và concurrency control để chống trùng và double spending.
- **MANDATORY:** phân biệt `accepted`, `processing`, `settled`, `failed` và trạng thái external outcome chưa xác định.
- **MANDATORY:** timeout không đồng nghĩa giao dịch thất bại; external outcome có thể chưa xác định cho đến khi được đối soát/xác minh.
- **OPEN:** transaction limits, business-day/cutoff, phí, transfer cancellation, settlement, reversal và reconciliation.
- **OPEN:** protocol, provider, callback/query semantics và timeout policy của interbank network.
- Contract và status mapping xem [API Contracts](../api/API-CONTRACTS.md) và [Event Contracts](../architecture/EVENT-CONTRACTS.md).

### Internal transfer — design constraints

1. Transfer xác thực caller, account ownership/eligibility, amount và idempotency key; rule limits/fees chưa được cung cấp.
2. Với Corporate, yêu cầu approval phải được xử lý trước execution theo policy được xác định; chi tiết snapshot còn OPEN.
3. Transfer điều phối atomic posting thông qua Ledger; không ghi account balance trực tiếp.
4. Ledger post double-entry journal trong transaction nguyên tử: tổng Debit = tổng Credit.
5. Transfer chỉ báo settled/completed theo kết quả Ledger/operation contract đã được duyệt.
6. Retry cùng idempotency key không được tạo financial operation thứ hai. Where idempotency is used, ADR-012 sets actor + operation + business scope and a 24-hour default retention; operation-specific scope remains to be defined.

### Interbank transfer — design constraints

1. Transfer điều phối; Payment là boundary với external network/provider.
2. Không giả định provider, protocol, settlement finality hoặc external API.
3. Sau submit, Transfer/Payment phải biểu diễn trạng thái chưa xác định nếu không thể kết luận outcome.
4. Không phát `TransferFailed` hoặc reversal chỉ do timeout; query/callback/reconciliation policy còn OPEN.
5. Ledger posting timing, hold/reservation, fees, settlement, reversal và reconciliation cần được quyết định theo network contract.

## Corporate Approval

- **ACCEPTED:** approval policy có cấu hình và hỗ trợ nhiều cấp duyệt.
- **ACCEPTED:** áp dụng Maker–Checker.
- **ACCEPTED:** lưu approval history.
- **ACCEPTED per ADR-007 (2026-11-20):** request giữ immutable policy version được chọn lúc submit; policy version mới không thay đổi request đang chờ.
- **ACCEPTED per ADR-007:** các level chạy tuần tự; request chỉ approved khi tất cả level bắt buộc đều approve; reject ở bất kỳ level nào là terminal cho request đó.
- **ACCEPTED per ADR-007:** resubmit sau reject tạo request ID mới và giữ nguyên request/history cũ.
- **ACCEPTED per ADR-007:** không hỗ trợ expiry hoặc delegation trong MVP.
- **MANDATORY control:** người tạo không tự phê duyệt giao dịch của chính mình nếu đó là Maker–Checker action; quyền phải được kiểm tra bởi service thực thi nghiệp vụ.
- **ACCEPTED audit baseline:** history ghi actor, action, resource, result, timestamp và correlation ID; approval history additionally identifies level, decision and applied policy version.
- **ACCEPTED per ADR-007:** policy management requires organization-scoped `approval-policy:manage`; activation is explicit; at most one policy version per organization and operation type may be active.
- **ACCEPTED per ADR-007:** each level defines a role/mandate scope and Corporate checks the actor's active Membership and Mandate at decision time. The owner-side authorization boundary is mandatory; detailed role/mandate catalogs and APIs remain OPEN.
- **ACCEPTED per ADR-007:** an owner service supplies opaque operation type/reference; Corporate records approval but does not execute the operation.
- **OPEN:** approval thresholds, exact role/mandate catalog, Membership/Mandate API contracts, and Keycloak claim mapping.

## Branch

- **ACCEPTED:** hỗ trợ nhiều chi nhánh.
- **ACCEPTED distinction:** `managingBranch` khác `transactionProcessingBranch`.
- **ACCEPTED:** Teller bị giới hạn bởi branch permissions.
- **ACCEPTED:** customer có một primary managing branch tại một thời điểm; `managingBranchId` khác transaction-processing branch. Reassignment là thao tác explicit và phải audit.
- **PROPOSED:** mọi branch reference dùng `branchId`; Branch service sở hữu branch data, service khác không query bảng Branch trực tiếp.
- **OPEN:** thời điểm phải gán managing branch, reassignment workflow/effective date, cross-branch operation, branch closure và timezone/cutoff.

## Loan

- **ACCEPTED / DEFERRED:** Loan không nằm trong MVP; dự kiến cho giai đoạn sau.
- Không tạo Loan Account, loan service hay loan workflow trong MVP.

## Ledger and financial correctness

1. **MANDATORY:** Ledger là nguồn dữ liệu chuẩn cho biến động tài chính.
2. **MANDATORY:** dùng double-entry bookkeeping; mỗi journal phải cân bằng `Total Debit = Total Credit`.
3. **MANDATORY:** posted journal entries immutable; correction bằng reversal entries, không update/delete posting cũ.
4. **MANDATORY:** posting atomically bên trong Ledger Service/database transaction.
5. **MANDATORY:** idempotency ngăn duplicate financial operations; concurrency control ngăn double spending.
6. **MANDATORY:** Transactional Outbox cho reliable event publishing; consumers phải idempotent.
7. **MANDATORY:** Java `BigDecimal`, PostgreSQL `NUMERIC`.
8. **MANDATORY:** Redis không phải source of truth cho financial balances.
9. **PROPOSED:** nếu cần balance read model/cache, phải rebuild/reconcile được từ Ledger; số precision, rounding và balance availability rules cần duyệt.

Xem [architecture](../architecture/BANKING_ARCHITECTURE.md), [Open Decisions](../architecture/OPEN-DECISIONS.md), [ERD](../database/ERD.md), [API Contracts](../api/API-CONTRACTS.md) và [Event Contracts](../architecture/EVENT-CONTRACTS.md).
