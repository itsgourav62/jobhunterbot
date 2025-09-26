package com.jobhunter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Interactive setup wizard to configure Job Hunter Bot
 */
public class SetupWizard {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("🚀 Welcome to Job Hunter Bot Setup Wizard!");
        System.out.println("=============================================");
        System.out.println("This wizard will help you configure your bot for optimal job hunting.\n");
        
        try {
            // Check if .env already exists
            if (Files.exists(Paths.get(".env"))) {
                System.out.print("⚠️ .env file already exists. Overwrite? (y/N): ");
                String overwrite = scanner.nextLine().trim().toLowerCase();
                if (!overwrite.equals("y") && !overwrite.equals("yes")) {
                    System.out.println("Setup cancelled. Your existing configuration is preserved.");
                    return;
                }
            }
            
            // Collect user information
            System.out.println("📝 Please provide your details:\n");
            
            System.out.print("👤 Full Name: ");
            String fullName = scanner.nextLine().trim();
            
            System.out.print("📧 Email Address: ");
            String email = scanner.nextLine().trim();
            
            System.out.print("📱 Phone Number (with country code, e.g., +91XXXXXXXXXX): ");
            String phone = scanner.nextLine().trim();
            
            System.out.println("\n📄 Resume Configuration:");
            System.out.println("Choose your resume option:");
            System.out.println("1. Google Drive link (recommended)");
            System.out.println("2. Local file path");
            System.out.println("3. Dropbox/OneDrive link");
            System.out.print("Enter choice (1-3): ");
            
            String resumeChoice = scanner.nextLine().trim();
            String resumePath = "";
            
            switch (resumeChoice) {
                case "1":
                    System.out.println("\n📋 Google Drive Setup:");
                    System.out.println("1. Upload your resume to Google Drive");
                    System.out.println("2. Right-click → Share → Change to 'Anyone with the link'");
                    System.out.println("3. Copy the sharing link");
                    System.out.print("Enter Google Drive link: ");
                    String driveLink = scanner.nextLine().trim();
                    resumePath = convertGoogleDriveLink(driveLink);
                    break;
                case "2":
                    System.out.print("Enter full path to your resume file: ");
                    resumePath = scanner.nextLine().trim();
                    break;
                case "3":
                    System.out.print("Enter direct download link to your resume: ");
                    resumePath = scanner.nextLine().trim();
                    break;
                default:
                    System.out.println("Invalid choice, using Google Drive option...");
                    System.out.print("Enter Google Drive link: ");
                    driveLink = scanner.nextLine().trim();
                    resumePath = convertGoogleDriveLink(driveLink);
            }
            
            // Discord webhook setup
            System.out.println("\n🎯 Discord Notifications Setup (Optional but Recommended):");
            System.out.println("Get job alerts directly in Discord!");
            System.out.print("Do you want to set up Discord notifications? (Y/n): ");
            String wantsDiscord = scanner.nextLine().trim().toLowerCase();
            
            String discordWebhook = "";
            if (wantsDiscord.isEmpty() || wantsDiscord.equals("y") || wantsDiscord.equals("yes")) {
                System.out.println("\n📱 Discord Webhook Setup:");
                System.out.println("1. Go to your Discord server");
                System.out.println("2. Server Settings → Integrations → Webhooks");
                System.out.println("3. Create New Webhook");
                System.out.println("4. Choose channel and copy webhook URL");
                System.out.print("Enter Discord webhook URL (or press Enter to skip): ");
                discordWebhook = scanner.nextLine().trim();
                
                if (discordWebhook.isEmpty()) {
                    discordWebhook = "YOUR_DISCORD_WEBHOOK_URL_HERE";
                }
            } else {
                discordWebhook = "DISCORD_NOTIFICATIONS_DISABLED";
            }
            
            // Job preferences
            System.out.println("\n🎯 Job Search Preferences:");
            System.out.print("Preferred locations (comma-separated, e.g., Bangalore,Mumbai,Remote): ");
            String locations = scanner.nextLine().trim();
            if (locations.isEmpty()) {
                locations = "Bangalore,Mumbai,Delhi,Remote";
            }
            
            System.out.print("Minimum experience years (default: 2): ");
            String minExp = scanner.nextLine().trim();
            if (minExp.isEmpty()) {
                minExp = "2";
            }
            
            System.out.print("Maximum applications per day (default: 10): ");
            String maxApps = scanner.nextLine().trim();
            if (maxApps.isEmpty()) {
                maxApps = "10";
            }
            
            System.out.print("Minimum match score % (default: 60): ");
            String minScore = scanner.nextLine().trim();
            if (minScore.isEmpty()) {
                minScore = "60";
            }
            
            // Skills
            System.out.println("\n💻 Technical Skills:");
            System.out.println("Enter your skills (comma-separated)");
            System.out.println("Example: Java,Spring Boot,React,Python,SQL,Docker");
            System.out.print("Your skills: ");
            String skills = scanner.nextLine().trim();
            if (skills.isEmpty()) {
                skills = "Java,Spring,React,Python,Node.js,SQL";
            }
            
            // Generate .env file
            StringBuilder envContent = new StringBuilder();
            envContent.append("# Job Hunter Bot Configuration\n");
            envContent.append("# Generated by Setup Wizard on ").append(java.time.LocalDateTime.now()).append("\n\n");
            
            envContent.append("# Personal Information\n");
            envContent.append("JOB_HUNTER_NAME=").append(fullName).append("\n");
            envContent.append("JOB_HUNTER_EMAIL=").append(email).append("\n");
            envContent.append("JOB_HUNTER_PHONE=").append(phone).append("\n\n");
            
            envContent.append("# Resume Configuration\n");
            envContent.append("RESUME_PATH=").append(resumePath).append("\n\n");
            
            envContent.append("# Notification Settings\n");
            envContent.append("DISCORD_WEBHOOK_URL=").append(discordWebhook).append("\n\n");
            
            envContent.append("# Job Search Preferences\n");
            envContent.append("PREFERRED_LOCATIONS=").append(locations).append("\n");
            envContent.append("MIN_EXPERIENCE_YEARS=").append(minExp).append("\n");
            envContent.append("MAX_APPLICATIONS_PER_DAY=").append(maxApps).append("\n");
            envContent.append("MINIMUM_MATCH_SCORE=").append(minScore).append("\n");
            envContent.append("SKILLS=").append(skills).append("\n\n");
            
            envContent.append("# Advanced Settings\n");
            envContent.append("HEADLESS_BROWSER=true\n");
            envContent.append("BROWSER_TIMEOUT_SECONDS=30\n");
            envContent.append("PARALLEL_FETCHING=true\n");
            envContent.append("DEBUG_MODE=false\n");
            
            // Write .env file
            try (FileWriter writer = new FileWriter(".env")) {
                writer.write(envContent.toString());
            }
            
            System.out.println("\n✅ Configuration saved to .env file!");
            System.out.println("\n🚀 Next Steps:");
            System.out.println("1. Test your setup: ./gradlew runAPIJob");
            System.out.println("2. Run full job search: ./gradlew runBot");
            System.out.println("3. Set up GitHub Actions for automation (see README.md)");
            
            if (discordWebhook.equals("YOUR_DISCORD_WEBHOOK_URL_HERE")) {
                System.out.println("\n⚠️  Don't forget to update your Discord webhook URL in .env");
            }
            
            System.out.println("\n🎯 Your Job Hunter Bot is ready to help you find amazing opportunities!");
            
        } catch (IOException e) {
            System.err.println("❌ Error creating configuration: " + e.getMessage());
        }
    }
    
    private static String convertGoogleDriveLink(String originalLink) {
        if (originalLink == null || !originalLink.contains("drive.google.com")) {
            return originalLink;
        }
        
        if (originalLink.contains("/file/d/")) {
            // Extract file ID from view link
            int startIndex = originalLink.indexOf("/file/d/") + "/file/d/".length();
            int endIndex = originalLink.indexOf("/", startIndex);
            if (endIndex == -1) {
                endIndex = originalLink.length();
            }
            String fileId = originalLink.substring(startIndex, endIndex);
            return "https://drive.google.com/uc?id=" + fileId + "&export=download";
        }
        
        return originalLink;
    }
}