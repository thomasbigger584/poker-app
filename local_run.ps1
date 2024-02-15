$filePath = Join-Path $PSScriptRoot -ChildPath "api/docker-compose.yml"

docker compose -f $filePath build --no-cache
docker compose -f $filePath up --remove-orphans
