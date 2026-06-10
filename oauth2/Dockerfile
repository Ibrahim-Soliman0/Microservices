FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl
COPY --from=build /build/target/oauth2-*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app.jar"]
