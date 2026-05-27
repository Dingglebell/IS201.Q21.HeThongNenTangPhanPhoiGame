package com.gameplatform.controller;

import com.gameplatform.database.Database;
import com.gameplatform.model.ThongTinNguoiDung;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class QuanLyTaiKhoanController {
    public List<ThongTinNguoiDung> traCuuNguoiChoi(String keyword) throws SQLException {
        String sql = """
                SELECT tk.MaTaiKhoan,
                       nc.MaNguoiChoi AS MaHoSo,
                       tk.TenDangNhap,
                       tk.LoaiTaiKhoan,
                       tk.TrangThai,
                       nc.TenHienThi AS TenHienThi,
                       CAST(NULL AS VARCHAR2(50)) AS VaiTroLoai,
                       nc.Email,
                       nc.SDT,
                       nc.QuocGia AS DiaChi,
                       CAST(NULL AS NUMBER) AS TyLeChiaSe,
                       tk.NgayTao
                FROM NguoiChoi nc
                JOIN TaiKhoan tk ON tk.MaTaiKhoan = nc.MaTaiKhoan
                WHERE ? IS NULL
                   OR LOWER(tk.TenDangNhap) LIKE ?
                   OR LOWER(nc.TenHienThi) LIKE ?
                   OR LOWER(nc.Email) LIKE ?
                   OR TO_CHAR(nc.MaNguoiChoi) = ?
                ORDER BY nc.MaNguoiChoi
                """;
        return search(sql, keyword);
    }

    public List<ThongTinNguoiDung> traCuuNhaPhatTrien(String keyword) throws SQLException {
        String sql = """
                SELECT tk.MaTaiKhoan,
                       npt.MaNPT AS MaHoSo,
                       tk.TenDangNhap,
                       tk.LoaiTaiKhoan,
                       tk.TrangThai,
                       npt.TenNPT AS TenHienThi,
                       npt.LoaiNPT AS VaiTroLoai,
                       npt.Email,
                       npt.SDT,
                       npt.DiaChi,
                       npt.TyLeChiaSe,
                       tk.NgayTao
                FROM NhaPhatTrien npt
                JOIN TaiKhoan tk ON tk.MaTaiKhoan = npt.MaTaiKhoan
                WHERE ? IS NULL
                   OR LOWER(tk.TenDangNhap) LIKE ?
                   OR LOWER(npt.TenNPT) LIKE ?
                   OR LOWER(npt.Email) LIKE ?
                   OR TO_CHAR(npt.MaNPT) = ?
                ORDER BY npt.MaNPT
                """;
        return search(sql, keyword);
    }

    public void capNhatTrangThaiTaiKhoan(int accountId, String status) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE TaiKhoan
                     SET TrangThai = ?
                     WHERE MaTaiKhoan = ?
                     """)) {
            statement.setString(1, status);
            statement.setInt(2, accountId);
            statement.executeUpdate();
        }
    }

    public void capNhatTyLeChiaSeNhaPhatTrien(int developerId, BigDecimal revenueShare) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE NhaPhatTrien
                     SET TyLeChiaSe = ?
                     WHERE MaNPT = ?
                     """)) {
            statement.setBigDecimal(1, revenueShare);
            statement.setInt(2, developerId);
            statement.executeUpdate();
        }
    }

    private List<ThongTinNguoiDung> search(String sql, String keyword) throws SQLException {
        String normalized = keyword == null || keyword.isBlank() ? null : keyword.trim().toLowerCase();
        String likeKeyword = normalized == null ? null : "%" + normalized + "%";
        List<ThongTinNguoiDung> users = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, normalized);
            statement.setString(2, likeKeyword);
            statement.setString(3, likeKeyword);
            statement.setString(4, likeKeyword);
            statement.setString(5, normalized);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(new ThongTinNguoiDung(
                            resultSet.getInt("MaTaiKhoan"),
                            resultSet.getInt("MaHoSo"),
                            resultSet.getString("TenDangNhap"),
                            resultSet.getString("LoaiTaiKhoan"),
                            resultSet.getString("TrangThai"),
                            resultSet.getString("TenHienThi"),
                            resultSet.getString("VaiTroLoai"),
                            resultSet.getString("Email"),
                            resultSet.getString("SDT"),
                            resultSet.getString("DiaChi"),
                            resultSet.getBigDecimal("TyLeChiaSe"),
                            JdbcHelper.localDate(resultSet, "NgayTao")
                    ));
                }
            }
        }
        return users;
    }
}





