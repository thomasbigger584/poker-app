#!/usr/bin/env python3
import os
from python_on_whales import DockerClient


def main():
    docker = get_docker_client()

    docker.compose.build(cache=False)
    docker.compose.up(remove_orphans=True)


def get_docker_client():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    api_dir = os.path.join(script_dir, "..", "..", "api")
    docker = DockerClient(
        log_level='error',
        compose_project_directory=api_dir,
        compose_project_name="poker-app"
    )
    return docker


if __name__ == "__main__":
    main()
