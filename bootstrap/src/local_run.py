#!/usr/bin/env python3
from bootstrap.src.docker import get_docker_client


def main():
    docker = get_docker_client()
    docker.compose.build(cache=False)
    docker.compose.up(remove_orphans=True)


if __name__ == "__main__":
    main()
