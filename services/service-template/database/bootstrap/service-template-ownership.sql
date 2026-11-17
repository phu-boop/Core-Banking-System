CREATE ROLE service_template_migrator
    LOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOINHERIT
    NOREPLICATION
    NOBYPASSRLS;

CREATE ROLE service_template_app
    LOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOINHERIT
    NOREPLICATION
    NOBYPASSRLS;

CREATE SCHEMA service_template_schema AUTHORIZATION service_template_migrator;

REVOKE ALL ON SCHEMA service_template_schema FROM PUBLIC;
GRANT USAGE ON SCHEMA service_template_schema TO service_template_app;
