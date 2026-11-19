CREATE TABLE identity_schema.user_identity (
    user_id UUID PRIMARY KEY,
    issuer TEXT NOT NULL CHECK (length(issuer) > 0),
    subject TEXT NOT NULL CHECK (length(subject) > 0),
    party_id UUID,
    status VARCHAR(8) NOT NULL CHECK (status IN ('ACTIVE', 'DISABLED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_identity_issuer_subject_unique UNIQUE (issuer, subject)
);

CREATE TABLE identity_schema.authorization_grant (
    user_id UUID NOT NULL REFERENCES identity_schema.user_identity (user_id),
    permission TEXT NOT NULL CHECK (length(permission) > 0),
    scope_type VARCHAR(12) NOT NULL CHECK (scope_type IN ('SELF', 'ORGANIZATION', 'BRANCH')),
    scope_id TEXT NOT NULL CHECK (length(scope_id) > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, permission, scope_type, scope_id)
);

GRANT SELECT ON identity_schema.user_identity, identity_schema.authorization_grant
    TO identity_service_app;
