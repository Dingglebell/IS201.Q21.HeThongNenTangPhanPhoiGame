package com.gameplatform.database;

import com.gameplatform.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException exception) {
            throw new ExceptionInInitializerError("Không tìm thấy Oracle JDBC driver ojdbc11 trong classpath.");
        }
    }

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


