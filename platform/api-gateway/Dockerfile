FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY shared/common-lib shared/common-lib
COPY shared/security-lib shared/security-lib
COPY platform/api-gateway platform/api-gateway
RUN mvn -f platform/api-gateway/pom.xml -pl platform/api-gateway -am -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/platform/api-gateway/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
