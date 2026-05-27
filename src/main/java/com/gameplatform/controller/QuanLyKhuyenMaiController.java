package com.gameplatform.controller;

import com.gameplatform.database.Database;
import com.gameplatform.model.GameTrongKhuyenMai;
import com.gameplatform.model.ChuongTrinhKhuyenMai;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class QuanLyKhuyenMaiController {
    public List<ChuongTrinhKhuyenMai> traCuuChuongTrinh() throws SQLException {
        String sql = """
                SELECT km.MaKM,
                       km.TenKM,
                       km.NgayBatDau,
                       km.NgayKetThuc,
                       km.TrangThai,
                       km.NoiDung,
                       COUNT(ct.MaGame) AS SoGame
                FROM KhuyenMai km
                LEFT JOIN ChiTietKhuyenMai ct ON ct.MaKM = km.MaKM
                GROUP BY km.MaKM, km.TenKM, km.NgayBatDau, km.NgayKetThuc, km.TrangThai, km.NoiDung
                ORDER BY km.MaKM DESC
                """;
        List<ChuongTrinhKhuyenMai> promotions = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                promotions.add(new ChuongTrinhKhuyenMai(
                        resultSet.getInt("MaKM"),
                        resultSet.getString("TenKM"),
                        JdbcHelper.localDate(resultSet, "NgayBatDau"),
                        JdbcHelper.localDate(resultSet, "NgayKetThuc"),
                        resultSet.getString("TrangThai"),
                        resultSet.getString("NoiDung"),
                        resultSet.getInt("SoGame")
                ));
            }
        }
        return promotions;
    }

    public int taoChuongTrinh(String name, LocalDate startDate, LocalDate endDate, String content) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            int promotionId = JdbcHelper.nextValue(connection, "SEQ_KhuyenMai");
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, TrangThai, NoiDung)
                    VALUES (?, ?, ?, ?, 'Đang hiệu lực', ?)
                    """)) {
                statement.setInt(1, promotionId);
                statement.setString(2, name);
                statement.setDate(3, Date.valueOf(startDate));
                statement.setDate(4, Date.valueOf(endDate));
                statement.setString(5, content);
                statement.executeUpdate();
            }
            return promotionId;
        }
    }

    public void ganGameVaoKhuyenMai(int promotionId, int gameId, BigDecimal discountPercent) throws SQLException {
        try (Connection connection = Database.getConnection();
             CallableStatement statement = connection.prepareCall("{call SP_ThemGameVaoKhuyenMai(?, ?, ?)}")) {
            statement.setInt(1, promotionId);
            statement.setInt(2, gameId);
            statement.setBigDecimal(3, discountPercent);
            statement.execute();
        }
    }

    public void capNhatChuongTrinh(int promotionId, String name, LocalDate startDate, LocalDate endDate,
                                String status, String content) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE KhuyenMai
                     SET TenKM = ?, NgayBatDau = ?, NgayKetThuc = ?, TrangThai = ?, NoiDung = ?
                     WHERE MaKM = ?
                     """)) {
            statement.setString(1, name);
            statement.setDate(2, Date.valueOf(startDate));
            statement.setDate(3, Date.valueOf(endDate));
            statement.setString(4, status);
            statement.setString(5, content);
            statement.setInt(6, promotionId);
            statement.executeUpdate();
        }
    }

    public List<GameTrongKhuyenMai> traCuuGameTrongKhuyenMai(int promotionId) throws SQLException {
        String sql = """
                SELECT km.MaKM, ct.MaGame, km.TenKM, g.TenGame, ct.PhanTramKM
                FROM ChiTietKhuyenMai ct
                JOIN KhuyenMai km ON km.MaKM = ct.MaKM
                JOIN Game g ON g.MaGame = ct.MaGame
                WHERE ct.MaKM = ?
                ORDER BY g.TenGame
                """;
        List<GameTrongKhuyenMai> games = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, promotionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    games.add(new GameTrongKhuyenMai(
                            resultSet.getInt("MaKM"),
                            resultSet.getInt("MaGame"),
                            resultSet.getString("TenKM"),
                            resultSet.getString("TenGame"),
                            resultSet.getBigDecimal("PhanTramKM")
                    ));
                }
            }
        }
        return games;
    }

    public void capNhatChuongTrinhGame(int promotionId, int gameId, BigDecimal discountPercent) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE ChiTietKhuyenMai
                     SET PhanTramKM = ?
                     WHERE MaKM = ?
                       AND MaGame = ?
                     """)) {
            statement.setBigDecimal(1, discountPercent);
            statement.setInt(2, promotionId);
            statement.setInt(3, gameId);
            statement.executeUpdate();
        }
    }
}








