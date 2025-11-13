FROM openjdk:24
COPY target/*.jar /app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/app.jar"]