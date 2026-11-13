
# Use Java 8 runtime
FROM eclipse-temurin:8-jre

# Set working directory
WORKDIR /app

# Copy Spring Boot executable JAR
COPY target/gestion-station-skii-0.0.1-SNAPSHOT.jar app.jar

# Expose application port
EXPOSE 8080

# Start Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
