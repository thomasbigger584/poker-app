#!/usr/bin/env bash

docker compose -f server/digitalocean/docker-compose.yml up --build --remove-orphans -d
