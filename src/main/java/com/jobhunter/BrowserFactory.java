package com.jobhunter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

public class BrowserFactory {

    public static WebDriver getDriver(String browserName) {
        switch (browserName.toLowerCase()) {
            case "firefox":
                return new FirefoxDriver();
            case "chrome":
            default:
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
                // Add any desired options here, e.g., headless mode
                // options.addArguments("--headless");
                return new ChromeDriver(options);
        }
    }
}
