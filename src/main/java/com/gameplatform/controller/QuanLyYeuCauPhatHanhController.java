package com.gameplatform.controller;

import com.gameplatform.database.Database;
import com.gameplatform.model.YeuCauPhatHanh;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class QuanLyYeuCauPhatHanhController {
    private static final String REQUEST_SELECT = """
            SELECT yc.MaYeuCau,
                   yc.MaNPT,
                   npt.TenNPT,
                   yc.MaGame,
                   g.TenGame,
                   yc.MaPhienBan,
                   pb.TenPhienBan,
                   yc.NgayYeuCau,
                   yc.TrangThai,
                   yc.MaNVXuLy,
                   nv.HoTen AS TenNVXuLy,
                   yc.LyDoTuChoi,
                   yc.NgayXuLy
            FROM YeuCauPhatHanh yc
            JOIN NhaPhatTrien npt ON npt.MaNPT = yc.MaNPT
            JOIN Game g ON g.MaGame = yc.MaGame
            LEFT JOIN PhienBanGame pb ON pb.MaPhienBan = yc.MaPhienBan
            LEFT JOIN NhanVien nv ON nv.MaNV = yc.MaNVXuLy
            """;

    public List<YeuCauPhatHanh> findPending() throws SQLException {
        return query(REQUEST_SELECT + " WHERE yc.TrangThai = 'Chờ duyệt' ORDER BY yc.NgayYeuCau");
    }

    public List<YeuCauPhatHanh> findByDeveloper(int developerId) throws SQLException {
        String sql = REQUEST_SELECT + " WHERE yc.MaNPT = ? ORDER BY yc.MaYeuCau DESC";
        List<YeuCauPhatHanh> requests = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, developerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(map(resultSet));
                }
            }
        }
        return requests;
    }

    public List<YeuCauPhatHanh> findAll() throws SQLException {
        return query(REQUEST_SELECT + " ORDER BY yc.MaYeuCau DESC");
    }

    public void approve(int requestId, int employeeId) throws SQLException {
        try (Connection connection = Database.getConnection();
             CallableStatement statement = connection.prepareCall("{call SP_XuLyYeuCauPhatHanh(?, ?, ?, ?)}")) {
            statement.setInt(1, requestId);
            statement.setInt(2, employeeId);
            statement.setString(3, "Đã duyệt");
            statement.setString(4, null);
            statement.execute();
        }
    }

    public void reject(int requestId, int employeeId, String reason) throws SQLException {
        try (Connection connection = Database.getConnection();
             CallableStatement statement = connection.prepareCall("{call SP_XuLyYeuCauPhatHanh(?, ?, ?, ?)}")) {
            statement.setInt(1, requestId);
            statement.setInt(2, employeeId);
            statement.setString(3, "Từ chối");
            statement.setString(4, reason);
            statement.execute();
        }
    }

    public void approveLegacy(int requestId, int employeeId) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE YeuCauPhatHanh
                     SET TrangThai = 'Đã duyệt',
                         MaNVXuLy = ?,
                         LyDoTuChoi = NULL,
                         NgayXuLy = SYSDATE
                     WHERE MaYeuCau = ?
                       AND TrangThai = 'Chờ duyệt'
                     """)) {
            statement.setInt(1, employeeId);
            statement.setInt(2, requestId);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Yêu cầu không còn ở trạng thái chờ duyệt.");
            }
        }
    }

    public void rejectLegacy(int requestId, int employeeId, String reason) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE YeuCauPhatHanh
                     SET TrangThai = 'Từ chối',
                         MaNVXuLy = ?,
                         LyDoTuChoi = ?,
                         NgayXuLy = SYSDATE
                     WHERE MaYeuCau = ?
                       AND TrangThai = 'Chờ duyệt'
                     """)) {
            statement.setInt(1, employeeId);
            statement.setString(2, reason);
            statement.setInt(3, requestId);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Yêu cầu không còn ở trạng thái chờ duyệt.");
            }
        }
    }

    private List<YeuCauPhatHanh> query(String sql) throws SQLException {
        List<YeuCauPhatHanh> requests = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                requests.add(map(resultSet));
            }
        }
        return requests;
    }

    private YeuCauPhatHanh map(ResultSet resultSet) throws SQLException {
        int handledBy = resultSet.getInt("MaNVXuLy");
        int versionId = resultSet.getInt("MaPhienBan");
        return new YeuCauPhatHanh(
                resultSet.getInt("MaYeuCau"),
                resultSet.getInt("MaNPT"),
                resultSet.getString("TenNPT"),
                resultSet.getInt("MaGame"),
                resultSet.getString("TenGame"),
                resultSet.wasNull() ? null : versionId,
                resultSet.getString("TenPhienBan"),
                JdbcHelper.localDateTime(resultSet, "NgayYeuCau"),
                resultSet.getString("TrangThai"),
                handledBy == 0 ? null : handledBy,
                resultSet.getString("TenNVXuLy"),
                resultSet.getString("LyDoTuChoi"),
                JdbcHelper.localDateTime(resultSet, "NgayXuLy")
        );
    }
}





