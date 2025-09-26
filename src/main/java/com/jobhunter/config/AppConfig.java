package com.jobhunter.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

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
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-gpu");
            driver = new ChromeDriver(options);
            driver.manage().window().maximize();
        }
        return driver;
    }

    public static String getResumePath() {
        return resumePath;
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

    // Optionally add setter methods if you want runtime updates
}
