#!/bin/bash

# Variables passed from Terraform
ECS_CLUSTER_NAME="${ecs_cluster_name}"
PROJECT_NAME="${project_name}"

# Join the cluster
echo ECS_CLUSTER=$ECS_CLUSTER_NAME >> /etc/ecs/ecs.config

# Setup DuckDNS Update Script
cat << 'SCRIPT' > /usr/local/bin/update-duckdns.sh
${update_duckdns_content}
SCRIPT

chmod +x /usr/local/bin/update-duckdns.sh

# Run update and log output
/usr/local/bin/update-duckdns.sh > /var/log/duckdns.log 2>&1
cat /var/log/duckdns.log

# Cron job to update every 5 mins
echo "*/5 * * * * root /usr/local/bin/update-duckdns.sh >> /var/log/duckdns.log 2>&1" > /etc/cron.d/duckdns

# Install Certbot
amazon-linux-extras install epel -y
yum install -y certbot

# Wait a bit for DNS propagation
sleep 30

# Request Certificate with retries
# Ensure port 80 is free (it should be on fresh instance)
MAX_RETRIES=5
for ((i=1; i<=MAX_RETRIES; i++)); do
  echo "Attempt $i of $MAX_RETRIES to obtain certificate..."
  certbot certonly --standalone --non-interactive --agree-tos -m admin@$PROJECT_NAME.duckdns.org -d $PROJECT_NAME.duckdns.org
  if [ $? -eq 0 ]; then
    echo "Certificate obtained successfully."
    break
  else
    echo "Certbot failed. Retrying in 30 seconds..."
    sleep 30
  fi
done

# Create Nginx Config
mkdir -p /etc/nginx-config
cat << 'CONF' > /etc/nginx-config/default.conf
${nginx_conf_content}
CONF