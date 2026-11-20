CREATE TABLE corporate_schema.approval_policy (
    policy_id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES corporate_schema.organization (organization_id),
    operation_type VARCHAR(64) NOT NULL,
    policy_version INTEGER NOT NULL CHECK (policy_version > 0),
    status VARCHAR(12) NOT NULL CHECK (status IN ('DRAFT', 'ACTIVE', 'INACTIVE')),
    created_by TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activated_at TIMESTAMPTZ,
    UNIQUE (organization_id, operation_type, policy_version),
    UNIQUE (policy_id, organization_id, operation_type)
);

CREATE UNIQUE INDEX approval_policy_one_active_idx
    ON corporate_schema.approval_policy (organization_id, operation_type)
    WHERE status = 'ACTIVE';

CREATE INDEX approval_policy_org_idx
    ON corporate_schema.approval_policy (organization_id, operation_type, policy_version DESC);

CREATE TABLE corporate_schema.approval_policy_level (
    policy_level_id UUID PRIMARY KEY,
    policy_id UUID NOT NULL REFERENCES corporate_schema.approval_policy (policy_id),
    step_number INTEGER NOT NULL CHECK (step_number > 0),
    role_code VARCHAR(64) NOT NULL,
    mandate_scope VARCHAR(64) NOT NULL,
    UNIQUE (policy_id, step_number)
);

CREATE TABLE corporate_schema.approval_request (
    approval_request_id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES corporate_schema.organization (organization_id),
    policy_id UUID NOT NULL REFERENCES corporate_schema.approval_policy (policy_id),
    operation_type VARCHAR(64) NOT NULL,
    operation_reference TEXT NOT NULL,
    maker_subject TEXT NOT NULL,
    status VARCHAR(12) NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    submitted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMPTZ,
    UNIQUE (approval_request_id, policy_id),
    FOREIGN KEY (policy_id, organization_id, operation_type)
        REFERENCES corporate_schema.approval_policy (policy_id, organization_id, operation_type)
);

CREATE INDEX approval_request_org_idx
    ON corporate_schema.approval_request (organization_id, submitted_at DESC);

CREATE TABLE corporate_schema.approval_step (
    approval_step_id UUID PRIMARY KEY,
    approval_request_id UUID NOT NULL REFERENCES corporate_schema.approval_request (approval_request_id),
    step_number INTEGER NOT NULL CHECK (step_number > 0),
    role_code VARCHAR(64) NOT NULL,
    mandate_scope VARCHAR(64) NOT NULL,
    status VARCHAR(12) NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'SKIPPED')),
    decided_by TEXT,
    decided_at TIMESTAMPTZ,
    UNIQUE (approval_request_id, step_number)
);

CREATE TABLE corporate_schema.approval_history (
    history_id UUID PRIMARY KEY,
    approval_request_id UUID NOT NULL,
    policy_id UUID NOT NULL REFERENCES corporate_schema.approval_policy (policy_id),
    actor TEXT NOT NULL,
    step_number INTEGER NOT NULL CHECK (step_number > 0),
    decision VARCHAR(8) NOT NULL CHECK (decision IN ('APPROVE', 'REJECT')),
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    correlation_id TEXT NOT NULL,
    FOREIGN KEY (approval_request_id, policy_id)
        REFERENCES corporate_schema.approval_request (approval_request_id, policy_id)
);

CREATE INDEX approval_history_request_idx
    ON corporate_schema.approval_history (approval_request_id, occurred_at);
CREATE INDEX approval_history_actor_idx
    ON corporate_schema.approval_history (actor, occurred_at);

GRANT SELECT, INSERT ON corporate_schema.approval_policy TO corporate_service_app;
GRANT UPDATE (status, activated_at) ON corporate_schema.approval_policy TO corporate_service_app;
GRANT SELECT, INSERT ON corporate_schema.approval_policy_level TO corporate_service_app;
GRANT SELECT, INSERT, UPDATE ON corporate_schema.approval_request TO corporate_service_app;
GRANT SELECT, INSERT, UPDATE ON corporate_schema.approval_step TO corporate_service_app;
GRANT SELECT, INSERT ON corporate_schema.approval_history TO corporate_service_app;
