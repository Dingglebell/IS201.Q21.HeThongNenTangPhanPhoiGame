# Kịch bản demo báo cáo HQTCSDL

Mục tiêu của buổi demo:

1. Nói sơ qua chức năng chính của app.
2. Chọn đúng 3 object database để trình bày sâu:
   - Trigger: `TRG_XuLyKhiGiaoDichThanhCong`.
   - Stored procedure: `SP_XuLyYeuCauPhatHanh`.
   - Stored function: `SF_KiemTraMaGiamGia`.
3. Demo một tình huống truy xuất đồng thời mục 4.1: Lost Update.
4. Vừa thao tác app, vừa mở VSCode để chỉ code liên quan.

Trước khi demo, nếu muốn dữ liệu sạch và dễ đoán, chạy lại setup database một lần:

```powershell
$env:NLS_LANG='.AL32UTF8'
sqlplus / as sysdba @sql/99CaiDatDayDu.sql
```

Chạy app:

```powershell
.\scripts\runApp.ps1
```

Tài khoản demo dùng mật khẩu chung `123456`.

| Vai trò | Tài khoản |
|---|---|
| Người chơi | `player01` |
| Nhà phát triển | `dev01` |
| Kiểm duyệt viên | `moderator01` |
| Marketing | `marketing01` |
| CSKH | `cskh01` |
| Quản lý nền tảng | `manager01` |

## 1. Demo Trigger: `TRG_XuLyKhiGiaoDichThanhCong`

### Ý chính cần nói

Trigger này xử lý tự động sau khi một giao dịch chuyển sang trạng thái `Thành công`.

Nó nằm trong:

```text
sql/05TaoTrigger.sql
```

Trigger được kích hoạt bởi:

```text
UPDATE GiaoDich
SET TrangThai = 'Thành công'
```

Trong app, câu update này xảy ra gián tiếp khi người chơi bấm thanh toán:

```text
MainView
-> QuanLyMuaHangController.checkoutCart(...)
-> SP_TaoGiaoDichTuGioHang
-> SP_XacNhanThanhToan
-> TRG_XuLyKhiGiaoDichThanhCong tự chạy
```

### Thao tác demo trên app

1. Đăng nhập `player01 / 123456`.
2. Mở tab `Cửa hàng`.
3. Chọn game `Lotus Quest` hoặc một game chưa sở hữu.
4. Bấm `Thêm vào giỏ`.
5. Mở tab `Giỏ hàng`.
6. Nhập mã giảm giá `WELCOME50`.
7. Bấm thanh toán.
8. Mở tab `Thư viện`: game vừa mua xuất hiện.
9. Quay lại `Giỏ hàng`: game đã được xóa khỏi giỏ.

Nếu muốn show dữ liệu bằng SQL trước/sau, dùng:

```sql
SELECT * FROM SoHuuGame WHERE MaNguoiChoi = 1 AND MaGame = 2;
SELECT MaGame, TenGame, LuotMua FROM Game WHERE MaGame = 2;
SELECT Code, LuotDung FROM MaGiamGia WHERE Code = 'WELCOME50';
```

Sau khi thanh toán thành công:

- `SoHuuGame` có thêm dòng người chơi sở hữu game.
- `Game.LuotMua` tăng.
- `GioHang` không còn game đó.
- `Wishlist` cũng bị xóa nếu game đang nằm trong wishlist.
- `MaGiamGia.LuotDung` tăng nếu có dùng mã giảm giá.

### Chuyển sang VSCode để chỉ code

Mở file:

```text
sql/05TaoTrigger.sql
```

Chỉ đoạn:

```sql
CREATE OR REPLACE TRIGGER TRG_XuLyKhiGiaoDichThanhCong
AFTER UPDATE ON GiaoDich
FOR EACH ROW
```

Giải thích:

- `AFTER UPDATE ON GiaoDich`: chạy sau khi giao dịch bị update.
- `FOR EACH ROW`: mỗi dòng giao dịch update thì trigger chạy một lần.
- Điều kiện:

```sql
IF (:NEW.TrangThai = 'Thành công' AND :OLD.TrangThai <> 'Thành công') THEN
```

Nghĩa là trigger chỉ chạy khi trạng thái vừa chuyển sang `Thành công`, tránh chạy lặp khi update các thông tin khác.

Các khối quan trọng:

```sql
INSERT INTO SoHuuGame ...
```

Thêm game vào thư viện người chơi.

```sql
FOR item IN (
    SELECT DISTINCT ct.MaGame
    ...
    ORDER BY ct.MaGame
) LOOP
    UPDATE Game
    SET LuotMua = NVL(LuotMua, 0) + 1
```

Tăng lượt mua theo thứ tự `MaGame` cố định. Đây cũng là một cách giảm nguy cơ deadlock khi nhiều giao dịch cùng cập nhật nhiều game.

```sql
DELETE FROM GioHang ...
DELETE FROM Wishlist ...
```

Dọn giỏ hàng và wishlist sau khi mua thành công.

```sql
UPDATE MaGiamGia
SET LuotDung = NVL(LuotDung, 0) + 1
```

Tăng số lượt dùng mã giảm giá.

Câu nói mẫu:

> Trigger này giúp hệ thống không phải xử lý rời rạc ở tầng Java. Chỉ cần giao dịch chuyển sang thành công, database tự bảo đảm các hệ quả sau thanh toán được cập nhật đồng bộ.

## 2. Demo Stored Procedure: `SP_XuLyYeuCauPhatHanh`

### Ý chính cần nói

Procedure này xử lý nghiệp vụ kiểm duyệt phát hành game: duyệt hoặc từ chối yêu cầu của nhà phát triển.

Nó nằm trong:

```text
sql/07TaoStoredProcedure.sql
```

Controller Java gọi procedure ở:

```text
src/main/java/com/gameplatform/controller/QuanLyYeuCauPhatHanhController.java
```

### Thao tác demo trên app

1. Đăng nhập `moderator01 / 123456`.
2. Mở màn `Duyệt yêu cầu phát hành`.
3. Chọn một yêu cầu đang `Chờ duyệt`.
4. Bấm `Duyệt` hoặc `Từ chối`.
5. Nếu duyệt, game chuyển sang trạng thái `Đang phát hành`.
6. Có thể đăng nhập người chơi và mở `Cửa hàng` để thấy game vừa duyệt xuất hiện.

### Chuyển sang VSCode để chỉ code Java

Mở:

```text
src/main/java/com/gameplatform/controller/QuanLyYeuCauPhatHanhController.java
```

Chỉ hàm:

```java
private void xuLyYeuCauPhatHanh(...)
```

Điểm cần nói:

```java
connection.setAutoCommit(false);
connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
```

Nghĩa là app tự mở transaction và đặt mức cô lập `SERIALIZABLE` khi xử lý yêu cầu phát hành. Đây là phần bám với Chương 4 mục Lost Update.

```java
connection.prepareCall("{call SP_XuLyYeuCauPhatHanh(?, ?, ?, ?)}")
```

Nghĩa là Java không tự viết toàn bộ update, mà gọi stored procedure trong Oracle.

```java
connection.commit();
...
connection.rollback();
```

Nếu xử lý thành công thì commit. Nếu có lỗi đồng thời hoặc lỗi nghiệp vụ thì rollback.

### Chuyển sang VSCode để chỉ code SQL

Mở:

```text
sql/07TaoStoredProcedure.sql
```

Chỉ procedure:

```sql
CREATE OR REPLACE PROCEDURE SP_XuLyYeuCauPhatHanh
```

Các điểm quan trọng:

```sql
SELECT TrangThai
INTO vTrangThai
FROM YeuCauPhatHanh
WHERE MaYeuCau = pMaYeuCau
FOR UPDATE;
```

Giải thích:

- Lấy trạng thái hiện tại của yêu cầu.
- `FOR UPDATE` khóa dòng đang xử lý.
- Nếu có session khác đang xử lý cùng dòng thì phải chờ.

```sql
IF vTrangThai <> 'Chờ duyệt' THEN
    RAISE_APPLICATION_ERROR(-20104, 'Yêu cầu phát hành đã được xử lý.');
END IF;
```

Giải thích:

- Chỉ yêu cầu còn `Chờ duyệt` mới được xử lý.
- Nếu yêu cầu đã duyệt/từ chối rồi thì báo lỗi, không cho ghi đè.

```sql
UPDATE YeuCauPhatHanh
SET TrangThai = pTrangThai,
    MaNVXuLy = pMaNVXuLy,
    LyDoTuChoi = CASE WHEN pTrangThai = 'Từ chối' THEN pLyDoTuChoi ELSE NULL END,
    NgayXuLy = SYSDATE
WHERE MaYeuCau = pMaYeuCau;
```

Giải thích:

- Ghi kết quả kiểm duyệt.
- Lưu nhân viên xử lý.
- Nếu từ chối thì lưu lý do.
- Ghi ngày xử lý.

Câu nói mẫu:

> Procedure này gom toàn bộ nghiệp vụ xử lý yêu cầu phát hành. App chỉ truyền quyết định duyệt hoặc từ chối, còn database kiểm tra trạng thái, khóa dòng và cập nhật kết quả.

## 3. Demo Stored Function: `SF_KiemTraMaGiamGia`

Mình chọn function này vì dễ demo ngay trên app: nhập mã giảm giá sai thì thanh toán bị chặn, nhập mã hợp lệ thì tổng tiền giảm.

Function nằm trong:

```text
sql/06TaoStoredFunction.sql
```

Nó được gọi bên trong:

```text
sql/07TaoStoredProcedure.sql
SP_TaoGiaoDichTuGioHang
```

### Thao tác demo trên app

1. Đăng nhập `player01 / 123456`.
2. Mở `Cửa hàng`, thêm một game chưa sở hữu vào giỏ.
3. Mở `Giỏ hàng`.
4. Nhập mã `EXPIRED10`.
5. Bấm thanh toán.
6. App báo lỗi mã không hợp lệ/hết hạn.
7. Đổi sang mã `WELCOME50`.
8. Bấm thanh toán lại.
9. Giao dịch thành công, tổng tiền được giảm.

### Chuyển sang VSCode để chỉ code SQL

Mở:

```text
sql/06TaoStoredFunction.sql
```

Chỉ function:

```sql
CREATE OR REPLACE FUNCTION SF_KiemTraMaGiamGia(pCode VARCHAR2, pTongTien NUMBER)
RETURN NUMBER
```

Giải thích tham số:

- `pCode`: mã người chơi nhập.
- `pTongTien`: tổng tiền giỏ hàng trước khi áp mã.

Điều kiện kiểm tra:

```sql
WHERE UPPER(Code) = UPPER(pCode)
  AND TrangThai = 'Đang hiệu lực'
  AND TRUNC(SYSDATE) BETWEEN NgayBatDau AND NgayHetHan
  AND LuotDung < GioiHanSuDung
  AND pTongTien >= TongGiaToiThieu;
```

Nghĩa là mã phải:

- Đúng code, không phân biệt hoa thường.
- Đang hiệu lực.
- Còn trong thời gian sử dụng.
- Chưa vượt giới hạn số lượt dùng.
- Tổng giỏ hàng đạt tối thiểu.

Kết quả trả về:

```sql
RETURN LEAST(vSoTienGiam, pTongTien);
```

Không cho giảm quá tổng tiền.

```sql
WHEN NO_DATA_FOUND THEN
    RETURN -1;
```

Nếu mã sai/hết hạn/không đủ điều kiện thì trả `-1`.

Mở thêm:

```text
sql/07TaoStoredProcedure.sql
```

Chỉ đoạn trong `SP_TaoGiaoDichTuGioHang`:

```sql
vSoTienGiamCode := SF_KiemTraMaGiamGia(pCode, vTongGiaBan);
IF vSoTienGiamCode < 0 THEN
    RAISE_APPLICATION_ERROR(-20108, 'Mã giảm giá không hợp lệ, hết hạn hoặc đã vượt giới hạn sử dụng.');
END IF;
```

Câu nói mẫu:

> Function không trực tiếp tạo giao dịch, mà trả về kết quả kiểm tra. Procedure thanh toán dùng kết quả đó để quyết định cho thanh toán tiếp hay báo lỗi.

## 4. Demo Lost Update - mục 4.1 Chương 4

### 4.0. Cách demo nhanh nhất bằng app

Bạn có thể demo Before/After ngay trên app, chỉ đổi một dòng trong:

```text
src/main/java/com/gameplatform/controller/QuanLyYeuCauPhatHanhController.java
```

Tìm dòng:

```java
private static final boolean DEMO_CHAN_LOST_UPDATE = true;
```

Ý nghĩa:

- `false`: Before, cố tình dùng bản lỗi để thấy Lost Update.
- `true`: After, dùng `SERIALIZABLE + SP_XuLyYeuCauPhatHanh` để chặn Lost Update.

Dòng này nằm cạnh:

```java
private static final int DEMO_DO_TRE_LOST_UPDATE_GIAY = 8;
```

Độ trễ 8 giây giúp bạn có thời gian bấm xử lý ở cửa sổ app thứ hai. Nếu không demo đồng thời nữa thì có thể đổi số này về `0`.

Chuẩn bị dữ liệu:

- Không cần nút reset và không cần file SQL riêng cho Lost Update.
- Dùng một yêu cầu đang `Chờ duyệt` để demo Before.
- Sau đó dùng một yêu cầu `Chờ duyệt` khác để demo After.
- Nếu lỡ xử lý hết yêu cầu chờ duyệt, chỉ cần chạy lại `sql/99CaiDatDayDu.sql` một lần để nạp lại dữ liệu mẫu.

Demo Before bằng app:

1. Set `DEMO_CHAN_LOST_UPDATE = false`.
2. Run app lần 1, đăng nhập `moderator01`.
3. Run app lần 2, cũng đăng nhập `moderator01`.
4. Cả hai cửa sổ cùng mở `Duyệt yêu cầu phát hành`.
5. Chọn cùng một yêu cầu đang `Chờ duyệt` ở cả hai cửa sổ.
6. Ở cửa sổ app 1, bấm `Từ chối`.
7. Trong vòng 8 giây, ở cửa sổ app 2, bấm `Duyệt` trên đúng yêu cầu đó.
8. Sau khi cả hai xử lý xong, tải lại danh sách hoặc mở lại màn duyệt.
9. Kết quả có thể là `Đã duyệt`, tức quyết định `Từ chối` trước đó bị ghi đè.

Câu nói:

> Đây là bản Before. Hai cửa sổ đều đọc yêu cầu khi còn `Chờ duyệt`. Vì code update trực tiếp, không dùng `SERIALIZABLE`, không gọi procedure kiểm tra trạng thái, nên thao tác commit sau ghi đè thao tác commit trước.

Demo After bằng app:

1. Set `DEMO_CHAN_LOST_UPDATE = true`.
2. Run lại app lần 1 và lần 2.
3. Chọn một yêu cầu `Chờ duyệt` khác với yêu cầu đã dùng ở phần Before.
4. Ở cả hai cửa sổ, chọn cùng yêu cầu mới đó.
5. Ở cửa sổ app 1, bấm `Từ chối`.
6. Trong vòng 8 giây, ở cửa sổ app 2, bấm `Duyệt` trên đúng yêu cầu đó.
7. Một cửa sổ sẽ xử lý thành công, cửa sổ còn lại sẽ bị rollback/báo lỗi yêu cầu đã bị phiên khác xử lý.
8. Kết quả cuối cùng không bị ghi đè im lặng.

Câu nói:

> Đây là bản After. Java đặt transaction `SERIALIZABLE`, sau đó gọi `SP_XuLyYeuCauPhatHanh`. Procedure khóa dòng bằng `FOR UPDATE` và kiểm tra trạng thái. Vì vậy nếu một phiên đã xử lý trước, phiên còn lại không thể ghi đè.

### Tình huống nghiệp vụ

Một yêu cầu phát hành game đang ở trạng thái `Chờ duyệt`.

- Session 1: kiểm duyệt viên A đọc yêu cầu và quyết định `Từ chối`.
- Session 2: kiểm duyệt viên B cũng đọc cùng yêu cầu lúc nó còn `Chờ duyệt` và quyết định `Đã duyệt`.
- Nếu không kiểm soát đồng thời, session commit sau có thể ghi đè quyết định của session commit trước.

Trong dữ liệu mẫu hiện có thể dùng hai yêu cầu `Chờ duyệt` khác nhau, ví dụ:

```text
Before: dùng một yêu cầu đang Chờ duyệt bất kỳ.
After: dùng một yêu cầu Chờ duyệt khác.
```

Không cần file SQL riêng cho phần Lost Update. Khi cần dữ liệu mới hoàn toàn thì chạy lại file tổng `sql/99CaiDatDayDu.sql`.

### 4.1. Show nghiệp vụ trên app trước

1. Đăng nhập app bằng `moderator01 / 123456`.
2. Mở `Duyệt yêu cầu phát hành`.
3. Chỉ một dòng yêu cầu đang `Chờ duyệt`.
4. Giải thích rằng cả hai cửa sổ app sẽ cùng xử lý dòng này để tạo tình huống Lost Update.

Câu nói:

> Đây là yêu cầu phát hành mà hai kiểm duyệt viên có thể cùng mở lên xử lý. Nếu không khóa hoặc không dùng transaction phù hợp, quyết định của người này có thể bị người kia ghi đè.

### 4.2. Show code Before/After trong source

Mở:

```text
src/main/java/com/gameplatform/controller/QuanLyYeuCauPhatHanhController.java
```

Chỉ công tắc demo:

```java
private static final boolean DEMO_CHAN_LOST_UPDATE = true;
```

Giải thích:

- `false`: Before, chạy bản lỗi `xuLyYeuCauPhatHanhBanLoiDemo(...)`, update trực tiếp bảng `YeuCauPhatHanh`.
- `true`: After, chạy bản an toàn `xuLyYeuCauPhatHanhAnToan(...)`, dùng `SERIALIZABLE` và gọi procedure.

Chỉ:

```java
connection.setAutoCommit(false);
connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
```

Giải thích:

- Mỗi lần kiểm duyệt là một transaction riêng.
- `SERIALIZABLE` giúp Oracle phát hiện nếu transaction đang xử lý dựa trên snapshot cũ.
- Nếu có session khác đã commit trước, session sau không được ghi đè im lặng.

Mở:

```text
sql/07TaoStoredProcedure.sql
```

Chỉ:

```sql
SELECT TrangThai
INTO vTrangThai
FROM YeuCauPhatHanh
WHERE MaYeuCau = pMaYeuCau
FOR UPDATE;
```

Giải thích:

- Procedure khóa dòng yêu cầu phát hành.
- Trạng thái được kiểm tra lại ngay trong database.

Chỉ tiếp:

```sql
IF vTrangThai <> 'Chờ duyệt' THEN
    RAISE_APPLICATION_ERROR(-20104, 'Yêu cầu phát hành đã được xử lý.');
END IF;
```

Giải thích:

- Nếu yêu cầu đã bị session khác xử lý, procedure không cho ghi đè.

Câu kết:

> Bản lỗi dùng update trực tiếp nên bị Lost Update. Bản xử lý của app dùng transaction `SERIALIZABLE` ở Java và gọi `SP_XuLyYeuCauPhatHanh`, trong procedure có `FOR UPDATE` và kiểm tra trạng thái. Vì vậy session xử lý sau không thể âm thầm ghi đè kết quả của session trước.

## 5. Thứ tự demo đề xuất trong 10-15 phút

1. Nói app có các vai trò: người chơi, nhà phát triển, kiểm duyệt viên, marketing, CSKH.
2. Demo người chơi mua game với mã `WELCOME50`.
3. Mở VSCode chỉ `TRG_XuLyKhiGiaoDichThanhCong`.
4. Nhân tiện chỉ `SF_KiemTraMaGiamGia` vì vừa dùng mã giảm giá.
5. Đăng nhập `moderator01`, duyệt một yêu cầu phát hành.
6. Mở VSCode chỉ `SP_XuLyYeuCauPhatHanh` và controller Java gọi procedure.
7. Chạy demo Lost Update Before bằng hai cửa sổ app với `DEMO_CHAN_LOST_UPDATE = false`.
8. Chạy demo Lost Update After bằng hai cửa sổ app với `DEMO_CHAN_LOST_UPDATE = true`.
9. Mở code giải pháp `SERIALIZABLE` + `FOR UPDATE`.
10. Kết luận: logic quan trọng được đưa xuống Oracle để đảm bảo toàn vẹn và xử lý đồng thời.

## 6. Câu kết tổng quát

Có thể kết phần HQTCSDL như sau:

> Trong source code, app JavaFX chỉ đóng vai trò giao diện và gọi nghiệp vụ. Các phần quan trọng của HQTCSDL được thể hiện ở Oracle: trigger tự đồng bộ dữ liệu sau thanh toán, stored procedure kiểm soát quy trình kiểm duyệt phát hành, stored function kiểm tra mã giảm giá, và transaction `SERIALIZABLE` kết hợp `FOR UPDATE` để xử lý Lost Update khi nhiều kiểm duyệt viên thao tác đồng thời.
