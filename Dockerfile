FROM maven:3.9.11-eclipse-temurin-21-alpine AS build
COPY pom.xml .
COPY srs ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
COPY --from=build-jar /target/*.jar /app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/app.jar"]