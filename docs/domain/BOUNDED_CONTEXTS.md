# Bounded Contexts

12 contexts dưới đây là service boundaries **PROPOSED** theo kiến trúc microservices ACCEPTED. Identity & Access, Party / Customer, Branch và Corporate Organization đã có first-slice implementation; các capability còn lại vẫn là thiết kế. Mỗi service sở hữu schema tương ứng trong [ERD](../database/ERD.md). Mọi reference sang context khác là ID/API/event, không phải direct table access hay foreign key.

| Context | Schema | Nguồn dữ liệu sở hữu |
|---|---|---|
| Identity & Access | `identity_schema` | External identity mappings, users and authorization grants; Keycloak owns credentials and tokens |
| Party / Customer | `party_schema` | Party và hồ sơ cá nhân/customer |
| Corporate | `corporate_schema` | Organization, membership, mandate, approval policy/history |
| Branch | `branch_schema` | Branch và branch status |
| Account | `account_schema` | Deposit account lifecycle và account status |
| Transaction | `transaction_schema` | Generic financial operation record/status |
| Transfer | `transfer_schema` | Transfer instructions và transfer orchestration |
| Ledger | `ledger_schema` | Journal, entries và posting/reversal |
| Payment | `payment_schema` | External payment submission/outcome references |
| Notification | `notification_schema` | Notification requests/delivery attempts |
| Audit | `audit_schema` | Audit records |
| Reporting | `reporting_schema` | Read-only reporting projections |

Chi tiết endpoint/DTO vẫn **PROPOSED**; synchronous service-to-service API dùng REST trong first slice theo [ADR-012](../adr/ADR-012-phase-2-identity-party-corporate-branch.md), endpoint matrix ở [API-CONTRACTS.md](../api/API-CONTRACTS.md). Event names/schema và broker transport ở [EVENT-CONTRACTS.md](../architecture/EVENT-CONTRACTS.md) vẫn chưa được chốt.

## 1. Identity & Access

- **Trách nhiệm:** tích hợp Keycloak qua OIDC, ánh xạ external identity, user lifecycle, RBAC/grants và principal resolution; Keycloak sở hữu login, password management và token issuance.
- **Entities sở hữu (PROPOSED):** `UserIdentity` với external `(issuer, subject)` duy nhất, `Role`, `Permission`, `UserRole` và scoped grant. Principal có `partyId` optional; không lưu password hoặc token.
- **Schema:** `identity_schema`; không sở hữu customer, account hay balance.
- **API:** resolve validated identity principal; quản lý user/role/grant theo quyền. Login/token endpoints thuộc Keycloak, không thuộc service này.
- **Publish:** chưa có domain event Identity nào được xác định; security audit input gửi tới Audit cần contract riêng, chưa định nghĩa.
- **Consume:** `PartyCreated` nếu user-party linking được duyệt; branch/org events chỉ nếu policy cần local projection.
- **Dependencies:** Keycloak/OIDC theo ADR-012; Party/Corporate/Branch/Account là resource owners được tham chiếu bằng ID.
- **Không được:** quản lý tài khoản tiền gửi, quyết định số dư, ghi Ledger, hoặc là authorization authority duy nhất thay service sở hữu resource.

## 2. Party / Customer

- **Trách nhiệm:** định danh party và customer profile chung cho cá nhân/doanh nghiệp; Corporate quản lý Organization aggregate.
- **Entities sở hữu (PROPOSED):** `Party`, `IndividualProfile`, customer contact/profile phù hợp PII fields được duyệt; một customer có tối đa một primary `managingBranchId` tại một thời điểm theo ADR-012.
- **Schema:** `party_schema`.
- **API:** tạo/đọc/cập nhật customer/party profile theo policy; không cung cấp thao tác tiền.
- **Publish:** `PartyCreated` (các profile update events chưa được xác định).
- **Consume:** Identity/user events nếu cần liên kết; không consume để ghi ngược Party từ service khác.
- **Dependencies:** Identity cho actor authorization; Corporate cho organization context; Branch ID là logical reference. Assignment/reassignment workflow và thời điểm bắt buộc gán managing branch vẫn cần contract.
- **Duplicate handling:** soft match và human review; không tự merge/xóa và không mặc định unique National ID, phone hoặc email. Matching fields/weights/thresholds còn OPEN.
- **Không được:** sở hữu credentials, organization membership/approval policy, account lifecycle hoặc ledger entries.

## 3. Corporate

- **Trách nhiệm:** quản lý organization, membership, mandate, quyền nghiệp vụ doanh nghiệp và configurable approval policy/workflow.
- **Entities sở hữu (PROPOSED):** `Organization`, `Membership`, `Mandate`, `ApprovalPolicy`, `ApprovalRequest`, `ApprovalStep`, `ApprovalHistory`. Corporate hiện đã có Organization, policy, Membership, Mandate, approval request/step/history implementation; exact Identity claim mapping remains a deployment/configuration integration concern.
- **Schema:** `corporate_schema`; mỗi Organization tham chiếu đúng một Party loại `ORGANIZATION` qua logical `partyId` theo ADR-012, không phải FK.
- **API:** first slice tạo/đọc Organization sau khi Party owner xác nhận `partyId` thuộc loại `ORGANIZATION`; membership/mandate/policy và approval/decision chưa triển khai.
- **Publish:** `TransferApproved` sau quyết định; denial/change events chưa được xác định.
- **Consume:** `PartyCreated`, `TransferRequested`; có thể cần Identity grants/Branch refs qua API.
- **Dependencies:** Party owner API qua REST và Keycloak Client Credentials; claim/audience mapping cần cấu hình và xác nhận. Identity actor/grants, Transfer workflow và Audit là dependencies cho capability sau; policy snapshot semantics còn OPEN.
- **Không được:** tự ghi ledger, xác nhận external settlement, sở hữu customer credential hoặc bypass Maker–Checker.

## 4. Branch

- **Trách nhiệm:** quản lý multi-branch, branch status và phạm vi servicing; managing branch và transaction-processing branch là hai khái niệm riêng.
- **Entities sở hữu (PROPOSED):** `Branch`, branch configuration/status.
- **Schema:** `branch_schema`.
- **API:** first slice tạo/đọc/list branch và kiểm tra branch visibility; quản lý lifecycle/status transitions còn cần contract.
- **Publish:** branch lifecycle events chưa được yêu cầu đặt tên.
- **Consume:** Identity grants hoặc domain events nếu cần projection; contract chưa xác định.
- **Dependencies:** Identity để authorization; Account/Transfer/Teller workflows tham chiếu branch ID. First slice nhận global permission và scoped branch-read grants từ validated JWT theo claim convention provisional; mapping chính thức với Identity/Keycloak còn OPEN. Teller access là branch-scoped; customer reassignment phải được audit.
- **Không được:** sở hữu Teller credentials/roles, customer/account, hoặc ghi financial transaction.

## 5. Account

- **Trách nhiệm:** quản lý vòng đời tài khoản thanh toán MVP, trạng thái mở/blocked/closed theo policy được xác nhận.
- **Entities sở hữu (PROPOSED):** `Account`, account status/history, account-party ownership/relationship.
- **Schema:** `account_schema`; account references `partyId`; customer managing branch thuộc Party, còn transaction-processing branch là context giao dịch riêng.
- **API:** mở/đọc/block/unblock tài khoản theo quyền; cung cấp account eligibility/status cho Transfer/Ledger integration.
- **Publish:** `AccountOpened`, `AccountBlocked`; các status events khác cần định nghĩa.
- **Consume:** `PartyCreated` nếu customer validation qua projection được duyệt.
- **Dependencies:** Party identity, Branch reference, Ledger posting integration.
- **Không được:** tự ghi sổ kế toán, tự quyết định authoritative balance, hoặc lưu/journal trực tiếp vào schema Ledger.

## 6. Transaction

- **Trách nhiệm:** generic record/lifecycle cho financial operation và correlation giữa request, Transfer/Payment outcome và Ledger reference. Boundary này là **PROPOSED**, cần tránh duplicate canonical transfer status.
- **Entities sở hữu (PROPOSED):** `Transaction`, lifecycle history, request/correlation metadata; idempotency record placement/retention cần chốt.
- **Schema:** `transaction_schema`.
- **API:** đọc transaction status; internal registration/status transition theo contract.
- **Publish:** event riêng chưa định nghĩa; không phát `TransferCompleted` thay Transfer.
- **Consume:** `TransferRequested`, `TransferApproved`, `TransferCompleted`, `TransferFailed`, `PaymentSubmitted`, `PaymentSettled`, `JournalPosted` theo mapping cần review.
- **Dependencies:** Transfer/Payment/Ledger references qua IDs/events.
- **Không được:** ghi ledger, sở hữu transfer instructions, quyết định external settlement, hoặc tạo trạng thái authoritative trùng với Transfer/Payment.

## 7. Transfer

- **Trách nhiệm:** điều phối internal và interbank transfer; kiểm tra idempotency/concurrency phối hợp với service owners; lưu workflow/route reference.
- **Entities sở hữu (PROPOSED):** `Transfer`, transfer instruction, destination reference, orchestration history.
- **Schema:** `transfer_schema`; mọi `accountId`, `transactionId`, `approvalId`, `branchId` là references.
- **API:** tạo transfer, xem trạng thái, yêu cầu cancel nếu rule cho phép; internal command tới Ledger/Payment.
- **Publish:** `TransferRequested`, `TransferCompleted`, `TransferFailed`; `TransferApproved` do Corporate publish.
- **Consume:** `TransferApproved`, `AccountOpened`/`AccountBlocked`, `PaymentSubmitted`/`PaymentSettled`, `JournalPosted`/`JournalReversed` theo workflow.
- **Dependencies:** Account eligibility, Corporate approval, Transaction record, Ledger posting, Payment external boundary.
- **Không được:** tự ghi Ledger, coi timeout là failure, truy cập schema của service khác, hoặc tự giả định provider/settlement rule.

## 8. Ledger

- **Trách nhiệm:** authoritative financial source of truth; validate/post/reverse balanced double-entry journals atomically.
- **Entities sở hữu (PROPOSED):** `Journal`, `JournalEntry`, posting/reversal metadata; account IDs là external references.
- **Schema:** `ledger_schema`.
- **API:** internal journal posting/reversal/query API; posting command phải idempotent.
- **Publish:** `JournalPosted`, `JournalReversed`.
- **Consume:** posting commands từ Transfer/Payment/authorized financial flows; `AccountOpened` có thể phục vụ validation projection nhưng không thay account owner.
- **Dependencies:** Account identifiers/eligibility qua integration; Transaction/Transfer correlation IDs.
- **Không được:** sửa posted entries, tự mở/block account, sử dụng Redis làm source of truth, hoặc chấp nhận journal mất cân bằng.

## 9. Payment

- **Trách nhiệm:** tích hợp external payment network/provider cho interbank instructions và xác minh external outcome.
- **Entities sở hữu (PROPOSED):** `Payment`, provider/external reference, submission/response history.
- **Schema:** `payment_schema`.
- **API:** internal submit/query/reconcile adapter boundary; provider-specific API chưa biết.
- **Publish:** `PaymentSubmitted`, `PaymentSettled`; failure/unknown events cần contract cụ thể.
- **Consume:** Transfer instruction, callback/status result từ provider boundary, reconciliation input nếu được thiết kế.
- **Dependencies:** Transfer correlation, external provider/network (OPEN), Ledger theo posting policy sẽ được duyệt.
- **Không được:** giả định protocol/provider, ghi schema Transfer/Ledger, hoặc biến timeout thành definitive failure.

## 10. Notification

- **Trách nhiệm:** chuyển yêu cầu thông báo thành delivery attempts qua channel/provider được chọn.
- **Entities sở hữu (PROPOSED):** `Notification`, template reference, delivery attempt/status.
- **Schema:** `notification_schema`.
- **API:** internal request/status và administrative template operations nếu scope được duyệt.
- **Publish:** delivery outcome events chưa được yêu cầu đặt tên.
- **Consume:** `NotificationRequested` và domain events được cấu hình thành notification trigger.
- **Dependencies:** channel provider và recipient references; provider/PII policy chưa chốt.
- **Không được:** quyết định financial outcome, thay đổi transaction state, hay lưu credentials của channel trong domain event.

## 11. Audit

- **Trách nhiệm:** append-only audit trail cho thao tác nhạy cảm, actor, resource, action, result và thời điểm.
- **Entities sở hữu (PROPOSED):** `AuditRecord`, audit metadata/correlation.
- **Schema:** `audit_schema`.
- **API:** ghi audit event và truy vấn theo quyền; các write bắt buộc audit phải có kết quả rõ ràng.
- **Publish:** `AuditEventRecorded` sau khi audit record được lưu.
- **Consume:** domain/security events; event-only audit không thay synchronous durable audit requirement nếu thao tác yêu cầu.
- **Dependencies:** producer contexts, Identity actor reference, retention/access policy.
- **Không được:** sửa dữ liệu domain thay service owner, ghi secret/token không cần thiết, hoặc coi audit event là authorization.

## 12. Reporting

- **Trách nhiệm:** cung cấp báo cáo/read models từ dữ liệu được nhận theo quyền truy cập.
- **Entities sở hữu (PROPOSED):** projections, report definitions/run metadata; không sở hữu source transactions.
- **Schema:** `reporting_schema`.
- **API:** truy vấn báo cáo, filter/export nếu được scope cho phép.
- **Publish:** report generation/status events chưa định nghĩa.
- **Consume:** business events, nhất là Party/Account/Transfer/Journal/Payment/Audit events theo data minimization.
- **Dependencies:** producers, Identity/Corporate/Branch authorization scope.
- **Không được:** cập nhật source domain, thay Ledger làm financial source of truth, hoặc cấp dữ liệu ngoài quyền caller.

## Context relationship rules

- Cross-service references là IDs; mỗi service đọc resource qua API hoặc giữ projection riêng được xây từ event.
- Không direct table access, shared ORM entities hay cross-schema foreign keys.
- Event consumer phải idempotent; event contracts là đề xuất trong [EVENT-CONTRACTS.md](../architecture/EVENT-CONTRACTS.md).
- Idempotency dùng actor + operation + business scope, mặc định retention 24 giờ khi API yêu cầu; xem [ADR-012](../adr/ADR-012-phase-2-identity-party-corporate-branch.md). Mỗi operation vẫn cần định nghĩa scope cụ thể.
- Transaction vs Transfer ownership of lifecycle needs architecture review trước khi triển khai; không tạo hai nguồn trạng thái chuẩn.
- Payment protocol, branch reassignment workflow, approval-policy snapshot và event transport xem [OPEN-DECISIONS.md](../architecture/OPEN-DECISIONS.md).
