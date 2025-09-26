package com.jobhunter.fetcher;

import com.jobhunter.model.Job;
import com.jobhunter.config.AppConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Shine.com job fetcher - Popular Indian job portal
 */
public class ShineJobFetcher implements JobFetcher {

    private static final int MAX_JOBS = 15;

    @Override
    public List<Job> fetchJobs(List<String> skills) {
        WebDriver driver = AppConfig.getDriver();
        List<Job> jobs = new ArrayList<>();

        try {
            String query = String.join("+", skills);
            String searchUrl = "https://www.shine.com/job-search/" + query + "-jobs";
            driver.get(searchUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
            Thread.sleep(3000); // Let page load

            JavascriptExecutor js = (JavascriptExecutor) driver;
            int totalJobsScraped = 0;
            int scrollAttempts = 0;
            final int MAX_SCROLL_ATTEMPTS = 4;

            while (totalJobsScraped < MAX_JOBS && scrollAttempts < MAX_SCROLL_ATTEMPTS) {
                List<WebElement> jobElements = driver.findElements(By.cssSelector(".jobCard, .job-card, [data-testid='job-card']"));

                for (WebElement el : jobElements) {
                    if (totalJobsScraped >= MAX_JOBS) break;

                    try {
                        String title = "";
                        try {
                            title = el.findElement(By.cssSelector(".jobTitle a, .job-title a, h2 a")).getText();
                        } catch (Exception e) {
                            title = el.findElement(By.cssSelector("a")).getText();
                        }

                        String company = "";
                        try {
                            company = el.findElement(By.cssSelector(".companyName, .company-name")).getText();
                        } catch (Exception e) {
                            company = "Company on Shine";
                        }

                        String link = "";
                        try {
                            WebElement linkElement = el.findElement(By.cssSelector(".jobTitle a, .job-title a, h2 a"));
                            link = linkElement.getAttribute("href");
                            if (link != null && !link.startsWith("http")) {
                                link = "https://www.shine.com" + link;
                            }
                        } catch (Exception e) {
                            link = "https://www.shine.com/job-search";
                        }

                        String description = "";
                        try {
                            description = el.findElement(By.cssSelector(".jobDescription, .job-desc")).getText();
                        } catch (Exception ignored) {}

                        if (!title.isEmpty()) {
                            jobs.add(new Job("shine_job" + (totalJobsScraped + 1), title, description, link, company));
                            totalJobsScraped++;
                        }

                    } catch (Exception innerEx) {
                        System.out.println("⚠️ Error scraping Shine job: " + innerEx.getMessage());
                    }
                }

                // Scroll to load more jobs
                js.executeScript("window.scrollBy(0, 1000)");
                Thread.sleep(2500);
                scrollAttempts++;
            }

            System.out.println("✅ Scraped " + jobs.size() + " jobs from Shine.com");

        } catch (Exception e) {
            System.out.println("❌ Error fetching Shine jobs: " + e.getMessage());
        }

        return jobs;
    }
}