# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /service
COPY --from=builder /build/target/*.jar invoice-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "invoice-service.jar"]