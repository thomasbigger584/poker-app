#!/usr/bin/env bash

docker-compose -f server/docker-compose.yml up --build --remove-orphans
