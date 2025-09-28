package com.jobhunter.config;

import com.jobhunter.autofill.BrowserSetup;
import org.openqa.selenium.WebDriver;

public class AppConfig {

    // Singleton instance
    private static AppConfig instance;
    private WebDriver driver;

    private AppConfig() {
        // Private constructor for singleton
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    // --- Configuration Methods ---

    /**
     * Get job hunter's name from environment
     */
    public String getJobHunterName() {
        String name = System.getenv("JOB_HUNTER_NAME");
        return (name != null && !name.isEmpty()) ? name : "Job Hunter";
    }

    /**
     * Get job hunter's email from environment
     */
    public String getJobHunterEmail() {
        String email = System.getenv("JOB_HUNTER_EMAIL");
        return (email != null && !email.isEmpty()) ? email : "jobhunter@example.com";
    }

    /**
     * Get job hunter's phone from environment
     */
    public String getJobHunterPhone() {
        String phone = System.getenv("JOB_HUNTER_PHONE");
        return (phone != null && !phone.isEmpty()) ? phone : "+1-000-000-0000";
    }

    /**
     * Get resume URL/path from environment
     */
    public String getResumeUrl() {
        String resumePath = System.getenv("RESUME_PATH");
        return (resumePath != null && !resumePath.isEmpty()) ? resumePath : "resumes/my_resume.pdf";
    }

    /**
     * Get Discord webhook URL from environment
     */
    public String getDiscordWebhookUrl() {
        String webhook = System.getenv("DISCORD_WEBHOOK_URL");
        return (webhook != null && !webhook.isEmpty() && !webhook.equals("bruh")) ? webhook : null;
    }

    /**
     * Get email configuration for notifications
     */
    public String getEmailFrom() {
        return System.getenv("EMAIL_FROM");
    }

    public String getEmailPassword() {
        return System.getenv("EMAIL_PASSWORD");
    }

    /**
     * Get email recipient (job hunter's email)
     */
    public String getEmailTo() {
        return getJobHunterEmail();
    }

    // Singleton WebDriver instance
    public WebDriver getDriver() {
        if (driver == null) {
            try {
                driver = BrowserSetup.getBraveDriver();
            } catch (Exception e) {
                System.err.println("⚠️ Failed to create WebDriver: " + e.getMessage());
                // For headless environments, we'll skip browser automation
            }
        }
        return driver;
    }

    /**
     * Get resume path, handling Google Drive links and downloads
     */
    public String getResumePath() {
        String resumePath = getResumeUrl();
        if (resumePath.startsWith("http")) {
            // For URLs, we'll parse them directly in ResumeParser
            return resumePath;
        } else {
            // Handle local files - just return the path
            return resumePath;
        }
    }

    /**
     * Validate resume accessibility
     */
    public boolean validateResumeAccess() {
        String resumePath = getResumeUrl();
        return resumePath != null && !resumePath.isEmpty();
    }

    /**
     * Check if running in headless mode
     */
    public boolean isHeadless() {
        return "true".equals(System.getProperty("headless")) || 
               System.getenv("CI") != null ||
               System.getenv("GITHUB_ACTIONS") != null;
    }

    /**
     * Get minimum job match score threshold
     */
    public int getMinimumMatchScore() {
        String threshold = System.getenv("MIN_MATCH_SCORE");
        try {
            return threshold != null ? Integer.parseInt(threshold) : 60;
        } catch (NumberFormatException e) {
            return 60; // Default threshold
        }
    }

    /**
     * Get maximum jobs to process per run
     */
    public int getMaxJobsPerRun() {
        String maxJobs = System.getenv("MAX_JOBS_PER_RUN");
        try {
            return maxJobs != null ? Integer.parseInt(maxJobs) : 50;
        } catch (NumberFormatException e) {
            return 50; // Default max jobs
        }
    }

    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error closing WebDriver: " + e.getMessage());
            }
            driver = null;
        }
    }
}