package com.jobhunter.autofill;

import com.jobhunter.model.Resume;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AutofillService {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public AutofillService(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void applyToJob(String jobUrl, Resume resume, String resumePath) {
        try {
            driver.get(jobUrl);
            wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));

            fillTextField(FormField.FULL_NAME, resume.getName());
            fillTextField(FormField.EMAIL, resume.getEmail());
            fillTextField(FormField.PHONE, resume.getPhone());

            uploadFile(FormField.RESUME_UPLOAD, resumePath);

            // The submit logic will be handled separately to allow for manual review
            // clickSubmitButton();

        } catch (Exception e) {
            System.err.println("❌ Error during autofill process for job: " + jobUrl);
            e.printStackTrace();
        }
    }

    private void fillTextField(FormField field, String value) {
        if (value == null || value.isEmpty()) return;

        findField(field).ifPresent(element -> {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(element));
                element.clear();
                element.sendKeys(value);
                System.out.println("✅ Filled '" + field.getDisplayName() + "'");
            } catch (Exception e) {
                System.err.println("⚠️ Could not fill field: " + field.getDisplayName());
            }
        });
    }

    private void uploadFile(FormField field, String filePath) {
        if (filePath == null || filePath.isEmpty()) return;

        findField(field).ifPresent(element -> {
            try {
                // Do not wait for clickability on file inputs; they are often not interactable
                element.sendKeys(filePath);
                System.out.println("📄 " + field.getDisplayName() + " uploaded successfully.");
            } catch (Exception e) {
                System.err.println("⚠️ Could not upload file for: " + field.getDisplayName());
            }
        });
    }

    public void clickSubmitButton() {
        findField(FormField.SUBMIT).ifPresent(element -> {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(element));
                element.click();
                System.out.println("✅ Application submitted!");
            } catch (Exception e) {
                System.err.println("⚠️ Could not click the submit button.");
            }
        });
    }

    private Optional<WebElement> findField(FormField field) {
        for (String identifier : field.getIdentifiers()) {
            try {
                // Try by name, id, placeholder, and aria-label
                List<WebElement> elements = driver.findElements(By.cssSelector(
                        String.format("input[name='%s'], input[id='%s'], textarea[name='%s'], textarea[id='%s'], *[aria-label*='%s'], *[placeholder*='%s']",
                                identifier, identifier, identifier, identifier, identifier, identifier)
                ));

                if (!elements.isEmpty()) {
                    return Optional.of(elements.get(0));
                }

                // For submit buttons, also try by text content
                if (field == FormField.SUBMIT) {
                    List<WebElement> buttons = driver.findElements(By.xpath(
                            String.format("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]", identifier.toLowerCase())
                    ));
                    if (!buttons.isEmpty()) {
                        return Optional.of(buttons.get(0));
                    }
                }

            } catch (Exception e) {
                // Ignore and try next identifier
            }
        }
        System.err.println("❌ Could not find field: " + field.getDisplayName());
        return Optional.empty();
    }

    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
