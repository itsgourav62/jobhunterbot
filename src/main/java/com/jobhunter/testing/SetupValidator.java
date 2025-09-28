package com.jobhunter.testing;

/**
 * Simple setup validator for GitHub Actions
 */
public class SetupValidator {
    
    public static void main(String[] args) {
        System.out.println("🔧 Job Hunter Bot Setup Validator");
        System.out.println("================================");
        
        boolean allGood = true;
        
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
        for (String envVar : requiredEnvVars) {
            String value = System.getenv(envVar);
            if (value != null && !value.isEmpty()) {
                String displayValue = value.length() > 20 ? 
                    value.substring(0, 20) + "..." : value;
                System.out.println("✅ " + envVar + ": " + displayValue);
            } else {
                System.out.println("❌ " + envVar + ": Not set");
                allGood = false;
            }
        }
        
        // Check system properties
        System.out.println("\n🖥️ System Info:");
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("Architecture: " + System.getProperty("os.arch"));
        System.out.println("User: " + System.getProperty("user.name"));
        
        // Final validation
        System.out.println("\n" + "=".repeat(32));
        if (allGood) {
            System.out.println("✅ Setup validation PASSED!");
            System.out.println("🚀 Job Hunter Bot is ready to run!");
            System.exit(0);
        } else {
            System.out.println("❌ Setup validation FAILED!");
            System.out.println("💡 Please check your GitHub Secrets configuration");
            System.exit(1);
        }
    }
}