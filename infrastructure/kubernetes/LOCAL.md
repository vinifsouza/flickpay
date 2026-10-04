## Build image
docker build -t flickpay/user-service:local services/user/app

## Load image into Kind
kind load docker-image flickpay/user-service:local --name flickpay

## Restart pods
kubectl delete pods -l app=user-service

## Follow logs
kubectl get pods -w

## Forward ports
kubectl port-forward svc/user-service 3000:8080
