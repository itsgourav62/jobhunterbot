package com.jobhunter.fetcher;

import com.jobhunter.model.Job;
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
 * TimesJobs.com job fetcher - Another major Indian job portal
 */
public class TimesJobsJobFetcher implements JobFetcher {

    private static final int MAX_JOBS = 15;

    @Override
    public List<Job> fetchJobs(List<String> skills, WebDriver driver) {
        List<Job> jobs = new ArrayList<>();

        try {
            String query = String.join("%20", skills);
            String searchUrl = "https://www.timesjobs.com/candidate/job-search.html?searchType=personalizedSearch&from=submit&txtKeywords=" + query + "&txtLocation=";
            driver.get(searchUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
            Thread.sleep(4000); // Times Jobs takes time to load

            JavascriptExecutor js = (JavascriptExecutor) driver;
            int totalJobsScraped = 0;
            int scrollAttempts = 0;
            final int MAX_SCROLL_ATTEMPTS = 3;

            while (totalJobsScraped < MAX_JOBS && scrollAttempts < MAX_SCROLL_ATTEMPTS) {
                List<WebElement> jobElements = driver.findElements(By.cssSelector(".clearfix.job-bx, .job-bx"));

                for (WebElement el : jobElements) {
                    if (totalJobsScraped >= MAX_JOBS) break;

                    try {
                        String title = "";
                        try {
                            title = el.findElement(By.cssSelector(".jobTitle, h2 a")).getText();
                        } catch (Exception e) {
                            title = el.findElement(By.cssSelector("h2")).getText();
                        }

                        String company = "";
                        try {
                            company = el.findElement(By.cssSelector(".jobTupleCompany, .company-name")).getText();
                        } catch (Exception e) {
                            company = "Times Jobs Company";
                        }

                        String link = "";
                        try {
                            WebElement linkElement = el.findElement(By.cssSelector("h2 a"));
                            link = linkElement.getAttribute("href");
                            if (link != null && !link.startsWith("http")) {
                                link = "https://www.timesjobs.com" + link;
                            }
                        } catch (Exception e) {
                            link = "https://www.timesjobs.com/candidate/job-search.html";
                        }

                        String description = "";
                        try {
                            description = el.findElement(By.cssSelector(".list-job-dtl, .job-description")).getText();
                        } catch (Exception ignored) {}

                        if (!title.isEmpty()) {
                            jobs.add(new Job("timesjobs_job" + (totalJobsScraped + 1), title, description, link, company));
                            totalJobsScraped++;
                        }

                    } catch (Exception innerEx) {
                        System.out.println("⚠️ Error scraping TimesJobs job: " + innerEx.getMessage());
                    }
                }

                // Scroll carefully for TimesJobs
                js.executeScript("window.scrollBy(0, 800)");
                Thread.sleep(3000);
                scrollAttempts++;
            }

            System.out.println("✅ Scraped " + jobs.size() + " jobs from TimesJobs.com");

        } catch (Exception e) {
            System.out.println("❌ Error fetching TimesJobs: " + e.getMessage());
        }

        return jobs;
    }
}
