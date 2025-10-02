package com.jobhunter;

import com.jobhunter.config.AppConfig;
import com.jobhunter.model.Resume;
import com.jobhunter.parser.ResumeParser;
import com.jobhunter.notifier.NotifierService;

/**
 * Simple setup validator for GitHub Actions and local testing
 */
public class SetupValidator {
    
    public static void main(String[] args) {
        System.out.println("🔧 Job Hunter Bot Setup Validator v2.0");
        System.out.println("=====================================");
        
        boolean allGood = true;
        
        try {
            // Check Java version
            String javaVersion = System.getProperty("java.version");
            System.out.println("☕ Java Version: " + javaVersion);
            
            // Check available memory
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            System.out.println("🧠 Max Memory: " + (maxMemory / 1024 / 1024) + "MB");
            
            // Check environment variables
            String[] requiredEnvVars = {
                "JOB_HUNTER_NAME", 
                "JOB_HUNTER_EMAIL", 
                "RESUME_PATH",
                "DISCORD_WEBHOOK_URL"
            };
            
            System.out.println("\n🔐 Environment Variables:");
            AppConfig config = AppConfig.getInstance();
            
            // Manually set properties from environment variables for validation
            config.setProperty("job.hunter.name", System.getenv("JOB_HUNTER_NAME"));
            config.setProperty("job.hunter.email", System.getenv("JOB_HUNTER_EMAIL"));
            config.setProperty("resume.url", System.getenv("RESUME_PATH"));
            config.setProperty("discord.webhook.url", System.getenv("DISCORD_WEBHOOK_URL"));
            config.setProperty("email.from", System.getenv("EMAIL_FROM"));
            config.setProperty("email.password", System.getenv("EMAIL_PASSWORD"));
            config.setProperty("email.to", System.getenv("EMAIL_TO"));

            String jobHunterName = config.getJobHunterName();
            String jobHunterEmail = config.getJobHunterEmail();
            String resumeUrl = config.getResumeUrl();
            String discordWebhookUrl = config.getDiscordWebhookUrl();
            String emailFrom = config.getEmailFrom();
            String emailPassword = config.getEmailPassword();
            String emailTo = config.getEmailTo();

            if (jobHunterName != null && !jobHunterName.isEmpty()) {
                System.out.println("✅ JOB_HUNTER_NAME: " + jobHunterName);
            } else {
                System.out.println("❌ JOB_HUNTER_NAME: Not set or invalid");
                allGood = false;
            }
            if (jobHunterEmail != null && !jobHunterEmail.isEmpty()) {
                System.out.println("✅ JOB_HUNTER_EMAIL: " + jobHunterEmail);
            } else {
                System.out.println("❌ JOB_HUNTER_EMAIL: Not set or invalid");
                allGood = false;
            }
            if (resumeUrl != null && !resumeUrl.isEmpty()) {
                System.out.println("✅ RESUME_PATH: " + resumeUrl);
            } else {
                System.out.println("❌ RESUME_PATH: Not set or invalid");
                allGood = false;
            }
            // Discord webhook is optional in CI/CD environments
            if (discordWebhookUrl != null && !discordWebhookUrl.isEmpty() && !discordWebhookUrl.equals("bruh")) {
                System.out.println("✅ DISCORD_WEBHOOK_URL: " + discordWebhookUrl);
            } else {
                System.out.println("⚠️ DISCORD_WEBHOOK_URL: Not set or invalid (optional for CI/CD)");
                // Don't fail validation for missing Discord webhook in CI/CD
            }
            
            // Email configuration is optional in CI/CD environments
            if (emailFrom != null && !emailFrom.isEmpty() && !emailFrom.equals("test@example.com")) {
                System.out.println("✅ EMAIL_FROM: " + emailFrom);
            } else {
                System.out.println("⚠️ EMAIL_FROM: Not set or invalid (optional for CI/CD)");
                // Don't fail validation for missing email config in CI/CD
            }
            if (emailPassword != null && !emailPassword.isEmpty() && !emailPassword.equals("test_password")) {
                System.out.println("✅ EMAIL_PASSWORD: (set)");
            } else {
                System.out.println("⚠️ EMAIL_PASSWORD: Not set or invalid (optional for CI/CD)");
                // Don't fail validation for missing email password in CI/CD
            }
            if (emailTo != null && !emailTo.isEmpty()) {
                System.out.println("✅ EMAIL_TO: " + emailTo);
            } else {
                System.out.println("❌ EMAIL_TO: Not set or invalid");
                allGood = false;
            }
            
            // Test resume parsing
            System.out.println("\n📄 Resume Parsing Test:");
            try {
                String resumePath = config.getResumePath();
                if (resumePath != null && !resumePath.isEmpty()) {
                    ResumeParser parser = new ResumeParser();
                    Resume resume = parser.parseFromUrl(resumePath);
                    
                    System.out.println("✅ Resume parsed successfully");
                    // System.out.println("   Name: " + resume.getName()); // Temporarily commented out
                    // System.out.println("   Skills: " + resume.getSkills().size() + " found"); // Temporarily commented out
                    // System.out.println("   Email: " + resume.getEmail()); // Temporarily commented out
                } else {
                    System.out.println("❌ Resume URL not configured");
                    allGood = false;
                }
            } catch (Exception e) {
                System.out.println("❌ Resume parsing failed: " + e.getMessage());
                allGood = false;
            }
            
            // Test Discord notification (optional)
            System.out.println("\n📱 Discord Test:");
            try {
                NotifierService notifier = new NotifierService();
                if (discordWebhookUrl != null && !discordWebhookUrl.isEmpty() && !discordWebhookUrl.equals("bruh")) {
                    notifier.sendDiscordNotification(discordWebhookUrl, "🧪 Setup validation test message from Job Hunter Bot!");
                    System.out.println("✅ Discord notification sent successfully");
                } else {
                    System.out.println("⚠️ Discord webhook URL not configured, skipping test.");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Discord test failed: " + e.getMessage() + " (non-critical)");
                // Don't fail validation for Discord issues in CI/CD
            }
            
            // System properties
            System.out.println("\n🖥️ System Info:");
            System.out.println("OS: " + System.getProperty("os.name"));
            System.out.println("Architecture: " + System.getProperty("os.arch"));
            System.out.println("Working Dir: " + System.getProperty("user.dir"));
            
        } catch (Exception e) {
            System.out.println("❌ Validation failed with exception: " + e.getMessage());
            allGood = false;
        }
        
        // Final validation
        System.out.println("\n" + "=".repeat(35));
        if (allGood) {
            System.out.println("✅ Setup validation PASSED!");
            System.out.println("🚀 Job Hunter Bot is ready to run!");
            System.exit(0);
        } else {
            System.out.println("❌ Setup validation FAILED!");
            System.out.println("💡 Please check your configuration and try again");
            System.exit(1);
        }
    }
}