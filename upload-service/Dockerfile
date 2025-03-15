FROM openjdk:17-jdk-alpine
LABEL authors="VitaliiSeniuk"

COPY target/upload-service-0.0.1-SNAPSHOT.jar app.jar


ENTRYPOINT ["java","-jar","/app.jar"]