# Hướng dẫn demo HQTCSDL theo Chương 3 và Chương 4

Tài liệu này dùng để demo phần HQTCSDL khi bảo vệ đồ án. Cách trình bày nên đi theo mẫu:

```text
Tình huống nghiệp vụ -> thao tác trên app/SQL -> object DB chạy -> kết quả nhìn thấy -> ý nghĩa HQTCSDL
```

Trước khi demo, nếu muốn dữ liệu sạch và kết quả dễ đoán, chạy lại setup database một lần:

```powershell
$env:NLS_LANG='.AL32UTF8'
sqlplus / as sysdba @sql/99CaiDatDayDu.sql
```

Tài khoản demo đều dùng mật khẩu `123456`:

| Vai trò | Tài khoản |
|---|---|
| Người chơi | `player01` |
| Nhà phát triển | `dev01` |
| Quản lý nền tảng | `manager01` |
| Kiểm duyệt viên | `moderator01` |
| Marketing | `marketing01` |
| CSKH | `cskh01` |

## 1. Chương 3 - Mỗi loại object chọn 1 ví dụ để demo

### 1.1. Trigger: `TRG_XuLyKhiGiaoDichThanhCong`

File cần chiếu:

- `sql/05TaoTrigger.sql`
- `src/main/java/com/gameplatform/controller/QuanLyMuaHangController.java`
- `src/main/java/com/gameplatform/ui/MainView.java`

Tính năng trên giao diện:

1. Đăng nhập `player01 / 123456`.
2. Mở tab `Cửa hàng`.
3. Chọn một game chưa sở hữu, bấm `Thêm vào giỏ`.
4. Mở `Giỏ hàng`, bấm thanh toán.
5. Mở `Thư viện` để thấy game vừa mua xuất hiện.
6. Quay lại `Giỏ hàng` để thấy game đã được xóa khỏi giỏ.

Khi bấm thanh toán, luồng chạy là:

```text
MainView.checkoutCart(...)
-> QuanLyMuaHangController.checkoutCart(...)
-> SP_TaoGiaoDichTuGioHang
-> SP_XacNhanThanhToan
-> UPDATE GiaoDich.TrangThai = 'Thành công'
-> TRG_XuLyKhiGiaoDichThanhCong tự chạy
```

Trigger tự xử lý các hệ quả sau:

- Thêm game vào `SoHuuGame`.
- Tăng `Game.LuotMua`.
- Xóa game khỏi `GioHang`.
- Xóa game khỏi `Wishlist` nếu có.
- Tăng `MaGiamGia.LuotDung` nếu giao dịch dùng mã giảm giá.

Câu nói khi demo:

> App không tự insert thủ công vào từng bảng sau thanh toán. App chỉ xác nhận giao dịch thành công, còn trigger đảm bảo các bảng liên quan được cập nhật đồng bộ trong database.

### 1.2. Stored Procedure: `SP_XuLyYeuCauPhatHanh`

File cần chiếu:

- `sql/07TaoStoredProcedure.sql`
- `sql/05TaoTrigger.sql`
- `src/main/java/com/gameplatform/controller/QuanLyYeuCauPhatHanhController.java`

Tính năng trên giao diện:

1. Đăng nhập `moderator01 / 123456`.
2. Mở `Duyệt yêu cầu phát hành`.
3. Chọn một yêu cầu có trạng thái `Chờ duyệt`.
4. Bấm `Duyệt`.
5. Mở SQL Developer/SQL*Plus kiểm tra:

```sql
SELECT MaYeuCau, MaGame, TrangThai, MaNVXuLy, NgayXuLy
FROM YeuCauPhatHanh
ORDER BY MaYeuCau DESC;

SELECT MaGame, TenGame, TrangThai, NgayPhatHanh
FROM Game
WHERE MaGame = <ma_game_vua_duyet>;
```

Luồng chạy:

```text
MainView.showModeratorRequests()
-> QuanLyYeuCauPhatHanhController.approve(...)
-> SP_XuLyYeuCauPhatHanh
-> UPDATE YeuCauPhatHanh
-> TRG_XuLyDuyetYCPH tự cập nhật Game/PhienBanGame
```

Ý nghĩa procedure:

- Khóa dòng yêu cầu bằng `FOR UPDATE`.
- Chỉ cho xử lý yêu cầu còn `Chờ duyệt`.
- Không cho duyệt lại hoặc từ chối lại yêu cầu đã xử lý.
- Ghi nhân viên xử lý và ngày xử lý.

Câu nói khi demo:

> Procedure là nơi gom nghiệp vụ duyệt phát hành. Trigger đi sau để tự đồng bộ trạng thái game và phiên bản, tránh trường hợp duyệt yêu cầu rồi quên cập nhật bảng Game.

### 1.3. Stored Function: `SF_TinhGiaHienTai`

File cần chiếu:

- `sql/06TaoStoredFunction.sql`
- `src/main/java/com/gameplatform/controller/QuanLyMuaHangController.java`
- `src/main/java/com/gameplatform/controller/QuanLyThongTinGameController.java`

Tính năng trên giao diện:

1. Đăng nhập `marketing01 / 123456`.
2. Mở màn khuyến mãi, thêm game vào chương trình khuyến mãi hoặc dùng dữ liệu mẫu có sẵn.
3. Đăng nhập `player01 / 123456`.
4. Mở `Cửa hàng`.
5. Quan sát game có giá gốc bị gạch và giá sau giảm.

Có thể chiếu thêm câu SQL:

```sql
SELECT MaGame, TenGame, GiaGoc, SF_TinhGiaHienTai(MaGame) AS GiaHienTai
FROM Game
WHERE TrangThai = 'Đang phát hành'
ORDER BY MaGame;
```

Ý nghĩa function:

- Tìm giá gốc trong bảng `Game`.
- Tìm phần trăm khuyến mãi đang hiệu lực trong `KhuyenMai` và `ChiTietKhuyenMai`.
- Trả về giá hiện tại sau giảm.
- Được dùng khi hiển thị cửa hàng và khi tạo giao dịch từ giỏ hàng.

Câu nói khi demo:

> Giá hiện tại không lưu cứng trên app. Giá được tính bằng stored function dựa trên khuyến mãi còn hiệu lực tại thời điểm truy vấn.

## 2. Chương 4 - Demo truy xuất đồng thời

Nên mở 2 cửa sổ SQL*Plus hoặc SQL Developer, cùng đăng nhập:

```text
GAME_PLATFORM / game123
```

Quy ước:

- Cửa sổ 1 là `T1`.
- Cửa sổ 2 là `T2`.
- Khi cần dữ liệu sạch, chạy lại `sql/99CaiDatDayDu.sql`.

### 2.1. Lost Update - hai kiểm duyệt viên xử lý cùng yêu cầu

Tình huống trong nghiệp vụ:

- Một yêu cầu phát hành đang `Chờ duyệt`.
- Hai kiểm duyệt viên cùng mở yêu cầu đó.
- Nếu xử lý đơn giản kiểu đọc trước rồi update sau, người sau có thể ghi đè kết quả người trước.

Object bảo vệ trong source:

- `SP_XuLyYeuCauPhatHanh` trong `sql/07TaoStoredProcedure.sql`.
- Dòng quan trọng: `SELECT ... FOR UPDATE`.

Demo bằng SQL:

Cửa sổ T1:

```sql
CONNECT GAME_PLATFORM/game123@//localhost:1521/orclpdb
SET SERVEROUTPUT ON

SELECT MaYeuCau, TrangThai
FROM YeuCauPhatHanh
WHERE TrangThai = 'Chờ duyệt'
FETCH FIRST 1 ROW ONLY;

-- Giả sử kết quả là MaYeuCau = 1.
DECLARE
    vTrangThai YeuCauPhatHanh.TrangThai%TYPE;
BEGIN
    SELECT TrangThai
    INTO vTrangThai
    FROM YeuCauPhatHanh
    WHERE MaYeuCau = 1
    FOR UPDATE;

    DBMS_OUTPUT.PUT_LINE('T1 đang khóa yêu cầu, trạng thái=' || vTrangThai);
    DBMS_SESSION.SLEEP(20);

    UPDATE YeuCauPhatHanh
    SET TrangThai = 'Đã duyệt',
        MaNVXuLy = 2,
        NgayXuLy = SYSDATE
    WHERE MaYeuCau = 1;

    COMMIT;
END;
/
```

Trong lúc T1 đang sleep, chạy ở cửa sổ T2:

```sql
CONNECT GAME_PLATFORM/game123@//localhost:1521/orclpdb
SET SERVEROUTPUT ON

BEGIN
    SP_XuLyYeuCauPhatHanh(1, 2, 'Từ chối', 'Demo xử lý trùng yêu cầu');
    COMMIT;
END;
/
```

Kết quả cần nói:

- T2 sẽ phải chờ vì T1 đang giữ khóa dòng.
- Sau khi T1 commit, procedure của T2 kiểm tra lại trạng thái.
- Nếu yêu cầu không còn `Chờ duyệt`, procedure báo lỗi `Yêu cầu phát hành đã được xử lý`.
- Không xảy ra chuyện người sau ghi đè kết quả người trước.

Câu kết:

> Lost Update được chặn bằng khóa dòng `FOR UPDATE` và kiểm tra trạng thái ngay trong stored procedure.

### 2.2. Lost Update - bản demo bằng SQL đã chuẩn bị

Tình huống nghiệp vụ:

- Hai kiểm duyệt viên cùng đọc một yêu cầu phát hành khi nó còn `Chờ duyệt`.
- T1 quyết định `Từ chối`.
- T2 quyết định `Đã duyệt`.
- Nếu update trực tiếp không kiểm soát, session commit sau có thể ghi đè session commit trước.

File cần chiếu:

- `src/main/java/com/gameplatform/controller/QuanLyYeuCauPhatHanhController.java`
- `sql/07TaoStoredProcedure.sql`

Demo lỗi:

1. Set `DEMO_CHAN_LOST_UPDATE = false`.
2. Mở hai cửa sổ app, cùng đăng nhập `moderator01`.
3. Trong màn `Duyệt yêu cầu phát hành`, chọn cùng một yêu cầu đang `Chờ duyệt` ở cả hai cửa sổ.
4. Cửa sổ 1 bấm `Từ chối`.
5. Trong 8 giây, cửa sổ 2 bấm `Duyệt` trên đúng yêu cầu đó.
6. Quan sát kết quả cuối: cửa sổ 2 có thể ghi đè quyết định từ chối của cửa sổ 1.

Demo cách xử lý:

1. Set `DEMO_CHAN_LOST_UPDATE = true`.
2. Mở hai cửa sổ app, cùng đăng nhập `moderator01`.
3. Trong màn `Duyệt yêu cầu phát hành`, chọn một yêu cầu `Chờ duyệt` khác với yêu cầu vừa dùng ở phần demo lỗi.
4. Cửa sổ 1 bấm `Từ chối`.
5. Trong 8 giây, cửa sổ 2 bấm `Duyệt` trên đúng yêu cầu đó.
6. Quan sát kết quả: phiên xử lý sau không thể ghi đè im lặng, mà bị rollback hoặc báo lỗi.

Trong app:

- `QuanLyYeuCauPhatHanhController.xuLyYeuCauPhatHanh(...)` dùng `Connection.TRANSACTION_SERIALIZABLE`.
- `SP_XuLyYeuCauPhatHanh` dùng `FOR UPDATE` và kiểm tra trạng thái còn `Chờ duyệt`.

Câu kết:

> Bản lỗi update trực tiếp nên bị Lost Update. Bản xử lý của app dùng `SERIALIZABLE` ở Java và `FOR UPDATE` trong procedure, nên một yêu cầu không thể bị hai kiểm duyệt viên ghi đè kết quả.

### 2.3. Non-repeatable Read - khuyến mãi thay đổi giữa lúc thanh toán

Tình huống nghiệp vụ:

- Người chơi đang thanh toán game có khuyến mãi.
- Marketing thay đổi phần trăm khuyến mãi trong lúc giao dịch chưa kết thúc.
- Nếu T1 đọc giá hai lần ở `READ COMMITTED`, lần hai có thể nhận giá khác.

Demo mô phỏng lỗi:

Cửa sổ T1:

```sql
CONNECT GAME_PLATFORM/game123@//localhost:1521/orclpdb
SET SERVEROUTPUT ON

PROMPT T1 đọc giá game 1 lần 1
SELECT g.MaGame, g.TenGame, g.GiaGoc, SF_TinhGiaHienTai(g.MaGame) AS GiaHienTai
FROM Game g
WHERE g.MaGame = 1;

BEGIN
    DBMS_SESSION.SLEEP(20);
END;
/

PROMPT T1 đọc giá game 1 lần 2
SELECT g.MaGame, g.TenGame, g.GiaGoc, SF_TinhGiaHienTai(g.MaGame) AS GiaHienTai
FROM Game g
WHERE g.MaGame = 1;

ROLLBACK;
```

Cửa sổ T2 chạy trong lúc T1 sleep:

```sql
CONNECT GAME_PLATFORM/game123@//localhost:1521/orclpdb

UPDATE ChiTietKhuyenMai
SET PhanTramKM = 60
WHERE MaKM = 1
  AND MaGame = 1;

COMMIT;
```

Kết quả:

- T1 lần 1 thấy giá theo khuyến mãi cũ.
- T1 lần 2 thấy giá theo khuyến mãi mới.
- Đây là đọc không lặp lại.

Cách giải thích trong app:

- Khi người chơi bấm thanh toán, `SP_TaoGiaoDichTuGioHang` tính giá và ghi giá vào `ChiTietGiaoDich.GiaBan`.
- Sau khi giao dịch đã tạo, hóa đơn không phụ thuộc vào giá hiển thị thay đổi sau đó.
- Nếu cần đảm bảo chặt hơn cho một giao dịch kéo dài, có thể dùng `SERIALIZABLE` hoặc khóa dòng khuyến mãi bằng `FOR UPDATE` như tài liệu chương 4.

Câu kết:

> Non-repeatable Read nguy hiểm ở bước tính giá. Trong app demo, giá được chốt vào chi tiết giao dịch khi tạo giao dịch, nên lịch sử mua hàng giữ nguyên dù khuyến mãi đổi sau đó.

### 2.4. Dirty Read - người chơi không được thấy giá chưa commit

Tình huống nghiệp vụ:

- Quản trị viên đang sửa giá game nhưng chưa commit.
- Người chơi mở cửa hàng cùng lúc.
- Nếu hệ quản trị cho dirty read, người chơi có thể thấy giá tạm thời rồi giá bị rollback.

Oracle không hỗ trợ `READ UNCOMMITTED`, nên dirty read được chặn tự động.

Demo:

Cửa sổ T1:

```sql
CONNECT GAME_PLATFORM/game123@//localhost:1521/orclpdb

UPDATE Game
SET GiaGoc = 1
WHERE MaGame = 1;

-- Không commit. Giữ cửa sổ này nguyên.
```

Cửa sổ T2:

```sql
CONNECT GAME_PLATFORM/game123@//localhost:1521/orclpdb

SELECT MaGame, TenGame, GiaGoc
FROM Game
WHERE MaGame = 1;
```

Kết quả:

- T2 không thấy giá `1`.
- T2 vẫn thấy giá đã commit trước đó.

Quay lại T1:

```sql
ROLLBACK;
```

Câu kết:

> Dirty Read không xảy ra trong Oracle vì mức thấp nhất là `READ COMMITTED`. Oracle dùng MVCC/Undo để phiên đọc chỉ thấy dữ liệu đã commit.

### 2.5. Deadlock - hai người chơi thanh toán nhiều game ngược thứ tự

Tình huống nghiệp vụ:

- Người chơi A thanh toán game 11 rồi 12.
- Người chơi B thanh toán game 12 rồi 11.
- Nếu code cập nhật `Game.LuotMua` theo thứ tự giỏ hàng của từng người, hai transaction có thể khóa chéo.

Object trong source:

- `TRG_XuLyKhiGiaoDichThanhCong` trong `sql/05TaoTrigger.sql`.
- Trigger cập nhật lượt mua theo `ORDER BY MaGame`, tức thứ tự khóa cố định.

Demo lỗi mô phỏng deadlock thủ công:

Cửa sổ T1:

```sql
CONNECT GAME_PLATFORM/game123@//localhost:1521/orclpdb

UPDATE Game SET LuotMua = LuotMua + 1 WHERE MaGame = 11;
```

Cửa sổ T2:

```sql
CONNECT GAME_PLATFORM/game123@//localhost:1521/orclpdb

UPDATE Game SET LuotMua = LuotMua + 1 WHERE MaGame = 12;
```

Quay lại T1:

```sql
UPDATE Game SET LuotMua = LuotMua + 1 WHERE MaGame = 12;
```

Quay lại T2:

```sql
UPDATE Game SET LuotMua = LuotMua + 1 WHERE MaGame = 11;
```

Kết quả:

- Oracle có thể báo `ORA-00060: deadlock detected`.
- Sau demo, chạy `ROLLBACK` ở cả hai cửa sổ.

Cách app tránh deadlock:

- Khi giao dịch thành công, trigger lấy danh sách game trong giao dịch theo `ORDER BY MaGame`.
- Dù người chơi thêm game vào giỏ theo thứ tự nào, database vẫn cập nhật lượt mua theo cùng một thứ tự.
- Transaction đến sau chỉ chờ transaction trước nhả khóa, không tạo vòng chờ chéo.

Câu kết:

> Deadlock không phải do isolation level, mà do thứ tự khóa tài nguyên. Cách phòng ngừa là luôn khóa/cập nhật tài nguyên theo một thứ tự chuẩn.

## 3. Thứ tự demo gợi ý trong buổi bảo vệ

Thứ tự ngắn, dễ nói:

1. Mở `sql/99CaiDatDayDu.sql`: giới thiệu SQL đã tách theo user, table, constraint, trigger, function, procedure, data.
2. Chạy app, đăng nhập `player01`, mua game: demo trigger thanh toán.
3. Đăng nhập `moderator01`, duyệt yêu cầu phát hành: demo stored procedure và trigger duyệt.
4. Đăng nhập `marketing01` hoặc dùng SQL query: demo stored function tính giá hiện tại.
5. Mở 2 cửa sổ app `moderator01`: demo Lost Update bằng công tắc `DEMO_CHAN_LOST_UPDATE`.
6. Nếu còn thời gian, demo Dirty Read vì rất nhanh và dễ giải thích Oracle MVCC.
7. Nếu còn thời gian, nói thêm Dirty Read hoặc Deadlock bằng kịch bản trong tài liệu.

## 4. Bảng nói nhanh khi bị hỏi object nào chạy

| Tính năng demo | Object DB chạy | Khi nào chạy |
|---|---|---|
| Đăng ký người chơi | `SP_DangKyNguoiChoi` | Bấm đăng ký người chơi |
| Đăng ký nhà phát triển | `SP_DangKyNhaPhatTrien` | Bấm đăng ký nhà phát triển |
| Thêm game vào giỏ | `SP_ThemGameVaoGioHang`, `TRG_KiemTraGioHang` | Bấm `Thêm vào giỏ` |
| Thanh toán | `SP_TaoGiaoDichTuGioHang`, `SP_XacNhanThanhToan`, `TRG_XuLyKhiGiaoDichThanhCong` | Bấm thanh toán |
| Hiển thị giá khuyến mãi | `SF_TinhGiaHienTai` | Khi load cửa hàng/giỏ hàng và khi tạo giao dịch |
| Kiểm tra mã giảm giá | `SF_KiemTraMaGiamGia` | Khi thanh toán có nhập mã |
| Đánh giá game | `TRG_KiemTraDanhGia` | Khi insert/update đánh giá |
| Duyệt phát hành | `SP_XuLyYeuCauPhatHanh`, `TRG_XuLyDuyetYCPH` | Kiểm duyệt viên bấm duyệt |
| Phản hồi ticket | `SP_XuLyTicket`, `TRG_TuDongGhiNhanNgayXuLyTicket` | CSKH bấm phản hồi và đóng ticket |
| Báo cáo doanh thu | Query tổng hợp, `SF_TinhDoanhThuNPT`, transaction `SERIALIZABLE` | Khi quản lý/NPT mở báo cáo |

## 5. Câu kết tổng quát

Có thể kết phần HQTCSDL như sau:

> Ứng dụng không chỉ xử lý nghiệp vụ ở tầng JavaFX. Các nghiệp vụ quan trọng được đưa xuống Oracle bằng constraint, trigger, stored procedure và stored function. Với truy xuất đồng thời, hệ thống dùng khóa dòng `FOR UPDATE`, transaction `SERIALIZABLE`, cơ chế MVCC của Oracle và quy tắc cập nhật tài nguyên theo thứ tự cố định để giữ dữ liệu nhất quán khi nhiều người dùng thao tác cùng lúc.
