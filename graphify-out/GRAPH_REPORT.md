# Graph Report - Core-Banking-System  (2026-11-23)

## Corpus Check
- 462 files · ~100,908 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 38 file(s) not represented in the graph (top: .example 17, (none) 15, .css 2)

## Summary
- 4344 nodes · 8139 edges · 275 communities (231 shown, 44 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 305 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `0eb0e807`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- org.springframework.http.ResponseEntity
- OpenApiProxyControllerTests.java
- org.springframework.context.annotation.Bean
- org.springframework.stereotype.Component
- containers
- containers
- NotificationRecord
- service-template/src/main/resources/application.yml
- Core Banking product flows
- identity-service/src/main/resources/application.yml
- party-service/src/main/resources/application.yml
- app/page.tsx
- OrganizationEndpointIntegrationTests
- org.springframework.web.client.RestClient
- plan-to-tasks.yaml
- branch-service/src/main/resources/application.yml
- files_changed
- LedgerEndpointIntegrationTests.java
- BranchService
- spec-to-plan.yaml
- tasks-to-impl.yaml
- steps
- org.springframework.security.oauth2.jwt.Jwt
- TransferStatus
- Phase 1 — Technical Foundation
- org.springframework.web.bind.annotation.PostMapping
- Feature Specification: Channels, Audit, Notification and Reporting
- architecture/BANKING_ARCHITECTURE.md
- Chi tiết từng phase
- MembershipService
- ReportRequest
- DatabaseConfigurationTests.java
- corporate
- Chi tiết quyết định
- Implementation Plan: Provider-Neutral Interbank Transfer
- Decisions
- API Contracts
- compilerOptions
- Feature Specification: Corporate Approval Core
- Banking Clone — Kiến trúc hệ thống
- Phase 2 — Identity, Party, Corporate and Branch
- ApprovalRepository
- Business Rules
- Feature Specification: Provider-Neutral Interbank Transfer
- PROJECT_ROADMAP.md
- containers
- batch-report.yaml
- files_changed
- Implementation Plan: Corporate Approval Core
- .submit
- corporate-service/src/main/resources/application.yml
- spring
- Service Template
- .submit
- ApprovalPolicyResponse
- Phase 2 — Decision Gate
- payment-service/src/main/resources/application.yml
- audit-service/src/main/resources/application.yml
- security
- ADR-001: Microservices Architecture
- ADR-005: VND Only
- ADR-006: Internal + Interbank Transfer
- ADR-008: Loan Excluded from MVP
- interbank-transfer/checkpoints/tasks-to-impl.yaml
- files_changed
- client
- health
- Identity Service
- package.json
- org.springframework.boot.context.properties.EnableConfigurationProperties
- ADR-002: Retail + Corporate Banking
- ADR-003: Multi-channel Banking
- ADR-004: Shared PostgreSQL, Schema-per-service
- LedgerServiceTests.java
- ADR-009: Multi-branch Banking
- ADR-011: Phase-based Configuration and Secret Management
- T012
- Logical ERD — Banking Clone
- T013
- Branch Service
- management
- PaymentSecurityProperties
- Party Service
- Event Contracts
- traceability
- GEMINI.md
- 00-validate-local-credentials.sh
- com.banking.platform:branch-service
- com.banking.platform:corporate-service
- com.banking.platform:identity-service
- com.banking.platform:party-service
- com.banking.platform:service-template
- T012
- T003
- T004
- containers
- T006
- T007
- T008
- T012
- T014
- PartyService
- Transfer Service
- com.banking.platform:transfer-service
- validation
- metadata
- containers
- TransferExceptions
- api-documentation/checkpoints/batch-report.yaml
- Centralized API Documentation Repository Audit
- uuid
- Migration Summary Report
- org.springframework.jdbc.core.JdbcTemplate
- traceability
- Smoke Test Verdict
- summary
- Decisions
- Centralized API Documentation Implementation Plan
- Requirements Quality Checklist
- org.springframework.boot.autoconfigure.SpringBootApplication
- com.banking.platform:core-banking-system
- channels-operations/checkpoints/tasks-to-impl.yaml
- spring
- AccountResponse
- 6. Implementation tasks
- spring
- page.tsx
- spring
- Payment Service
- 3. Existing endpoint inventory
- org.junit.jupiter.api.Test
- next
- com.banking.platform:payment-service
- reporting-service/src/main/resources/application.yml
- channels-operations/checkpoints/spec-to-plan.yaml
- Phase 4 - Retail Internal Transfer va Teller
- postgres
- org.springframework.boot.context.properties.ConfigurationProperties
- Implementation Plan: Channels, Audit, Notification and Reporting
- services
- CorporateSecurityProperties
- environment
- ChannelAuthorizationService
- Phase 6 - Provider-Neutral Interbank Transfer
- ADR-010: Phase 1 Technical Baseline
- Banking Clone — Architecture Document
- Phase 7 Spec-Level Research
- ADR-007: Configurable Corporate Approval
- Phase 3 - Account va Ledger
- Requirements Quality Checklist
- 8. Testing strategy
- com.banking.platform:account-service
- com.banking.platform:ledger-service
- com.banking.platform:audit-service
- TransferSecurityProperties
- containers
- 12. Recommended Phase 2 decision options
- 4. Required configuration
- files_changed
- Core Banking frontend implementation standards
- OidcPartyReferenceClientTests.java
- command.md
- summary
- Phases.md
- CorporateExceptionHandler.java
- validation
- Centralized API Documentation Implementation Report
- Implementation Plan: Production Readiness
- Core Banking frontend
- Phase 7 - Channels, Audit, Notification and Reporting
- channels-operations/checkpoints/batch-report.yaml
- production-readiness/checkpoints/spec-to-plan.yaml
- com.banking.platform:api-docs-service
- BranchEndpointIntegrationTests
- org.springframework.stereotype.Service
- org.springframework.transaction.annotation.Transactional
- jakarta.servlet.http.HttpServletRequest
- Service postgres
- ChannelContext
- MembershipRepository
- com.banking.platform:notification-service
- Phase 7 Smoke Test Verdict
- test_results
- containers
- containers
- validation
- containers
- AuditRecord
- account_service
- notification_service
- containers
- identity-api.ts
- BranchSecurityProperties
- containers
- Phase 8 Release Checklist
- containers
- reporting_service
- com.banking.platform:reporting-service
- transfer_service
- workspace
- Kubernetes Deployment Catalog
- containers
- containers
- containers
- OrganizationService
- AuditSecurityProperties
- data
- environment
- keycloak
- scripts
- Phase 7
- healthcheck
- PartyReferenceResponse
- identity-response.mjs
- account
- kustomization.yaml
- dependencies
- production-readiness/checkpoints/batch-report.yaml
- account
- audit
- api_docs_module
- docker_images
- focused_log_redaction
- CURRENT_STATUS.md
- test_results
- workspace
- audit
- workspace-smoke.sh
- module_counts
- production-readiness/checkpoints/tasks-to-impl.yaml
- module_counts
- 10-create-service-roles.sh
- DR_DEPENDENCY_SMOKE_TEST_PLAN.md
- branch
- ledger_service
- evidence
- workspace_smoke
- branch
- corporate
- Outbox Backlog, Retry, and Replay Runbook
- corporate
- setup-minikube-local.sh
- identity
- identity
- Page patterns
- identity-api.test.mjs
- party
- party
- payment
- payment
- reporting
- production/README.md
- reporting
- transfer
- transfer
- Phases 3-6
- Core Banking frontend
- Alert Response Runbook
- Service Recovery Runbook
- AGENTS.md
- Backup and Restore Rehearsal
- Core Banking design system
- Disaster Recovery and Dependency-Failure Smoke Test Plan
- Independent Service Rollback Rehearsal
- Phase 8 Security, Recovery, and Rollback Evidence
- navigation
- PartySecurityProperties
- PartyEndpointIntegrationTests
- identity-response.d.mts
- { GET, POST }

## God Nodes (most connected - your core abstractions)
1. `OrganizationEndpointIntegrationTests` - 46 edges
2. `NotificationRecord` - 28 edges
3. `TransferStatus` - 28 edges
4. `TransferRecord` - 27 edges
5. `JournalResponse` - 27 edges
6. `environment` - 27 edges
7. `AccountResponse` - 23 edges
8. `files_changed` - 23 edges
9. `ApprovalRepository` - 22 edges
10. `PaymentRecord` - 21 edges

## Surprising Connections (you probably didn't know these)
- `Step 6: Expose approval APIs and enforce command safety` --references--> `CorporateExceptionHandler`  [INFERRED]
  docs/plan/phase/corporate-approval/plan.md → services/corporate-service/src/main/java/com/banking/platform/corporate/web/CorporateExceptionHandler.java
- `11. Audit` --references--> `AuditRecord`  [INFERRED]
  docs/domain/BOUNDED_CONTEXTS.md → services/audit-service/src/main/java/com/banking/platform/audit/domain/AuditRecord.java
- `Add a microservice` --references--> `ApiDocsProperties`  [INFERRED]
  docs/api-documentation/README.md → services/api-docs-service/src/main/java/com/banking/platform/apidocs/config/ApiDocsProperties.java
- `Authorization` --references--> `ApproverEligibilityClient`  [INFERRED]
  services/corporate-service/README.md → services/corporate-service/src/main/java/com/banking/platform/corporate/application/ApproverEligibilityClient.java
- `AccountServiceApplication` --references--> `IdentityClientProperties`  [EXTRACTED]
  services/account-service/src/main/java/com/banking/platform/account/AccountServiceApplication.java → services/account-service/src/main/java/com/banking/platform/account/config/IdentityClientProperties.java

## Import Cycles
- None detected.

## Communities (275 total, 44 thin omitted)

### Community 0 - "org.springframework.http.ResponseEntity"
Cohesion: 0.07
Nodes (10): IdentityServiceUnavailableException, AccountExceptionHandler, CorporateExceptionHandler, IdentityExceptionHandler, LedgerExceptionHandler, PaymentExceptions, ConflictException, NotFoundException (+2 more)

### Community 1 - "OpenApiProxyControllerTests.java"
Cohesion: 0.10
Nodes (11): Add a microservice, API Documentation, Authenticate in Swagger UI, Kubernetes local, Local development, Troubleshooting, ApiDocsProperties, OpenApiProxyController (+3 more)

### Community 2 - "org.springframework.context.annotation.Bean"
Cohesion: 0.06
Nodes (11): AccountSecurityConfiguration, AuditSecurityConfiguration, BranchSecurityConfiguration, CorporatePartyClientConfiguration, CorporateSecurityConfiguration, IdentitySecurityConfiguration, IdentitySecurityConfigurationTests, PartySecurityConfiguration (+3 more)

### Community 3 - "org.springframework.stereotype.Component"
Cohesion: 0.08
Nodes (8): CorrelationIdFilter, CorrelationIdFilter, CorrelationIdFilter, CorrelationIdFilterTests, CorrelationIdFilter, CorrelationIdFilter, CorrelationIdFilterTests, CorrelationIdFilter

### Community 4 - "containers"
Cohesion: 0.04
Nodes (48): env, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe, securityContext (+40 more)

### Community 5 - "containers"
Cohesion: 0.05
Nodes (48): env, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe, startupProbe (+40 more)

### Community 6 - "NotificationRecord"
Cohesion: 0.09
Nodes (15): NotificationService, NotificationAttempt, NotificationRecord, NotificationRequest, NotificationStatus, DELIVERED, FAILED, REQUESTED (+7 more)

### Community 7 - "service-template/src/main/resources/application.yml"
Cohesion: 0.05
Nodes (42): name, import, password, url, username, password, url, username (+34 more)

### Community 8 - "Core Banking product flows"
Cohesion: 0.25
Nodes (7): 1. Customer transfer, 2. Corporate maker-checker, 3. Teller operation, 4. Account and activity review, 5. Audit and reports, Core Banking product flows, Status vocabulary

### Community 9 - "identity-service/src/main/resources/application.yml"
Cohesion: 0.05
Nodes (40): name, import, password, url, username, password, url, username (+32 more)

### Community 10 - "party-service/src/main/resources/application.yml"
Cohesion: 0.05
Nodes (40): name, import, password, url, username, password, url, username (+32 more)

### Community 11 - "app/page.tsx"
Cohesion: 0.14
Nodes (18): instant, LoginPage(), metadata, signInWithKeycloak(), getIdentityPresentation(), Home(), instant, services (+10 more)

### Community 13 - "org.springframework.web.client.RestClient"
Cohesion: 0.08
Nodes (8): AccountAuthorizationService, IdentityClientProperties, IdentityGrant, IdentityPrincipal, IdentityPrincipalClient, AccountAuthorizationServiceTests, IdentityPrincipalClientTests, OidcPartyReferenceClient

### Community 14 - "plan-to-tasks.yaml"
Cohesion: 0.06
Nodes (35): description, item_id, notes, tasks, metadata, generated, source_plan, target_tasks (+27 more)

### Community 15 - "branch-service/src/main/resources/application.yml"
Cohesion: 0.05
Nodes (36): name, branch, security, import, password, url, username, health (+28 more)

### Community 16 - "files_changed"
Cohesion: 0.15
Nodes (13): files_changed, T001, T002, T003, T007, T008, T014, T015 (+5 more)

### Community 17 - "LedgerEndpointIntegrationTests.java"
Cohesion: 0.13
Nodes (8): AccountEndpointIntegrationTests, AccountStateResponse, AuditServiceApplication, AuditRepositoryIntegrationTests, LedgerEndpointIntegrationTests, NotificationRepositoryIntegrationTests, PaymentRepositoryIntegrationTests, TransferRepositoryIntegrationTests

### Community 18 - "BranchService"
Cohesion: 0.08
Nodes (14): BranchAuthorizationService, BranchService, BranchNotFoundException, DuplicateBranchCodeException, InvalidBranchException, InvalidPageRequestException, BranchPage, BranchResponse (+6 more)

### Community 19 - "spec-to-plan.yaml"
Cohesion: 0.09
Nodes (22): metadata, generated, source_spec, target_plan, missing_requirements, orphan_plan_items, items, phase (+14 more)

### Community 20 - "tasks-to-impl.yaml"
Cohesion: 0.07
Nodes (26): metadata, generated, last_updated, source_tasks, pending_tasks, summary, completed_tasks, completion_percentage (+18 more)

### Community 21 - "steps"
Cohesion: 0.06
Nodes (60): runs-on, steps, runs-on, steps, runs-on, steps, runs-on, steps (+52 more)

### Community 22 - "org.springframework.security.oauth2.jwt.Jwt"
Cohesion: 0.15
Nodes (7): ApprovalWorkflowService, OrganizationAuthorizationService, ApprovalHistoryResponse, ApprovalRequestResponse, ApprovalDecisionController, ApprovalDecisionRequest, CreateApprovalRequest

### Community 23 - "TransferStatus"
Cohesion: 0.06
Nodes (27): ApprovalEnforcedTransferService, ExecutionAdapter, DeferredExecutionAdapter, PaymentSubmissionResponse, SubmitTransferRequest, TransferRecord, TransferStatus, ACCEPTED (+19 more)

### Community 24 - "Phase 1 — Technical Foundation"
Cohesion: 0.10
Nodes (20): 1.1 — Chốt technical baseline, 1.2 — Tạo service template tối thiểu, 1.3 — Thiết lập database ownership, 1.4 — Cấu hình, secrets và security boundary, 1.5 — Reliability foundations, 1.6 — Logging và observability, 1.7 — CI và kiểm thử nền tảng, Bao gồm (+12 more)

### Community 25 - "org.springframework.web.bind.annotation.PostMapping"
Cohesion: 0.20
Nodes (4): OrganizationController, NotificationController, PaymentOutcomeRequest, PaymentController

### Community 26 - "Feature Specification: Channels, Audit, Notification and Reporting"
Cohesion: 0.11
Nodes (18): Assumptions, Edge Cases, Feature Specification: Channels, Audit, Notification and Reporting, Functional Requirements, Key Entities, Measurable Outcomes, NEEDS CLARIFICATION, Requirements (+10 more)

### Community 28 - "Chi tiết từng phase"
Cohesion: 0.12
Nodes (17): Banking Clone — Lộ trình triển khai đề xuất, Chi tiết từng phase, Later — Loan, Ngoài phạm vi MVP, Nguyên tắc chia giai đoạn, Phase 0 — Hoàn tất thiết kế và quyết định MVP, Phase 1 — Nền tảng kỹ thuật, Phase 2 — Identity, Party, Corporate cơ bản và Branch (+9 more)

### Community 29 - "MembershipService"
Cohesion: 0.18
Nodes (6): MembershipService, MembershipNotFoundException, MembershipValidationException, CreateMandateRequest, CreateMembershipRequest, MembershipController

### Community 30 - "ReportRequest"
Cohesion: 0.10
Nodes (11): ReportingAuthorizationService, ReportingService, ExportJob, ExportRequest, ReportRequest, ReportResponse, JdbcReportingRepository, ReportingRepository (+3 more)

### Community 31 - "DatabaseConfigurationTests.java"
Cohesion: 0.16
Nodes (3): DatabaseConnectionProperties, ServiceTemplateApplication, DatabaseConfigurationTests

### Community 32 - "corporate"
Cohesion: 0.05
Nodes (40): name, import, base-url, client-id, client-secret, token-uri, password, url (+32 more)

### Community 33 - "Chi tiết quyết định"
Cohesion: 0.13
Nodes (15): 10. Interbank settlement, timeout, reversal and reconciliation, 11. Production external secret store and workload identity, 1. External interbank network / provider / protocol, 2. Customer managing branch, 3. Approval policy snapshot, 4. API Gateway technology, 5. Identity Provider, 6. Event broker (+7 more)

### Community 34 - "Implementation Plan: Provider-Neutral Interbank Transfer"
Cohesion: 0.12
Nodes (17): Applied Guidelines, Complexity Tracking, Constitution Check, Deferred Decisions, Implementation Plan: Provider-Neutral Interbank Transfer, Implementation Steps, Project Structure, Requirement Mapping (+9 more)

### Community 35 - "Decisions"
Cohesion: 0.14
Nodes (14): 1. Identity Provider, 2. Customer managing branch, 3. Organization-to-Party mapping, 4. Customer Party link, 5. PII duplicate policy, 6. Authorization, 7. Idempotency, 8. Audit (+6 more)

### Community 36 - "API Contracts"
Cohesion: 0.12
Nodes (16): Account, API Contracts, Approval — Corporate core, Audit, Authentication — Identity & Access, Branch, Conventions, Corporate (+8 more)

### Community 37 - "compilerOptions"
Cohesion: 0.11
Nodes (18): compilerOptions, allowJs, esModuleInterop, incremental, isolatedModules, jsx, lib, module (+10 more)

### Community 38 - "Feature Specification: Corporate Approval Core"
Cohesion: 0.14
Nodes (13): Assumptions, Edge Cases, Feature Specification: Corporate Approval Core, Functional Requirements, Key Entities, Measurable Outcomes, Requirements, Scope Baseline (+5 more)

### Community 39 - "Banking Clone — Kiến trúc hệ thống"
Cohesion: 0.15
Nodes (13): Banking Clone — Kiến trúc hệ thống, Bounded Contexts / Context Map, Component Diagram, Deployment Diagram, Deployment Strategy, Giao tiếp nội bộ và bên ngoài, Liên kết liên quan, Nguyên tắc kiến trúc (+5 more)

### Community 40 - "Phase 2 — Identity, Party, Corporate and Branch"
Cohesion: 0.09
Nodes (22): 2.1 — Chốt contract và domain gate, 2.2 — Identity & Access, 2.3 — Party / Customer, 2.4 — Corporate, 2.5 — Branch, 2.6 — Authorization và tích hợp cross-service, 2.7 — Audit foundation, 2.8 — CI và kiểm thử Phase 2 (+14 more)

### Community 41 - "ApprovalRepository"
Cohesion: 0.07
Nodes (24): Authorization, Build and tests, Corporate Service, Database, Party owner API, Scope, ApprovalRequestConflictException, ApprovalRequestNotFoundException (+16 more)

### Community 42 - "Business Rules"
Cohesion: 0.17
Nodes (12): Account, Branch, Business Rules, Corporate Approval, Currency, Idempotency and audit, Identity, authorization and customer data, Interbank transfer — design constraints (+4 more)

### Community 43 - "Feature Specification: Provider-Neutral Interbank Transfer"
Cohesion: 0.12
Nodes (16): Assumptions, Constitution Alignment, Edge Cases, Feature Specification: Provider-Neutral Interbank Transfer, Functional Requirements, Key Entities, Measurable Outcomes, NEEDS CLARIFICATION (+8 more)

### Community 44 - "PROJECT_ROADMAP.md"
Cohesion: 0.18
Nodes (10): Critical dependencies and open decisions, Các phase, Mục tiêu dự án, Phase 0, Phase 1, Phase 2, Phase 8, Phases 0-2 (+2 more)

### Community 45 - "containers"
Cohesion: 0.06
Nodes (37): env, envFrom, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe (+29 more)

### Community 46 - "batch-report.yaml"
Cohesion: 0.18
Nodes (10): batch_id, blocked_tasks, completed_tasks, errors, protocol_violation, artifact, used_for, upstream_artifacts_consumed (+2 more)

### Community 47 - "files_changed"
Cohesion: 0.06
Nodes (34): batch_id, blocked_tasks, completed_tasks, files_changed, T004, T005, T006, T007 (+26 more)

### Community 48 - "Implementation Plan: Corporate Approval Core"
Cohesion: 0.11
Nodes (18): Applied Guidelines, Complexity Tracking, Constitution Check, Deferred: Transfer enforcement and financial execution, Implementation Plan: Corporate Approval Core, Implementation Steps, Project Structure, Requirement Mapping (+10 more)

### Community 49 - ".submit"
Cohesion: 0.13
Nodes (11): RetailTransferService, RetailTransferRecord, RetailTransferRequest, RetailTransferType, DEPOSIT, INTERNAL, WITHDRAWAL, LedgerExecutionClient (+3 more)

### Community 50 - "corporate-service/src/main/resources/application.yml"
Cohesion: 0.22
Nodes (8): corporate, console, logging, structured, server, port, shutdown, format

### Community 51 - "spring"
Cohesion: 0.18
Nodes (11): name, import, password, url, username, enabled, spring, application (+3 more)

### Community 52 - "Service Template"
Cohesion: 0.20
Nodes (9): Baseline, Build and run in local Kubernetes, Build and test, Configuration and secrets, Database ownership proof, Logging and local observability, Reliability foundation proof, Run locally (+1 more)

### Community 53 - ".submit"
Cohesion: 0.10
Nodes (15): PaymentService, PaymentRecord, PaymentStatus, FAILED, PENDING_SUBMISSION, SETTLED, UNRESOLVED, SubmitPaymentRequest (+7 more)

### Community 54 - "ApprovalPolicyResponse"
Cohesion: 0.17
Nodes (8): ApprovalPolicyLevel, ApprovalPolicyResponse, ApprovalPolicyStatus, ACTIVE, DRAFT, INACTIVE, ApprovalPolicyRepository, PolicyBuilder

### Community 55 - "Phase 2 — Decision Gate"
Cohesion: 0.25
Nodes (8): API and event scope — exact contracts remain open, Approved authorization principal boundary, Approved first-slice domain contract, Confirmed constraints, Gate outcome, Phase 2 — Decision Gate, Purpose, Remaining OPEN implementation details

### Community 56 - "payment-service/src/main/resources/application.yml"
Cohesion: 0.06
Nodes (33): name, import, password, url, username, health, web, include (+25 more)

### Community 57 - "audit-service/src/main/resources/application.yml"
Cohesion: 0.06
Nodes (33): name, audit, security, import, password, url, username, health (+25 more)

### Community 58 - "security"
Cohesion: 0.25
Nodes (8): security, audience, issuer-uri, jwk-set-uri, party-client-id, party-client-secret, party-service-base-url, token-uri

### Community 59 - "ADR-001: Microservices Architecture"
Cohesion: 0.33
Nodes (6): ADR-001: Microservices Architecture, Alternatives, Consequences, Context, Decision, Dependencies

### Community 60 - "ADR-005: VND Only"
Cohesion: 0.33
Nodes (6): ADR-005: VND Only, Alternatives, Consequences, Context, Decision, Dependencies

### Community 61 - "ADR-006: Internal + Interbank Transfer"
Cohesion: 0.33
Nodes (6): ADR-006: Internal + Interbank Transfer, Alternatives, Consequences, Context, Decision, Dependencies

### Community 62 - "ADR-008: Loan Excluded from MVP"
Cohesion: 0.33
Nodes (6): ADR-008: Loan Excluded from MVP, Alternatives, Consequences, Context, Decision, Dependencies

### Community 63 - "interbank-transfer/checkpoints/tasks-to-impl.yaml"
Cohesion: 0.18
Nodes (10): blocked_tasks, completed_tasks, metadata, generated, source_plan, validation, blocking_issues, passed (+2 more)

### Community 64 - "files_changed"
Cohesion: 0.22
Nodes (9): files_changed, T005, T011, change_type, description, file, change_type, description (+1 more)

### Community 65 - "client"
Cohesion: 0.15
Nodes (13): provider, registration, token-uri, client, authorization-grant-type, client-id, client-secret, provider (+5 more)

### Community 66 - "health"
Cohesion: 0.29
Nodes (7): health, readiness, group, probes, show-details, enabled, include

### Community 67 - "Identity Service"
Cohesion: 0.15
Nodes (11): ADR-013: Identity-Authoritative User Authorization, Consequences, Context, Decisions, Initial scope, Build and tests, Configuration, Container image (+3 more)

### Community 68 - "package.json"
Cohesion: 0.15
Nodes (17): eslintConfig, devDependencies, eslint@^9, eslint-config-next@16.4.6, @types/node@^20, @types/react@^19, @types/react-dom@^19, typescript@^5 (+9 more)

### Community 70 - "ADR-002: Retail + Corporate Banking"
Cohesion: 0.33
Nodes (6): ADR-002: Retail + Corporate Banking, Alternatives, Consequences, Context, Decision, Dependencies

### Community 71 - "ADR-003: Multi-channel Banking"
Cohesion: 0.33
Nodes (6): ADR-003: Multi-channel Banking, Alternatives, Consequences, Context, Decision, Dependencies

### Community 72 - "ADR-004: Shared PostgreSQL, Schema-per-service"
Cohesion: 0.33
Nodes (6): ADR-004: Shared PostgreSQL, Schema-per-service, Alternatives, Consequences, Context, Decision, Dependencies

### Community 73 - "LedgerServiceTests.java"
Cohesion: 0.09
Nodes (15): LedgerService, InvalidJournalException, JournalAlreadyReversedException, JournalNotFoundException, UnbalancedJournalException, BalanceResponse, JournalEntryRequest, JournalRequest (+7 more)

### Community 74 - "ADR-009: Multi-branch Banking"
Cohesion: 0.33
Nodes (6): ADR-009: Multi-branch Banking, Alternatives, Consequences, Context, Decision, Dependencies

### Community 75 - "ADR-011: Phase-based Configuration and Secret Management"
Cohesion: 0.25
Nodes (8): ADR-011: Phase-based Configuration and Secret Management, Alternatives, Consequences, Context, Decision, Dependencies, Phase 1 — Local development, Phase 2 — Production

### Community 76 - "T012"
Cohesion: 0.29
Nodes (7): command, failed, failures, passed, skipped, test_results, T012

### Community 77 - "Logical ERD — Banking Clone"
Cohesion: 0.33
Nodes (6): Database boundaries, Entity and constraint proposal, Key constraints and unresolved details, Logical ERD — Banking Clone, Logical relationships, Ownership and access

### Community 78 - "T013"
Cohesion: 0.33
Nodes (6): command, failed, failures, passed, skipped, T013

### Community 79 - "Branch Service"
Cohesion: 0.33
Nodes (5): Authorization claim boundary, Branch Service, Build and tests, Configuration and database, Endpoints

### Community 80 - "management"
Cohesion: 0.33
Nodes (6): web, include, management, endpoint, endpoints, exposure

### Community 82 - "Party Service"
Cohesion: 0.33
Nodes (5): Build and test, Configuration, Database, Party Service, Scope

### Community 83 - "Event Contracts"
Cohesion: 0.40
Nodes (5): Delivery, retry and deduplication, Envelope chung, Event catalog, Event Contracts, Event-specific notes

### Community 84 - "traceability"
Cohesion: 0.33
Nodes (6): traceability, files, plan_item, requirement, status, task

### Community 93 - "T012"
Cohesion: 0.40
Nodes (5): evidence_mapping, T012, evidence, output, upstream

### Community 94 - "T003"
Cohesion: 0.50
Nodes (4): T003, change_type, description, file

### Community 95 - "T004"
Cohesion: 0.50
Nodes (4): T004, change_type, description, file

### Community 96 - "containers"
Cohesion: 0.05
Nodes (45): env, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe, securityContext (+37 more)

### Community 97 - "T006"
Cohesion: 0.50
Nodes (4): T006, change_type, description, file

### Community 98 - "T007"
Cohesion: 0.50
Nodes (4): T007, change_type, description, file

### Community 99 - "T008"
Cohesion: 0.50
Nodes (4): T008, change_type, description, file

### Community 100 - "T012"
Cohesion: 0.50
Nodes (4): T012, change_type, description, file

### Community 101 - "T014"
Cohesion: 0.50
Nodes (4): T014, change_type, description, file

### Community 102 - "PartyService"
Cohesion: 0.08
Nodes (13): IdempotencyRecord, IndividualProfileResponse, PartyResponse, PartyStatus, ACTIVE, INACTIVE, PartyType, INDIVIDUAL (+5 more)

### Community 103 - "Transfer Service"
Cohesion: 0.40
Nodes (4): Run, Scope, Transfer Service, Verification

### Community 105 - "validation"
Cohesion: 0.17
Nodes (11): mapping, plan_item, tasks, metadata, generated, source_plan, validation, passed (+3 more)

### Community 106 - "metadata"
Cohesion: 0.17
Nodes (11): mapping, plan_items, requirement, metadata, feature, generated, source_spec, validation (+3 more)

### Community 107 - "containers"
Cohesion: 0.06
Nodes (37): env, envFrom, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe (+29 more)

### Community 108 - "TransferExceptions"
Cohesion: 0.18
Nodes (5): TransferExceptions, ConflictException, NotFoundException, UnsupportedException, ValidationException

### Community 109 - "api-documentation/checkpoints/batch-report.yaml"
Cohesion: 0.07
Nodes (27): command, failed, passed, skipped, batch_id, blocked_tasks, completed_tasks, endpoint_probes (+19 more)

### Community 110 - "Centralized API Documentation Repository Audit"
Cohesion: 0.14
Nodes (12): 10. Dependency compatibility, 11. Blockers and unresolved decisions, 13. Audit conclusion, 1. Service inventory, 2. Technology stack, 4. Existing Springdoc / Swagger / OpenAPI, 5. API Gateway and routing, 6. Authentication and authorization (+4 more)

### Community 111 - "uuid"
Cohesion: 0.09
Nodes (5): EntrySide, CREDIT, DEBIT, JournalEntryResponse, CorporateApprovalResponse

### Community 112 - "Migration Summary Report"
Cohesion: 0.13
Nodes (13): Checkpoint Validation Summary, Consistency, Constitution Compliance, End-to-End Traceability Matrix, Findings, Metrics Summary, Migration Summary Report, Testing Evidence (+5 more)

### Community 113 - "org.springframework.jdbc.core.JdbcTemplate"
Cohesion: 0.10
Nodes (6): JdbcAccountRepository, JdbcAuditRepository, BranchRepository, JdbcApproverEligibilityClient, OutcomeSource, TransferRepository

### Community 114 - "traceability"
Cohesion: 0.29
Nodes (6): traceability, files, plan_items, requirement, status, tasks

### Community 115 - "Smoke Test Verdict"
Cohesion: 0.33
Nodes (5): Command, Coverage warning, Deferred scope, Evidence, Smoke Test Verdict

### Community 116 - "summary"
Cohesion: 0.09
Nodes (21): item_id, tasks, metadata, generated, source_plan, missing_plan_items, orphan_tasks, phases (+13 more)

### Community 117 - "Decisions"
Cohesion: 0.33
Nodes (5): Decisions, Interbank Transfer Research, Outcome safety, Scope decision, Unresolved Clarifications

### Community 118 - "Centralized API Documentation Implementation Plan"
Cohesion: 0.14
Nodes (14): 10. Acceptance criteria, 11. Blockers and decisions requiring approval, 12. Current implementation status, 1. Architecture decision, 2. Service scope, 3. Required dependencies, 5. Files and modules, 7. Task dependencies (+6 more)

### Community 119 - "Requirements Quality Checklist"
Cohesion: 0.33
Nodes (5): Completeness, Constitution Alignment, Requirement ID Coverage, Requirements Quality Checklist, Testability

### Community 120 - "org.springframework.boot.autoconfigure.SpringBootApplication"
Cohesion: 0.16
Nodes (9): ApiDocsServiceApplication, BranchServiceApplication, CorporateServiceApplication, LedgerServiceApplication, NotificationServiceApplication, PartyServiceApplication, PaymentServiceApplication, ReportingServiceApplication (+1 more)

### Community 122 - "channels-operations/checkpoints/tasks-to-impl.yaml"
Cohesion: 0.20
Nodes (9): blocked_tasks, completed_tasks, metadata, generated, source_plan, validation, blocking_issues, passed (+1 more)

### Community 123 - "spring"
Cohesion: 0.07
Nodes (28): name, import, password, url, username, health, web, include (+20 more)

### Community 124 - "AccountResponse"
Cohesion: 0.11
Nodes (15): AccountService, AccountNotFoundException, DuplicateAccountException, InvalidAccountException, InvalidAccountTransitionException, AccountResponse, AccountStatus, ACTIVE (+7 more)

### Community 126 - "6. Implementation tasks"
Cohesion: 0.17
Nodes (12): 6. Implementation tasks, T001 — Add dependency management, T002 — Add per-service OpenAPI configuration, T003 — Add JWT bearer documentation, T004 — Create the docs service, T005 — Configure local and Kubernetes-local access, T006 — Add CORS policy, T007 — Add OpenAPI contract tests (+4 more)

### Community 127 - "spring"
Cohesion: 0.06
Nodes (35): account, identity, security, name, import, password, url, username (+27 more)

### Community 128 - "page.tsx"
Cohesion: 0.47
Nodes (4): IconName, IconProps, icons, NavigationItem

### Community 129 - "spring"
Cohesion: 0.07
Nodes (27): name, import, password, url, username, health, web, include (+19 more)

### Community 130 - "Payment Service"
Cohesion: 0.50
Nodes (3): Payment Service, Scope, Verification

### Community 131 - "3. Existing endpoint inventory"
Cohesion: 0.20
Nodes (10): 3. Existing endpoint inventory, Account, Audit, Branch, Corporate, Identity, Ledger, Party (+2 more)

### Community 132 - "org.junit.jupiter.api.Test"
Cohesion: 0.12
Nodes (3): IdentityPrincipalEndpointIntegrationTests, DatabaseOwnershipIntegrationTests, ServiceTemplateApplicationTests

### Community 133 - "next"
Cohesion: 0.29
Nodes (3): nextConfig, metadata, next

### Community 135 - "reporting-service/src/main/resources/application.yml"
Cohesion: 0.06
Nodes (33): name, import, password, url, username, health, web, include (+25 more)

### Community 136 - "channels-operations/checkpoints/spec-to-plan.yaml"
Cohesion: 0.11
Nodes (18): metadata, generated, source_spec, target_plan, missing_requirements, orphan_plan_items, requirements, plan_items (+10 more)

### Community 137 - "Phase 4 - Retail Internal Transfer va Teller"
Cohesion: 0.40
Nodes (4): Acceptance, API, Phase 4 - Retail Internal Transfer va Teller, Scope

### Community 138 - "postgres"
Cohesion: 0.15
Nodes (11): name, image, ports, restart, volumes, services, postgres, volumes (+3 more)

### Community 139 - "org.springframework.boot.context.properties.ConfigurationProperties"
Cohesion: 0.15
Nodes (4): AccountSecurityProperties, IdentityDatabaseProperties, IdentitySecurityProperties, IdentityServiceApplication

### Community 140 - "Implementation Plan: Channels, Audit, Notification and Reporting"
Cohesion: 0.12
Nodes (17): Applied Guidelines, Constitution Check, Deferred Decisions, Execution Sequence, Implementation Plan: Channels, Audit, Notification and Reporting, Implementation Steps, Project Structure, Requirement Mapping (+9 more)

### Community 141 - "services"
Cohesion: 0.05
Nodes (38): api-docs, services, name, import, health, web, include, readiness (+30 more)

### Community 144 - "environment"
Cohesion: 0.07
Nodes (27): ACCOUNT_SERVICE_APP_PASSWORD, ACCOUNT_SERVICE_MIGRATOR_PASSWORD, AUDIT_SERVICE_APP_PASSWORD, AUDIT_SERVICE_MIGRATOR_PASSWORD, BRANCH_SERVICE_APP_PASSWORD, BRANCH_SERVICE_MIGRATOR_PASSWORD, CORPORATE_SERVICE_APP_PASSWORD, CORPORATE_SERVICE_MIGRATOR_PASSWORD (+19 more)

### Community 146 - "Phase 6 - Provider-Neutral Interbank Transfer"
Cohesion: 0.40
Nodes (4): Acceptance, API, Phase 6 - Provider-Neutral Interbank Transfer, Sandbox scope

### Community 147 - "ADR-010: Phase 1 Technical Baseline"
Cohesion: 0.33
Nodes (6): ADR-010: Phase 1 Technical Baseline, Alternatives, Consequences, Context, Decision, Dependencies

### Community 148 - "Banking Clone — Architecture Document"
Cohesion: 0.67
Nodes (3): Banking Clone — Architecture Document, Hoàn tất tài liệu thiết kế, Khảo sát và kiểm tra

### Community 149 - "Phase 7 Spec-Level Research"
Cohesion: 0.33
Nodes (5): Decision: Keep Phase 7 provider-neutral, Open clarification: API Gateway, Open clarification: event broker, Open clarification: notification provider, Phase 7 Spec-Level Research

### Community 150 - "ADR-007: Configurable Corporate Approval"
Cohesion: 0.33
Nodes (6): ADR-007: Configurable Corporate Approval, Alternatives, Consequences, Context, Decision, Dependencies

### Community 151 - "Phase 3 - Account va Ledger"
Cohesion: 0.40
Nodes (4): Acceptance, API, Pham vi, Phase 3 - Account va Ledger

### Community 152 - "Requirements Quality Checklist"
Cohesion: 0.33
Nodes (5): Completeness, Constitution Alignment, Requirement ID Coverage, Requirements Quality Checklist, Testability

### Community 153 - "8. Testing strategy"
Cohesion: 0.33
Nodes (6): 8. Testing strategy, Centralized checks, Dependency and build checks, Docker/Kubernetes checks, Per-service checks, Security checks

### Community 158 - "containers"
Cohesion: 0.06
Nodes (37): env, envFrom, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe (+29 more)

### Community 159 - "12. Recommended Phase 2 decision options"
Cohesion: 0.50
Nodes (4): 12. Recommended Phase 2 decision options, Option A — Minimal centralized local aggregator, Option B — Static Swagger UI in an existing developer/service template, Option C — Per-service Swagger UI only

### Community 160 - "4. Required configuration"
Cohesion: 0.50
Nodes (4): 4. Required configuration, Central docs service, Per microservice, Security

### Community 161 - "files_changed"
Cohesion: 0.09
Nodes (23): files_changed, T001, T002, T003, T004, T005, T006, T007 (+15 more)

### Community 162 - "Core Banking frontend implementation standards"
Cohesion: 0.29
Nodes (7): Accessibility and visual consistency, Core Banking frontend implementation standards, Financial UI types, Forms and transaction safety, Rendering and data, Suggested structure, Verification

### Community 165 - "summary"
Cohesion: 0.09
Nodes (22): item_id, tasks, metadata, generated, source_plan, target_tasks, missing_plan_items, orphan_tasks (+14 more)

### Community 166 - "Phases.md"
Cohesion: 0.17
Nodes (10): Acceptance, Decisions Still Open, Phase 8 - Production Readiness, Scope, Status, Feature Specification: Production Readiness, Functional Requirements, NEEDS CLARIFICATION (+2 more)

### Community 167 - "CorporateExceptionHandler.java"
Cohesion: 0.09
Nodes (11): ApprovalPolicyService, ApprovalPolicyConflictException, ApprovalPolicyNotFoundException, InvalidApprovalPolicyException, IneligibleApproverException, MakerCheckerException, DuplicateOrganizationPartyException, InvalidOrganizationException (+3 more)

### Community 168 - "validation"
Cohesion: 0.18
Nodes (10): blocked_tasks, completed_tasks, metadata, generated, source_plan, validation, blocking_issues, passed (+2 more)

### Community 169 - "Centralized API Documentation Implementation Report"
Cohesion: 0.22
Nodes (8): Centralized API Documentation Implementation Report, Documentation URLs, Implemented components, Modified modules, Remaining limitations, Rollback, Security considerations, Tests executed

### Community 170 - "Implementation Plan: Production Readiness"
Cohesion: 0.12
Nodes (16): Applied Guidelines, Constitution Check, Deferred Decisions, Implementation Plan: Production Readiness, Implementation Steps, Project Structure, Requirement Mapping, Step 1: Release and CI gate (+8 more)

### Community 171 - "Core Banking frontend"
Cohesion: 0.29
Nodes (7): Banking safety and UX, Core Banking frontend, Product context, Product surfaces, Required preflight, Technical guidance, Visual direction

### Community 172 - "Phase 7 - Channels, Audit, Notification and Reporting"
Cohesion: 0.17
Nodes (10): Acceptance, API, Central API Documentation, Deferred Work, Existing Audit API, Phase 7 - Channels, Audit, Notification and Reporting, Planned Notification API, Planned Reporting API (+2 more)

### Community 173 - "channels-operations/checkpoints/batch-report.yaml"
Cohesion: 0.25
Nodes (7): batch_id, blocked_tasks, completed_tasks, protocol_violation, warnings, message, severity

### Community 174 - "production-readiness/checkpoints/spec-to-plan.yaml"
Cohesion: 0.10
Nodes (19): metadata, generated, source_spec, target_plan, missing_requirements, orphan_plan_items, items, requirements (+11 more)

### Community 177 - "org.springframework.stereotype.Service"
Cohesion: 0.11
Nodes (15): IdentityPrincipalService, IdentityMapping, IdentityPrincipalResponse, IdentityStatus, ACTIVE, DISABLED, ScopedGrant, ScopeType (+7 more)

### Community 178 - "org.springframework.transaction.annotation.Transactional"
Cohesion: 0.13
Nodes (3): ApprovalDecisionResponse, ApprovalPolicyLevelRequest, CreateApprovalPolicyRequest

### Community 179 - "jakarta.servlet.http.HttpServletRequest"
Cohesion: 0.20
Nodes (8): PartyService, IdempotencyConflictException, IdempotencyInProgressException, InvalidPartyException, PartyNotFoundException, StalePartyVersionException, PartyController, PartyExceptionHandler

### Community 180 - "Service postgres"
Cohesion: 0.14
Nodes (17): Service postgres, Service keycloak, name, name, port, protocol, apiVersion, kind (+9 more)

### Community 181 - "ChannelContext"
Cohesion: 0.40
Nodes (4): ChannelContext, ADMIN, INTERNET_BANKING, TELLER

### Community 182 - "MembershipRepository"
Cohesion: 0.32
Nodes (4): OrganizationNotFoundException, MandateResponse, MembershipResponse, MembershipRepository

### Community 184 - "Phase 7 Smoke Test Verdict"
Cohesion: 0.29
Nodes (6): Command, Deduplication Boundaries, Deferred Scope, Evidence, Journey Coverage, Phase 7 Smoke Test Verdict

### Community 185 - "test_results"
Cohesion: 0.33
Nodes (6): command, failed, passed, skipped, test_results, audit_service

### Community 186 - "containers"
Cohesion: 0.06
Nodes (37): env, envFrom, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe (+29 more)

### Community 187 - "containers"
Cohesion: 0.06
Nodes (37): env, envFrom, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe (+29 more)

### Community 188 - "validation"
Cohesion: 0.12
Nodes (16): command, failed, passed, command, failed, passed, skipped, command (+8 more)

### Community 189 - "containers"
Cohesion: 0.06
Nodes (37): env, envFrom, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe (+29 more)

### Community 190 - "AuditRecord"
Cohesion: 0.08
Nodes (21): 10. Notification, 11. Audit, 12. Reporting, 1. Identity & Access, 2. Party / Customer, 3. Corporate, 4. Branch, 5. Account (+13 more)

### Community 191 - "account_service"
Cohesion: 0.40
Nodes (5): command, failed, passed, skipped, account_service

### Community 192 - "notification_service"
Cohesion: 0.40
Nodes (5): command, failed, passed, skipped, notification_service

### Community 193 - "containers"
Cohesion: 0.06
Nodes (37): env, envFrom, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe (+29 more)

### Community 194 - "identity-api.ts"
Cohesion: 0.36
Nodes (4): requestIdentityPrincipal(), identityEndpoint(), loadCurrentPrincipal(), validateSecureUrl()

### Community 196 - "containers"
Cohesion: 0.06
Nodes (37): env, envFrom, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe (+29 more)

### Community 197 - "Phase 8 Release Checklist"
Cohesion: 0.29
Nodes (7): Build and verification, Evidence references, Operations and recovery, Owner approval record, Phase 8 Release Checklist, Release approval, Security and configuration

### Community 198 - "containers"
Cohesion: 0.06
Nodes (37): env, envFrom, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe (+29 more)

### Community 199 - "reporting_service"
Cohesion: 0.40
Nodes (5): command, failed, passed, skipped, reporting_service

### Community 201 - "transfer_service"
Cohesion: 0.40
Nodes (5): transfer_service, command, failed, passed, skipped

### Community 202 - "workspace"
Cohesion: 0.40
Nodes (5): workspace, command, failed, passed, skipped

### Community 203 - "Kubernetes Deployment Catalog"
Cohesion: 0.29
Nodes (6): Deploy locally, Kubernetes Deployment Catalog, Local prerequisites, Runtime prerequisites, Secret references, Service inventory

### Community 204 - "containers"
Cohesion: 0.06
Nodes (37): env, envFrom, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe (+29 more)

### Community 205 - "containers"
Cohesion: 0.06
Nodes (36): env, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe, securityContext (+28 more)

### Community 206 - "containers"
Cohesion: 0.06
Nodes (36): env, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe, securityContext (+28 more)

### Community 207 - "OrganizationService"
Cohesion: 0.17
Nodes (7): OrganizationService, OrganizationResponse, OrganizationStatus, ACTIVE, INACTIVE, PartyReferenceClient, OrganizationRepository

### Community 209 - "data"
Cohesion: 0.09
Nodes (22): apiVersion, data, ACCOUNT_OPENAPI_URL, AUDIT_OPENAPI_URL, BRANCH_OPENAPI_URL, CORPORATE_APPROVAL_BASE_URL, CORPORATE_OPENAPI_URL, IDENTITY_OPENAPI_URL (+14 more)

### Community 211 - "environment"
Cohesion: 0.22
Nodes (9): KC_BOOTSTRAP_ADMIN_PASSWORD, KC_BOOTSTRAP_ADMIN_USERNAME, KC_DB, KC_DB_PASSWORD, KC_DB_URL, KC_DB_USERNAME, KC_HEALTH_ENABLED, KC_HOSTNAME (+1 more)

### Community 212 - "keycloak"
Cohesion: 0.22
Nodes (9): postgres, command, depends_on, image, ports, restart, volumes, condition (+1 more)

### Community 213 - "scripts"
Cohesion: 0.33
Nodes (6): scripts, build, dev, lint, start, test

### Community 214 - "Phase 7"
Cohesion: 0.33
Nodes (6): Acceptance, Current evidence, Dependencies, Objective, Phase 7, Scope

### Community 215 - "healthcheck"
Cohesion: 0.53
Nodes (6): interval, retries, test, timeout, healthcheck, healthcheck

### Community 216 - "PartyReferenceResponse"
Cohesion: 0.24
Nodes (4): PartyReferenceResponse, PartyType, INDIVIDUAL, ORGANIZATION

### Community 217 - "identity-response.mjs"
Cohesion: 0.33
Nodes (5): IdentityLookup, isGrant(), isRecord(), parseIdentityPrincipal(), scopeTypes

### Community 219 - "account"
Cohesion: 0.29
Nodes (6): account, identity, security, allow-http, base-url, allow-http

### Community 220 - "kustomization.yaml"
Cohesion: 0.50
Nodes (3): apiVersion, kind, resources

### Community 221 - "dependencies"
Cohesion: 0.25
Nodes (8): dependencies, next@16.4.6, next-auth@^5.0.0-beta.32, react@19.3.0, react-dom@19.3.0, next-auth, react, react-dom

### Community 222 - "production-readiness/checkpoints/batch-report.yaml"
Cohesion: 0.25
Nodes (7): batch_id, blocked_tasks, completed_tasks, protocol_violation, warnings, message, severity

### Community 223 - "account"
Cohesion: 0.29
Nodes (6): account, identity, security, allow-http, base-url, allow-http

### Community 224 - "audit"
Cohesion: 0.50
Nodes (3): audit, security, allow-http

### Community 226 - "api_docs_module"
Cohesion: 0.40
Nodes (5): command, failed, passed, skipped, api_docs_module

### Community 227 - "docker_images"
Cohesion: 0.40
Nodes (5): command, failed, passed, skipped, docker_images

### Community 228 - "focused_log_redaction"
Cohesion: 0.40
Nodes (5): command, failed, passed, skipped, focused_log_redaction

### Community 229 - "CURRENT_STATUS.md"
Cohesion: 0.25
Nodes (6): Blockers, Completed evidence, Current phase, Current task, Incomplete work, Working tree

### Community 230 - "test_results"
Cohesion: 0.15
Nodes (13): command, failed, passed, reason, skipped, status, test_results, kubernetes_manifests (+5 more)

### Community 231 - "workspace"
Cohesion: 0.40
Nodes (5): workspace, command, failed, passed, skipped

### Community 232 - "audit"
Cohesion: 0.50
Nodes (3): audit, security, allow-http

### Community 234 - "module_counts"
Cohesion: 0.14
Nodes (14): account-service, api-docs-service, audit-service, branch-service, corporate-service, identity-service, ledger-service, notification-service (+6 more)

### Community 235 - "production-readiness/checkpoints/tasks-to-impl.yaml"
Cohesion: 0.14
Nodes (12): Not verified or blocked, Phase 8 Smoke Test Verdict, Release decision, Verified, blocked_reasons, T023, blocked_tasks, completed_tasks (+4 more)

### Community 236 - "module_counts"
Cohesion: 0.14
Nodes (14): module_counts, account-service, api-docs-service, audit-service, branch-service, corporate-service, identity-service, ledger-service (+6 more)

### Community 239 - "branch"
Cohesion: 0.50
Nodes (3): branch, security, allow-http

### Community 240 - "ledger_service"
Cohesion: 0.40
Nodes (5): command, failed, passed, skipped, ledger_service

### Community 241 - "evidence"
Cohesion: 0.40
Nodes (5): command, tests_failed, tests_passed, tests_skipped, evidence

### Community 242 - "workspace_smoke"
Cohesion: 0.50
Nodes (4): workspace_smoke, command, failed, passed

### Community 243 - "branch"
Cohesion: 0.50
Nodes (3): branch, security, allow-http

### Community 244 - "corporate"
Cohesion: 0.50
Nodes (3): corporate, security, allow-http

### Community 245 - "Outbox Backlog, Retry, and Replay Runbook"
Cohesion: 0.33
Nodes (6): Assess a backlog, Controlled replay, Implementation prerequisites, Outbox Backlog, Retry, and Replay Runbook, Restore normal delivery, Scope and guardrails

### Community 246 - "corporate"
Cohesion: 0.50
Nodes (3): corporate, security, allow-http

### Community 248 - "identity"
Cohesion: 0.50
Nodes (3): identity, security, allow-http

### Community 249 - "identity"
Cohesion: 0.50
Nodes (3): identity, security, allow-http

### Community 250 - "Page patterns"
Cohesion: 0.17
Nodes (12): Accounts and transactions, Audit and reporting, Banking workspace shell, Core Banking UI guidelines, Corporate approval, Frontend/API boundary, Interaction and states, Overview (+4 more)

### Community 251 - "identity-api.test.mjs"
Cohesion: 0.21
Nodes (4): navigation, principal, realm, realmUrl

### Community 252 - "party"
Cohesion: 0.50
Nodes (3): party, security, allow-http

### Community 254 - "party"
Cohesion: 0.50
Nodes (3): party, security, allow-http

### Community 255 - "payment"
Cohesion: 0.50
Nodes (3): payment, security, allow-http

### Community 256 - "payment"
Cohesion: 0.50
Nodes (3): payment, security, allow-http

### Community 257 - "reporting"
Cohesion: 0.50
Nodes (3): reporting, security, allow-http

### Community 259 - "reporting"
Cohesion: 0.50
Nodes (3): reporting, security, allow-http

### Community 260 - "transfer"
Cohesion: 0.50
Nodes (3): allow-http, transfer, security

### Community 261 - "transfer"
Cohesion: 0.50
Nodes (3): allow-http, transfer, security

### Community 262 - "Phases 3-6"
Cohesion: 0.40
Nodes (5): Phase 3, Phase 4, Phase 5, Phase 6, Phases 3-6

### Community 263 - "Core Banking frontend"
Cohesion: 0.50
Nodes (3): Core Banking frontend, Run locally, Validate

### Community 264 - "Alert Response Runbook"
Cohesion: 0.33
Nodes (6): Alert Response Runbook, First response, Owner inputs required, Resolution and handoff, Response and recovery, Triage

### Community 265 - "Service Recovery Runbook"
Cohesion: 0.33
Nodes (6): Diagnose, Environment-specific details to approve, Recover, Safety checks, Service Recovery Runbook, Verify and hand off

### Community 267 - "Backup and Restore Rehearsal"
Cohesion: 0.40
Nodes (5): Backup and Restore Rehearsal, Before the rehearsal, Complete and clean up, Rehearse restore, Service-specific cautions

### Community 268 - "Core Banking design system"
Cohesion: 0.22
Nodes (9): Color system, Components, Core Banking design system, Design intent, Responsive and accessibility, Safety boundary, Spacing and surfaces, Typography and numbers (+1 more)

### Community 269 - "Disaster Recovery and Dependency-Failure Smoke Test Plan"
Cohesion: 0.50
Nodes (4): Disaster Recovery and Dependency-Failure Smoke Test Plan, Exit criteria, Scenarios, Test controls

### Community 270 - "Independent Service Rollback Rehearsal"
Cohesion: 0.50
Nodes (4): Evidence and cleanup, Independent Service Rollback Rehearsal, Preconditions, Rehearse

### Community 271 - "Phase 8 Security, Recovery, and Rollback Evidence"
Cohesion: 0.50
Nodes (4): Additional local evidence, Phase 8 Security, Recovery, and Rollback Evidence, Recovery and rollback, Security

## Knowledge Gaps
- **69 isolated node(s):** `ACTIVE`, `INACTIVE`, `com.banking.platform:transfer-service`, `com.banking.platform:core-banking-system`, `ACTIVE` (+64 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 1996 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **44 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AuditRecord` connect `AuditRecord` to `org.springframework.jdbc.core.JdbcTemplate`, `LedgerEndpointIntegrationTests.java`, `org.springframework.web.bind.annotation.PostMapping`, `uuid`?**
  _High betweenness centrality (0.073) - this node is a cross-community bridge._
- **What connects `ACTIVE`, `INACTIVE`, `com.banking.platform:transfer-service` to the rest of the system?**
  _69 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `org.springframework.http.ResponseEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.06905370843989769 - nodes in this community are weakly interconnected._
- **Why does `Bounded Contexts` connect `AuditRecord` to `architecture/BANKING_ARCHITECTURE.md`?**
  _High betweenness centrality (0.072) - this node is a cross-community bridge._
- **Should `OpenApiProxyControllerTests.java` be split into smaller, more focused modules?**
  _Cohesion score 0.09846153846153846 - nodes in this community are weakly interconnected._
- **Should `org.springframework.context.annotation.Bean` be split into smaller, more focused modules?**
  _Cohesion score 0.06416978776529339 - nodes in this community are weakly interconnected._
- **Should `org.springframework.stereotype.Component` be split into smaller, more focused modules?**
  _Cohesion score 0.08361581920903954 - nodes in this community are weakly interconnected._