CREATE TABLE reporting_schema.processed_events (
    event_id VARCHAR(255) NOT NULL,
    consumer_name VARCHAR(128) NOT NULL,
    event_type VARCHAR(128) NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (event_id, consumer_name)
);
