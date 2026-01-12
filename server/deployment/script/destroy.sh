#!/bin/bash
set -e
source "$(dirname "$0")/setup.sh"

terraform destroy --auto-approve
