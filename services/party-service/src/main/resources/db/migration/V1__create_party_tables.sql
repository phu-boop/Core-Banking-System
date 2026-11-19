CREATE TABLE party_schema.party (
    party_id UUID PRIMARY KEY,
    party_type VARCHAR(12) NOT NULL CHECK (party_type IN ('INDIVIDUAL', 'ORGANIZATION')),
    status VARCHAR(12) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    display_name TEXT NOT NULL CHECK (length(btrim(display_name)) > 0),
    managing_branch_id UUID,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE party_schema.individual_profile (
    party_id UUID PRIMARY KEY REFERENCES party_schema.party (party_id),
    first_name TEXT,
    last_name TEXT,
    date_of_birth DATE,
    national_id_hash TEXT,
    CONSTRAINT first_name_minimum CHECK (first_name IS NULL OR length(btrim(first_name)) > 0),
    CONSTRAINT last_name_minimum CHECK (last_name IS NULL OR length(btrim(last_name)) > 0),
    CONSTRAINT national_id_hash_minimum CHECK (national_id_hash IS NULL OR length(national_id_hash) >= 32)
);

CREATE TABLE party_schema.party_audit (
    audit_id UUID PRIMARY KEY,
    actor TEXT NOT NULL,
    action TEXT NOT NULL,
    resource_type TEXT NOT NULL,
    resource_id TEXT NOT NULL,
    result TEXT NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    correlation_id TEXT,
    party_id UUID,
    branch_id UUID
);

CREATE TABLE party_schema.idempotency_record (
    actor TEXT NOT NULL,
    operation TEXT NOT NULL,
    business_scope TEXT NOT NULL,
    idempotency_key TEXT NOT NULL,
    payload_hash CHAR(64) NOT NULL,
    status_code INTEGER NOT NULL,
    response_body TEXT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (actor, operation, business_scope, idempotency_key)
);

GRANT SELECT, INSERT, UPDATE ON party_schema.party, party_schema.individual_profile
    TO party_service_app;
GRANT SELECT, INSERT ON party_schema.party_audit TO party_service_app;
GRANT SELECT, INSERT, UPDATE ON party_schema.idempotency_record
    TO party_service_app;
