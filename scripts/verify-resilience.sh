#!/bin/sh
# Demo script: verify name aggregation + resilience (retry / downgrade / recovery).
# Usage: ./scripts/verify-resilience.sh [base-url]
# Example: ./scripts/verify-resilience.sh http://18.225.218.150:8088

BASE_URL="${1:-http://localhost:8088}"
AGG_URL="${BASE_URL}/name/aggregation"
HEALTH_URL="${BASE_URL}/actuator/health"

PAYLOAD='{"name":["Jessica","Jocelyn","Simon","Suzy","April","Allen","Liz","Rachel"]}'

echo "=== 1. Health ==="
curl -sf "${HEALTH_URL}" | head -c 400
echo ""
echo ""

echo "=== 2. Name aggregation ==="
RESPONSE=$(curl -sf -X POST "${AGG_URL}" \
  -H "Content-Type: application/json" \
  -d "${PAYLOAD}")
echo "${RESPONSE}"
echo ""

if echo "${RESPONSE}" | grep -q '"warning"'; then
  if echo "${RESPONSE}" | grep -q '"warning":null'; then
    echo "OK: No warning — downstream likely succeeded."
  else
    echo "OK: Downgrade working — response includes warning (Steven may be down)."
    echo "    Check app logs for [Downstream] retry lines and [Recovery] persist/replay."
  fi
else
  echo "Response received (check names include Krystal)."
fi

echo ""
echo "=== 3. What to look for in logs (EC2) ==="
echo "  docker compose logs app --tail 50 | grep -E 'Downstream|Recovery|Circuit|Chain'"
