-- Tạo trigger nghiệp vụ
-- File tách riêng để dễ trình bày phần HQTCSDL.

CREATE OR REPLACE TRIGGER TRG_XuLyKhiGiaoDichThanhCong
AFTER UPDATE ON GiaoDich
FOR EACH ROW
BEGIN
    IF (:NEW.TrangThai = 'Thành công' AND :OLD.TrangThai <> 'Thành công') THEN
        INSERT INTO SoHuuGame (MaNguoiChoi, MaGame, MaGD, NgaySoHuu)
        SELECT :NEW.MaNguoiChoi, ct.MaGame, :NEW.MaGD, SYSDATE
        FROM ChiTietGiaoDich ct
        WHERE ct.MaGD = :NEW.MaGD
          AND NOT EXISTS (
              SELECT 1
              FROM SoHuuGame sh
              WHERE sh.MaNguoiChoi = :NEW.MaNguoiChoi
                AND sh.MaGame = ct.MaGame
          );

        FOR item IN (
            SELECT DISTINCT ct.MaGame
            FROM ChiTietGiaoDich ct
            WHERE ct.MaGD = :NEW.MaGD
            ORDER BY ct.MaGame
        ) LOOP
            UPDATE Game
            SET LuotMua = NVL(LuotMua, 0) + 1
            WHERE MaGame = item.MaGame;
        END LOOP;

        DELETE FROM GioHang gh
        WHERE gh.MaNguoiChoi = :NEW.MaNguoiChoi
          AND gh.MaGame IN (
              SELECT ct.MaGame
              FROM ChiTietGiaoDich ct
              WHERE ct.MaGD = :NEW.MaGD
          );

        DELETE FROM Wishlist wl
        WHERE wl.MaNguoiChoi = :NEW.MaNguoiChoi
          AND wl.MaGame IN (
              SELECT ct.MaGame
              FROM ChiTietGiaoDich ct
              WHERE ct.MaGD = :NEW.MaGD
          );

        IF (:NEW.MaMaGiamGia IS NOT NULL) THEN
            UPDATE MaGiamGia
            SET LuotDung = NVL(LuotDung, 0) + 1
            WHERE MaMaGiamGia = :NEW.MaMaGiamGia;
        END IF;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_XuLyDuyetYCPH
AFTER UPDATE ON YeuCauPhatHanh
FOR EACH ROW
BEGIN
    IF (:NEW.TrangThai = 'Đã duyệt' AND :OLD.TrangThai <> 'Đã duyệt') THEN
        UPDATE Game
        SET TrangThai = 'Đang phát hành',
            NgayPhatHanh = NVL(NgayPhatHanh, SYSDATE)
        WHERE MaGame = :NEW.MaGame;

        IF (:NEW.MaPhienBan IS NOT NULL) THEN
            UPDATE PhienBanGame
            SET TrangThai = 'Đang phát hành'
            WHERE MaPhienBan = :NEW.MaPhienBan;
        END IF;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_KiemTraGioHang
BEFORE INSERT OR UPDATE ON GioHang
FOR EACH ROW
DECLARE
    vTrangThai Game.TrangThai%TYPE;
    vDoTuoi Game.DoTuoi%TYPE;
    vNgaySinh NguoiChoi.NgaySinh%TYPE;
    vSoHuu NUMBER;
    vTuoi NUMBER;
BEGIN
    SELECT TrangThai, NVL(DoTuoi, 0)
    INTO vTrangThai, vDoTuoi
    FROM Game
    WHERE MaGame = :NEW.MaGame;

    IF (vTrangThai <> 'Đang phát hành') THEN
        RAISE_APPLICATION_ERROR(-20001, 'Game chưa được phát hành, không thể thêm vào giỏ hàng.');
    END IF;

    SELECT COUNT(*)
    INTO vSoHuu
    FROM SoHuuGame
    WHERE MaNguoiChoi = :NEW.MaNguoiChoi
      AND MaGame = :NEW.MaGame;

    IF (vSoHuu > 0) THEN
        RAISE_APPLICATION_ERROR(-20002, 'Người chơi đã sở hữu game này.');
    END IF;

    SELECT NgaySinh
    INTO vNgaySinh
    FROM NguoiChoi
    WHERE MaNguoiChoi = :NEW.MaNguoiChoi;

    vTuoi := FLOOR(MONTHS_BETWEEN(SYSDATE, vNgaySinh) / 12);
    IF (vTuoi < vDoTuoi) THEN
        RAISE_APPLICATION_ERROR(-20003, 'Người chơi chưa đủ tuổi để mua game này.');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_KiemTraDanhGia
BEFORE INSERT OR UPDATE ON DanhGia
FOR EACH ROW
DECLARE
    vSoHuu NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO vSoHuu
    FROM SoHuuGame
    WHERE MaNguoiChoi = :NEW.MaNguoiChoi
      AND MaGame = :NEW.MaGame;

    IF vSoHuu = 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'Người chơi chỉ được đánh giá game đã sở hữu.');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_KiemTraWishlist
BEFORE INSERT OR UPDATE ON Wishlist
FOR EACH ROW
DECLARE
    vTrangThai Game.TrangThai%TYPE;
    vSoHuu NUMBER;
BEGIN
    SELECT TrangThai
    INTO vTrangThai
    FROM Game
    WHERE MaGame = :NEW.MaGame;

    IF vTrangThai <> 'Đang phát hành' THEN
        RAISE_APPLICATION_ERROR(-20005, 'Chỉ được thêm game đang phát hành vào wishlist.');
    END IF;

    SELECT COUNT(*)
    INTO vSoHuu
    FROM SoHuuGame
    WHERE MaNguoiChoi = :NEW.MaNguoiChoi
      AND MaGame = :NEW.MaGame;

    IF vSoHuu > 0 THEN
        RAISE_APPLICATION_ERROR(-20008, 'Người chơi đã sở hữu game này, không thể thêm vào wishlist.');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_TuDongGhiNhanNgayXuLyTicket
BEFORE UPDATE ON Ticket
FOR EACH ROW
BEGIN
    IF (:NEW.TrangThai = 'Đã xử lý') THEN
        IF (:NEW.MaNVXuLy IS NULL) THEN
            RAISE_APPLICATION_ERROR(-20006, 'Ticket đã xử lý phải có nhân viên xử lý.');
        END IF;
        IF (:NEW.NoiDungPhanHoi IS NULL OR TRIM(:NEW.NoiDungPhanHoi) IS NULL) THEN
            RAISE_APPLICATION_ERROR(-20007, 'Ticket đã xử lý phải có nội dung phản hồi.');
        END IF;
        IF (:NEW.NgayXuLy IS NULL) THEN
            :NEW.NgayXuLy := SYSDATE;
        END IF;
    END IF;
END;
/


