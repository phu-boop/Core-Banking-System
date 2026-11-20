# Banking Clone — Kiến trúc hệ thống

Tài liệu kiến trúc tổng quan. Bounded Contexts, nghiệp vụ, API, event và dữ liệu được đặc tả riêng trong [Bounded Contexts](../domain/BOUNDED_CONTEXTS.md), [Business Rules](../domain/BUSINESS-RULES.md), [API Contracts](../api/API-CONTRACTS.md), [Event Contracts](./EVENT-CONTRACTS.md) và [ERD](../database/ERD.md).

## Trạng thái và mục tiêu

- Dự án đang ở **Phase 2 — Identity, Party, Corporate and Branch**. Phase 1 đã hoàn tất nền tảng kỹ thuật; Identity, Party, Branch và Corporate Organization có first-slice implementation. Corporate Membership/Mandate/Approval vẫn chưa triển khai.
- Phạm vi: Core Banking cho Retail và Corporate Banking, hỗ trợ Internet Banking, Admin và Teller.
- Trạng thái của nội dung: **ACCEPTED** là quyết định chủ dự án xác nhận; **PROPOSED** là thiết kế cần duyệt; **OPEN** là chưa quyết định; **DEFERRED** là để giai đoạn sau.

## Quyết định đã xác nhận

| ADR | Quyết định | Chi tiết |
|---|---|---|
| [ADR-001](../adr/ADR-001-microservices-architecture.md) | ACCEPTED | Microservices Architecture |
| [ADR-002](../adr/ADR-002-retail-corporate-banking.md) | ACCEPTED | Retail + Corporate Banking |
| [ADR-003](../adr/ADR-003-multi-channel.md) | ACCEPTED | Internet Banking, Admin, Teller |
| [ADR-004](../adr/ADR-004-shared-postgresql-schema-per-service.md) | ACCEPTED | PostgreSQL dùng chung, schema riêng theo service |
| [ADR-005](../adr/ADR-005-vnd-only.md) | ACCEPTED | MVP chỉ dùng VND |
| [ADR-006](../adr/ADR-006-internal-and-interbank-transfer.md) | ACCEPTED | Internal + Interbank Transfer |
| [ADR-007](../adr/ADR-007-configurable-corporate-approval.md) | ACCEPTED | Corporate approval có cấu hình |
| [ADR-008](../adr/ADR-008-loan-excluded-from-mvp.md) | ACCEPTED / DEFERRED | Loan ngoài MVP, dự kiến giai đoạn sau |
| [ADR-009](../adr/ADR-009-multi-branch-banking.md) | ACCEPTED | Hỗ trợ nhiều chi nhánh |
| [ADR-010](../adr/ADR-010-phase-1-technical-baseline.md) | ACCEPTED | Java 21, Spring Boot 3, Maven Wrapper, monorepo, Kubernetes local + production, Flyway |
| [ADR-011](../adr/ADR-011-phase-based-secret-management.md) | ACCEPTED; production provider OPEN | Local: Ubuntu 24.04, Docker, Minikube, Kubernetes Secret, Spring Boot đọc environment variables. Production: EKS hoặc tương đương, AWS Secrets Manager hoặc Vault qua External Secrets Operator, IAM/RBAC |

Ngày phê duyệt lịch sử không được cung cấp; xem từng ADR. Công nghệ và chi tiết thiết kế bên dưới không phải quyết định đã chốt trừ khi ghi rõ ACCEPTED.

## Nguyên tắc kiến trúc

- Mỗi service sở hữu dữ liệu và schema của mình. Dùng chung PostgreSQL **không** cho phép truy cập trực tiếp bảng của service khác.
- Không tạo cross-schema foreign key. Service trao đổi định danh và dữ liệu qua API hoặc event.
- Ledger là nguồn dữ liệu chuẩn cho biến động tài chính; chỉ Ledger được ghi journal và posting.
- Debit phải bằng Credit; journal đã posted bất biến, sửa sai bằng reversal.
- Dùng `BigDecimal` trong Java và `NUMERIC` trong PostgreSQL; Redis không phải nguồn chuẩn cho số dư.
- Dùng idempotency, concurrency control và Transactional Outbox cho các luồng tài chính.
- Authorization phải được kiểm tra tại service sở hữu nghiệp vụ và resource, không chỉ ở gateway.
- Phân biệt trạng thái đã xác nhận với đề xuất; không giả định provider, giao thức hay quy tắc settlement chưa được chốt.

## Bounded Contexts / Context Map

Chi tiết ownership, entity, API, event, dependency và giới hạn từng context trong [BOUNDED_CONTEXTS.md](../domain/BOUNDED_CONTEXTS.md).

```mermaid
flowchart LR
  IB[Internet Banking] --> EDGE[Edge / API Gateway<br/>PROPOSED; technology OPEN]
  ADM[Admin] --> EDGE
  TEL[Teller] --> EDGE
  EDGE --> IAM[Identity & Access]
  EDGE --> PARTY[Party / Customer]
  EDGE --> CORP[Corporate]
  EDGE --> BR[Branch]
  EDGE --> ACC[Account]
  EDGE --> TX[Transaction]
  EDGE --> TR[Transfer]
  EDGE --> PAY[Payment]
  EDGE --> AUD[Audit]
  EDGE --> REP[Reporting]
  TR -->|internal API / events| ACC
  TR -->|posting request| LED[Ledger]
  TR -->|external payment boundary| PAY
  PAY -->|provider protocol OPEN| EXT[External interbank network]
  CORP -->|approval policy / decision| TR
  BR -. branch scope .-> ACC
  BR -. branch permissions .-> IAM
  LED -. journal events .-> REP
  PARTY -. party references .-> ACC
  PARTY -. party references .-> CORP
  IAM -. actor identity .-> AUD
  classDef proposed fill:#fff4d6,stroke:#9a6700,color:#222
  class EDGE,TR,PAY proposed
```

Các đường nối mô tả quan hệ nghiệp vụ hoặc giao tiếp dự kiến, không phải foreign key. Cơ chế đồng bộ cụ thể giữa một số context là **PROPOSED** và cần review.

## Component Diagram

```mermaid
flowchart TB
  subgraph Channels["Kênh — ACCEPTED"]
    IB[Internet Banking]
    ADMIN[Admin]
    TELLER[Teller]
  end
  EDGE[Edge / API Gateway<br/>PROPOSED; technology OPEN]
  subgraph Services["Microservices — boundaries dự kiến"]
    IAM[Identity & Access]
    PARTY[Party / Customer]
    CORP[Corporate]
    BRANCH[Branch]
    ACCOUNT[Account]
    TRANSACTION[Transaction]
    TRANSFER[Transfer]
    LEDGER[Ledger]
    PAYMENT[Payment]
    NOTIFY[Notification]
    AUDIT[Audit]
    REPORT[Reporting]
  end
  subgraph Platform["Platform — lựa chọn còn mở"]
    PG[(Shared PostgreSQL<br/>schema-per-service)]
    BUS[(Event broker<br/>Kafka vs RabbitMQ OPEN)]
    CACHE[(Redis<br/>non-authoritative)]
    OBS[Metrics / Logs / Traces]
  end
  EXT[External interbank network/provider<br/>unspecified]
  Channels --> EDGE --> Services
  Services --> PG
  Services -. events .-> BUS
  Services -. optional cache .-> CACHE
  Services -. telemetry .-> OBS
  TRANSFER --> PAYMENT
  PAYMENT --> EXT
```

Gateway, broker, cache usage, deployment topology và các kết nối giữa service là đề xuất; xem [OPEN-DECISIONS.md](./OPEN-DECISIONS.md). PostgreSQL dùng chung và schema-per-service là ACCEPTED.

## Deployment Diagram

```mermaid
flowchart TB
  subgraph Runtime["Kubernetes runtime — topology PROPOSED"]
    CLIENTS[Internet Banking / Admin / Teller]
    GATEWAY[API Gateway<br/>technology OPEN]
    subgraph Workloads["Kubernetes workloads"]
      SVC[12 independently deployable service workloads]
    end
    DB[(PostgreSQL cluster<br/>separate schema per service)]
    BROKER[(Event broker<br/>technology OPEN)]
    REDIS[(Redis<br/>optional; not balance source)]
    TELEMETRY[Metrics / Logs / Traces backend]
    CLIENTS --> GATEWAY --> SVC
    SVC --> DB
    SVC -. events .-> BROKER
    SVC -. optional .-> REDIS
    SVC -. telemetry .-> TELEMETRY
  end
  EXT[External payment network/provider<br/>TBD]
  SVC -. Payment integration .-> EXT
```

Đây là sơ đồ logic. Kubernetes đã được chấp nhận cho local và production; distribution, cluster provider, network zones, replica strategy, availability topology và rollout chi tiết còn OPEN.

## Giao tiếp nội bộ và bên ngoài

- **Channel → service:** REST API là lựa chọn thiết kế hiện tại (**PROPOSED**); gateway technology **OPEN**. Mỗi service vẫn tự xác thực quyền với resource của mình.
- **Service → service:** REST cho synchronous commands/queries trong first implementation slice theo [ADR-012](../adr/ADR-012-phase-2-identity-party-corporate-branch.md). gRPC chưa thuộc slice này; broker và event transport vẫn **OPEN**.
- **Event contracts:** envelope và payload dự kiến trong [EVENT-CONTRACTS.md](./EVENT-CONTRACTS.md), chưa khẳng định broker đã chọn.
- **External interbank:** Payment là ranh giới tích hợp. Provider, protocol, settlement, retry và reconciliation chưa được xác định; không giả định cụ thể.

## Technology Baseline

| Công nghệ / lựa chọn | Trạng thái |
|---|---|
| Java 21, Spring Boot 3 | ACCEPTED; minor/patch versions not fixed |
| Maven Wrapper | ACCEPTED |
| PostgreSQL dùng chung, schema-per-service | ACCEPTED về topology dữ liệu; phiên bản chưa xác định |
| PostgreSQL 17.12 | ACCEPTED cho local development và kiểm thử nền tảng; production version OPEN |
| Flyway | ACCEPTED; migration structure remains service-owned |
| Kafka | PROPOSED; lựa chọn Kafka vs RabbitMQ OPEN |
| Redis | PROPOSED; không phải nguồn chuẩn số dư |
| Kubernetes | ACCEPTED for local development and production orchestration |
| Local Kubernetes distribution | ACCEPTED: Minikube on Ubuntu 24.04 |
| Production Kubernetes provider / cluster topology / rollout details | OPEN |
| Secret delivery | ACCEPTED direction per ADR-011; exact production provider OPEN |
| Keycloak + OIDC for initial identity integration | ACCEPTED by ADR-012; hosting/configuration remain implementation details |
| JUnit 5, Mockito, Testcontainers | PROPOSED |
| Prometheus, Grafana, OpenTelemetry | PROPOSED |
| API Gateway technology; event broker/transport | OPEN |

## Security Architecture

- Keycloak/OIDC is the accepted initial Identity Provider; Authorization Code + PKCE is for interactive users and Client Credentials for machine identities. Keycloak owns login and token issuance; see [ADR-012](../adr/ADR-012-phase-2-identity-party-corporate-branch.md).
- **ACCEPTED:** RBAC plus scoped grants and resource-level authorization; initial scopes are `SELF`, `ORGANIZATION` and `BRANCH`. Complete roles, grant inheritance and detailed permission vocabulary remain OPEN.
- Maker–Checker phải được thực thi trong Corporate/approval workflow và được kiểm tra tại service xử lý lệnh, không tin cậy chỉ vào gateway.
- Mỗi service kiểm tra authorization cho resource mà service sở hữu.
- Audit các thao tác nhạy cảm, quyết định duyệt và kết quả financial operation; tránh ghi secret hoặc dữ liệu nhạy cảm không cần thiết.
- **Local (ACCEPTED):** lưu secret trong Kubernetes Secret, inject vào service qua environment variables; không commit giá trị secret.
- **Production (ACCEPTED direction):** external secret store qua External Secrets Operator, với quyền IAM/RBAC tối thiểu. AWS EKS hay Kubernetes tương đương và AWS Secrets Manager hay Vault vẫn là lựa chọn **OPEN**.
- Service template dùng password PostgreSQL local qua Kubernetes Secret; chỉ thêm secret reference khi có consumer thực tế.

## Observability

- **PROPOSED:** metrics, structured logs và distributed traces; Prometheus/Grafana/OpenTelemetry là ứng viên.
- Service template hiện có structured JSON request logs và local Micrometer `/actuator/metrics`/`/actuator/prometheus`; đây là foundation, chưa chọn production collector hoặc backend.
- Dùng correlation/causation identifiers xuyên suốt request và event; không ghi credentials, token hoặc dữ liệu thanh toán nhạy cảm vào log.
- Theo dõi trạng thái giao dịch, posting, retry, outbox backlog và external outcome; không dùng telemetry/cache thay dữ liệu nghiệp vụ.
- Retention, alert thresholds, SLOs và backend observability chưa được quyết định.

## Deployment Strategy

- Mỗi bounded context được thiết kế để deploy độc lập (**PROPOSED**, phù hợp ADR-001).
- Kubernetes được ACCEPTED cho local development và production orchestration.
- Local cluster tooling là Minikube trên Ubuntu 24.04 theo ADR-011. Production provider/topology, HA/DR, scaling, release/rollback và migration coordination còn **OPEN**.
- Mọi thay đổi schema tương lai phải thuộc service sở hữu schema; tài liệu này không tạo hoặc phê duyệt migration.

## Liên kết liên quan

- [Open decisions](./OPEN-DECISIONS.md)
- [ADR index](../adr/README.md)
- [ADR-011 — Secret management](../adr/ADR-011-phase-based-secret-management.md)
- [Bounded contexts](../domain/BOUNDED_CONTEXTS.md)
- [Business rules](../domain/BUSINESS-RULES.md)
- [Logical ERD](../database/ERD.md)
- [API contracts](../api/API-CONTRACTS.md)
- [Event contracts](./EVENT-CONTRACTS.md)
