package com.qa.practice.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class Config {

    private static final Properties PROPERTIES = load();

    private Config() {
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream stream = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (stream == null) {
                throw new IllegalStateException("config.properties not found in test resources");
            }
            properties.load(stream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
        return properties;
    }

    public static String get(String key) {
        String envKey = key.toUpperCase().replace('.', '_');
        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        String fromSystem = System.getProperty(key);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }
        return Objects.requireNonNull(PROPERTIES.getProperty(key), "Missing config key: " + key);
    }

    public static String apiBaseUrl() {
        return get("api.baseUrl");
    }

    public static String authUsername() {
        return get("auth.username");
    }

    public static String authPassword() {
        return get("auth.password");
    }
}
