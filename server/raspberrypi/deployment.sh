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

if [ -z "$REAL_USER" ] || [ -z "$REAL_HOME" ]; then
  echo "❌ Could not resolve the invoking user/home. Run via: sudo bash deployment.sh"
  exit 1
fi

REPO_DIR="$REAL_HOME/poker-app"
SERVER_DIR="$REPO_DIR/server"
ENV_FILE="$SERVER_DIR/env/.secrets.env"
CONFIG_FILE="$SERVER_DIR/raspberrypi/deploy.config"
DB_VOLUME="poker-app_postgres_data"
TS_REGEX="^poker-app"
TS_TAILNET="dinosaur-emperor.ts.net"
WORKER_SCRIPT="$REAL_HOME/startup-task.sh"
SERVICE_NAME="poker-app.service"
LOG_FILE="$REAL_HOME/poker-deploy.log"
VERSION_FILE="$REAL_HOME/.poker-deploy-version"

# --- PREREQUISITES ---
# Debian-based only (Raspbian is a Debian flavour) for stability. Installs only
# what's missing, so re-runs are cheap. Runs here in the root setup phase — the
# generated worker runs as $REAL_USER and can't apt-get.
ensure_prerequisites() {
  echo "🔧 Checking prerequisites..."

  if ! command -v apt-get >/dev/null 2>&1; then
    echo "❌ Unsupported OS: this script targets Debian-based systems only (apt-get not found)."
    exit 1
  fi

  export DEBIAN_FRONTEND=noninteractive

  # CLI tools the worker relies on (all in the default Debian repos). Images are
  # built in CI, so the Pi needs no build/web tooling — just git (pull configs),
  # curl + jq (Tailscale API cleanup) and Docker (below).
  REQUIRED_PKGS=(git curl jq ca-certificates)
  MISSING_PKGS=()
  for pkg in "${REQUIRED_PKGS[@]}"; do
    dpkg -s "$pkg" >/dev/null 2>&1 || MISSING_PKGS+=("$pkg")
  done
  if [ ${#MISSING_PKGS[@]} -gt 0 ]; then
    echo "📦 Installing missing packages: ${MISSING_PKGS[*]}"
    apt-get update -y
    apt-get install -y --no-install-recommends "${MISSING_PKGS[@]}"
  else
    echo "✅ Base packages present: ${REQUIRED_PKGS[*]}"
  fi

  # Docker Engine — install via Docker's official script only if absent.
  if ! command -v docker >/dev/null 2>&1; then
    echo "🐳 Docker not found. Installing via the official get.docker.com script..."
    curl -fsSL https://get.docker.com | sh
  else
    echo "✅ Docker present: $(docker --version)"
  fi

  # Compose v2 plugin (`docker compose ...`). get.docker.com bundles it; this
  # covers a pre-existing Docker installed without the plugin.
  if ! docker compose version >/dev/null 2>&1; then
    echo "📦 Installing docker-compose-plugin..."
    apt-get install -y docker-compose-plugin \
      || { echo "❌ Could not install the Docker Compose v2 plugin — install it manually."; exit 1; }
  else
    echo "✅ Docker Compose present: $(docker compose version | head -n1)"
  fi

  # Make sure Docker is up and the deploy user can reach it (the systemd unit
  # below runs with Group=docker).
  systemctl enable --now docker >/dev/null 2>&1 || true
  if ! id -nG "$REAL_USER" | grep -qw docker; then
    echo "👤 Adding $REAL_USER to the 'docker' group..."
    usermod -aG docker "$REAL_USER"
  fi

  echo "✅ All prerequisites satisfied."
}
ensure_prerequisites

echo "🛠️ Orchestrating setup for $REAL_USER..."

# 2. Cleanup old service (Idempotency)
if systemctl list-unit-files | grep -q "$SERVICE_NAME"; then
    echo "♻️ Removing existing service..."
    systemctl stop "$SERVICE_NAME" 2>/dev/null || true
    systemctl disable "$SERVICE_NAME" 2>/dev/null || true
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

# Version Tracking
CURRENT_HASH=\$(git rev-parse HEAD)
if [ -f "$VERSION_FILE" ]; then
    DEPLOYED_HASH=\$(cat "$VERSION_FILE")
else
    DEPLOYED_HASH="none"
fi

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

# Ensure persistent Tailscale volume exists
VOLUME_NAME="tailscale_certs"
if ! docker volume inspect "\$VOLUME_NAME" >/dev/null 2>&1; then
    echo "📦 Volume '\$VOLUME_NAME' not found. Creating..."
    docker volume create "\$VOLUME_NAME"
else
    echo "✅ Volume '\$VOLUME_NAME' already exists. Skipping creation."
fi

# Docker Deploy
cd "$SERVER_DIR" || exit 1
set +e
docker compose down --remove-orphans

# Optionally wipe the Postgres data volume before starting (controlled by deploy.config)
WIPE_DB_DATA=0
if [ -f "$CONFIG_FILE" ]; then
    source "$CONFIG_FILE"
fi
if [ "\$WIPE_DB_DATA" == "1" ]; then
    echo "🧹 WIPE_DB_DATA=1 -> wiping Postgres volume '$DB_VOLUME' for a fresh database..."
    docker volume rm "$DB_VOLUME" 2>/dev/null || true
else
    echo "💾 WIPE_DB_DATA=\$WIPE_DB_DATA -> keeping existing Postgres volume '$DB_VOLUME'."
fi

# Images (api, nginx, keycloak, rabbitmq) are built on a native ARM64 GitHub
# runner and pushed straight into this box's Docker (docker save | ssh docker
# load over Tailscale) BEFORE this script runs — the Pi never builds anything.
# --no-build makes a missing image a hard error instead of silently building
# here; public images (postgres/pgadmin/tailscale) are still pulled as needed.
if [ "\$CURRENT_HASH" == "\$DEPLOYED_HASH" ]; then
    echo "⏩ No code change since last deploy (\$CURRENT_HASH)."
else
    echo "📦 Code updated (\$DEPLOYED_HASH -> \$CURRENT_HASH)."
fi
# docker load (run by CI just before this script) re-points each image tag to
# the newly pushed image, so the old one becomes dangling. --force-recreate then
# rebuilds every container from those current tags, guaranteeing a stale
# container can't keep running an old image. The dangling old images are cleaned
# up by the 'docker system prune -f' below.
echo "🚀 Starting stack with prebuilt images (no build on the Pi)..."
docker compose up -d --no-build --force-recreate

EXIT_CODE=\$?
set -e

if [ \$EXIT_CODE -ne 0 ]; then
    echo "❌ Error: Docker failed."
    exit \$EXIT_CODE
fi

# Update deployed version hash
echo "\$CURRENT_HASH" > "$VERSION_FILE"

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
