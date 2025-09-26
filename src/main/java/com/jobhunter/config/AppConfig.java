package com.jobhunter.config;

import com.jobhunter.autofill.BrowserSetup;
import com.jobhunter.utils.ResumeUtils;
import org.openqa.selenium.WebDriver;

public class AppConfig {

    // --- Security Warning ---
    // It is strongly recommended to use environment variables for sensitive data.
    // Do not hardcode credentials in a public repository.
    // Example: System.getenv("RESUME_PATH")

    private static String resumePath = System.getenv("RESUME_PATH") != null ? System.getenv("RESUME_PATH") : "resumes/my_resume.pdf";
    private static String discordWebhookUrl = System.getenv("DISCORD_WEBHOOK_URL"); // Example: "https://discord.com/api/webhooks/..."
    private static String emailFrom = System.getenv("EMAIL_FROM"); // Example: "your_email@gmail.com"
    private static String emailPassword = System.getenv("EMAIL_PASSWORD"); // Example: "your_app_password"

    private static WebDriver driver;

    // Singleton WebDriver instance
    public static WebDriver getDriver() {
        if (driver == null) {
            driver = BrowserSetup.getBraveDriver();
        }
        return driver;
    }

    /**
     * Get resume path, handling Google Drive links and downloads
     */
    public static String getResumePath() {
        // Handle Google Drive links and download if needed
        return ResumeUtils.downloadResumeIfNeeded(resumePath);
    }

    /**
     * Get raw resume path (without processing)
     */
    public static String getRawResumePath() {
        return resumePath;
    }

    /**
     * Validate resume accessibility
     */
    public static boolean validateResumeAccess() {
        return ResumeUtils.validateResumeAccess(resumePath);
    }

    public static String getDiscordWebhookUrl() {
        return discordWebhookUrl;
    }

    public static String getEmailFrom() {
        return emailFrom;
    }

    public static String getEmailPassword() {
        return emailPassword;
    }

    /**
     * Cleanup temp files on application shutdown
     */
    public static void cleanup() {
        ResumeUtils.cleanupTempFiles();
        if (driver != null) {
            driver.quit();
        }
    }

    // Optionally add setter methods if you want runtime updates
}
