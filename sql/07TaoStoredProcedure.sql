-- Tạo stored procedure nghiệp vụ
-- File tách riêng để dễ trình bày phần HQTCSDL.

CREATE OR REPLACE PROCEDURE SP_DangKyNguoiChoi(
    pTenDangNhap IN VARCHAR2,
    pMatKhau IN VARCHAR2,
    pTenHienThi IN VARCHAR2,
    pNgaySinh IN DATE,
    pEmail IN VARCHAR2,
    pSDT IN VARCHAR2,
    pQuocGia IN VARCHAR2,
    pMaNguoiChoi OUT NUMBER
)
IS
    vMaTaiKhoan NUMBER;
BEGIN
    vMaTaiKhoan := SEQ_TaiKhoan.NEXTVAL;
    pMaNguoiChoi := SEQ_NguoiChoi.NEXTVAL;

    INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
    VALUES (vMaTaiKhoan, pTenDangNhap, pMatKhau, 'Người chơi', 'Đang hoạt động');

    INSERT INTO NguoiChoi (MaNguoiChoi, MaTaiKhoan, TenHienThi, NgaySinh, Email, SDT, QuocGia)
    VALUES (pMaNguoiChoi, vMaTaiKhoan, pTenHienThi, pNgaySinh, pEmail, pSDT, pQuocGia);
END;
/

CREATE OR REPLACE PROCEDURE SP_DangKyNhaPhatTrien(
    pTenDangNhap IN VARCHAR2,
    pMatKhau IN VARCHAR2,
    pTenNPT IN VARCHAR2,
    pLoaiNPT IN VARCHAR2,
    pEmail IN VARCHAR2,
    pSDT IN VARCHAR2,
    pDiaChi IN VARCHAR2,
    pMaNPT OUT NUMBER
)
IS
    vMaTaiKhoan NUMBER;
BEGIN
    vMaTaiKhoan := SEQ_TaiKhoan.NEXTVAL;
    pMaNPT := SEQ_NhaPhatTrien.NEXTVAL;

    INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
    VALUES (vMaTaiKhoan, pTenDangNhap, pMatKhau, 'Nhà phát triển', 'Đang hoạt động');

    INSERT INTO NhaPhatTrien (MaNPT, MaTaiKhoan, TenNPT, LoaiNPT, Email, SDT, DiaChi, TyLeChiaSe)
    VALUES (pMaNPT, vMaTaiKhoan, pTenNPT, pLoaiNPT, pEmail, pSDT, pDiaChi, 0.70);
END;
/

CREATE OR REPLACE PROCEDURE SP_TaoGame(
    pMaNPT IN NUMBER,
    pTenGame IN VARCHAR2,
    pDoTuoi IN NUMBER,
    pGiaGoc IN NUMBER,
    pMoTa IN VARCHAR2
)
IS
BEGIN
    INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
    VALUES (SEQ_Game.NEXTVAL, pMaNPT, pTenGame, pDoTuoi, pGiaGoc, 'Chưa phát hành', NULL, 0, pMoTa);
END;
/

CREATE OR REPLACE PROCEDURE SP_TaoYeuCauPhatHanh(
    pMaNPT IN NUMBER,
    pMaGame IN NUMBER,
    pMaPhienBan IN NUMBER,
    pMaYeuCau OUT NUMBER
)
IS
    vCount NUMBER;
BEGIN
    SELECT COUNT(*) INTO vCount FROM Game WHERE MaGame = pMaGame AND MaNPT = pMaNPT;
    IF vCount = 0 THEN
        RAISE_APPLICATION_ERROR(-20101, 'Game không thuộc quyền quản lý của nhà phát triển.');
    END IF;

    IF pMaPhienBan IS NOT NULL THEN
        SELECT COUNT(*) INTO vCount FROM PhienBanGame WHERE MaPhienBan = pMaPhienBan AND MaGame = pMaGame;
        IF vCount = 0 THEN
            RAISE_APPLICATION_ERROR(-20102, 'Phiên bản không thuộc game trong yêu cầu.');
        END IF;
    END IF;

    SELECT COUNT(*) INTO vCount FROM YeuCauPhatHanh WHERE MaGame = pMaGame AND TrangThai = 'Chờ duyệt';
    IF vCount > 0 THEN
        RAISE_APPLICATION_ERROR(-20103, 'Game đã có yêu cầu phát hành đang chờ duyệt.');
    END IF;

    pMaYeuCau := SEQ_YeuCauPhatHanh.NEXTVAL;
    INSERT INTO YeuCauPhatHanh (MaYeuCau, MaNPT, MaGame, MaPhienBan, TrangThai)
    VALUES (pMaYeuCau, pMaNPT, pMaGame, pMaPhienBan, 'Chờ duyệt');
END;
/



CREATE OR REPLACE PROCEDURE SP_XuLyYeuCauPhatHanh(
    pMaYeuCau IN NUMBER,
    pMaNVXuLy IN NUMBER,
    pTrangThai IN VARCHAR2,
    pLyDoTuChoi IN VARCHAR2
)
IS
    vTrangThai YeuCauPhatHanh.TrangThai%TYPE;
BEGIN
    SELECT TrangThai INTO vTrangThai FROM YeuCauPhatHanh WHERE MaYeuCau = pMaYeuCau FOR UPDATE;
    IF vTrangThai <> 'Chờ duyệt' THEN
        RAISE_APPLICATION_ERROR(-20104, 'Yêu cầu phát hành đã được xử lý.');
    END IF;
    IF pTrangThai NOT IN ('Đã duyệt', 'Từ chối') THEN
        RAISE_APPLICATION_ERROR(-20105, 'Trạng thái xử lý yêu cầu không hợp lệ.');
    END IF;

    UPDATE YeuCauPhatHanh
    SET TrangThai = pTrangThai,
        MaNVXuLy = pMaNVXuLy,
        LyDoTuChoi = CASE WHEN pTrangThai = 'Từ chối' THEN pLyDoTuChoi ELSE NULL END,
        NgayXuLy = SYSDATE
    WHERE MaYeuCau = pMaYeuCau;
END;
/

CREATE OR REPLACE PROCEDURE SP_ThemGameVaoGioHang(
    pMaNguoiChoi IN NUMBER,
    pMaGame IN NUMBER
)
IS
BEGIN
    INSERT INTO GioHang (MaNguoiChoi, MaGame, NgayThem)
    VALUES (pMaNguoiChoi, pMaGame, SYSDATE);
END;
/

CREATE OR REPLACE PROCEDURE SP_TaoGiaoDichTuGioHang(
    pMaNguoiChoi IN NUMBER,
    pCode IN VARCHAR2,
    pPhuongThucThanhToan IN VARCHAR2,
    pMaGD OUT NUMBER
)
IS
    vSoGame NUMBER;
    vTongTienGoc NUMBER := 0;
    vTongGiamKM NUMBER := 0;
    vTongGiaBan NUMBER := 0;
    vSoTienGiamCode NUMBER := 0;
    vMaMaGiamGia NUMBER;
    vGiaHienTai NUMBER;
BEGIN
    SELECT COUNT(*) INTO vSoGame FROM GioHang WHERE MaNguoiChoi = pMaNguoiChoi;
    IF vSoGame = 0 THEN
        RAISE_APPLICATION_ERROR(-20106, 'Giỏ hàng không có game.');
    END IF;

    FOR item IN (
        SELECT gh.MaGame, g.GiaGoc
        FROM GioHang gh
        JOIN Game g ON g.MaGame = gh.MaGame
        WHERE gh.MaNguoiChoi = pMaNguoiChoi
    ) LOOP
        vGiaHienTai := SF_TinhGiaHienTai(item.MaGame);
        vTongTienGoc := vTongTienGoc + item.GiaGoc;
        vTongGiamKM := vTongGiamKM + (item.GiaGoc - vGiaHienTai);
        vTongGiaBan := vTongGiaBan + vGiaHienTai;
    END LOOP;

    IF pCode IS NOT NULL THEN
        vSoTienGiamCode := SF_KiemTraMaGiamGia(pCode, vTongGiaBan);
        IF vSoTienGiamCode < 0 THEN
            RAISE_APPLICATION_ERROR(-20108, 'Mã giảm giá không hợp lệ, hết hạn hoặc đã vượt giới hạn sử dụng.');
        END IF;

        SELECT MaMaGiamGia
        INTO vMaMaGiamGia
        FROM MaGiamGia
        WHERE UPPER(Code) = UPPER(pCode);
    END IF;

    pMaGD := SEQ_GiaoDich.NEXTVAL;
    INSERT INTO GiaoDich (MaGD, MaNguoiChoi, MaMaGiamGia, TongTienGoc, TongGiamGia, TongThanhToan, PhuongThucThanhToan, TrangThai)
    VALUES (
        pMaGD,
        pMaNguoiChoi,
        vMaMaGiamGia,
        vTongTienGoc,
        vTongGiamKM + NVL(vSoTienGiamCode, 0),
        GREATEST(vTongGiaBan - NVL(vSoTienGiamCode, 0), 0),
        NVL(pPhuongThucThanhToan, 'Ví điện tử'),
        'Chờ thanh toán'
    );

    FOR item IN (
        SELECT g.MaGame, g.GiaGoc, SF_TinhGiaHienTai(g.MaGame) AS GiaBan
        FROM GioHang gh
        JOIN Game g ON g.MaGame = gh.MaGame
        WHERE gh.MaNguoiChoi = pMaNguoiChoi
    ) LOOP
        INSERT INTO ChiTietGiaoDich (MaGD, MaGame, GiaGoc, SoTienGiamKM, GiaBan)
        VALUES (pMaGD, item.MaGame, item.GiaGoc, item.GiaGoc - item.GiaBan, item.GiaBan);
    END LOOP;
END;
/

CREATE OR REPLACE PROCEDURE SP_XacNhanThanhToan(
    pMaGD IN NUMBER,
    pTrangThai IN VARCHAR2
)
IS
    vTrangThai GiaoDich.TrangThai%TYPE;
BEGIN
    IF pTrangThai NOT IN ('Thành công', 'Thất bại') THEN
        RAISE_APPLICATION_ERROR(-20107, 'Trạng thái thanh toán không hợp lệ.');
    END IF;

    SELECT TrangThai INTO vTrangThai FROM GiaoDich WHERE MaGD = pMaGD FOR UPDATE;
    IF vTrangThai = 'Thành công' THEN
        RETURN;
    END IF;

    UPDATE GiaoDich
    SET TrangThai = pTrangThai
    WHERE MaGD = pMaGD;
END;
/

CREATE OR REPLACE PROCEDURE SP_XuLyTicket(
    pMaTicket IN NUMBER,
    pMaNVXuLy IN NUMBER,
    pNoiDungPhanHoi IN VARCHAR2
)
IS
BEGIN
    UPDATE Ticket
    SET TrangThai = 'Đã xử lý',
        MaNVXuLy = pMaNVXuLy,
        NoiDungPhanHoi = pNoiDungPhanHoi
    WHERE MaTicket = pMaTicket;
END;
/

CREATE OR REPLACE PROCEDURE SP_ThemGameVaoKhuyenMai(
    pMaKM IN NUMBER,
    pMaGame IN NUMBER,
    pPhanTramKM IN NUMBER
)
IS
BEGIN
    INSERT INTO ChiTietKhuyenMai (MaKM, MaGame, PhanTramKM)
    VALUES (pMaKM, pMaGame, pPhanTramKM);
END;
/

CREATE OR REPLACE PROCEDURE SP_TaoMaGiamGia(
    pCode IN VARCHAR2,
    pSoTienGiam IN NUMBER,
    pGioiHanSuDung IN NUMBER,
    pNgayBatDau IN DATE,
    pNgayHetHan IN DATE,
    pTongGiaToiThieu IN NUMBER,
    pMoTa IN VARCHAR2
)
IS
BEGIN
    INSERT INTO MaGiamGia (
        MaMaGiamGia, Code, SoTienGiam, GioiHanSuDung, LuotDung,
        NgayBatDau, NgayHetHan, TongGiaToiThieu, TrangThai, MoTa
    )
    VALUES (
        SEQ_MaGiamGia.NEXTVAL, pCode, pSoTienGiam, pGioiHanSuDung, 0,
        pNgayBatDau, pNgayHetHan, pTongGiaToiThieu, 'Đang hiệu lực', pMoTa
    );
END;
/


