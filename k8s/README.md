# Kubernetes manifests

Namespace `payments` with Postgres, Kafka (single node, KRaft) and the three services.

```powershell
# 1. build the images locally (from the repo root)
docker build -t payment-service:local ./payment-service
docker build -t notification-service:local ./notification-service
docker build -t ledger-service:local ./ledger-service

# 2. deploy everything
kubectl apply -f k8s/

# 3. watch pods come up
kubectl get pods -n payments -w

# 4. call the services
kubectl port-forward -n payments svc/payment-service 8082:8080
kubectl port-forward -n payments svc/ledger-service 8083:8080
```

Remove everything: `kubectl delete namespace payments`
