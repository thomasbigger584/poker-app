# Set execution policy for the current process only
if ((Get-ExecutionPolicy) -ne 'Bypass') {
    Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process -Force
}

$ErrorActionPreference = "Stop"
$PROFILE_VAL = "local"

# Handle arguments using the native PowerShell switch statement
if ($args.Count -gt 0) {
    for ($i = 0; $i -lt $args.Count; $i++) {
        switch ($args[$i]) {
            { $_ -eq "-p" -or $_ -eq "--profile" } {
                $PROFILE_VAL = $args[$i + 1]
                $i++ # skip the value in the next iteration
            }
            Default {
                Write-Error "Unknown option $($args[$i])"
                exit 1
            }
        }
    }
}

Write-Host "$PROFILE_VAL profile selected."

if ($PROFILE_VAL -eq "aws") {
    $SecretsFile = Join-Path $PSScriptRoot "..\..\env\.secrets.env"

    if (-not (Test-Path $SecretsFile)) {
        Write-Error "Error: Secrets file not found at $SecretsFile"
        exit 1
    }

    # Read .env file and set environment variables
    Get-Content $SecretsFile | Where-Object { $_ -match '=' -and -not $_.StartsWith('#') } | ForEach-Object {
        $name, $value = $_.Split('=', 2)
        [System.Environment]::SetEnvironmentVariable($name.Trim(), $value.Trim(), "Process")
    }
}

# Change directory to the profile folder
$TargetPath = Join-Path $PSScriptRoot "..\$PROFILE_VAL"
if (Test-Path $TargetPath) {
    Set-Location $TargetPath
} else {
    Write-Error "Directory not found: $TargetPath"
    exit 1
}
