FROM eclipse-temurin:21-jre

WORKDIR /app
COPY target/notification-service.jar /app/notification-service.jar

USER 10001:10001
EXPOSE 8088

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/notification-service.jar"]
