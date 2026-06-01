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

    private static final int MUC_CO_LAP_XU_LY_DONG_THOI = Connection.TRANSACTION_SERIALIZABLE;

    private static final int DEMO_DO_TRE_LOST_UPDATE_GIAY = 3;

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
        xuLyYeuCauPhatHanh(requestId, employeeId, "Đã duyệt", null);
    }

    public void reject(int requestId, int employeeId, String reason) throws SQLException {
        xuLyYeuCauPhatHanh(requestId, employeeId, "Từ chối", reason);
    }

    private void xuLyYeuCauPhatHanh(int requestId, int employeeId, String status, String rejectReason) throws SQLException {
        if (MUC_CO_LAP_XU_LY_DONG_THOI == Connection.TRANSACTION_SERIALIZABLE) {
            xuLyYeuCauPhatHanhAnToan(requestId, employeeId, status, rejectReason);
        } else {
            xuLyYeuCauPhatHanhBanKhongAnToan(requestId, employeeId, status, rejectReason);
        }
    }

    private void xuLyYeuCauPhatHanhAnToan(int requestId, int employeeId, String status, String rejectReason) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(MUC_CO_LAP_XU_LY_DONG_THOI);
            try (CallableStatement statement = connection.prepareCall("{call SP_XuLyYeuCauPhatHanh(?, ?, ?, ?)}")) {
                docTrangThaiVaChoDemo(connection, requestId);
                statement.setInt(1, requestId);
                statement.setInt(2, employeeId);
                statement.setString(3, status);
                statement.setString(4, rejectReason);
                statement.execute();
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw friendlyLostUpdateException(exception);
            }
        }
    }

    private void xuLyYeuCauPhatHanhBanKhongAnToan(int requestId, int employeeId, String status, String rejectReason) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(MUC_CO_LAP_XU_LY_DONG_THOI);
            try (PreparedStatement statement = connection.prepareStatement("""
                     UPDATE YeuCauPhatHanh
                     SET TrangThai = ?,
                         MaNVXuLy = ?,
                         LyDoTuChoi = ?,
                         NgayXuLy = SYSDATE
                     WHERE MaYeuCau = ?
                     """)) {
                docTrangThaiVaChoDemo(connection, requestId);
                statement.setString(1, status);
                statement.setInt(2, employeeId);
                statement.setString(3, "Từ chối".equals(status) ? rejectReason : null);
                statement.setInt(4, requestId);
                statement.executeUpdate();
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    private void docTrangThaiVaChoDemo(Connection connection, int requestId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT TrangThai
                FROM YeuCauPhatHanh
                WHERE MaYeuCau = ?
                """)) {
            statement.setInt(1, requestId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    resultSet.getString("TrangThai");
                }
            }
        }
        if (DEMO_DO_TRE_LOST_UPDATE_GIAY > 0) {
            try {
                Thread.sleep(DEMO_DO_TRE_LOST_UPDATE_GIAY * 1000L);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new SQLException("Demo Lost Update bị ngắt trong lúc chờ.", exception);
            }
        }
    }

    private SQLException friendlyLostUpdateException(SQLException exception) {
        String message = exception.getMessage() == null ? "" : exception.getMessage();
        if (exception.getErrorCode() == 8177 || message.contains("ORA-08177") || message.contains("-20104")) {
            return new SQLException("Yêu cầu phát hành đã bị phiên khác xử lý trước. Hãy tải lại danh sách để tránh Lost Update.", exception);
        }
        return exception;
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






