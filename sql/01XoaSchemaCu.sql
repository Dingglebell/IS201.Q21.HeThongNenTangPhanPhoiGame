-- Xóa object cũ theo đúng thứ tự phụ thuộc
-- File tách riêng để dễ trình bày phần HQTCSDL.

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


