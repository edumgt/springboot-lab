# Exam BE (온라인 시험/문제 관리 백엔드)

Spring Boot + MyBatis 기반의 온라인 시험 시스템 백엔드입니다.  
관리자(문항/시험지/사용자 관리)와 학생(시험 응시/답안 조회) API를 분리해 제공하며, PostgreSQL을 주 데이터 저장소로 사용합니다.

---

## 1. 프로젝트 개요

이 프로젝트는 다음 목적을 가진 **시험 플랫폼 백엔드 API 서버**입니다.

- 과목/문항/시험지 생성 및 관리
- 학생의 시험 응시 및 채점 결과 저장
- 학생/관리자 대시보드 데이터 제공
- 세션/보안 기반 로그인 처리
- PostgreSQL 스키마 자동 초기화(Docker)

---

## 2. 기술 스택 (Tech Stack)

### Language & Runtime
- **Java 11**
- **Maven** 빌드

### Framework
- **Spring Boot 2.1.6.RELEASE**
- **Spring Web** (REST API)
- **Spring Security** (권한 기반 접근제어)
- **Spring Validation**
- **Spring Cache**
- **Undertow** (내장 웹서버)

### Data & Persistence
- **PostgreSQL 14** (로컬 Docker 구성)
- **MyBatis** (`mapper/*.xml` 기반 SQL 매핑)
- **PageHelper** (페이징)
- **HikariCP** (DB 커넥션풀)

### Session / Cache
- **Spring Session Data Redis**
- **Spring Data Redis / Jedis**
- 캐시 타입: Redis

### Utility / Infra
- **Lombok**
- **ModelMapper**
- **Logback (`logback-spring.xml`)**
- (옵션) **FastDFS Client** 의존성 포함

---

## 3. 핵심 기능 (Features)

## 3.1 사용자/인증
- 로그인 API: `POST /api/user/login`
- 로그아웃 API: `POST /api/user/logout`
- 학생 회원가입 API: `POST /api/student/user/register`
- 현재 사용자 조회(관리자/학생)
- Spring Security 기반 역할(Role) 접근제어
  - `/api/admin/**` → ADMIN
  - `/api/student/**` → STUDENT
  - `/api/teacher/**` → TEACHER

## 3.2 관리자(Admin) 기능
- **대시보드**: 통계/요약 데이터 조회
- **사용자 관리**: 목록, 상세조회, 수정/업데이트
- **과목 관리**: 목록, 페이징, 등록/수정, 상세조회
- **문항 관리**: 목록(페이지), 등록/수정, 상세조회
- **시험지 관리**: 목록(페이지), 등록/수정, 상세조회
- **업로드 API** 제공 (`/api/admin/upload/configAndUpload`)

## 3.3 학생(Student) 기능
- **대시보드** 조회
- **과목 목록/선택**
- **시험지 목록/상세** 조회
- **시험 응시 제출** (`answerSubmit`)
- **응시 이력/결과 조회**
- **문항 답안 이력 조회 및 상세 조회**

## 3.4 시험/채점 데이터 모델
주요 도메인 테이블:
- `t_user` (사용자)
- `t_subject` (과목)
- `t_question` (문항)
- `t_exam_paper` (시험지)
- `t_exam_paper_answer` (시험 응시 결과)
- `t_exam_paper_question_customer_answer` (문항별 사용자 답안)
- `t_text_content` (문항/시험지 JSON 본문)

시험지/문항의 본문은 `t_text_content`에 JSON 형태로 저장되며, 메인 엔터티는 참조 ID를 보관하는 구조입니다.

---

## 4. API 구성

### 관리자 API Prefix
- `/api/admin/dashboard`
- `/api/admin/user`
- `/api/admin/education`
- `/api/admin/question`
- `/api/admin/exam/paper`
- `/api/admin/upload`

### 학생 API Prefix
- `/api/student/dashboard`
- `/api/student/user`
- `/api/student/education`
- `/api/student/question`
- `/api/student/question/answer`
- `/api/student/exam/paper`
- `/api/student/exampaper/answer`

---

## 5. 로컬 실행 가이드 (Docker + PostgreSQL + Spring Boot)

## 5.1 DB 컨테이너 실행
```bash
./scripts/start-db.sh
```

## 5.2 초기 데이터 확인
```bash
./scripts/check-db-data.sh
```

## 5.3 Spring Boot 실행
```bash
./scripts/run-spring.sh
```

앱은 기본 `dev` 프로파일에서 다음 DB 정보를 사용합니다.
- url: `jdbc:postgresql://127.0.0.1:5432/exam2`
- username: `postgres`
- password: `123456`

## 5.4 DB 중지
```bash
./scripts/stop-db.sh
```

---

## 6. DB 초기화/시드 구성

컨테이너 시작 시 아래 스크립트가 자동 실행됩니다.

- `docker/postgres-init/01_schema.sql`: 스키마 생성
- `docker/postgres-init/02_seed.sql`: 샘플 데이터 삽입 + 시퀀스 보정

`SERIAL` PK를 사용하는 테이블의 시퀀스를 `setval`로 동기화하여, 시드 데이터 삽입 후에도 ID 충돌 없이 신규 데이터가 저장됩니다.

### 기본 시드 계정
- 관리자: `admin` / 비밀번호 해시(초기값 `admin`)
- 학생: `student1` / 비밀번호 해시(초기값 `1234`)

> 비밀번호는 MD5 해시 형태로 저장되어 있으므로 운영 환경에서는 반드시 강한 해시 정책(BCrypt/Argon2)으로 교체하세요.

---

## 7. 환경 프로파일

Maven profile 및 Spring 설정 파일이 분리되어 있습니다.

- `dev` (기본)
- `test`
- `pre`
- `prod`

관련 파일:
- `src/main/resources/application.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-test.yml`
- `src/main/resources/application-pre.yml`
- `src/main/resources/application-prod.yml`

---

## 8. 로깅/운영 참고

- 로깅 설정: `logback-spring.xml`
- server.port 기본값: `8001`
- 압축 전송 활성화(compression enabled)
- Undertow 튜닝 값(io threads / worker threads) 적용

---

## 9. 프로젝트 구조

```text
src/main/java/com/alvis/exam
├── controller        # admin/student REST API
├── service           # 비즈니스 로직
├── repository        # MyBatis Mapper 인터페이스
├── domain            # 엔티티/Enum
├── configuration     # 보안, 스프링 설정, 프로퍼티
└── utility           # 공통 유틸

src/main/resources
├── mapper            # MyBatis XML SQL
├── application*.yml  # 환경별 설정
└── logback-spring.xml
```

---

## 10. 개선 권장사항

- Spring Boot / Spring Security 버전 업그레이드(장기지원 버전)
- 테스트 자동화 활성화(`maven-surefire-plugin`의 `skipTests` 해제)
- 비밀번호 해시 강화(MD5 → BCrypt/Argon2)
- Redis 세션/캐시 사용 여부를 환경별로 명확하게 분리
- API 문서화(OpenAPI/Swagger) 추가
