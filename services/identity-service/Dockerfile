FROM eclipse-temurin:21-jre

WORKDIR /app
COPY target/identity-service.jar /app/identity-service.jar

USER 10001:10001
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/identity-service.jar"]
