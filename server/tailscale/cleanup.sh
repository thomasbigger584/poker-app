#!/bin/sh
set -e

PREFIX="poker-app"

if [ -z "$TS_APIKEY" ]; then
  echo "Error: TS_APIKEY environment variable is not set."
  exit 1
fi

# Install dependencies if they are missing
if ! command -v curl >/dev/null 2>&1 || ! command -v jq >/dev/null 2>&1; then
  apk add --no-cache curl jq
fi

echo "Scanning devices starting with '$PREFIX'..."
DEVICES=$(curl -f -s -u "${TS_APIKEY}:" https://api.tailscale.com/api/v2/tailnet/-/devices)

IDS=$(echo "$DEVICES" | jq -r --arg pn "$PREFIX" '.devices[] | select(.name | startswith($pn)) | .id')

if [ -z "$IDS" ]; then
  echo "No matching devices found."
  exit 0
fi

for ID in $IDS; do
  echo "Deleting device: $ID"
  curl -f -s -X DELETE -u "${TS_APIKEY}:" "https://api.tailscale.com/api/v2/device/$ID"
done
