package com.jobhunter.cron;

import com.jobhunter.config.AppConfig;
import com.jobhunter.autofill.AutofillService;
import com.jobhunter.fetcher.JobFetcherFactory;
import com.jobhunter.matcher.MatcherService;
import com.jobhunter.model.Job;
import com.jobhunter.model.Resume;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SchedulerService {

    private final JobFetcherFactory jobFetcherFactory;
    private final MatcherService matcherService;
    private final AutofillService autofillService;
    private final Resume resume;

    public SchedulerService(Resume resume, WebDriver driver) {
        this.resume = resume;
        this.jobFetcherFactory = new JobFetcherFactory();
        this.matcherService = new MatcherService();
        this.autofillService = new AutofillService(driver);
    }

    public void runDailyJob() {
        System.out.println("📅 Starting daily job search...");

        // 1️⃣ Fetch jobs from all platforms
        List<Job> jobs = jobFetcherFactory.fetchAllJobs(resume.getSkills());
        System.out.println("📊 Total jobs fetched: " + jobs.size());

        // 2️⃣ Match jobs with improved scoring
        Map<Job, Integer> scoredJobs = matcherService.matchJobs(jobs, resume);

        // 3️⃣ Filter top jobs (configurable threshold)
        int threshold = 60; // Could be moved to config
        List<Job> topJobs = scoredJobs.entrySet().stream()
                .filter(entry -> entry.getValue() >= threshold)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Sort by score descending
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        System.out.println("🎯 Top matching jobs (score >= " + threshold + "): " + topJobs.size());

        // 4️⃣ Apply to jobs with rate limiting
        int appliedCount = 0;
        int maxApplications = 10; // Limit daily applications
        
        for (Job job : topJobs) {
            if (appliedCount >= maxApplications) {
                System.out.println("⏰ Reached daily application limit (" + maxApplications + ")");
                break;
            }
            
            System.out.println("\n🚀 Applying to: " + job.getTitle() + " at " + job.getCompany() + 
                             " (Score: " + scoredJobs.get(job) + ")");
            
            try {
                autofillService.applyToJob(job.getUrl(), Map.of(
                        "name", resume.getName(),
                        "email", resume.getEmail(),
                        "phone", resume.getPhone()
                ), AppConfig.getResumePath());
                
                appliedCount++;
                Thread.sleep(5000); // 5-second delay between applications
                
            } catch (Exception e) {
                System.out.println("⚠️ Failed to apply to " + job.getTitle() + ": " + e.getMessage());
            }
        }

        System.out.println("\n✅ Daily job run complete! Applied to " + appliedCount + " jobs.");
    }

    public void close() {
        if (jobFetcherFactory != null) {
            jobFetcherFactory.shutdown();
        }
        if (autofillService != null) {
            autofillService.closeBrowser();
        }
    }
}

