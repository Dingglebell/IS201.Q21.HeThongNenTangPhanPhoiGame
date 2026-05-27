package com.gameplatform.controller;

import com.gameplatform.database.Database;
import com.gameplatform.model.ThongTinTicket;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class QuanLyTicketHoTroController {
    private static final String TICKET_SELECT = """
            SELECT t.MaTicket,
                   t.LoaiYeuCau,
                   nc.TenHienThi,
                   g.TenGame,
                   TO_CHAR(t.MaGD) AS MaGDText,
                   t.NgayTao,
                   t.TrangThai,
                   nv.HoTen AS TenNVXuLy,
                   t.NoiDungPhanHoi,
                   t.NgayXuLy
            FROM Ticket t
            JOIN NguoiChoi nc ON nc.MaNguoiChoi = t.MaNguoiChoi
            LEFT JOIN Game g ON g.MaGame = t.MaGame
            LEFT JOIN NhanVien nv ON nv.MaNV = t.MaNVXuLy
            """;

    public List<ThongTinTicket> traCuuTatCaTicket() throws SQLException {
        return query(TICKET_SELECT + " ORDER BY CASE t.TrangThai WHEN 'Chờ xử lý' THEN 1 WHEN 'Đang xử lý' THEN 2 ELSE 3 END, t.NgayTao");
    }

    public List<ThongTinTicket> traCuuTicketTheoNguoiChoi(int playerId) throws SQLException {
        String sql = TICKET_SELECT + " WHERE t.MaNguoiChoi = ? ORDER BY t.NgayTao DESC";
        List<ThongTinTicket> tickets = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, playerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tickets.add(map(resultSet));
                }
            }
        }
        return tickets;
    }

    public int taoTicket(int playerId, String type, String content, Integer gameId, Integer transactionId) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            int ticketId = JdbcHelper.nextValue(connection, "SEQ_Ticket");
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO Ticket (MaTicket, LoaiYeuCau, NoiDung, MaNguoiChoi, MaGame, MaGD, TrangThai)
                    VALUES (?, ?, ?, ?, ?, ?, 'Chờ xử lý')
                    """)) {
                statement.setInt(1, ticketId);
                statement.setString(2, type);
                statement.setString(3, content);
                statement.setInt(4, playerId);
                if (gameId == null) {
                    statement.setNull(5, java.sql.Types.NUMERIC);
                } else {
                    statement.setInt(5, gameId);
                }
                if (transactionId == null) {
                    statement.setNull(6, java.sql.Types.NUMERIC);
                } else {
                    statement.setInt(6, transactionId);
                }
                statement.executeUpdate();
            }
            return ticketId;
        }
    }

    public void nhanXuLyTicket(int ticketId, int employeeId) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE Ticket
                     SET TrangThai = 'Đang xử lý',
                         MaNVXuLy = ?
                     WHERE MaTicket = ?
                       AND TrangThai = 'Chờ xử lý'
                     """)) {
            statement.setInt(1, employeeId);
            statement.setInt(2, ticketId);
            statement.executeUpdate();
        }
    }

    public void phanHoiTicket(int ticketId, int employeeId, String response) throws SQLException {
        try (Connection connection = Database.getConnection();
             CallableStatement statement = connection.prepareCall("{call SP_XuLyTicket(?, ?, ?)}")) {
            statement.setInt(1, ticketId);
            statement.setInt(2, employeeId);
            statement.setString(3, response);
            statement.execute();
        }
    }

    private List<ThongTinTicket> query(String sql) throws SQLException {
        List<ThongTinTicket> tickets = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                tickets.add(map(resultSet));
            }
        }
        return tickets;
    }

    private ThongTinTicket map(ResultSet resultSet) throws SQLException {
        return new ThongTinTicket(
                resultSet.getInt("MaTicket"),
                resultSet.getString("LoaiYeuCau"),
                resultSet.getString("TenHienThi"),
                resultSet.getString("TenGame"),
                resultSet.getString("MaGDText"),
                JdbcHelper.localDateTime(resultSet, "NgayTao"),
                resultSet.getString("TrangThai"),
                resultSet.getString("TenNVXuLy"),
                resultSet.getString("NoiDungPhanHoi"),
                JdbcHelper.localDateTime(resultSet, "NgayXuLy")
        );
    }
}






