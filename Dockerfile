FROM maven:3.8.5-openjdk-17 AS builder

LABEL authors="halik"
WORKDIR /opt/app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline
COPY src src
RUN ./mvnw clean install

FROM eclipse-temurin:17-jre-alpine

COPY --from=builder /opt/app/target/*.jar ./app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]