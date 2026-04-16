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

# Convert the uber jar into a layered layout so docker can reuse unchanged layers
# across deploys (reduces `docker pull` time, especially for large application layers).
# See: Spring Boot "efficient Docker images" layering.
RUN cp /workspace/target/*.jar /workspace/application.jar
RUN java -Djarmode=tools -jar /workspace/application.jar extract --layers --destination extracted

# 2) Runtime stage: smaller JRE image (Eclipse Temurin 21)
FROM ${RUNTIME_IMAGE}
WORKDIR /application

# Copy each extracted layer as a separate docker layer.
# Docker can then only pull layers that changed between deploys.
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

# expose the default Spring Boot port (change if your app uses another)
EXPOSE 8080

# Start the (smaller) application jar produced by the extraction step.
ENTRYPOINT ["java", "-jar", "application.jar"]

