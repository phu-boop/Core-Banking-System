ALTER TABLE transfer_schema.transfers
    ADD COLUMN IF NOT EXISTS payment_request_id UUID,
    ADD COLUMN IF NOT EXISTS payment_status VARCHAR(32);

CREATE INDEX IF NOT EXISTS idx_transfers_payment
    ON transfer_schema.transfers(payment_request_id);
