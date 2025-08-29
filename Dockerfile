FROM openjdk:17-jdk-slim
ARG DEBIAN_FRONTEND=noninteractive
RUN apt-get update \
 && apt-get install -y --no-install-recommends fontconfig fonts-dejavu-core \
 && fc-cache -f -v \
 && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY build/libs/GlobalShopper-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]