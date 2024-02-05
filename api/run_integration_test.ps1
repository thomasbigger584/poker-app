
$currentDirectory = Get-Location
$buildPath = Join-Path $currentDirectory "build_local_docker.ps1"
Start-Process powershell -ArgumentList "-File $buildPath" -NoNewWindow -Wait

.\mvnw.cmd test-compile failsafe:integration-test
