# Stage 1: Build the application
FROM gradle:jdk17-jammy AS build
WORKDIR /home/gradle/src
COPY . .
RUN gradle build -x test --no-daemon

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-jammy

# Expose the application port
EXPOSE 8080

# Set the working directory
WORKDIR /app

# Copy the specific built JAR file from the build stage
COPY --from=build /home/gradle/src/build/libs/internhub-0.0.1-SNAPSHOT.jar /app/spring-boot-application.jar

# Run the application with optimized JVM settings
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/spring-boot-application.jar"]