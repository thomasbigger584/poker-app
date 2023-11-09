#!/usr/bin/env bash
trap ctrl_c INT # trap ctrl-c and call ctrl_c()

function ctrl_c() {
  docker compose -f api/docker-compose.yml rm --force
}

docker compose -f api/docker-compose.yml build --no-cache
docker compose -f api/docker-compose.yml up --remove-orphans
