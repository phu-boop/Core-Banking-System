FROM eclipse-temurin:21-jre

WORKDIR /app
COPY target/payment-service.jar /app/payment-service.jar

USER 10001:10001
EXPOSE 8086

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/payment-service.jar"]
