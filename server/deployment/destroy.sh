#!/bin/bash
set -e

PROFILE="local"

while [[ $# -gt 0 ]]; do
  case $1 in
    -p|--profile)
      PROFILE="$2"
      shift # past argument
      shift # past value
      ;;
    *)
      echo "Unknown option $1"
      exit 1
      ;;
  esac
done

if [ "$PROFILE" = "aws" ]; then
    SECRETS_FILE="$(dirname "$0")/../env/.secrets.env"
    if [ ! -f "$SECRETS_FILE" ]; then
        echo "Error: Secrets file not found at $SECRETS_FILE"
        exit 1
    fi
    set -a
    # shellcheck source=../env/.secrets.env
    source "$SECRETS_FILE"
    set +a
fi

cd "$(dirname "$0")/$PROFILE"

terraform destroy --auto-approve
