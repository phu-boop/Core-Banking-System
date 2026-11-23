CREATE SCHEMA IF NOT EXISTS reporting_schema;

CREATE TABLE reporting_schema.report_projections (
    projection_id UUID PRIMARY KEY,
    report_type VARCHAR(64) NOT NULL,
    row_key VARCHAR(255) NOT NULL,
    organization_id VARCHAR(255),
    account_id VARCHAR(255),
    branch_id VARCHAR(255),
    channel VARCHAR(32),
    source_at TIMESTAMPTZ NOT NULL,
    payload JSONB NOT NULL
);

CREATE INDEX idx_reporting_scope
    ON reporting_schema.report_projections(report_type, organization_id, account_id, branch_id);
CREATE INDEX idx_reporting_source
    ON reporting_schema.report_projections(source_at);

CREATE TABLE reporting_schema.export_jobs (
    report_job_id UUID PRIMARY KEY,
    export_key VARCHAR(255) UNIQUE NOT NULL,
    report_type VARCHAR(64) NOT NULL,
    format VARCHAR(16) NOT NULL,
    organization_id VARCHAR(255),
    account_id VARCHAR(255),
    branch_id VARCHAR(255),
    channel VARCHAR(32),
    range_start TIMESTAMPTZ NOT NULL,
    range_end TIMESTAMPTZ NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
