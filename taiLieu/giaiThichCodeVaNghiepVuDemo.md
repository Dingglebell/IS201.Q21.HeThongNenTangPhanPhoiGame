# Giải thích code và nghiệp vụ khi demo

Tài liệu này dùng để vừa thao tác giao diện vừa mở code giải thích. Khi demo, nên đi theo luồng: màn hình đang bấm -> controller được gọi -> bảng/procedure/trigger trong Oracle -> kết quả quay lại UI.

## 1. Kiến trúc tổng quát

Luồng chính của ứng dụng:

```text
App.java
-> LoginView.java
-> XacThucTaiKhoanController.java
-> Oracle Database
-> MainView.java
-> Các controller nghiệp vụ
```

Ý nghĩa từng lớp:

- `App.java`: điểm khởi động JavaFX. Hàm `start(...)` tạo cửa sổ, gọi `showLogin()`, sau khi đăng nhập thành công thì chuyển sang `showMain(...)`.
- `LoginView.java`: màn đăng nhập, đăng ký người chơi, đăng ký nhà phát triển.
- `MainView.java`: màn chính sau đăng nhập. File này dựng menu theo loại tài khoản và vai trò nhân viên.
- `Database.java`: tạo kết nối JDBC tới Oracle bằng `DriverManager.getConnection(...)`.
- `AppConfig.java`: đọc `db.properties` hoặc biến môi trường để lấy URL, username, password DB.
- `controller/*`: xử lý nghiệp vụ/use case, gọi SQL, stored procedure, trigger gián tiếp.
- `model/*`: các `record` dùng để đưa dữ liệu từ DB lên table/card UI.

Câu giải thích mẫu:

> App được chia theo mô hình View - Controller - Database. View chỉ lo giao diện, Controller gom nghiệp vụ, còn các ràng buộc quan trọng được đưa xuống Oracle bằng constraint, trigger, procedure và function.

## 2. Đăng nhập và phân quyền

File cần mở:

- `src/main/java/com/gameplatform/ui/LoginView.java`
- `src/main/java/com/gameplatform/controller/XacThucTaiKhoanController.java`
- `src/main/java/com/gameplatform/model/TaiKhoanDangNhap.java`
- `src/main/java/com/gameplatform/model/LoaiTaiKhoan.java`
- `src/main/java/com/gameplatform/model/VaiTroNhanVien.java`

Luồng xử lý:

1. Người dùng nhập username/password trong `LoginView`.
2. Nút đăng nhập gọi `xacThucTaiKhoanController.login(...)`.
3. Controller hash mật khẩu bằng `PasswordHasher.sha256(...)`.
4. Controller truy vấn `TaiKhoan`, left join sang `NhanVien`, `NhaPhatTrien`, `NguoiChoi`.
5. Nếu hợp lệ, controller trả về `TaiKhoanDangNhap`.
6. `App.showMain(...)` mở `MainView`.
7. `MainView` đọc `session.accountType()` và `session.employeeRole()` để dựng menu.

Điểm code quan trọng:

- `PasswordHasher.sha256(...)`: không lưu mật khẩu plain text.
- `XacThucTaiKhoanController.login(...)`: chặn tài khoản bị khóa/ngưng hoạt động.
- `TaiKhoanDangNhap.isVaiTroNhanVien(...)`: kiểm tra quyền nhân viên.
- `MainView.sidebar()` và `MainView.playerTopBar()`: menu khác nhau theo vai trò.

Câu giải thích mẫu:

> Sau khi đăng nhập, hệ thống không hard-code menu. Menu được dựng từ dữ liệu `LoaiTaiKhoan` và `VaiTro` trong database, nên cùng một app nhưng người chơi, nhà phát triển, marketing, CSKH và kiểm duyệt viên thấy chức năng khác nhau.

## 3. Đăng ký tài khoản

File cần mở:

- `LoginView.java`
- `XacThucTaiKhoanController.java`
- `sql/07TaoStoredProcedure.sql`
- `sql/04TaoRangBuoc.sql`

Nghiệp vụ:

- Đăng ký người chơi tạo dòng ở `TaiKhoan` và `NguoiChoi`.
- Đăng ký nhà phát triển tạo dòng ở `TaiKhoan` và `NhaPhatTrien`.

Code quan trọng:

- `registerPlayer(...)`
- `registerDeveloper(...)`
- `ensureUsernameAvailable(...)`
- `ensureEmailAvailable(...)`
- `friendlyRegistrationException(...)`

Ý nghĩa:

- App kiểm tra dữ liệu trống, username/email trùng trước khi gọi procedure.
- DB vẫn có unique constraint để bảo vệ tầng cuối:
  - `UQ_TaiKhoan_TenDangNhap`
  - `UQ_NguoiChoi_Email`
  - `UQ_NhaPhatTrien_Email`
- Stored procedure:
  - `SP_DangKyNguoiChoi`
  - `SP_DangKyNhaPhatTrien`

Câu giải thích mẫu:

> Ở đây app kiểm tra trước để báo lỗi thân thiện. Nhưng database vẫn có unique constraint, nên nếu có hai người đăng ký cùng lúc thì DB vẫn chặn trùng dữ liệu.

## 4. Cửa hàng game

File cần mở:

- `MainView.java`
- `QuanLyThongTinGameController.java`
- `ThongTinGame.java`
- `sql/06TaoStoredFunction.sql`

Luồng xử lý:

1. Người chơi đăng nhập, `MainView.showStore()` được gọi.
2. UI gọi `findReleasedGamesNotOwned(playerId)`.
3. Controller lấy game đang phát hành, loại game đã sở hữu.
4. Query join `Game`, `NhaPhatTrien`, `TheLoai`, `KhuyenMai`, `GameMedia`.
5. UI dựng card bằng `gameCard(...)`, ảnh bìa bằng `gameCoverNode(...)`.

Điểm code quan trọng:

- `GAME_SELECT`: query trung tâm lấy thông tin game.
- `mapGame(...)`: map `ResultSet` sang `ThongTinGame`.
- `imageNode(...)`: load ảnh bìa local từ `anhBiaGame` hoặc file upload.
- `priceBox(...)`: hiển thị giá gốc, giá sau khuyến mãi.

Nghiệp vụ DB:

- `Game.TrangThai = 'Đang phát hành'` mới hiện ở store.
- `SF_TinhGiaHienTai` trong DB tính giá hiện tại theo khuyến mãi.
- `GameMedia.LoaiMedia = 'Ảnh bìa'` dùng làm ảnh cover.

Câu giải thích mẫu:

> Store không lấy dữ liệu tĩnh. Mỗi card game được dựng từ query tổng hợp: thông tin game, nhà phát triển, thể loại, khuyến mãi hiện hành và ảnh bìa mới nhất.

## 5. Wishlist, giỏ hàng và thanh toán

File cần mở:

- `MainView.java`
- `QuanLyMuaHangController.java`
- `sql/05TaoTrigger.sql`
- `sql/07TaoStoredProcedure.sql`

Luồng wishlist:

1. Nút `Wishlist` trên card gọi `addGameToWishlist(...)`.
2. UI gọi `QuanLyMuaHangController.addToWishlist(...)`.
3. Controller insert vào bảng `Wishlist`.
4. Trigger `TRG_KiemTraWishlist` kiểm tra game đang phát hành và người chơi chưa sở hữu.

Luồng giỏ hàng:

1. Nút `Thêm vào giỏ` gọi `addGameToCart(...)`.
2. Controller gọi stored procedure `SP_ThemGameVaoGioHang`.
3. Trigger `TRG_KiemTraGioHang` kiểm tra:
   - game đang phát hành,
   - người chơi chưa sở hữu,
   - đủ độ tuổi.

Luồng thanh toán:

1. Màn `Giỏ hàng` bấm thanh toán.
2. UI gọi `checkoutCart(playerId, discountCode)`.
3. Controller gọi:
   - `SP_TaoGiaoDichTuGioHang`
   - `SP_XacNhanThanhToan`
4. Khi giao dịch chuyển sang `Thành công`, trigger `TRG_XuLyKhiGiaoDichThanhCong` tự động:
   - thêm game vào `SoHuuGame`,
   - tăng `Game.LuotMua`,
   - xóa game khỏi `GioHang`,
   - xóa game khỏi `Wishlist`,
   - tăng lượt dùng mã giảm giá.

Câu giải thích mẫu:

> Phần thanh toán không chỉ insert một bảng. App gọi procedure tạo giao dịch, còn trigger đảm bảo các hệ quả sau thanh toán được thực hiện đồng bộ: sở hữu game, lượt mua, giỏ hàng và wishlist.

## 6. Thư viện game và đánh giá

File cần mở:

- `MainView.showLibrary()`
- `QuanLyThongTinGameController.findLibrary(...)`
- `QuanLyThongTinGameController.rateGame(...)`
- `sql/05TaoTrigger.sql`

Luồng xử lý:

1. Người chơi vào `Thư viện`.
2. App lấy dữ liệu từ `SoHuuGame`, `Game`, `NhaPhatTrien`, `DanhGia`.
3. Người chơi đánh giá game bằng dialog.
4. Controller dùng `MERGE INTO DanhGia` để nếu đã đánh giá thì update, chưa có thì insert.
5. Trigger `TRG_KiemTraDanhGia` bảo đảm chỉ game đã sở hữu mới được đánh giá.

Câu giải thích mẫu:

> App có thể kiểm tra trên UI, nhưng rule quan trọng nhất vẫn nằm ở trigger: không sở hữu game thì không thể đánh giá, kể cả khi ai đó cố insert trực tiếp vào DB.

## 7. Nhà phát triển đăng tải game

File cần mở:

- `MainView.submitGameForm()`
- `QuanLyThongTinGameController.submitGame(...)`
- `QuanLyDanhMucGameController.addVersionAndReleaseRequest(...)`
- `sql/03TaoBang.sql`
- `sql/04TaoRangBuoc.sql`

Luồng gửi game mới:

1. Nhà phát triển mở `Gửi game mới chờ kiểm duyệt`.
2. Nhập tên game, độ tuổi, giá, mô tả, phiên bản, file build, dung lượng tối thiểu, ảnh bìa.
3. UI gọi `submitGame(...)`.
4. Controller mở transaction.
5. Controller insert:
   - `Game` với trạng thái `Chưa phát hành`,
   - `PhienBanGame` với trạng thái `Chưa phát hành`,
   - `GameMedia` loại `Ảnh bìa`,
   - `YeuCauPhatHanh` với trạng thái `Chờ duyệt`.
6. Nếu có lỗi, controller rollback toàn bộ.

Câu giải thích mẫu:

> Đây là một transaction nghiệp vụ. Game, phiên bản, ảnh bìa và yêu cầu phát hành phải được tạo cùng nhau. Nếu một bước lỗi thì rollback để không có dữ liệu mồ côi.

## 8. Kiểm duyệt phát hành game

File cần mở:

- `MainView.showModeratorRequests()`
- `QuanLyYeuCauPhatHanhController.approve(...)`
- `QuanLyYeuCauPhatHanhController.reject(...)`
- `sql/07TaoStoredProcedure.sql`
- `sql/05TaoTrigger.sql`

Luồng duyệt:

1. Kiểm duyệt viên mở màn `Duyệt yêu cầu phát hành`.
2. Chọn yêu cầu `Chờ duyệt`.
3. Bấm duyệt hoặc từ chối.
4. Controller gọi `SP_XuLyYeuCauPhatHanh`.
5. Procedure kiểm tra yêu cầu có tồn tại và còn `Chờ duyệt`.
6. Nếu duyệt, trigger `TRG_XuLyDuyetYCPH` tự cập nhật:
   - `Game.TrangThai = 'Đang phát hành'`,
   - `Game.NgayPhatHanh`,
   - `PhienBanGame.TrangThai = 'Đang phát hành'`.

Câu giải thích mẫu:

> Nhân viên chỉ cập nhật yêu cầu phát hành. Việc đổi trạng thái game và phiên bản được trigger xử lý tự động để tránh quên cập nhật các bảng liên quan.

## 9. Khuyến mãi và mã giảm giá

File cần mở:

- `MainView.showPromotions()`
- `MainView.showDiscountCodes()`
- `QuanLyKhuyenMaiController.java`
- `QuanLyMaGiamGiaController.java`
- `sql/06TaoStoredFunction.sql`
- `sql/07TaoStoredProcedure.sql`

Luồng khuyến mãi:

1. Marketing tạo chương trình khuyến mãi.
2. Chọn game và phần trăm giảm.
3. Controller gọi `SP_ThemGameVaoKhuyenMai` hoặc update `ChiTietKhuyenMai`.
4. Store tự hiển thị giá mới do query/function tính theo khuyến mãi đang hiệu lực.

Luồng mã giảm giá:

1. Marketing tạo `MaGiamGia`.
2. Khi người chơi thanh toán, `SP_TaoGiaoDichTuGioHang` gọi `SF_KiemTraMaGiamGia`.
3. Function kiểm tra mã có hiệu lực, còn lượt dùng, đúng thời gian và đạt tổng tiền tối thiểu.

Câu giải thích mẫu:

> Khuyến mãi giảm theo từng game, còn mã giảm giá giảm trên tổng giỏ hàng. Hai loại giảm giá được tách bảng và tách logic để dễ quản lý.

## 10. Ticket hỗ trợ CSKH

File cần mở:

- `MainView.showPlayerTickets()`
- `MainView.showSupportTickets()`
- `QuanLyTicketHoTroController.java`
- `sql/05TaoTrigger.sql`
- `sql/07TaoStoredProcedure.sql`

Luồng xử lý:

1. Người chơi tạo ticket.
2. CSKH mở danh sách ticket.
3. CSKH nhận xử lý, ticket chuyển `Đang xử lý`.
4. CSKH phản hồi, controller gọi `SP_XuLyTicket`.
5. Trigger `TRG_TuDongGhiNhanNgayXuLyTicket` bắt buộc ticket đã xử lý phải có nhân viên và nội dung phản hồi, đồng thời tự ghi ngày xử lý nếu thiếu.

Câu giải thích mẫu:

> Trigger giúp ticket không thể ở trạng thái đã xử lý mà thiếu nhân viên xử lý hoặc thiếu nội dung phản hồi.

## 11. Quản lý tài khoản và nhân viên

File cần mở:

- `QuanLyTaiKhoanController.java`
- `QuanLyNhanVienController.java`
- `MainView.showPlayerManagement()`
- `MainView.showDeveloperManagement()`
- `MainView.showEmployeeManagement()`

Nghiệp vụ:

- Quản lý người chơi: tra cứu, khóa/mở tài khoản.
- Quản lý nhà phát triển: tra cứu, cập nhật tỷ lệ chia sẻ doanh thu.
- Quản lý nhân viên: thêm nhân viên, đổi vai trò, khóa/mở tài khoản.

Điểm DB:

- Vai trò nhân viên bị giới hạn bởi `CK_NhanVien_VaiTro`.
- Trạng thái tài khoản bị giới hạn bởi `CK_TaiKhoan_TrangThai`.
- Email và username có unique constraint.

Câu giải thích mẫu:

> Các combobox trên UI chỉ cho chọn giá trị hợp lệ, nhưng database vẫn có check constraint để bảo vệ nếu dữ liệu được cập nhật từ bên ngoài app.

## 12. Kiểm duyệt phát hành và xử lý Lost Update

File cần mở:

- `MainView.showModeratorRequests()`
- `QuanLyYeuCauPhatHanhController.java`
- `sql/07TaoStoredProcedure.sql`

Luồng xử lý trong app:

1. Kiểm duyệt viên mở màn `Duyệt yêu cầu phát hành`.
2. UI gọi `QuanLyYeuCauPhatHanhController.approve(...)` hoặc `reject(...)`.
3. Controller mở transaction, tắt auto commit.
4. Controller đặt transaction isolation là `SERIALIZABLE`.
5. Controller gọi `SP_XuLyYeuCauPhatHanh`.
6. Procedure khóa dòng bằng `FOR UPDATE`, kiểm tra yêu cầu còn `Chờ duyệt`.
7. Nếu hợp lệ thì update kết quả duyệt/từ chối; nếu lỗi thì rollback.

Tại sao dùng `SERIALIZABLE`:

- Theo tài liệu HQTCSDL, tình huống Lost Update có thể xảy ra khi hai kiểm duyệt viên cùng xử lý một yêu cầu phát hành.
- Nếu cả hai cùng đọc trạng thái `Chờ duyệt`, người commit sau có thể ghi đè quyết định của người commit trước.
- Ở mức `SERIALIZABLE`, nếu transaction xử lý dựa trên snapshot cũ và dòng đã bị session khác commit trước đó, Oracle không cho ghi đè im lặng.
- Kết hợp thêm `FOR UPDATE` và kiểm tra trạng thái trong procedure, hệ thống chặn xử lý lặp một yêu cầu đã được duyệt/từ chối.

Câu giải thích mẫu:

> Với kiểm duyệt phát hành, yêu cầu quan trọng là một yêu cầu chỉ được xử lý một lần. Vì vậy controller đặt isolation level `SERIALIZABLE`, còn procedure dùng `FOR UPDATE` và kiểm tra trạng thái để tránh Lost Update.

## 13. SQL đã tách theo nhóm object

Thư mục `sql` hiện được tách để dễ trình bày:

| File | Ý nghĩa |
|---|---|
| `00TaoUserGamePlatform.sql` | Tạo user/schema và cấp quyền |
| `01XoaSchemaCu.sql` | Xóa bảng, sequence, function, procedure cũ để setup lại |
| `02TaoSequence.sql` | Tạo sequence sinh khóa chính |
| `03TaoBang.sql` | Tạo bảng, chưa gắn constraint |
| `04TaoRangBuoc.sql` | Gắn primary key, foreign key, unique, check và unique index |
| `05TaoTrigger.sql` | Trigger tự động xử lý nghiệp vụ |
| `06TaoStoredFunction.sql` | Function tính giá, kiểm tra mã, tính doanh thu |
| `07TaoStoredProcedure.sql` | Procedure cho các use case chính |
| `08NapDuLieuMau.sql` | Dữ liệu mẫu để demo |
| `99CaiDatDayDu.sql` | File tổng chạy toàn bộ setup |

## 14. Kịch bản demo đồng thời Lost Update

Chuẩn bị:

1. Nếu muốn dữ liệu sạch, chạy `sql/99CaiDatDayDu.sql` một lần trước buổi demo.
2. Mở hai cửa sổ app, cùng đăng nhập `moderator01`.
3. Đảm bảo còn ít nhất hai yêu cầu phát hành đang `Chờ duyệt`: một yêu cầu dùng cho Before, một yêu cầu dùng cho After.

Demo lỗi:

1. Set `DEMO_CHAN_LOST_UPDATE = false`.
2. Trong màn `Duyệt yêu cầu phát hành`, chọn cùng một yêu cầu đang `Chờ duyệt` ở cả hai cửa sổ.
3. Cửa sổ app 1 bấm `Từ chối`.
4. Trong 8 giây, cửa sổ app 2 bấm `Duyệt` trên đúng yêu cầu đó.
5. Quan sát kết quả cuối: quyết định của cửa sổ 1 có thể bị cửa sổ 2 ghi đè.

Demo cách xử lý:

1. Set `DEMO_CHAN_LOST_UPDATE = true`.
2. Trong màn `Duyệt yêu cầu phát hành`, chọn một yêu cầu `Chờ duyệt` khác với yêu cầu đã dùng ở phần demo lỗi.
3. Cửa sổ app 1 bấm `Từ chối`.
4. Trong 8 giây, cửa sổ app 2 bấm `Duyệt` trên đúng yêu cầu đó.
5. Quan sát kết quả: phiên xử lý sau không thể ghi đè im lặng, mà bị rollback hoặc báo lỗi.

Câu kết khi demo:

> Bản lỗi update trực tiếp nên bị Lost Update. Bản xử lý dùng `SERIALIZABLE` ở Java và `FOR UPDATE` trong `SP_XuLyYeuCauPhatHanh`, nên một yêu cầu phát hành không thể bị hai kiểm duyệt viên ghi đè kết quả cho nhau.
