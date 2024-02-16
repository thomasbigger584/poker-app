$filePath = Join-Path $PSScriptRoot -ChildPath "Dockerfile.local"

cd $PSScriptRoot
docker build -t com.twb.pokerapp/api:latest -f $filePath .
