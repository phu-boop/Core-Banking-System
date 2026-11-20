-- This file is executed by psql; passwords are provided through the process environment.
\getenv corporate_service_migrator_password CORPORATE_SERVICE_MIGRATOR_PASSWORD
\getenv corporate_service_app_password CORPORATE_SERVICE_APP_PASSWORD

CREATE ROLE corporate_service_migrator
    LOGIN PASSWORD :'corporate_service_migrator_password'
    NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT NOREPLICATION NOBYPASSRLS;

CREATE ROLE corporate_service_app
    LOGIN PASSWORD :'corporate_service_app_password'
    NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT NOREPLICATION NOBYPASSRLS;

CREATE SCHEMA corporate_schema AUTHORIZATION corporate_service_migrator;
REVOKE ALL ON SCHEMA corporate_schema FROM PUBLIC;
GRANT USAGE ON SCHEMA corporate_schema TO corporate_service_app;
