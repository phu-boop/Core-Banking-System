CREATE SCHEMA IF NOT EXISTS payment_schema;

CREATE TABLE payment_schema.payments (
    payment_id UUID PRIMARY KEY,
    transfer_id UUID NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    destination_reference VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    provider_reference VARCHAR(255),
    correlation_id VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE payment_schema.payment_idempotency (
    idempotency_key VARCHAR(255) NOT NULL,
    actor_subject VARCHAR(255) NOT NULL,
    operation VARCHAR(255) NOT NULL,
    business_scope VARCHAR(255) NOT NULL,
    payload_hash VARCHAR(64) NOT NULL,
    payment_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (idempotency_key, actor_subject, operation, business_scope)
);

CREATE TABLE payment_schema.payment_outcomes (
    outcome_id UUID PRIMARY KEY,
    payment_id UUID NOT NULL,
    outcome VARCHAR(32) NOT NULL,
    outcome_source VARCHAR(255) NOT NULL,
    provider_reference VARCHAR(255),
    correlation_id VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE payment_schema.payment_audit (
    audit_id UUID PRIMARY KEY,
    payment_id UUID NOT NULL,
    actor_subject VARCHAR(255),
    action VARCHAR(64) NOT NULL,
    result VARCHAR(32) NOT NULL,
    outcome_source VARCHAR(255),
    provider_reference VARCHAR(255),
    correlation_id VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_payment_outcome_once
    ON payment_schema.payment_outcomes(
        payment_id, outcome, outcome_source, COALESCE(provider_reference, '')
    );
CREATE INDEX idx_payments_transfer ON payment_schema.payments(transfer_id);
