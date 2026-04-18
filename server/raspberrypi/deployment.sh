#!/bin/bash
# Exit immediately if a command exits with a non-zero status
set -e

# 1. Check if script is run as root
if [ "$EUID" -ne 0 ]; then
  echo "❌ Please run as root (use sudo bash deployment.sh)"
  exit 1
fi

# --- DYNAMIC CONFIGURATION ---
REAL_USER=$SUDO_USER
REAL_HOME=$(getent passwd "$SUDO_USER" | cut -d: -f6)

REPO_DIR="$REAL_HOME/poker-app"
SERVER_DIR="$REPO_DIR/server"
ENV_FILE="$SERVER_DIR/env/.secrets.env"
TS_REGEX="^poker-app"
TS_TAILNET="taila8b6c7.ts.net"
WORKER_SCRIPT="$REAL_HOME/startup-task.sh"
SERVICE_NAME="poker-app.service"
LOG_FILE="$REAL_HOME/poker-deploy.log"

echo "🛠️ Orchestrating setup for $REAL_USER..."

# 2. Cleanup old service (Idempotency)
if systemctl list-unit-files | grep -q "$SERVICE_NAME"; then
    echo "♻️ Removing existing service..."
    systemctl stop "$SERVICE_NAME" 2>/dev/null
    systemctl disable "$SERVICE_NAME" 2>/dev/null
    rm -f "/etc/systemd/system/$SERVICE_NAME"
    systemctl daemon-reload
fi

# 3. CREATE WORKER SCRIPT
echo "📝 Generating worker script..."
cat <<EOF > "$WORKER_SCRIPT"
#!/bin/bash
set -e

# Log rotation and redirection
tail -n 1000 "$LOG_FILE" > "$LOG_FILE.tmp" 2>/dev/null && mv "$LOG_FILE.tmp" "$LOG_FILE" 2>/dev/null
exec > >(tee -a "$LOG_FILE") 2>&1

# Notification setup
NTFY_URL="ntfy.sh/raspberrypi-poker-app-584"
on_exit() {
    EXIT_STATUS=\$?
    if [ \$EXIT_STATUS -eq 0 ]; then
        curl -s -d "✅ Deployment successful! 🚀" "\$NTFY_URL" > /dev/null
    else
        curl -s -d "❌ Deployment FAILED with exit code \$EXIT_STATUS! 🛑" "\$NTFY_URL" > /dev/null
    fi
}
trap on_exit EXIT

echo "🚀 Execution started: \$(date)"

# Update Repo with Network Wait
cd "$REPO_DIR" || exit 1
MAX_RETRIES=15
RETRY_COUNT=0
until git fetch origin master || [ \$RETRY_COUNT -eq \$MAX_RETRIES ]; do
    echo "Waiting for network... (\$((++RETRY_COUNT))/\$MAX_RETRIES)"
    sleep 5
done

# Ensure we are on master and match origin exactly
git checkout -B master
git reset --hard FETCH_HEAD
git clean -fd

# Source Secrets
if [ -f "$ENV_FILE" ]; then
    set -a
    source "$ENV_FILE"
    set +a
else
    echo "❌ Error: Secrets file not found at $ENV_FILE"
    exit 1
fi

# Tailscale Regex Cleanup
if [ -n "\$TS_APIKEY" ]; then
    echo "🔍 Cleaning up Tailscale machines matching: $TS_REGEX"
    DEVICE_IDS=\$(curl -s -f -u "\$TS_APIKEY:" "https://api.tailscale.com/api/v2/tailnet/$TS_TAILNET/devices" | \\
                jq -r ".devices[] | select(.name | test(\"$TS_REGEX\")) | .id")

    if [ -n "\$DEVICE_IDS" ] && [ "\$DEVICE_IDS" != "null" ]; then
        for ID in \$DEVICE_IDS; do
            echo "🗑️ Deleting Tailscale ID: \$ID"
            curl -s -f -X DELETE -u "\$TS_APIKEY:" "https://api.tailscale.com/api/v2/device/\$ID"
        done
    fi
fi

# Docker Deploy
cd "$SERVER_DIR" || exit 1
set +e
docker compose down --remove-orphans
docker compose up --build -d
EXIT_CODE=\$?
set -e

if [ \$EXIT_CODE -ne 0 ]; then
    echo "❌ Error: Docker failed."
    exit \$EXIT_CODE
fi

# SD Card Optimization
docker system prune -f
sync

echo "✅ Deployment finished: \$(date)"
EOF

# Fix Ownership and Permissions
chmod +x "$WORKER_SCRIPT"
chown "$REAL_USER:$REAL_USER" "$WORKER_SCRIPT"
touch "$LOG_FILE"
chown "$REAL_USER:$REAL_USER" "$LOG_FILE"

# 4. SYSTEMD SERVICE
echo "⚙️ Creating systemd service..."
cat <<EOF > "/etc/systemd/system/$SERVICE_NAME"
[Unit]
Description=Automated Poker App Deployment
After=network-online.target tailscaled.service
Wants=network-online.target tailscaled.service

[Service]
Type=oneshot
User=$REAL_USER
Group=docker
WorkingDirectory=$REPO_DIR
EnvironmentFile=$ENV_FILE
ExecStart=$WORKER_SCRIPT
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
EOF

# 5. START
systemctl daemon-reload
systemctl enable "$SERVICE_NAME"

if [ -f "$ENV_FILE" ]; then
    echo "🚀 Starting deployment service..."
    systemctl start "$SERVICE_NAME"
    echo "✨ Done! Track logs here: tail -f $LOG_FILE"
else
    echo "⚠️ Setup complete. Please create $ENV_FILE to run this script."
fi
