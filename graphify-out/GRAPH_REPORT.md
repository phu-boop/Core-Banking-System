# Graph Report - Core-Banking-System  (2026-11-21)

## Corpus Check
- 235 files · ~63,787 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 10 file(s) not represented in the graph (top: (none) 6, .example 1, .jar 1)

## Summary
- 2140 nodes · 4310 edges · 132 communities (101 shown, 31 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 159 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `54b0af14`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- jakarta.servlet.http.HttpServletRequest
- org.springframework.boot.autoconfigure.SpringBootApplication
- PartySecurityConfiguration.java
- org.springframework.stereotype.Component
- containers
- containers
- TransferRecord
- service-template/src/main/resources/application.yml
- socialpilot-ai-frontend
- identity-service/src/main/resources/application.yml
- party-service/src/main/resources/application.yml
- org.junit.jupiter.api.Test
- SocialPilot AI design system
- plan-to-tasks.yaml
- branch-service/src/main/resources/application.yml
- org.springframework.jdbc.core.JdbcTemplate
- BranchResponse
- spec-to-plan.yaml
- tasks-to-impl.yaml
- steps
- org.springframework.security.oauth2.jwt.Jwt
- OrganizationService
- Phase 1 — Technical Foundation
- OidcPartyReferenceClientTests.java
- ApprovalDecisionController.java
- architecture/BANKING_ARCHITECTURE.md
- Chi tiết từng phase
- MembershipService
- org.springframework.transaction.annotation.Transactional
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
- BranchEndpointIntegrationTests
- IdentityPrincipalServiceTests.java
- batch-report.yaml
- files_changed
- Implementation Plan: Corporate Approval Core
- org.springframework.security.oauth2.jwt.JwtDecoder
- corporate-service/src/main/resources/application.yml
- spring
- Service Template
- PaymentRepository
- ApprovalPolicyRepository.java
- Phase 2 — Decision Gate
- payment-service/src/main/resources/application.yml
- .recordOutcome
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
- ADR-007: Configurable Corporate Approval
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
- IdentitySecurityProperties
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
- Corporate Service
- .submit
- org.springframework.boot.context.properties.ConfigurationProperties
- DatabaseOwnershipIntegrationTests
- Migration Summary Report
- DatabaseConfigurationTests.java
- traceability
- Smoke Test Verdict
- BranchSecurityProperties
- Decisions
- PaymentStatus
- Requirements Quality Checklist
- PartySecurityProperties
- com.banking.platform:core-banking-system
- TransferSecurityProperties
- ApprovalRequestStatus
- CorporateSecurityProperties
- ADR-010: Phase 1 Technical Baseline
- NotFoundException
- Payment Service
- com.banking.platform:payment-service

## God Nodes (most connected - your core abstractions)
1. `OrganizationEndpointIntegrationTests` - 46 edges
2. `TransferRecord` - 27 edges
3. `ApprovalRepository` - 22 edges
4. `BranchEndpointIntegrationTests` - 21 edges
5. `PartyService` - 20 edges
6. `PaymentRepository` - 20 edges
7. `OrganizationAuthorizationService` - 20 edges
8. `IdentityPrincipalEndpointIntegrationTests` - 20 edges
9. `ApprovalPolicyService` - 19 edges
10. `ApprovalEnforcedTransferService` - 19 edges

## Surprising Connections (you probably didn't know these)
- `Step 6: Expose approval APIs and enforce command safety` --references--> `CorporateExceptionHandler`  [INFERRED]
  docs/plan/phase/corporate-approval/plan.md → services/corporate-service/src/main/java/com/banking/platform/corporate/web/CorporateExceptionHandler.java
- `Authorization` --references--> `ApproverEligibilityClient`  [INFERRED]
  services/corporate-service/README.md → services/corporate-service/src/main/java/com/banking/platform/corporate/application/ApproverEligibilityClient.java
- `PaymentRepositoryIntegrationTests` --references--> `PaymentRepository`  [EXTRACTED]
  services/payment-service/src/test/java/com/banking/platform/payment/persistence/PaymentRepositoryIntegrationTests.java → services/payment-service/src/main/java/com/banking/platform/payment/persistence/PaymentRepository.java
- `BranchServiceApplication` --references--> `BranchSecurityProperties`  [EXTRACTED]
  services/branch-service/src/main/java/com/banking/platform/branch/BranchServiceApplication.java → services/branch-service/src/main/java/com/banking/platform/branch/config/BranchSecurityProperties.java
- `ApprovalWorkflowService` --references--> `ApprovalRepository`  [EXTRACTED]
  services/corporate-service/src/main/java/com/banking/platform/corporate/application/ApprovalWorkflowService.java → services/corporate-service/src/main/java/com/banking/platform/corporate/persistence/ApprovalRepository.java

## Import Cycles
- None detected.

## Communities (132 total, 31 thin omitted)

### Community 0 - "jakarta.servlet.http.HttpServletRequest"
Cohesion: 0.07
Nodes (17): BranchNotFoundException, DuplicateBranchCodeException, InvalidBranchException, InvalidPageRequestException, BranchExceptionHandler, CorporateExceptionHandler, InvalidPartyException, PartyNotFoundException (+9 more)

### Community 1 - "org.springframework.boot.autoconfigure.SpringBootApplication"
Cohesion: 0.24
Nodes (4): BranchServiceApplication, CorporateServiceApplication, PaymentServiceApplication, ServiceTemplateApplication

### Community 2 - "PartySecurityConfiguration.java"
Cohesion: 0.07
Nodes (10): BranchSecurityConfiguration, CorporatePartyClientConfiguration, CorporateSecurityConfiguration, OidcPartyReferenceClient, IdentitySecurityConfiguration, IdentitySecurityProperties, IdentitySecurityConfigurationTests, PartySecurityConfiguration (+2 more)

### Community 3 - "org.springframework.stereotype.Component"
Cohesion: 0.07
Nodes (8): CorrelationIdFilter, CorrelationIdFilter, CorrelationIdFilter, CorrelationIdFilterTests, CorrelationIdFilter, CorrelationIdFilter, CorrelationIdFilterTests, CorrelationIdFilter

### Community 4 - "containers"
Cohesion: 0.04
Nodes (48): env, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe, securityContext (+40 more)

### Community 5 - "containers"
Cohesion: 0.05
Nodes (48): env, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe, startupProbe (+40 more)

### Community 6 - "TransferRecord"
Cohesion: 0.05
Nodes (25): ApprovalEnforcedTransferService, ExecutionAdapter, DeferredExecutionAdapter, CorporateApprovalResponse, PaymentSubmissionResponse, SubmitTransferRequest, TransferRecord, TransferStatus (+17 more)

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

### Community 12 - "org.junit.jupiter.api.Test"
Cohesion: 0.13
Nodes (5): PartyReferenceResponse, PartyType, INDIVIDUAL, ORGANIZATION, OrganizationEndpointIntegrationTests

### Community 13 - "SocialPilot AI design system"
Cohesion: 0.05
Nodes (35): Accessibility, Buttons, Card styling, Color system, Component patterns, Core palette, Design intent, Inputs and form fields (+27 more)

### Community 14 - "plan-to-tasks.yaml"
Cohesion: 0.06
Nodes (35): description, item_id, notes, tasks, metadata, generated, source_plan, target_tasks (+27 more)

### Community 15 - "branch-service/src/main/resources/application.yml"
Cohesion: 0.06
Nodes (34): name, branch, security, password, url, username, health, web (+26 more)

### Community 18 - "BranchResponse"
Cohesion: 0.10
Nodes (10): BranchAuthorizationService, BranchService, BranchPage, BranchResponse, BranchStatus, ACTIVE, INACTIVE, BranchRepository (+2 more)

### Community 19 - "spec-to-plan.yaml"
Cohesion: 0.09
Nodes (22): metadata, generated, source_spec, target_plan, missing_requirements, orphan_plan_items, items, phase (+14 more)

### Community 20 - "tasks-to-impl.yaml"
Cohesion: 0.07
Nodes (26): metadata, generated, last_updated, source_tasks, pending_tasks, summary, completed_tasks, completion_percentage (+18 more)

### Community 21 - "steps"
Cohesion: 0.11
Nodes (21): runs-on, steps, jobs, identity-service, service-template, name, on, pull_request (+13 more)

### Community 22 - "org.springframework.security.oauth2.jwt.Jwt"
Cohesion: 0.13
Nodes (11): ApprovalWorkflowService, OrganizationAuthorizationService, ApprovalDecision, APPROVE, REJECT, ApprovalDecisionResponse, ApprovalHistoryResponse, ApprovalRequestResponse (+3 more)

### Community 23 - "OrganizationService"
Cohesion: 0.11
Nodes (12): OrganizationService, DuplicateOrganizationPartyException, InvalidOrganizationException, OrganizationNotFoundException, OrganizationResponse, OrganizationStatus, ACTIVE, INACTIVE (+4 more)

### Community 24 - "Phase 1 — Technical Foundation"
Cohesion: 0.10
Nodes (20): 1.1 — Chốt technical baseline, 1.2 — Tạo service template tối thiểu, 1.3 — Thiết lập database ownership, 1.4 — Cấu hình, secrets và security boundary, 1.5 — Reliability foundations, 1.6 — Logging và observability, 1.7 — CI và kiểm thử nền tảng, Bao gồm (+12 more)

### Community 28 - "Chi tiết từng phase"
Cohesion: 0.12
Nodes (17): Banking Clone — Lộ trình triển khai đề xuất, Chi tiết từng phase, Later — Loan, Ngoài phạm vi MVP, Nguyên tắc chia giai đoạn, Phase 0 — Hoàn tất thiết kế và quyết định MVP, Phase 1 — Nền tảng kỹ thuật, Phase 2 — Identity, Party, Corporate cơ bản và Branch (+9 more)

### Community 29 - "MembershipService"
Cohesion: 0.14
Nodes (9): MembershipService, MembershipNotFoundException, MembershipValidationException, MandateResponse, MembershipResponse, MembershipRepository, CreateMandateRequest, CreateMembershipRequest (+1 more)

### Community 30 - "org.springframework.transaction.annotation.Transactional"
Cohesion: 0.10
Nodes (5): ApprovalRequestConflictException, ApprovalRequestNotFoundException, ApprovalRequestValidationException, IneligibleApproverException, MakerCheckerException

### Community 31 - "Rules"
Cohesion: 0.12
Nodes (16): 10. Performance conventions, 1. App Router by default, 2. Server-first rendering, 3. Type-safe domain models, 4. Clean component API design, 5. Backend contract separation, 6. Status-driven UI patterns, 7. Form design and interaction (+8 more)

### Community 32 - "corporate"
Cohesion: 0.07
Nodes (28): name, base-url, client-id, client-secret, token-uri, password, url, username (+20 more)

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
Cohesion: 0.17
Nodes (7): ApprovalRepository, IdempotencyRow, PolicyLevelRow, PolicyRow, RequestLockRow, RequestRow, StepRow

### Community 42 - "Business Rules"
Cohesion: 0.17
Nodes (12): Account, Branch, Business Rules, Corporate Approval, Currency, Idempotency and audit, Identity, authorization and customer data, Interbank transfer — design constraints (+4 more)

### Community 43 - "Feature Specification: Provider-Neutral Interbank Transfer"
Cohesion: 0.12
Nodes (16): Assumptions, Constitution Alignment, Edge Cases, Feature Specification: Provider-Neutral Interbank Transfer, Functional Requirements, Key Entities, Measurable Outcomes, NEEDS CLARIFICATION (+8 more)

### Community 45 - "IdentityPrincipalServiceTests.java"
Cohesion: 0.11
Nodes (15): IdentityPrincipalService, IdentityMapping, IdentityPrincipalResponse, IdentityStatus, ACTIVE, DISABLED, ScopedGrant, ScopeType (+7 more)

### Community 46 - "batch-report.yaml"
Cohesion: 0.18
Nodes (10): batch_id, blocked_tasks, completed_tasks, errors, protocol_violation, artifact, used_for, upstream_artifacts_consumed (+2 more)

### Community 47 - "files_changed"
Cohesion: 0.06
Nodes (34): batch_id, blocked_tasks, completed_tasks, files_changed, T004, T005, T006, T007 (+26 more)

### Community 48 - "Implementation Plan: Corporate Approval Core"
Cohesion: 0.11
Nodes (18): Applied Guidelines, Complexity Tracking, Constitution Check, Deferred: Transfer enforcement and financial execution, Implementation Plan: Corporate Approval Core, Implementation Steps, Project Structure, Requirement Mapping (+10 more)

### Community 49 - "org.springframework.security.oauth2.jwt.JwtDecoder"
Cohesion: 0.17
Nodes (3): PartyEndpointIntegrationTests, PaymentRepositoryIntegrationTests, TransferRepositoryIntegrationTests

### Community 50 - "corporate-service/src/main/resources/application.yml"
Cohesion: 0.22
Nodes (8): corporate, console, logging, structured, server, port, shutdown, format

### Community 51 - "spring"
Cohesion: 0.22
Nodes (9): name, password, url, username, enabled, spring, application, datasource (+1 more)

### Community 52 - "Service Template"
Cohesion: 0.22
Nodes (9): Baseline, Build and run in local Kubernetes, Build and test, Configuration and secrets, Database ownership proof, Logging and local observability, Reliability foundation proof, Run locally (+1 more)

### Community 53 - "PaymentRepository"
Cohesion: 0.21
Nodes (3): PaymentRecord, PaymentRepository, IdempotencyRecord

### Community 54 - "ApprovalPolicyRepository.java"
Cohesion: 0.08
Nodes (15): ApprovalPolicyService, ApprovalPolicyConflictException, ApprovalPolicyNotFoundException, InvalidApprovalPolicyException, ApprovalPolicyLevel, ApprovalPolicyResponse, ApprovalPolicyStatus, ACTIVE (+7 more)

### Community 55 - "Phase 2 — Decision Gate"
Cohesion: 0.25
Nodes (8): API and event scope — exact contracts remain open, Approved authorization principal boundary, Approved first-slice domain contract, Confirmed constraints, Gate outcome, Phase 2 — Decision Gate, Purpose, Remaining OPEN implementation details

### Community 56 - "payment-service/src/main/resources/application.yml"
Cohesion: 0.09
Nodes (21): name, password, url, username, web, include, enabled, management (+13 more)

### Community 57 - ".recordOutcome"
Cohesion: 0.23
Nodes (3): PaymentOutcomeRequest, OutcomeSource, PaymentServiceTests

### Community 58 - "security"
Cohesion: 0.25
Nodes (8): security, audience, issuer-uri, jwk-set-uri, party-client-id, party-client-secret, party-service-base-url, token-uri

### Community 59 - "ADR-001: Microservices Architecture"
Cohesion: 0.29
Nodes (6): ADR-001: Microservices Architecture, Alternatives, Consequences, Context, Decision, Dependencies

### Community 60 - "ADR-005: VND Only"
Cohesion: 0.29
Nodes (6): ADR-005: VND Only, Alternatives, Consequences, Context, Decision, Dependencies

### Community 61 - "ADR-006: Internal + Interbank Transfer"
Cohesion: 0.33
Nodes (6): ADR-006: Internal + Interbank Transfer, Alternatives, Consequences, Context, Decision, Dependencies

### Community 62 - "ADR-008: Loan Excluded from MVP"
Cohesion: 0.29
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

### Community 73 - "ADR-007: Configurable Corporate Approval"
Cohesion: 0.33
Nodes (6): ADR-007: Configurable Corporate Approval, Alternatives, Consequences, Context, Decision, Dependencies

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
Nodes (17): PartyService, IdempotencyConflictException, IdempotencyInProgressException, IdempotencyRecord, IndividualProfileResponse, PartyResponse, PartyStatus, ACTIVE (+9 more)

### Community 103 - "Transfer Service"
Cohesion: 0.40
Nodes (4): Run, Scope, Transfer Service, Verification

### Community 105 - "validation"
Cohesion: 0.17
Nodes (11): mapping, plan_item, tasks, metadata, generated, source_plan, validation, passed (+3 more)

### Community 106 - "metadata"
Cohesion: 0.17
Nodes (11): mapping, plan_items, requirement, metadata, feature, generated, source_spec, validation (+3 more)

### Community 107 - "Corporate Service"
Cohesion: 0.20
Nodes (7): Authorization, Build and tests, Corporate Service, Database, Party owner API, Scope, ApproverEligibilityClient

### Community 108 - ".submit"
Cohesion: 0.42
Nodes (3): PaymentService, SubmitPaymentRequest, PaymentController

### Community 111 - "DatabaseOwnershipIntegrationTests"
Cohesion: 0.13
Nodes (3): IdentityPrincipalEndpointIntegrationTests, DatabaseOwnershipIntegrationTests, ServiceTemplateApplicationTests

### Community 112 - "Migration Summary Report"
Cohesion: 0.13
Nodes (13): Checkpoint Validation Summary, Consistency, Constitution Compliance, End-to-End Traceability Matrix, Findings, Metrics Summary, Migration Summary Report, Testing Evidence (+5 more)

### Community 114 - "traceability"
Cohesion: 0.29
Nodes (6): traceability, files, plan_items, requirement, status, tasks

### Community 115 - "Smoke Test Verdict"
Cohesion: 0.33
Nodes (5): Command, Coverage warning, Deferred scope, Evidence, Smoke Test Verdict

### Community 117 - "Decisions"
Cohesion: 0.33
Nodes (5): Decisions, Interbank Transfer Research, Outcome safety, Scope decision, Unresolved Clarifications

### Community 118 - "PaymentStatus"
Cohesion: 0.33
Nodes (5): PaymentStatus, FAILED, PENDING_SUBMISSION, SETTLED, UNRESOLVED

### Community 119 - "Requirements Quality Checklist"
Cohesion: 0.33
Nodes (5): Completeness, Constitution Alignment, Requirement ID Coverage, Requirements Quality Checklist, Testability

### Community 123 - "ApprovalRequestStatus"
Cohesion: 0.40
Nodes (4): ApprovalRequestStatus, APPROVED, PENDING, REJECTED

### Community 125 - "ADR-010: Phase 1 Technical Baseline"
Cohesion: 0.18
Nodes (9): Banking Clone — Architecture Document, Hoàn tất tài liệu thiết kế, Khảo sát và kiểm tra, ADR-010: Phase 1 Technical Baseline, Alternatives, Consequences, Context, Decision (+1 more)

### Community 130 - "Payment Service"
Cohesion: 0.50
Nodes (3): Payment Service, Scope, Verification

## Knowledge Gaps
- **43 isolated node(s):** `ACTIVE`, `INACTIVE`, `INDIVIDUAL`, `ORGANIZATION`, `com.banking.platform:transfer-service` (+38 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 904 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **31 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CorporateExceptionHandler` connect `jakarta.servlet.http.HttpServletRequest` to `Implementation Plan: Corporate Approval Core`?**
  _High betweenness centrality (0.098) - this node is a cross-community bridge._
- **What connects `ACTIVE`, `INACTIVE`, `INDIVIDUAL` to the rest of the system?**
  _43 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `jakarta.servlet.http.HttpServletRequest` be split into smaller, more focused modules?**
  _Cohesion score 0.07317073170731707 - nodes in this community are weakly interconnected._
- **Why does `Step 6: Expose approval APIs and enforce command safety` connect `Implementation Plan: Corporate Approval Core` to `jakarta.servlet.http.HttpServletRequest`?**
  _High betweenness centrality (0.098) - this node is a cross-community bridge._
- **Should `PartySecurityConfiguration.java` be split into smaller, more focused modules?**
  _Cohesion score 0.07364185110663984 - nodes in this community are weakly interconnected._
- **Should `org.springframework.stereotype.Component` be split into smaller, more focused modules?**
  _Cohesion score 0.0744047619047619 - nodes in this community are weakly interconnected._
- **Should `containers` be split into smaller, more focused modules?**
  _Cohesion score 0.04421768707482993 - nodes in this community are weakly interconnected._