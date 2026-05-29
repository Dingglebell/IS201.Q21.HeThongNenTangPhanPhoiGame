package com.gameplatform.controller;

import com.gameplatform.database.Database;
import com.gameplatform.model.VaiTroNhanVien;
import com.gameplatform.model.ThongTinNguoiDung;
import com.gameplatform.service.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class QuanLyNhanVienController {
    public List<ThongTinNguoiDung> traCuuNhanVien(String keyword) throws SQLException {
        String sql = """
                SELECT tk.MaTaiKhoan,
                       nv.MaNV AS MaHoSo,
                       tk.TenDangNhap,
                       tk.LoaiTaiKhoan,
                       tk.TrangThai,
                       nv.HoTen AS TenHienThi,
                       nv.VaiTro AS VaiTroLoai,
                       nv.Email,
                       nv.SDT,
                       CAST(NULL AS VARCHAR2(200)) AS DiaChi,
                       CAST(NULL AS NUMBER) AS TyLeChiaSe,
                       tk.NgayTao
                FROM NhanVien nv
                JOIN TaiKhoan tk ON tk.MaTaiKhoan = nv.MaTaiKhoan
                WHERE ? IS NULL
                   OR LOWER(tk.TenDangNhap) LIKE ?
                   OR LOWER(nv.HoTen) LIKE ?
                   OR LOWER(nv.Email) LIKE ?
                   OR TO_CHAR(nv.MaNV) = ?
                ORDER BY nv.MaNV
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

    public void updateVaiTroNhanVien(int employeeId, VaiTroNhanVien role) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE NhanVien
                     SET VaiTro = ?
                     WHERE MaNV = ?
                     """)) {
            statement.setString(1, role.dbValue());
            statement.setInt(2, employeeId);
            statement.executeUpdate();
        }
    }

    public int themNhanVien(String username, String password, String fullName, VaiTroNhanVien role, String email, String phone)
            throws SQLException {
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int accountId = JdbcHelper.nextValue(connection, "SEQ_TaiKhoan");
                int employeeId = JdbcHelper.nextValue(connection, "SEQ_NhanVien");

                try (PreparedStatement account = connection.prepareStatement("""
                        INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, TrangThai)
                        VALUES (?, ?, ?, 'Nhân viên', 'Đang hoạt động')
                        """)) {
                    account.setInt(1, accountId);
                    account.setString(2, username);
                    account.setString(3, PasswordHasher.sha256(password));
                    account.executeUpdate();
                }

                try (PreparedStatement employee = connection.prepareStatement("""
                        INSERT INTO NhanVien (MaNV, MaTaiKhoan, HoTen, VaiTro, Email, SDT)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """)) {
                    employee.setInt(1, employeeId);
                    employee.setInt(2, accountId);
                    employee.setString(3, fullName);
                    employee.setString(4, role.dbValue());
                    employee.setString(5, email);
                    employee.setString(6, phone);
                    employee.executeUpdate();
                }

                connection.commit();
                return employeeId;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private List<ThongTinNguoiDung> search(String sql, String keyword) throws SQLException {
        String normalized = keyword == null || keyword.isBlank() ? null : keyword.trim().toLowerCase();
        String likeKeyword = normalized == null ? null : "%" + normalized + "%";
        List<ThongTinNguoiDung> employees = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, normalized);
            statement.setString(2, likeKeyword);
            statement.setString(3, likeKeyword);
            statement.setString(4, likeKeyword);
            statement.setString(5, normalized);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    employees.add(new ThongTinNguoiDung(
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
        return employees;
    }
}





