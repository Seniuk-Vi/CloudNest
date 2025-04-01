FROM openjdk:21-jdk
LABEL authors="VitaliiSeniuk"

COPY target/compression-worker-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_OPTS="-Dotel.java.global-autoconfigure.enabled=true -Dotel.service.name=compression-worker"

ENTRYPOINT ["java $JAVA_OPTS","-jar","/app.jar"]