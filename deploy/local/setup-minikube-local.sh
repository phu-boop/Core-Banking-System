#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
local_dir="$repo_root/deploy/local"
env_file="$local_dir/.env"

if [[ ! -f "$env_file" ]]; then
  cp "$local_dir/.env.example" "$env_file"
  for key in \
    POSTGRES_SUPERUSER_PASSWORD KEYCLOAK_ADMIN_PASSWORD KEYCLOAK_DB_PASSWORD \
    IDENTITY_SERVICE_MIGRATOR_PASSWORD IDENTITY_SERVICE_APP_PASSWORD \
    PARTY_SERVICE_MIGRATOR_PASSWORD PARTY_SERVICE_APP_PASSWORD \
    BRANCH_SERVICE_MIGRATOR_PASSWORD BRANCH_SERVICE_APP_PASSWORD \
    CORPORATE_SERVICE_MIGRATOR_PASSWORD CORPORATE_SERVICE_APP_PASSWORD \
    ACCOUNT_SERVICE_MIGRATOR_PASSWORD ACCOUNT_SERVICE_APP_PASSWORD \
    LEDGER_SERVICE_MIGRATOR_PASSWORD LEDGER_SERVICE_APP_PASSWORD \
    TRANSFER_SERVICE_MIGRATOR_PASSWORD TRANSFER_SERVICE_APP_PASSWORD \
    PAYMENT_SERVICE_MIGRATOR_PASSWORD PAYMENT_SERVICE_APP_PASSWORD \
    AUDIT_SERVICE_MIGRATOR_PASSWORD AUDIT_SERVICE_APP_PASSWORD \
    NOTIFICATION_SERVICE_MIGRATOR_PASSWORD NOTIFICATION_SERVICE_APP_PASSWORD \
    REPORTING_SERVICE_MIGRATOR_PASSWORD REPORTING_SERVICE_APP_PASSWORD; do
    value="$(openssl rand -base64 32 | tr -d '/+=')"
    sed -i "s|^${key}=.*|${key}=${value}|" "$env_file"
  done
fi

docker compose --env-file "$env_file" -f "$local_dir/docker-compose.yml" up -d
docker compose --env-file "$env_file" -f "$local_dir/docker-compose.yml" ps

if ! minikube status >/dev/null 2>&1; then
  minikube start
fi

set -a
source "$env_file"
set +a

create_db_secret() {
  local service="$1"
  local env_prefix="$(printf '%s' "$service" | tr '[:lower:]' '[:upper:]')"
  local app_password_var="${env_prefix}_SERVICE_APP_PASSWORD"
  local username="${service}_service_app"
  local password="${!app_password_var}"
  kubectl create secret generic "${service}-service-db-credentials" \
    --namespace default \
    --from-literal=username="$username" \
    --from-literal=password="$password" \
    --dry-run=client -o yaml | kubectl apply -f -
}

for service in identity party branch corporate account ledger transfer payment audit notification reporting; do
  create_db_secret "$service"
done

kubectl create secret generic corporate-party-client-credentials \
  --namespace default \
  --from-literal=client-id=corporate-party-client \
  --from-literal=client-secret="$CORPORATE_PARTY_CLIENT_SECRET" \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic transfer-corporate-client-credentials \
  --namespace default \
  --from-literal=client-id=transfer-corporate-client \
  --from-literal=client-secret="$TRANSFER_CORPORATE_CLIENT_SECRET" \
  --dry-run=client -o yaml | kubectl apply -f -

for image in \
  identity-service party-service branch-service corporate-service \
  account-service ledger-service transfer-service payment-service \
  audit-service notification-service reporting-service api-docs-service; do
  docker build --tag "core-banking/${image}:ci" "$repo_root/services/${image}"
  minikube image load "core-banking/${image}:ci"
done

kubectl apply -k "$repo_root/deploy/kubernetes/local"
kubectl get deployments,services,pods
