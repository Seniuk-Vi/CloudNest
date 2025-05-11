# Upload Service
```cmd
helm install upload-service ./charts/common-chart -f ./charts/common-chart/values.yaml
helm uninstall upload-service
``` 

# Compression Service
```cmd
helm install compression-service ./charts/common-chart -f ./charts/common-chart/values-compression.yaml
helm uninstall compression-service
``` 