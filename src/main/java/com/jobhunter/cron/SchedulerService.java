package com.jobhunter.cron;

import com.jobhunter.config.AppConfig;
import com.jobhunter.autofill.AutofillService;
import com.jobhunter.fetcher.JobFetcher;
import com.jobhunter.fetcher.NaukriJobFetcher;
import com.jobhunter.matcher.MatcherService;
import com.jobhunter.model.Job;
import com.jobhunter.model.Resume;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SchedulerService {

    private final JobFetcher jobFetcher;
    private final MatcherService matcherService;
    private final AutofillService autofillService;
    private final Resume resume;

    public SchedulerService(Resume resume, WebDriver driver) {
        this.resume = resume;
        this.jobFetcher = new NaukriJobFetcher(); // Can add more fetchers later
        this.matcherService = new MatcherService();
        this.autofillService = new AutofillService(driver);
    }

    public void runDailyJob() {
        System.out.println("📅 Starting daily job search...");

        // 1️⃣ Fetch jobs
        List<Job> jobs = jobFetcher.fetchJobs(resume.getSkills());
        System.out.println("Fetched " + jobs.size() + " jobs");

        // 2️⃣ Match jobs
        Map<Job, Integer> scoredJobs = matcherService.matchJobs(jobs, resume);

        // 3️⃣ Filter top jobs (e.g., score >= 60)
        List<Job> topJobs = scoredJobs.entrySet().stream()
                .filter(entry -> entry.getValue() >= 60)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        System.out.println("Top jobs to apply: " + topJobs.size());

        // 4️⃣ Apply to jobs
        for (Job job : topJobs) {
            System.out.println("\n🚀 Applying to: " + job.getTitle() + " at " + job.getCompany());
            autofillService.applyToJob(job.getUrl(), Map.of(
                    "name", resume.getName(),
                    "email", resume.getEmail(),
                    "phone", resume.getPhone()
            ), AppConfig.getResumePath());
        }

        System.out.println("\n✅ Daily job run complete!");
    }

    public void close() {
        autofillService.closeBrowser();
    }
}

