# Giải thích từng file SQL trong đồ án HQTCSDL

Tài liệu này dùng để ôn phần database trước khi báo cáo môn Hệ Quản Trị Cơ Sở Dữ Liệu. Thư mục `sql` được tách thành nhiều file nhỏ để khi trình bày có thể nói rõ:

- File nào tạo user/schema.
- File nào tạo cấu trúc bảng.
- File nào gắn ràng buộc toàn vẹn.
- File nào chứa trigger, stored function, stored procedure.
- File nào nạp dữ liệu mẫu.
- File nào dùng riêng cho demo truy xuất đồng thời.

Thứ tự chạy đầy đủ nằm trong `99CaiDatDayDu.sql`.

## Tổng quan thứ tự file

| Thứ tự | File | Mục đích ngắn gọn |
|---|---|---|
| 00 | `00TaoUserGamePlatform.sql` | Tạo user/schema `GAME_PLATFORM` và cấp quyền |
| 01 | `01XoaSchemaCu.sql` | Reset schema cũ để setup lại sạch |
| 02 | `02TaoSequence.sql` | Tạo sequence sinh mã tự động |
| 03 | `03TaoBang.sql` | Tạo các bảng dữ liệu |
| 04 | `04TaoRangBuoc.sql` | Gắn khóa chính, khóa ngoại, unique, check |
| 05 | `05TaoTrigger.sql` | Tạo trigger bảo vệ nghiệp vụ tự động |
| 06 | `06TaoStoredFunction.sql` | Tạo function tính toán/trả về giá trị |
| 07 | `07TaoStoredProcedure.sql` | Tạo procedure xử lý các use case chính |
| 08 | `08NapDuLieuMau.sql` | Nạp dữ liệu mẫu để demo app |
| 99 | `99CaiDatDayDu.sql` | File tổng gọi tất cả file setup |

## 00TaoUserGamePlatform.sql

Mục đích:

File này tạo user/schema riêng cho đồ án là `GAME_PLATFORM`. Đây là schema chứa toàn bộ bảng, sequence, trigger, function, procedure và dữ liệu của hệ thống.

Vì sao cần file này:

- Không nên tạo bảng trực tiếp trong user SYS/SYSTEM.
- Tách riêng schema giúp đồ án sạch, dễ reset và dễ trình bày.
- Khi demo, chỉ cần nói mọi object nghiệp vụ đều nằm trong schema `GAME_PLATFORM`.

Nội dung chính:

- `ALTER SESSION SET "_ORACLE_SCRIPT" = TRUE;`
  - Cho phép tạo user trong môi trường Oracle local/CDB tùy cấu hình.
- Kiểm tra `dba_users` xem user `GAME_PLATFORM` đã tồn tại chưa.
- Nếu chưa có thì chạy `CREATE USER GAME_PLATFORM IDENTIFIED BY game123`.
- Cấp quyền:
  - `CREATE SESSION`: cho phép đăng nhập.
  - `CREATE TABLE`: cho phép tạo bảng.
  - `CREATE VIEW`: cho phép tạo view nếu cần.
  - `CREATE SEQUENCE`: cho phép tạo sequence sinh khóa.
  - `CREATE TRIGGER`: cho phép tạo trigger.
  - `CREATE PROCEDURE`: cho phép tạo procedure/function.

Điểm cần nói khi báo cáo:

> File 00 là bước chuẩn bị môi trường. Nó tạo schema riêng `GAME_PLATFORM` và cấp đúng các quyền cần thiết để chạy toàn bộ database của hệ thống, thay vì thao tác trực tiếp trên SYS/SYSTEM.

## 01XoaSchemaCu.sql

Mục đích:

File này xóa các object cũ trong schema `GAME_PLATFORM` để có thể setup lại database từ đầu.

Quan trọng:

File này **không chạy khi app thoát** và **không ảnh hưởng dữ liệu khi app đang dùng bình thường**. Nó chỉ chạy khi mình chủ động reset database bằng `99CaiDatDayDu.sql`.

Vì sao cần file này:

- Khi sửa bảng, trigger, procedure, nếu chạy lại `CREATE TABLE` sẽ lỗi vì object đã tồn tại.
- File này giúp quá trình demo có thể reset về trạng thái sạch.
- Dữ liệu sau reset sẽ quay về dữ liệu mẫu trong `08NapDuLieuMau.sql`.

Nội dung chính:

- Xóa view cũ nếu có.
- Xóa stored procedure và stored function cũ.
- Xóa bảng theo danh sách, dùng `CASCADE CONSTRAINTS PURGE`.
- Xóa sequence cũ.

Vì sao phải xóa theo thứ tự:

- View phụ thuộc vào bảng nên xóa view trước.
- Procedure/function có thể tham chiếu bảng nên xóa trước khi xóa bảng.
- Bảng có khóa ngoại phụ thuộc nhau nên dùng `CASCADE CONSTRAINTS`.
- Sequence độc lập nên xóa sau cũng được.

Điểm cần nói khi báo cáo:

> File 01 dùng cho reset môi trường demo. Nó giúp đảm bảo mỗi lần chạy setup, database bắt đầu từ trạng thái sạch và không bị lỗi object đã tồn tại.

## 02TaoSequence.sql

Mục đích:

File này tạo các sequence để sinh mã tự động cho các bảng chính.

Sequence là gì:

Sequence là object của Oracle dùng để sinh số tăng dần, thường dùng làm khóa chính. Ví dụ `SEQ_Game.NEXTVAL` sinh mã game mới.

Các sequence chính:

- `SEQ_TaiKhoan`
- `SEQ_NhanVien`
- `SEQ_NhaPhatTrien`
- `SEQ_NguoiChoi`
- `SEQ_TheLoai`
- `SEQ_Game`
- `SEQ_GameMedia`
- `SEQ_PhienBanGame`
- `SEQ_YeuCauPhatHanh`
- `SEQ_GiaoDich`
- `SEQ_DanhGia`
- `SEQ_KhuyenMai`
- `SEQ_MaGiamGia`
- `SEQ_Ticket`

Vì sao `START WITH 1000`:

- Dữ liệu mẫu dùng mã nhỏ như 1, 2, 3...
- Dữ liệu người dùng tạo trong lúc demo sẽ bắt đầu từ 1000.
- Nhìn vào mã có thể phân biệt dữ liệu mẫu và dữ liệu phát sinh khi demo.

Vì sao dùng `NOCACHE`:

- Khi demo, mã tăng dễ quan sát hơn.
- Tránh cảm giác bị nhảy số do Oracle cache sequence.

Điểm cần nói khi báo cáo:

> File 02 tạo bộ sequence sinh khóa chính. Dữ liệu mẫu dùng ID nhỏ, còn dữ liệu phát sinh trong app bắt đầu từ 1000 để dễ phân biệt khi demo.

## 03TaoBang.sql

Mục đích:

File này tạo toàn bộ bảng dữ liệu của hệ thống, nhưng chưa gắn khóa chính, khóa ngoại và check constraint.

Vì sao tách bảng và ràng buộc ra riêng:

- Dễ trình bày: file 03 là cấu trúc dữ liệu, file 04 là toàn vẹn dữ liệu.
- Khi bảng có quan hệ vòng hoặc phụ thuộc nhiều, tạo bảng trước rồi gắn ràng buộc sau sẽ dễ kiểm soát hơn.
- Khi demo HQTCSDL, giảng viên nhìn vào sẽ thấy rõ thiết kế vật lý và ràng buộc được tách riêng.

Nhóm bảng tài khoản và actor:

- `TaiKhoan`: thông tin đăng nhập chung.
- `NhanVien`: thông tin nhân viên nội bộ, gồm vai trò quản lý, kiểm duyệt, marketing, CSKH.
- `NhaPhatTrien`: thông tin nhà phát triển game.
- `NguoiChoi`: thông tin người chơi.

Ý nghĩa:

`TaiKhoan` là bảng trung tâm cho đăng nhập. Tùy `LoaiTaiKhoan`, một tài khoản sẽ liên kết sang `NguoiChoi`, `NhaPhatTrien` hoặc `NhanVien`.

Nhóm bảng game:

- `TheLoai`: danh mục thể loại.
- `Game`: thông tin game.
- `GameMedia`: ảnh bìa, ảnh phụ, video của game.
- `DanhMucTheLoai`: bảng trung gian nhiều-nhiều giữa `Game` và `TheLoai`.
- `PhienBanGame`: các bản build/phiên bản của game.
- `YeuCauPhatHanh`: yêu cầu phát hành gửi cho kiểm duyệt viên.

Ý nghĩa:

Nhà phát triển tạo game và phiên bản. Game ban đầu có trạng thái `Chưa phát hành`, sau khi yêu cầu được duyệt thì chuyển sang `Đang phát hành`.

Nhóm bảng mua hàng:

- `Wishlist`: game người chơi quan tâm.
- `GioHang`: game người chơi chuẩn bị mua.
- `MaGiamGia`: mã giảm giá theo tổng giỏ hàng.
- `GiaoDich`: hóa đơn/giao dịch.
- `ChiTietGiaoDich`: từng game trong giao dịch, lưu giá tại thời điểm mua.
- `SoHuuGame`: game người chơi đã sở hữu.

Ý nghĩa:

Khi thanh toán thành công, dữ liệu đi từ `GioHang` sang `GiaoDich`, `ChiTietGiaoDich`, rồi trigger tạo `SoHuuGame`.

Nhóm bảng tương tác:

- `DanhGia`: đánh giá game của người chơi.
- `KhuyenMai`: chương trình khuyến mãi.
- `ChiTietKhuyenMai`: game nào được giảm bao nhiêu phần trăm trong chương trình.
- `Ticket`: yêu cầu hỗ trợ của người chơi.

Điểm cần nói khi báo cáo:

> File 03 là phần thiết kế vật lý bảng. Các bảng được chia theo actor và nghiệp vụ: tài khoản, game, phát hành, mua hàng, khuyến mãi, đánh giá và hỗ trợ.

## 04TaoRangBuoc.sql

Mục đích:

File này gắn toàn bộ ràng buộc toàn vẹn dữ liệu cho các bảng đã tạo ở file 03.

Các loại ràng buộc:

- Primary key: định danh duy nhất mỗi dòng.
- Foreign key: bảo đảm quan hệ giữa các bảng.
- Unique: chống trùng username, email, mã giảm giá, đánh giá trùng.
- Check: giới hạn giá trị hợp lệ cho trạng thái, vai trò, giá tiền, độ tuổi, ngày tháng.

Ví dụ quan trọng:

- `UQ_TaiKhoan_TenDangNhap`: tên đăng nhập không được trùng.
- `CK_TaiKhoan_Loai`: loại tài khoản chỉ được là `Người chơi`, `Nhà phát triển`, `Nhân viên`.
- `CK_NhanVien_VaiTro`: vai trò nhân viên chỉ được là `Quản lý nền tảng`, `Kiểm duyệt viên`, `Marketing`, `CSKH`.
- `CK_Game_TrangThai`: game chỉ có trạng thái `Đang phát hành`, `Chưa phát hành`, `Đã gỡ bỏ`.
- `CK_Game_NgayPhatHanh`: nếu game đang phát hành thì phải có ngày phát hành.
- `PK_Wishlist` và `PK_GioHang`: một người chơi không thể thêm cùng một game nhiều lần vào wishlist/giỏ hàng.
- `UQ_DanhGia_NguoiChoi_Game`: một người chơi chỉ có một đánh giá cho một game.
- `CK_Ticket_XuLy`: ticket đã xử lý phải có nhân viên xử lý, nội dung phản hồi và ngày xử lý.

Vì sao constraint quan trọng:

- UI có thể kiểm tra trước, nhưng database mới là tầng bảo vệ cuối.
- Nếu ai đó insert/update trực tiếp bằng SQL, constraint vẫn chặn dữ liệu sai.
- Đây là phần rất đúng trọng tâm môn HQTCSDL: đảm bảo toàn vẹn dữ liệu ở tầng DBMS.

Điểm cần nói khi báo cáo:

> File 04 là lớp bảo vệ dữ liệu bằng constraint. App chỉ hỗ trợ nhập liệu thuận tiện, còn database đảm bảo dữ liệu cuối cùng luôn hợp lệ.

## 05TaoTrigger.sql

Mục đích:

File này tạo các trigger tự động chạy khi dữ liệu thay đổi. Trigger dùng cho các nghiệp vụ cần DB tự bảo vệ hoặc tự đồng bộ nhiều bảng.

### TRG_XuLyKhiGiaoDichThanhCong

Khi nào chạy:

Sau khi bảng `GiaoDich` được update, nếu trạng thái chuyển sang `Thành công`.

Nó làm gì:

- Insert game đã mua vào `SoHuuGame`.
- Tăng `Game.LuotMua`.
- Xóa game khỏi `GioHang`.
- Xóa game khỏi `Wishlist`.
- Tăng `MaGiamGia.LuotDung` nếu có dùng mã giảm giá.

Điểm HQTCSDL:

- Đây là trigger đồng bộ dữ liệu sau thanh toán.
- Trigger cập nhật lượt mua theo `ORDER BY MaGame` để giảm nguy cơ deadlock khi nhiều giao dịch cùng cập nhật nhiều game.

Câu nói mẫu:

> Khi giao dịch thành công, app không tự update rời rạc từng bảng. Trigger đảm bảo các hệ quả sau thanh toán được thực hiện tự động và nhất quán.

### TRG_XuLyDuyetYCPH

Khi nào chạy:

Sau khi `YeuCauPhatHanh.TrangThai` chuyển sang `Đã duyệt`.

Nó làm gì:

- Cập nhật `Game.TrangThai = 'Đang phát hành'`.
- Gán `Game.NgayPhatHanh` nếu chưa có.
- Cập nhật `PhienBanGame.TrangThai = 'Đang phát hành'`.

Ý nghĩa:

Kiểm duyệt viên chỉ xử lý yêu cầu. Trigger tự cập nhật game và phiên bản để tránh quên cập nhật bảng liên quan.

### TRG_KiemTraGioHang

Khi nào chạy:

Trước khi insert/update `GioHang`.

Nó kiểm tra:

- Game phải đang phát hành.
- Người chơi chưa sở hữu game.
- Người chơi đủ tuổi theo `Game.DoTuoi`.

Nếu sai:

Trigger báo lỗi bằng `RAISE_APPLICATION_ERROR`.

### TRG_KiemTraDanhGia

Khi nào chạy:

Trước khi insert/update `DanhGia`.

Nó kiểm tra:

Người chơi chỉ được đánh giá game đã sở hữu.

### TRG_KiemTraWishlist

Khi nào chạy:

Trước khi insert/update `Wishlist`.

Nó kiểm tra:

- Chỉ thêm game đang phát hành.
- Không thêm game đã sở hữu vào wishlist.

### TRG_TuDongGhiNhanNgayXuLyTicket

Khi nào chạy:

Trước khi update `Ticket`.

Nó làm gì:

- Nếu ticket chuyển sang `Đã xử lý`, bắt buộc có nhân viên xử lý.
- Bắt buộc có nội dung phản hồi.
- Nếu chưa có ngày xử lý thì tự gán `SYSDATE`.

Điểm cần nói khi báo cáo:

> File 05 là nơi đặt các luật nghiệp vụ tự động. Trigger giúp dữ liệu nhất quán ngay cả khi thao tác không đi qua app.

## 06TaoStoredFunction.sql

Mục đích:

File này tạo các stored function. Function dùng để tính toán và trả về một giá trị.

Khác procedure ở điểm:

- Function thường dùng trong câu `SELECT`.
- Function có `RETURN`.
- Procedure thường dùng để thực hiện một quy trình có nhiều bước.

### SF_TinhGiaHienTai

Mục đích:

Tính giá hiện tại của game sau khuyến mãi.

Cách tính:

- Lấy `Game.GiaGoc`.
- Tìm phần trăm khuyến mãi cao nhất đang hiệu lực.
- Trả về giá sau giảm.

Dùng ở:

- Cửa hàng.
- Giỏ hàng.
- Tạo giao dịch từ giỏ hàng.

### SF_KiemTraMaGiamGia

Mục đích:

Kiểm tra mã giảm giá khi thanh toán.

Nó kiểm tra:

- Code có tồn tại không.
- Mã đang hiệu lực không.
- Ngày hiện tại có nằm trong thời gian áp dụng không.
- Lượt dùng còn không.
- Tổng tiền có đạt tối thiểu không.

Kết quả:

- Trả số tiền giảm nếu hợp lệ.
- Trả `0` nếu không nhập mã.
- Trả `-1` nếu mã không hợp lệ.

### SF_KiemTraDoTuoi

Mục đích:

Kiểm tra người chơi có đủ tuổi mua game không.

Kết quả:

- `1`: đủ tuổi.
- `0`: không đủ tuổi hoặc dữ liệu không tồn tại.

### SF_KiemTraSoHuuGame

Mục đích:

Kiểm tra người chơi đã sở hữu game chưa.

Kết quả:

- `1`: đã sở hữu.
- `0`: chưa sở hữu.

### SF_TinhTongTienGioHang

Mục đích:

Tính tổng tiền giỏ hàng dựa trên giá hiện tại của từng game.

Điểm hay:

Function này gọi lại `SF_TinhGiaHienTai`, tức là function có thể tái sử dụng logic của function khác.

### SF_TinhDiemTrungBinhGame

Mục đích:

Tính điểm đánh giá trung bình của game.

Dùng khi cần thống kê hoặc hiển thị chất lượng game.

### SF_TinhDoanhThuNPT

Mục đích:

Tính doanh thu thực nhận của một nhà phát triển trong khoảng ngày.

Cách tính:

Tổng `ChiTietGiaoDich.GiaBan * NhaPhatTrien.TyLeChiaSe` với các giao dịch thành công.

### SF_TongChiTieuNguoiChoi

Mục đích:

Tính tổng tiền người chơi đã chi cho các giao dịch thành công.

Điểm cần nói khi báo cáo:

> File 06 gom các phép tính nghiệp vụ thường dùng. Đưa logic tính giá, mã giảm giá, doanh thu xuống database giúp kết quả thống nhất giữa nhiều màn hình.

## 07TaoStoredProcedure.sql

Mục đích:

File này tạo các stored procedure xử lý use case chính của hệ thống.

Procedure khác function:

- Procedure không nhất thiết trả về một giá trị.
- Có thể insert/update nhiều bảng.
- Có thể dùng tham số OUT.
- Phù hợp với nghiệp vụ nhiều bước.

### SP_DangKyNguoiChoi

Mục đích:

Đăng ký tài khoản người chơi.

Nó làm gì:

- Lấy mã mới từ `SEQ_TaiKhoan`.
- Lấy mã mới từ `SEQ_NguoiChoi`.
- Insert vào `TaiKhoan`.
- Insert vào `NguoiChoi`.

Ý nghĩa:

Tạo tài khoản đăng nhập và hồ sơ người chơi trong cùng một nghiệp vụ.

### SP_DangKyNhaPhatTrien

Mục đích:

Đăng ký tài khoản nhà phát triển.

Nó làm gì:

- Insert vào `TaiKhoan`.
- Insert vào `NhaPhatTrien`.
- Gán mặc định `TyLeChiaSe = 0.70`.

### SP_TaoGame

Mục đích:

Tạo game mới cho nhà phát triển.

Trạng thái ban đầu:

`Chưa phát hành`.

Ý nghĩa:

Game mới chưa được hiện ở cửa hàng cho tới khi được kiểm duyệt.

### SP_TaoYeuCauPhatHanh

Mục đích:

Tạo yêu cầu phát hành game.

Nó kiểm tra:

- Game có thuộc nhà phát triển này không.
- Phiên bản có thuộc game này không.
- Game đã có yêu cầu chờ duyệt chưa.

Nếu hợp lệ:

Insert vào `YeuCauPhatHanh` với trạng thái `Chờ duyệt`.

### SP_XuLyYeuCauPhatHanh

Mục đích:

Kiểm duyệt viên duyệt hoặc từ chối yêu cầu phát hành.

Điểm HQTCSDL quan trọng:

```sql
SELECT TrangThai
INTO vTrangThai
FROM YeuCauPhatHanh
WHERE MaYeuCau = pMaYeuCau
FOR UPDATE;
```

Ý nghĩa:

- Khóa dòng yêu cầu đang xử lý.
- Chặn Lost Update khi hai kiểm duyệt viên xử lý cùng một yêu cầu.
- Kiểm tra yêu cầu còn `Chờ duyệt` thì mới cho xử lý.

### SP_ThemGameVaoGioHang

Mục đích:

Thêm game vào giỏ hàng.

Nó chỉ insert vào `GioHang`. Các luật như game phải đang phát hành, chưa sở hữu, đủ tuổi được trigger `TRG_KiemTraGioHang` kiểm tra.

### SP_TaoGiaoDichTuGioHang

Mục đích:

Tạo giao dịch từ giỏ hàng.

Nó làm gì:

- Kiểm tra giỏ hàng có game.
- Duyệt từng game trong giỏ.
- Gọi `SF_TinhGiaHienTai` để lấy giá sau khuyến mãi.
- Nếu có mã giảm giá, gọi `SF_KiemTraMaGiamGia`.
- Insert vào `GiaoDich`.
- Insert từng dòng vào `ChiTietGiaoDich`.

Điểm quan trọng:

Giá mua được lưu vào `ChiTietGiaoDich.GiaBan`, nên lịch sử giao dịch không bị đổi khi giá game/khuyến mãi thay đổi sau này.

### SP_XacNhanThanhToan

Mục đích:

Chuyển giao dịch sang `Thành công` hoặc `Thất bại`.

Điểm HQTCSDL:

- Dùng `FOR UPDATE` để khóa dòng giao dịch.
- Nếu giao dịch đã thành công thì return, tránh xử lý lặp.
- Khi update sang `Thành công`, trigger `TRG_XuLyKhiGiaoDichThanhCong` tự chạy.

### SP_XuLyTicket

Mục đích:

CSKH phản hồi và đóng ticket.

Nó làm gì:

- Update `Ticket.TrangThai = 'Đã xử lý'`.
- Gán `MaNVXuLy`.
- Gán `NoiDungPhanHoi`.

Sau đó trigger `TRG_TuDongGhiNhanNgayXuLyTicket` kiểm tra và tự gán ngày xử lý.

### SP_ThemGameVaoKhuyenMai

Mục đích:

Thêm game vào chương trình khuyến mãi với phần trăm giảm.

Nó insert vào `ChiTietKhuyenMai`.

### SP_TaoMaGiamGia

Mục đích:

Tạo mã giảm giá dùng khi thanh toán.

Nó insert vào `MaGiamGia` với `LuotDung = 0` và trạng thái `Đang hiệu lực`.

Điểm cần nói khi báo cáo:

> File 07 là tầng nghiệp vụ của database. Các use case quan trọng như đăng ký, duyệt phát hành, thanh toán, xử lý ticket được gom vào stored procedure để đảm bảo các bước chạy nhất quán.

## 08NapDuLieuMau.sql

Mục đích:

File này nạp dữ liệu mẫu để chạy app và demo nghiệp vụ.

Vì sao cần:

- Không phải nhập dữ liệu thủ công trước buổi báo cáo.
- Có sẵn tài khoản cho từng vai trò.
- Có sẵn game, khuyến mãi, mã giảm giá, giao dịch, ticket, yêu cầu phát hành.
- Có dữ liệu phục vụ báo cáo doanh thu và demo truy xuất đồng thời.

Dữ liệu chính:

- Tài khoản demo:
  - `player01`
  - `dev01`
  - `manager01`
  - `moderator01`
  - `marketing01`
  - `cskh01`
- Mật khẩu chung là `123456`, lưu dưới dạng SHA-256.
- Nhiều game có trạng thái khác nhau:
  - `Đang phát hành`
  - `Chưa phát hành`
  - `Đã gỡ bỏ`
- Ảnh bìa trong bảng `GameMedia`, trỏ tới thư mục `anhBiaGame`.
- Các giao dịch thành công để có dữ liệu báo cáo.
- Các ticket chờ xử lý và đang xử lý để CSKH demo.
- Các yêu cầu phát hành để kiểm duyệt viên demo.
- Các khuyến mãi và mã giảm giá để marketing demo.

Điểm cần nói khi báo cáo:

> File 08 là dữ liệu kịch bản. Nó không phải cấu trúc hệ thống, mà dùng để app có dữ liệu phong phú ngay khi mở lên demo.

## 99CaiDatDayDu.sql

Mục đích:

File này là file tổng để setup toàn bộ database chỉ bằng một lệnh.

Nó làm gì:

1. Mở PDB `ORCLPDB` nếu chưa mở.
2. Chuyển session vào container `ORCLPDB`.
3. Chạy `00TaoUserGamePlatform.sql`.
4. Connect vào `GAME_PLATFORM`.
5. Chạy lần lượt:
   - `01XoaSchemaCu.sql`
   - `02TaoSequence.sql`
   - `03TaoBang.sql`
   - `04TaoRangBuoc.sql`
   - `05TaoTrigger.sql`
   - `06TaoStoredFunction.sql`
   - `07TaoStoredProcedure.sql`
   - `08NapDuLieuMau.sql`

Vì sao file này quan trọng:

- Giúp reset database nhanh trước buổi demo.
- Giảm rủi ro chạy sai thứ tự.
- Khi giảng viên hỏi cách cài database, chỉ cần chỉ file này.

Lệnh chạy:

```powershell
$env:NLS_LANG='.AL32UTF8'
sqlplus / as sysdba @sql/99CaiDatDayDu.sql
```

Điểm cần nói khi báo cáo:

> File 99 là script tổng. Nó đóng vai trò orchestration, gọi các file SQL theo đúng thứ tự phụ thuộc từ tạo user, reset schema, tạo cấu trúc, tạo logic nghiệp vụ đến nạp dữ liệu mẫu.

## Cách trả lời nhanh khi giảng viên hỏi

Nếu hỏi vì sao tách nhiều file:

> Nhóm tách file theo loại object trong Oracle: user, sequence, table, constraint, trigger, function, procedure và data. Cách tách này giúp dễ bảo trì, dễ chạy lại từng phần và phù hợp với cách trình bày trong môn HQTCSDL.

Nếu hỏi file nào quan trọng nhất:

> Về setup thì `99CaiDatDayDu.sql` quan trọng nhất vì gọi toàn bộ quy trình. Về nghiệp vụ thì ba file quan trọng nhất là `05TaoTrigger.sql`, `06TaoStoredFunction.sql`, `07TaoStoredProcedure.sql`.

Nếu hỏi app chạy có gọi file SQL không:

> Không. App không chạy các file SQL setup. Các file SQL chỉ dùng để tạo database ban đầu. Khi app chạy, Java gọi vào bảng, procedure, function và trigger đã được tạo trong Oracle.

Nếu hỏi thoát app có mất dữ liệu không:

> Không. Dữ liệu đã insert/update vào Oracle vẫn còn. Dữ liệu chỉ reset khi mình chủ động chạy lại `99CaiDatDayDu.sql`, vì file này gọi `01XoaSchemaCu.sql`.

Nếu hỏi trigger, function, procedure khác nhau thế nào:

> Trigger tự chạy khi dữ liệu thay đổi. Function nhận tham số và trả về một giá trị, thường dùng trong SELECT hoặc procedure. Procedure là một quy trình xử lý nghiệp vụ, có thể insert/update nhiều bảng và có tham số OUT.

## Gợi ý thứ tự mở file khi báo cáo

1. Mở `99CaiDatDayDu.sql` để giới thiệu thứ tự setup.
2. Mở `03TaoBang.sql` để nói về bảng chính.
3. Mở `04TaoRangBuoc.sql` để nói về toàn vẹn dữ liệu.
4. Mở `05TaoTrigger.sql` và chỉ `TRG_XuLyKhiGiaoDichThanhCong`.
5. Mở `06TaoStoredFunction.sql` và chỉ `SF_TinhGiaHienTai`.
6. Mở `07TaoStoredProcedure.sql` và chỉ `SP_XuLyYeuCauPhatHanh` hoặc `SP_TaoGiaoDichTuGioHang`.
7. Trong app kiểm duyệt, mở hai cửa sổ `moderator01`, chọn cùng một yêu cầu `Chờ duyệt` để demo Lost Update Before; sau đó chọn một yêu cầu `Chờ duyệt` khác để demo After.
