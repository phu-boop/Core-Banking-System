CREATE TABLE account_schema.account (
    account_id UUID PRIMARY KEY,
    account_number VARCHAR(34) NOT NULL,
    party_id UUID NOT NULL,
    account_type VARCHAR(16) NOT NULL CHECK (account_type IN ('PAYMENT')),
    status VARCHAR(8) NOT NULL CHECK (status IN ('ACTIVE', 'BLOCKED', 'CLOSED')),
    currency VARCHAR(3) NOT NULL CHECK (currency = 'VND'),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX account_number_unique ON account_schema.account (account_number);
CREATE INDEX account_party_status_idx ON account_schema.account (party_id, status);

CREATE TABLE account_schema.account_status_history (
    history_id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES account_schema.account(account_id),
    from_status VARCHAR(8),
    to_status VARCHAR(8) NOT NULL,
    reason VARCHAR(120) NOT NULL,
    actor TEXT NOT NULL,
    correlation_id TEXT NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
