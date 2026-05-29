package com.gameplatform.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {
    private static final String CONFIG_FILE = "/db.properties";
    private static final Properties PROPERTIES = loadProperties();

    private AppConfig() {
    }

    public static String dbUrl() {
        return value("db.url", "GAME_DB_URL");
    }

    public static String dbUsername() {
        return value("db.username", "GAME_DB_USERNAME");
    }

    public static String dbPassword() {
        return value("db.password", "GAME_DB_PASSWORD");
    }

    private static String value(String propertyName, String environmentName) {
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }
        return PROPERTIES.getProperty(propertyName, "").trim();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = AppConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Không đọc được file cấu hình " + CONFIG_FILE, exception);
        }
        return properties;
    }
}


