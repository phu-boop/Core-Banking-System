CREATE SCHEMA IF NOT EXISTS audit_schema;

CREATE TABLE audit_schema.audit_records (
    audit_record_id UUID PRIMARY KEY,
    event_id VARCHAR(255) UNIQUE NOT NULL,
    actor_id VARCHAR(255) NOT NULL,
    channel VARCHAR(32) NOT NULL,
    action VARCHAR(128) NOT NULL,
    resource_type VARCHAR(128) NOT NULL,
    resource_id VARCHAR(255) NOT NULL,
    organization_id VARCHAR(255),
    branch_id VARCHAR(255),
    result VARCHAR(32) NOT NULL,
    correlation_id VARCHAR(255) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_actor ON audit_schema.audit_records(actor_id);
CREATE INDEX idx_audit_resource ON audit_schema.audit_records(resource_type, resource_id);
CREATE INDEX idx_audit_org ON audit_schema.audit_records(organization_id);
CREATE INDEX idx_audit_branch ON audit_schema.audit_records(branch_id);
CREATE INDEX idx_audit_occurred ON audit_schema.audit_records(occurred_at);
