package com.gameplatform.controller;

import com.gameplatform.database.Database;
import com.gameplatform.model.GameCuaNhaPhatTrien;
import com.gameplatform.model.ThongTinGame;
import com.gameplatform.model.GameTrongThuVien;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class QuanLyThongTinGameController {
    private static final String GAME_SELECT = """
            SELECT g.MaGame,
                   g.TenGame,
                   npt.TenNPT,
                   NVL((
                       SELECT LISTAGG(tl.TenTheLoai, ', ') WITHIN GROUP (ORDER BY tl.TenTheLoai)
                       FROM DanhMucTheLoai dmtl
                       JOIN TheLoai tl ON tl.MaTheLoai = dmtl.MaTheLoai
                       WHERE dmtl.MaGame = g.MaGame
                   ), 'Chưa phân loại') AS TheLoai,
                   g.DoTuoi,
                   g.GiaGoc,
                   NVL((
                       SELECT MAX(ctkm.PhanTramKM)
                       FROM ChiTietKhuyenMai ctkm
                       JOIN KhuyenMai km ON km.MaKM = ctkm.MaKM
                       WHERE ctkm.MaGame = g.MaGame
                         AND km.TrangThai = 'Đang hiệu lực'
                         AND TRUNC(SYSDATE) BETWEEN km.NgayBatDau AND km.NgayKetThuc
                   ), 0) AS PhanTramKM,
                   g.TrangThai,
                   g.NgayPhatHanh,
                   g.LuotMua,
                   g.MoTa,
                   (
                       SELECT FileMedia
                       FROM (
                           SELECT gm.FileMedia
                           FROM GameMedia gm
                           WHERE gm.MaGame = g.MaGame
                             AND gm.LoaiMedia = 'Ảnh bìa'
                           ORDER BY gm.MaMedia DESC
                       )
                       WHERE ROWNUM = 1
                   ) AS AnhBia
            FROM Game g
            JOIN NhaPhatTrien npt ON npt.MaNPT = g.MaNPT
            """;

    public List<ThongTinGame> findReleasedGames() throws SQLException {
        return queryGames(GAME_SELECT + " WHERE g.TrangThai = 'Đang phát hành' ORDER BY g.TenGame", null);
    }

    public List<ThongTinGame> findReleasedGamesNotOwned(int playerId) throws SQLException {
        return queryGames(GAME_SELECT + """
                 WHERE g.TrangThai = 'Đang phát hành'
                   AND NOT EXISTS (
                       SELECT 1
                       FROM SoHuuGame sh
                       WHERE sh.MaNguoiChoi = ?
                         AND sh.MaGame = g.MaGame
                   )
                 ORDER BY g.TenGame
                """, playerId);
    }

    public List<ThongTinGame> findAllGames() throws SQLException {
        return queryGames(GAME_SELECT + " ORDER BY g.MaGame", null);
    }

    public List<GameCuaNhaPhatTrien> findGamesByDeveloper(int developerId) throws SQLException {
        String sql = """
                SELECT g.MaGame,
                       g.TenGame,
                       g.GiaGoc,
                       g.TrangThai,
                       g.NgayPhatHanh,
                       g.LuotMua
                FROM Game g
                WHERE g.MaNPT = ?
                ORDER BY g.MaGame DESC
                """;
        List<GameCuaNhaPhatTrien> games = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, developerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    games.add(new GameCuaNhaPhatTrien(
                            resultSet.getInt("MaGame"),
                            resultSet.getString("TenGame"),
                            resultSet.getBigDecimal("GiaGoc"),
                            resultSet.getString("TrangThai"),
                            JdbcHelper.localDate(resultSet, "NgayPhatHanh"),
                            resultSet.getInt("LuotMua")
                    ));
                }
            }
        }
        return games;
    }

    public List<GameTrongThuVien> findLibrary(int playerId) throws SQLException {
        String sql = """
                SELECT g.MaGame,
                       g.TenGame,
                       npt.TenNPT,
                       NVL((
                           SELECT LISTAGG(tl.TenTheLoai, ', ') WITHIN GROUP (ORDER BY tl.TenTheLoai)
                           FROM DanhMucTheLoai dmtl
                           JOIN TheLoai tl ON tl.MaTheLoai = dmtl.MaTheLoai
                           WHERE dmtl.MaGame = g.MaGame
                       ), 'Chưa phân loại') AS TheLoai,
                       sh.NgaySoHuu,
                       sh.SoGioChoi,
                       dg.DiemDanhGia,
                       (
                           SELECT pb.FilePhienBan
                           FROM PhienBanGame pb
                           WHERE pb.MaGame = g.MaGame
                             AND pb.TrangThai = 'Đang phát hành'
                             AND pb.FilePhienBan IS NOT NULL
                             AND ROWNUM = 1
                       ) AS FilePhienBan
                FROM SoHuuGame sh
                JOIN Game g ON g.MaGame = sh.MaGame
                JOIN NhaPhatTrien npt ON npt.MaNPT = g.MaNPT
                LEFT JOIN DanhGia dg ON dg.MaGame = g.MaGame AND dg.MaNguoiChoi = sh.MaNguoiChoi
                WHERE sh.MaNguoiChoi = ?
                ORDER BY sh.NgaySoHuu DESC
                """;
        List<GameTrongThuVien> library = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, playerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int rating = resultSet.getInt("DiemDanhGia");
                    library.add(new GameTrongThuVien(
                            resultSet.getInt("MaGame"),
                            resultSet.getString("TenGame"),
                            resultSet.getString("TenNPT"),
                            resultSet.getString("TheLoai"),
                            JdbcHelper.localDateTime(resultSet, "NgaySoHuu"),
                            resultSet.getInt("SoGioChoi"),
                            resultSet.wasNull() ? null : rating,
                            resultSet.getString("FilePhienBan")
                    ));
                }
            }
        }
        return library;
    }

    public int purchaseGame(int playerId, int gameId) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                PriceSnapshot price = loadPriceForPurchase(connection, gameId);
                int transactionId = JdbcHelper.nextValue(connection, "SEQ_GiaoDich");
                BigDecimal discountMoney = price.originalPrice()
                        .multiply(BigDecimal.valueOf(price.discountPercent()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal salePrice = price.originalPrice().subtract(discountMoney);

                try (PreparedStatement transaction = connection.prepareStatement("""
                        INSERT INTO GiaoDich
                            (MaGD, MaNguoiChoi, MaMaGiamGia, TongTienGoc, TongGiamGia, TongThanhToan, PhuongThucThanhToan, TrangThai)
                        VALUES (?, ?, NULL, ?, ?, ?, 'Ví điện tử', 'Chờ thanh toán')
                        """)) {
                    transaction.setInt(1, transactionId);
                    transaction.setInt(2, playerId);
                    transaction.setBigDecimal(3, price.originalPrice());
                    transaction.setBigDecimal(4, discountMoney);
                    transaction.setBigDecimal(5, salePrice);
                    transaction.executeUpdate();
                }

                try (PreparedStatement detail = connection.prepareStatement("""
                        INSERT INTO ChiTietGiaoDich (MaGD, MaGame, GiaGoc, SoTienGiamKM, GiaBan)
                        VALUES (?, ?, ?, ?, ?)
                        """)) {
                    detail.setInt(1, transactionId);
                    detail.setInt(2, gameId);
                    detail.setBigDecimal(3, price.originalPrice());
                    detail.setBigDecimal(4, discountMoney);
                    detail.setBigDecimal(5, salePrice);
                    detail.executeUpdate();
                }

                try (PreparedStatement success = connection.prepareStatement("""
                        UPDATE GiaoDich
                        SET TrangThai = 'Thành công'
                        WHERE MaGD = ?
                        """)) {
                    success.setInt(1, transactionId);
                    success.executeUpdate();
                }

                connection.commit();
                return transactionId;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public int submitGame(int developerId, String title, int ageRating, BigDecimal price, String description,
                          String versionName, String fileName, BigDecimal sizeInMb, String coverMedia) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int gameId = JdbcHelper.nextValue(connection, "SEQ_Game");
                int versionId = JdbcHelper.nextValue(connection, "SEQ_PhienBanGame");
                int requestId = JdbcHelper.nextValue(connection, "SEQ_YeuCauPhatHanh");

                try (PreparedStatement game = connection.prepareStatement("""
                        INSERT INTO Game (MaGame, MaNPT, TenGame, DoTuoi, GiaGoc, TrangThai, MoTa)
                        VALUES (?, ?, ?, ?, ?, 'Chưa phát hành', ?)
                        """)) {
                    game.setInt(1, gameId);
                    game.setInt(2, developerId);
                    game.setString(3, title);
                    game.setInt(4, ageRating);
                    game.setBigDecimal(5, price);
                    game.setString(6, description);
                    game.executeUpdate();
                }

                try (PreparedStatement version = connection.prepareStatement("""
                        INSERT INTO PhienBanGame
                            (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, TrangThai)
                        VALUES (?, ?, ?, 'Bản nộp kiểm duyệt từ nhà phát triển', ?, ?, 'Chưa phát hành')
                        """)) {
                    version.setInt(1, versionId);
                    version.setInt(2, gameId);
                    version.setString(3, versionName);
                    version.setString(4, fileName);
                    version.setBigDecimal(5, sizeInMb);
                    version.executeUpdate();
                }

                if (coverMedia != null && !coverMedia.isBlank()) {
                    try (PreparedStatement media = connection.prepareStatement("""
                            INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia)
                            VALUES (SEQ_GameMedia.NEXTVAL, ?, 'Ảnh bìa', ?)
                            """)) {
                        media.setInt(1, gameId);
                        media.setString(2, coverMedia);
                        media.executeUpdate();
                    }
                }

                try (PreparedStatement request = connection.prepareStatement("""
                        INSERT INTO YeuCauPhatHanh (MaYeuCau, MaNPT, MaGame, MaPhienBan, TrangThai)
                        VALUES (?, ?, ?, ?, 'Chờ duyệt')
                        """)) {
                    request.setInt(1, requestId);
                    request.setInt(2, developerId);
                    request.setInt(3, gameId);
                    request.setInt(4, versionId);
                    request.executeUpdate();
                }

                connection.commit();
                return requestId;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void rateGame(int playerId, int gameId, int rating, String content) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     MERGE INTO DanhGia dg
                     USING (SELECT ? AS MaNguoiChoi, ? AS MaGame FROM dual) src
                     ON (dg.MaNguoiChoi = src.MaNguoiChoi AND dg.MaGame = src.MaGame)
                     WHEN MATCHED THEN
                         UPDATE SET DiemDanhGia = ?, NoiDung = ?, NgayDanhGia = SYSDATE
                     WHEN NOT MATCHED THEN
                         INSERT (MaDanhGia, MaGame, MaNguoiChoi, DiemDanhGia, NoiDung, NgayDanhGia)
                         VALUES (SEQ_DanhGia.NEXTVAL, ?, ?, ?, ?, SYSDATE)
                     """)) {
            statement.setInt(1, playerId);
            statement.setInt(2, gameId);
            statement.setInt(3, rating);
            statement.setString(4, content);
            statement.setInt(5, gameId);
            statement.setInt(6, playerId);
            statement.setInt(7, rating);
            statement.setString(8, content);
            statement.executeUpdate();
        }
    }

    public void deleteReview(int playerId, int gameId) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     DELETE FROM DanhGia
                     WHERE MaNguoiChoi = ?
                       AND MaGame = ?
                     """)) {
            statement.setInt(1, playerId);
            statement.setInt(2, gameId);
            statement.executeUpdate();
        }
    }

    private List<ThongTinGame> queryGames(String sql, Integer id) throws SQLException {
        List<ThongTinGame> games = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (id != null) {
                statement.setInt(1, id);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    games.add(mapGame(resultSet));
                }
            }
        }
        return games;
    }

    private ThongTinGame mapGame(ResultSet resultSet) throws SQLException {
        BigDecimal originalPrice = resultSet.getBigDecimal("GiaGoc");
        int discountPercent = resultSet.getInt("PhanTramKM");
        BigDecimal discount = originalPrice
                .multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return new ThongTinGame(
                resultSet.getInt("MaGame"),
                resultSet.getString("TenGame"),
                resultSet.getString("TenNPT"),
                resultSet.getString("TheLoai"),
                resultSet.getInt("DoTuoi"),
                originalPrice,
                originalPrice.subtract(discount),
                discountPercent,
                resultSet.getString("TrangThai"),
                JdbcHelper.localDate(resultSet, "NgayPhatHanh"),
                resultSet.getInt("LuotMua"),
                resultSet.getString("MoTa"),
                resultSet.getString("AnhBia")
        );
    }

    private PriceSnapshot loadPriceForPurchase(Connection connection, int gameId) throws SQLException {
        String sql = """
                SELECT GiaGoc,
                       NVL((
                           SELECT MAX(ctkm.PhanTramKM)
                           FROM ChiTietKhuyenMai ctkm
                           JOIN KhuyenMai km ON km.MaKM = ctkm.MaKM
                           WHERE ctkm.MaGame = g.MaGame
                             AND km.TrangThai = 'Đang hiệu lực'
                             AND TRUNC(SYSDATE) BETWEEN km.NgayBatDau AND km.NgayKetThuc
                       ), 0) AS PhanTramKM
                FROM Game g
                WHERE MaGame = ?
                  AND TrangThai = 'Đang phát hành'
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gameId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Game không tồn tại hoặc chưa phát hành.");
                }
                return new PriceSnapshot(resultSet.getBigDecimal("GiaGoc"), resultSet.getInt("PhanTramKM"));
            }
        }
    }

    private record PriceSnapshot(BigDecimal originalPrice, int discountPercent) {
    }
}





