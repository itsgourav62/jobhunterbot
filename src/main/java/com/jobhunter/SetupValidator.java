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
            
            for (String envVar : requiredEnvVars) {
                String value = System.getenv(envVar);
                if (value != null && !value.isEmpty() && !value.equals("bruh")) {
                    String displayValue = value.length() > 30 ? 
                        value.substring(0, 30) + "..." : value;
                    System.out.println("✅ " + envVar + ": " + displayValue);
                } else {
                    System.out.println("❌ " + envVar + ": Not set or invalid");
                    allGood = false;
                }
            }
            
            // Test resume parsing
            System.out.println("\n📄 Resume Parsing Test:");
            try {
                String resumePath = config.getResumeUrl();
                if (resumePath != null && !resumePath.isEmpty()) {
                    ResumeParser parser = new ResumeParser();
                    Resume resume = parser.parseResumeFromUrl(resumePath);
                    
                    System.out.println("✅ Resume parsed successfully");
                    System.out.println("   Name: " + resume.getName());
                    System.out.println("   Skills: " + resume.getSkills().size() + " found");
                    System.out.println("   Email: " + resume.getEmail());
                } else {
                    System.out.println("❌ Resume URL not configured");
                    allGood = false;
                }
            } catch (Exception e) {
                System.out.println("❌ Resume parsing failed: " + e.getMessage());
                allGood = false;
            }
            
            // Test Discord notification
            System.out.println("\n📱 Discord Test:");
            try {
                NotifierService notifier = new NotifierService();
                boolean discordSent = notifier.sendDiscordNotification("🧪 Setup validation test message from Job Hunter Bot!");
                if (discordSent) {
                    System.out.println("✅ Discord notification sent successfully");
                } else {
                    System.out.println("❌ Discord notification failed");
                    allGood = false;
                }
            } catch (Exception e) {
                System.out.println("❌ Discord test failed: " + e.getMessage());
                allGood = false;
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