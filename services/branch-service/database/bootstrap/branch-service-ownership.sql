-- This file is executed by psql; passwords are provided through the process environment.
\getenv branch_service_migrator_password BRANCH_SERVICE_MIGRATOR_PASSWORD
\getenv branch_service_app_password BRANCH_SERVICE_APP_PASSWORD

CREATE ROLE branch_service_migrator
    LOGIN PASSWORD :'branch_service_migrator_password'
    NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT NOREPLICATION NOBYPASSRLS;

CREATE ROLE branch_service_app
    LOGIN PASSWORD :'branch_service_app_password'
    NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT NOREPLICATION NOBYPASSRLS;

CREATE SCHEMA branch_schema AUTHORIZATION branch_service_migrator;
REVOKE ALL ON SCHEMA branch_schema FROM PUBLIC;
GRANT USAGE ON SCHEMA branch_schema TO branch_service_app;
