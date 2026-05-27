BEGIN
    FOR item IN (
        SELECT view_name
        FROM user_views
        WHERE view_name IN (
            'V_LICHSUMUAGAME', 'V_GAMEDANGKHUYENMAI', 'V_GAMEBANCHAY',
            'V_BAOCAODOANHTHUNPT', 'V_BAOCAODOANHTHUNENTANG'
        )
    ) LOOP
        EXECUTE IMMEDIATE 'DROP VIEW ' || item.view_name;
    END LOOP;
END;
/

BEGIN
    FOR item IN (
        SELECT object_name, object_type
        FROM user_objects
        WHERE object_type IN ('PROCEDURE', 'FUNCTION')
          AND object_name IN (
              'SP_TAOTAIKHOANNGUOICHOI', 'SP_TAOTAIKHOANNPT', 'SP_DANGKYNGUOICHOI',
              'SP_DANGKYNHAPHATTRIEN', 'SP_TAOGAME', 'SP_THEMPHIENBANGAME',
              'SP_TAOYEUCAUPHATHANH', 'SP_XULYYEUCAUPHATHANH', 'SP_THEMGAMEVAOGIOHANG',
              'SP_TAOGIAODICHTUGIOHANG', 'SP_XACNHANTHANHTOAN', 'SP_THEMDANHGIA',
              'SP_XULYTICKET', 'SP_THEMGAMEVAOKHUYENMAI', 'SP_TAOMAGIAMGIA',
              'SF_TINHGIAHIENTAI', 'SF_KIEMTRAMAGIAMGIA', 'SF_KIEMTRADUTUOI',
              'SF_KIEMTRADOTUOI', 'SF_KIEMTRASOHUUGAME', 'SF_TINHTONGTIENGIOHANG',
              'SF_TINHDIEMTRUNGBINHGAME', 'SF_TINHDOANHTHUNENTANG',
              'SF_TINHDOANHTHUNPT', 'SF_TONGCHITIEUNGUOICHOI'
          )
    ) LOOP
        EXECUTE IMMEDIATE 'DROP ' || item.object_type || ' ' || item.object_name;
    END LOOP;
END;
/

BEGIN
    FOR item IN (
        SELECT table_name
        FROM user_tables
        WHERE table_name IN (
            'TICKET', 'MAGIAMGIA', 'CHITIETKHUYENMAI', 'KHUYENMAI', 'DANHGIA',
            'SOHUUGAME', 'CHITIETGIAODICH', 'GIAODICH', 'GIOHANG', 'WISHLIST',
            'YEUCAUPHATHANH', 'PHIENBANGAME', 'DANHMUCTHELOAI', 'GAMEMEDIA',
            'GAME', 'THELOAI', 'NGUOICHOI', 'NHAPHATTRIEN', 'NHANVIEN', 'TAIKHOAN'
        )
    ) LOOP
        EXECUTE IMMEDIATE 'DROP TABLE ' || item.table_name || ' CASCADE CONSTRAINTS PURGE';
    END LOOP;
END;
/

BEGIN
    FOR item IN (
        SELECT sequence_name
        FROM user_sequences
        WHERE sequence_name IN (
            'SEQ_TAIKHOAN', 'SEQ_NHANVIEN', 'SEQ_NHAPHATTRIEN', 'SEQ_NGUOICHOI',
            'SEQ_THELOAI', 'SEQ_GAME', 'SEQ_GAMEMEDIA', 'SEQ_PHIENBANGAME',
            'SEQ_YEUCAUPHATHANH', 'SEQ_GIAODICH', 'SEQ_DANHGIA', 'SEQ_KHUYENMAI',
            'SEQ_MAGIAMGIA', 'SEQ_TICKET'
        )
    ) LOOP
        EXECUTE IMMEDIATE 'DROP SEQUENCE ' || item.sequence_name;
    END LOOP;
END;
/

CREATE SEQUENCE SEQ_TaiKhoan START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_NhanVien START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_NhaPhatTrien START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_NguoiChoi START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_TheLoai START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_Game START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_GameMedia START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_PhienBanGame START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_YeuCauPhatHanh START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_GiaoDich START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_DanhGia START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_KhuyenMai START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_MaGiamGia START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_Ticket START WITH 1000 INCREMENT BY 1 NOCACHE;

CREATE TABLE TaiKhoan (
    MaTaiKhoan NUMBER DEFAULT SEQ_TaiKhoan.NEXTVAL PRIMARY KEY,
    TenDangNhap VARCHAR2(50 CHAR) NOT NULL,
    MatKhau VARCHAR2(255 CHAR) NOT NULL,
    LoaiTaiKhoan VARCHAR2(30 CHAR) NOT NULL,
    TrangThai VARCHAR2(30 CHAR) DEFAULT 'Đang hoạt động' NOT NULL,
    NgayTao DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT UQ_TaiKhoan_TenDangNhap UNIQUE (TenDangNhap),
    CONSTRAINT CK_TaiKhoan_Loai CHECK (LoaiTaiKhoan IN ('Người chơi', 'Nhà phát triển', 'Nhân viên')),
    CONSTRAINT CK_TaiKhoan_TrangThai CHECK (TrangThai IN ('Đang hoạt động', 'Ngưng hoạt động', 'Bị khóa'))
);

CREATE TABLE NhanVien (
    MaNV NUMBER DEFAULT SEQ_NhanVien.NEXTVAL PRIMARY KEY,
    MaTaiKhoan NUMBER NOT NULL,
    HoTen VARCHAR2(100 CHAR) NOT NULL,
    VaiTro VARCHAR2(50 CHAR) NOT NULL,
    Email VARCHAR2(100 CHAR) NOT NULL,
    SDT VARCHAR2(15 CHAR),
    CONSTRAINT UQ_NhanVien_TaiKhoan UNIQUE (MaTaiKhoan),
    CONSTRAINT UQ_NhanVien_Email UNIQUE (Email),
    CONSTRAINT CK_NhanVien_VaiTro CHECK (VaiTro IN ('Quản lý nền tảng', 'Kiểm duyệt viên', 'Marketing', 'CSKH')),
    CONSTRAINT FK_NhanVien_TaiKhoan FOREIGN KEY (MaTaiKhoan) REFERENCES TaiKhoan(MaTaiKhoan)
);

CREATE TABLE NhaPhatTrien (
    MaNPT NUMBER DEFAULT SEQ_NhaPhatTrien.NEXTVAL PRIMARY KEY,
    MaTaiKhoan NUMBER NOT NULL,
    TenNPT VARCHAR2(100 CHAR) NOT NULL,
    LoaiNPT VARCHAR2(30 CHAR) NOT NULL,
    Email VARCHAR2(100 CHAR) NOT NULL,
    SDT VARCHAR2(15 CHAR),
    DiaChi VARCHAR2(200 CHAR),
    TyLeChiaSe NUMBER(4,2) DEFAULT 0.70 NOT NULL,
    CONSTRAINT UQ_NhaPhatTrien_TaiKhoan UNIQUE (MaTaiKhoan),
    CONSTRAINT UQ_NhaPhatTrien_Email UNIQUE (Email),
    CONSTRAINT CK_NhaPhatTrien_Loai CHECK (LoaiNPT IN ('Cá nhân', 'Studio', 'Doanh nghiệp')),
    CONSTRAINT CK_NhaPhatTrien_TyLe CHECK (TyLeChiaSe BETWEEN 0 AND 1),
    CONSTRAINT FK_NhaPhatTrien_TaiKhoan FOREIGN KEY (MaTaiKhoan) REFERENCES TaiKhoan(MaTaiKhoan)
);

CREATE TABLE NguoiChoi (
    MaNguoiChoi NUMBER DEFAULT SEQ_NguoiChoi.NEXTVAL PRIMARY KEY,
    MaTaiKhoan NUMBER NOT NULL,
    TenHienThi VARCHAR2(100 CHAR) NOT NULL,
    NgaySinh DATE NOT NULL,
    Email VARCHAR2(100 CHAR) NOT NULL,
    SDT VARCHAR2(15 CHAR),
    QuocGia VARCHAR2(50 CHAR),
    CONSTRAINT UQ_NguoiChoi_TaiKhoan UNIQUE (MaTaiKhoan),
    CONSTRAINT UQ_NguoiChoi_Email UNIQUE (Email),
    CONSTRAINT FK_NguoiChoi_TaiKhoan FOREIGN KEY (MaTaiKhoan) REFERENCES TaiKhoan(MaTaiKhoan)
);

CREATE TABLE TheLoai (
    MaTheLoai NUMBER DEFAULT SEQ_TheLoai.NEXTVAL PRIMARY KEY,
    TenTheLoai VARCHAR2(100 CHAR) NOT NULL,
    MoTa VARCHAR2(500 CHAR),
    CONSTRAINT UQ_TheLoai_Ten UNIQUE (TenTheLoai)
);

CREATE TABLE Game (
    MaGame NUMBER DEFAULT SEQ_Game.NEXTVAL PRIMARY KEY,
    MaNPT NUMBER NOT NULL,
    TenGame VARCHAR2(150 CHAR) NOT NULL,
    DoTuoi NUMBER(2) DEFAULT 0 NOT NULL,
    GiaGoc NUMBER(12,2) DEFAULT 0 NOT NULL,
    TrangThai VARCHAR2(30 CHAR) DEFAULT 'Chưa phát hành' NOT NULL,
    NgayPhatHanh DATE,
    LuotMua NUMBER DEFAULT 0 NOT NULL,
    MoTa VARCHAR2(1000 CHAR),
    CONSTRAINT CK_Game_TrangThai CHECK (TrangThai IN ('Đang phát hành', 'Chưa phát hành', 'Đã gỡ bỏ')),
    CONSTRAINT CK_Game_Gia CHECK (GiaGoc >= 0),
    CONSTRAINT CK_Game_DoTuoi CHECK (DoTuoi >= 0),
    CONSTRAINT CK_Game_LuotMua CHECK (LuotMua >= 0),
    CONSTRAINT CK_Game_NgayPhatHanh CHECK (TrangThai <> 'Đang phát hành' OR NgayPhatHanh IS NOT NULL),
    CONSTRAINT FK_Game_NhaPhatTrien FOREIGN KEY (MaNPT) REFERENCES NhaPhatTrien(MaNPT)
);

CREATE TABLE GameMedia (
    MaMedia NUMBER DEFAULT SEQ_GameMedia.NEXTVAL PRIMARY KEY,
    MaGame NUMBER NOT NULL,
    LoaiMedia VARCHAR2(30 CHAR) NOT NULL,
    FileMedia VARCHAR2(255 CHAR) NOT NULL,
    CONSTRAINT CK_GameMedia_Loai CHECK (LoaiMedia IN ('Ảnh bìa', 'Ảnh phụ', 'Video')),
    CONSTRAINT FK_GameMedia_Game FOREIGN KEY (MaGame) REFERENCES Game(MaGame)
);

CREATE TABLE DanhMucTheLoai (
    MaTheLoai NUMBER NOT NULL,
    MaGame NUMBER NOT NULL,
    CONSTRAINT PK_DanhMucTheLoai PRIMARY KEY (MaTheLoai, MaGame),
    CONSTRAINT FK_DanhMuc_TheLoai FOREIGN KEY (MaTheLoai) REFERENCES TheLoai(MaTheLoai),
    CONSTRAINT FK_DanhMuc_Game FOREIGN KEY (MaGame) REFERENCES Game(MaGame)
);

CREATE TABLE PhienBanGame (
    MaPhienBan NUMBER DEFAULT SEQ_PhienBanGame.NEXTVAL PRIMARY KEY,
    MaGame NUMBER NOT NULL,
    TenPhienBan VARCHAR2(100 CHAR) NOT NULL,
    NoiDungPhienBan VARCHAR2(1000 CHAR),
    FilePhienBan VARCHAR2(255 CHAR),
    DungLuong NUMBER(12,2) NOT NULL,
    NgayTao DATE DEFAULT SYSDATE NOT NULL,
    TrangThai VARCHAR2(30 CHAR) DEFAULT 'Chưa phát hành' NOT NULL,
    CONSTRAINT UQ_PhienBan_Ten UNIQUE (MaGame, TenPhienBan),
    CONSTRAINT CK_PhienBan_DungLuong CHECK (DungLuong > 0),
    CONSTRAINT CK_PhienBan_TrangThai CHECK (TrangThai IN ('Đang phát hành', 'Chưa phát hành')),
    CONSTRAINT FK_PhienBan_Game FOREIGN KEY (MaGame) REFERENCES Game(MaGame)
);

CREATE TABLE YeuCauPhatHanh (
    MaYeuCau NUMBER DEFAULT SEQ_YeuCauPhatHanh.NEXTVAL PRIMARY KEY,
    MaNPT NUMBER NOT NULL,
    MaGame NUMBER NOT NULL,
    MaPhienBan NUMBER,
    NgayYeuCau DATE DEFAULT SYSDATE NOT NULL,
    TrangThai VARCHAR2(30 CHAR) DEFAULT 'Chờ duyệt' NOT NULL,
    MaNVXuLy NUMBER,
    LyDoTuChoi VARCHAR2(500 CHAR),
    NgayXuLy DATE,
    CONSTRAINT CK_YCPH_TrangThai CHECK (TrangThai IN ('Chờ duyệt', 'Đã duyệt', 'Từ chối')),
    CONSTRAINT CK_YCPH_LyDo CHECK (TrangThai <> 'Từ chối' OR LyDoTuChoi IS NOT NULL),
    CONSTRAINT CK_YCPH_XuLy CHECK (
        TrangThai = 'Chờ duyệt'
        OR (MaNVXuLy IS NOT NULL AND NgayXuLy IS NOT NULL)
    ),
    CONSTRAINT CK_YCPH_NgayXuLy CHECK (NgayXuLy IS NULL OR NgayXuLy >= NgayYeuCau),
    CONSTRAINT FK_YCPH_NPT FOREIGN KEY (MaNPT) REFERENCES NhaPhatTrien(MaNPT),
    CONSTRAINT FK_YCPH_Game FOREIGN KEY (MaGame) REFERENCES Game(MaGame),
    CONSTRAINT FK_YCPH_PhienBan FOREIGN KEY (MaPhienBan) REFERENCES PhienBanGame(MaPhienBan),
    CONSTRAINT FK_YCPH_NhanVien FOREIGN KEY (MaNVXuLy) REFERENCES NhanVien(MaNV)
);

CREATE UNIQUE INDEX UQ_YCPH_Game_ChoDuyet
    ON YeuCauPhatHanh (CASE WHEN TrangThai = 'Chờ duyệt' THEN MaGame END);

CREATE TABLE Wishlist (
    MaNguoiChoi NUMBER NOT NULL,
    MaGame NUMBER NOT NULL,
    NgayThem DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT PK_Wishlist PRIMARY KEY (MaNguoiChoi, MaGame),
    CONSTRAINT FK_Wishlist_NguoiChoi FOREIGN KEY (MaNguoiChoi) REFERENCES NguoiChoi(MaNguoiChoi),
    CONSTRAINT FK_Wishlist_Game FOREIGN KEY (MaGame) REFERENCES Game(MaGame)
);

CREATE TABLE GioHang (
    MaNguoiChoi NUMBER NOT NULL,
    MaGame NUMBER NOT NULL,
    NgayThem DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT PK_GioHang PRIMARY KEY (MaNguoiChoi, MaGame),
    CONSTRAINT FK_GioHang_NguoiChoi FOREIGN KEY (MaNguoiChoi) REFERENCES NguoiChoi(MaNguoiChoi),
    CONSTRAINT FK_GioHang_Game FOREIGN KEY (MaGame) REFERENCES Game(MaGame)
);

CREATE TABLE MaGiamGia (
    MaMaGiamGia NUMBER DEFAULT SEQ_MaGiamGia.NEXTVAL PRIMARY KEY,
    Code VARCHAR2(50 CHAR) NOT NULL,
    SoTienGiam NUMBER(12,2) DEFAULT 0 NOT NULL,
    GioiHanSuDung NUMBER DEFAULT 0 NOT NULL,
    LuotDung NUMBER DEFAULT 0 NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayHetHan DATE NOT NULL,
    TongGiaToiThieu NUMBER(12,2) DEFAULT 0 NOT NULL,
    TrangThai VARCHAR2(30 CHAR) DEFAULT 'Đang hiệu lực' NOT NULL,
    MoTa VARCHAR2(500 CHAR),
    CONSTRAINT UQ_MaGiamGia_Code UNIQUE (Code),
    CONSTRAINT CK_MaGiamGia_TrangThai CHECK (TrangThai IN ('Đang hiệu lực', 'Hết hiệu lực')),
    CONSTRAINT CK_MaGiamGia_Ngay CHECK (NgayBatDau <= NgayHetHan),
    CONSTRAINT CK_MaGiamGia_Luot CHECK (GioiHanSuDung >= 0 AND LuotDung >= 0 AND LuotDung <= GioiHanSuDung),
    CONSTRAINT CK_MaGiamGia_Tien CHECK (SoTienGiam >= 0 AND TongGiaToiThieu >= 0)
);

CREATE TABLE GiaoDich (
    MaGD NUMBER DEFAULT SEQ_GiaoDich.NEXTVAL PRIMARY KEY,
    MaNguoiChoi NUMBER NOT NULL,
    MaMaGiamGia NUMBER,
    TongTienGoc NUMBER(12,2) DEFAULT 0 NOT NULL,
    TongGiamGia NUMBER(12,2) DEFAULT 0 NOT NULL,
    TongThanhToan NUMBER(12,2) DEFAULT 0 NOT NULL,
    PhuongThucThanhToan VARCHAR2(50 CHAR) NOT NULL,
    NgayGD DATE DEFAULT SYSDATE NOT NULL,
    TrangThai VARCHAR2(30 CHAR) DEFAULT 'Chờ thanh toán' NOT NULL,
    CONSTRAINT CK_GiaoDich_TrangThai CHECK (TrangThai IN ('Chờ thanh toán', 'Thành công', 'Thất bại')),
    CONSTRAINT CK_GiaoDich_Tien CHECK (TongTienGoc >= 0 AND TongGiamGia >= 0 AND TongThanhToan >= 0),
    CONSTRAINT FK_GiaoDich_NguoiChoi FOREIGN KEY (MaNguoiChoi) REFERENCES NguoiChoi(MaNguoiChoi),
    CONSTRAINT FK_GiaoDich_MaGiamGia FOREIGN KEY (MaMaGiamGia) REFERENCES MaGiamGia(MaMaGiamGia)
);

CREATE TABLE ChiTietGiaoDich (
    MaGD NUMBER NOT NULL,
    MaGame NUMBER NOT NULL,
    GiaGoc NUMBER(12,2) NOT NULL,
    SoTienGiamKM NUMBER(12,2) DEFAULT 0 NOT NULL,
    GiaBan NUMBER(12,2) NOT NULL,
    CONSTRAINT PK_ChiTietGiaoDich PRIMARY KEY (MaGD, MaGame),
    CONSTRAINT CK_CTGD_Tien CHECK (GiaGoc >= 0 AND SoTienGiamKM >= 0 AND GiaBan >= 0),
    CONSTRAINT CK_CTGD_GiaBan CHECK (GiaBan = GiaGoc - SoTienGiamKM),
    CONSTRAINT FK_CTGD_GiaoDich FOREIGN KEY (MaGD) REFERENCES GiaoDich(MaGD),
    CONSTRAINT FK_CTGD_Game FOREIGN KEY (MaGame) REFERENCES Game(MaGame)
);

CREATE TABLE SoHuuGame (
    MaNguoiChoi NUMBER NOT NULL,
    MaGame NUMBER NOT NULL,
    MaGD NUMBER NOT NULL,
    NgaySoHuu DATE DEFAULT SYSDATE NOT NULL,
    SoGioChoi NUMBER DEFAULT 0 NOT NULL,
    ThanhTuuDatDuoc VARCHAR2(500 CHAR),
    CONSTRAINT PK_SoHuuGame PRIMARY KEY (MaNguoiChoi, MaGame),
    CONSTRAINT CK_SoHuu_GioChoi CHECK (SoGioChoi >= 0),
    CONSTRAINT FK_SoHuu_NguoiChoi FOREIGN KEY (MaNguoiChoi) REFERENCES NguoiChoi(MaNguoiChoi),
    CONSTRAINT FK_SoHuu_Game FOREIGN KEY (MaGame) REFERENCES Game(MaGame),
    CONSTRAINT FK_SoHuu_GiaoDich FOREIGN KEY (MaGD) REFERENCES GiaoDich(MaGD)
);

CREATE TABLE DanhGia (
    MaDanhGia NUMBER DEFAULT SEQ_DanhGia.NEXTVAL PRIMARY KEY,
    MaGame NUMBER NOT NULL,
    MaNguoiChoi NUMBER NOT NULL,
    DiemDanhGia NUMBER(1) NOT NULL,
    NoiDung VARCHAR2(1000 CHAR),
    NgayDanhGia DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT UQ_DanhGia_NguoiChoi_Game UNIQUE (MaNguoiChoi, MaGame),
    CONSTRAINT CK_DanhGia_Diem CHECK (DiemDanhGia BETWEEN 1 AND 5),
    CONSTRAINT FK_DanhGia_Game FOREIGN KEY (MaGame) REFERENCES Game(MaGame),
    CONSTRAINT FK_DanhGia_NguoiChoi FOREIGN KEY (MaNguoiChoi) REFERENCES NguoiChoi(MaNguoiChoi)
);

CREATE TABLE KhuyenMai (
    MaKM NUMBER DEFAULT SEQ_KhuyenMai.NEXTVAL PRIMARY KEY,
    TenKM VARCHAR2(150 CHAR) NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    TrangThai VARCHAR2(30 CHAR) DEFAULT 'Đang hiệu lực' NOT NULL,
    NoiDung VARCHAR2(1000 CHAR),
    CONSTRAINT CK_KhuyenMai_TrangThai CHECK (TrangThai IN ('Đang hiệu lực', 'Hết hiệu lực')),
    CONSTRAINT CK_KhuyenMai_Ngay CHECK (NgayBatDau <= NgayKetThuc)
);

CREATE TABLE ChiTietKhuyenMai (
    MaKM NUMBER NOT NULL,
    MaGame NUMBER NOT NULL,
    PhanTramKM NUMBER(5,2) NOT NULL,
    CONSTRAINT PK_ChiTietKhuyenMai PRIMARY KEY (MaKM, MaGame),
    CONSTRAINT CK_CTKM_PhanTram CHECK (PhanTramKM > 0 AND PhanTramKM <= 100),
    CONSTRAINT FK_CTKM_KhuyenMai FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM),
    CONSTRAINT FK_CTKM_Game FOREIGN KEY (MaGame) REFERENCES Game(MaGame)
);

CREATE TABLE Ticket (
    MaTicket NUMBER DEFAULT SEQ_Ticket.NEXTVAL PRIMARY KEY,
    LoaiYeuCau VARCHAR2(50 CHAR) NOT NULL,
    NoiDung VARCHAR2(1000 CHAR) NOT NULL,
    MaNguoiChoi NUMBER NOT NULL,
    MaGame NUMBER,
    MaGD NUMBER,
    NgayTao DATE DEFAULT SYSDATE NOT NULL,
    TrangThai VARCHAR2(30 CHAR) DEFAULT 'Chờ xử lý' NOT NULL,
    MaNVXuLy NUMBER,
    NoiDungPhanHoi VARCHAR2(1000 CHAR),
    NgayXuLy DATE,
    CONSTRAINT CK_Ticket_TrangThai CHECK (TrangThai IN ('Chờ xử lý', 'Đang xử lý', 'Đã xử lý')),
    CONSTRAINT CK_Ticket_XuLy CHECK (
        TrangThai <> 'Đã xử lý'
        OR (MaNVXuLy IS NOT NULL AND NoiDungPhanHoi IS NOT NULL AND NgayXuLy IS NOT NULL)
    ),
    CONSTRAINT CK_Ticket_NgayXuLy CHECK (NgayXuLy IS NULL OR NgayXuLy >= NgayTao),
    CONSTRAINT FK_Ticket_NguoiChoi FOREIGN KEY (MaNguoiChoi) REFERENCES NguoiChoi(MaNguoiChoi),
    CONSTRAINT FK_Ticket_Game FOREIGN KEY (MaGame) REFERENCES Game(MaGame),
    CONSTRAINT FK_Ticket_GiaoDich FOREIGN KEY (MaGD) REFERENCES GiaoDich(MaGD),
    CONSTRAINT FK_Ticket_NhanVien FOREIGN KEY (MaNVXuLy) REFERENCES NhanVien(MaNV)
);

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

        UPDATE Game g
        SET g.LuotMua = NVL(g.LuotMua, 0) + 1
        WHERE g.MaGame IN (
            SELECT ct.MaGame
            FROM ChiTietGiaoDich ct
            WHERE ct.MaGD = :NEW.MaGD
        );

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


