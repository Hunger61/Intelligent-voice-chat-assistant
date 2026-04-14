# Multi-stage Dockerfile: build with Maven (Eclipse Temurin 21) then run on a lightweight JRE image

# 1) Build stage: use Maven with JDK 21 to compile and package the app
ARG BUILDER_IMAGE=maven:3.9.6-eclipse-temurin-21
ARG RUNTIME_IMAGE=eclipse-temurin:21-jre
FROM ${BUILDER_IMAGE} AS build
WORKDIR /workspace

# copy only pom.xml first to leverage Docker cache for dependency downloads
COPY pom.xml ./
# download dependencies (offline) to speed up rebuilds
RUN mvn -B -DskipTests dependency:go-offline

# copy source and package
COPY src ./src
RUN mvn -B -DskipTests package

# 2) Runtime stage: smaller JRE image (Eclipse Temurin 21)
FROM ${RUNTIME_IMAGE}
WORKDIR /app

# copy the fat jar produced by Spring Boot
COPY --from=build /workspace/target/*.jar app.jar

# expose the default Spring Boot port (change if your app uses another)
EXPOSE 8080

# run the Spring Boot executable jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

