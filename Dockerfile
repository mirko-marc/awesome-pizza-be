FROM maven:3.9.11-eclipse-temurin-21-alpine AS builder

WORKDIR /workspace
COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
RUN mkdir -p /app/logs && chown -R spring:spring /app

COPY --from=builder --chown=spring:spring /workspace/target/*.jar /app/awesome-pizza.jar

USER spring:spring
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/awesome-pizza.jar"]
