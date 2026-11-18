-- This file is executed by psql; passwords are provided through the process environment.
\getenv service_template_migrator_password SERVICE_TEMPLATE_MIGRATOR_PASSWORD
\getenv service_template_app_password SERVICE_TEMPLATE_APP_PASSWORD

CREATE ROLE service_template_migrator
    LOGIN
    PASSWORD :'service_template_migrator_password'
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOINHERIT
    NOREPLICATION
    NOBYPASSRLS;

CREATE ROLE service_template_app
    LOGIN
    PASSWORD :'service_template_app_password'
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOINHERIT
    NOREPLICATION
    NOBYPASSRLS;

CREATE SCHEMA service_template_schema AUTHORIZATION service_template_migrator;

REVOKE ALL ON SCHEMA service_template_schema FROM PUBLIC;
GRANT USAGE ON SCHEMA service_template_schema TO service_template_app;
