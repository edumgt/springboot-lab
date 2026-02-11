#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

docker exec -i exam2-postgres psql -U postgres -d exam2 -c "\dt" \
  -c "SELECT id, user_name, role FROM t_user ORDER BY id;" \
  -c "SELECT id, name, question_count, score FROM t_exam_paper ORDER BY id;"
