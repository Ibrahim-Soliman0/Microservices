FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl
COPY --from=build /build/target/discovery-service*.jar app.jar
EXPOSE 8501
ENTRYPOINT ["java", "-jar", "/app.jar"]
