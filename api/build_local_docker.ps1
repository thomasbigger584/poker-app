$filePath = Join-Path $PSScriptRoot -ChildPath "Dockerfile.local"

docker build -t com.twb.pokerapp/api:latest -f $filePath .
