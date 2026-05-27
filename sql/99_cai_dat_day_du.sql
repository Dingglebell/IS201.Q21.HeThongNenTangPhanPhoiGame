-- Chạy bằng SQL*Plus với tài khoản SYS/SYSTEM.
-- Ví dụ:
-- sqlplus "/ as sysdba" @sql/99_cai_dat_day_du.sql

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
@sql/00_tao_user_game_platform.sql

PROMPT === Dang nhap schema GAME_PLATFORM ===
CONNECT GAME_PLATFORM/game123@//localhost:1521/orclpdb

PROMPT === Tao schema ===
@sql/01_tao_cau_truc_csdl.sql

PROMPT === Nap du lieu demo ===
@sql/02_nap_du_lieu_mau.sql

PROMPT === Hoan tat setup database ===

