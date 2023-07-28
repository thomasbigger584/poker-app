#!/usr/bin/env bash

git pull --recursive

docker compose -f server/digitalocean/docker-compose.yml up --build --remove-orphans -d
