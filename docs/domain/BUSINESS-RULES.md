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
6. Retry cùng idempotency key không được tạo financial operation thứ hai. Scope/retention của key còn cần chốt.

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
- **OPEN:** policy snapshot/version khi tạo giao dịch và xử lý policy change trong lúc chờ duyệt.
- **MANDATORY control:** người tạo không tự phê duyệt giao dịch của chính mình nếu đó là Maker–Checker action; quyền phải được kiểm tra bởi service thực thi nghiệp vụ.
- **PROPOSED:** ghi actor, role/scope, step, decision, timestamp và policy reference trong history để audit; quy tắc rejection, expiry, delegation và resubmission chưa được xác nhận.

## Branch

- **ACCEPTED:** hỗ trợ nhiều chi nhánh.
- **ACCEPTED distinction:** `managingBranch` khác `transactionProcessingBranch`.
- **ACCEPTED:** Teller bị giới hạn bởi branch permissions.
- **OPEN:** customer có một hay nhiều managing branches.
- **PROPOSED:** mọi branch reference dùng `branchId`; Branch service sở hữu branch data, service khác không query bảng Branch trực tiếp.
- Assignment, transfer giữa branch, branch closure và timezone/cutoff chưa được xác định.

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
