$ErrorActionPreference = "Stop"
Set-Location -Path "$PSScriptRoot\.."

if (-not (docker volume ls -q -f name=^tailscale_certs$)) {
    docker volume create tailscale_certs
}

docker compose -f server/docker-compose.yml down --remove-orphans --volumes
docker system prune --all --force
docker compose -f server/docker-compose.yml up --build
