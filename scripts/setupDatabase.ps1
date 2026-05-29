$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot
$env:NLS_LANG = ".AL32UTF8"

Write-Host "Chạy script tạo user bằng SYS/SYSTEM nếu cần:"
Write-Host "  `$env:NLS_LANG='.AL32UTF8'; sqlplus / as sysdba @sql/99CaiDatDayDu.sql"
Write-Host ""
Write-Host "Hoặc chạy thủ công từng bước:"
Write-Host "  sqlplus / as sysdba @sql/00TaoUserGamePlatform.sql"
Write-Host "  sqlplus GAME_PLATFORM/game123@//localhost:1521/orclpdb @sql/01XoaSchemaCu.sql"
Write-Host "  sqlplus GAME_PLATFORM/game123@//localhost:1521/orclpdb @sql/02TaoSequence.sql"
Write-Host "  sqlplus GAME_PLATFORM/game123@//localhost:1521/orclpdb @sql/03TaoBang.sql"
Write-Host "  sqlplus GAME_PLATFORM/game123@//localhost:1521/orclpdb @sql/04TaoRangBuoc.sql"
Write-Host "  sqlplus GAME_PLATFORM/game123@//localhost:1521/orclpdb @sql/05TaoTrigger.sql"
Write-Host "  sqlplus GAME_PLATFORM/game123@//localhost:1521/orclpdb @sql/06TaoStoredFunction.sql"
Write-Host "  sqlplus GAME_PLATFORM/game123@//localhost:1521/orclpdb @sql/07TaoStoredProcedure.sql"
Write-Host "  sqlplus GAME_PLATFORM/game123@//localhost:1521/orclpdb @sql/08NapDuLieuMau.sql"
Write-Host ""
Write-Host "Nếu SQL*Plus gặp ORA-12638 khi dùng TCP, mở SQL Developer hoặc VSCode Oracle extension và chạy trực tiếp các file trong thư mục sql."



