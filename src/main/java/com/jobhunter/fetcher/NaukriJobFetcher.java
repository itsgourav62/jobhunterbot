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

public class NaukriJobFetcher implements JobFetcher {

    private static final int MAX_JOBS = 20; // Limit number of jobs to scrape

    @Override
    public List<Job> fetchJobs(List<String> skills) {
        WebDriver driver = AppConfig.getInstance().getDriver();
        List<Job> jobs = new ArrayList<>();

        try {
            // Build search query from skills
            String query = String.join("+", skills);
            String searchUrl = "https://www.naukri.com/jobs?q=" + query + "&l=India";
            driver.get(searchUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".jobTuple")));

            // Scroll to load more jobs dynamically
            JavascriptExecutor js = (JavascriptExecutor) driver;
            int totalJobsScraped = 0;
            while (totalJobsScraped < MAX_JOBS) {
                List<WebElement> jobElements = driver.findElements(By.cssSelector(".jobTuple"));

                for (WebElement el : jobElements) {
                    if (totalJobsScraped >= MAX_JOBS) break;

                    try {
                        String title = el.findElement(By.cssSelector(".job-title")).getText();
                        String company = el.findElement(By.cssSelector(".companyName")).getText();
                        String link = el.findElement(By.cssSelector("a")).getAttribute("href");

                        // Some pages have short descriptions; else use empty string
                        String description = "";
                        try {
                            description = el.findElement(By.cssSelector(".job-description")).getText();
                        } catch (Exception ignored) {}

                        jobs.add(new Job("job" + (totalJobsScraped + 1), title, description, link, company));
                        totalJobsScraped++;

                    } catch (Exception innerEx) {
                        System.out.println("⚠️ Error scraping a job element: " + innerEx.getMessage());
                    }
                }

                // Scroll down to load more jobs if needed
                js.executeScript("window.scrollBy(0, 1000)");
                Thread.sleep(2000);
            }

            System.out.println("✅ Scraped " + jobs.size() + " jobs from Naukri.com");

        } catch (Exception e) {
            System.out.println("❌ Error fetching jobs: " + e.getMessage());
            e.printStackTrace();
        }

        return jobs;
    }
}
