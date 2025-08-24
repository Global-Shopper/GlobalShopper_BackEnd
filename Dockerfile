# Stage 1: Build
FROM gradle:8.8-jdk17-alpine AS builder
WORKDIR /home/gradle/project
COPY . .
RUN gradle clean bootJar --no-daemon

# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]