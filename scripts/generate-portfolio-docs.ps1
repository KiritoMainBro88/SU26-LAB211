[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$generator = Join-Path $PSScriptRoot 'generate_portfolio_docs.py'
$python = Get-Command python -ErrorAction SilentlyContinue
if ($null -eq $python) {
    $python = Get-Command python3 -ErrorAction SilentlyContinue
}
if ($null -eq $python) {
    throw 'Python 3 is required to regenerate reports and Audit Log evidence indexes.'
}

Push-Location $repoRoot
try {
    & $python.Source -X utf8 $generator
    if ($LASTEXITCODE -ne 0) {
        throw "Document generation failed with exit code $LASTEXITCODE."
    }
} finally {
    Pop-Location
}
