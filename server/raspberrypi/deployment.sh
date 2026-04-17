#!/bin/bash

# --- CONFIGURATION ---
REPO_DIR="/home/tbigg/poker-app"
SERVER_DIR="$REPO_DIR/server"
ENV_FILE="$SERVER_DIR/api/env/.secrets.env"
TS_REGEX="^poker-app(-[0-9]+)?$"
TS_TAILNET="taila8b6c7.ts.net"
WORKER_SCRIPT="/home/tbigg/startup-task.sh"
SERVICE_NAME="poker-app.service"
LOG_FILE="/home/tbigg/poker-deploy.log"

echo "🔐 Updating setup with SD-card optimized logic..."

# 1. Cleanup old service
if systemctl list-unit-files | grep -q "$SERVICE_NAME"; then
    sudo systemctl stop "$SERVICE_NAME" 2>/dev/null
    sudo systemctl disable "$SERVICE_NAME" 2>/dev/null
    sudo rm -f "/etc/systemd/system/$SERVICE_NAME"
fi

# 2. CREATE WORKER SCRIPT
cat <<EOF > "$WORKER_SCRIPT"
#!/bin/bash
# Keep log file size manageable for SD card
tail -n 1000 "$LOG_FILE" > "$LOG_FILE.tmp" 2>/dev/null && mv "$LOG_FILE.tmp" "$LOG_FILE" 2>/dev/null
exec > >(tee -a "$LOG_FILE") 2>&1

echo "🚀 Execution started: \$(date)"

# Check dependencies
for cmd in curl jq docker; do
    if ! command -v \$cmd &> /dev/null; then
        echo "❌ Error: \$cmd is not installed."
        exit 1
    fi
done

# Update Repo with Retries (SD cards can be slow at boot)
cd "$REPO_DIR" || exit 1
MAX_RETRIES=5
RETRY_COUNT=0
until git fetch --all || [ \$RETRY_COUNT -eq \$MAX_RETRIES ]; do
    echo "Waiting for network... (\$((++RETRY_COUNT))/\$MAX_RETRIES)"
    sleep 5
done

git reset --hard origin/master

# --- SOURCE THE ENV FILE ---
if [ -f "$ENV_FILE" ]; then
    set -a
    source "$ENV_FILE"
    set +a
else
    echo "❌ Error: .env file not found at $ENV_FILE"
    exit 1
fi

# Tailscale API Regex Cleanup
if [ -n "\$TS_API_KEY" ]; then
    DEVICE_IDS=\$(curl -s -u "\$TS_API_KEY:" "https://api.tailscale.com/api/v2/tailnet/$TS_TAILNET/devices" | \\
                jq -r ".devices[] | select(.name | test(\"\$TS_REGEX\")) | .id")

    if [ -n "\$DEVICE_IDS" ] && [ "\$DEVICE_IDS" != "null" ]; then
        for ID in \$DEVICE_IDS; do
            echo "🗑️ Deleting Tailscale machine ID: \$ID"
            curl -s -X DELETE -u "\$TS_API_KEY:" "https://api.tailscale.com/api/v2/device/\$ID"
        done
    fi
fi

# Docker Deploy
cd "$SERVER_DIR" || exit 1
echo "📦 Pulling and Starting Containers..."
docker compose pull
# Removed --force-recreate to save SD card writes; it will only update changed containers
docker compose up --build -d
docker compose down --remove-orphans

# SD Card Cleanup: Aggressively remove unused build cache and images
echo "🧹 Cleaning up SD card space..."
docker system prune -f

# Force SD card write sync
sync

echo "✅ Deployment finished: \$(date)"
EOF

chmod +x "$WORKER_SCRIPT"

# 3. SYSTEMD SERVICE
sudo cat <<EOF > "/etc/systemd/system/$SERVICE_NAME"
[Unit]
Description=Automated Poker App Deployment
After=network-online.target tailscaled.service
Wants=network-online.target tailscaled.service

[Service]
Type=oneshot
User=$USER
Group=docker
WorkingDirectory=$REPO_DIR
EnvironmentFile=-$ENV_FILE
ExecStart=$WORKER_SCRIPT
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
EOF

# 4. START
sudo systemctl daemon-reload
sudo systemctl enable "$SERVICE_NAME"

if [ -f "$ENV_FILE" ]; then
    sudo systemctl start "$SERVICE_NAME"
    echo "✨ Service started. Log: $LOG_FILE"
else
    echo "⚠️ Setup complete, but .env is missing at $ENV_FILE"
fi