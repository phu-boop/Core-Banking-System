# Open Decisions

Các mục có trạng thái **OPEN** dưới đây chưa được chủ dự án quyết định. Những dòng **RESOLVED** được giữ lại để duy trì số tham chiếu và trỏ tới quyết định đã chấp nhận. Recommendation, nếu có, là **PROPOSED**, không phải quyết định.

| # | Vấn đề | Status |
|---|---|---|
| 1 | External interbank network / provider / protocol | OPEN |
| 2 | Managing branch cardinality của customer | RESOLVED — ADR-012 |
| 3 | Approval policy snapshot và policy change trong khi giao dịch chờ duyệt | RESOLVED — ADR-007 (2026-11-20) |
| 4 | API Gateway technology | OPEN |
| 5 | Identity Provider | RESOLVED — ADR-012 |
| 6 | Kafka hay RabbitMQ | OPEN |
| 7 | REST hay gRPC cho synchronous internal API | RESOLVED — ADR-012 (first slice) |
| 8 | Production Kubernetes provider, cluster topology and rollout details | OPEN |
| 9 | Transaction limits và business-day rules | OPEN |
| 10 | Interbank settlement, timeout, reversal và reconciliation rules | OPEN |
| 11 | Production external secret store và workload identity details | OPEN |

## Chi tiết quyết định

### 1. External interbank network / provider / protocol

- **Mô tả:** Chưa xác định network, provider, giao thức, xác thực hay mô hình request/callback.
- **Ảnh hưởng:** Payment integration, dữ liệu lệnh, timeout/retry, settlement, reconciliation và vận hành.
- **Lựa chọn:** Tùy provider/network được chọn; hiện chưa có lựa chọn cụ thể để so sánh.
- **Recommendation (PROPOSED):** Giữ adapter boundary trong Payment và tránh đưa protocol của provider vào domain Transfer/Ledger.
- **Status:** OPEN.

### 2. Customer managing branch

- **Status:** RESOLVED by [ADR-012](../adr/ADR-012-phase-2-identity-party-corporate-branch.md).
- Customer có một primary managing branch tại một thời điểm, khác transaction-processing branch. Reassignment là thao tác explicit và phải audit.
- Thời điểm phải gán branch, reassignment workflow/effective date và cross-branch operations vẫn OPEN; xem mục 9.

### 3. Approval policy snapshot
- **Status:** RESOLVED by [ADR-007](../adr/ADR-007-configurable-corporate-approval.md), approved 2026-11-20.
- Each request is bound to the immutable policy version selected at submission; a later version does not change pending requests.
- Approval levels run sequentially and all required levels must approve. Rejection is terminal; resubmission creates a new request and preserves prior history.
- Expiry and delegation are excluded from the MVP.
- Policy management uses organization-scoped `approval-policy:manage`; activation is explicit, and only one policy version per organization/operation type may be active.
- Each level declares role/mandate scope; Corporate checks active Membership and Mandate. The owner service supplies an opaque operation type/reference; Corporate does not execute the operation.
- Detailed thresholds, role/mandate catalogs, Membership/Mandate API contracts, and exact Keycloak claim mapping remain OPEN and are tracked in the Approval feature spec.

### 4. API Gateway technology

- **Mô tả:** Gateway được yêu cầu ở mức kiến trúc kênh, nhưng sản phẩm/công nghệ chưa chốt.
- **Ảnh hưởng:** Routing, TLS, throttling, token handling, operations và deployment.
- **Lựa chọn:** Chọn sau khi xác định runtime/platform và tiêu chí vận hành.
- **Recommendation (PROPOSED):** So sánh theo yêu cầu bảo mật, vận hành và tích hợp; không đặt business authorization chỉ ở gateway.
- **Status:** OPEN.

### 5. Identity Provider

- **Status:** RESOLVED by [ADR-012](../adr/ADR-012-phase-2-identity-party-corporate-branch.md).
- Keycloak là Identity Provider ban đầu, dùng OIDC; Authorization Code + PKCE cho interactive users và Client Credentials cho service identities.
- Keycloak hosting, deployment, environment configuration và production security/compliance review chưa được chốt ở đây.

### 6. Event broker

- **Mô tả:** Event contracts được thiết kế độc lập với broker; Kafka vs RabbitMQ chưa chốt.
- **Ảnh hưởng:** Delivery semantics, ordering, replay, operations và consumer topology.
- **Lựa chọn:** Kafka hoặc RabbitMQ; so sánh theo event retention, replay, routing và vận hành.
- **Recommendation (PROPOSED):** Chọn dựa trên yêu cầu đã xác nhận; không xem danh sách event là quyết định Kafka.
- **Status:** OPEN.

### 7. Internal communication

- **Synchronous APIs:** REST is ACCEPTED for the first implementation slice by [ADR-012](../adr/ADR-012-phase-2-identity-party-corporate-branch.md); gRPC is excluded from that slice.
- Event transport is still OPEN under item 6; this decision does not choose Kafka/RabbitMQ or define delivery semantics.

### 8. Production Kubernetes provider, cluster topology and rollout details

- **Mô tả:** Kubernetes đã được chấp nhận cho local và production theo ADR-010. Local dùng Minikube trên Ubuntu 24.04 theo ADR-011; production provider/topology và rollout details chưa được chọn.
- **Ảnh hưởng:** Deployment topology, scaling, health checks, service discovery, rollback và operational effort.
- **Lựa chọn:** AWS EKS hoặc Kubernetes tương đương; production topology và rollout strategy cần được đánh giá.
- **Recommendation (PROPOSED):** Chốt môi trường và availability requirements trước khi chọn distribution/provider, topology và rollout strategy.
- **Status:** OPEN.

### 9. Transaction limits and business-day rules

- **Mô tả:** Chưa có hạn mức theo transaction/account/channel/customer, cutoff time hay business-day/calendar rule.
- **Ảnh hưởng:** Validation, approval routing, settlement date, fees và customer communication.
- **Lựa chọn:** Cần business owner xác định scope, values, timezone và ngoại lệ; không có giá trị mặc định trong tài liệu này.
- **Recommendation (PROPOSED):** Định nghĩa thành bảng policy có version/effective date sau khi requirements được xác nhận.
- **Status:** OPEN.

### 10. Interbank settlement, timeout, reversal and reconciliation

- **Mô tả:** Chưa xác định network semantics, settlement finality, timeout resolution, reversal window hay reconciliation process.
- **Ảnh hưởng:** Transfer state machine, ledger posting timing, customer-visible status, exception handling và accounting.
- **Lựa chọn:** Phụ thuộc provider/network; có thể cần trạng thái outcome chưa xác định cho đến khi query/callback/reconciliation hoàn tất.
- **Recommendation (PROPOSED):** Không coi timeout là failure; giữ trạng thái unresolved và chỉ quyết định posting/reversal theo contract được chủ dự án duyệt.
- **Status:** OPEN.

### 11. Production external secret store and workload identity

- **Mô tả:** Hướng production đã thống nhất là external secret store qua External Secrets Operator. Chưa chọn AWS Secrets Manager hay Vault; workload identity và cách cấp quyền trên provider cụ thể cũng chưa được thiết kế.
- **Ảnh hưởng:** Workload identity, secret synchronization, encryption at rest, IAM/RBAC, vận hành và chi phí.
- **Lựa chọn:** AWS Secrets Manager hoặc Vault; cách tích hợp workload identity/RBAC phụ thuộc Kubernetes provider đã chọn trong quyết định 8.
- **Recommendation (PROPOSED):** Chọn theo môi trường triển khai thực tế; trước production cần xác định encryption at rest, workload identity và policy truy cập tối thiểu.
- **Status:** OPEN cho external store và chi tiết workload identity. Hướng tích hợp và yêu cầu IAM/RBAC đã được ghi nhận tại [ADR-011](../adr/ADR-011-phase-based-secret-management.md).

## Phase 2 implementation details still OPEN

The decisions in [ADR-012](../adr/ADR-012-phase-2-identity-party-corporate-branch.md) establish approved boundaries, not complete service specifications. The following details remain open and are tracked in the [Phase 2 Decision Gate](../plan/phase/Phase-2-Decision-Gate.md):

- Keycloak user population/provisioning flows, MFA/federation, hosting and environment-specific configuration.
- Full role catalog, detailed permission vocabulary and grant inheritance.
- Approved PII profile fields, duplicate-match attributes/weights/thresholds, and jurisdiction-specific legal identifiers/uniqueness.
- Customer and organization onboarding workflows; managing-branch assignment and reassignment/effective-date rules.
- Idempotency key format, storage and operation-specific business scopes.
- Audit storage, tamper-evidence, read access matrix, retention enforcement and archival.
- Exact API routes, DTOs, error codes, versioning and per-operation audit requirements.
- Event broker/transport, `PartyCreated` payload and Outbox publication.
- Approval thresholds, role/mandate catalog, Membership/Mandate API contracts and exact Keycloak claim mapping; Transfer-level approval enforcement/execution and all Account/Ledger/Transfer behavior.

## Khác biệt cần giữ rõ

- ACCEPTED: VND only, Internal + Interbank, configurable corporate approval, Loan excluded from MVP, multi-branch.
- ACCEPTED cho local development: Ubuntu 24.04, Docker, Minikube, Kubernetes Secret và Spring Boot đọc secrets qua environment variables.
- Các mục còn trạng thái OPEN/PROPOSED vẫn cần quyết định trước phần việc phụ thuộc; các hàng 2, 5 và 7 đã được giải quyết trong ADR-012.
- Xem [architecture overview](./BANKING_ARCHITECTURE.md) và [ADRs](../adr/README.md).
