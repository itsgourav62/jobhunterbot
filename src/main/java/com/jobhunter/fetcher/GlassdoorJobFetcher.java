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
 * Glassdoor job fetcher - good for company reviews + jobs
 */
public class GlassdoorJobFetcher implements JobFetcher {

    private static final int MAX_JOBS = 15;

    @Override
    public List<Job> fetchJobs(List<String> skills) {
        WebDriver driver = AppConfig.getDriver();
        List<Job> jobs = new ArrayList<>();

        try {
            String query = String.join("%20", skills);
            String searchUrl = "https://www.glassdoor.co.in/Job/jobs.htm?sc.keyword=" + query;
            driver.get(searchUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
            Thread.sleep(4000); // Glassdoor has anti-bot measures

            JavascriptExecutor js = (JavascriptExecutor) driver;
            int totalJobsScraped = 0;
            int scrollAttempts = 0;
            final int MAX_SCROLL_ATTEMPTS = 4;

            while (totalJobsScraped < MAX_JOBS && scrollAttempts < MAX_SCROLL_ATTEMPTS) {
                List<WebElement> jobElements = driver.findElements(By.cssSelector(".react-job-listing, .jobsearch-SerpJobCard, [data-test='job-listing']"));

                for (WebElement el : jobElements) {
                    if (totalJobsScraped >= MAX_JOBS) break;

                    try {
                        String title = "";
                        try {
                            title = el.findElement(By.cssSelector(".jobLink, .job-title a, [data-test='job-title']")).getText();
                        } catch (Exception e) {
                            title = el.findElement(By.cssSelector("h2 a, h3 a")).getText();
                        }

                        String company = "";
                        try {
                            company = el.findElement(By.cssSelector(".employerName, .company")).getText();
                        } catch (Exception e) {
                            company = "Company";
                        }

                        String link = "";
                        try {
                            WebElement linkElement = el.findElement(By.cssSelector(".jobLink, .job-title a, h2 a"));
                            link = linkElement.getAttribute("href");
                            if (link != null && !link.startsWith("http")) {
                                link = "https://www.glassdoor.co.in" + link;
                            }
                        } catch (Exception e) {
                            link = "https://www.glassdoor.co.in/Jobs/index.htm";
                        }

                        String description = "";
                        try {
                            description = el.findElement(By.cssSelector(".jobDescription, .summary")).getText();
                        } catch (Exception ignored) {}

                        jobs.add(new Job("glassdoor_job" + (totalJobsScraped + 1), title, description, link, company));
                        totalJobsScraped++;

                    } catch (Exception innerEx) {
                        System.out.println("⚠️ Error scraping Glassdoor job: " + innerEx.getMessage());
                    }
                }

                // Careful scrolling for Glassdoor
                js.executeScript("window.scrollBy(0, 1200)");
                Thread.sleep(3000);
                scrollAttempts++;
            }

            System.out.println("✅ Scraped " + jobs.size() + " jobs from Glassdoor");

        } catch (Exception e) {
            System.out.println("❌ Error fetching Glassdoor jobs: " + e.getMessage());
        }

        return jobs;
    }
}