#!/bin/bash
set -e
source "$(dirname "$0")/setup.sh"

terraform init
terraform apply --auto-approve
