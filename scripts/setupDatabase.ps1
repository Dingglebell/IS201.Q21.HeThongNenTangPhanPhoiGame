param(
    [string]$PdbName = "ORCLPDB"
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$setupScript = Join-Path $projectRoot "sql\99CaiDatDayDu.sql"
$generatedScript = Join-Path $projectRoot ".setupDatabaseGenerated.sql"

Set-Location $projectRoot
$env:NLS_LANG = ".AL32UTF8"

$sqlplus = Get-Command "sqlplus" -ErrorAction SilentlyContinue
if (-not $sqlplus) {
    throw "Không tìm thấy SQL*Plus. Hãy cài Oracle Database/Oracle Client và thêm sqlplus vào PATH."
}

Write-Host "Đang cài đặt database GAME_PLATFORM..."
$pdbUpper = $PdbName.ToUpperInvariant()
$pdbLower = $PdbName.ToLowerInvariant()

$scriptContent = Get-Content -Raw -Path $setupScript
$scriptContent = $scriptContent.Replace("ORCLPDB", $pdbUpper).Replace("orclpdb", $pdbLower)
Set-Content -Path $generatedScript -Value $scriptContent -Encoding UTF8

try {
    & $sqlplus.Source -L "/ as sysdba" "@$generatedScript"
} finally {
    Remove-Item -LiteralPath $generatedScript -Force -ErrorAction SilentlyContinue
}

if ($LASTEXITCODE -ne 0) {
    throw "Setup database thất bại. Kiểm tra Oracle service, SQL*Plus và quyền SYSDBA."
}

Write-Host "Hoàn tất setup database."
