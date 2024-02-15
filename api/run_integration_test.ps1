$filePath = Join-Path $PSScriptRoot -ChildPath "build_local_docker.ps1"
Start-Process powershell -ArgumentList "-File $filePath" -NoNewWindow -Wait

.\mvnw.cmd test-compile failsafe:integration-test
