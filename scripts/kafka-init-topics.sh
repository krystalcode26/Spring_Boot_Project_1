#!/bin/bash
set -euo pipefail

BOOTSTRAP="${KAFKA_BOOTSTRAP_SERVER:-kafka-1:19092}"
TOPICS_BIN="/opt/kafka/bin/kafka-topics.sh"
BROKER_BIN="/opt/kafka/bin/kafka-broker-api-versions.sh"

wait_for_broker() {
  local attempts=30
  local attempt=1

  while (( attempt <= attempts )); do
    if "$BROKER_BIN" --bootstrap-server "$BOOTSTRAP" >/dev/null 2>&1; then
      echo "Kafka broker is ready at ${BOOTSTRAP}"
      return 0
    fi

    echo "Waiting for Kafka broker... (${attempt}/${attempts})"
    sleep 2
    ((attempt++))
  done

  echo "Kafka broker not ready at ${BOOTSTRAP}"
  return 1
}

create_topic() {
  local topic="$1"
  local attempt=1

  while (( attempt <= 5 )); do
    if "$TOPICS_BIN" --create --if-not-exists \
      --topic "$topic" \
      --partitions 3 \
      --replication-factor 3 \
      --bootstrap-server "$BOOTSTRAP"; then
      echo "Topic ready: ${topic}"
      return 0
    fi

    echo "Retry creating ${topic}... (${attempt}/5)"
    sleep 2
    ((attempt++))
  done

  echo "Failed to create topic: ${topic}"
  return 1
}

wait_for_broker
create_topic "employee-events"
create_topic "employee-events.DLT"

echo "Kafka topics initialized successfully"
