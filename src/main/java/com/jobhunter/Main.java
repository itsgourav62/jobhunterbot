package com.jobhunter;

import com.jobhunter.autofill.BrowserSetup;
import com.jobhunter.cron.SchedulerService;
import com.jobhunter.model.Resume;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("🦁 Job Hunter Bot v1.0");
        System.out.println("====================================");

        SchedulerService scheduler = null;
        try {
            // Create a dummy resume
            Resume resume = new Resume(
                    "Gourav",
                    "gourav@example.com",
                    "1234567890",
                    Arrays.asList("Java", "Spring", "SQL", "React"),
                    Collections.singletonList("5 years of experience in software development."),
                    Collections.singletonList("Bachelor's in Computer Science")
            );

            // Get the WebDriver
            WebDriver driver = BrowserSetup.getBraveDriver();

            // Start the scheduler service
            scheduler = new SchedulerService(resume, driver);
            scheduler.runDailyJob();

        } catch (Exception e) {
            System.out.println("\n❌ An unexpected error occurred in the main application:");
            e.printStackTrace();
        } finally {
            if (scheduler != null) {
                scheduler.close();
                System.out.println("✅ Application finished and browser closed.");
            }
            System.out.println("====================================");
        }
    }
}
