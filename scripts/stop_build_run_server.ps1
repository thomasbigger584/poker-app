$ErrorActionPreference = "Stop"
Set-Location -Path "$PSScriptRoot\.."

docker compose -f server/docker-compose.yml down --remove-orphans
docker compose -f server/docker-compose.yml up --build
