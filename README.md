# Arcadia - Hệ Thống Nền Tảng Phân Phối Game

Arcadia là ứng dụng desktop mô phỏng một nền tảng phân phối game. Project được xây dựng bằng JavaFX, Maven và Oracle Database, phục vụ đồ án Phân tích thiết kế hệ thống thông tin và Hệ quản trị cơ sở dữ liệu.

## 1. Nội Dung Repository

| Phần | Vị trí | Nội dung |
|---|---|---|
| Source code chương trình | `src/main/java/com/gameplatform` | Code JavaFX, controller nghiệp vụ, model, service, kết nối database |
| Tài nguyên giao diện | `src/main/resources` | CSS và file cấu hình database mẫu |
| Database | `sql` | Script tạo user, bảng, ràng buộc, trigger, stored function, stored procedure và dữ liệu mẫu |
| Ảnh/file demo | `anhBiaGame`, `tepBuild`, `tepMedia` | Ảnh bìa game, thư mục build/media phục vụ demo |
| Script hỗ trợ | `scripts` | Script chạy app và hướng dẫn setup database |

## 2. Công Nghệ Và Thư Viện

| Thành phần | Sử dụng |
|---|---|
| Ngôn ngữ | Java 21 |
| Giao diện | JavaFX 21.0.2 |
| Database | Oracle Database 21c |
| JDBC driver | `ojdbc11` 23.3.0.23.09 |
| Build tool | Maven |

Các thư viện JavaFX và Oracle JDBC đã được khai báo trong `pom.xml`. Khi chạy bằng Maven, project sẽ tự tải thư viện cần thiết.

## 3. Yêu Cầu Cài Đặt

Cần cài đặt:

1. JDK 21.
2. Maven.
3. Oracle Database 21c.
4. SQL*Plus hoặc Oracle SQL Developer.

Kiểm tra Java và Maven:

```powershell
java -version
mvn -version
```

Lưu ý: nếu VSCode báo lỗi `No suitable driver`, thường là do bấm Run Java trực tiếp khiến Oracle JDBC không nằm trong classpath. Hãy chạy app bằng Maven theo mục 8.

## 4. Cấu Hình Database Cho App

Ứng dụng đọc cấu hình kết nối từ file:

```text
src/main/resources/db.properties
```

Sau khi clone project, tạo file này từ file mẫu:

```powershell
Copy-Item src\main\resources\db.properties.example src\main\resources\db.properties
```

Nội dung mặc định:

```properties
db.url=jdbc:oracle:thin:@localhost:1521/orclpdb
db.username=GAME_PLATFORM
db.password=game123
```

Project mặc định dùng service/PDB là `orclpdb`. Nếu Oracle trên máy dùng tên khác như `ORCLPDB1` hoặc `XEPDB1`, sửa dòng `db.url` cho đúng:

```properties
db.url=jdbc:oracle:thin:@localhost:1521/ORCLPDB1
```

## 5. Mở Database Oracle

Nếu app báo lỗi database chưa mở, mở PDB trước bằng SQL*Plus:

```powershell
sqlplus / as sysdba
```

Trong SQL*Plus, chạy:

```sql
ALTER PLUGGABLE DATABASE ORCLPDB OPEN;
ALTER PLUGGABLE DATABASE ORCLPDB SAVE STATE;
EXIT;
```

Nếu máy dùng PDB khác `ORCLPDB`, thay tên PDB trong lệnh trên bằng tên PDB trên máy.

Có thể kiểm tra PDB/service bằng:

```sql
SHOW PDBS;
SELECT name FROM v$services;
```

## 6. Tạo Database Cho Project

Sau khi Oracle đã mở, tại thư mục gốc project chạy:

```powershell
sqlplus / as sysdba @sql/99CaiDatDayDu.sql
```

File `99CaiDatDayDu.sql` tự động thực hiện toàn bộ setup:

1. Mở PDB `ORCLPDB`.
2. Tạo user `GAME_PLATFORM` với password `game123`.
3. Xóa schema cũ nếu có.
4. Tạo sequence.
5. Tạo bảng.
6. Tạo ràng buộc khóa chính, khóa ngoại, unique, check.
7. Tạo trigger.
8. Tạo stored function.
9. Tạo stored procedure.
10. Nạp dữ liệu mẫu.

Nếu không dùng SQL*Plus, có thể mở Oracle SQL Developer bằng user `SYS` hoặc `SYSTEM`, sau đó chạy file:

```text
sql/99CaiDatDayDu.sql
```

Nên chạy bằng **Run Script (F5)**, không chạy từng dòng.

## 7. Kiểm Tra Database

Đăng nhập SQL Developer bằng:

```text
Username: GAME_PLATFORM
Password: game123
Service : orclpdb
```

Chạy thử:

```sql
SELECT * FROM TaiKhoan;
SELECT * FROM Game;
SELECT * FROM YeuCauPhatHanh;
```

Nếu có dữ liệu trả về thì database đã sẵn sàng.

## 8. Chạy Chương Trình

Tại thư mục gốc project, chạy:

```powershell
mvn clean javafx:run
```

Hoặc trên Windows:

```powershell
.\scripts\runApp.ps1
```

Không nên bấm Run trực tiếp một file `.java` trong VSCode nếu chưa cấu hình Maven, vì dễ gặp lỗi thiếu Oracle JDBC driver.

## 9. Tài Khoản Demo

Tất cả tài khoản demo có mật khẩu:

```text
123456
```

| Vai trò | Tài khoản |
|---|---|
| Người chơi | `player01` |
| Nhà phát triển | `dev01` |
| Quản lý nền tảng | `manager01` |
| Kiểm duyệt viên | `moderator01` |
| Marketing | `marketing01` |
| CSKH | `cskh01` |

## 10. Chức Năng Chính

| Vai trò | Chức năng |
|---|---|
| Người chơi | Xem cửa hàng, tìm kiếm game, wishlist, giỏ hàng, thanh toán, thư viện game, ticket hỗ trợ |
| Nhà phát triển | Đăng tải game, quản lý game, phiên bản, media, gửi yêu cầu phát hành, xem doanh thu |
| Kiểm duyệt viên | Duyệt/từ chối yêu cầu phát hành, quản lý thể loại game |
| Marketing | Quản lý chương trình khuyến mãi và mã giảm giá |
| CSKH | Xem, nhận xử lý, phản hồi và đóng ticket |
| Quản lý nền tảng | Quản lý nhân viên, tài khoản, xem và xuất báo cáo doanh thu |

## 11. Các File SQL Chính

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

## 12. Database Object Nổi Bật

| Loại | Object | Vai trò |
|---|---|---|
| Trigger | `TRG_XuLyKhiGiaoDichThanhCong` | Sau khi giao dịch thành công, tự động thêm game vào thư viện, tăng lượt mua, xóa giỏ hàng và cập nhật mã giảm giá |
| Trigger | `TRG_XuLyDuyetYCPH` | Khi yêu cầu phát hành được duyệt, cập nhật game/phiên bản sang trạng thái đang phát hành |
| Stored Procedure | `SP_XuLyYeuCauPhatHanh` | Duyệt hoặc từ chối yêu cầu phát hành, có khóa dòng bằng `FOR UPDATE` |
| Stored Procedure | `SP_TaoGiaoDichTuGioHang` | Tạo giao dịch từ giỏ hàng |
| Stored Procedure | `SP_XacNhanThanhToan` | Xác nhận thanh toán |
| Stored Function | `SF_KiemTraMaGiamGia` | Kiểm tra mã giảm giá có hợp lệ không |
| Stored Function | `SF_TinhGiaHienTai` | Tính giá game sau khuyến mãi |

## 13. Lỗi Thường Gặp

### `No suitable driver`

Nguyên nhân: chạy app không thông qua Maven nên thiếu `ojdbc11`.

Cách xử lý:

```powershell
mvn clean javafx:run
```

Nếu Maven chưa tải dependency:

```powershell
mvn -U clean javafx:run
```

### `database not open` hoặc không kết nối được Oracle

Mở PDB:

```powershell
sqlplus / as sysdba
```

```sql
ALTER PLUGGABLE DATABASE ORCLPDB OPEN;
ALTER PLUGGABLE DATABASE ORCLPDB SAVE STATE;
EXIT;
```

Sau đó chạy lại:

```powershell
sqlplus / as sysdba @sql/99CaiDatDayDu.sql
mvn clean javafx:run
```

### Đăng nhập app không được

Kiểm tra:

1. Đã chạy `sql/99CaiDatDayDu.sql` chưa.
2. File `src/main/resources/db.properties` có đúng user/password không.
3. `db.url` có đúng service name Oracle trên máy không.

Test nhanh:

```sql
SELECT TenDangNhap, LoaiTaiKhoan, TrangThai
FROM TaiKhoan;
```

## 14. Thành Viên

- Nguyễn Xuân Bình
- Lê Nguyễn Hữu Hiếu
- Phạm Công Định
- Nguyễn Thị Quỳnh Hân

## 15. Ghi Chú

Project được xây dựng phục vụ mục đích học tập và báo cáo đồ án.
