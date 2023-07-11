#!/usr/bin/env bash

docker-compose -f server/local/docker-compose.yml up --build --remove-orphans
