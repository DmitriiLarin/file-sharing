FROM openjdk:17-jdk-slim

WORKDIR /app

COPY src/main/resources/static /app/src/main/resources/static
COPY target/File-sharing-0.0.1-SNAPSHOT.jar /app/app.jar

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/forall
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=503812

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
