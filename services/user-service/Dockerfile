FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY shared/common-lib shared/common-lib
COPY shared/security-lib shared/security-lib
COPY services/user-service services/user-service
RUN mvn -f services/user-service/pom.xml -pl services/user-service -am -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/services/user-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
