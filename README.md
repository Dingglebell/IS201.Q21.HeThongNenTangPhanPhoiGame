# Arcadia - Hệ Thống Nền Tảng Phân Phối Game

Arcadia là ứng dụng desktop mô phỏng một nền tảng phân phối game. Project được xây dựng bằng JavaFX, Maven và Oracle Database, phục vụ đồ án Phân tích thiết kế hệ thống thông tin và Hệ quản trị cơ sở dữ liệu.

## 1. Nội Dung Repository

| Phần | Vị trí | Nội dung |
|---|---|---|
| Source code chương trình | `src/main/java/com/gameplatform` | Code JavaFX, controller nghiệp vụ, model, service, kết nối database |
| Database | `sql` | Script tạo user, bảng, ràng buộc, trigger, stored function, stored procedure và dữ liệu mẫu |
| Tài nguyên giao diện | `src/main/resources`, `anhBiaGame` | CSS, ảnh bìa game và cấu hình database |
| Script hỗ trợ | `scripts` | Script setup database và chạy app |

## 2. Công Nghệ, Thư Viện Và Yêu Cầu Cài Đặt

| Thành phần | Sử dụng | Link cài đặt |
|---|---|---|
| JDK | Java 21 | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java21) |
| Build tool | Maven | [Apache Maven](https://maven.apache.org/download.cgi) |
| Database | Oracle Database 21c XE | [Oracle Database XE](https://www.oracle.com/database/technologies/xe-downloads.html) |
| Giao diện | JavaFX 21.0.2 | Đã khai báo trong `pom.xml` |
| JDBC driver | `ojdbc11` 23.3.0.23.09 | Đã khai báo trong `pom.xml` |

JavaFX và Oracle JDBC đã nằm trong `pom.xml`, nên khi chạy bằng Maven project sẽ tự tải thư viện cần thiết. Không cần tải riêng file `.jar`.

## 3. Cấu Hình Database

App đọc thông tin kết nối trong file đã có sẵn:

```text
src/main/resources/db.properties
```

Nội dung mặc định:

```properties
db.url=jdbc:oracle:thin:@localhost:1521/orclpdb
db.username=GAME_PLATFORM
db.password=game123
```

Nếu Oracle trên máy dùng service/PDB khác, ví dụ `ORCLPDB1` hoặc `XEPDB1`, chỉ cần sửa dòng `db.url`:

```properties
db.url=jdbc:oracle:thin:@localhost:1521/ORCLPDB1
```

## 4. Cài Database Và Chạy Chương Trình

Sau khi cài JDK, Maven và Oracle Database, mở PowerShell tại thư mục gốc project.

Cài database:

```powershell
.\scripts\setupDatabase.ps1
```

Script này tự chạy file `sql/99CaiDatDayDu.sql`, mở PDB `ORCLPDB`, tạo user `GAME_PLATFORM`, tạo toàn bộ bảng, trigger, stored function, stored procedure và nạp dữ liệu mẫu.

Nếu Oracle trên máy dùng PDB khác, ví dụ `XEPDB1`, chạy:

```powershell
.\scripts\setupDatabase.ps1 -PdbName XEPDB1
```

Sau đó sửa `db.url` trong `src/main/resources/db.properties` thành:

```properties
db.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
```

Chạy app:

```powershell
.\scripts\runApp.ps1
```

Hoặc chạy trực tiếp bằng Maven:

```powershell
mvn clean javafx:run
```

Không nên bấm Run trực tiếp một file `.java` trong VSCode, vì dễ gặp lỗi thiếu Oracle JDBC driver.

## 5. Lỗi Thường Gặp Khi Cài Đặt

### `No suitable driver`

Nguyên nhân thường gặp là chạy app không thông qua Maven nên thiếu `ojdbc11`.

Cách xử lý:

```powershell
mvn clean javafx:run
```

Nếu Maven chưa tải dependency:

```powershell
mvn -U clean javafx:run
```

### `database not open` hoặc không kết nối được Oracle

Cách nhanh nhất là chạy lại script setup:

```powershell
.\scripts\setupDatabase.ps1
```

Nếu vẫn lỗi, kiểm tra Oracle service đã chạy chưa. Trên Windows có thể mở `Services` và bật các service Oracle, sau đó chạy lại script.

### Đăng nhập app không được

Kiểm tra:

1. Đã chạy `.\scripts\setupDatabase.ps1` chưa.
2. File `src/main/resources/db.properties` có đúng user/password không.
3. `db.url` có đúng service/PDB Oracle trên máy không.

Tài khoản database mặc định:

```text
Username: GAME_PLATFORM
Password: game123
```

## 6. Tổng Quan Demo Và Thành Phần Hệ Thống

### Tài khoản demo

Tất cả tài khoản demo có mật khẩu `123456`.

| Vai trò | Tài khoản |
|---|---|
| Người chơi | `player01` |
| Nhà phát triển | `dev01` |
| Quản lý nền tảng | `manager01` |
| Kiểm duyệt viên | `moderator01` |
| Marketing | `marketing01` |
| CSKH | `cskh01` |

### Chức năng chính

| Vai trò | Chức năng |
|---|---|
| Người chơi | Xem cửa hàng, tìm kiếm game, wishlist, giỏ hàng, thanh toán, thư viện game, ticket hỗ trợ |
| Nhà phát triển | Đăng tải game, quản lý game, phiên bản, media, gửi yêu cầu phát hành, xem doanh thu |
| Kiểm duyệt viên | Duyệt/từ chối yêu cầu phát hành, quản lý thể loại game |
| Marketing | Quản lý chương trình khuyến mãi và mã giảm giá |
| CSKH | Xem, nhận xử lý, phản hồi và đóng ticket |
| Quản lý nền tảng | Quản lý nhân viên, tài khoản, xem và xuất báo cáo doanh thu |

### Các file SQL chính

| File | Mục đích |
|---|---|
| `00TaoUserGamePlatform.sql` | Tạo user/schema `GAME_PLATFORM` |
| `01XoaSchemaCu.sql` | Xóa object cũ để setup lại sạch |
| `02TaoSequence.sql` | Tạo sequence sinh mã tự động |
| `03TaoBang.sql` | Tạo các bảng chính |
| `04TaoRangBuoc.sql` | Tạo khóa chính, khóa ngoại, unique, check, index |
| `05TaoTrigger.sql` | Tạo trigger xử lý nghiệp vụ tự động |
| `06TaoStoredFunction.sql` | Tạo stored function |
| `07TaoStoredProcedure.sql` | Tạo stored procedure |
| `08NapDuLieuMau.sql` | Nạp dữ liệu mẫu |
| `99CaiDatDayDu.sql` | Script tổng để cài đặt database |

### Database object nổi bật

| Loại | Object | Vai trò |
|---|---|---|
| Trigger | `TRG_XuLyKhiGiaoDichThanhCong` | Sau khi giao dịch thành công, tự động thêm game vào thư viện, tăng lượt mua, xóa giỏ hàng và cập nhật mã giảm giá |
| Trigger | `TRG_XuLyDuyetYCPH` | Khi yêu cầu phát hành được duyệt, cập nhật game/phiên bản sang trạng thái đang phát hành |
| Stored Procedure | `SP_XuLyYeuCauPhatHanh` | Duyệt hoặc từ chối yêu cầu phát hành, có khóa dòng bằng `FOR UPDATE` |
| Stored Procedure | `SP_TaoGiaoDichTuGioHang` | Tạo giao dịch từ giỏ hàng |
| Stored Procedure | `SP_XacNhanThanhToan` | Xác nhận thanh toán |
| Stored Function | `SF_KiemTraMaGiamGia` | Kiểm tra mã giảm giá có hợp lệ không |
| Stored Function | `SF_TinhGiaHienTai` | Tính giá game sau khuyến mãi |
