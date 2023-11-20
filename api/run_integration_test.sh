#!/usr/bin/env bash

./build_local_docker.sh
./mvnw test-compile failsafe:integration-test
