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
 * AngelList/Wellfound job fetcher - good for startup jobs
 */
public class AngelListJobFetcher implements JobFetcher {

    private static final int MAX_JOBS = 15;

    @Override
    public List<Job> fetchJobs(List<String> skills) {
        WebDriver driver = AppConfig.getInstance().getDriver();
        List<Job> jobs = new ArrayList<>();

        try {
            String query = String.join("%20", skills);
            String searchUrl = "https://angel.co/jobs?keywords=" + query;
            driver.get(searchUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Thread.sleep(3000); // Let page load completely

            JavascriptExecutor js = (JavascriptExecutor) driver;
            int totalJobsScraped = 0;
            int scrollAttempts = 0;
            final int MAX_SCROLL_ATTEMPTS = 5;

            while (totalJobsScraped < MAX_JOBS && scrollAttempts < MAX_SCROLL_ATTEMPTS) {
                List<WebElement> jobElements = driver.findElements(By.cssSelector("[data-test='JobSearchCard']"));

                for (WebElement el : jobElements) {
                    if (totalJobsScraped >= MAX_JOBS) break;

                    try {
                        String title = el.findElement(By.cssSelector("h2 a, .job-title")).getText();
                        String company = "";
                        try {
                            company = el.findElement(By.cssSelector(".company-name, .startup-name")).getText();
                        } catch (Exception e) {
                            company = "Startup Company";
                        }
                        
                        String link = "";
                        try {
                            link = el.findElement(By.cssSelector("h2 a")).getAttribute("href");
                            if (link != null && !link.startsWith("http")) {
                                link = "https://angel.co" + link;
                            }
                        } catch (Exception e) {
                            link = "https://angel.co/jobs";
                        }

                        String description = "";
                        try {
                            description = el.findElement(By.cssSelector(".job-description, .content")).getText();
                        } catch (Exception ignored) {}

                        jobs.add(new Job("angellist_job" + (totalJobsScraped + 1), title, description, link, company));
                        totalJobsScraped++;

                    } catch (Exception innerEx) {
                        System.out.println("⚠️ Error scraping AngelList job: " + innerEx.getMessage());
                    }
                }

                // Scroll to load more jobs
                js.executeScript("window.scrollBy(0, 1500)");
                Thread.sleep(2500);
                scrollAttempts++;
            }

            System.out.println("✅ Scraped " + jobs.size() + " jobs from AngelList/Wellfound");

        } catch (Exception e) {
            System.out.println("❌ Error fetching AngelList jobs: " + e.getMessage());
        }

        return jobs;
    }
}