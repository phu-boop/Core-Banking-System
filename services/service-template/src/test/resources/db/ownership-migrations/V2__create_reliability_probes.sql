CREATE TABLE reliability_probe (
    id BIGSERIAL PRIMARY KEY,
    event_id UUID,
    value TEXT NOT NULL
);

CREATE TABLE outbox_event (
    event_id UUID PRIMARY KEY,
    event_type TEXT NOT NULL,
    aggregate_type TEXT NOT NULL,
    aggregate_id TEXT NOT NULL,
    correlation_id TEXT,
    payload JSONB NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING',
    publish_attempt_count INTEGER NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMPTZ,
    last_error TEXT,
    CONSTRAINT outbox_event_status_check
        CHECK (status IN ('PENDING', 'PUBLISHED'))
);

CREATE TABLE processed_event (
    event_id UUID NOT NULL,
    consumer_name TEXT NOT NULL,
    event_type TEXT NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (event_id, consumer_name)
);

GRANT SELECT, INSERT, UPDATE, DELETE
    ON TABLE reliability_probe, outbox_event, processed_event
    TO service_template_app;

GRANT USAGE, SELECT
    ON SEQUENCE reliability_probe_id_seq
    TO service_template_app;
