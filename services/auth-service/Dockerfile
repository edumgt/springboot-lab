FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY shared/common-lib shared/common-lib
COPY shared/security-lib shared/security-lib
COPY services/auth-service services/auth-service
RUN mvn -f services/auth-service/pom.xml -pl services/auth-service -am -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/services/auth-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
