CREATE TABLE corporate_schema.organization (
    organization_id UUID PRIMARY KEY,
    party_id UUID NOT NULL UNIQUE,
    status VARCHAR(12) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX organization_status_idx
    ON corporate_schema.organization (status, organization_id);

CREATE TABLE corporate_schema.organization_audit (
    audit_id UUID PRIMARY KEY,
    actor TEXT NOT NULL,
    action TEXT NOT NULL,
    resource_type TEXT NOT NULL,
    resource_id TEXT NOT NULL,
    result TEXT NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    correlation_id TEXT NOT NULL
);

GRANT SELECT, INSERT ON corporate_schema.organization TO corporate_service_app;
GRANT SELECT, INSERT ON corporate_schema.organization_audit TO corporate_service_app;
