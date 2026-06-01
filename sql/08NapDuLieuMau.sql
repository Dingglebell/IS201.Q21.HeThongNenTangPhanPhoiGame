-- Dữ liệu mẫu phục vụ demo JavaFX.
-- Mật khẩu của tất cả tài khoản demo là: 123456
-- SHA-256(123456) = 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92

INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
VALUES (1, 'player01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Người chơi', 'Đang hoạt động');
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
VALUES (2, 'dev01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Nhà phát triển', 'Đang hoạt động');
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
VALUES (3, 'manager01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Nhân viên', 'Đang hoạt động');
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
VALUES (4, 'moderator01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Nhân viên', 'Đang hoạt động');
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
VALUES (5, 'marketing01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Nhân viên', 'Đang hoạt động');
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
VALUES (6, 'cskh01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Nhân viên', 'Đang hoạt động');
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
VALUES (7, 'player02', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Người chơi', 'Đang hoạt động');

INSERT INTO NguoiChoi (MaNguoiChoi, MaTaiKhoan, TenHienThi, NgaySinh, Email, SDT, QuocGia)
VALUES (1, 1, 'MinhKhang', DATE '2001-04-12', 'player01@example.com', '0901000001', 'Việt Nam');
INSERT INTO NguoiChoi (MaNguoiChoi, MaTaiKhoan, TenHienThi, NgaySinh, Email, SDT, QuocGia)
VALUES (2, 7, 'QuangHuy', DATE '1999-09-21', 'player02@example.com', '0901000002', 'Việt Nam');

INSERT INTO NhaPhatTrien (MaNPT, MaTaiKhoan, TenNPT, LoaiNPT, Email, SDT, DiaChi, TyLeChiaSe)
VALUES (1, 2, 'Lotus Indie Studio', 'Studio', 'dev01@example.com', '0912000001', 'TP. Hồ Chí Minh', 0.70);

INSERT INTO NhanVien (MaNV, MaTaiKhoan, HoTen, VaiTro, Email, SDT)
VALUES (1, 3, 'Nguyễn An Quản', 'Quản lý nền tảng', 'manager01@example.com', '0923000001');
INSERT INTO NhanVien (MaNV, MaTaiKhoan, HoTen, VaiTro, Email, SDT)
VALUES (2, 4, 'Trần Bình Duyệt', 'Kiểm duyệt viên', 'moderator01@example.com', '0923000002');
INSERT INTO NhanVien (MaNV, MaTaiKhoan, HoTen, VaiTro, Email, SDT)
VALUES (3, 5, 'Lê Chi Marketing', 'Marketing', 'marketing01@example.com', '0923000003');
INSERT INTO NhanVien (MaNV, MaTaiKhoan, HoTen, VaiTro, Email, SDT)
VALUES (4, 6, 'Phạm Dũng CSKH', 'CSKH', 'cskh01@example.com', '0923000004');

INSERT INTO TheLoai (MaTheLoai, TenTheLoai, MoTa) VALUES (1, 'Hành động', 'Game hành động nhịp độ cao.');
INSERT INTO TheLoai (MaTheLoai, TenTheLoai, MoTa) VALUES (2, 'Phiêu lưu', 'Khám phá cốt truyện và thế giới.');
INSERT INTO TheLoai (MaTheLoai, TenTheLoai, MoTa) VALUES (3, 'Nhập vai', 'Phát triển nhân vật và nhiệm vụ.');
INSERT INTO TheLoai (MaTheLoai, TenTheLoai, MoTa) VALUES (4, 'Chiến thuật', 'Quản lý tài nguyên và ra quyết định.');
INSERT INTO TheLoai (MaTheLoai, TenTheLoai, MoTa) VALUES (5, 'Mô phỏng', 'Mô phỏng đời sống hoặc hệ thống.');

INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (1, 1, 'Neon Runner', 12, 180000, 'Đang phát hành', DATE '2026-03-01', 1, 'Game chạy parkour trong thành phố neon.');
INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (2, 1, 'Lotus Quest', 7, 220000, 'Đang phát hành', DATE '2026-04-10', 0, 'Game phiêu lưu lấy cảm hứng văn hóa Việt.');
INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (3, 1, 'Server Tycoon', 0, 150000, 'Đang phát hành', DATE '2026-05-01', 0, 'Game mô phỏng vận hành hạ tầng máy chủ.');
INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (4, 1, 'Mythic Frontier', 12, 250000, 'Chưa phát hành', NULL, 0, 'Game nhập vai đang chờ kiểm duyệt phát hành.');

INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (1, 1);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (2, 1);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (2, 2);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (3, 4);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (5, 3);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (4, 3);

INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (1, 1, 'Ảnh bìa', 'anhBiaGame/neonRunnerCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (2, 2, 'Ảnh bìa', 'anhBiaGame/lotusQuestCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (3, 3, 'Ảnh bìa', 'anhBiaGame/serverTycoonCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (17, 4, 'Ảnh bìa', 'anhBiaGame/mythicFrontierCover.jpg');

INSERT INTO PhienBanGame (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, NgayTao, TrangThai)
VALUES (1, 1, '1.0.0', 'Bản phát hành đầu tiên.', 'tepBuild/neonRunner1.0.zip', 2200, DATE '2026-02-25', 'Đang phát hành');
INSERT INTO PhienBanGame (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, NgayTao, TrangThai)
VALUES (2, 4, '0.9.0', 'Bản đề xuất phát hành.', 'tepBuild/mythicFrontier0.9.zip', 3600, DATE '2026-05-20', 'Chưa phát hành');

INSERT INTO YeuCauPhatHanh (MaYeuCau, MaNPT, MaGame, MaPhienBan, NgayYeuCau, TrangThai)
VALUES (1, 1, 4, 2, DATE '2026-05-21', 'Chờ duyệt');
INSERT INTO YeuCauPhatHanh (MaYeuCau, MaNPT, MaGame, MaPhienBan, NgayYeuCau, TrangThai, MaNVXuLy, NgayXuLy)
VALUES (2, 1, 1, 1, DATE '2026-02-26', 'Đã duyệt', 2, DATE '2026-02-27');

INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, TrangThai, NoiDung)
VALUES (1, 'Summer Game Fest', DATE '2026-05-01', DATE '2026-06-15', 'Đang hiệu lực', 'Ưu đãi mùa hè cho các game nổi bật.');
INSERT INTO ChiTietKhuyenMai (MaKM, MaGame, PhanTramKM) VALUES (1, 1, 20);
INSERT INTO ChiTietKhuyenMai (MaKM, MaGame, PhanTramKM) VALUES (1, 3, 15);

INSERT INTO MaGiamGia (MaMaGiamGia, Code, SoTienGiam, GioiHanSuDung, LuotDung, NgayBatDau, NgayHetHan, TongGiaToiThieu, TrangThai, MoTa)
VALUES (1, 'WELCOME50', 50000, 100, 0, DATE '2026-05-01', DATE '2026-12-31', 150000, 'Đang hiệu lực', 'Mã chào mừng người chơi mới.');

INSERT INTO GiaoDich (MaGD, MaNguoiChoi, MaMaGiamGia, TongTienGoc, TongGiamGia, TongThanhToan, PhuongThucThanhToan, NgayGD, TrangThai)
VALUES (1, 1, NULL, 180000, 36000, 144000, 'Ví điện tử', DATE '2026-05-05', 'Thành công');
INSERT INTO ChiTietGiaoDich (MaGD, MaGame, GiaGoc, SoTienGiamKM, GiaBan)
VALUES (1, 1, 180000, 36000, 144000);
INSERT INTO SoHuuGame (MaNguoiChoi, MaGame, MaGD, NgaySoHuu, SoGioChoi, ThanhTuuDatDuoc)
VALUES (1, 1, 1, DATE '2026-05-05', 12, 'Hoàn thành màn đầu tiên');

INSERT INTO GioHang (MaNguoiChoi, MaGame, NgayThem) VALUES (1, 2, SYSDATE);
INSERT INTO Wishlist (MaNguoiChoi, MaGame, NgayThem) VALUES (1, 3, SYSDATE);

INSERT INTO DanhGia (MaDanhGia, MaGame, MaNguoiChoi, DiemDanhGia, NoiDung, NgayDanhGia)
VALUES (1, 1, 1, 5, 'Gameplay nhanh và nhạc nền rất cuốn.', DATE '2026-05-10');

INSERT INTO Ticket (MaTicket, LoaiYeuCau, NoiDung, MaNguoiChoi, MaGame, MaGD, NgayTao, TrangThai)
VALUES (1, 'Thanh toán', 'Tôi muốn kiểm tra trạng thái giao dịch mua game.', 1, 1, 1, SYSDATE, 'Chờ xử lý');
INSERT INTO Ticket (MaTicket, LoaiYeuCau, NoiDung, MaNguoiChoi, MaGame, MaGD, NgayTao, TrangThai, MaNVXuLy, NoiDungPhanHoi, NgayXuLy)
VALUES (2, 'Lỗi game', 'Game bị crash ở màn 2.', 1, 1, NULL, DATE '2026-05-12', 'Đã xử lý', 4, 'Đã ghi nhận và chuyển thông tin bản vá cho nhà phát triển.', DATE '2026-05-13');

-- Dữ liệu mở rộng cho demo đầy đủ các actor/use case.
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
VALUES (8, 'player03', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Người chơi', 'Đang hoạt động');
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
VALUES (9, 'dev02', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Nhà phát triển', 'Đang hoạt động');
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
VALUES (10, 'playerlocked', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Người chơi', 'Bị khóa');

INSERT INTO NguoiChoi (MaNguoiChoi, MaTaiKhoan, TenHienThi, NgaySinh, Email, SDT, QuocGia)
VALUES (3, 8, 'LanAnh', DATE '2002-11-08', 'player03@example.com', '0901000003', 'Việt Nam');
INSERT INTO NguoiChoi (MaNguoiChoi, MaTaiKhoan, TenHienThi, NgaySinh, Email, SDT, QuocGia)
VALUES (4, 10, 'TaiKhoanViPham', DATE '1998-03-18', 'locked@example.com', '0901000004', 'Việt Nam');

INSERT INTO NhaPhatTrien (MaNPT, MaTaiKhoan, TenNPT, LoaiNPT, Email, SDT, DiaChi, TyLeChiaSe)
VALUES (2, 9, 'Aurora Pixel Works', 'Studio', 'dev02@example.com', '0912000002', 'Đà Nẵng', 0.68);

INSERT INTO TheLoai (MaTheLoai, TenTheLoai, MoTa) VALUES (6, 'Kinh dị', 'Game tạo cảm giác hồi hộp và khám phá bí ẩn.');
INSERT INTO TheLoai (MaTheLoai, TenTheLoai, MoTa) VALUES (7, 'Thể thao', 'Game mô phỏng thi đấu thể thao.');

INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (5, 1, 'Cyber Kitchen', 0, 99000, 'Đang phát hành', DATE '2026-05-18', 1, 'Game co-op quản lý bếp ăn ngoài không gian.');
INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (6, 2, 'Shadow Valley', 16, 260000, 'Đang phát hành', DATE '2026-04-22', 1, 'Game kinh dị sinh tồn trong thung lũng bị bỏ hoang.');
INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (7, 2, 'Pixel Striker', 7, 175000, 'Đang phát hành', DATE '2026-05-10', 1, 'Game thể thao arcade nhịp độ nhanh.');
INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (8, 2, 'Echoes of Rain', 12, 210000, 'Chưa phát hành', NULL, 0, 'Game phiêu lưu đang chờ kiểm duyệt bản phát hành đầu tiên.');
INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (9, 1, 'Mecha Garden', 7, 185000, 'Đang phát hành', DATE '2026-02-14', 0, 'Game chiến thuật chăm sóc vườn robot.');
INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (10, 2, 'Trash Clicker 9', 0, 50000, 'Đang phát hành', DATE '2026-05-02', 0, 'Game chất lượng thấp dùng để demo tình huống gỡ game vi phạm.');

INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (4, 5);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (5, 5);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (6, 6);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (7, 7);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (2, 8);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (4, 9);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (1, 10);

INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (4, 5, 'Ảnh bìa', 'anhBiaGame/cyberKitchenCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (5, 6, 'Ảnh bìa', 'anhBiaGame/shadowValleyCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (6, 7, 'Ảnh bìa', 'anhBiaGame/pixelStrikerCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (7, 8, 'Ảnh bìa', 'anhBiaGame/echoesOfRainCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (8, 9, 'Ảnh bìa', 'anhBiaGame/mechaGardenCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (9, 10, 'Ảnh bìa', 'anhBiaGame/trashClickerCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (10, 6, 'Video', 'tepMedia/taiLen/shadowValleyTrailer.mp4');

INSERT INTO PhienBanGame (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, NgayTao, TrangThai)
VALUES (3, 5, '1.0.0', 'Bản phát hành Cyber Kitchen.', 'tepBuild/cyberKitchen1.0.zip', 1300, DATE '2026-05-14', 'Đang phát hành');
INSERT INTO PhienBanGame (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, NgayTao, TrangThai)
VALUES (4, 6, '1.0.0', 'Bản phát hành Shadow Valley.', 'tepBuild/shadowValley1.0.zip', 4100, DATE '2026-04-18', 'Đang phát hành');
INSERT INTO PhienBanGame (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, NgayTao, TrangThai)
VALUES (5, 7, '1.0.0', 'Bản phát hành Pixel Striker.', 'tepBuild/pixelStriker1.0.zip', 900, DATE '2026-05-06', 'Đang phát hành');
INSERT INTO PhienBanGame (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, NgayTao, TrangThai)
VALUES (6, 8, '0.8.0', 'Bản đầu tiên gửi kiểm duyệt.', 'tepBuild/echoesOfRain0.8.zip', 2800, DATE '2026-05-22', 'Chưa phát hành');
INSERT INTO PhienBanGame (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, NgayTao, TrangThai)
VALUES (7, 9, '1.0.0', 'Bản phát hành Mecha Garden.', 'tepBuild/mechaGarden1.0.zip', 1600, DATE '2026-02-10', 'Đang phát hành');

INSERT INTO YeuCauPhatHanh (MaYeuCau, MaNPT, MaGame, MaPhienBan, NgayYeuCau, TrangThai)
VALUES (3, 2, 8, 6, DATE '2026-05-23', 'Chờ duyệt');
INSERT INTO YeuCauPhatHanh (MaYeuCau, MaNPT, MaGame, MaPhienBan, NgayYeuCau, TrangThai, MaNVXuLy, LyDoTuChoi, NgayXuLy)
VALUES (4, 2, 10, NULL, DATE '2026-05-04', 'Từ chối', 2, 'Nội dung chưa đạt tiêu chuẩn kiểm duyệt.', DATE '2026-05-05');

INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, TrangThai, NoiDung)
VALUES (2, 'Indie Spotlight', DATE '2026-05-20', DATE '2026-06-30', 'Đang hiệu lực', 'Ưu đãi cho game indie nổi bật.');
INSERT INTO ChiTietKhuyenMai (MaKM, MaGame, PhanTramKM) VALUES (2, 5, 10);
INSERT INTO ChiTietKhuyenMai (MaKM, MaGame, PhanTramKM) VALUES (2, 7, 25);
INSERT INTO ChiTietKhuyenMai (MaKM, MaGame, PhanTramKM) VALUES (2, 9, 15);

INSERT INTO MaGiamGia (MaMaGiamGia, Code, SoTienGiam, GioiHanSuDung, LuotDung, NgayBatDau, NgayHetHan, TongGiaToiThieu, TrangThai, MoTa)
VALUES (2, 'INDIE25', 25000, 50, 1, DATE '2026-05-20', DATE '2026-06-30', 100000, 'Đang hiệu lực', 'Mã cho chiến dịch Indie Spotlight.');
INSERT INTO MaGiamGia (MaMaGiamGia, Code, SoTienGiam, GioiHanSuDung, LuotDung, NgayBatDau, NgayHetHan, TongGiaToiThieu, TrangThai, MoTa)
VALUES (3, 'EXPIRED10', 10000, 10, 10, DATE '2026-01-01', DATE '2026-02-01', 50000, 'Hết hiệu lực', 'Mã hết hạn dùng để demo tra cứu.');

INSERT INTO GiaoDich (MaGD, MaNguoiChoi, MaMaGiamGia, TongTienGoc, TongGiamGia, TongThanhToan, PhuongThucThanhToan, NgayGD, TrangThai)
VALUES (2, 2, 2, 175000, 68750, 106250, 'Ví điện tử', DATE '2026-05-21', 'Thành công');
INSERT INTO ChiTietGiaoDich (MaGD, MaGame, GiaGoc, SoTienGiamKM, GiaBan)
VALUES (2, 7, 175000, 43750, 131250);
INSERT INTO SoHuuGame (MaNguoiChoi, MaGame, MaGD, NgaySoHuu, SoGioChoi, ThanhTuuDatDuoc)
VALUES (2, 7, 2, DATE '2026-05-21', 5, 'Thắng giải tân binh');

INSERT INTO GiaoDich (MaGD, MaNguoiChoi, MaMaGiamGia, TongTienGoc, TongGiamGia, TongThanhToan, PhuongThucThanhToan, NgayGD, TrangThai)
VALUES (3, 3, NULL, 260000, 0, 260000, 'Thẻ ngân hàng', DATE '2026-05-24', 'Thành công');
INSERT INTO ChiTietGiaoDich (MaGD, MaGame, GiaGoc, SoTienGiamKM, GiaBan)
VALUES (3, 6, 260000, 0, 260000);
INSERT INTO SoHuuGame (MaNguoiChoi, MaGame, MaGD, NgaySoHuu, SoGioChoi, ThanhTuuDatDuoc)
VALUES (3, 6, 3, DATE '2026-05-24', 2, 'Sống sót đêm đầu tiên');

INSERT INTO GiaoDich (MaGD, MaNguoiChoi, MaMaGiamGia, TongTienGoc, TongGiamGia, TongThanhToan, PhuongThucThanhToan, NgayGD, TrangThai)
VALUES (4, 3, NULL, 99000, 9900, 89100, 'Ví điện tử', DATE '2026-05-25', 'Chờ thanh toán');
INSERT INTO ChiTietGiaoDich (MaGD, MaGame, GiaGoc, SoTienGiamKM, GiaBan)
VALUES (4, 5, 99000, 9900, 89100);

INSERT INTO GioHang (MaNguoiChoi, MaGame, NgayThem) VALUES (2, 5, SYSDATE);
INSERT INTO GioHang (MaNguoiChoi, MaGame, NgayThem) VALUES (3, 7, SYSDATE);
INSERT INTO Wishlist (MaNguoiChoi, MaGame, NgayThem) VALUES (2, 6, SYSDATE);
INSERT INTO Wishlist (MaNguoiChoi, MaGame, NgayThem) VALUES (3, 9, SYSDATE);
INSERT INTO Wishlist (MaNguoiChoi, MaGame, NgayThem) VALUES (3, 5, SYSDATE);

INSERT INTO DanhGia (MaDanhGia, MaGame, MaNguoiChoi, DiemDanhGia, NoiDung, NgayDanhGia)
VALUES (2, 7, 2, 4, 'Chơi nhanh, vui, hợp chơi cùng bạn bè.', DATE '2026-05-22');
INSERT INTO DanhGia (MaDanhGia, MaGame, MaNguoiChoi, DiemDanhGia, NoiDung, NgayDanhGia)
VALUES (3, 6, 3, 5, 'Không khí rất tốt, âm thanh gây hồi hộp.', DATE '2026-05-25');

INSERT INTO Ticket (MaTicket, LoaiYeuCau, NoiDung, MaNguoiChoi, MaGame, MaGD, NgayTao, TrangThai)
VALUES (3, 'Tài khoản', 'Tôi muốn cập nhật lại email nhưng bị báo trùng.', 2, NULL, NULL, SYSDATE, 'Chờ xử lý');
INSERT INTO Ticket (MaTicket, LoaiYeuCau, NoiDung, MaNguoiChoi, MaGame, MaGD, NgayTao, TrangThai, MaNVXuLy)
VALUES (4, 'Lỗi game', 'Shadow Valley bị tụt FPS ở đoạn mở đầu.', 3, 6, 3, SYSDATE, 'Đang xử lý', 4);

-- Dữ liệu bổ sung để demo cửa hàng, duyệt phát hành, khuyến mãi, giao dịch và báo cáo doanh thu.
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
VALUES (11, 'player04', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Người chơi', 'Đang hoạt động');
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
VALUES (12, 'player05', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Người chơi', 'Đang hoạt động');
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
VALUES (13, 'dev03', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Nhà phát triển', 'Đang hoạt động');

INSERT INTO NguoiChoi (MaNguoiChoi, MaTaiKhoan, TenHienThi, NgaySinh, Email, SDT, QuocGia)
VALUES (5, 11, 'BaoTran', DATE '2000-07-19', 'player04@example.com', '0901000005', 'Việt Nam');
INSERT INTO NguoiChoi (MaNguoiChoi, MaTaiKhoan, TenHienThi, NgaySinh, Email, SDT, QuocGia)
VALUES (6, 12, 'GiaBao', DATE '2004-02-03', 'player05@example.com', '0901000006', 'Việt Nam');

INSERT INTO NhaPhatTrien (MaNPT, MaTaiKhoan, TenNPT, LoaiNPT, Email, SDT, DiaChi, TyLeChiaSe)
VALUES (3, 13, 'Saigon Play Lab', 'Cá nhân', 'dev03@example.com', '0912000003', 'TP. Hồ Chí Minh', 0.72);

INSERT INTO TheLoai (MaTheLoai, TenTheLoai, MoTa) VALUES (8, 'Giải đố', 'Game tập trung vào tư duy logic và giải mã.');
INSERT INTO TheLoai (MaTheLoai, TenTheLoai, MoTa) VALUES (9, 'Giáo dục', 'Game vừa học vừa chơi.');

INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (11, 3, 'Sky Rail', 3, 120000, 'Đang phát hành', DATE '2026-05-11', 4, 'Game mô phỏng vận hành tuyến tàu trên cao.');
INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (12, 3, 'Dragon Market', 7, 145000, 'Đang phát hành', DATE '2026-05-16', 3, 'Game quản lý khu chợ fantasy với rồng và thương nhân.');
INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (13, 2, 'Silent Campus', 16, 230000, 'Đang phát hành', DATE '2026-05-19', 2, 'Game kinh dị học đường dùng để demo giới hạn độ tuổi.');
INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (14, 1, 'Ocean Farm', 0, 80000, 'Chưa phát hành', NULL, 0, 'Game nông trại đại dương đang chờ kiểm duyệt.');
INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (15, 3, 'Byte Dungeon', 12, 199000, 'Đang phát hành', DATE '2026-04-30', 5, 'Game nhập vai đánh theo lượt trong hầm ngục số hóa.');
INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, NgayPhatHanh, LuotMua, MoTa)
VALUES (16, 1, 'City Bus Pro', 0, 70000, 'Đã gỡ bỏ', DATE '2026-01-12', 1, 'Game bị gỡ để demo trạng thái không còn bán trên cửa hàng.');

INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (5, 11);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (4, 11);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (5, 12);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (8, 12);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (6, 13);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (5, 14);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (3, 15);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (8, 15);
INSERT INTO DanhMucTheLoai (MaTheLoai, MaGame) VALUES (5, 16);

INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (11, 11, 'Ảnh bìa', 'anhBiaGame/skyRailCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (12, 12, 'Ảnh bìa', 'anhBiaGame/dragonMarketCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (13, 13, 'Ảnh bìa', 'anhBiaGame/silentCampusCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (14, 14, 'Ảnh bìa', 'anhBiaGame/oceanFarmCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (15, 15, 'Ảnh bìa', 'anhBiaGame/byteDungeonCover.jpg');
INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia) VALUES (16, 16, 'Ảnh bìa', 'anhBiaGame/cityBusProCover.jpg');

INSERT INTO PhienBanGame (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, NgayTao, TrangThai)
VALUES (8, 11, '1.0.0', 'Bản phát hành Sky Rail.', 'tepBuild/skyRail1.0.zip', 1100, DATE '2026-05-08', 'Đang phát hành');
INSERT INTO PhienBanGame (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, NgayTao, TrangThai)
VALUES (9, 12, '1.0.0', 'Bản phát hành Dragon Market.', 'tepBuild/dragonMarket1.0.zip', 1500, DATE '2026-05-14', 'Đang phát hành');
INSERT INTO PhienBanGame (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, NgayTao, TrangThai)
VALUES (10, 13, '1.0.0', 'Bản phát hành Silent Campus.', 'tepBuild/silentCampus1.0.zip', 3300, DATE '2026-05-17', 'Đang phát hành');
INSERT INTO PhienBanGame (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, NgayTao, TrangThai)
VALUES (11, 14, '0.9.0', 'Bản gửi kiểm duyệt Ocean Farm.', 'tepBuild/oceanFarm0.9.zip', 780, DATE '2026-05-24', 'Chưa phát hành');
INSERT INTO PhienBanGame (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, NgayTao, TrangThai)
VALUES (12, 15, '1.0.0', 'Bản phát hành Byte Dungeon.', 'tepBuild/byteDungeon1.0.zip', 2400, DATE '2026-04-25', 'Đang phát hành');
INSERT INTO PhienBanGame (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, NgayTao, TrangThai)
VALUES (13, 16, '1.0.0', 'Bản đã bị gỡ khỏi nền tảng.', 'tepBuild/cityBusPro1.0.zip', 600, DATE '2026-01-10', 'Chưa phát hành');

INSERT INTO YeuCauPhatHanh (MaYeuCau, MaNPT, MaGame, MaPhienBan, NgayYeuCau, TrangThai)
VALUES (5, 1, 14, 11, DATE '2026-05-25', 'Chờ duyệt');
INSERT INTO YeuCauPhatHanh (MaYeuCau, MaNPT, MaGame, MaPhienBan, NgayYeuCau, TrangThai, MaNVXuLy, NgayXuLy)
VALUES (6, 3, 12, 9, DATE '2026-05-13', 'Đã duyệt', 2, DATE '2026-05-15');

INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, TrangThai, NoiDung)
VALUES (3, 'Weekend Cozy Sale', DATE '2026-05-24', DATE '2026-06-02', 'Đang hiệu lực', 'Khuyến mãi cuối tuần cho game mô phỏng và giải đố.');
INSERT INTO ChiTietKhuyenMai (MaKM, MaGame, PhanTramKM) VALUES (3, 11, 20);
INSERT INTO ChiTietKhuyenMai (MaKM, MaGame, PhanTramKM) VALUES (3, 12, 15);
INSERT INTO ChiTietKhuyenMai (MaKM, MaGame, PhanTramKM) VALUES (3, 15, 30);

INSERT INTO MaGiamGia (MaMaGiamGia, Code, SoTienGiam, GioiHanSuDung, LuotDung, NgayBatDau, NgayHetHan, TongGiaToiThieu, TrangThai, MoTa)
VALUES (4, 'DEMO100', 100000, 20, 2, DATE '2026-05-24', DATE '2026-06-30', 200000, 'Đang hiệu lực', 'Mã giảm sâu để demo thanh toán giỏ hàng nhiều game.');

INSERT INTO GiaoDich (MaGD, MaNguoiChoi, MaMaGiamGia, TongTienGoc, TongGiamGia, TongThanhToan, PhuongThucThanhToan, NgayGD, TrangThai)
VALUES (5, 5, 4, 319000, 183700, 135300, 'Ví điện tử', DATE '2026-05-26', 'Thành công');
INSERT INTO ChiTietGiaoDich (MaGD, MaGame, GiaGoc, SoTienGiamKM, GiaBan)
VALUES (5, 11, 120000, 24000, 96000);
INSERT INTO ChiTietGiaoDich (MaGD, MaGame, GiaGoc, SoTienGiamKM, GiaBan)
VALUES (5, 15, 199000, 59700, 139300);
INSERT INTO SoHuuGame (MaNguoiChoi, MaGame, MaGD, NgaySoHuu, SoGioChoi, ThanhTuuDatDuoc)
VALUES (5, 11, 5, DATE '2026-05-26', 4, 'Hoàn thành tuyến đầu tiên');
INSERT INTO SoHuuGame (MaNguoiChoi, MaGame, MaGD, NgaySoHuu, SoGioChoi, ThanhTuuDatDuoc)
VALUES (5, 15, 5, DATE '2026-05-26', 9, 'Hạ boss hầm ngục đầu tiên');

INSERT INTO GiaoDich (MaGD, MaNguoiChoi, MaMaGiamGia, TongTienGoc, TongGiamGia, TongThanhToan, PhuongThucThanhToan, NgayGD, TrangThai)
VALUES (6, 6, NULL, 145000, 21750, 123250, 'Thẻ ngân hàng', DATE '2026-05-26', 'Thành công');
INSERT INTO ChiTietGiaoDich (MaGD, MaGame, GiaGoc, SoTienGiamKM, GiaBan)
VALUES (6, 12, 145000, 21750, 123250);
INSERT INTO SoHuuGame (MaNguoiChoi, MaGame, MaGD, NgaySoHuu, SoGioChoi, ThanhTuuDatDuoc)
VALUES (6, 12, 6, DATE '2026-05-26', 3, 'Mở gian hàng rồng đầu tiên');

INSERT INTO GioHang (MaNguoiChoi, MaGame, NgayThem) VALUES (5, 12, SYSDATE);
INSERT INTO GioHang (MaNguoiChoi, MaGame, NgayThem) VALUES (6, 11, SYSDATE);
INSERT INTO Wishlist (MaNguoiChoi, MaGame, NgayThem) VALUES (5, 13, SYSDATE);
INSERT INTO Wishlist (MaNguoiChoi, MaGame, NgayThem) VALUES (6, 15, SYSDATE);

INSERT INTO DanhGia (MaDanhGia, MaGame, MaNguoiChoi, DiemDanhGia, NoiDung, NgayDanhGia)
VALUES (4, 11, 5, 4, 'Dễ chơi, hợp demo phần mô phỏng.', DATE '2026-05-26');
INSERT INTO DanhGia (MaDanhGia, MaGame, MaNguoiChoi, DiemDanhGia, NoiDung, NgayDanhGia)
VALUES (5, 12, 6, 5, 'Hình ảnh dễ thương, quản lý cửa hàng khá cuốn.', DATE '2026-05-26');

INSERT INTO Ticket (MaTicket, LoaiYeuCau, NoiDung, MaNguoiChoi, MaGame, MaGD, NgayTao, TrangThai)
VALUES (5, 'Hoàn tiền', 'Tôi muốn hỏi chính sách hoàn tiền cho game mua nhầm.', 5, 15, 5, SYSDATE, 'Chờ xử lý');
INSERT INTO Ticket (MaTicket, LoaiYeuCau, NoiDung, MaNguoiChoi, MaGame, MaGD, NgayTao, TrangThai, MaNVXuLy)
VALUES (6, 'Lỗi tải game', 'Không tải được bản build của Dragon Market.', 6, 12, 6, SYSDATE, 'Đang xử lý', 4);
COMMIT;



