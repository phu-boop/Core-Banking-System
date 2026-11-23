#!/usr/bin/env bash
set -euo pipefail

database="${POSTGRES_DB:-core_banking}"
admin="${POSTGRES_USER:-postgres}"

services=(
  identity party branch corporate account ledger transfer
  payment audit notification reporting
)

psql --username "$admin" --dbname "$database" <<'SQL'
\set ON_ERROR_STOP on
SELECT 1;
SQL

psql --username "$admin" --dbname "$database" --set ON_ERROR_STOP=1 <<'SQL'
\getenv keycloak_password KEYCLOAK_DB_PASSWORD
SELECT 'CREATE DATABASE keycloak'
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'keycloak') \gexec
SQL

psql --username "$admin" --dbname "$database" --set ON_ERROR_STOP=1 <<'SQL'
\getenv keycloak_password KEYCLOAK_DB_PASSWORD
SELECT format('CREATE ROLE keycloak LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT NOREPLICATION NOBYPASSRLS PASSWORD %L', :'keycloak_password')
WHERE NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'keycloak') \gexec
ALTER DATABASE keycloak OWNER TO keycloak;
SQL

for service in "${services[@]}"; do
  service_upper="$(printf '%s' "$service" | tr '[:lower:]' '[:upper:]')"
  migrator_var="${service_upper}_SERVICE_MIGRATOR_PASSWORD"
  app_var="${service_upper}_SERVICE_APP_PASSWORD"
  migrator_password="${!migrator_var:?Missing $migrator_var}"
  app_password="${!app_var:?Missing $app_var}"

  MIGRATOR_PASSWORD="$migrator_password" APP_PASSWORD="$app_password" SERVICE="$service" \
  psql --username "$admin" --dbname "$database" --set ON_ERROR_STOP=1 <<'SQL'
\set migrator_password `echo "$MIGRATOR_PASSWORD"`
\set app_password `echo "$APP_PASSWORD"`
\set service `echo "$SERVICE"`

SELECT format('CREATE ROLE %I LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT NOREPLICATION NOBYPASSRLS PASSWORD %L',
              :'service' || '_service_migrator', :'migrator_password')
WHERE NOT EXISTS (
  SELECT 1 FROM pg_roles WHERE rolname = :'service' || '_service_migrator'
) \gexec

SELECT format('CREATE ROLE %I LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT NOREPLICATION NOBYPASSRLS PASSWORD %L',
              :'service' || '_service_app', :'app_password')
WHERE NOT EXISTS (
  SELECT 1 FROM pg_roles WHERE rolname = :'service' || '_service_app'
) \gexec

SELECT format('CREATE SCHEMA IF NOT EXISTS %I AUTHORIZATION %I',
              :'service' || '_schema', :'service' || '_service_migrator')
\gexec

SELECT format('REVOKE ALL ON SCHEMA %I FROM PUBLIC',
              :'service' || '_schema')
\gexec

SELECT format('GRANT USAGE ON SCHEMA %I TO %I',
              :'service' || '_schema', :'service' || '_service_app')
\gexec
SQL
done
