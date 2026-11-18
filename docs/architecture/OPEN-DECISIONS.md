# Open Decisions

Các mục dưới đây chưa được chủ dự án quyết định. Recommendation là **PROPOSED**, không phải quyết định; không triển khai theo recommendation nếu chưa được duyệt.

| # | Vấn đề | Status |
|---|---|---|
| 1 | External interbank network / provider / protocol | OPEN |
| 2 | Một hay nhiều managing branch cho customer | OPEN |
| 3 | Approval policy snapshot và policy change trong khi giao dịch chờ duyệt | OPEN |
| 4 | API Gateway technology | OPEN |
| 5 | Identity Provider | OPEN |
| 6 | Kafka hay RabbitMQ | OPEN |
| 7 | REST hay gRPC cho internal communication | OPEN |
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

- **Mô tả:** Chưa rõ mỗi customer có một managing branch hay có thể gắn nhiều branch.
- **Ảnh hưởng:** Customer profile, servicing permissions, reporting và quy trình Teller.
- **Lựa chọn:** Một branch chính; nhiều branch; hoặc một branch chính kèm các branch được phép phục vụ.
- **Recommendation (PROPOSED):** Chốt cardinality và nghiệp vụ chuyển branch trước khi cố định model; không suy ra từ multi-branch decision.
- **Status:** OPEN.

### 3. Approval policy snapshot

- **Mô tả:** Chưa xác định policy version nào áp dụng nếu policy đổi khi transfer đang chờ duyệt.
- **Ảnh hưởng:** Approval eligibility, auditability, replay và quyết định có cần đánh giá lại giao dịch đang chờ.
- **Lựa chọn:** Snapshot policy/version tại lúc tạo; dùng policy mới tại mỗi lần duyệt; hoặc quy tắc chuyển tiếp được chủ dự án định nghĩa.
- **Recommendation (PROPOSED):** Lưu policy/version và dữ liệu quyết định có thể audit; chính sách áp dụng phải được chủ dự án duyệt.
- **Status:** OPEN.

### 4. API Gateway technology

- **Mô tả:** Gateway được yêu cầu ở mức kiến trúc kênh, nhưng sản phẩm/công nghệ chưa chốt.
- **Ảnh hưởng:** Routing, TLS, throttling, token handling, operations và deployment.
- **Lựa chọn:** Chọn sau khi xác định runtime/platform và tiêu chí vận hành.
- **Recommendation (PROPOSED):** So sánh theo yêu cầu bảo mật, vận hành và tích hợp; không đặt business authorization chỉ ở gateway.
- **Status:** OPEN.

### 5. Identity Provider

- **Mô tả:** OAuth2/OIDC/JWT là technology proposal; chưa chọn Identity Provider.
- **Ảnh hưởng:** Authentication, user lifecycle, token claims, MFA, federation và secret management.
- **Lựa chọn:** Managed hoặc self-hosted provider; chưa có lựa chọn được xác nhận.
- **Recommendation (PROPOSED):** Xác định channel, user populations và security requirements trước khi chọn.
- **Status:** OPEN.

### 6. Event broker

- **Mô tả:** Event contracts được thiết kế độc lập với broker; Kafka vs RabbitMQ chưa chốt.
- **Ảnh hưởng:** Delivery semantics, ordering, replay, operations và consumer topology.
- **Lựa chọn:** Kafka hoặc RabbitMQ; so sánh theo event retention, replay, routing và vận hành.
- **Recommendation (PROPOSED):** Chọn dựa trên yêu cầu đã xác nhận; không xem danh sách event là quyết định Kafka.
- **Status:** OPEN.

### 7. Internal communication

- **Mô tả:** REST vs gRPC chưa chốt; event contracts cũng chưa xác định transport.
- **Ảnh hưởng:** Synchronous API contracts, latency, tooling, compatibility và service coupling.
- **Lựa chọn:** REST, gRPC, hoặc hỗn hợp theo use case.
- **Recommendation (PROPOSED):** Dùng asynchronous event cho decoupled notifications; đánh giá sync protocol theo interoperability và operational support.
- **Status:** OPEN.

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

## Khác biệt cần giữ rõ

- ACCEPTED: VND only, Internal + Interbank, configurable corporate approval, Loan excluded from MVP, multi-branch.
- ACCEPTED cho local development: Ubuntu 24.04, Docker, Minikube, Kubernetes Secret và Spring Boot đọc secrets qua environment variables.
- Các lựa chọn công nghệ hoặc policy ở trên vẫn OPEN/PROPOSED; không làm thay đổi các ADR đã xác nhận.
- Xem [architecture overview](./BANKING_ARCHITECTURE.md) và [ADRs](../adr/README.md).
