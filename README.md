# postgresql 의 pk auto increment 대신 nextval('t_text_content_id_seq'::regclass) 부분 처리

## 아래와 같이 실행행
## java -jar "-Dspring.profiles.active=test"  C:\SpringBoot_Proj\exam\source\exam\target\exam-1.0.0.jar
## test 패키지 없을 경우 오류

## logback-spring.xml 에 의해 LOG 에 오류 생성

## session - redis 설정 주의

## LOG 파일로 Booting 정보 넘어가므로, Spring Boot 이후 화면 없음

## spring session 항목 주의
## 로컬 실행 가이드 (Docker + PostgreSQL + Spring Boot)

### 1) DB 컨테이너 실행
```bash
./scripts/start-db.sh
```

### 2) 초기 데이터 확인
```bash
./scripts/check-db-data.sh
```

### 3) Spring Boot 실행
```bash
./scripts/run-spring.sh
```

앱은 `dev` 프로파일 기준으로 아래 DB 접속 정보를 사용합니다.

- url: `jdbc:postgresql://127.0.0.1:5432/exam2`
- username: `postgres`
- password: `123456`

### 4) DB 중지
```bash
./scripts/stop-db.sh
```

## DB 구성
`src/main/resources/mapper/*.xml`의 테이블/컬럼 정의를 기준으로,
다음 스크립트로 스키마/샘플 데이터가 자동 생성됩니다.

- `docker/postgres-init/01_schema.sql`
- `docker/postgres-init/02_seed.sql`
