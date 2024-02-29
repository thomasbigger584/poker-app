#!/usr/bin/env python3
from bootstrap.utils import get_docker_client, API_FOLDER_NAME


def main():
    docker = get_docker_client()
    docker.compose.build(
        services=[API_FOLDER_NAME],
        cache=False,
    )


if __name__ == "__main__":
    main()
