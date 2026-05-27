$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot
$env:NLS_LANG = ".AL32UTF8"

Write-Host "Chạy script tạo user bằng SYS/SYSTEM nếu cần:"
Write-Host "  `$env:NLS_LANG='.AL32UTF8'; sqlplus / as sysdba @sql/99_cai_dat_day_du.sql"
Write-Host ""
Write-Host "Hoặc chạy thủ công từng bước:"
Write-Host "  sqlplus / as sysdba @sql/00_tao_user_game_platform.sql"
Write-Host "  sqlplus GAME_PLATFORM/game123@//localhost:1521/orclpdb @sql/01_tao_cau_truc_csdl.sql"
Write-Host "  sqlplus GAME_PLATFORM/game123@//localhost:1521/orclpdb @sql/02_nap_du_lieu_mau.sql"
Write-Host ""
Write-Host "Nếu SQL*Plus gặp ORA-12638 khi dùng TCP, mở SQL Developer hoặc VSCode Oracle extension và chạy trực tiếp các file trong thư mục sql."

