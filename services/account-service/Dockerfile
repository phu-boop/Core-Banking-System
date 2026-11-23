FROM eclipse-temurin:21-jre

WORKDIR /app
COPY target/account-service.jar /app/account-service.jar

USER 10001:10001
EXPOSE 8085

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/account-service.jar"]
