package com.jobhunter.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class AppConfig {

    private static AppConfig instance;
    private final Properties properties = new Properties();

    private AppConfig() {
        try (InputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            // If config.properties is not found, use default values
            System.out.println("config.properties not found, using default values.");
        }
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public String getBrowser() {
        return properties.getProperty("browser", "chrome");
    }

    public String getResumePath() {
        return properties.getProperty("resume.path", "resume.json");
    }

    public String getDatabasePath() {
        return properties.getProperty("database.path", "./db/jobhunter");
    }

    public String getOutputDir() {
        return properties.getProperty("output.dir", "output");
    }

    public String getDiscordWebhookUrl() {
        return properties.getProperty("discord.webhook.url", "");
    }

    public void cleanup() {
        // In case any cleanup is needed for the config
    }
}