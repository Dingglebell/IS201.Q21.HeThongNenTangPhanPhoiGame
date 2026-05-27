package com.gameplatform.controller;

import com.gameplatform.database.Database;
import com.gameplatform.model.GameTrongGioHang;
import com.gameplatform.model.ThongTinGame;
import com.gameplatform.model.ThongTinGiaoDich;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public final class QuanLyMuaHangController {
    private static final String RELEASED_GAME_SELECT = """
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

    public List<ThongTinGame> findWishlist(int playerId) throws SQLException {
        String sql = RELEASED_GAME_SELECT + """
                JOIN Wishlist wl ON wl.MaGame = g.MaGame
                WHERE wl.MaNguoiChoi = ?
                ORDER BY wl.NgayThem DESC
                """;
        List<ThongTinGame> games = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, playerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    games.add(mapGame(resultSet));
                }
            }
        }
        return games;
    }

    public boolean addToWishlist(int playerId, int gameId) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement check = connection.prepareStatement("""
                     SELECT COUNT(*)
                     FROM Wishlist
                     WHERE MaNguoiChoi = ?
                       AND MaGame = ?
                     """)) {
            check.setInt(1, playerId);
            check.setInt(2, gameId);
            try (ResultSet resultSet = check.executeQuery()) {
                resultSet.next();
                if (resultSet.getInt(1) > 0) {
                    return false;
                }
            }
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO Wishlist (MaNguoiChoi, MaGame, NgayThem)
                    VALUES (?, ?, SYSDATE)
                    """)) {
                statement.setInt(1, playerId);
                statement.setInt(2, gameId);
                statement.executeUpdate();
            }
            return true;
        }
    }

    public boolean isInWishlist(int playerId, int gameId) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT COUNT(*)
                     FROM Wishlist
                     WHERE MaNguoiChoi = ?
                       AND MaGame = ?
                     """)) {
            statement.setInt(1, playerId);
            statement.setInt(2, gameId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }

    public void removeFromWishlist(int playerId, int gameId) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     DELETE FROM Wishlist
                     WHERE MaNguoiChoi = ?
                       AND MaGame = ?
                     """)) {
            statement.setInt(1, playerId);
            statement.setInt(2, gameId);
            statement.executeUpdate();
        }
    }

    public List<GameTrongGioHang> findCart(int playerId) throws SQLException {
        String sql = """
                SELECT g.MaGame,
                       g.TenGame,
                       npt.TenNPT,
                       g.GiaGoc,
                       SF_TinhGiaHienTai(g.MaGame) AS GiaBan,
                       gh.NgayThem
                FROM GioHang gh
                JOIN Game g ON g.MaGame = gh.MaGame
                JOIN NhaPhatTrien npt ON npt.MaNPT = g.MaNPT
                WHERE gh.MaNguoiChoi = ?
                ORDER BY gh.NgayThem DESC
                """;
        List<GameTrongGioHang> items = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, playerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new GameTrongGioHang(
                            resultSet.getInt("MaGame"),
                            resultSet.getString("TenGame"),
                            resultSet.getString("TenNPT"),
                            resultSet.getBigDecimal("GiaGoc"),
                            resultSet.getBigDecimal("GiaBan"),
                            JdbcHelper.localDateTime(resultSet, "NgayThem")
                    ));
                }
            }
        }
        return items;
    }

    public boolean addToCart(int playerId, int gameId) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement check = connection.prepareStatement("""
                     SELECT COUNT(*)
                     FROM GioHang
                     WHERE MaNguoiChoi = ?
                       AND MaGame = ?
                     """)) {
            check.setInt(1, playerId);
            check.setInt(2, gameId);
            try (ResultSet resultSet = check.executeQuery()) {
                resultSet.next();
                if (resultSet.getInt(1) > 0) {
                    return false;
                }
            }
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO GioHang (MaNguoiChoi, MaGame, NgayThem)
                    VALUES (?, ?, SYSDATE)
                    """)) {
                statement.setInt(1, playerId);
                statement.setInt(2, gameId);
                statement.executeUpdate();
            }
            return true;
        }
    }

    public boolean isInCart(int playerId, int gameId) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT COUNT(*)
                     FROM GioHang
                     WHERE MaNguoiChoi = ?
                       AND MaGame = ?
                     """)) {
            statement.setInt(1, playerId);
            statement.setInt(2, gameId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }

    public void removeFromCart(int playerId, int gameId) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     DELETE FROM GioHang
                     WHERE MaNguoiChoi = ?
                       AND MaGame = ?
                     """)) {
            statement.setInt(1, playerId);
            statement.setInt(2, gameId);
            statement.executeUpdate();
        }
    }

    public int checkoutCart(int playerId, String discountCode) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            int transactionId;
            try (CallableStatement create = connection.prepareCall("{call SP_TaoGiaoDichTuGioHang(?, ?, ?, ?)}")) {
                create.setInt(1, playerId);
                if (discountCode == null || discountCode.isBlank()) {
                    create.setNull(2, Types.VARCHAR);
                } else {
                    create.setString(2, discountCode.trim());
                }
                create.setString(3, "Ví điện tử");
                create.registerOutParameter(4, Types.NUMERIC);
                create.execute();
                transactionId = create.getInt(4);
            }
            try (CallableStatement confirm = connection.prepareCall("{call SP_XacNhanThanhToan(?, ?)}")) {
                confirm.setInt(1, transactionId);
                confirm.setString(2, "Thành công");
                confirm.execute();
            }
            return transactionId;
        }
    }

    public List<ThongTinGiaoDich> findTransactions(int playerId) throws SQLException {
        String sql = """
                SELECT gd.MaGD,
                       NVL(LISTAGG(g.TenGame, ', ') WITHIN GROUP (ORDER BY g.TenGame), '') AS Games,
                       gd.TongTienGoc,
                       gd.TongGiamGia,
                       gd.TongThanhToan,
                       gd.PhuongThucThanhToan,
                       gd.NgayGD,
                       gd.TrangThai
                FROM GiaoDich gd
                LEFT JOIN ChiTietGiaoDich ct ON ct.MaGD = gd.MaGD
                LEFT JOIN Game g ON g.MaGame = ct.MaGame
                WHERE gd.MaNguoiChoi = ?
                GROUP BY gd.MaGD, gd.TongTienGoc, gd.TongGiamGia, gd.TongThanhToan,
                         gd.PhuongThucThanhToan, gd.NgayGD, gd.TrangThai
                ORDER BY gd.NgayGD DESC
                """;
        List<ThongTinGiaoDich> transactions = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, playerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(new ThongTinGiaoDich(
                            resultSet.getInt("MaGD"),
                            resultSet.getString("Games"),
                            resultSet.getBigDecimal("TongTienGoc"),
                            resultSet.getBigDecimal("TongGiamGia"),
                            resultSet.getBigDecimal("TongThanhToan"),
                            resultSet.getString("PhuongThucThanhToan"),
                            JdbcHelper.localDateTime(resultSet, "NgayGD"),
                            resultSet.getString("TrangThai")
                    ));
                }
            }
        }
        return transactions;
    }

    public List<ThongTinGiaoDich> findAllTransactions() throws SQLException {
        String sql = """
                SELECT gd.MaGD,
                       NVL(LISTAGG(g.TenGame, ', ') WITHIN GROUP (ORDER BY g.TenGame), '') AS Games,
                       gd.TongTienGoc,
                       gd.TongGiamGia,
                       gd.TongThanhToan,
                       gd.PhuongThucThanhToan,
                       gd.NgayGD,
                       gd.TrangThai
                FROM GiaoDich gd
                LEFT JOIN ChiTietGiaoDich ct ON ct.MaGD = gd.MaGD
                LEFT JOIN Game g ON g.MaGame = ct.MaGame
                GROUP BY gd.MaGD, gd.TongTienGoc, gd.TongGiamGia, gd.TongThanhToan,
                         gd.PhuongThucThanhToan, gd.NgayGD, gd.TrangThai
                ORDER BY gd.NgayGD DESC
                """;
        List<ThongTinGiaoDich> transactions = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                transactions.add(new ThongTinGiaoDich(
                        resultSet.getInt("MaGD"),
                        resultSet.getString("Games"),
                        resultSet.getBigDecimal("TongTienGoc"),
                        resultSet.getBigDecimal("TongGiamGia"),
                        resultSet.getBigDecimal("TongThanhToan"),
                        resultSet.getString("PhuongThucThanhToan"),
                        JdbcHelper.localDateTime(resultSet, "NgayGD"),
                        resultSet.getString("TrangThai")
                ));
            }
        }
        return transactions;
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
}







