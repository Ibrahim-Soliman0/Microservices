FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests -B


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/user-service-*.jar app.jar
EXPOSE 7071

ENTRYPOINT ["java", "-jar", "app.jar"]