package com.jobhunter.autofill;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class AutofillService {

    private WebDriver driver;

    public AutofillService(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Opens the job application page and fills in details
     * @param jobUrl - URL of the job application page
     * @param personalDetails - Map of fieldName -> value
     * @param resumePath - Path to the resume file (PDF/DOCX)
     */
    public void applyToJob(String jobUrl, Map<String, String> personalDetails, String resumePath) {
        try {
            driver.get(jobUrl);
            Thread.sleep(2000); // wait for page to load

            // Fill in text fields
            for (Map.Entry<String, String> entry : personalDetails.entrySet()) {
                try {
                    WebElement inputField = driver.findElement(By.name(entry.getKey()));
                    inputField.clear();
                    inputField.sendKeys(entry.getValue());
                } catch (Exception e) {
                    System.out.println("⚠️ Could not fill field: " + entry.getKey());
                }
            }

            // Upload resume
            if (resumePath != null && !resumePath.isEmpty()) {
                try {
                    WebElement resumeUpload = driver.findElement(By.name("resume")); // adjust selector per platform
                    resumeUpload.sendKeys(resumePath);
                    System.out.println("📄 Resume uploaded successfully");
                } catch (Exception e) {
                    System.out.println("⚠️ Could not upload resume. Check the file input selector.");
                }
            }

            // Click apply/submit button
            try {
                WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'Apply')]"));
                submitButton.click();
                System.out.println("✅ Application submitted successfully for job: " + jobUrl);
            } catch (Exception e) {
                System.out.println("⚠️ Could not find or click the submit button.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error applying to job: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
