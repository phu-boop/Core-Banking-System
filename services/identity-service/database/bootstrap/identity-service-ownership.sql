-- This file is executed by psql; passwords are provided through the process environment.
\getenv identity_service_migrator_password IDENTITY_SERVICE_MIGRATOR_PASSWORD
\getenv identity_service_app_password IDENTITY_SERVICE_APP_PASSWORD

CREATE ROLE identity_service_migrator
    LOGIN
    PASSWORD :'identity_service_migrator_password'
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOINHERIT
    NOREPLICATION
    NOBYPASSRLS;

CREATE ROLE identity_service_app
    LOGIN
    PASSWORD :'identity_service_app_password'
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOINHERIT
    NOREPLICATION
    NOBYPASSRLS;

CREATE SCHEMA identity_schema AUTHORIZATION identity_service_migrator;

REVOKE ALL ON SCHEMA identity_schema FROM PUBLIC;
GRANT USAGE ON SCHEMA identity_schema TO identity_service_app;
