docker compose -f api/docker-compose.yml build --no-cache
docker compose -f api/docker-compose.yml up --remove-orphans
