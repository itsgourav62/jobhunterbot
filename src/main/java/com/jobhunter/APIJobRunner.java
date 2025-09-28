package com.jobhunter;

import com.jobhunter.analytics.AnalyticsService;
import com.jobhunter.config.AppConfig;
import com.jobhunter.fetcher.JobFetcherFactory;
import com.jobhunter.matcher.MatcherService;
import com.jobhunter.model.Job;
import com.jobhunter.model.Resume;
import com.jobhunter.notifier.NotifierService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API-only Job Runner - Perfect for CI/CD environments
 * Uses only API-based job fetchers, no browser automation
 * TOS-compliant and faster execution
 */
public class APIJobRunner {
    
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("🌐 API Job Hunter v1.0 - TOS Compliant");
        System.out.println("====================================");

        JobFetcherFactory jobFetcherFactory = null;
        NotifierService notifier = new NotifierService();

        try {
            // Create resume from environment variables or defaults
            Resume resume = new Resume(
                    System.getenv("JOB_HUNTER_NAME") != null ? System.getenv("JOB_HUNTER_NAME") : "Gourav Shaw",
                    System.getenv("JOB_HUNTER_EMAIL") != null ? System.getenv("JOB_HUNTER_EMAIL") : "gourav@example.com",
                    System.getenv("JOB_HUNTER_PHONE") != null ? System.getenv("JOB_HUNTER_PHONE") : "1234567890",
                    Arrays.asList("Java", "Spring", "React", "Python", "Node.js", "SQL", "JavaScript", "Docker", "Microservices"),
                    Arrays.asList("5+ years of software development experience", "Full-stack development", "System architecture"),
                    Collections.singletonList("Bachelor's in Computer Science")
            );

            System.out.println("👤 Using profile: " + resume.getName() + " (" + resume.getEmail() + ")");
            
            // Validate resume access
            if (!AppConfig.getInstance().validateResumeAccess()) {
                System.out.println("⚠️ Resume file not accessible - continuing without file upload capability");
                System.out.println("💡 Raw resume path: " + AppConfig.getInstance().getResumeUrl());
                System.out.println("💡 Make sure your Google Drive link is publicly accessible");
            }

            // Initialize factory and matcher
            jobFetcherFactory = new JobFetcherFactory();
            MatcherService matcherService = new MatcherService();

            // 1️⃣ Fetch jobs from API sources only (fast & reliable)
            System.out.println("🚀 Fetching jobs from API sources...");
            List<Job> jobs = jobFetcherFactory.fetchJobsFromAPIs(resume.getSkills());
            System.out.println("📊 Total jobs fetched: " + jobs.size());

            if (jobs.isEmpty()) {
                System.out.println("⚠️ No jobs found from API sources");
                return;
            }

            // 2️⃣ Match jobs
            System.out.println("🎯 Matching jobs against resume...");
            Map<Job, Integer> scoredJobs = matcherService.matchJobs(jobs, resume);

            // 3️⃣ Filter and sort top jobs
            int threshold = 50; // Lower threshold for API-only to get more results
            List<Map.Entry<Job, Integer>> topJobs = scoredJobs.entrySet().stream()
                    .filter(entry -> entry.getValue() >= threshold)
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(15) // Limit to top 15 jobs
                    .collect(Collectors.toList());

            System.out.println("🏆 Top matching jobs (score >= " + threshold + "): " + topJobs.size());

            // 4️⃣ Display results and generate analytics
            if (!topJobs.isEmpty()) {
                // Generate comprehensive analytics report
                AnalyticsService.JobSearchReport analyticsReport = 
                    AnalyticsService.generateReport(jobs, scoredJobs, 
                        topJobs.stream().map(Map.Entry::getKey).collect(Collectors.toList()), 
                        resume.getName());

                System.out.println("\n🏆 TOP MATCHING JOBS:");
                System.out.println("==========================================");

                int count = 1;
                for (Map.Entry<Job, Integer> entry : topJobs) {
                    Job job = entry.getKey();
                    int score = entry.getValue();

                    String jobInfo = String.format("%d. **%s** at **%s** (Score: %d%%)\n   🔗 %s\n",
                            count++, job.getTitle(), job.getCompany(), score, job.getUrl());
                    
                    System.out.println(jobInfo);
                    if (count > 10) break; // Show top 10 in console
                }

                // 5️⃣ Send enhanced notification with analytics
                String discordWebhook = AppConfig.getInstance().getDiscordWebhookUrl();
                if (discordWebhook != null && !discordWebhook.equals("bruh") && !discordWebhook.isEmpty()) {
                    System.out.println("\n📢 Sending Discord notification with analytics...");
                    String discordReport = analyticsReport.generateDiscordReport();
                    notifier.sendDiscordNotification(discordWebhook, discordReport);
                    System.out.println("✅ Discord notification sent!");
                } else {
                    System.out.println("\n⚠️ Discord webhook not configured - skipping notification");
                    System.out.println("💡 Set DISCORD_WEBHOOK_URL to receive notifications");
                }

                // Show full analytics report in console
                System.out.println("\n" + analyticsReport.generateTextReport());

            } else {
                System.out.println("😔 No jobs matched your criteria (score >= " + threshold + ")");
                
                // Send notification about no matches with basic analytics
                String discordWebhook = AppConfig.getInstance().getDiscordWebhookUrl();
                if (discordWebhook != null && !discordWebhook.equals("bruh") && !discordWebhook.isEmpty()) {
                    String message = "🤖 Job Hunter API Run - " + java.time.LocalDate.now() + 
                        "\n📊 " + jobs.size() + " jobs fetched, but none matched criteria (score >= " + threshold + ")" +
                        "\n💡 Consider adjusting your skills or lowering the threshold";
                    notifier.sendDiscordNotification(discordWebhook, message);
                }
            }

        } catch (Exception e) {
            System.out.println("\n❌ An error occurred:");
            e.printStackTrace();
            
            // Send error notification
            String discordWebhook = AppConfig.getInstance().getDiscordWebhookUrl();
            if (discordWebhook != null && !discordWebhook.equals("bruh")) {
                notifier.sendDiscordNotification(discordWebhook, 
                    "❌ Job Hunter API Run Failed - " + java.time.LocalDate.now() + 
                    "\nError: " + e.getMessage());
            }
            
        } finally {
            if (jobFetcherFactory != null) {
                jobFetcherFactory.shutdown();
            }
            AppConfig.getInstance().cleanup(); // Clean up temp files and resources
            System.out.println("✅ API Job Runner finished.");
            System.out.println("====================================");
        }
    }
}