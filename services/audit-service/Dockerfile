FROM eclipse-temurin:21-jre

WORKDIR /app
COPY target/audit-service.jar /app/audit-service.jar

USER 10001:10001
EXPOSE 8087

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/audit-service.jar"]
