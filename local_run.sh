#!/usr/bin/env bash
trap ctrl_c INT # trap ctrl-c and call ctrl_c()

function ctrl_c() {
  docker-compose -f server/api/docker-compose.yml rm --force
}

docker-compose -f server/api/docker-compose.yml up --build --remove-orphans
