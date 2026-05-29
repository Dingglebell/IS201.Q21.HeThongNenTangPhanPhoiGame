package com.gameplatform.controller;

import com.gameplatform.database.Database;
import com.gameplatform.model.LoaiTaiKhoan;
import com.gameplatform.model.VaiTroNhanVien;
import com.gameplatform.model.TaiKhoanDangNhap;
import com.gameplatform.model.VietnameseText;
import com.gameplatform.service.AuthException;
import com.gameplatform.service.PasswordHasher;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Optional;

public final class XacThucTaiKhoanController {
    private static final String FIND_USER_SQL = """
            SELECT tk.MaTaiKhoan,
                   tk.TenDangNhap,
                   tk.LoaiTaiKhoan,
                   tk.TrangThai,
                   nv.MaNV,
                   nv.HoTen,
                   nv.VaiTro,
                   npt.MaNPT,
                   npt.TenNPT,
                   nc.MaNguoiChoi,
                   nc.TenHienThi
            FROM TaiKhoan tk
            LEFT JOIN NhanVien nv ON nv.MaTaiKhoan = tk.MaTaiKhoan
            LEFT JOIN NhaPhatTrien npt ON npt.MaTaiKhoan = tk.MaTaiKhoan
            LEFT JOIN NguoiChoi nc ON nc.MaTaiKhoan = tk.MaTaiKhoan
            WHERE tk.TenDangNhap = ?
              AND tk.MatKhau = ?
            """;

    public boolean canConnectDatabase() {
        return Database.canConnect();
    }

    public TaiKhoanDangNhap login(String username, String password) throws SQLException, AuthException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new AuthException("Vui lòng nhập tên đăng nhập và mật khẩu.");
        }
        TaiKhoanDangNhap user = findByCredentials(username.trim(), PasswordHasher.sha256(password))
                .orElseThrow(() -> new AuthException("Sai tên đăng nhập hoặc mật khẩu."));
        if (VietnameseText.equalsDbText("Bị khóa", user.status())
                || VietnameseText.equalsDbText("Ngưng hoạt động", user.status())) {
            throw new AuthException("Tài khoản không ở trạng thái đang hoạt động.");
        }
        if (user.profileId() == null || user.profileId() == 0) {
            throw new AuthException("Tài khoản chưa liên kết đúng hồ sơ nghiệp vụ.");
        }
        return user;
    }

    public int registerPlayer(String username, String password, String displayName, LocalDate birthDate,
                              String email, String phone, String country) throws SQLException, AuthException {
        validateRequired(username, "Tên đăng nhập");
        validateRequired(password, "Mật khẩu");
        validateRequired(displayName, "Tên hiển thị");
        validateRequired(email, "Email");
        if (birthDate == null) {
            throw new AuthException("Vui lòng chọn ngày sinh.");
        }
        ensureUsernameAvailable(username);
        ensureEmailAvailable("NguoiChoi", email);
        try (Connection connection = Database.getConnection();
             CallableStatement statement = connection.prepareCall("{call SP_DangKyNguoiChoi(?, ?, ?, ?, ?, ?, ?, ?)}")) {
            statement.setString(1, username.trim());
            statement.setString(2, PasswordHasher.sha256(password));
            statement.setString(3, displayName.trim());
            statement.setDate(4, Date.valueOf(birthDate));
            statement.setString(5, email.trim());
            statement.setString(6, cleanOptional(phone));
            statement.setString(7, cleanOptional(country));
            statement.registerOutParameter(8, Types.NUMERIC);
            statement.execute();
            return statement.getInt(8);
        } catch (SQLException exception) {
            throw friendlyRegistrationException(exception);
        }
    }

    public int registerDeveloper(String username, String password, String developerName, String developerType,
                                 String email, String phone, String address) throws SQLException, AuthException {
        validateRequired(username, "Tên đăng nhập");
        validateRequired(password, "Mật khẩu");
        validateRequired(developerName, "Tên nhà phát triển");
        validateRequired(developerType, "Loại nhà phát triển");
        validateRequired(email, "Email");
        ensureUsernameAvailable(username);
        ensureEmailAvailable("NhaPhatTrien", email);
        try (Connection connection = Database.getConnection();
             CallableStatement statement = connection.prepareCall("{call SP_DangKyNhaPhatTrien(?, ?, ?, ?, ?, ?, ?, ?)}")) {
            statement.setString(1, username.trim());
            statement.setString(2, PasswordHasher.sha256(password));
            statement.setString(3, developerName.trim());
            statement.setString(4, developerType.trim());
            statement.setString(5, email.trim());
            statement.setString(6, cleanOptional(phone));
            statement.setString(7, cleanOptional(address));
            statement.registerOutParameter(8, Types.NUMERIC);
            statement.execute();
            return statement.getInt(8);
        } catch (SQLException exception) {
            throw friendlyRegistrationException(exception);
        }
    }

    private void validateRequired(String value, String fieldName) throws AuthException {
        if (value == null || value.isBlank()) {
            throw new AuthException("Vui lòng nhập " + fieldName + ".");
        }
    }

    private String cleanOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void ensureUsernameAvailable(String username) throws SQLException, AuthException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT COUNT(*) AS Total
                     FROM TaiKhoan
                     WHERE LOWER(TenDangNhap) = LOWER(?)
                     """)) {
            statement.setString(1, username.trim());
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                if (resultSet.getInt("Total") > 0) {
                    throw new AuthException("Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.");
                }
            }
        }
    }

    private void ensureEmailAvailable(String tableName, String email) throws SQLException, AuthException {
        String sql = "SELECT COUNT(*) AS Total FROM " + tableName + " WHERE LOWER(Email) = LOWER(?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email.trim());
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                if (resultSet.getInt("Total") > 0) {
                    throw new AuthException("Email đã được sử dụng. Vui lòng nhập email khác.");
                }
            }
        }
    }

    private SQLException friendlyRegistrationException(SQLException exception) {
        String message = exception.getMessage();
        if (exception.getErrorCode() == 1 && message != null) {
            if (message.contains("UQ_TAIKHOAN_TENDANGNHAP")) {
                return new SQLException("Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.", exception);
            }
            if (message.contains("UQ_NGUOICHOI_EMAIL") || message.contains("UQ_NHAPHATTRIEN_EMAIL")) {
                return new SQLException("Email đã được sử dụng. Vui lòng nhập email khác.", exception);
            }
        }
        return exception;
    }

    private Optional<TaiKhoanDangNhap> findByCredentials(String username, String passwordHash) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_USER_SQL)) {
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                LoaiTaiKhoan accountType = LoaiTaiKhoan.fromDbValue(resultSet.getString("LoaiTaiKhoan"));
                Integer profileId = switch (accountType) {
                    case NGUOI_CHOI -> resultSet.getInt("MaNguoiChoi");
                    case NHA_PHAT_TRIEN -> resultSet.getInt("MaNPT");
                    case NHAN_VIEN -> resultSet.getInt("MaNV");
                };
                String displayName = switch (accountType) {
                    case NGUOI_CHOI -> resultSet.getString("TenHienThi");
                    case NHA_PHAT_TRIEN -> resultSet.getString("TenNPT");
                    case NHAN_VIEN -> resultSet.getString("HoTen");
                };
                VaiTroNhanVien employeeRole = null;
                if (accountType == LoaiTaiKhoan.NHAN_VIEN) {
                    employeeRole = VaiTroNhanVien.fromDbValue(resultSet.getString("VaiTro"));
                }
                return Optional.of(new TaiKhoanDangNhap(
                        resultSet.getInt("MaTaiKhoan"),
                        resultSet.getString("TenDangNhap"),
                        accountType,
                        resultSet.getString("TrangThai"),
                        profileId,
                        displayName,
                        employeeRole
                ));
            }
        }
    }
}







