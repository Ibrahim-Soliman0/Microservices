FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
EXPOSE 8081
COPY target/oauth2-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]