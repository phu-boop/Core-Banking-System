CREATE TABLE ownership_probe (
    id BIGSERIAL PRIMARY KEY,
    value TEXT NOT NULL
);

GRANT SELECT, INSERT, UPDATE, DELETE
    ON TABLE ownership_probe
    TO service_template_app;

GRANT USAGE, SELECT
    ON SEQUENCE ownership_probe_id_seq
    TO service_template_app;
