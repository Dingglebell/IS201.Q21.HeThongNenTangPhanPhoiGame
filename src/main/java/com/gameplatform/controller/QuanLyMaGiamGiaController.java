package com.gameplatform.controller;

import com.gameplatform.database.Database;
import com.gameplatform.model.ThongTinMaGiamGia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class QuanLyMaGiamGiaController {
    public List<ThongTinMaGiamGia> traCuuMaGiamGia() throws SQLException {
        List<ThongTinMaGiamGia> codes = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT MaMaGiamGia, Code, SoTienGiam, GioiHanSuDung, LuotDung,
                            NgayBatDau, NgayHetHan, TongGiaToiThieu, TrangThai, MoTa
                     FROM MaGiamGia
                     ORDER BY MaMaGiamGia DESC
                     """);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                codes.add(new ThongTinMaGiamGia(
                        resultSet.getInt("MaMaGiamGia"),
                        resultSet.getString("Code"),
                        resultSet.getBigDecimal("SoTienGiam"),
                        resultSet.getInt("GioiHanSuDung"),
                        resultSet.getInt("LuotDung"),
                        JdbcHelper.localDate(resultSet, "NgayBatDau"),
                        JdbcHelper.localDate(resultSet, "NgayHetHan"),
                        resultSet.getBigDecimal("TongGiaToiThieu"),
                        resultSet.getString("TrangThai"),
                        resultSet.getString("MoTa")
                ));
            }
        }
        return codes;
    }

    public int taoMaGiamGia(String code, BigDecimal amount, int limit, LocalDate start, LocalDate end,
                      BigDecimal minimumTotal, String description) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            int id = JdbcHelper.nextValue(connection, "SEQ_MaGiamGia");
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO MaGiamGia
                        (MaMaGiamGia, Code, SoTienGiam, GioiHanSuDung, LuotDung, NgayBatDau, NgayHetHan,
                         TongGiaToiThieu, TrangThai, MoTa)
                    VALUES (?, ?, ?, ?, 0, ?, ?, ?, 'Đang hiệu lực', ?)
                    """)) {
                statement.setInt(1, id);
                statement.setString(2, code);
                statement.setBigDecimal(3, amount);
                statement.setInt(4, limit);
                statement.setDate(5, Date.valueOf(start));
                statement.setDate(6, Date.valueOf(end));
                statement.setBigDecimal(7, minimumTotal);
                statement.setString(8, description);
                statement.executeUpdate();
            }
            return id;
        }
    }

    public void capNhatMaGiamGia(int id, BigDecimal amount, int limit, LocalDate start, LocalDate end,
                       BigDecimal minimumTotal, String status, String description) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE MaGiamGia
                     SET SoTienGiam = ?, GioiHanSuDung = ?, NgayBatDau = ?, NgayHetHan = ?,
                         TongGiaToiThieu = ?, TrangThai = ?, MoTa = ?
                     WHERE MaMaGiamGia = ?
                     """)) {
            statement.setBigDecimal(1, amount);
            statement.setInt(2, limit);
            statement.setDate(3, Date.valueOf(start));
            statement.setDate(4, Date.valueOf(end));
            statement.setBigDecimal(5, minimumTotal);
            statement.setString(6, status);
            statement.setString(7, description);
            statement.setInt(8, id);
            statement.executeUpdate();
        }
    }
}






