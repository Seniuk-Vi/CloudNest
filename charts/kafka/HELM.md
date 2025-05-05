kubectl get secret my-release-kafka-user-passwords --namespace default -o jsonpath="{.data.client-passwords}" > encoded.txt

helm install my-kafka-cluster -f ./charts/kafka/kraft-values.yaml oci://registry-1.docker.io/bitnamicharts/kafka

helm delete my-kafka-cluster

kubectl apply -f charts/kafka/deployment-ui.yml
kubectl delete -f charts/kafka/deployment-ui.yml


kubectl get svc --namespace default -l "app.kubernetes.io/instance=my-kafka-cluster,app.kubernetes.io/component=kafka,pod" -o jsonpath='{.items[*].status.loadBalancer.ingress[0].ip}' | % { $_ -replace " ", "`n" }

kubectl get pods -n default

kubectl delete pvc -l app.kubernetes.io/instance=my-kafka-cluster  