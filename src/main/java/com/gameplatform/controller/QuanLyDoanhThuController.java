package com.gameplatform.controller;

import com.gameplatform.database.Database;
import com.gameplatform.model.DongBaoCaoDoanhThu;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class QuanLyDoanhThuController {
    public List<DongBaoCaoDoanhThu> doanhThuNenTang(LocalDate fromDate, LocalDate toDate) throws SQLException {
        return queryRevenue(null, fromDate, toDate);
    }

    public List<DongBaoCaoDoanhThu> doanhThuNhaPhatTrien(int developerId, LocalDate fromDate, LocalDate toDate) throws SQLException {
        return queryRevenue(developerId, fromDate, toDate);
    }

    public BigDecimal developerTotal(int developerId, LocalDate fromDate, LocalDate toDate) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT SF_TinhDoanhThuNPT(?, ?, ?) AS TotalRevenue
                     FROM dual
                     """)) {
            statement.setInt(1, developerId);
            statement.setDate(2, Date.valueOf(fromDate));
            statement.setDate(3, Date.valueOf(toDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getBigDecimal("TotalRevenue");
            }
        }
    }

    public Path xuatCsv(String filePrefix, List<DongBaoCaoDoanhThu> rows) throws IOException {
        Path directory = Path.of("baoCao");
        Files.createDirectories(directory);
        Path file = directory.resolve(filePrefix + System.currentTimeMillis() + ".csv");
        StringBuilder builder = new StringBuilder();
        builder.append('\uFEFF');
        builder.append("MaGame,TenGame,NhaPhatTrien,SoLuongBan,DoanhThuGoc,DoanhThuNPT,DoanhThuNenTang\n");
        for (DongBaoCaoDoanhThu row : rows) {
            builder.append(row.gameId()).append(',')
                    .append(csv(row.tenGame())).append(',')
                    .append(csv(row.tenNhaPhatTrien())).append(',')
                    .append(row.soLuongBan()).append(',')
                    .append(row.doanhThuGoc()).append(',')
                    .append(row.doanhThuNhaPhatTrien()).append(',')
                    .append(row.doanhThuNenTang()).append('\n');
        }
        Files.writeString(file, builder.toString(), StandardCharsets.UTF_8);
        return file.toAbsolutePath();
    }

    private List<DongBaoCaoDoanhThu> queryRevenue(Integer developerId, LocalDate fromDate, LocalDate toDate) throws SQLException {
        String sql = """
                SELECT g.MaGame,
                       g.TenGame,
                       npt.TenNPT,
                       COUNT(*) AS SoLuongBan,
                       NVL(SUM(ct.GiaBan), 0) AS DoanhThuGoc,
                       NVL(SUM(ct.GiaBan * npt.TyLeChiaSe), 0) AS DoanhThuNPT,
                       NVL(SUM(ct.GiaBan * (1 - npt.TyLeChiaSe)), 0) AS DoanhThuNenTang
                FROM GiaoDich gd
                JOIN ChiTietGiaoDich ct ON ct.MaGD = gd.MaGD
                JOIN Game g ON g.MaGame = ct.MaGame
                JOIN NhaPhatTrien npt ON npt.MaNPT = g.MaNPT
                WHERE gd.TrangThai = 'Thành công'
                  AND TRUNC(gd.NgayGD) BETWEEN ? AND ?
                  AND (? IS NULL OR g.MaNPT = ?)
                GROUP BY g.MaGame, g.TenGame, npt.TenNPT
                ORDER BY DoanhThuGoc DESC
                """;
        List<DongBaoCaoDoanhThu> rows = new ArrayList<>();
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDate(1, Date.valueOf(fromDate));
                statement.setDate(2, Date.valueOf(toDate));
                if (developerId == null) {
                    statement.setNull(3, java.sql.Types.NUMERIC);
                    statement.setNull(4, java.sql.Types.NUMERIC);
                } else {
                    statement.setInt(3, developerId);
                    statement.setInt(4, developerId);
                }
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        rows.add(new DongBaoCaoDoanhThu(
                                resultSet.getInt("MaGame"),
                                resultSet.getString("TenGame"),
                                resultSet.getString("TenNPT"),
                                resultSet.getInt("SoLuongBan"),
                                resultSet.getBigDecimal("DoanhThuGoc"),
                                resultSet.getBigDecimal("DoanhThuNPT"),
                                resultSet.getBigDecimal("DoanhThuNenTang")
                        ));
                    }
                }
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
        return rows;
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}






