#!/bin/sh
set -eu

: "${POSTGRES_PASSWORD:?Provide POSTGRES_ADMIN_PASSWORD through a Kubernetes Secret}"
: "${SERVICE_TEMPLATE_APP_PASSWORD:?Provide SERVICE_TEMPLATE_APP_PASSWORD through a Kubernetes Secret}"
: "${SERVICE_TEMPLATE_MIGRATOR_PASSWORD:?Provide SERVICE_TEMPLATE_MIGRATOR_PASSWORD through a Kubernetes Secret}"

case "$POSTGRES_PASSWORD:$SERVICE_TEMPLATE_APP_PASSWORD:$SERVICE_TEMPLATE_MIGRATOR_PASSWORD" in
    *replace-with-a-unique-random-value*)
        printf '%s\n' "Replace all example database passwords before provisioning." >&2
        exit 1
        ;;
esac
