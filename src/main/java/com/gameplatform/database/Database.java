package com.gameplatform.database;

import com.gameplatform.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                AppConfig.dbUrl(),
                AppConfig.dbUsername(),
                AppConfig.dbPassword()
        );
    }

    public static boolean canConnect() {
        try (Connection ignored = getConnection()) {
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }
}

