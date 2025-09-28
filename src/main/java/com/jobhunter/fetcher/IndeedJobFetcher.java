package com.jobhunter.fetcher;

import com.jobhunter.model.Job;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class IndeedJobFetcher implements JobFetcher {

    private static final int MAX_JOBS = 20;

    @Override
    public List<Job> fetchJobs(List<String> skills, WebDriver driver) {
        List<Job> jobs = new ArrayList<>();

        try {
            String query = String.join("+", skills);
            String url = "https://www.indeed.co.in/jobs?q=" + query + "&l=India";
            driver.get(url);

            Thread.sleep(5000); // Wait for the page to load

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("ul.jobsearch-ResultsList")));

            JavascriptExecutor js = (JavascriptExecutor) driver;
            int totalJobsScraped = 0;

            while (totalJobsScraped < MAX_JOBS) {
                List<WebElement> jobElements = driver.findElements(By.cssSelector("li.css-5lfssm.eu4oa1w0"));

                for (WebElement el : jobElements) {
                    if (totalJobsScraped >= MAX_JOBS) break;

                    try {
                        String title = el.findElement(By.cssSelector("h2.jobTitle a")).getText();
                        String company = el.findElement(By.cssSelector("span.companyName")).getText();
                        String link = el.findElement(By.cssSelector("a")).getAttribute("href");

                        String description = "";
                        try {
                            description = el.findElement(By.cssSelector("div.job-snippet")).getText();
                        } catch (Exception ignored) {}

                        jobs.add(new Job("job" + (totalJobsScraped + 1), title, description, link, company));
                        totalJobsScraped++;
                    } catch (Exception innerEx) {
                        System.out.println("⚠️ Error scraping a job element: " + innerEx.getMessage());
                    }
                }

                js.executeScript("window.scrollBy(0, 1000)");
                Thread.sleep(2000);
            }

            System.out.println("✅ Scraped " + jobs.size() + " jobs from Indeed.com");

        } catch (Exception e) {
            System.out.println("❌ Error fetching jobs from Indeed: " + e.getMessage());
            e.printStackTrace();
        }

        return jobs;
    }
}
