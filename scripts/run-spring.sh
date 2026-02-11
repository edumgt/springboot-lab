#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

./scripts/start-db.sh

echo "[INFO] Running Spring Boot (profile: dev)..."
mvn spring-boot:run -Dspring-boot.run.profiles=dev
