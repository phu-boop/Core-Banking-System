CREATE SCHEMA IF NOT EXISTS notification_schema;

CREATE TABLE notification_schema.notifications (
    notification_id UUID PRIMARY KEY,
    notification_key VARCHAR(255) UNIQUE NOT NULL,
    recipient_ref VARCHAR(255) NOT NULL,
    template_code VARCHAR(128) NOT NULL,
    channel VARCHAR(32) NOT NULL,
    correlation_id VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE notification_schema.notification_attempts (
    attempt_id UUID PRIMARY KEY,
    notification_id UUID NOT NULL,
    attempt_number INTEGER NOT NULL,
    status VARCHAR(32) NOT NULL,
    provider_reference VARCHAR(255),
    error_code VARCHAR(128),
    attempted_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (notification_id, attempt_number)
);

CREATE INDEX idx_notifications_correlation
    ON notification_schema.notifications(correlation_id);
