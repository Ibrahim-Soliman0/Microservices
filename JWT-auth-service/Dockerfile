FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B -Dmaven.test.skip=true
COPY src src
RUN mvn package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl
COPY --from=build /build/target/JWT-auth-service-*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app.jar"]
