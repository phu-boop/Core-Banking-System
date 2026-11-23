CREATE TABLE transfer_schema.retail_transfer (
    transfer_id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    operation_type VARCHAR(16) NOT NULL,
    source_account_id UUID,
    destination_account_id UUID,
    processing_branch_id UUID NOT NULL,
    amount NUMERIC(19,2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL CHECK (currency = 'VND'),
    status VARCHAR(16) NOT NULL,
    ledger_journal_id UUID,
    correlation_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE transfer_schema.retail_transfer_idempotency (
    idempotency_key VARCHAR(255) NOT NULL,
    actor_subject VARCHAR(255) NOT NULL,
    business_scope VARCHAR(255) NOT NULL,
    operation VARCHAR(64) NOT NULL,
    payload_hash VARCHAR(64) NOT NULL,
    transfer_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (idempotency_key, actor_subject, business_scope, operation)
);

CREATE INDEX retail_transfer_status_idx
    ON transfer_schema.retail_transfer(status, created_at);
