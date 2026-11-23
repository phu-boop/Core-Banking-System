#!/usr/bin/env bash
set -euo pipefail

container_name=core-banking-api-docs-smoke
base_url=http://127.0.0.1:18090

docker run --detach --name "$container_name" --publish 18090:8090 \
  core-banking/api-docs-service:ci >/dev/null
cleanup() {
  docker rm --force "$container_name" >/dev/null
}
trap cleanup EXIT

ready=0
for attempt in $(seq 1 30); do
  if curl --silent --fail --output /dev/null "$base_url/actuator/health"; then
    ready=1
    break
  fi
  sleep 2
done

if [[ "$ready" -ne 1 ]]; then
  docker logs "$container_name"
  echo "API docs service did not become healthy within 60 seconds." >&2
  exit 1
fi

verify_health_endpoint() {
  local endpoint="$1"
  curl --silent --show-error --fail "$base_url/$endpoint" |
    python3 -c '
import json
import sys

health = json.load(sys.stdin)
status = health.get("status")
if status != "UP":
    raise SystemExit("Unexpected status for {}: {!r}".format(sys.argv[1], status))
print("{} reports UP.".format(sys.argv[1]))
' "$endpoint"
}

verify_health_endpoint actuator/health
verify_health_endpoint actuator/health/liveness
verify_health_endpoint actuator/health/readiness

curl --silent --show-error --fail \
  "$base_url/actuator/metrics/jvm.memory.used" |
  python3 -c '
import json
import sys

metric = json.load(sys.stdin)
if metric.get("name") != "jvm.memory.used" or not metric.get("measurements"):
    raise SystemExit("JVM memory metric response is missing its name or measurements.")
print("JVM memory metric is available.")
'
