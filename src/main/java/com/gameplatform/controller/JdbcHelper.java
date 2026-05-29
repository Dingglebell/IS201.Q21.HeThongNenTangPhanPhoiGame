package com.gameplatform.controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

final class JdbcHelper {
    private JdbcHelper() {
    }

    static int nextValue(Connection connection, String sequenceName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT " + sequenceName + ".NEXTVAL FROM dual");
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    static LocalDate localDate(ResultSet resultSet, String column) throws SQLException {
        Date date = resultSet.getDate(column);
        return date == null ? null : date.toLocalDate();
    }

    static LocalDateTime localDateTime(ResultSet resultSet, String column) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(column);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}



