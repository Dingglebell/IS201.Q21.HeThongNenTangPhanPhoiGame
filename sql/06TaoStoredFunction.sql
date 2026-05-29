-- Tạo stored function nghiệp vụ
-- File tách riêng để dễ trình bày phần HQTCSDL.

CREATE OR REPLACE FUNCTION SF_TinhGiaHienTai(pMaGame NUMBER)
RETURN NUMBER
IS
    vGiaGoc Game.GiaGoc%TYPE;
    vPhanTramKM NUMBER := 0;
BEGIN
    SELECT GiaGoc
    INTO vGiaGoc
    FROM Game
    WHERE MaGame = pMaGame;

    SELECT NVL(MAX(ctkm.PhanTramKM), 0)
    INTO vPhanTramKM
    FROM ChiTietKhuyenMai ctkm
    JOIN KhuyenMai km ON km.MaKM = ctkm.MaKM
    WHERE ctkm.MaGame = pMaGame
      AND km.TrangThai = 'Đang hiệu lực'
      AND TRUNC(SYSDATE) BETWEEN km.NgayBatDau AND km.NgayKetThuc;

    RETURN vGiaGoc - ROUND(vGiaGoc * vPhanTramKM / 100, 2);
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION SF_KiemTraMaGiamGia(pCode VARCHAR2, pTongTien NUMBER)
RETURN NUMBER
IS
    vSoTienGiam MaGiamGia.SoTienGiam%TYPE;
BEGIN
    IF pCode IS NULL THEN
        RETURN 0;
    END IF;

    SELECT SoTienGiam
    INTO vSoTienGiam
    FROM MaGiamGia
    WHERE UPPER(Code) = UPPER(pCode)
      AND TrangThai = 'Đang hiệu lực'
      AND TRUNC(SYSDATE) BETWEEN NgayBatDau AND NgayHetHan
      AND LuotDung < GioiHanSuDung
      AND pTongTien >= TongGiaToiThieu;

    RETURN LEAST(vSoTienGiam, pTongTien);
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN -1;
END;
/

CREATE OR REPLACE FUNCTION SF_KiemTraDoTuoi(pMaNguoiChoi NUMBER, pMaGame NUMBER)
RETURN NUMBER
IS
    vNgaySinh NguoiChoi.NgaySinh%TYPE;
    vDoTuoi Game.DoTuoi%TYPE;
    vTuoi NUMBER;
BEGIN
    SELECT NgaySinh INTO vNgaySinh FROM NguoiChoi WHERE MaNguoiChoi = pMaNguoiChoi;
    SELECT NVL(DoTuoi, 0) INTO vDoTuoi FROM Game WHERE MaGame = pMaGame;
    vTuoi := FLOOR(MONTHS_BETWEEN(SYSDATE, vNgaySinh) / 12);
    RETURN CASE WHEN vTuoi >= vDoTuoi THEN 1 ELSE 0 END;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION SF_KiemTraSoHuuGame(pMaNguoiChoi NUMBER, pMaGame NUMBER)
RETURN NUMBER
IS
    vCount NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO vCount
    FROM SoHuuGame
    WHERE MaNguoiChoi = pMaNguoiChoi
      AND MaGame = pMaGame;
    RETURN CASE WHEN vCount > 0 THEN 1 ELSE 0 END;
END;
/

CREATE OR REPLACE FUNCTION SF_TinhTongTienGioHang(pMaNguoiChoi NUMBER)
RETURN NUMBER
IS
    vTong NUMBER;
BEGIN
    SELECT NVL(SUM(SF_TinhGiaHienTai(MaGame)), 0)
    INTO vTong
    FROM GioHang
    WHERE MaNguoiChoi = pMaNguoiChoi;
    RETURN vTong;
END;
/

CREATE OR REPLACE FUNCTION SF_TinhDiemTrungBinhGame(pMaGame NUMBER)
RETURN NUMBER
IS
    vDiem NUMBER;
BEGIN
    SELECT ROUND(NVL(AVG(DiemDanhGia), 0), 2)
    INTO vDiem
    FROM DanhGia
    WHERE MaGame = pMaGame;
    RETURN vDiem;
END;
/

CREATE OR REPLACE FUNCTION SF_TinhDoanhThuNPT(pMaNPT NUMBER, pTuNgay DATE, pDenNgay DATE)
RETURN NUMBER
IS
    vDoanhThu NUMBER;
BEGIN
    SELECT NVL(SUM(ct.GiaBan * npt.TyLeChiaSe), 0)
    INTO vDoanhThu
    FROM GiaoDich gd
    JOIN ChiTietGiaoDich ct ON ct.MaGD = gd.MaGD
    JOIN Game g ON g.MaGame = ct.MaGame
    JOIN NhaPhatTrien npt ON npt.MaNPT = g.MaNPT
    WHERE g.MaNPT = pMaNPT
      AND gd.TrangThai = 'Thành công'
      AND TRUNC(gd.NgayGD) BETWEEN TRUNC(pTuNgay) AND TRUNC(pDenNgay);
    RETURN vDoanhThu;
END;
/

CREATE OR REPLACE FUNCTION SF_TongChiTieuNguoiChoi(pMaNguoiChoi NUMBER)
RETURN NUMBER
IS
    vTong NUMBER;
BEGIN
    SELECT NVL(SUM(TongThanhToan), 0)
    INTO vTong
    FROM GiaoDich
    WHERE MaNguoiChoi = pMaNguoiChoi
      AND TrangThai = 'Thành công';
    RETURN vTong;
END;
/


