CREATE TABLE corporate_schema.membership (
    membership_id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES corporate_schema.organization (organization_id),
    subject TEXT NOT NULL,
    role_code VARCHAR(64) NOT NULL,
    status VARCHAR(12) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX membership_active_unique_idx
    ON corporate_schema.membership (organization_id, subject, role_code)
    WHERE status = 'ACTIVE';

CREATE INDEX membership_lookup_idx
    ON corporate_schema.membership (organization_id, subject, role_code, status);

CREATE TABLE corporate_schema.mandate (
    mandate_id UUID PRIMARY KEY,
    membership_id UUID NOT NULL REFERENCES corporate_schema.membership (membership_id),
    mandate_scope VARCHAR(64) NOT NULL,
    status VARCHAR(12) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX mandate_active_unique_idx
    ON corporate_schema.mandate (membership_id, mandate_scope)
    WHERE status = 'ACTIVE';

CREATE INDEX mandate_lookup_idx
    ON corporate_schema.mandate (membership_id, mandate_scope, status);

GRANT SELECT, INSERT, UPDATE ON corporate_schema.membership TO corporate_service_app;
GRANT SELECT, INSERT, UPDATE ON corporate_schema.mandate TO corporate_service_app;
