# Graph Report - Core-Banking-System  (2026-11-23)

## Corpus Check
- 407 files · ~95,387 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 31 file(s) not represented in the graph (top: .example 14, (none) 14, .jar 1)

## Summary
- 4073 nodes · 7625 edges · 253 communities (199 shown, 54 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 307 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `2b984937`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- jakarta.servlet.http.HttpServletRequest
- OpenApiProxyControllerTests.java
- org.springframework.context.annotation.Bean
- org.springframework.stereotype.Component
- containers
- containers
- NotificationRecord
- service-template/src/main/resources/application.yml
- socialpilot-ai-frontend
- identity-service/src/main/resources/application.yml
- party-service/src/main/resources/application.yml
- OrganizationEndpointIntegrationTests
- SocialPilot AI design system
- plan-to-tasks.yaml
- branch-service/src/main/resources/application.yml
- files_changed
- org.junit.jupiter.api.Test
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
- Override
- Rules
- corporate
- Chi tiết quyết định
- Implementation Plan: Provider-Neutral Interbank Transfer
- Decisions
- API Contracts
- Bounded Contexts
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
- ApprovalPolicyRepository.java
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
- SocialPilot AI product flows
- ADR-002: Retail + Corporate Banking
- ADR-003: Multi-channel Banking
- ADR-004: Shared PostgreSQL, Schema-per-service
- JournalResponse
- ADR-009: Multi-branch Banking
- ADR-011: Phase-based Configuration and Secret Management
- T012
- Logical ERD — Banking Clone
- T013
- Branch Service
- management
- party-service
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
- org.springframework.transaction.annotation.Transactional
- Transfer Service
- com.banking.platform:transfer-service
- validation
- metadata
- containers
- TransferExceptions
- api-documentation/checkpoints/batch-report.yaml
- Centralized API Documentation Repository Audit
- RetailTransferServiceTests.java
- Migration Summary Report
- uuid
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
- AccountResponse
- 6. Implementation tasks
- spring
- spring
- Payment Service
- 3. Existing endpoint inventory
- com.banking.platform:payment-service
- reporting-service/src/main/resources/application.yml
- channels-operations/checkpoints/spec-to-plan.yaml
- Phase 4 - Retail Internal Transfer va Teller
- ENVIRONMENT_SETUP.md
- IdentitySecurityProperties
- Implementation Plan: Channels, Audit, Notification and Reporting
- services
- org.springframework.boot.context.properties.ConfigurationProperties
- OrganizationEndpointIntegrationTests.java
- CURRENT_STATUS.md
- TransferController
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
- AccountAuthorizationService
- containers
- 12. Recommended Phase 2 decision options
- 4. Required configuration
- files_changed
- BranchSecurityProperties
- OidcPartyReferenceClientTests.java
- command.md
- summary
- Phases.md
- org.springframework.transaction.annotation.Transactional
- validation
- Centralized API Documentation Implementation Report
- Implementation Plan: Production Readiness
- DatabaseConfigurationTests.java
- Phase 7 - Channels, Audit, Notification and Reporting
- channels-operations/checkpoints/batch-report.yaml
- production-readiness/checkpoints/spec-to-plan.yaml
- com.banking.platform:api-docs-service
- BranchEndpointIntegrationTests
- IdentityPrincipalServiceTests.java
- AccountSecurityProperties
- io.swagger.v3.oas.annotations.security.SecurityScheme
- OidcPartyReferenceClient.java
- ChannelContext
- IdentityPrincipalEndpointIntegrationTests
- com.banking.platform:notification-service
- Phase 7 Smoke Test Verdict
- test_results
- containers
- containers
- validation
- containers
- TransferServiceApplication.java
- account_service
- notification_service
- containers
- PartySecurityConfiguration
- DatabaseOwnershipIntegrationTests
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
- AuditRecord
- AuditSecurityPropertiesTests.java
- data
- map
- TransferRecord
- CorporateApprovalClient
- org.springframework.boot.context.properties.EnableConfigurationProperties
- TransferRepository
- PartyService
- PartySecurityConfiguration.java
- ChannelAuthorizationServiceTests
- CorporatePartyClientConfiguration.java
- org.springframework.context.annotation.Bean
- kustomization.yaml
- production/README.md
- production-readiness/checkpoints/batch-report.yaml
- .recordOutcome
- ApprovalEnforcedTransferServiceTests
- PaymentSubmissionClient
- api_docs_module
- docker_images
- focused_log_redaction
- PartyType
- test_results
- workspace
- .create
- workspace-smoke.sh
- module_counts
- production-readiness/checkpoints/tasks-to-impl.yaml
- module_counts
- API Documentation
- DR_DEPENDENCY_SMOKE_TEST_PLAN.md
- PartyReferenceResponse
- ledger_service
- evidence
- workspace_smoke
- PartyStatus
- Alert Response Runbook
- Outbox Backlog, Retry, and Replay Runbook
- Service Recovery Runbook
- Backup and Restore Rehearsal
- TransferSecurityProperties
- Disaster Recovery and Dependency-Failure Smoke Test Plan
- Independent Service Rollback Rehearsal
- Phase 8 Security, Recovery, and Rollback Evidence

## God Nodes (most connected - your core abstractions)
1. `OrganizationEndpointIntegrationTests` - 46 edges
2. `NotificationRecord` - 28 edges
3. `TransferStatus` - 28 edges
4. `files_changed` - 23 edges
5. `ApprovalRepository` - 22 edges
6. `BranchEndpointIntegrationTests` - 21 edges
7. `PartyService` - 20 edges
8. `TransferRecord` - 20 edges
9. `OrganizationAuthorizationService` - 20 edges
10. `IdentityPrincipalEndpointIntegrationTests` - 20 edges

## Surprising Connections (you probably didn't know these)
- `Step 6: Expose approval APIs and enforce command safety` --references--> `CorporateExceptionHandler`  [INFERRED]
  docs/plan/phase/corporate-approval/plan.md → services/corporate-service/src/main/java/com/banking/platform/corporate/web/CorporateExceptionHandler.java
- `Add a microservice` --references--> `ApiDocsProperties`  [INFERRED]
  docs/api-documentation/README.md → services/api-docs-service/src/main/java/com/banking/platform/apidocs/config/ApiDocsProperties.java
- `Authorization` --references--> `ApproverEligibilityClient`  [INFERRED]
  services/corporate-service/README.md → services/corporate-service/src/main/java/com/banking/platform/corporate/application/ApproverEligibilityClient.java
- `JdbcAccountRepository` --implements--> `AccountRepository`  [EXTRACTED]
  services/account-service/src/main/java/com/banking/platform/account/persistence/JdbcAccountRepository.java → services/account-service/src/main/java/com/banking/platform/account/persistence/AccountRepository.java
- `AccountServiceTests` --references--> `AccountRepository`  [EXTRACTED]
  services/account-service/src/test/java/com/banking/platform/account/application/AccountServiceTests.java → services/account-service/src/main/java/com/banking/platform/account/persistence/AccountRepository.java

## Import Cycles
- None detected.

## Communities (253 total, 54 thin omitted)

### Community 0 - "jakarta.servlet.http.HttpServletRequest"
Cohesion: 0.05
Nodes (17): AccountExceptionHandler, BranchNotFoundException, DuplicateBranchCodeException, InvalidBranchException, InvalidPageRequestException, BranchExceptionHandler, CorporateExceptionHandler, IdentityExceptionHandler (+9 more)

### Community 1 - "OpenApiProxyControllerTests.java"
Cohesion: 0.18
Nodes (5): ApiDocsProperties, OpenApiProxyController, OpenApiProxyControllerTests, HttpLedgerExecutionClient, LedgerJournalResponse

### Community 3 - "org.springframework.stereotype.Component"
Cohesion: 0.09
Nodes (7): CorrelationIdFilter, CorrelationIdFilter, CorrelationIdFilter, CorrelationIdFilterTests, CorrelationIdFilter, CorrelationIdFilter, CorrelationIdFilter

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
Nodes (40): name, password, url, username, password, url, username, health (+32 more)

### Community 8 - "socialpilot-ai-frontend"
Cohesion: 0.05
Nodes (39): 1. Architecture, 1. Design for creators, not designers only, 2. Prioritize clarity over decoration, 2. Server vs client boundaries, 3. Support AI workflow mental model, 3. Type safety, 4. Clean component design, 4. Respect operational status states (+31 more)

### Community 9 - "identity-service/src/main/resources/application.yml"
Cohesion: 0.05
Nodes (38): name, password, url, username, password, url, username, health (+30 more)

### Community 10 - "party-service/src/main/resources/application.yml"
Cohesion: 0.05
Nodes (38): name, password, url, username, password, url, username, health (+30 more)

### Community 13 - "SocialPilot AI design system"
Cohesion: 0.05
Nodes (35): Accessibility, Buttons, Card styling, Color system, Component patterns, Core palette, Design intent, Inputs and form fields (+27 more)

### Community 14 - "plan-to-tasks.yaml"
Cohesion: 0.06
Nodes (35): description, item_id, notes, tasks, metadata, generated, source_plan, target_tasks (+27 more)

### Community 15 - "branch-service/src/main/resources/application.yml"
Cohesion: 0.06
Nodes (34): name, branch, security, password, url, username, health, web (+26 more)

### Community 16 - "files_changed"
Cohesion: 0.15
Nodes (13): files_changed, T001, T002, T003, T007, T008, T014, T015 (+5 more)

### Community 18 - "BranchService"
Cohesion: 0.12
Nodes (10): BranchAuthorizationService, BranchService, BranchPage, BranchResponse, BranchStatus, ACTIVE, INACTIVE, BranchRepository (+2 more)

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
Cohesion: 0.09
Nodes (16): ApprovalWorkflowService, OrganizationAuthorizationService, ApprovalDecision, APPROVE, REJECT, ApprovalDecisionResponse, ApprovalHistoryResponse, ApprovalRequestResponse (+8 more)

### Community 23 - "TransferStatus"
Cohesion: 0.11
Nodes (13): TransferStatus, ACCEPTED, APPROVED, EXECUTION_REQUESTED, FAILED, INTERBANK_FAILED, INTERBANK_SETTLED, PENDING_APPROVAL (+5 more)

### Community 24 - "Phase 1 — Technical Foundation"
Cohesion: 0.10
Nodes (20): 1.1 — Chốt technical baseline, 1.2 — Tạo service template tối thiểu, 1.3 — Thiết lập database ownership, 1.4 — Cấu hình, secrets và security boundary, 1.5 — Reliability foundations, 1.6 — Logging và observability, 1.7 — CI và kiểm thử nền tảng, Bao gồm (+12 more)

### Community 25 - "org.springframework.web.bind.annotation.PostMapping"
Cohesion: 0.19
Nodes (6): OrganizationController, IdentityPrincipalResponse, IdentityPrincipalController, ReversalRequest, LedgerController, NotificationController

### Community 26 - "Feature Specification: Channels, Audit, Notification and Reporting"
Cohesion: 0.11
Nodes (18): Assumptions, Edge Cases, Feature Specification: Channels, Audit, Notification and Reporting, Functional Requirements, Key Entities, Measurable Outcomes, NEEDS CLARIFICATION, Requirements (+10 more)

### Community 28 - "Chi tiết từng phase"
Cohesion: 0.12
Nodes (17): Banking Clone — Lộ trình triển khai đề xuất, Chi tiết từng phase, Later — Loan, Ngoài phạm vi MVP, Nguyên tắc chia giai đoạn, Phase 0 — Hoàn tất thiết kế và quyết định MVP, Phase 1 — Nền tảng kỹ thuật, Phase 2 — Identity, Party, Corporate cơ bản và Branch (+9 more)

### Community 29 - "MembershipService"
Cohesion: 0.14
Nodes (9): MembershipService, MembershipNotFoundException, MembershipValidationException, MandateResponse, MembershipResponse, MembershipRepository, CreateMandateRequest, CreateMembershipRequest (+1 more)

### Community 30 - "Override"
Cohesion: 0.11
Nodes (10): ReportingAuthorizationService, ReportingService, ExportJob, ExportRequest, ReportRequest, ReportResponse, ReportingRepository, ReportingController (+2 more)

### Community 31 - "Rules"
Cohesion: 0.12
Nodes (16): 10. Performance conventions, 1. App Router by default, 2. Server-first rendering, 3. Type-safe domain models, 4. Clean component API design, 5. Backend contract separation, 6. Status-driven UI patterns, 7. Form design and interaction (+8 more)

### Community 32 - "corporate"
Cohesion: 0.05
Nodes (38): name, base-url, client-id, client-secret, token-uri, password, url, username (+30 more)

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

### Community 37 - "Bounded Contexts"
Cohesion: 0.14
Nodes (14): 10. Notification, 11. Audit, 12. Reporting, 1. Identity & Access, 2. Party / Customer, 3. Corporate, 4. Branch, 5. Account (+6 more)

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
Cohesion: 0.09
Nodes (14): Authorization, Build and tests, Corporate Service, Database, Party owner API, Scope, ApproverEligibilityClient, ApprovalRepository (+6 more)

### Community 42 - "Business Rules"
Cohesion: 0.17
Nodes (12): Account, Branch, Business Rules, Corporate Approval, Currency, Idempotency and audit, Identity, authorization and customer data, Interbank transfer — design constraints (+4 more)

### Community 43 - "Feature Specification: Provider-Neutral Interbank Transfer"
Cohesion: 0.12
Nodes (16): Assumptions, Constitution Alignment, Edge Cases, Feature Specification: Provider-Neutral Interbank Transfer, Functional Requirements, Key Entities, Measurable Outcomes, NEEDS CLARIFICATION (+8 more)

### Community 44 - "PROJECT_ROADMAP.md"
Cohesion: 0.09
Nodes (21): Acceptance, Critical dependencies and open decisions, Current evidence, Các phase, Dependencies, Mục tiêu dự án, Objective, Phase 0 (+13 more)

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
Cohesion: 0.15
Nodes (11): RetailTransferService, RetailTransferRecord, RetailTransferRequest, RetailTransferType, DEPOSIT, INTERNAL, WITHDRAWAL, LedgerExecutionClient (+3 more)

### Community 50 - "corporate-service/src/main/resources/application.yml"
Cohesion: 0.22
Nodes (8): corporate, console, logging, structured, server, port, shutdown, format

### Community 51 - "spring"
Cohesion: 0.22
Nodes (9): name, password, url, username, enabled, spring, application, datasource (+1 more)

### Community 52 - "Service Template"
Cohesion: 0.20
Nodes (9): Baseline, Build and run in local Kubernetes, Build and test, Configuration and secrets, Database ownership proof, Logging and local observability, Reliability foundation proof, Run locally (+1 more)

### Community 53 - ".submit"
Cohesion: 0.07
Nodes (16): PaymentService, PaymentOutcomeRequest, PaymentRecord, PaymentStatus, FAILED, PENDING_SUBMISSION, SETTLED, UNRESOLVED (+8 more)

### Community 54 - "ApprovalPolicyRepository.java"
Cohesion: 0.09
Nodes (15): ApprovalPolicyService, ApprovalPolicyConflictException, ApprovalPolicyNotFoundException, InvalidApprovalPolicyException, ApprovalPolicyLevel, ApprovalPolicyResponse, ApprovalPolicyStatus, ACTIVE (+7 more)

### Community 55 - "Phase 2 — Decision Gate"
Cohesion: 0.25
Nodes (8): API and event scope — exact contracts remain open, Approved authorization principal boundary, Approved first-slice domain contract, Confirmed constraints, Gate outcome, Phase 2 — Decision Gate, Purpose, Remaining OPEN implementation details

### Community 56 - "payment-service/src/main/resources/application.yml"
Cohesion: 0.06
Nodes (31): name, password, url, username, health, web, include, default-schema (+23 more)

### Community 57 - "audit-service/src/main/resources/application.yml"
Cohesion: 0.06
Nodes (31): name, audit, security, password, url, username, health, web (+23 more)

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
Cohesion: 0.29
Nodes (7): provider, registration, token-uri, client, keycloak, oauth2, security

### Community 66 - "health"
Cohesion: 0.29
Nodes (7): health, readiness, group, probes, show-details, enabled, include

### Community 67 - "Identity Service"
Cohesion: 0.29
Nodes (6): Build and tests, Configuration, Container image, Current scope, Database ownership, Identity Service

### Community 69 - "SocialPilot AI product flows"
Cohesion: 0.29
Nodes (6): 1. Create content flow, 2. Workflow tracking UX, 3. Content review UX, 4. Publishing UX, 5. Future dashboard UX, SocialPilot AI product flows

### Community 70 - "ADR-002: Retail + Corporate Banking"
Cohesion: 0.33
Nodes (6): ADR-002: Retail + Corporate Banking, Alternatives, Consequences, Context, Decision, Dependencies

### Community 71 - "ADR-003: Multi-channel Banking"
Cohesion: 0.33
Nodes (6): ADR-003: Multi-channel Banking, Alternatives, Consequences, Context, Decision, Dependencies

### Community 72 - "ADR-004: Shared PostgreSQL, Schema-per-service"
Cohesion: 0.33
Nodes (6): ADR-004: Shared PostgreSQL, Schema-per-service, Alternatives, Consequences, Context, Decision, Dependencies

### Community 73 - "JournalResponse"
Cohesion: 0.07
Nodes (17): LedgerService, InvalidJournalException, JournalAlreadyReversedException, JournalNotFoundException, UnbalancedJournalException, BalanceResponse, EntrySide, CREDIT (+9 more)

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

### Community 81 - "party-service"
Cohesion: 0.33
Nodes (6): authorization-grant-type, client-id, client-secret, provider, scope, party-service

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

### Community 112 - "Migration Summary Report"
Cohesion: 0.13
Nodes (13): Checkpoint Validation Summary, Consistency, Constitution Compliance, End-to-End Traceability Matrix, Findings, Metrics Summary, Migration Summary Report, Testing Evidence (+5 more)

### Community 113 - "uuid"
Cohesion: 0.08
Nodes (4): JdbcApproverEligibilityClient, PartyRepository, SubmitPaymentRequest, JdbcReportingRepository

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
Nodes (7): AccountServiceApplication, ApiDocsServiceApplication, CorporateServiceApplication, LedgerServiceApplication, NotificationServiceApplication, PartyServiceApplication, ReportingServiceApplication

### Community 122 - "channels-operations/checkpoints/tasks-to-impl.yaml"
Cohesion: 0.20
Nodes (9): blocked_tasks, completed_tasks, metadata, generated, source_plan, validation, blocking_issues, passed (+1 more)

### Community 123 - "spring"
Cohesion: 0.07
Nodes (26): name, password, url, username, health, web, include, default-schema (+18 more)

### Community 124 - "AccountResponse"
Cohesion: 0.17
Nodes (7): AccountService, AccountNotFoundException, DuplicateAccountException, InvalidAccountException, InvalidAccountTransitionException, AccountRepository, StateChangeRequest

### Community 125 - "AccountResponse"
Cohesion: 0.23
Nodes (6): AccountResponse, AccountStatus, ACTIVE, BLOCKED, CLOSED, JdbcAccountRepository

### Community 126 - "6. Implementation tasks"
Cohesion: 0.17
Nodes (12): 6. Implementation tasks, T001 — Add dependency management, T002 — Add per-service OpenAPI configuration, T003 — Add JWT bearer documentation, T004 — Create the docs service, T005 — Configure local and Kubernetes-local access, T006 — Add CORS policy, T007 — Add OpenAPI contract tests (+4 more)

### Community 127 - "spring"
Cohesion: 0.06
Nodes (30): account, security, name, password, url, username, health, web (+22 more)

### Community 129 - "spring"
Cohesion: 0.08
Nodes (25): name, password, url, username, health, web, include, default-schema (+17 more)

### Community 130 - "Payment Service"
Cohesion: 0.50
Nodes (3): Payment Service, Scope, Verification

### Community 131 - "3. Existing endpoint inventory"
Cohesion: 0.20
Nodes (10): 3. Existing endpoint inventory, Account, Audit, Branch, Corporate, Identity, Ledger, Party (+2 more)

### Community 135 - "reporting-service/src/main/resources/application.yml"
Cohesion: 0.06
Nodes (31): name, password, url, username, health, web, include, default-schema (+23 more)

### Community 136 - "channels-operations/checkpoints/spec-to-plan.yaml"
Cohesion: 0.11
Nodes (18): metadata, generated, source_spec, target_plan, missing_requirements, orphan_plan_items, requirements, plan_items (+10 more)

### Community 137 - "Phase 4 - Retail Internal Transfer va Teller"
Cohesion: 0.40
Nodes (4): Acceptance, API, Phase 4 - Retail Internal Transfer va Teller, Scope

### Community 139 - "IdentitySecurityProperties"
Cohesion: 0.21
Nodes (3): IdentitySecurityConfiguration, IdentitySecurityProperties, IdentitySecurityConfigurationTests

### Community 140 - "Implementation Plan: Channels, Audit, Notification and Reporting"
Cohesion: 0.12
Nodes (17): Applied Guidelines, Constitution Check, Deferred Decisions, Execution Sequence, Implementation Plan: Channels, Audit, Notification and Reporting, Implementation Steps, Project Structure, Requirement Mapping (+9 more)

### Community 141 - "services"
Cohesion: 0.05
Nodes (36): api-docs, services, name, health, web, include, readiness, group (+28 more)

### Community 142 - "org.springframework.boot.context.properties.ConfigurationProperties"
Cohesion: 0.11
Nodes (5): AuditSecurityProperties, CorporateSecurityProperties, IdentityDatabaseProperties, PaymentSecurityConfiguration, PaymentSecurityProperties

### Community 143 - "OrganizationEndpointIntegrationTests.java"
Cohesion: 0.09
Nodes (7): AccountEndpointIntegrationTests, AuditRepositoryIntegrationTests, LedgerEndpointIntegrationTests, NotificationRepositoryIntegrationTests, PartyEndpointIntegrationTests, PaymentRepositoryIntegrationTests, TransferRepositoryIntegrationTests

### Community 144 - "CURRENT_STATUS.md"
Cohesion: 0.25
Nodes (6): Blockers, Completed evidence, Current phase, Current task, Incomplete work, Working tree

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

### Community 157 - "AccountAuthorizationService"
Cohesion: 0.22
Nodes (3): AccountAuthorizationService, AccountController, AccountAuthorizationServiceTests

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

### Community 163 - "OidcPartyReferenceClientTests.java"
Cohesion: 0.12
Nodes (4): PartyType, INDIVIDUAL, ORGANIZATION, OidcPartyReferenceClientTests

### Community 165 - "summary"
Cohesion: 0.09
Nodes (22): item_id, tasks, metadata, generated, source_plan, target_tasks, missing_plan_items, orphan_tasks (+14 more)

### Community 166 - "Phases.md"
Cohesion: 0.17
Nodes (10): Acceptance, Decisions Still Open, Phase 8 - Production Readiness, Scope, Status, Feature Specification: Production Readiness, Functional Requirements, NEEDS CLARIFICATION (+2 more)

### Community 167 - "org.springframework.transaction.annotation.Transactional"
Cohesion: 0.08
Nodes (5): ApprovalRequestConflictException, ApprovalRequestNotFoundException, ApprovalRequestValidationException, IneligibleApproverException, MakerCheckerException

### Community 168 - "validation"
Cohesion: 0.18
Nodes (10): blocked_tasks, completed_tasks, metadata, generated, source_plan, validation, blocking_issues, passed (+2 more)

### Community 169 - "Centralized API Documentation Implementation Report"
Cohesion: 0.22
Nodes (8): Centralized API Documentation Implementation Report, Documentation URLs, Implemented components, Modified modules, Remaining limitations, Rollback, Security considerations, Tests executed

### Community 170 - "Implementation Plan: Production Readiness"
Cohesion: 0.12
Nodes (16): Applied Guidelines, Constitution Check, Deferred Decisions, Implementation Plan: Production Readiness, Implementation Steps, Project Structure, Requirement Mapping, Step 1: Release and CI gate (+8 more)

### Community 171 - "DatabaseConfigurationTests.java"
Cohesion: 0.16
Nodes (3): DatabaseConnectionProperties, ServiceTemplateApplication, DatabaseConfigurationTests

### Community 172 - "Phase 7 - Channels, Audit, Notification and Reporting"
Cohesion: 0.17
Nodes (10): Acceptance, API, Central API Documentation, Deferred Work, Existing Audit API, Phase 7 - Channels, Audit, Notification and Reporting, Planned Notification API, Planned Reporting API (+2 more)

### Community 173 - "channels-operations/checkpoints/batch-report.yaml"
Cohesion: 0.25
Nodes (7): batch_id, blocked_tasks, completed_tasks, protocol_violation, warnings, message, severity

### Community 174 - "production-readiness/checkpoints/spec-to-plan.yaml"
Cohesion: 0.10
Nodes (19): metadata, generated, source_spec, target_plan, missing_requirements, orphan_plan_items, items, requirements (+11 more)

### Community 177 - "IdentityPrincipalServiceTests.java"
Cohesion: 0.11
Nodes (13): IdentityPrincipalService, IdentityMapping, IdentityStatus, ACTIVE, DISABLED, ScopedGrant, ScopeType, BRANCH (+5 more)

### Community 179 - "io.swagger.v3.oas.annotations.security.SecurityScheme"
Cohesion: 0.23
Nodes (3): AuditServiceApplication, BranchServiceApplication, PaymentServiceApplication

### Community 181 - "ChannelContext"
Cohesion: 0.40
Nodes (4): ChannelContext, ADMIN, INTERNET_BANKING, TELLER

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

### Community 191 - "account_service"
Cohesion: 0.40
Nodes (5): command, failed, passed, skipped, account_service

### Community 192 - "notification_service"
Cohesion: 0.40
Nodes (5): command, failed, passed, skipped, notification_service

### Community 193 - "containers"
Cohesion: 0.06
Nodes (37): env, envFrom, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe (+29 more)

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

### Community 207 - "AuditRecord"
Cohesion: 0.07
Nodes (20): AuditAuthorizationService, AuditService, AuditRecord, AuditRecordRequest, AuditRepository, JdbcAuditRepository, AuditController, AuditServiceTests (+12 more)

### Community 208 - "AuditSecurityPropertiesTests.java"
Cohesion: 0.19
Nodes (3): AuditSecurityPropertiesTests, PaymentSecurityPropertiesTests, TransferSecurityPropertiesTests

### Community 209 - "data"
Cohesion: 0.09
Nodes (22): apiVersion, data, ACCOUNT_OPENAPI_URL, AUDIT_OPENAPI_URL, BRANCH_OPENAPI_URL, CORPORATE_APPROVAL_BASE_URL, CORPORATE_OPENAPI_URL, IDENTITY_OPENAPI_URL (+14 more)

### Community 211 - "TransferRecord"
Cohesion: 0.20
Nodes (4): ApprovalEnforcedTransferService, ExecutionAdapter, DeferredExecutionAdapter, TransferRecord

### Community 212 - "CorporateApprovalClient"
Cohesion: 0.22
Nodes (3): CorporateApprovalClient, CreateApprovalRequest, CorporateApprovalClientTests

### Community 214 - "TransferRepository"
Cohesion: 0.26
Nodes (3): SubmitTransferRequest, TransferRepository, IdempotencyRecord

### Community 215 - "PartyService"
Cohesion: 0.24
Nodes (7): PartyService, IdempotencyConflictException, IdempotencyInProgressException, IdempotencyRecord, InvalidPartyException, CreatePartyRequest, IndividualProfileRequest

### Community 220 - "kustomization.yaml"
Cohesion: 0.50
Nodes (3): apiVersion, kind, resources

### Community 222 - "production-readiness/checkpoints/batch-report.yaml"
Cohesion: 0.25
Nodes (7): batch_id, blocked_tasks, completed_tasks, protocol_violation, warnings, message, severity

### Community 225 - "PaymentSubmissionClient"
Cohesion: 0.28
Nodes (3): PaymentSubmissionResponse, PaymentSubmissionClient, SubmitPaymentRequest

### Community 226 - "api_docs_module"
Cohesion: 0.40
Nodes (5): command, failed, passed, skipped, api_docs_module

### Community 227 - "docker_images"
Cohesion: 0.40
Nodes (5): command, failed, passed, skipped, docker_images

### Community 228 - "focused_log_redaction"
Cohesion: 0.40
Nodes (5): command, failed, passed, skipped, focused_log_redaction

### Community 229 - "PartyType"
Cohesion: 0.25
Nodes (4): IndividualProfileResponse, PartyType, INDIVIDUAL, ORGANIZATION

### Community 230 - "test_results"
Cohesion: 0.15
Nodes (13): command, failed, passed, reason, skipped, status, test_results, kubernetes_manifests (+5 more)

### Community 231 - "workspace"
Cohesion: 0.40
Nodes (5): workspace, command, failed, passed, skipped

### Community 234 - "module_counts"
Cohesion: 0.14
Nodes (14): account-service, api-docs-service, audit-service, branch-service, corporate-service, identity-service, ledger-service, notification-service (+6 more)

### Community 235 - "production-readiness/checkpoints/tasks-to-impl.yaml"
Cohesion: 0.14
Nodes (12): Not verified or blocked, Phase 8 Smoke Test Verdict, Release decision, Verified, blocked_reasons, T023, blocked_tasks, completed_tasks (+4 more)

### Community 236 - "module_counts"
Cohesion: 0.14
Nodes (14): module_counts, account-service, api-docs-service, audit-service, branch-service, corporate-service, identity-service, ledger-service (+6 more)

### Community 237 - "API Documentation"
Cohesion: 0.29
Nodes (6): Add a microservice, API Documentation, Authenticate in Swagger UI, Kubernetes local, Local development, Troubleshooting

### Community 240 - "ledger_service"
Cohesion: 0.40
Nodes (5): command, failed, passed, skipped, ledger_service

### Community 241 - "evidence"
Cohesion: 0.40
Nodes (5): command, tests_failed, tests_passed, tests_skipped, evidence

### Community 242 - "workspace_smoke"
Cohesion: 0.50
Nodes (4): workspace_smoke, command, failed, passed

### Community 243 - "PartyStatus"
Cohesion: 0.50
Nodes (3): PartyStatus, ACTIVE, INACTIVE

### Community 244 - "Alert Response Runbook"
Cohesion: 0.33
Nodes (6): Alert Response Runbook, First response, Owner inputs required, Resolution and handoff, Response and recovery, Triage

### Community 245 - "Outbox Backlog, Retry, and Replay Runbook"
Cohesion: 0.33
Nodes (6): Assess a backlog, Controlled replay, Implementation prerequisites, Outbox Backlog, Retry, and Replay Runbook, Restore normal delivery, Scope and guardrails

### Community 246 - "Service Recovery Runbook"
Cohesion: 0.33
Nodes (6): Diagnose, Environment-specific details to approve, Recover, Safety checks, Service Recovery Runbook, Verify and hand off

### Community 247 - "Backup and Restore Rehearsal"
Cohesion: 0.40
Nodes (5): Backup and Restore Rehearsal, Before the rehearsal, Complete and clean up, Rehearse restore, Service-specific cautions

### Community 250 - "Disaster Recovery and Dependency-Failure Smoke Test Plan"
Cohesion: 0.50
Nodes (4): Disaster Recovery and Dependency-Failure Smoke Test Plan, Exit criteria, Scenarios, Test controls

### Community 251 - "Independent Service Rollback Rehearsal"
Cohesion: 0.50
Nodes (4): Evidence and cleanup, Independent Service Rollback Rehearsal, Preconditions, Rehearse

### Community 252 - "Phase 8 Security, Recovery, and Rollback Evidence"
Cohesion: 0.50
Nodes (4): Additional local evidence, Phase 8 Security, Recovery, and Rollback Evidence, Recovery and rollback, Security

## Knowledge Gaps
- **67 isolated node(s):** `ACTIVE`, `INACTIVE`, `INDIVIDUAL`, `ORGANIZATION`, `com.banking.platform:transfer-service` (+62 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 1854 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **54 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ApproverEligibilityClient` connect `ApprovalRepository` to `uuid`?**
  _High betweenness centrality (0.062) - this node is a cross-community bridge._
- **What connects `ACTIVE`, `INACTIVE`, `INDIVIDUAL` to the rest of the system?**
  _67 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `jakarta.servlet.http.HttpServletRequest` be split into smaller, more focused modules?**
  _Cohesion score 0.0537714712471994 - nodes in this community are weakly interconnected._
- **Should `org.springframework.stereotype.Component` be split into smaller, more focused modules?**
  _Cohesion score 0.09155844155844156 - nodes in this community are weakly interconnected._
- **Should `containers` be split into smaller, more focused modules?**
  _Cohesion score 0.04421768707482993 - nodes in this community are weakly interconnected._
- **Should `containers` be split into smaller, more focused modules?**
  _Cohesion score 0.045068027210884355 - nodes in this community are weakly interconnected._
- **Should `NotificationRecord` be split into smaller, more focused modules?**
  _Cohesion score 0.09285714285714286 - nodes in this community are weakly interconnected._