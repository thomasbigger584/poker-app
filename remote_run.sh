#!/usr/bin/env bash

git checkout master

git pull origin master
git submodule update --recursive --remote

docker compose -f server/digitalocean/docker-compose.yml up --build --remove-orphans -d
