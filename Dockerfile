# Use the official Eclipse Temurin image for Java 25
FROM eclipse-temurin:25-jdk-slim

# Copy your compiled Spring Boot jar into the container
COPY target/juratech-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]