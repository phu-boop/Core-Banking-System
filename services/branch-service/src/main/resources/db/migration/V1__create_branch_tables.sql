CREATE TABLE branch_schema.branch (
    branch_id UUID PRIMARY KEY,
    code VARCHAR(32) NOT NULL CHECK (length(btrim(code)) > 0),
    name VARCHAR(120) NOT NULL CHECK (length(btrim(name)) > 0),
    status VARCHAR(8) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX branch_code_case_insensitive_unique
    ON branch_schema.branch (lower(code));

CREATE INDEX branch_status_name_idx
    ON branch_schema.branch (status, name, branch_id);

CREATE TABLE branch_schema.branch_audit (
    audit_id UUID PRIMARY KEY,
    actor TEXT NOT NULL,
    action TEXT NOT NULL,
    resource_type TEXT NOT NULL,
    resource_id TEXT NOT NULL,
    result TEXT NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    correlation_id TEXT NOT NULL
);

GRANT SELECT, INSERT ON branch_schema.branch TO branch_service_app;
GRANT SELECT, INSERT ON branch_schema.branch_audit TO branch_service_app;
