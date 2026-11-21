# Graph Report - Core-Banking-System  (2026-11-22)

## Corpus Check
- 281 files · ~69,083 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 10 file(s) not represented in the graph (top: (none) 6, .example 1, .jar 1)

## Summary
- 2435 nodes · 5193 edges · 152 communities (115 shown, 37 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 210 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `1bf15fa6`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- jakarta.servlet.http.HttpServletRequest
- org.springframework.boot.autoconfigure.SpringBootApplication
- PartySecurityConfiguration.java
- org.springframework.stereotype.Component
- containers
- containers
- TransferRepository
- service-template/src/main/resources/application.yml
- socialpilot-ai-frontend
- identity-service/src/main/resources/application.yml
- party-service/src/main/resources/application.yml
- org.junit.jupiter.api.Test
- SocialPilot AI design system
- plan-to-tasks.yaml
- branch-service/src/main/resources/application.yml
- RetailTransferServiceTests.java
- BranchResponse
- spec-to-plan.yaml
- tasks-to-impl.yaml
- steps
- org.springframework.security.oauth2.jwt.Jwt
- OrganizationService
- Phase 1 — Technical Foundation
- OidcPartyReferenceClientTests.java
- CorporateApprovalClient.java
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
- JdbcIdentityMappingRepository.java
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
- jakarta.servlet.http.HttpServletRequest
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
- list
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
- uuid
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
- ApprovalEnforcedTransferServiceTests
- TransferExceptionHandler.java
- uuid
- BranchExceptionHandler.java
- IdentityPrincipalEndpointIntegrationTests
- Migration Summary Report
- org.springframework.http.ResponseEntity
- traceability
- Smoke Test Verdict
- Decisions
- PartyEndpointIntegrationTests
- Requirements Quality Checklist
- TransferStatus
- com.banking.platform:core-banking-system
- org.junit.jupiter.api.Test
- ApprovalEnforcedTransferServiceTests.java
- AccountResponse
- ADR-010: Phase 1 Technical Baseline
- org.springframework.web.bind.annotation.RestControllerAdvice
- spring
- spring
- Payment Service
- TransferRecord
- com.banking.platform:payment-service
- map
- PartyReferenceResponse
- Phase 4 - Retail Internal Transfer va Teller
- OidcPartyReferenceClient.java
- IdentitySecurityProperties
- PartySecurityProperties
- DatabaseConfigurationTests.java
- org.springframework.boot.context.properties.ConfigurationProperties
- AccountExceptionHandler
- OrganizationStatus
- CorporatePartyClientConfiguration.java
- PartyStatus
- ADR-007: Configurable Corporate Approval
- Phase 3 - Account va Ledger
- org.springframework.security.oauth2.jwt.Jwt
- com.banking.platform:account-service
- com.banking.platform:ledger-service

## God Nodes (most connected - your core abstractions)
1. `OrganizationEndpointIntegrationTests` - 46 edges
2. `TransferStatus` - 28 edges
3. `JournalResponse` - 27 edges
4. `AccountResponse` - 23 edges
5. `ApprovalRepository` - 22 edges
6. `BranchEndpointIntegrationTests` - 21 edges
7. `PartyService` - 20 edges
8. `PaymentRepository` - 20 edges
9. `TransferRecord` - 20 edges
10. `OrganizationAuthorizationService` - 20 edges

## Surprising Connections (you probably didn't know these)
- `Step 6: Expose approval APIs and enforce command safety` --references--> `CorporateExceptionHandler`  [INFERRED]
  docs/plan/phase/corporate-approval/plan.md → services/corporate-service/src/main/java/com/banking/platform/corporate/web/CorporateExceptionHandler.java
- `Authorization` --references--> `ApproverEligibilityClient`  [INFERRED]
  services/corporate-service/README.md → services/corporate-service/src/main/java/com/banking/platform/corporate/application/ApproverEligibilityClient.java
- `BranchController` --references--> `BranchService`  [EXTRACTED]
  services/branch-service/src/main/java/com/banking/platform/branch/web/BranchController.java → services/branch-service/src/main/java/com/banking/platform/branch/application/BranchService.java
- `ApprovalPolicyController` --references--> `ApprovalPolicyService`  [EXTRACTED]
  services/corporate-service/src/main/java/com/banking/platform/corporate/web/ApprovalPolicyController.java → services/corporate-service/src/main/java/com/banking/platform/corporate/application/ApprovalPolicyService.java
- `ApprovalWorkflowService` --references--> `ApprovalRepository`  [EXTRACTED]
  services/corporate-service/src/main/java/com/banking/platform/corporate/application/ApprovalWorkflowService.java → services/corporate-service/src/main/java/com/banking/platform/corporate/persistence/ApprovalRepository.java

## Import Cycles
- None detected.

## Communities (152 total, 37 thin omitted)

### Community 1 - "org.springframework.boot.autoconfigure.SpringBootApplication"
Cohesion: 0.08
Nodes (10): BranchServiceApplication, BranchSecurityProperties, CorporateServiceApplication, IdentityDatabaseProperties, IdentityServiceApplication, LedgerServiceApplication, PaymentServiceApplication, HttpLedgerExecutionClient (+2 more)

### Community 2 - "PartySecurityConfiguration.java"
Cohesion: 0.17
Nodes (5): BranchSecurityConfiguration, CorporateSecurityConfiguration, PartySecurityConfiguration, PaymentSecurityConfiguration, TransferSecurityConfiguration

### Community 3 - "org.springframework.stereotype.Component"
Cohesion: 0.09
Nodes (8): CorrelationIdFilter, CorrelationIdFilter, CorrelationIdFilter, CorrelationIdFilterTests, CorrelationIdFilter, CorrelationIdFilter, CorrelationIdFilterTests, CorrelationIdFilter

### Community 4 - "containers"
Cohesion: 0.04
Nodes (48): env, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe, securityContext (+40 more)

### Community 5 - "containers"
Cohesion: 0.05
Nodes (48): env, image, imagePullPolicy, livenessProbe, name, ports, readinessProbe, startupProbe (+40 more)

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

### Community 17 - "RetailTransferServiceTests.java"
Cohesion: 0.12
Nodes (10): RetailTransferRecord, RetailTransferRequest, RetailTransferType, DEPOSIT, INTERNAL, WITHDRAWAL, LedgerExecutionClient, RetailTransferRepository (+2 more)

### Community 18 - "BranchResponse"
Cohesion: 0.09
Nodes (13): BranchAuthorizationService, BranchService, BranchNotFoundException, DuplicateBranchCodeException, InvalidBranchException, InvalidPageRequestException, BranchPage, BranchResponse (+5 more)

### Community 19 - "spec-to-plan.yaml"
Cohesion: 0.09
Nodes (22): metadata, generated, source_spec, target_plan, missing_requirements, orphan_plan_items, items, phase (+14 more)

### Community 20 - "tasks-to-impl.yaml"
Cohesion: 0.07
Nodes (26): metadata, generated, last_updated, source_tasks, pending_tasks, summary, completed_tasks, completion_percentage (+18 more)

### Community 21 - "steps"
Cohesion: 0.08
Nodes (42): runs-on, steps, runs-on, steps, runs-on, steps, runs-on, steps (+34 more)

### Community 22 - "org.springframework.security.oauth2.jwt.Jwt"
Cohesion: 0.12
Nodes (14): ApprovalWorkflowService, ApprovalDecision, APPROVE, REJECT, ApprovalDecisionResponse, ApprovalHistoryResponse, ApprovalRequestResponse, ApprovalRequestStatus (+6 more)

### Community 23 - "OrganizationService"
Cohesion: 0.16
Nodes (9): OrganizationService, DuplicateOrganizationPartyException, InvalidOrganizationException, OrganizationResponse, PartyServiceUnavailableException, PartyReferenceClient, OrganizationRepository, CreateOrganizationRequest (+1 more)

### Community 24 - "Phase 1 — Technical Foundation"
Cohesion: 0.10
Nodes (20): 1.1 — Chốt technical baseline, 1.2 — Tạo service template tối thiểu, 1.3 — Thiết lập database ownership, 1.4 — Cấu hình, secrets và security boundary, 1.5 — Reliability foundations, 1.6 — Logging và observability, 1.7 — CI và kiểm thử nền tảng, Bao gồm (+12 more)

### Community 25 - "OidcPartyReferenceClientTests.java"
Cohesion: 0.12
Nodes (4): PartyType, INDIVIDUAL, ORGANIZATION, OidcPartyReferenceClientTests

### Community 26 - "CorporateApprovalClient.java"
Cohesion: 0.21
Nodes (3): CorporateApprovalClient, CreateApprovalRequest, CorporateApprovalClientTests

### Community 28 - "Chi tiết từng phase"
Cohesion: 0.12
Nodes (17): Banking Clone — Lộ trình triển khai đề xuất, Chi tiết từng phase, Later — Loan, Ngoài phạm vi MVP, Nguyên tắc chia giai đoạn, Phase 0 — Hoàn tất thiết kế và quyết định MVP, Phase 1 — Nền tảng kỹ thuật, Phase 2 — Identity, Party, Corporate cơ bản và Branch (+9 more)

### Community 29 - "MembershipService"
Cohesion: 0.13
Nodes (9): MembershipService, MembershipNotFoundException, MembershipValidationException, MandateResponse, MembershipResponse, MembershipRepository, CreateMandateRequest, CreateMembershipRequest (+1 more)

### Community 30 - "org.springframework.transaction.annotation.Transactional"
Cohesion: 0.11
Nodes (4): ApprovalRequestConflictException, ApprovalRequestValidationException, IneligibleApproverException, MakerCheckerException

### Community 31 - "Rules"
Cohesion: 0.12
Nodes (16): 10. Performance conventions, 1. App Router by default, 2. Server-first rendering, 3. Type-safe domain models, 4. Clean component API design, 5. Backend contract separation, 6. Status-driven UI patterns, 7. Form design and interaction (+8 more)

### Community 32 - "corporate"
Cohesion: 0.06
Nodes (30): name, base-url, client-id, client-secret, token-uri, password, url, username (+22 more)

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

### Community 45 - "JdbcIdentityMappingRepository.java"
Cohesion: 0.09
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
Cohesion: 0.18
Nodes (5): AccountServiceApplication, AccountEndpointIntegrationTests, LedgerEndpointIntegrationTests, PaymentRepositoryIntegrationTests, TransferRepositoryIntegrationTests

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
Cohesion: 0.09
Nodes (13): PaymentService, PaymentOutcomeRequest, PaymentRecord, PaymentStatus, FAILED, PENDING_SUBMISSION, SETTLED, UNRESOLVED (+5 more)

### Community 54 - "ApprovalPolicyRepository.java"
Cohesion: 0.08
Nodes (14): ApprovalPolicyService, ApprovalPolicyConflictException, ApprovalPolicyNotFoundException, InvalidApprovalPolicyException, OrganizationNotFoundException, ApprovalPolicyLevel, ApprovalPolicyResponse, ApprovalPolicyStatus (+6 more)

### Community 55 - "Phase 2 — Decision Gate"
Cohesion: 0.25
Nodes (8): API and event scope — exact contracts remain open, Approved authorization principal boundary, Approved first-slice domain contract, Confirmed constraints, Gate outcome, Phase 2 — Decision Gate, Purpose, Remaining OPEN implementation details

### Community 56 - "payment-service/src/main/resources/application.yml"
Cohesion: 0.09
Nodes (21): name, password, url, username, web, include, enabled, management (+13 more)

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

### Community 73 - "list"
Cohesion: 0.08
Nodes (19): LedgerService, InvalidJournalException, JournalAlreadyReversedException, JournalNotFoundException, UnbalancedJournalException, BalanceResponse, EntrySide, CREDIT (+11 more)

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
Cohesion: 0.12
Nodes (10): PartyService, IdempotencyConflictException, IdempotencyInProgressException, IdempotencyRecord, PartyResponse, PartyRepository, CreatePartyRequest, IndividualProfileRequest (+2 more)

### Community 103 - "Transfer Service"
Cohesion: 0.40
Nodes (4): Run, Scope, Transfer Service, Verification

### Community 105 - "validation"
Cohesion: 0.17
Nodes (11): mapping, plan_item, tasks, metadata, generated, source_plan, validation, passed (+3 more)

### Community 106 - "metadata"
Cohesion: 0.17
Nodes (11): mapping, plan_items, requirement, metadata, feature, generated, source_spec, validation (+3 more)

### Community 107 - "ApprovalEnforcedTransferServiceTests"
Cohesion: 0.30
Nodes (4): CorporateApprovalResponse, SubmitTransferRequest, IdempotencyRecord, ApprovalEnforcedTransferServiceTests

### Community 108 - "TransferExceptionHandler.java"
Cohesion: 0.10
Nodes (6): TransferExceptions, ConflictException, NotFoundException, UnsupportedException, ValidationException, TransferExceptionHandler

### Community 109 - "uuid"
Cohesion: 0.15
Nodes (5): ApprovalRequestNotFoundException, IndividualProfileResponse, PartyType, INDIVIDUAL, ORGANIZATION

### Community 112 - "Migration Summary Report"
Cohesion: 0.13
Nodes (13): Checkpoint Validation Summary, Consistency, Constitution Compliance, End-to-End Traceability Matrix, Findings, Metrics Summary, Migration Summary Report, Testing Evidence (+5 more)

### Community 113 - "org.springframework.http.ResponseEntity"
Cohesion: 0.33
Nodes (4): InvalidPartyException, PartyNotFoundException, StalePartyVersionException, PartyExceptionHandler

### Community 114 - "traceability"
Cohesion: 0.29
Nodes (6): traceability, files, plan_items, requirement, status, tasks

### Community 115 - "Smoke Test Verdict"
Cohesion: 0.33
Nodes (5): Command, Coverage warning, Deferred scope, Evidence, Smoke Test Verdict

### Community 117 - "Decisions"
Cohesion: 0.33
Nodes (5): Decisions, Interbank Transfer Research, Outcome safety, Scope decision, Unresolved Clarifications

### Community 119 - "Requirements Quality Checklist"
Cohesion: 0.33
Nodes (5): Completeness, Constitution Alignment, Requirement ID Coverage, Requirements Quality Checklist, Testability

### Community 120 - "TransferStatus"
Cohesion: 0.14
Nodes (13): TransferStatus, ACCEPTED, APPROVED, EXECUTION_REQUESTED, FAILED, INTERBANK_FAILED, INTERBANK_SETTLED, PENDING_APPROVAL (+5 more)

### Community 122 - "org.junit.jupiter.api.Test"
Cohesion: 0.14
Nodes (3): IdentitySecurityConfigurationTests, DatabaseOwnershipIntegrationTests, ServiceTemplateApplicationTests

### Community 123 - "ApprovalEnforcedTransferServiceTests.java"
Cohesion: 0.19
Nodes (3): PaymentSubmissionResponse, PaymentSubmissionClient, SubmitPaymentRequest

### Community 124 - "AccountResponse"
Cohesion: 0.09
Nodes (17): AccountService, AccountNotFoundException, DuplicateAccountException, InvalidAccountException, InvalidAccountTransitionException, AccountResponse, AccountStatus, ACTIVE (+9 more)

### Community 125 - "ADR-010: Phase 1 Technical Baseline"
Cohesion: 0.18
Nodes (9): Banking Clone — Architecture Document, Hoàn tất tài liệu thiết kế, Khảo sát và kiểm tra, ADR-010: Phase 1 Technical Baseline, Alternatives, Consequences, Context, Decision (+1 more)

### Community 126 - "org.springframework.web.bind.annotation.RestControllerAdvice"
Cohesion: 0.21
Nodes (4): PaymentExceptions, ConflictException, NotFoundException, PaymentExceptionHandler

### Community 127 - "spring"
Cohesion: 0.11
Nodes (17): name, password, url, username, web, include, default-schema, schemas (+9 more)

### Community 129 - "spring"
Cohesion: 0.11
Nodes (17): name, password, url, username, web, include, default-schema, schemas (+9 more)

### Community 130 - "Payment Service"
Cohesion: 0.50
Nodes (3): Payment Service, Scope, Verification

### Community 131 - "TransferRecord"
Cohesion: 0.19
Nodes (3): ExecutionAdapter, DeferredExecutionAdapter, TransferRecord

### Community 137 - "Phase 4 - Retail Internal Transfer va Teller"
Cohesion: 0.40
Nodes (4): Acceptance, API, Phase 4 - Retail Internal Transfer va Teller, Scope

### Community 141 - "DatabaseConfigurationTests.java"
Cohesion: 0.15
Nodes (3): DatabaseConnectionProperties, ServiceTemplateApplication, DatabaseConfigurationTests

### Community 142 - "org.springframework.boot.context.properties.ConfigurationProperties"
Cohesion: 0.17
Nodes (3): CorporateSecurityProperties, PaymentSecurityProperties, TransferSecurityProperties

### Community 144 - "OrganizationStatus"
Cohesion: 0.50
Nodes (3): OrganizationStatus, ACTIVE, INACTIVE

### Community 146 - "PartyStatus"
Cohesion: 0.50
Nodes (3): PartyStatus, ACTIVE, INACTIVE

### Community 150 - "ADR-007: Configurable Corporate Approval"
Cohesion: 0.33
Nodes (6): ADR-007: Configurable Corporate Approval, Alternatives, Consequences, Context, Decision, Dependencies

### Community 151 - "Phase 3 - Account va Ledger"
Cohesion: 0.40
Nodes (4): Acceptance, API, Pham vi, Phase 3 - Account va Ledger

### Community 152 - "org.springframework.security.oauth2.jwt.Jwt"
Cohesion: 0.19
Nodes (3): OrganizationAuthorizationService, ApprovalPolicyController, CreateApprovalPolicyRequest

## Knowledge Gaps
- **57 isolated node(s):** `com.banking.platform:transfer-service`, `INDIVIDUAL`, `ORGANIZATION`, `ACCEPTED`, `APPROVED` (+52 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 961 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **37 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CorporateExceptionHandler` connect `jakarta.servlet.http.HttpServletRequest` to `Implementation Plan: Corporate Approval Core`, `map`?**
  _High betweenness centrality (0.064) - this node is a cross-community bridge._
- **What connects `com.banking.platform:transfer-service`, `INDIVIDUAL`, `ORGANIZATION` to the rest of the system?**
  _57 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `org.springframework.boot.autoconfigure.SpringBootApplication` be split into smaller, more focused modules?**
  _Cohesion score 0.08048780487804878 - nodes in this community are weakly interconnected._
- **Why does `Step 6: Expose approval APIs and enforce command safety` connect `Implementation Plan: Corporate Approval Core` to `jakarta.servlet.http.HttpServletRequest`?**
  _High betweenness centrality (0.064) - this node is a cross-community bridge._
- **Should `org.springframework.stereotype.Component` be split into smaller, more focused modules?**
  _Cohesion score 0.08590441621294616 - nodes in this community are weakly interconnected._
- **Should `containers` be split into smaller, more focused modules?**
  _Cohesion score 0.04421768707482993 - nodes in this community are weakly interconnected._
- **Should `containers` be split into smaller, more focused modules?**
  _Cohesion score 0.045068027210884355 - nodes in this community are weakly interconnected._