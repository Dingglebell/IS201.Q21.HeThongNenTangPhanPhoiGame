package com.gameplatform.controller;

import com.gameplatform.database.Database;
import com.gameplatform.model.TheLoaiGame;
import com.gameplatform.model.MediaGame;
import com.gameplatform.model.PhienBanGame;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public final class QuanLyDanhMucGameController {
    public List<TheLoaiGame> findCategories() throws SQLException {
        List<TheLoaiGame> categories = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT MaTheLoai, TenTheLoai, MoTa
                     FROM TheLoai
                     ORDER BY TenTheLoai
                     """);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                categories.add(new TheLoaiGame(
                        resultSet.getInt("MaTheLoai"),
                        resultSet.getString("TenTheLoai"),
                        resultSet.getString("MoTa")
                ));
            }
        }
        return categories;
    }

    public int createCategory(String name, String description) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            int id = JdbcHelper.nextValue(connection, "SEQ_TheLoai");
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO TheLoai (MaTheLoai, TenTheLoai, MoTa)
                    VALUES (?, ?, ?)
                    """)) {
                statement.setInt(1, id);
                statement.setString(2, name);
                statement.setString(3, description);
                statement.executeUpdate();
            }
            return id;
        }
    }

    public void updateCategory(int categoryId, String name, String description) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE TheLoai
                     SET TenTheLoai = ?, MoTa = ?
                     WHERE MaTheLoai = ?
                     """)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, categoryId);
            statement.executeUpdate();
        }
    }

    public void assignCategory(int gameId, int categoryId) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     MERGE INTO DanhMucTheLoai dm
                     USING (SELECT ? AS MaTheLoai, ? AS MaGame FROM dual) src
                     ON (dm.MaTheLoai = src.MaTheLoai AND dm.MaGame = src.MaGame)
                     WHEN NOT MATCHED THEN
                         INSERT (MaTheLoai, MaGame)
                         VALUES (src.MaTheLoai, src.MaGame)
                     """)) {
            statement.setInt(1, categoryId);
            statement.setInt(2, gameId);
            statement.executeUpdate();
        }
    }

    public List<MediaGame> findMedia(Integer developerId) throws SQLException {
        String sql = """
                SELECT gm.MaMedia, gm.MaGame, g.TenGame, gm.LoaiMedia, gm.FileMedia
                FROM GameMedia gm
                JOIN Game g ON g.MaGame = gm.MaGame
                WHERE ? IS NULL OR g.MaNPT = ?
                ORDER BY gm.MaMedia DESC
                """;
        List<MediaGame> media = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (developerId == null) {
                statement.setNull(1, Types.NUMERIC);
                statement.setNull(2, Types.NUMERIC);
            } else {
                statement.setInt(1, developerId);
                statement.setInt(2, developerId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    media.add(new MediaGame(
                            resultSet.getInt("MaMedia"),
                            resultSet.getInt("MaGame"),
                            resultSet.getString("TenGame"),
                            resultSet.getString("LoaiMedia"),
                            resultSet.getString("FileMedia")
                    ));
                }
            }
        }
        return media;
    }

    public List<MediaGame> findMediaByGame(int gameId) throws SQLException {
        String sql = """
                SELECT gm.MaMedia, gm.MaGame, g.TenGame, gm.LoaiMedia, gm.FileMedia
                FROM GameMedia gm
                JOIN Game g ON g.MaGame = gm.MaGame
                WHERE gm.MaGame = ?
                ORDER BY gm.MaMedia
                """;
        List<MediaGame> media = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gameId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    media.add(new MediaGame(
                            resultSet.getInt("MaMedia"),
                            resultSet.getInt("MaGame"),
                            resultSet.getString("TenGame"),
                            resultSet.getString("LoaiMedia"),
                            resultSet.getString("FileMedia")
                    ));
                }
            }
        }
        return media;
    }

    public int addMedia(int gameId, String mediaType, String fileMedia) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            int id = JdbcHelper.nextValue(connection, "SEQ_GameMedia");
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO GameMedia (MaMedia, MaGame, LoaiMedia, FileMedia)
                    VALUES (?, ?, ?, ?)
                    """)) {
                statement.setInt(1, id);
                statement.setInt(2, gameId);
                statement.setString(3, mediaType);
                statement.setString(4, fileMedia);
                statement.executeUpdate();
            }
            return id;
        }
    }

    public void updateMedia(int mediaId, String mediaType, String fileMedia) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE GameMedia
                     SET LoaiMedia = ?, FileMedia = ?
                     WHERE MaMedia = ?
                     """)) {
            statement.setString(1, mediaType);
            statement.setString(2, fileMedia);
            statement.setInt(3, mediaId);
            statement.executeUpdate();
        }
    }

    public void deleteMedia(int mediaId) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     DELETE FROM GameMedia
                     WHERE MaMedia = ?
                     """)) {
            statement.setInt(1, mediaId);
            statement.executeUpdate();
        }
    }

    public List<PhienBanGame> findVersions(Integer developerId) throws SQLException {
        String sql = """
                SELECT pb.MaPhienBan, pb.MaGame, g.TenGame, pb.TenPhienBan, pb.NoiDungPhienBan,
                       pb.FilePhienBan, pb.DungLuong, pb.NgayTao, pb.TrangThai
                FROM PhienBanGame pb
                JOIN Game g ON g.MaGame = pb.MaGame
                WHERE ? IS NULL OR g.MaNPT = ?
                ORDER BY pb.NgayTao DESC, pb.MaPhienBan DESC
                """;
        List<PhienBanGame> versions = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (developerId == null) {
                statement.setNull(1, Types.NUMERIC);
                statement.setNull(2, Types.NUMERIC);
            } else {
                statement.setInt(1, developerId);
                statement.setInt(2, developerId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    versions.add(new PhienBanGame(
                            resultSet.getInt("MaPhienBan"),
                            resultSet.getInt("MaGame"),
                            resultSet.getString("TenGame"),
                            resultSet.getString("TenPhienBan"),
                            resultSet.getString("NoiDungPhienBan"),
                            resultSet.getString("FilePhienBan"),
                            resultSet.getBigDecimal("DungLuong"),
                            JdbcHelper.localDate(resultSet, "NgayTao"),
                            resultSet.getString("TrangThai")
                    ));
                }
            }
        }
        return versions;
    }

    public List<PhienBanGame> findVersionsByGame(int gameId) throws SQLException {
        String sql = """
                SELECT pb.MaPhienBan, pb.MaGame, g.TenGame, pb.TenPhienBan, pb.NoiDungPhienBan,
                       pb.FilePhienBan, pb.DungLuong, pb.NgayTao, pb.TrangThai
                FROM PhienBanGame pb
                JOIN Game g ON g.MaGame = pb.MaGame
                WHERE pb.MaGame = ?
                ORDER BY pb.NgayTao DESC, pb.MaPhienBan DESC
                """;
        List<PhienBanGame> versions = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gameId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    versions.add(new PhienBanGame(
                            resultSet.getInt("MaPhienBan"),
                            resultSet.getInt("MaGame"),
                            resultSet.getString("TenGame"),
                            resultSet.getString("TenPhienBan"),
                            resultSet.getString("NoiDungPhienBan"),
                            resultSet.getString("FilePhienBan"),
                            resultSet.getBigDecimal("DungLuong"),
                            JdbcHelper.localDate(resultSet, "NgayTao"),
                            resultSet.getString("TrangThai")
                    ));
                }
            }
        }
        return versions;
    }

    public int addVersionAndReleaseRequest(int developerId, int gameId, String versionName, String content,
                                           String fileVersion, BigDecimal sizeMb) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int versionId = JdbcHelper.nextValue(connection, "SEQ_PhienBanGame");
                try (PreparedStatement version = connection.prepareStatement("""
                        INSERT INTO PhienBanGame
                            (MaPhienBan, MaGame, TenPhienBan, NoiDungPhienBan, FilePhienBan, DungLuong, TrangThai)
                        SELECT ?, MaGame, ?, ?, ?, ?, 'Chưa phát hành'
                        FROM Game
                        WHERE MaGame = ?
                          AND MaNPT = ?
                        """)) {
                    version.setInt(1, versionId);
                    version.setString(2, versionName);
                    version.setString(3, content);
                    version.setString(4, fileVersion);
                    version.setBigDecimal(5, sizeMb);
                    version.setInt(6, gameId);
                    version.setInt(7, developerId);
                    if (version.executeUpdate() == 0) {
                        throw new SQLException("Game không thuộc nhà phát triển đang đăng nhập.");
                    }
                }

                int requestId;
                try (CallableStatement request = connection.prepareCall("{call SP_TaoYeuCauPhatHanh(?, ?, ?, ?)}")) {
                    request.setInt(1, developerId);
                    request.setInt(2, gameId);
                    request.setInt(3, versionId);
                    request.registerOutParameter(4, Types.NUMERIC);
                    request.execute();
                    requestId = request.getInt(4);
                }
                connection.commit();
                return requestId;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void updateDeveloperGame(int developerId, int gameId, int ageRating,
                                    BigDecimal price, String description) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE Game
                     SET DoTuoi = ?, GiaGoc = ?, MoTa = ?
                     WHERE MaGame = ?
                       AND MaNPT = ?
                     """)) {
            statement.setInt(1, ageRating);
            statement.setBigDecimal(2, price);
            statement.setString(3, description);
            statement.setInt(4, gameId);
            statement.setInt(5, developerId);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Không cập nhật được game hoặc game không thuộc nhà phát triển.");
            }
        }
    }
    public void unpublishGame(int gameId) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE Game
                     SET TrangThai = 'Đã gỡ bỏ',
                         NgayPhatHanh = NULL
                     WHERE MaGame = ?
                     """)) {
            statement.setInt(1, gameId);
            statement.executeUpdate();
        }
    }
}








