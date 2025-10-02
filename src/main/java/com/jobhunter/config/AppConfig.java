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
        // Load from config.properties first
        try (InputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            System.out.println("config.properties not found, using default values.");
        }

        // Load from .env file if it exists
        loadDotEnvFile();

        // Override properties with environment variables if set
        System.getenv().forEach((key, value) -> {
            String propKey = key.toLowerCase().replace("_", ".");
            properties.setProperty(propKey, value); // Always set, overriding if exists
        });
    }

    private void loadDotEnvFile() {
        try (InputStream input = new FileInputStream(".env")) {
            Properties envProps = new Properties();
            envProps.load(input);
            
            // Convert .env properties to our format
            envProps.forEach((key, value) -> {
                String propKey = key.toString().toLowerCase().replace("_", ".");
                properties.setProperty(propKey, value.toString());
            });
            
            System.out.println("✅ Loaded configuration from .env file");
        } catch (IOException ex) {
            System.out.println("⚠️ .env file not found, using config.properties and environment variables only.");
        }
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public String getJobHunterName() {
        return properties.getProperty("job.hunter.name", "");
    }

    public String getJobHunterEmail() {
        return properties.getProperty("job.hunter.email", "");
    }

    public String getBrowser() {
        return properties.getProperty("browser", "chrome");
    }

    public void setProperty(String key, String value) {
        if (value != null) {
            properties.setProperty(key, value);
        }
    }

    public String getResumePath() {
        // Try both RESUME_PATH (.env format) and resume.url (config.properties format)
        String resumePath = properties.getProperty("resume.path", "");
        if (resumePath.isEmpty()) {
            resumePath = properties.getProperty("resume.url", "");
        }
        return resumePath;
    }

    public String getResumeUrl() {
        return getResumePath(); // Same as resume path
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

    public String getEmailFrom() {
        return properties.getProperty("email.from", "");
    }

    public String getEmailPassword() {
        return properties.getProperty("email.password", "");
    }

    public String getEmailTo() {
        return properties.getProperty("email.to", "");
    }

    public void cleanup() {
        // In case any cleanup is needed for the config
    }
}