# Stage 1: Build app
FROM gradle:8.2dock.1-jdk17 AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN ./gradlew build -x test

# Stage 2: Run app
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]