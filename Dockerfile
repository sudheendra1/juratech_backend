# Use the official Eclipse Temurin image for Java 17
FROM eclipse-temurin:17-jdk-alpine

# Copy your compiled Spring Boot jar into the container
COPY target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]