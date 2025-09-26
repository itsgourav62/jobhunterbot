package com.jobhunter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("🦁 Job Hunter Bot v1.0 - Brave Edition");
        System.out.println("====================================");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name"));

        testBraveSetup();
    }

    private static void testBraveSetup() {
        System.out.println("\n📋 Starting Brave browser test...");

        WebDriver driver = null;

        try {
            // Setup Chrome driver (Brave uses the same driver)
            System.out.println("⚙️  Setting up driver for Brave...");
            // Force exact Brave Chromium version (adjust if Brave updates)
            WebDriverManager.chromedriver().browserVersion("140.0.7339.213").setup();

            // Configure for Brave
            ChromeOptions options = new ChromeOptions();

            // IMPORTANT: Set Brave binary location based on OS
            String braveLocation = getBravePath();
            System.out.println("🔍 Looking for Brave at: " + braveLocation);

            options.setBinary(braveLocation);

            // Add necessary arguments
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-gpu");  // Added for compatibility

            // Create driver with Brave
            System.out.println("🦁 Launching Brave browser...");
            driver = new ChromeDriver(options);
            System.out.println("✅ Brave launched successfully!");

            // Configure driver
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().window().maximize();

            // Wait a moment for browser to fully initialize
            Thread.sleep(2000);

            // Test navigation with proper URL (removed < >)
            System.out.println("📍 Navigating to Naukri.com...");
            driver.navigate().to("https://www.naukri.com");

            // Wait for page to load
            Thread.sleep(3000);

            // Get page info
            String title = driver.getTitle();
            String url = driver.getCurrentUrl();

            System.out.println("✅ Successfully opened: " + url);
            System.out.println("📄 Page title: " + title);

            // Keep browser open for 5 seconds to see the result
            System.out.println("\n👀 Browser will close in 5 seconds...");
            Thread.sleep(5000);

        } catch (Exception e) {
            System.out.println("\n❌ Error occurred:");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();

            System.out.println("\n💡 Troubleshooting Tips:");
            System.out.println("1. Make sure Brave is installed");
            System.out.println("2. Check if Brave path is correct");
            System.out.println("3. Try running with admin/sudo privileges");

        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                    System.out.println("👋 Brave browser closed");
                } catch (Exception e) {
                    System.out.println("Error closing browser: " + e.getMessage());
                }
            }
            System.out.println("====================================");
        }
    }

    private static String getBravePath() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("mac")) {
            return "/Applications/Brave Browser.app/Contents/MacOS/Brave Browser";
        } else if (os.contains("win")) {
            // Try common Windows locations
            String[] possiblePaths = {
                    System.getenv("LOCALAPPDATA") + "\\BraveSoftware\\Brave-Browser\\Application\\brave.exe",
                    System.getenv("PROGRAMFILES") + "\\BraveSoftware\\Brave-Browser\\Application\\brave.exe",
                    System.getenv("PROGRAMFILES(X86)") + "\\BraveSoftware\\Brave-Browser\\Application\\brave.exe",
                    "C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe",
                    "C:\\Program Files (x86)\\BraveSoftware\\Brave-Browser\\Application\\brave.exe"
            };

            for (String path : possiblePaths) {
                if (new java.io.File(path).exists()) {
                    return path;
                }
            }
            // Return default if not found
            return possiblePaths[0];

        } else if (os.contains("linux")) {
            // Try common Linux locations
            String[] possiblePaths = {
                    "/usr/bin/brave-browser",
                    "/usr/bin/brave",
                    "/snap/bin/brave",
                    "/opt/brave.com/brave/brave-browser"
            };

            for (String path : possiblePaths) {
                if (new java.io.File(path).exists()) {
                    return path;
                }
            }
            // Return default if not found
            return possiblePaths[0];
        }

        return "";
    }
}
