# NEO4J

http://localhost:7474


# Kafka

http://localhost:3030

# Swagger

http://localhost:8080/swagger-ui.html

# Redis Insight

http://localhost:5540


# Prometheus
https://opentelemetry.io/docs/zero-code/java/spring-boot-starter/getting-started/
http://localhost:9090

# Grafana

http://localhost:3000

# Zipkin

http://localhost:9411


# run with otel agent

java -javaagent:opentelemetry-javaagent.jar  -jar upload-service/target/upload-service-0.0.1-SNAPSHOT.jar
java -javaagent:opentelemetry-javaagent.jar  -jar compression-worker/target/compression-worker-0.0.1-SNAPSHOT.jar
