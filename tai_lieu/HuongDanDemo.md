# Hướng dẫn giải thích code và demo giao diện

Tài liệu này dùng để ôn nhanh trước khi demo. Mục tiêu là nắm được luồng chạy, vai trò của từng lớp và thứ tự trình bày các chức năng chính.

## 1. Luồng chạy tổng quát

Ứng dụng được viết bằng JavaFX và chạy từ `App.java`.

Luồng chính:

```text
App
-> LoginView
-> XacThucTaiKhoanController
-> Oracle Database
-> MainView theo loại tài khoản/vai trò
```

- `App.java`: khởi tạo cửa sổ JavaFX, hiển thị màn hình đăng nhập, sau khi đăng nhập thành công thì chuyển sang `MainView`.
- `LoginView.java`: giao diện đăng nhập, đăng ký người chơi, đăng ký nhà phát triển.
- `MainView.java`: giao diện chính sau đăng nhập, tự dựng menu theo `LoaiTaiKhoan` và `VaiTroNhanVien`.
- `Database.java`: tạo kết nối JDBC đến Oracle.
- `AppConfig.java`: đọc cấu hình DB từ `db.properties` hoặc biến môi trường.

## 2. Tầng controller bám theo use case

Các màn hình JavaFX không xử lý SQL trực tiếp. View gọi controller, controller gọi JDBC/procedure/function/trigger trong Oracle.

| Controller | Nhóm use case tương ứng |
|---|---|
| `XacThucTaiKhoanController` | Đăng nhập, đăng ký người chơi, đăng ký nhà phát triển |
| `QuanLyThongTinGameController` | Tra cứu game trên cửa hàng, thư viện game, đánh giá game, gửi game mới |
| `QuanLyDanhMucGameController` | Quản lý media game, thể loại game, phiên bản game và trạng thái game |
| `QuanLyMuaHangController` | Wishlist, giỏ hàng, thanh toán, lịch sử giao dịch |
| `QuanLyYeuCauPhatHanhController` | Tạo, tra cứu, duyệt/từ chối yêu cầu phát hành |
| `QuanLyKhuyenMaiController` | Chương trình khuyến mãi, game trong khuyến mãi |
| `QuanLyMaGiamGiaController` | Thêm, cập nhật, tra cứu mã giảm giá |
| `QuanLyTicketHoTroController` | Tạo ticket, tra cứu ticket, nhận xử lý, phản hồi ticket |
| `QuanLyTaiKhoanController` | Quản lý người chơi, nhà phát triển |
| `QuanLyNhanVienController` | Quản lý nhân viên, thêm nhân viên, phân quyền |
| `QuanLyDoanhThuController` | Doanh thu nền tảng, doanh thu nhà phát triển, xuất CSV |
| `QuanLyHoSoController` | Cập nhật hồ sơ, đổi mật khẩu |

Khi demo, có thể giải thích ngắn gọn: "Các controller được đặt tên theo đúng nhóm use case trong sơ đồ sequence, ví dụ màn hình mã giảm giá gọi `QuanLyMaGiamGiaController`, màn hình khuyến mãi gọi `QuanLyKhuyenMaiController`."

## 3. Model quan trọng

Các model là `record` hoặc `enum`, chủ yếu dùng để đưa dữ liệu từ controller lên giao diện.

- `TaiKhoanDangNhap`: thông tin phiên đăng nhập: mã tài khoản, username, loại tài khoản, mã hồ sơ, vai trò nhân viên.
- `LoaiTaiKhoan`: gồm `NGUOI_CHOI`, `NHA_PHAT_TRIEN`, `NHAN_VIEN`.
- `VaiTroNhanVien`: gồm `QUAN_LY_NEN_TANG`, `KIEM_DUYET_VIEN`, `MARKETING`, `CSKH`.
- `ThongTinGame`: dữ liệu game hiển thị trên cửa hàng.
- `GameTrongGioHang`, `GameTrongThuVien`: dữ liệu cho giỏ hàng và thư viện.
- `YeuCauPhatHanh`: dữ liệu yêu cầu phát hành game.
- `ChuongTrinhKhuyenMai`, `GameTrongKhuyenMai`, `ThongTinMaGiamGia`: dữ liệu marketing.
- `ThongTinTicket`: dữ liệu ticket hỗ trợ.
- `DongBaoCaoDoanhThu`: dữ liệu báo cáo doanh thu.

## 4. Phân quyền giao diện

`MainView` nhận `TaiKhoanDangNhap session`, sau đó dựng menu theo tài khoản:

- Người chơi: cửa hàng, wishlist, giỏ hàng, thư viện, lịch sử giao dịch, ticket, hồ sơ.
- Nhà phát triển: studio dashboard, game của tôi, phiên bản, media, yêu cầu phát hành, gửi game mới, doanh thu NPT, hồ sơ.
- Quản lý nền tảng: quản lý người chơi, nhà phát triển, nhân viên, kho game, doanh thu nền tảng, hồ sơ.
- Kiểm duyệt viên: duyệt yêu cầu phát hành, kho game, thể loại, media, phiên bản, hồ sơ.
- Marketing: quản lý khuyến mãi, mã giảm giá, kho game, hồ sơ.
- CSKH: xử lý ticket, kho game, hồ sơ.

Điểm nên nói khi demo: cùng một `MainView`, nhưng menu khác nhau tùy dữ liệu đăng nhập trong DB.

## 5. Kịch bản demo đề xuất

### 5.1. Đăng nhập và phân quyền

Tài khoản demo đều dùng mật khẩu `123456`.

1. Đăng nhập `player01`.
2. Chỉ ra menu của người chơi.
3. Đăng xuất.
4. Đăng nhập `manager01`, `moderator01`, `marketing01` hoặc `cskh01` để thấy menu thay đổi.

### 5.2. Người chơi

Demo nhanh:

1. Mở `Cửa hàng`.
2. Tìm kiếm hoặc lọc thể loại.
3. Xem chi tiết game.
4. Thêm game vào wishlist hoặc giỏ hàng.
5. Mở giỏ hàng và thanh toán.
6. Mở thư viện để thấy game đã sở hữu.
7. Đánh giá game hoặc tạo ticket hỗ trợ.

Code liên quan:

- Store, thư viện, đánh giá: `QuanLyThongTinGameController`.
- Wishlist, giỏ hàng, thanh toán: `QuanLyMuaHangController`.
- Ticket người chơi: `QuanLyTicketHoTroController`.

### 5.3. Nhà phát triển

Demo nhanh:

1. Đăng nhập `dev01`.
2. Mở `Game của tôi`.
3. Cập nhật giá/mô tả game.
4. Quản lý media hoặc phiên bản.
5. Gửi game/phiên bản mới để tạo yêu cầu phát hành.
6. Xem doanh thu nhà phát triển.

Code liên quan:

- Game, thư viện, gửi game mới: `QuanLyThongTinGameController`.
- Media, thể loại, phiên bản: `QuanLyDanhMucGameController`.
- Yêu cầu phát hành: `QuanLyYeuCauPhatHanhController`.
- Doanh thu: `QuanLyDoanhThuController`.

### 5.4. Kiểm duyệt viên

Demo nhanh:

1. Đăng nhập `moderator01`.
2. Mở màn hình duyệt yêu cầu phát hành.
3. Chọn yêu cầu đang chờ duyệt.
4. Bấm duyệt hoặc từ chối.
5. Giải thích trigger `TRG_XuLyDuyetYCPH`: khi duyệt, DB tự cập nhật trạng thái game/phiên bản.

Code liên quan:

- `QuanLyYeuCauPhatHanhController.approve(...)`
- `QuanLyYeuCauPhatHanhController.reject(...)`
- Stored procedure `SP_XuLyYeuCauPhatHanh`
- Trigger `TRG_XuLyDuyetYCPH`

### 5.5. Marketing

Demo nhanh:

1. Đăng nhập `marketing01`.
2. Tạo/cập nhật chương trình khuyến mãi.
3. Chọn game và nhập phần trăm giảm.
4. Tạo hoặc cập nhật mã giảm giá.

Code liên quan:

- `QuanLyKhuyenMaiController`: chương trình khuyến mãi và game trong khuyến mãi.
- `QuanLyMaGiamGiaController`: mã giảm giá.
- Function `SF_TinhGiaHienTai`: tính giá hiện tại theo khuyến mãi.
- Function `SF_KiemTraMaGiamGia`: kiểm tra mã giảm giá khi thanh toán.

### 5.6. CSKH

Demo nhanh:

1. Đăng nhập `cskh01`.
2. Mở danh sách ticket.
3. Nhận xử lý ticket.
4. Nhập nội dung phản hồi và đóng ticket.

Code liên quan:

- `QuanLyTicketHoTroController.nhanXuLy(...)`
- `QuanLyTicketHoTroController.phanHoiTicket(...)`
- Procedure `SP_XuLyTicket`
- Trigger `TRG_TuDongGhiNhanNgayXuLyTicket`

### 5.7. Quản lý nền tảng

Demo nhanh:

1. Đăng nhập `manager01`.
2. Tra cứu người chơi và cập nhật trạng thái tài khoản.
3. Tra cứu nhà phát triển và cập nhật tỷ lệ chia sẻ doanh thu.
4. Tạo tài khoản nhân viên.
5. Mở báo cáo doanh thu nền tảng và xuất CSV.

Code liên quan:

- Người chơi/NPT: `QuanLyTaiKhoanController`.
- Nhân viên: `QuanLyNhanVienController`.
- Báo cáo: `QuanLyDoanhThuController`.

## 6. Các điểm kỹ thuật nên nhấn mạnh

- Mật khẩu không lưu dạng plain text, mà dùng SHA-256 trong `PasswordHasher`.
- Các thao tác nghiệp vụ quan trọng nằm ở stored procedure và trigger để DB tự bảo vệ dữ liệu.
- UI chỉ hiển thị chức năng theo vai trò, giảm nhầm lẫn khi sử dụng.
- Tên controller đã được đổi theo nhóm use case trong tài liệu thiết kế để dễ đối chiếu với sequence diagram.
- Các file SQL được đặt lại tên tiếng Việt không dấu, dễ chạy theo thứ tự:
  - `00_tao_user_game_platform.sql`
  - `01_tao_cau_truc_csdl.sql`
  - `02_nap_du_lieu_mau.sql`
  - ``
  - `99_cai_dat_day_du.sql`

