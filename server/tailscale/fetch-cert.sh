#!/bin/sh
set -e

# Configuration
TAILSCALE_CONTAINER="tailscale"
DOMAIN="poker-app.taila8b6c7.ts.net"
CERT_DIR="/etc/nginx/certs"
CERT_FILE="${CERT_DIR}/fullchain.pem"
KEY_FILE="${CERT_DIR}/privkey.pem"

# Install docker-cli if not present (assuming Alpine environment)
if ! command -v docker >/dev/null 2>&1; then
    echo "Installing docker-cli..."
    apk add --no-cache docker-cli
fi

# Check if Tailscale container is running
if ! docker ps --format '{{.Names}}' | grep -q "^${TAILSCALE_CONTAINER}$"; then
    echo "Error: Container '${TAILSCALE_CONTAINER}' is not running."
    exit 1
fi

echo "Waiting for Tailscale to be ready..."
RETRIES=0
while ! docker exec "${TAILSCALE_CONTAINER}" tailscale status --json | grep -q '"BackendState":.*"Running"'; do
    RETRIES=$((RETRIES+1))
    if [ "$RETRIES" -gt 30 ]; then
        echo "Error: Tailscale container did not reach 'Running' state."
        exit 1
    fi
    echo "Waiting for Tailscale backend state 'Running'... ($RETRIES/30)"
    sleep 2
done

echo "Fetching Tailscale certificate for ${DOMAIN}..."

# Run tailscale cert command inside the container
timeout 60 docker exec "${TAILSCALE_CONTAINER}" tailscale cert \
    --cert-file "${CERT_FILE}" \
    --key-file "${KEY_FILE}" \
    "${DOMAIN}" < /dev/null

# Log the contents of the tailscale_certs volume
echo "Contents of volume tailscale_certs:"
docker run --rm -v tailscale_certs:/data alpine ls -la /data

echo "Certificate updated successfully."
