package com.jobhunter.workflow;

import com.jobhunter.analytics.AnalyticsService;
import com.jobhunter.config.AppConfig;
import com.jobhunter.fetcher.JobFetcherFactory;
import com.jobhunter.matcher.MatcherService;
import com.jobhunter.model.Job;
import com.jobhunter.model.Resume;
import com.jobhunter.notifier.NotifierService;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Smart Application Workflow - Collects job links and creates an optimized application process
 */
public class SmartApplicationWorkflow {
    
    public static void main(String[] args) {
        System.out.println("🎯 SMART JOB APPLICATION WORKFLOW");
        System.out.println("==================================");
        System.out.println("Collecting the best job links for streamlined applications...\n");

        JobFetcherFactory jobFetcherFactory = null;
        NotifierService notifier = new NotifierService();

        try {
            // Create resume from environment
            Resume resume = new Resume(
                    System.getenv("JOB_HUNTER_NAME") != null ? System.getenv("JOB_HUNTER_NAME") : "Gourav Shaw",
                    System.getenv("JOB_HUNTER_EMAIL") != null ? System.getenv("JOB_HUNTER_EMAIL") : "shawgourav62@gmail.com",
                    System.getenv("JOB_HUNTER_PHONE") != null ? System.getenv("JOB_HUNTER_PHONE") : "+91-0748719503",
                    Arrays.asList("Java", "Spring Boot", "React", "Python", "Node.js", "SQL", "Docker", "Kubernetes", "Microservices", "JavaScript"),
                    Arrays.asList("5+ years of full-stack development", "System architecture design", "Team leadership experience"),
                    Collections.singletonList("Bachelor's in Computer Science")
            );

            System.out.println("👤 Profile: " + resume.getName() + " (" + resume.getEmail() + ")");

            // Initialize services
            jobFetcherFactory = new JobFetcherFactory();
            MatcherService matcherService = new MatcherService();

            // 1️⃣ Fetch jobs from all available sources
            System.out.println("\n🔍 Collecting jobs from all sources...");
            List<Job> allJobs = jobFetcherFactory.fetchAllJobs(resume.getSkills());
            System.out.println("📊 Total jobs collected: " + allJobs.size());

            // 2️⃣ Intelligent job matching
            System.out.println("\n🧠 Analyzing job compatibility...");
            Map<Job, Integer> scoredJobs = matcherService.matchJobs(allJobs, resume);

            // 3️⃣ Create application tiers
            ApplicationTiers tiers = categorizeJobsByScore(scoredJobs);
            
            // 4️⃣ Generate application files
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
            
            generateApplicationFiles(tiers, resume, timestamp);
            generateQuickApplyHTML(tiers, resume, timestamp);
            
            // 5️⃣ Analytics and notifications
            AnalyticsService.JobSearchReport report = AnalyticsService.generateReport(
                allJobs, scoredJobs, 
                tiers.priorityJobs.stream().map(ApplicationJob::getJob).collect(Collectors.toList()), 
                resume.getName()
            );

            // 6️⃣ Send Discord notification with actionable links
            sendSmartNotification(notifier, tiers, report, timestamp);

            // 7️⃣ Console summary
            printApplicationSummary(tiers, report);

        } catch (Exception e) {
            System.err.println("❌ Error in Smart Application Workflow: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (jobFetcherFactory != null) {
                jobFetcherFactory.shutdown();
            }
            AppConfig.cleanup();
        }
    }

    private static ApplicationTiers categorizeJobsByScore(Map<Job, Integer> scoredJobs) {
        ApplicationTiers tiers = new ApplicationTiers();

        for (Map.Entry<Job, Integer> entry : scoredJobs.entrySet()) {
            Job job = entry.getKey();
            int score = entry.getValue();
            
            ApplicationJob appJob = new ApplicationJob(job, score);

            if (score >= 80) {
                tiers.priorityJobs.add(appJob);        // Must apply - excellent match
            } else if (score >= 70) {
                tiers.highPotentialJobs.add(appJob);   // Should apply - good match  
            } else if (score >= 60) {
                tiers.goodMatchJobs.add(appJob);       // Consider applying - decent match
            } else if (score >= 50) {
                tiers.considerJobs.add(appJob);        // Backup options
            }
        }

        // Sort each tier by score (highest first)
        tiers.priorityJobs.sort((a, b) -> Integer.compare(b.score, a.score));
        tiers.highPotentialJobs.sort((a, b) -> Integer.compare(b.score, a.score));
        tiers.goodMatchJobs.sort((a, b) -> Integer.compare(b.score, a.score));
        tiers.considerJobs.sort((a, b) -> Integer.compare(b.score, a.score));

        return tiers;
    }

    private static void generateApplicationFiles(ApplicationTiers tiers, Resume resume, String timestamp) throws IOException {
        // Generate markdown file for easy reference
        StringBuilder md = new StringBuilder();
        md.append("# 🎯 Job Application Plan - ").append(timestamp).append("\n\n");
        md.append("**Candidate:** ").append(resume.getName()).append(" (").append(resume.getEmail()).append(")\n");
        md.append("**Skills:** ").append(String.join(", ", resume.getSkills())).append("\n\n");

        // Priority Jobs (80%+)
        if (!tiers.priorityJobs.isEmpty()) {
            md.append("## 🔥 PRIORITY APPLICATIONS (80%+ Match)\n");
            md.append("**Action:** Apply TODAY - Excellent matches!\n\n");
            for (ApplicationJob job : tiers.priorityJobs) {
                md.append(formatJobForMarkdown(job)).append("\n");
            }
            md.append("\n---\n\n");
        }

        // High Potential (70-79%)
        if (!tiers.highPotentialJobs.isEmpty()) {
            md.append("## ⭐ HIGH POTENTIAL (70-79% Match)\n");
            md.append("**Action:** Apply this week - Strong matches!\n\n");
            for (ApplicationJob job : tiers.highPotentialJobs) {
                md.append(formatJobForMarkdown(job)).append("\n");
            }
            md.append("\n---\n\n");
        }

        // Good Match (60-69%)
        if (!tiers.goodMatchJobs.isEmpty()) {
            md.append("## 📊 GOOD MATCHES (60-69% Match)\n");
            md.append("**Action:** Consider applying - Decent fit\n\n");
            for (ApplicationJob job : tiers.goodMatchJobs) {
                md.append(formatJobForMarkdown(job)).append("\n");
            }
            md.append("\n---\n\n");
        }

        // Write to file
        String filename = "job-applications-" + timestamp + ".md";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(md.toString());
        }
        System.out.println("📝 Generated application plan: " + filename);
    }

    private static void generateQuickApplyHTML(ApplicationTiers tiers, Resume resume, String timestamp) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html><head>\n");
        html.append("<title>Quick Apply - Job Applications</title>\n");
        html.append("<style>\n");
        html.append("body{font-family:Arial,sans-serif;margin:20px;background:#f5f5f5}\n");
        html.append(".container{max-width:1200px;margin:0 auto;background:white;padding:20px;border-radius:8px}\n");
        html.append(".job-card{border:1px solid #ddd;margin:10px 0;padding:15px;border-radius:5px;background:#fff}\n");
        html.append(".priority{border-left:5px solid #ff4444;background:#fff5f5}\n");
        html.append(".high-potential{border-left:5px solid #ff8800;background:#fff8f0}\n");
        html.append(".good-match{border-left:5px solid #0088ff;background:#f0f8ff}\n");
        html.append(".score{float:right;background:#333;color:white;padding:5px 10px;border-radius:15px;font-weight:bold}\n");
        html.append(".apply-btn{background:#28a745;color:white;padding:10px 20px;text-decoration:none;border-radius:5px;display:inline-block;margin:5px 0}\n");
        html.append(".apply-btn:hover{background:#218838}\n");
        html.append("h1{color:#333} h2{color:#555} .company{color:#666;font-style:italic}\n");
        html.append("</style>\n</head><body>\n");

        html.append("<div class='container'>\n");
        html.append("<h1>🎯 Quick Apply Dashboard - ").append(timestamp).append("</h1>\n");
        html.append("<p><strong>Candidate:</strong> ").append(resume.getName()).append(" | ");
        html.append("<strong>Email:</strong> ").append(resume.getEmail()).append("</p>\n");

        // Priority jobs
        if (!tiers.priorityJobs.isEmpty()) {
            html.append("<h2>🔥 PRIORITY APPLICATIONS (").append(tiers.priorityJobs.size()).append(" jobs)</h2>\n");
            for (ApplicationJob job : tiers.priorityJobs) {
                html.append(formatJobForHTML(job, "priority")).append("\n");
            }
        }

        // High potential
        if (!tiers.highPotentialJobs.isEmpty()) {
            html.append("<h2>⭐ HIGH POTENTIAL (").append(tiers.highPotentialJobs.size()).append(" jobs)</h2>\n");
            for (ApplicationJob job : tiers.highPotentialJobs) {
                html.append(formatJobForHTML(job, "high-potential")).append("\n");
            }
        }

        // Good matches
        if (!tiers.goodMatchJobs.isEmpty()) {
            html.append("<h2>📊 GOOD MATCHES (").append(tiers.goodMatchJobs.size()).append(" jobs)</h2>\n");
            for (ApplicationJob job : tiers.goodMatchJobs) {
                html.append(formatJobForHTML(job, "good-match")).append("\n");
            }
        }

        html.append("</div>\n</body></html>");

        String filename = "quick-apply-" + timestamp + ".html";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(html.toString());
        }
        System.out.println("🌐 Generated Quick Apply dashboard: " + filename);
        System.out.println("💡 Open this file in your browser for easy job applications!");
    }

    private static String formatJobForMarkdown(ApplicationJob job) {
        return String.format("### %s at %s (%d%% match)\n" +
                "- **Company:** %s\n" +
                "- **Apply Here:** [%s](%s)\n" +
                "- **Match Score:** %d%%\n",
                job.job.getTitle(), job.job.getCompany(), job.score,
                job.job.getCompany(), "APPLY NOW", job.job.getUrl(), job.score);
    }

    private static String formatJobForHTML(ApplicationJob job, String cssClass) {
        return String.format(
                "<div class='job-card %s'>\n" +
                "<div class='score'>%d%%</div>\n" +
                "<h3>%s</h3>\n" +
                "<p class='company'>%s</p>\n" +
                "<a href='%s' target='_blank' class='apply-btn'>🚀 APPLY NOW</a>\n" +
                "</div>\n",
                cssClass, job.score, job.job.getTitle(), 
                job.job.getCompany(), job.job.getUrl());
    }

    private static void sendSmartNotification(NotifierService notifier, ApplicationTiers tiers, 
                                            AnalyticsService.JobSearchReport report, String timestamp) {
        String discordWebhook = AppConfig.getDiscordWebhookUrl();
        if (discordWebhook != null && !discordWebhook.equals("bruh") && !discordWebhook.isEmpty()) {
            
            StringBuilder message = new StringBuilder();
            message.append("🎯 **SMART JOB ALERTS** - ").append(timestamp).append("\\n\\n");
            
            // Priority jobs
            if (!tiers.priorityJobs.isEmpty()) {
                message.append("🔥 **PRIORITY APPLICATIONS** (").append(tiers.priorityJobs.size()).append(" jobs)\\n");
                for (int i = 0; i < Math.min(3, tiers.priorityJobs.size()); i++) {
                    ApplicationJob job = tiers.priorityJobs.get(i);
                    message.append("• **").append(job.job.getTitle()).append("** at ").append(job.job.getCompany())
                           .append(" (").append(job.score).append("%)\\n");
                }
                if (tiers.priorityJobs.size() > 3) {
                    message.append("• ... and ").append(tiers.priorityJobs.size() - 3).append(" more!\\n");
                }
                message.append("\\n");
            }

            // High potential
            if (!tiers.highPotentialJobs.isEmpty()) {
                message.append("⭐ **HIGH POTENTIAL** (").append(tiers.highPotentialJobs.size()).append(" jobs)\\n");
                message.append("\\n");
            }

            message.append("📊 **SUMMARY:**\\n");
            message.append("• Total analyzed: ").append(report.getTotalJobsFetched()).append(" jobs\\n");
            message.append("• Avg match score: ").append(String.format("%.1f", report.getAverageScore())).append("%\\n");
            message.append("• Priority jobs: ").append(tiers.priorityJobs.size()).append("\\n");
            message.append("• Files generated: quick-apply-").append(timestamp).append(".html\\n");

            notifier.sendDiscordNotification(discordWebhook, message.toString());
        }
    }

    private static void printApplicationSummary(ApplicationTiers tiers, AnalyticsService.JobSearchReport report) {
        System.out.println("\n🎯 SMART APPLICATION WORKFLOW COMPLETE!");
        System.out.println("==========================================");
        System.out.println("📊 Jobs Analyzed: " + report.getTotalJobsFetched());
        System.out.println("🔥 Priority Applications: " + tiers.priorityJobs.size() + " (80%+ match)");
        System.out.println("⭐ High Potential: " + tiers.highPotentialJobs.size() + " (70-79% match)");
        System.out.println("📊 Good Matches: " + tiers.goodMatchJobs.size() + " (60-69% match)");
        
        if (!tiers.priorityJobs.isEmpty()) {
            System.out.println("\n🚀 TOP 3 PRIORITY JOBS:");
            for (int i = 0; i < Math.min(3, tiers.priorityJobs.size()); i++) {
                ApplicationJob job = tiers.priorityJobs.get(i);
                System.out.println((i + 1) + ". " + job.job.getTitle() + " at " + 
                                 job.job.getCompany() + " (" + job.score + "%)");
                System.out.println("   🔗 " + job.job.getUrl());
            }
        }
        
        System.out.println("\n💡 Next Steps:");
        System.out.println("1. Open quick-apply-[timestamp].html in your browser");
        System.out.println("2. Start with Priority Applications (80%+ match)");
        System.out.println("3. Use the 'APPLY NOW' buttons for direct access");
        System.out.println("4. Check your Discord for mobile notifications");
    }

    // Data classes
    public static class ApplicationTiers {
        public List<ApplicationJob> priorityJobs = new java.util.ArrayList<>();      // 80%+
        public List<ApplicationJob> highPotentialJobs = new java.util.ArrayList<>(); // 70-79%
        public List<ApplicationJob> goodMatchJobs = new java.util.ArrayList<>();     // 60-69%  
        public List<ApplicationJob> considerJobs = new java.util.ArrayList<>();      // 50-59%
    }

    public static class ApplicationJob {
        public final Job job;
        public final int score;

        public ApplicationJob(Job job, int score) {
            this.job = job;
            this.score = score;
        }

        public Job getJob() { return job; }
        public int getScore() { return score; }
    }
}