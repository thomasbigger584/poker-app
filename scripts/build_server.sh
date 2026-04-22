#!/bin/bash
set -e
cd "$(dirname "$0")/.." || exit 1

docker compose -f server/docker-compose.yml build keycloak rabbitmq api
