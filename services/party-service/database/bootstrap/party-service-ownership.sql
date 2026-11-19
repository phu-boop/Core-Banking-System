-- This file is executed by psql; passwords are provided through the process environment.
\getenv party_service_migrator_password PARTY_SERVICE_MIGRATOR_PASSWORD
\getenv party_service_app_password PARTY_SERVICE_APP_PASSWORD

CREATE ROLE party_service_migrator
    LOGIN PASSWORD :'party_service_migrator_password'
    NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT NOREPLICATION NOBYPASSRLS;

CREATE ROLE party_service_app
    LOGIN PASSWORD :'party_service_app_password'
    NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT NOREPLICATION NOBYPASSRLS;

CREATE SCHEMA party_schema AUTHORIZATION party_service_migrator;
REVOKE ALL ON SCHEMA party_schema FROM PUBLIC;
GRANT USAGE ON SCHEMA party_schema TO party_service_app;
