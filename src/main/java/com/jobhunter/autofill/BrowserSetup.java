package com.jobhunter.autofill;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.time.Duration;

public class BrowserSetup {

    public static WebDriver getBraveDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        String bravePath = getBravePath();

        if (bravePath == null) {
            System.out.println("⚠️ Brave browser not found. Please install it or update the path in BrowserSetup.java");
            // Fallback to default Chrome, or handle the error as needed
            return null;
        }

        options.setBinary(bravePath);
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-gpu");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        return driver;
    }

    private static String getBravePath() {
        String os = System.getProperty("os.name").toLowerCase();
        String[] paths;

        if (os.contains("win")) {
            paths = new String[]{
                    System.getenv("LOCALAPPDATA") + "\\BraveSoftware\\Brave-Browser\\Application\\brave.exe",
                    System.getenv("PROGRAMFILES") + "\\BraveSoftware\\Brave-Browser\\Application\\brave.exe",
                    System.getenv("PROGRAMFILES(X86)") + "\\BraveSoftware\\Brave-Browser\\Application\\brave.exe",
                    "C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe",
                    "C:\\Program Files (x86)\\BraveSoftware\\Brave-Browser\\Application\\brave.exe"
            };
        } else if (os.contains("mac")) {
            paths = new String[]{
                    "/Applications/Brave Browser.app/Contents/MacOS/Brave Browser",
                    System.getProperty("user.home") + "/Applications/Brave Browser.app/Contents/MacOS/Brave Browser"
            };
        } else { // linux
            paths = new String[]{
                    "/usr/bin/brave-browser",
                    "/usr/bin/brave",
                    "/snap/bin/brave",
                    "/opt/brave.com/brave/brave-browser",
                    "/opt/brave/brave"
            };
        }

        for (String path : paths) {
            if (path != null && new File(path).exists()) {
                return path;
            }
        }
        return null; // Brave not found
    }
}
