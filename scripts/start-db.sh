#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

echo "[INFO] Starting PostgreSQL container..."
docker compose up -d postgres

echo "[INFO] Waiting for DB health check..."
docker compose ps

until [ "$(docker inspect -f '{{.State.Health.Status}}' exam2-postgres 2>/dev/null || echo starting)" = "healthy" ]; do
  sleep 2
  echo "[INFO] waiting for postgres health..."
done

echo "[INFO] PostgreSQL is healthy."
