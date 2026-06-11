# Exam Platform — MSA 백엔드

Spring Boot 3 기반의 온라인 시험 플랫폼 백엔드입니다.  
**Spring Cloud Gateway + 8개 마이크로서비스**로 구성되며, PostgreSQL / Redis / Kafka 인프라 위에서 동작합니다.

---

## 1. 아키텍처 개요

```
외부 클라이언트
      │
      ▼
┌─────────────────┐
│   API Gateway   │  :8080  Spring Cloud Gateway · JWT 검증 · CORS
└────────┬────────┘
         │ (라우팅)
┌────────┴──────────────────────────────────────────────┐
│  auth-service  :8081   user-service     :8082          │
│  subject-svc   :8083   question-service :8084          │
│  exam-paper    :8085   exam-runtime     :8086          │
│  report-svc    :8087                                   │
└───────────────────────────────────────────────────────┘
         │                    │              │
    PostgreSQL 14          Redis 7       Kafka (Confluent 7.5)
```

레거시 모놀리스 소스는 `src/` 에 참조용으로 보존되어 있습니다.

---

## 2. 기술 스택

### Language & Build
| 항목 | 버전 |
|------|------|
| Java | 17 |
| Maven (multi-module) | 3.x |

### Core Framework
| 항목 | 버전 | 용도 |
|------|------|------|
| Spring Boot | 3.5.0 | 전체 서비스 기반 |
| Spring Cloud | 2024.0.1 | Gateway, LoadBalancer |
| Spring Cloud Gateway | (관리됨) | API Gateway / 라우팅 |
| Spring Security | (관리됨) | JWT 인증·인가 |
| Spring Web (MVC) | (관리됨) | REST API |
| Spring WebFlux | (관리됨) | WebClient (서비스간 HTTP 호출) |
| Spring Kafka | (관리됨) | 채점 이벤트 발행 |
| Spring Session Data Redis | (관리됨) | auth-service 세션 |
| Spring Data Redis | (관리됨) | user-service 캐시 |
| Spring Validation | (관리됨) | 입력 유효성 검사 |

### Persistence
| 항목 | 버전 | 용도 |
|------|------|------|
| PostgreSQL | 14 | 주 데이터 저장소 |
| MyBatis Spring Boot Starter | 3.0.4 | SQL 매핑 |
| PageHelper Spring Boot Starter | 2.1.0 | 페이징 (`helperDialect: postgresql`) |
| HikariCP | (관리됨) | DB 커넥션풀 |

### JWT & Security
| 항목 | 버전 | 용도 |
|------|------|------|
| JJWT (io.jsonwebtoken) | 0.12.6 | JWT 생성·검증 |
| `shared/security-lib` | 1.0.0 | JWT 필터·Auto-Configuration 공유 라이브러리 |

### Resilience (복원력)
| 항목 | 버전 | 용도 |
|------|------|------|
| Resilience4j Spring Boot 3 | 2.2.0 | Circuit Breaker (서비스간 호출 보호) |
| Spring Boot Starter AOP | (관리됨) | Resilience4j AOP 지원 |
| WebClient + `.timeout(5s)` | (관리됨) | 서비스간 호출 타임아웃 |

### Observability (관측 가능성)
| 항목 | 버전 | 용도 |
|------|------|------|
| Micrometer Tracing (Brave) | (관리됨) | 분산 트레이싱 (Zipkin 연동) |
| Zipkin Reporter Brave | (관리됨) | Trace 전송 (`/api/v2/spans`) |
| Micrometer Registry Prometheus | (관리됨) | `/actuator/prometheus` 메트릭 노출 |
| Spring Boot Actuator | (관리됨) | Health · Info · Metrics · Prometheus |

### API 문서
| 항목 | 버전 | URL |
|------|------|-----|
| springdoc-openapi (webmvc-ui) | 2.6.0 | `http://<service>:<port>/swagger-ui.html` |

### 공유 라이브러리 (`shared/`)
| 모듈 | 내용 |
|------|------|
| `common-lib` | `RestResponse`, `RestPage`, `GlobalExceptionHandler`, `BusinessException`, `JsonUtil`, `DateTimeUtil` |
| `security-lib` | `JwtTokenProvider`, `JwtAuthenticationFilter`, `JwtProperties`, `SecurityAutoConfiguration` |

### 인프라
| 항목 | 버전 | 용도 |
|------|------|------|
| Redis | 7-alpine | 세션·캐시 |
| Apache Kafka (Confluent) | 7.5.0 | 채점 완료 이벤트 스트리밍 |
| Zookeeper (Confluent) | 7.5.0 | Kafka 코디네이터 |
| Docker Compose | v2 | 로컬 개발 환경 |
| Kubernetes | 1.25+ | 운영 배포 |

### Utilities
| 항목 | 용도 |
|------|------|
| Lombok 1.18.24 | 보일러플레이트 제거 |
| ModelMapper 2.3.3 | DTO 매핑 |

---

## 3. 모듈 구조

```
springboot-lab/
├── shared/
│   ├── common-lib/          공통 응답·예외·유틸
│   └── security-lib/        JWT 인증 공유 라이브러리
├── services/
│   ├── auth-service/        :8081  로그인·JWT 발급·갱신
│   ├── user-service/        :8082  사용자 CRUD
│   ├── subject-service/     :8083  과목 관리
│   ├── question-service/    :8084  문항 CRUD
│   ├── exam-paper-service/  :8085  시험지 CRUD
│   ├── exam-runtime-service/:8086  응시·채점·Kafka 이벤트
│   └── report-service/      :8087  관리자·학생 대시보드
├── platform/
│   └── api-gateway/         :8080  Spring Cloud Gateway
├── deploy/
│   ├── docker-compose.yml       전체 로컬 스택
│   ├── docker-compose-infra.yml 인프라만 (DB·Redis·Kafka)
│   └── k8s/                     Kubernetes 매니페스트
└── src/                         레거시 모놀리스 (참조용)
```

---

## 4. 서비스 API 엔드포인트

게이트웨이(`localhost:8080`) 기준 라우팅 경로입니다.

| 경로 | 서비스 | 인증 |
|------|--------|------|
| `POST /auth/login` | auth-service | 불필요 |
| `POST /auth/refresh` | auth-service | 불필요 |
| `/api/users/**` | user-service | JWT |
| `/api/subjects/**` | subject-service | JWT |
| `/api/questions/**` | question-service | JWT |
| `/api/exam-papers/**` | exam-paper-service | JWT |
| `/api/exam-runtime/**` | exam-runtime-service | JWT |
| `/api/admin/dashboard/**` | report-service | JWT (ADMIN) |
| `/api/student/dashboard/**` | report-service | JWT (STUDENT) |

API 상세 문서: 각 서비스 포트의 `/swagger-ui.html`

---

## 5. 서비스간 호출 의존관계

```
exam-paper-service  ──→  question-service
exam-paper-service  ──→  subject-service
question-service    ──→  subject-service
exam-runtime-service──→  exam-paper-service
exam-runtime-service──→  Kafka (exam.grading.complete)
```

- **Circuit Breaker**: Resilience4j `@CircuitBreaker` (sliding-window 10, failure-rate 50%, open 10s)
- **Timeout**: WebClient 5초 + Reactor `.timeout(5s)`
- **JWT 전달**: `RequestContextHolder`로 인바운드 Bearer 토큰 자동 전파

---

## 6. 로컬 실행 가이드

### 인프라만 먼저 시작 (DB / Redis / Kafka)
```bash
cd deploy
docker compose -f docker-compose-infra.yml up -d
```

### 전체 스택 (서비스 포함)
```bash
cd deploy
docker compose up -d
```

### 서비스 개별 빌드 및 실행
```bash
# 공유 라이브러리 먼저 설치
mvn install -pl shared/common-lib,shared/security-lib -am

# 특정 서비스 실행
mvn spring-boot:run -pl services/question-service
```

### 기본 접속 정보
| 항목 | 값 |
|------|-----|
| API Gateway | `http://localhost:8080` |
| PostgreSQL | `localhost:5432` / `postgres` / `123456` / DB: `exam2` |
| Redis | `localhost:6379` / password: `redis1` |
| Kafka | `localhost:9092` |

> 운영 환경에서는 반드시 강한 시크릿(`JWT_SECRET`, `DS_PASSWORD`, `REDIS_PASSWORD`)으로 교체하세요.

---

## 7. Kubernetes 배포

```bash
kubectl apply -f deploy/k8s/00-namespace.yaml
kubectl apply -f deploy/k8s/01-configmap.yaml
kubectl apply -f deploy/k8s/02-secrets.yaml
kubectl apply -f deploy/k8s/infrastructure/
kubectl apply -f deploy/k8s/services/
kubectl apply -f deploy/k8s/ingress.yaml
```

### ConfigMap 주요 설정값 (`deploy/k8s/01-configmap.yaml`)

| 키 | 값 |
|----|-----|
| `DS_URL` | `jdbc:postgresql://postgres-service:5432/exam2` |
| `KAFKA_BOOTSTRAP_SERVERS` | `kafka-service:9092` |
| `ZIPKIN_URL` | `http://zipkin:9411` |
| `TRACING_SAMPLING` | `0.1` (10%) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000` |

### 서비스별 K8s 구성

| 서비스 | Replicas | HPA (max) | PDB |
|--------|----------|-----------|-----|
| api-gateway | 2 | — | minAvailable 1 |
| auth-service | 2 | 10 | minAvailable 1 |
| user-service | 2 | 10 | — |
| subject-service | 2 | 10 | — |
| question-service | 2 | 10 | — |
| exam-paper-service | 2 | 10 | minAvailable 1 |
| exam-runtime-service | 3 | 10 | minAvailable 2 |
| report-service | 2 | 8 | minAvailable 1 |

---

## 8. 관측 가능성 (Observability)

### 분산 트레이싱
- **Zipkin** 연동 — `ZIPKIN_URL` 환경변수로 엔드포인트 설정
- 샘플링 비율 `TRACING_SAMPLING` (기본 로컬: `1.0`, 운영: `0.1`)
- 모든 HTTP 요청에 `traceId` / `spanId` 자동 주입

### 메트릭
- Prometheus 스크래핑: `/actuator/prometheus` (전체 서비스)
- K8s Pod 어노테이션: `prometheus.io/scrape: "true"` 자동 설정

### Health Check
- Liveness: `/actuator/health/liveness`
- Readiness: `/actuator/health/readiness`
- K8s Probe로 자동 연결

---

## 9. 보안

- **JWT**: HMAC-SHA256, 만료 24시간, `JWT_SECRET` 환경변수로 주입
- **게이트웨이 JWT 필터**: 공개 라우트(`/auth/**`)를 제외한 모든 요청 검증
- **CORS**: `CORS_ALLOWED_ORIGINS` 환경변수 (와일드카드 금지)
- **K8s Secret**: DB·Redis·JWT 자격증명 분리 (`deploy/k8s/02-secrets.yaml`)
- **비밀번호**: MD5 해시 저장 — 운영 전 BCrypt/Argon2 전환 권장

---

## 10. 환경 프로파일 (레거시 모놀리스 참조용)

Maven 빌드 프로파일 및 Spring 설정 파일이 분리되어 있습니다 (`src/` 하위).

| 프로파일 | 용도 |
|----------|------|
| `dev` (기본) | 로컬 개발 |
| `test` | 테스트 환경 |
| `pre` | 스테이징 |
| `prod` | 운영 |
