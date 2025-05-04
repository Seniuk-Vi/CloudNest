helm install neo4j -f ./charts/neo4j/neo4j-values.yaml  oci://registry-1.docker.io/bitnamicharts/neo4j

kubectl port-forward --namespace default svc/neo4j 7474:7474


helm uninstall neo4j
