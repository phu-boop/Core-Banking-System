FROM eclipse-temurin:21-jre

WORKDIR /app
COPY target/api-docs-service.jar /app/api-docs-service.jar

USER 10001:10001
EXPOSE 8090

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/api-docs-service.jar"]
