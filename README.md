## 2024-12-17

## java -jar -Dspring.profiles.active=test  exam-1.0.0.jar

# postgresql 의 pk auto increment 대신 nextval('t_text_content_id_seq'::regclass) 부분 처리

## 아래와 같이 실행행
## java -jar "-Dspring.profiles.active=test"  C:\SpringBoot_Proj\exam\source\exam\target\exam-1.0.0.jar
## test 패키지 없을 경우 오류

## logback-spring.xml 에 의해 LOG 에 오류 생성

## session - redis 설정 주의

## LOG 파일로 Booting 정보 넘어가므로, Spring Boot 이후 화면 없음

## spring session 항목 주의