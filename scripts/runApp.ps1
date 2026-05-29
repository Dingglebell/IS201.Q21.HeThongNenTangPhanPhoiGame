$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$mavenCandidates = @(
    "mvn",
    "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd"
)

Set-Location $projectRoot

New-Item -ItemType Directory -Force -Path "baoCao", "tepBuild\taiLen", "tepMedia\taiLen", "anhBiaGame" | Out-Null

foreach ($maven in $mavenCandidates) {
    $command = Get-Command $maven -ErrorAction SilentlyContinue
    if ($command) {
        & $command.Source javafx:run
        exit $LASTEXITCODE
    }
    if (Test-Path -LiteralPath $maven) {
        & $maven javafx:run
        exit $LASTEXITCODE
    }
}

throw "Không tìm thấy Maven. Hãy cài Maven hoặc thêm mvn vào PATH."


