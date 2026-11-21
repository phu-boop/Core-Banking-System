CREATE TABLE identity_schema.identity_audit (
    audit_id UUID PRIMARY KEY,
    actor TEXT NOT NULL,
    action TEXT NOT NULL,
    resource_type TEXT NOT NULL,
    resource_id TEXT NOT NULL,
    result TEXT NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    correlation_id TEXT NOT NULL
);

CREATE INDEX identity_audit_actor_occurred_idx
    ON identity_schema.identity_audit (actor, occurred_at);

CREATE INDEX identity_audit_resource_occurred_idx
    ON identity_schema.identity_audit (resource_type, resource_id, occurred_at);

GRANT SELECT, INSERT ON identity_schema.identity_audit TO identity_service_app;
