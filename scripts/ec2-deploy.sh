#!/bin/sh
# Run on EC2 after a new image is pushed to Docker Hub.
set -e

cd "${HOME}"

echo "Pulling latest image..."
docker compose pull

echo "Starting services..."
docker compose up -d

echo "Container status:"
docker compose ps

echo "Health check:"
sleep 15
curl -sf http://localhost:8088/actuator/health | head -c 500 || echo "Health check failed — see: docker compose logs app --tail 30"
