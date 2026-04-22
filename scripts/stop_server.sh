#!/bin/bash
set -e
cd "$(dirname "$0")/.." || exit 1

docker compose -f server/docker-compose.yml down --remove-orphans
