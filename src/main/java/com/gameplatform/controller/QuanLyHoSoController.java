package com.gameplatform.controller;

import com.gameplatform.database.Database;
import com.gameplatform.model.LoaiTaiKhoan;
import com.gameplatform.model.ThongTinHoSo;
import com.gameplatform.service.PasswordHasher;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

public final class QuanLyHoSoController {
    public ThongTinHoSo loadProfile(LoaiTaiKhoan accountType, int profileId) throws SQLException {
        String sql = switch (accountType) {
            case NGUOI_CHOI -> """
                    SELECT TenHienThi AS DisplayName, Email, SDT, 'Quốc gia' AS ExtraLabel, QuocGia AS ExtraValue
                    FROM NguoiChoi
                    WHERE MaNguoiChoi = ?
                    """;
            case NHA_PHAT_TRIEN -> """
                    SELECT TenNPT AS DisplayName, Email, SDT, 'Địa chỉ' AS ExtraLabel, DiaChi AS ExtraValue
                    FROM NhaPhatTrien
                    WHERE MaNPT = ?
                    """;
            case NHAN_VIEN -> """
                    SELECT HoTen AS DisplayName, Email, SDT, 'Vai trò' AS ExtraLabel, VaiTro AS ExtraValue
                    FROM NhanVien
                    WHERE MaNV = ?
                    """;
        };
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, profileId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new ThongTinHoSo(
                            resultSet.getString("DisplayName"),
                            resultSet.getString("Email"),
                            resultSet.getString("SDT"),
                            resultSet.getString("ExtraLabel"),
                            resultSet.getString("ExtraValue")
                    );
                }
            }
        }
        throw new SQLException("Không tìm thấy hồ sơ tài khoản.");
    }

    public void updatePlayerProfile(int playerId, String displayName, String email, String phone, String country) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE NguoiChoi
                     SET TenHienThi = ?, Email = ?, SDT = ?, QuocGia = ?
                     WHERE MaNguoiChoi = ?
                     """)) {
            statement.setString(1, displayName);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setString(4, country);
            statement.setInt(5, playerId);
            statement.executeUpdate();
        }
    }

    public void updateDeveloperProfile(int developerId, String name, String email, String phone, String address) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE NhaPhatTrien
                     SET TenNPT = ?, Email = ?, SDT = ?, DiaChi = ?
                     WHERE MaNPT = ?
                     """)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setString(4, address);
            statement.setInt(5, developerId);
            statement.executeUpdate();
        }
    }

    public void updateEmployeeProfile(int employeeId, String fullName, String email, String phone) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE NhanVien
                     SET HoTen = ?, Email = ?, SDT = ?
                     WHERE MaNV = ?
                     """)) {
            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setInt(4, employeeId);
            statement.executeUpdate();
        }
    }

    public void changePassword(int accountId, String currentPassword, String newPassword) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            try (PreparedStatement check = connection.prepareStatement("""
                    SELECT COUNT(*)
                    FROM TaiKhoan
                    WHERE MaTaiKhoan = ?
                      AND MatKhau = ?
                    """)) {
                check.setInt(1, accountId);
                check.setString(2, PasswordHasher.sha256(currentPassword));
                try (ResultSet resultSet = check.executeQuery()) {
                    resultSet.next();
                    if (resultSet.getInt(1) == 0) {
                        throw new SQLException("Mật khẩu hiện tại không đúng.");
                    }
                }
            }
            try (PreparedStatement update = connection.prepareStatement("""
                    UPDATE TaiKhoan
                    SET MatKhau = ?
                    WHERE MaTaiKhoan = ?
                    """)) {
                update.setString(1, PasswordHasher.sha256(newPassword));
                update.setInt(2, accountId);
                update.executeUpdate();
            }
        }
    }

    public int registerPlayer(String username, String password, String displayName, LocalDate birthDate,
                              String email, String phone, String country) throws SQLException {
        try (Connection connection = Database.getConnection();
             CallableStatement statement = connection.prepareCall("{call SP_DangKyNguoiChoi(?, ?, ?, ?, ?, ?, ?, ?)}")) {
            statement.setString(1, username);
            statement.setString(2, PasswordHasher.sha256(password));
            statement.setString(3, displayName);
            statement.setDate(4, Date.valueOf(birthDate));
            statement.setString(5, email);
            statement.setString(6, phone);
            statement.setString(7, country);
            statement.registerOutParameter(8, Types.NUMERIC);
            statement.execute();
            return statement.getInt(8);
        }
    }

    public int registerDeveloper(String username, String password, String developerName, String developerType,
                                 String email, String phone, String address) throws SQLException {
        try (Connection connection = Database.getConnection();
             CallableStatement statement = connection.prepareCall("{call SP_DangKyNhaPhatTrien(?, ?, ?, ?, ?, ?, ?, ?)}")) {
            statement.setString(1, username);
            statement.setString(2, PasswordHasher.sha256(password));
            statement.setString(3, developerName);
            statement.setString(4, developerType);
            statement.setString(5, email);
            statement.setString(6, phone);
            statement.setString(7, address);
            statement.registerOutParameter(8, Types.NUMERIC);
            statement.execute();
            return statement.getInt(8);
        }
    }
}






