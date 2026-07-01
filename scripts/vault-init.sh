#!/bin/sh
set -e

VAULT_ADDR="${VAULT_ADDR:-http://vault:8200}"
VAULT_TOKEN="${VAULT_TOKEN:-ems-vault-token}"

echo "Waiting for Vault at ${VAULT_ADDR}..."
until vault status >/dev/null 2>&1; do
  sleep 1
done

echo "Seeding integration secrets into secret/ems-backend..."
vault kv put secret/ems-backend \
  downstream.application.enabled=true \
  downstream.application.url="${DOWNSTREAM_APPLICATION_URL:-http://3.21.129.124:8080}" \
  downstream.application.path="${DOWNSTREAM_APPLICATION_PATH:-/api/v1/integrations/name/aggregation}" \
  upstream.application.enabled=true \
  upstream.application.url="${UPSTREAM_APPLICATION_URL:-http://3.134.97.56:8080}" \
  upstream.application.path="${UPSTREAM_APPLICATION_PATH:-/name/notify}" \
  app.student.name="${APP_STUDENT_NAME:-Krystal}"

echo "Vault secrets ready."
