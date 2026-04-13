# destroy.ps1
$ErrorActionPreference = "Stop"
. "$PSScriptRoot\setup.ps1" $args

terraform destroy --auto-approve
