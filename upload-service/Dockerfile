FROM openjdk:21-jdk
LABEL authors="VitaliiSeniuk"

COPY target/upload-service-0.0.1-SNAPSHOT.jar app.jar

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar .
ENV JAVA_TOOL_OPTIONS="-javaagent:./opentelemetry-javaagent.jar"

CMD ["java", "-javaagent:./opentelemetry-javaagent.jar", "-jar", "/app/app.jar"]
