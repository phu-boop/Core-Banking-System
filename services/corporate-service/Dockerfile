FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/corporate-service.jar /app/corporate-service.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/corporate-service.jar"]
