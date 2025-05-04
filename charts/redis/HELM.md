helm install redis -f ./charts/redis/redis-values.yaml  oci://registry-1.docker.io/bitnamicharts/redis

kubectl get svc --namespace default -l "app.kubernetes.io/name=redis,app.kubernetes.io/instance=redis"

kubectl port-forward --namespace default svc/redis-master 6379:6379


helm uninstall redis
