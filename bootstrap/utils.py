#!/usr/bin/env python3
import os

from python_on_whales import DockerClient

DOCKER_LOG_LEVEL = "error"
PROJECT_NAME = "poker-app"
API_FOLDER_NAME = "api"
LOCAL_DOCKERFILE = "Dockerfile.local"


def get_docker_client():
    return DockerClient(
        log_level=DOCKER_LOG_LEVEL,
        compose_project_directory=get_api_path(),
        compose_project_name=PROJECT_NAME
    )


def get_api_path():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    return os.path.join(script_dir, "..", API_FOLDER_NAME)


def get_dockerfile_local():
    api_path = get_api_path()
    return os.path.join(api_path, LOCAL_DOCKERFILE)
