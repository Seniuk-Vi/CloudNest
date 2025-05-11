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

# Deploy kubernetes
### Upload Service
docker build -t upload-service:latest .

#### docker
docker tag upload-service:latest vitaliiseniuk/upload-service:latest

docker push vitaliiseniuk/upload-service:latest

#### k8s
kubectl  apply -f charts/upload-service/secrets.yml

kubectl  apply -f charts/upload-service/configmap.yml

kubectl  apply -f charts/upload-service/deployment.yml

kubectl  apply -f charts/upload-service/service.yml

kubectl  apply -f charts/upload-service/hpa.yml

### Compression service
docker build -t compression-worker:latest .
docker tag compression-worker:latest vitaliiseniuk/compression-worker:latest
docker push vitaliiseniuk/compression-worker:latest

### monitoring

kubectl apply -f charts/monitoring/prometheus/deployment.yml
kubectl apply -f charts/monitoring/grafana/deployment.yml
kubectl apply -f charts/monitoring/zipkin/deployment.yml
kubectl apply -f charts/monitoring/otel-collector/deployment.yml

## kubernetes commands
kubectl get pods
kubectl get services

kubectl get ingress
kubectl get hpa
kubectl logs <pod-name>
kubectl describe pod <pod-name>
kubectl describe svc <service-name>


