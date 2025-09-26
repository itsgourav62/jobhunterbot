package com.jobhunter;

public class TestRun {
    public static void main(String[] args) {
        System.out.println("✅ Basic Java execution works!");
        System.out.println("Current Directory: " + System.getProperty("user.dir"));
        System.out.println("Java Version: " + System.getProperty("java.version"));

        try {
            Class.forName("org.openqa.selenium.WebDriver");
            System.out.println("✅ Selenium library is available!");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Selenium library not found!");
        }
    }
}
