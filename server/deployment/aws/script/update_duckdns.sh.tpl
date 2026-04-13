#!/bin/bash
echo "Updating DuckDNS..."
curl -s "https://www.duckdns.org/update?domains=${project_name}&token=${duckdns_token}&ip="