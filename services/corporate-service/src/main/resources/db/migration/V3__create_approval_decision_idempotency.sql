CREATE TABLE corporate_schema.approval_decision_idempotency (
    idempotency_id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES corporate_schema.organization (organization_id),
    approval_request_id UUID NOT NULL REFERENCES corporate_schema.approval_request (approval_request_id),
    actor_subject TEXT NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL,
    payload_hash CHAR(64) NOT NULL,
    result_status VARCHAR(12) NOT NULL,
    result_current_step INTEGER,
    result_history_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL '24 hours'),
    UNIQUE (organization_id, approval_request_id, actor_subject, idempotency_key)
);

CREATE INDEX approval_decision_idempotency_expiry_idx
    ON corporate_schema.approval_decision_idempotency (expires_at);

GRANT SELECT, INSERT ON corporate_schema.approval_decision_idempotency TO corporate_service_app;
