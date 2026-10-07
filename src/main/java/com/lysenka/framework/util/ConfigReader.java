package com.lysenka.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream in = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("project.properties")) {
            if (in == null) {
                throw new RuntimeException("properties file not found");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load project file", e);
        }
    }

    public static String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key);
        }
        if (value == null) {
            throw new RuntimeException(key + " not found in properties file");
        }
        return value.trim();
    }

    public static String getProperty(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key, defaultValue);
        }
        return value.trim();
    }
}
