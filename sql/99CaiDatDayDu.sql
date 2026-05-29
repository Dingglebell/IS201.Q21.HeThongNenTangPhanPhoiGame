-- Chạy bằng SQL*Plus với tài khoản SYS/SYSTEM.
-- Ví dụ:
-- sqlplus "/ as sysdba" @sql/99CaiDatDayDu.sql

PROMPT === Mo PDB ORCLPDB neu dang mounted ===
DECLARE
    v_open_mode VARCHAR2(20);
BEGIN
    SELECT open_mode
    INTO v_open_mode
    FROM v$pdbs
    WHERE name = 'ORCLPDB';

    IF v_open_mode <> 'READ WRITE' THEN
        EXECUTE IMMEDIATE 'ALTER PLUGGABLE DATABASE ORCLPDB OPEN';
    END IF;
END;
/
ALTER SESSION SET CONTAINER = ORCLPDB;

PROMPT === Tao user GAME_PLATFORM neu chua ton tai ===
@sql/00TaoUserGamePlatform.sql

PROMPT === Dang nhap schema GAME_PLATFORM ===
CONNECT GAME_PLATFORM/game123@//localhost:1521/orclpdb

PROMPT === Xoa schema cu ===
@sql/01XoaSchemaCu.sql

PROMPT === Tao sequence ===
@sql/02TaoSequence.sql

PROMPT === Tao bang ===
@sql/03TaoBang.sql

PROMPT === Tao rang buoc ===
@sql/04TaoRangBuoc.sql

PROMPT === Tao trigger ===
@sql/05TaoTrigger.sql

PROMPT === Tao stored function ===
@sql/06TaoStoredFunction.sql

PROMPT === Tao stored procedure ===
@sql/07TaoStoredProcedure.sql

PROMPT === Nap du lieu demo ===
@sql/08NapDuLieuMau.sql

PROMPT === Hoan tat setup database ===



