#!/bin/bash
set -e
cd "$(dirname "$0")/.." || exit 1

# Ensure tailscale_certs volume exists
if ! docker volume inspect tailscale_certs >/dev/null 2>&1; then
    docker volume create tailscale_certs
fi

docker compose -f server/docker-compose.yml down --remove-orphans --volumes
docker system prune --all --force
docker compose -f server/docker-compose.yml up --build
