CREATE SCHEMA IF NOT EXISTS transfer_schema;

CREATE TABLE transfer_schema.transfers (
    transfer_id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    operation_type VARCHAR(100) NOT NULL,
    operation_reference VARCHAR(255) NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    approval_request_id UUID,
    approval_policy_version INTEGER,
    approval_status VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    maker_subject VARCHAR(255) NOT NULL,
    correlation_id VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE transfer_schema.transfer_idempotency (
    idempotency_key VARCHAR(255) NOT NULL,
    actor_subject VARCHAR(255) NOT NULL,
    organization_id UUID NOT NULL,
    operation VARCHAR(255) NOT NULL,
    payload_hash VARCHAR(64) NOT NULL,
    transfer_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (idempotency_key, actor_subject, organization_id, operation)
);

CREATE TABLE transfer_schema.transfer_audit (
    audit_id UUID PRIMARY KEY,
    transfer_id UUID NOT NULL,
    actor_subject VARCHAR(255),
    action VARCHAR(64) NOT NULL,
    approval_request_id UUID,
    approval_policy_version INTEGER,
    result VARCHAR(32) NOT NULL,
    correlation_id VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_transfers_org_status ON transfer_schema.transfers(organization_id, status);
