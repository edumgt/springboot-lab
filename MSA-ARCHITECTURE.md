# MSA Architecture

This repository now includes a Maven multi-module microservices layout for the exam platform.

## Modules
- `shared/common-lib`: shared response models, exception handling, JSON/date utilities.
- `shared/security-lib`: JWT properties, provider, servlet auth filter, stateless security auto-configuration.
- `services/auth-service`: username/password authentication and JWT issuance.
- `services/user-service`: user CRUD APIs.
- `services/subject-service`: subject and education APIs.
- `services/question-service`: question CRUD APIs and subject-service integration.
- `services/exam-paper-service`: exam-paper CRUD APIs and question/subject integrations.
- `services/exam-runtime-service`: answer submission, grading persistence, Kafka grading events.
- `services/report-service`: admin and student dashboards using reporting queries.
- `platform/api-gateway`: Spring Cloud Gateway entrypoint with JWT validation and header forwarding.

## Runtime Ports
- Gateway: 8080
- Auth: 8081
- User: 8082
- Subject: 8083
- Question: 8084
- Exam Paper: 8085
- Exam Runtime: 8086
- Report: 8087

## Deployment Assets
- `deploy/docker-compose.yml`: full local stack.
- `deploy/docker-compose-infra.yml`: infrastructure-only stack.
- `deploy/postgres-init`: database bootstrap scripts.
- `deploy/k8s`: namespace, config, secrets, infrastructure, service manifests, ingress.

## Notes
- The legacy monolith source under `src/` is preserved as a reference implementation.
- The root `pom.xml` is now a parent aggregator for all new modules.
