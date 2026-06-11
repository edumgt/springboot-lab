FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY shared/common-lib shared/common-lib
COPY shared/security-lib shared/security-lib
COPY services/exam-paper-service services/exam-paper-service
RUN mvn -f services/exam-paper-service/pom.xml -pl services/exam-paper-service -am -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/services/exam-paper-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
