# Đối chiếu code với thiết kế đồ án

## Vai trò

Code giữ đúng hai tầng phân quyền trong tài liệu:

| Thiết kế | Code |
|---|---|
| `TaiKhoan.LoaiTaiKhoan = 'Người chơi'` | `LoaiTaiKhoan.NGUOI_CHOI` |
| `TaiKhoan.LoaiTaiKhoan = 'Nhà phát triển'` | `LoaiTaiKhoan.NHA_PHAT_TRIEN` |
| `TaiKhoan.LoaiTaiKhoan = 'Nhân viên'` | `LoaiTaiKhoan.NHAN_VIEN` |
| `NhanVien.VaiTro = 'Quản lý nền tảng'` | `VaiTroNhanVien.QUAN_LY_NEN_TANG` |
| `NhanVien.VaiTro = 'Kiểm duyệt viên'` | `VaiTroNhanVien.KIEM_DUYET_VIEN` |
| `NhanVien.VaiTro = 'Marketing'` | `VaiTroNhanVien.MARKETING` |
| `NhanVien.VaiTro = 'CSKH'` | `VaiTroNhanVien.CSKH` |

## Luồng kiến trúc bám sequence

Các màn hình JavaFX không gọi trực tiếp database. Luồng code hiện tại đi theo thứ tự trong sơ đồ sequence đã thiết kế:

`View (JavaFX)` -> `Controller` -> `Oracle Database`

Package `controller` là lớp điều khiển nghiệp vụ/use case và cũng chứa phần JDBC/SQL tương ứng, nên không còn package `dao` tách riêng. Tên controller dùng tiếng Việt không dấu để dễ đọc khi bảo vệ nhưng vẫn ổn định với Maven/Windows:

- `XacThucTaiKhoanController`: đăng nhập, đăng ký tài khoản người chơi, đăng ký tài khoản nhà phát triển.
- `QuanLyThongTinGameController`: tra cứu game trên cửa hàng, quản lý thư viện game, đánh giá game, gửi game mới.
- `QuanLyMuaHangController`: quản lý wishlist, quản lý giỏ hàng, thanh toán, tra cứu lịch sử giao dịch.
- `QuanLyDanhMucGameController`: quản lý media game, thể loại game, phiên bản game và cập nhật trạng thái game.
- `QuanLyYeuCauPhatHanhController`: quản lý, tra cứu và xử lý yêu cầu phát hành game.
- `QuanLyKhuyenMaiController`: quản lý chương trình khuyến mãi và danh sách game trong chương trình khuyến mãi.
- `QuanLyMaGiamGiaController`: quản lý, thêm, cập nhật và tra cứu mã giảm giá.
- `QuanLyTicketHoTroController`: tạo ticket hỗ trợ, tra cứu ticket hỗ trợ, nhận xử lý và phản hồi ticket.
- `QuanLyTaiKhoanController`: quản lý tài khoản người chơi và nhà phát triển.
- `QuanLyNhanVienController`: quản lý nhân viên, thêm mới nhân viên, tra cứu và cập nhật nhân viên.
- `QuanLyDoanhThuController`: quản lý doanh thu nền tảng và doanh thu nhà phát triển, xuất báo cáo CSV.
- `QuanLyHoSoController`: cập nhật hồ sơ và đổi mật khẩu.

## Bảng dữ liệu

Các file `sql/02TaoSequence.sql`, `sql/03TaoBang.sql`, `sql/04TaoRangBuoc.sql`, `sql/05TaoTrigger.sql`, `sql/06TaoStoredFunction.sql` và `sql/07TaoStoredProcedure.sql` tạo schema theo mô hình quan hệ trong tài liệu HQTCSDL:

- `TaiKhoan`
- `NhanVien`
- `NhaPhatTrien`
- `NguoiChoi`
- `TheLoai`
- `Game`
- `GameMedia`
- `DanhMucTheLoai`
- `PhienBanGame`
- `YeuCauPhatHanh`
- `Wishlist`
- `GioHang`
- `GiaoDich`
- `ChiTietGiaoDich`
- `SoHuuGame`
- `DanhGia`
- `KhuyenMai`
- `ChiTietKhuyenMai`
- `MaGiamGia`
- `Ticket`

## Ràng buộc quan trọng đã đưa vào SQL

- `LoaiTaiKhoan` chỉ nhận `Người chơi`, `Nhà phát triển`, `Nhân viên`.
- `VaiTro` nhân viên chỉ nhận `Quản lý nền tảng`, `Kiểm duyệt viên`, `Marketing`, `CSKH`.
- Trạng thái game: `Đang phát hành`, `Chưa phát hành`, `Đã gỡ bỏ`.
- Trạng thái yêu cầu phát hành: `Chờ duyệt`, `Đã duyệt`, `Từ chối`.
- Trạng thái giao dịch: `Chờ thanh toán`, `Thành công`, `Thất bại`.
- Trạng thái ticket: `Chờ xử lý`, `Đang xử lý`, `Đã xử lý`.
- Unique các nghiệp vụ chống trùng: username, thể loại, mã giảm giá, wishlist, giỏ hàng, sở hữu game, đánh giá, chi tiết khuyến mãi.

## Trigger nghiệp vụ

| Trigger | Ý nghĩa |
|---|---|
| `TRG_XuLyKhiGiaoDichThanhCong` | Khi giao dịch thành công, tự thêm `SoHuuGame`, tăng `LuotMua`, xóa game khỏi giỏ hàng/wishlist, tăng lượt dùng mã giảm giá. |
| `TRG_XuLyDuyetYCPH` | Khi yêu cầu phát hành được duyệt, tự chuyển `Game` và `PhienBanGame` sang trạng thái phát hành. |
| `TRG_KiemTraGioHang` | Không cho thêm game chưa phát hành, game đã sở hữu hoặc game vượt độ tuổi vào giỏ hàng. |
| `TRG_KiemTraDanhGia` | Chỉ cho đánh giá game đã sở hữu. |
| `TRG_KiemTraWishlist` | Chỉ cho thêm game đang phát hành và chưa sở hữu vào wishlist. |
| `TRG_TuDongGhiNhanNgayXuLyTicket` | Khi ticket đã xử lý, bắt buộc có nhân viên và nội dung phản hồi, tự ghi ngày xử lý. |

## Màn hình demo

| Màn hình | Bảng/use case liên quan |
|---|---|
| Login + đăng ký | `TaiKhoan`, `NhanVien`, `NhaPhatTrien`, `NguoiChoi`, `SP_DangKyNguoiChoi`, `SP_DangKyNhaPhatTrien` |
| Dashboard người chơi | Cửa hàng, thư viện cá nhân, ticket của chính người chơi |
| Dashboard nhà phát triển | Game của NPT, yêu cầu phát hành của NPT |
| Dashboard nhân viên | Chỉ hiển thị theo vai trò nhân viên |
| Hồ sơ + đổi mật khẩu | Cập nhật hồ sơ theo loại tài khoản, đổi `TaiKhoan.MatKhau` |
| Quản lý người chơi | `NguoiChoi`, `TaiKhoan`; tra cứu và khóa/mở tài khoản |
| Quản lý nhà phát triển | `NhaPhatTrien`, `TaiKhoan`; tra cứu và cập nhật `TyLeChiaSe` |
| Quản lý nhân viên | `NhanVien`, `TaiKhoan`; thêm nhân viên theo 4 `VaiTro` |
| Cửa hàng | `Game`, `TheLoai`, `KhuyenMai`, `ChiTietKhuyenMai` |
| Wishlist | `Wishlist`; thêm, xóa, tra cứu |
| Giỏ hàng + thanh toán | `GioHang`, `GiaoDich`, `ChiTietGiaoDich`, `SP_ThemGameVaoGioHang`, `SP_TaoGiaoDichTuGioHang`, `SP_XacNhanThanhToan`, trigger tạo `SoHuuGame` |
| Lịch sử giao dịch | `GiaoDich`, `ChiTietGiaoDich` |
| Thư viện | `SoHuuGame`, `DanhGia`; tải game demo, thêm/cập nhật/xóa đánh giá |
| Gửi game mới | `Game`, `PhienBanGame`, `YeuCauPhatHanh` |
| Quản lý game NPT | Cập nhật thông tin game thuộc NPT |
| Media game | `GameMedia`; thêm, cập nhật, xóa, tra cứu |
| Phiên bản game | `PhienBanGame`; thêm phiên bản và tạo yêu cầu phát hành |
| Thể loại game | `TheLoai`, `DanhMucTheLoai`; thêm, cập nhật, gắn thể loại vào game |
| Duyệt phát hành | `YeuCauPhatHanh`, trigger cập nhật `Game` |
| Khuyến mãi | `KhuyenMai`, `ChiTietKhuyenMai`; thêm/cập nhật chương trình, thêm/cập nhật game trong khuyến mãi |
| Mã giảm giá | `MaGiamGia`; thêm, cập nhật, tra cứu |
| Ticket CSKH | `Ticket`, `NhanVien` |
| Báo cáo doanh thu | `GiaoDich`, `ChiTietGiaoDich`, `SF_TinhDoanhThuNPT`; tra cứu và xuất CSV |

## Ma trận phân quyền UI hiện tại

| Vai trò | Chức năng trên menu |
|---|---|
| Người chơi | Trang chủ, cửa hàng, wishlist, giỏ hàng, thư viện, lịch sử giao dịch, ticket, hồ sơ |
| Nhà phát triển | Studio dashboard, game của tôi, phiên bản, media, yêu cầu phát hành, gửi game mới, doanh thu NPT, hồ sơ |
| Nhân viên - Quản lý nền tảng | Quản lý người chơi, nhà phát triển, nhân viên, kho game, báo cáo doanh thu, hồ sơ |
| Nhân viên - Kiểm duyệt viên | Duyệt yêu cầu phát hành, kho game, thể loại, media, phiên bản, hồ sơ |
| Nhân viên - Marketing | Quản lý khuyến mãi, mã giảm giá, kho game, hồ sơ |
| Nhân viên - CSKH | Xử lý ticket, kho game, hồ sơ |

## Database Objects

Trigger:

- `TRG_XuLyKhiGiaoDichThanhCong`
- `TRG_XuLyDuyetYCPH`
- `TRG_KiemTraGioHang`
- `TRG_KiemTraDanhGia`
- `TRG_KiemTraWishlist`
- `TRG_TuDongGhiNhanNgayXuLyTicket`

Stored procedure:

- `SP_DangKyNguoiChoi`
- `SP_DangKyNhaPhatTrien`
- `SP_TaoGame`
- `SP_TaoYeuCauPhatHanh`
- `SP_XuLyYeuCauPhatHanh`
- `SP_ThemGameVaoGioHang`
- `SP_TaoGiaoDichTuGioHang`
- `SP_XacNhanThanhToan`
- `SP_XuLyTicket`
- `SP_ThemGameVaoKhuyenMai`
- `SP_TaoMaGiamGia`

Stored function:

- `SF_TinhGiaHienTai`
- `SF_KiemTraMaGiamGia`
- `SF_KiemTraDoTuoi`
- `SF_KiemTraSoHuuGame`
- `SF_TinhTongTienGioHang`
- `SF_TinhDiemTrungBinhGame`
- `SF_TinhDoanhThuNPT`
- `SF_TongChiTieuNguoiChoi`








