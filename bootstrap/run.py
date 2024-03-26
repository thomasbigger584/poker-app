#!/usr/bin/env python3
import argparse
import traceback
from utils import get_docker_client


def parse_arguments():
    parser = argparse.ArgumentParser(description='Bootstrap poker-app Server')
    parser.add_argument('--no-build', action='store_true', default=False, help='Skip building Docker images')
    parser.add_argument('--no-cache', action='store_true', default=False, help='Build without cache')
    parser.add_argument('--no-run', action='store_true', default=False, help='Skip running Docker containers')
    return parser.parse_args()


def build(args, docker):
    if not args.no_build:
        try:
            docker.compose.build(cache=args.no_cache)
        except Exception as e:
            print("An error occurred while building Docker images")
            traceback.print_exc()
            docker.compose.down()


def up(args, docker):
    if not args.no_run:
        try:
            docker.compose.up(remove_orphans=True)
        except Exception as e:
            print("An error occurred while running docker containers")
            traceback.print_exc()
            docker.compose.down()


def main():
    args = parse_arguments()
    docker = get_docker_client()
    build(args, docker)
    up(args, docker)


if __name__ == "__main__":
    main()
