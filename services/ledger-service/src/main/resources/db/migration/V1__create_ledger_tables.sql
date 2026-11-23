CREATE TABLE ledger_schema.journal (
    journal_id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    operation_key VARCHAR(128) NOT NULL,
    status VARCHAR(8) NOT NULL CHECK (status IN ('POSTED')),
    reversal_of_journal_id UUID REFERENCES ledger_schema.journal(journal_id),
    posted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX journal_operation_key_unique ON ledger_schema.journal(operation_key);
CREATE INDEX journal_transaction_idx ON ledger_schema.journal(transaction_id);
CREATE UNIQUE INDEX journal_one_reversal_unique
    ON ledger_schema.journal(reversal_of_journal_id)
    WHERE reversal_of_journal_id IS NOT NULL;

CREATE TABLE ledger_schema.journal_entry (
    entry_id UUID PRIMARY KEY,
    journal_id UUID NOT NULL REFERENCES ledger_schema.journal(journal_id),
    account_id UUID NOT NULL,
    side VARCHAR(6) NOT NULL CHECK (side IN ('DEBIT', 'CREDIT')),
    amount NUMERIC(19,2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL CHECK (currency = 'VND')
);

CREATE INDEX journal_entry_account_idx ON ledger_schema.journal_entry(account_id, journal_id);

CREATE TABLE ledger_schema.ledger_audit (
    audit_id UUID PRIMARY KEY,
    actor TEXT NOT NULL,
    action TEXT NOT NULL,
    resource_id TEXT NOT NULL,
    result TEXT NOT NULL,
    correlation_id TEXT NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
