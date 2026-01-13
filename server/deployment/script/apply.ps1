# apply.ps1
$ErrorActionPreference = "Stop"
. "$PSScriptRoot\setup.ps1" $args

terraform init
terraform apply --auto-approve
