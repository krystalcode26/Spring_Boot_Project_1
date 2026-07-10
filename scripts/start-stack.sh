#!/bin/bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "Starting database and vault..."
docker compose up -d db vault vault-init

echo "Starting Kafka brokers..."
docker compose up -d kafka-1 kafka-2 kafka-3

echo "Waiting for Kafka brokers to become healthy..."
for attempt in $(seq 1 60); do
  healthy_count="$(docker ps --filter "name=kafka-" --format '{{.Status}}' | grep -c "(healthy)" || true)"
  if [[ "$healthy_count" -ge 3 ]]; then
    echo "Kafka brokers are healthy."
    break
  fi

  if [[ "$attempt" -eq 60 ]]; then
    echo "Kafka brokers did not become healthy in time."
    echo "Try: docker compose restart kafka-1 kafka-2 kafka-3"
    exit 1
  fi

  sleep 2
done

echo "Creating Kafka topics..."
docker compose up -d kafka-init
docker wait kafka-init >/dev/null

if [[ "$(docker inspect kafka-init --format '{{.State.ExitCode}}')" != "0" ]]; then
  echo "kafka-init failed. Showing logs:"
  docker logs kafka-init
  exit 1
fi

echo "Starting app and Kafka UI..."
docker compose up -d kafka-ui app

echo "Stack is up."
docker ps --format 'table {{.Names}}\t{{.Status}}' | grep -E 'kafka-|ems-|NAMES'
