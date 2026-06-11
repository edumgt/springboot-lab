FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY shared/common-lib shared/common-lib
COPY shared/security-lib shared/security-lib
COPY services/question-service services/question-service
RUN mvn -f services/question-service/pom.xml -pl services/question-service -am -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/services/question-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
