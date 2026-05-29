# Arcadia - Hệ thống quản lý nền tảng phân phối game

Arcadia là ứng dụng desktop mô phỏng hệ thống quản lý nền tảng phân phối game, được xây dựng bằng **JavaFX** và **Oracle Database 21c**. Hệ thống hỗ trợ nhiều nhóm người dùng như người chơi, nhà phát triển, kiểm duyệt viên, nhân viên marketing, nhân viên chăm sóc khách hàng và quản lý nền tảng.

## 1. Chức năng chính

### Người chơi
- Xem cửa hàng game.
- Tìm kiếm, lọc game theo thể loại.
- Thêm game vào wishlist.
- Thêm game vào giỏ hàng.
- Thanh toán giỏ hàng qua cổng thanh toán mô phỏng.
- Xem thư viện game đã sở hữu.
- Xem lịch sử giao dịch.
- Tạo ticket hỗ trợ.
- Cập nhật hồ sơ và đổi mật khẩu.

### Nhà phát triển
- Xem dashboard studio.
- Quản lý danh sách game của studio.
- Cập nhật thông tin game.
- Quản lý phiên bản game.
- Quản lý media game.
- Gửi yêu cầu phát hành game.
- Theo dõi doanh thu của nhà phát triển.

### Kiểm duyệt viên
- Xem danh sách yêu cầu phát hành game.
- Duyệt hoặc từ chối yêu cầu phát hành.
- Quản lý thể loại game.
- Tra cứu thông tin nhà phát triển và người chơi.

### Nhân viên marketing
- Quản lý chương trình khuyến mãi.
- Gán game vào chương trình khuyến mãi.
- Cập nhật mức giảm giá cho game.
- Quản lý mã giảm giá.

### Nhân viên chăm sóc khách hàng
- Xem danh sách ticket hỗ trợ.
- Nhận xử lý ticket.
- Phản hồi và đóng ticket.
- Tra cứu danh sách người chơi.

### Quản lý nền tảng
- Quản lý nhân viên.
- Cập nhật trạng thái tài khoản nhân viên.
- Phân quyền nhân viên.
- Xem báo cáo doanh thu nền tảng.
- Xuất báo cáo doanh thu dạng CSV.

## 2. Công nghệ sử dụng

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ lập trình | Java |
| Giao diện | JavaFX |
| Cơ sở dữ liệu | Oracle Database 21c |
| Kết nối CSDL | Oracle JDBC |
| Quản lý thư viện/build | Maven |
| IDE khuyến nghị | Visual Studio Code / IntelliJ IDEA |
| Công cụ quản trị CSDL | Oracle SQL Developer |

## 3. Yêu cầu cài đặt

Trước khi chạy project, cần cài đặt:

- JDK 21 hoặc JDK 17 trở lên.
- Maven.
- Oracle Database 21c.
- Oracle SQL Developer hoặc công cụ tương đương.
- Visual Studio Code hoặc IntelliJ IDEA.

Kiểm tra Java và Maven:

```bash
java -version
mvn -version
```

## 4. Cấu trúc thư mục

```text
HeThongQuanLyNenTangPhanPhoiGame/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/gameplatform/
│       │       ├── App.java
│       │       ├── config/
│       │       ├── controller/
│       │       ├── database/
│       │       ├── model/
│       │       ├── service/
│       │       └── ui/
│       └── resources/
│           ├── css/
│           │   └── app.css
│           └── db.properties
├── sql/
│   ├── 00TaoUserGamePlatform.sql
│   ├── 01XoaSchemaCu.sql
│   ├── 02TaoSequence.sql
│   ├── 03TaoBang.sql
│   ├── 04TaoRangBuoc.sql
│   ├── 05TaoTrigger.sql
│   ├── 06TaoStoredFunction.sql
│   ├── 07TaoStoredProcedure.sql
│   ├── 08NapDuLieuMau.sql
│   └── 99CaiDatDayDu.sql
├── scripts/
├── pom.xml
└── README.md
```

## 5. Cấu hình cơ sở dữ liệu

Ứng dụng đọc thông tin kết nối Oracle từ file:

```text
src/main/resources/db.properties
```

Sao chép `src/main/resources/db.properties.example` thành `src/main/resources/db.properties` và cập nhật thông tin kết nối.

Ví dụ cấu hình:

```properties
db.url=jdbc:oracle:thin:@//localhost:1521/orclpdb
db.username=GAME_PLATFORM
db.password=YOUR_PASSWORD
```
Tùy cấu hình Oracle trên máy, `db.url` có thể cần đổi thành một trong các dạng sau:

```properties
db.url=jdbc:oracle:thin:@//localhost:1521/ORCLPDB1
```

hoặc:

```properties
db.url=jdbc:oracle:thin:@localhost:1521:ORCL
```

Nếu không chắc service name của Oracle, kiểm tra trong SQL Developer hoặc dùng lệnh:

```sql
SELECT name FROM v$services;
```

## 6. Khởi tạo database

### Cách 1: Chạy bằng SQL Developer

Mở Oracle SQL Developer, đăng nhập bằng tài khoản có quyền DBA hoặc tài khoản có quyền tạo user/schema, sau đó chạy file:

```text
sql/99CaiDatDayDu.sql
```

Nên chạy bằng **Run Script** hoặc phím `F5`, không chạy từng dòng bằng `Ctrl + Enter`.

File `99CaiDatDayDu.sql` dùng để thiết lập toàn bộ database cho demo, bao gồm:

- Tạo user/schema.
- Tạo bảng.
- Tạo khóa chính, khóa ngoại, ràng buộc.
- Tạo trigger.
- Tạo stored procedure.
- Tạo stored function.
- Thêm dữ liệu mẫu.

### Cách 2: Chạy từng file SQL

Nếu muốn chạy từng bước, thực hiện theo thứ tự:

```text
sql/00TaoUserGamePlatform.sql
sql/01XoaSchemaCu.sql
sql/02TaoSequence.sql
sql/03TaoBang.sql
sql/04TaoRangBuoc.sql
sql/05TaoTrigger.sql
sql/06TaoStoredFunction.sql
sql/07TaoStoredProcedure.sql
sql/08NapDuLieuMau.sql
```

Trong đó:

| File | Ý nghĩa |
|---|---|
| `00TaoUserGamePlatform.sql` | Tạo user/schema `GAME_PLATFORM` |
| `01XoaSchemaCu.sql` | Xóa object cũ để chạy setup lại sạch |
| `02TaoSequence.sql` | Tạo sequence sinh mã tự động |
| `03TaoBang.sql` | Tạo bảng dữ liệu |
| `04TaoRangBuoc.sql` | Gắn khóa chính, khóa ngoại, unique, check và index nghiệp vụ |
| `05TaoTrigger.sql` | Tạo trigger xử lý nghiệp vụ tự động |
| `06TaoStoredFunction.sql` | Tạo stored function tính giá, kiểm tra mã giảm giá, doanh thu |
| `07TaoStoredProcedure.sql` | Tạo stored procedure cho đăng ký, thanh toán, duyệt phát hành, ticket |
| `08NapDuLieuMau.sql` | Thêm dữ liệu mẫu phục vụ demo |
| `99CaiDatDayDu.sql` | File tổng hợp để setup nhanh |

## 7. Kiểm tra database sau khi setup

Sau khi chạy SQL, đăng nhập bằng user:

```text
Username: GAME_PLATFORM
Password: game123
```

Chạy thử:

```sql
SELECT * FROM TaiKhoan;
SELECT * FROM Game;
SELECT * FROM YeuCauPhatHanh;
SELECT * FROM GiaoDich;
```

Nếu các câu lệnh trên trả dữ liệu, database đã sẵn sàng.

## 8. Chạy ứng dụng

Tại thư mục gốc của project, chạy:

```bash
mvn javafx:run
```

Hoặc mở project bằng IDE và chạy class:

```text
com.gameplatform.App
```

Nếu gặp lỗi JavaFX module, hãy ưu tiên chạy bằng Maven:

```bash
mvn clean javafx:run
```

## 9. Tài khoản demo

Mật khẩu chung cho các tài khoản demo:

```text
123456
```

| Vai trò | Username | Password |
|---|---|---|
| Người chơi | `player01` | `123456` |
| Nhà phát triển | `dev01` | `123456` |
| Quản lý nền tảng | `manager01` | `123456` |
| Kiểm duyệt viên | `moderator01` | `123456` |
| Nhân viên marketing | `marketing01` | `123456` |
| Nhân viên CSKH | `cskh01` | `123456` |

## 10. Kịch bản demo đề xuất

### Bước 1: Đăng nhập và phân quyền
1. Mở ứng dụng.
2. Đăng nhập bằng `player01 / 123456`.
3. Kiểm tra giao diện người chơi.
4. Đăng xuất.
5. Đăng nhập bằng `dev01 / 123456`.
6. Kiểm tra giao diện nhà phát triển.
7. Đăng xuất và thử các tài khoản nhân viên khác nếu cần.

### Bước 2: Nhà phát triển gửi yêu cầu phát hành
1. Đăng nhập bằng `dev01 / 123456`.
2. Vào mục **Quản lý game của tôi** để xem danh sách game của studio.
3. Vào mục **Yêu cầu phát hành game**.
4. Gửi yêu cầu phát hành game hoặc xem danh sách yêu cầu đã gửi.

### Bước 3: Kiểm duyệt viên duyệt yêu cầu
1. Đăng nhập bằng `moderator01 / 123456`.
2. Vào mục **Duyệt yêu cầu phát hành**.
3. Chọn yêu cầu đang chờ duyệt.
4. Bấm **Duyệt** hoặc **Từ chối**.
5. Khi duyệt thành công, trigger trong database tự động cập nhật trạng thái game sang **Đang phát hành**.

### Bước 4: Người chơi mua game
1. Đăng nhập bằng `player01 / 123456`.
2. Vào **Cửa hàng**.
3. Thêm game vào giỏ hàng.
4. Vào **Giỏ hàng**.
5. Bấm **Thanh toán**.
6. Khi giao dịch thành công, trigger tự động thêm game vào bảng `SoHuuGame`.
7. Vào **Thư viện** để kiểm tra game đã sở hữu.

### Bước 5: Báo cáo doanh thu
1. Đăng nhập bằng `manager01 / 123456`.
2. Vào **Quản lý doanh thu nền tảng**.
3. Chọn khoảng thời gian.
4. Bấm **Tra cứu doanh thu**.
5. Xem tổng doanh thu, biểu đồ và bảng chi tiết.
6. Có thể bấm **Xuất CSV** để xuất báo cáo.

## 11. Một số đối tượng database quan trọng

### Trigger

| Trigger | Ý nghĩa |
|---|---|
| `TRG_XuLyKhiGiaoDichThanhCong` | Khi giao dịch thành công, tự động thêm game vào thư viện, tăng lượt mua, xóa khỏi giỏ hàng và cập nhật lượt dùng mã giảm giá |
| `TRG_XuLyDuyetYCPH` | Khi yêu cầu phát hành được duyệt, tự động cập nhật trạng thái game/phiên bản |
| `TRG_KiemTraGioHang` | Kiểm tra điều kiện thêm game vào giỏ hàng |
| `TRG_KiemTraDanhGia` | Chỉ cho đánh giá game đã sở hữu |
| `TRG_KiemTraWishlist` | Chỉ cho thêm game đang phát hành vào wishlist |
| `TRG_TuDongGhiNhanNgayXuLyTicket` | Tự động ghi nhận ngày xử lý ticket |

### Stored Procedure

| Procedure | Ý nghĩa |
|---|---|
| `SP_DangKyNguoiChoi` | Đăng ký tài khoản người chơi |
| `SP_DangKyNhaPhatTrien` | Đăng ký tài khoản nhà phát triển |
| `SP_TaoTaiKhoanNhanVien` | Tạo tài khoản nhân viên |
| `SP_TaoGame` | Tạo game mới |
| `SP_TaoYeuCauPhatHanh` | Tạo yêu cầu phát hành game |
| `SP_XuLyYeuCauPhatHanh` | Duyệt hoặc từ chối yêu cầu phát hành |
| `SP_TaoGiaoDichTuGioHang` | Tạo giao dịch từ giỏ hàng |
| `SP_XacNhanThanhToan` | Xác nhận kết quả thanh toán |
| `SP_XuLyTicket` | Xử lý ticket hỗ trợ |

### Stored Function

| Đối tượng | Ý nghĩa |
|---|---|
| `SF_TinhGiaHienTai` | Tính giá hiện tại của game sau khuyến mãi |
| `SF_KiemTraSoHuuGame` | Kiểm tra người chơi đã sở hữu game hay chưa |
| `SF_TinhDoanhThuNPT` | Tính doanh thu nhà phát triển |
| `SF_TongChiTieuNguoiChoi` | Tính tổng chi tiêu của người chơi |

## 12. Lỗi thường gặp

### Lỗi `ORA-00942: table or view does not exist`

Nguyên nhân thường gặp:

- Chưa chạy file SQL tạo database.
- App đang kết nối sai user.
- Bảng được tạo ở schema khác.

Cách xử lý:

```sql
SELECT * FROM TaiKhoan;
```

Nếu câu lệnh trên lỗi, cần chạy lại script setup database.

### Lỗi không đăng nhập được

Kiểm tra:

1. Database đã có dữ liệu trong bảng `TaiKhoan` chưa.
2. Username/password trong `db.properties` có đúng không.
3. Service name trong `db.url` có đúng với Oracle trên máy không.

Test nhanh:

```sql
SELECT TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai
FROM TaiKhoan;
```

### Lỗi kết nối Oracle

Kiểm tra lại file:

```text
src/main/resources/db.properties
```

Đặc biệt là dòng:

```properties
db.url=jdbc:oracle:thin:@//localhost:1521/orclpdb
```

Nếu máy dùng `ORCLPDB1`, sửa thành:

```properties
db.url=jdbc:oracle:thin:@//localhost:1521/ORCLPDB1
```

### Lỗi JavaFX khi chạy

Chạy bằng Maven:

```bash
mvn clean javafx:run
```

Không nên chạy trực tiếp từng file `.java` nếu IDE chưa cấu hình JavaFX đúng.

## 13. Ghi chú triển khai

- Hệ thống hiện là bản mô phỏng phục vụ đồ án môn học.
- Chức năng thanh toán chưa tích hợp cổng thanh toán thật.
- File game/media được mô phỏng bằng đường dẫn hoặc dữ liệu mẫu.
- Báo cáo doanh thu được hiển thị trực tiếp trong ứng dụng và có hỗ trợ xuất CSV.
- Một số chức năng được triển khai ở mức cơ bản để phục vụ demo nghiệp vụ.

## 14. Thành viên thực hiện

Nhóm thực hiện đồ án:

- Nguyễn Xuân Bình
- Lê Nguyễn Hữu Hiếu
- Phạm Công Định
- Nguyễn Thị Quỳnh Hân

## 15. Giấy phép

Project được xây dựng phục vụ mục đích học tập và demo đồ án môn học.



