#!/usr/bin/env bash

set -e

# Colors
RESET='\033[0m'
BOLD='\033[1m'
BLUE='\033[34m'
GREEN='\033[32m'
YELLOW='\033[33m'
RED='\033[31m'
CYAN='\033[36m'

info() {
  echo -e "${BLUE}==>${RESET} $1"
}

success() {
  echo -e "${GREEN}✔${RESET} $1"
}

warning() {
  echo -e "${YELLOW}⚠${RESET} $1"
}

error() {
  echo -e "${RED}✖${RESET} $1"
}

title() {
  echo
  echo -e "${BOLD}${CYAN}$1${RESET}"
  echo
}

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
USER_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

IMAGE="flickpay/user-service:local"
CLUSTER="flickpay"
SERVICE="user-service"
INGRESS_CONTROLLER_MANIFEST="https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.12.1/deploy/static/provider/baremetal/deploy.yaml"

title "FlickPay - User Service"

info "Building Docker image..."
docker build \
  -t "$IMAGE" \
  "$USER_DIR/app"

success "Docker image built: $IMAGE"

info "Loading image into Kind..."
kind load docker-image "$IMAGE" --name "$CLUSTER"

success "Image loaded into Kind"

info "Installing ingress-nginx controller..."
kubectl apply -f "$INGRESS_CONTROLLER_MANIFEST"
kubectl rollout status \
  deployment/ingress-nginx-controller \
  -n ingress-nginx

success "Ingress controller is ready"

info "Applying Kubernetes manifests..."
kubectl apply -f "$USER_DIR/../../infrastructure/kubernetes/base/user-service"

success "Kubernetes manifests applied"

info "Restarting $SERVICE deployment..."
kubectl rollout restart deployment/"$SERVICE"

success "Pods restarted"

info "Waiting for rollout..."
kubectl rollout status \
  deployment/"$SERVICE"

success "Deployment is ready"

info "Current pods:"
kubectl get pods -l app="$SERVICE"

info "Starting port-forward through Ingress..."

kubectl port-forward \
  -n ingress-nginx \
  service/ingress-nginx-controller \
  3000:80 &
API_PID=$!

kubectl port-forward svc/user-db 5433:5432 &
DB_PID=$!

trap 'kill $API_PID $DB_PID 2>/dev/null || true' EXIT

echo
echo -e "${GREEN}${BOLD}✔ User Service${RESET}"
echo -e "  ${CYAN}http://localhost:3000${RESET}"

echo
echo -e "${GREEN}${BOLD}✔ PostgreSQL${RESET}"
echo -e "  ${CYAN}localhost:5433${RESET}"

echo
echo -e "${YELLOW}Press Ctrl+C to stop port-forwarding.${RESET}"
echo

wait
