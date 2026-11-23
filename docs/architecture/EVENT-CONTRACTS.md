# Event Contracts

Các contract dưới đây là thiết kế **PROPOSED**, chưa khẳng định broker đã chọn hoặc event đã được triển khai. Kafka vs RabbitMQ còn OPEN; xem [Open Decisions](./OPEN-DECISIONS.md). Schema evolution cần backward-compatible theo policy được xác nhận sau này.

## Envelope chung

Mọi event đề xuất có envelope:

```json
{
  "eventId": "unique-event-id",
  "eventType": "TransferRequested",
  "aggregateType": "Transfer",
  "aggregateId": "transfer-id",
  "correlationId": "request-or-workflow-id",
  "causationId": "optional-causative-event-or-command-id",
  "occurredAt": "RFC-3339 UTC timestamp",
  "version": 1,
  "producer": "transfer",
  "payload": {}
}
```

| Trường | Ý nghĩa / yêu cầu |
|---|---|
| `eventId` | ID duy nhất cho event instance; consumer dùng để deduplicate. |
| `eventType` | Tên event ổn định, khớp tên trong bảng contract. |
| `aggregateType`, `aggregateId` | Entity và ID do producer sở hữu; không phải DB foreign key. |
| `correlationId` | Theo dõi một request/workflow xuyên service. |
| `causationId` | Tham chiếu command/event trước đó nếu có. |
| `occurredAt` | Thời điểm producer ghi nhận event, RFC-3339 UTC. |
| `version` | Phiên bản payload nguyên dương; chưa quyết định schema registry. |
| `producer` | Tên context phát event. |
| `payload` | Nội dung nghiệp vụ tối thiểu; không đưa secret/token vào event. |

Tên payload field, PII classification, retention và partitioning là **PROPOSED** và cần review trước triển khai.

## Event catalog

Payload fields dưới đây là tối thiểu đề xuất. `amount` dùng decimal string, currency cho MVP là `VND`; không dùng floating-point.

| Event | Producer → Consumers dự kiến | Payload đề xuất |
|---|---|---|
| `PartyCreated` | Party → Corporate, Account, Reporting | `partyId`, `partyType`, `createdAt` |
| `AccountOpened` | Account → Transfer, Ledger, Notification, Reporting | `accountId`, `partyId`, `accountType`, `currency`, `openedAt` |
| `AccountBlocked` | Account → Transfer, Ledger, Notification, Reporting | `accountId`, `reasonCode`, `blockedAt` |
| `TransferRequested` | Transfer → Transaction, Corporate, Account, Payment | `transferId`, `transactionId`, `sourceAccountId`, `destination`, `amount`, `currency`, `channel`, `requestedBy` |
| `TransferApproved` | Corporate → Transfer, Transaction, Audit | `transferId`, `approvalId`, `organizationId`, `approverId`, `decision`, `decidedAt` |
| `TransferCompleted` | Transfer → Transaction, Ledger, Notification, Reporting | `transferId`, `transactionId`, `amount`, `currency`, `completedAt`, `ledgerReference` |
| `TransferFailed` | Transfer → Transaction, Notification, Reporting | `transferId`, `transactionId`, `failureCode`, `failedAt`, `retryable` |
| `JournalPosted` | Ledger → Transfer, Transaction, Reporting, Audit | `journalId`, `transactionId`, `entries[]`, `postedAt` |
| `JournalReversed` | Ledger → Transfer, Transaction, Reporting, Audit | `journalId`, `reversalJournalId`, `reasonCode`, `reversedAt` |
| `PaymentSubmitted` | Payment → Transfer, Transaction, Audit | `paymentId`, `transferId`, `providerReference` (nếu được cấp), `submittedAt` |
| `PaymentSettled` | Payment → Transfer, Transaction, Ledger, Reporting | `paymentId`, `transferId`, `amount`, `currency`, `settledAt`, `externalReference` |
| `NotificationRequested` | Domain producer → Notification | `notificationId`, `recipientRef`, `templateCode`, `channel`, `parameters`, `requestedAt` |
| `AuditEventRecorded` | Audit → Reporting / audit sinks | `auditRecordId`, `actorId`, `action`, `resourceType`, `resourceId`, `result`, `recordedAt` |

Producer/consumer mapping và payload đều là **PROPOSED**. Không phát payload account balance làm nguồn chuẩn; Ledger remains authoritative. Nên dùng ID/reference thay vì duplicate PII.

## Delivery, retry and deduplication

- **Transactional Outbox (mandatory design rule):** producer ghi thay đổi nghiệp vụ và outbox record trong cùng local DB transaction. Outbox publisher gửi lại cho đến khi broker xác nhận theo semantics sẽ chọn.
- Không có distributed transaction giữa database service và broker được giả định.
- **At-least-once delivery:** thiết kế consumer phải chịu duplicate; đúng semantics cuối cùng phụ thuộc broker và cần xác nhận.
- **Consumer idempotency:** lưu processed `eventId` hoặc business idempotency marker trong cùng transaction với side effect. Unique constraint đề xuất để ngăn áp dụng lại.
- **Ordering:** chỉ giả định thứ tự trong một aggregate nếu broker/config cho phép; consumer nên kiểm tra aggregate/version và xử lý stale/out-of-order event an toàn.
- **Retry:** retry lỗi transient có backoff và giới hạn cấu hình; lỗi validation/permanent không retry vô hạn. Số lần, khoảng thời gian và policy cụ thể chưa chốt.
- **Dead-letter:** chuyển message lỗi không thể xử lý sang dead-letter/quarantine sau policy retry; lưu nguyên nhân, correlation ID, event ID; hỗ trợ replay có kiểm soát và audit. Cơ chế broker-specific còn OPEN.
- Event chứa outcome chưa xác định không được quy thành `TransferFailed` chỉ vì timeout.
  - Interbank timeout and unavailable provider response remain `UNRESOLVED`, not a failed event.
  - `PaymentSettled` and `TransferFailed` require a verified outcome source; stale/duplicate outcomes are not reapplied.
- PII, retention, encryption, schema registry, partition keys và replay authorization cần thiết kế trước triển khai.

## Event-specific notes

- `TransferRequested` không đồng nghĩa transfer đã accepted, approved, settled hay posted.
- `TransferApproved` là quyết định approval; `TransferCompleted` chỉ được phát khi Transfer xác định completion theo business/network contract. Completion criteria cho interbank còn OPEN.
- `PaymentSettled` báo external outcome do Payment xác minh; không tự thay thế posting của Ledger.
- Chỉ Ledger phát `JournalPosted` / `JournalReversed`; event không cấp quyền ghi trực tiếp vào schema Ledger.
- Account does not consume Ledger events to become a balance authority; balance reads belong to Ledger or an explicitly non-authoritative projection.
- `AuditEventRecorded` là xác nhận record đã được Audit lưu; không dùng event này để thay thế audit write đồng bộ cho thao tác bắt buộc audit.

Xem [API Contracts](../api/API-CONTRACTS.md), [Bounded Contexts](../domain/BOUNDED_CONTEXTS.md), [Business Rules](../domain/BUSINESS-RULES.md) và [Open Decisions](./OPEN-DECISIONS.md).
