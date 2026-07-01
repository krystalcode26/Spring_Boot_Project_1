#!/bin/sh
# Run on EC2 after a new image is pushed to Docker Hub.
set -e

COMPOSE_DIR="${COMPOSE_DIR:-${HOME}/ems-backend}"
COMPOSE_FILE="${COMPOSE_FILE:-${COMPOSE_DIR}/docker-compose.yml}"

if [ ! -f "$COMPOSE_FILE" ]; then
  COMPOSE_FILE="${HOME}/docker-compose.yml"
fi

echo "Using compose file: $COMPOSE_FILE"

echo "Pulling latest app image..."
docker compose -f "$COMPOSE_FILE" pull app

echo "Starting app service..."
docker compose -f "$COMPOSE_FILE" up -d app

echo "Container status:"
docker compose -f "$COMPOSE_FILE" ps

echo "Health check:"
sleep 15
curl -sf http://localhost:8088/actuator/health | head -c 500 || echo "Health check failed — see: docker compose logs app --tail 30"
