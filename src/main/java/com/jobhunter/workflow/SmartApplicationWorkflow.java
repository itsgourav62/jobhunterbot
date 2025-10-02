package com.jobhunter.workflow;

import com.jobhunter.analytics.AnalyticsService;
import com.jobhunter.autofill.AutofillService;
import com.jobhunter.autofill.JobApplicationService;
import com.jobhunter.config.AppConfig;
import com.jobhunter.fetcher.JobFetcherFactory;
import com.jobhunter.matcher.MatcherService;
import com.jobhunter.model.Job;
import com.jobhunter.model.Resume;
import com.jobhunter.notifier.NotifierService;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SmartApplicationWorkflow {

    private final Resume resume;
    private final JobApplicationService jobApplicationService;
    private final NotifierService notifierService;
    private final String outputDir;

    private final WebDriver driver;

    public SmartApplicationWorkflow(Resume resume, JobApplicationService jobApplicationService, String outputDir, WebDriver driver) {
        this.resume = resume;
        this.jobApplicationService = jobApplicationService;
        this.notifierService = new NotifierService();
        this.outputDir = outputDir;
        this.driver = driver;

        try {
            Files.createDirectories(Paths.get(outputDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create output directory: " + outputDir, e);
        }
    }

    public void run() {
        System.out.println("🎯 SMART JOB APPLICATION WORKFLOW");
        System.out.println("==================================");
        System.out.println("👤 Profile: " + resume.getName() + " (" + resume.getEmail() + ")");

        JobFetcherFactory jobFetcherFactory = new JobFetcherFactory();

        try {
            // 1. Fetch jobs
            System.out.println("\n🔍 Collecting jobs...");
            List<Job> allJobs = jobFetcherFactory.fetchAllJobs(resume.getSkills(), driver);
            System.out.println("📊 Total jobs collected: " + allJobs.size());
            
            // Debug: Show first few jobs
            if (!allJobs.isEmpty()) {
                System.out.println("📋 Sample jobs found:");
                for (int i = 0; i < Math.min(3, allJobs.size()); i++) {
                    Job job = allJobs.get(i);
                    System.out.println("   " + (i+1) + ". " + job.getTitle() + " at " + job.getCompany());
                }
            } else {
                System.out.println("⚠️ No jobs were fetched! This might indicate an issue with job fetchers.");
            }

            // 2. Match jobs
            System.out.println("\n🧠 Analyzing job compatibility...");
            MatcherService matcherService = new MatcherService();
            Map<Job, Integer> scoredJobs = matcherService.matchJobs(allJobs, resume);

            // 3. Categorize jobs
            ApplicationTiers tiers = categorizeJobsByScore(scoredJobs);

            // 4. Generate reports
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
            generateApplicationFiles(tiers, timestamp);
            generateQuickApplyHTML(tiers, timestamp);

            // 5. Automated applications for top-tier jobs
            applyToPriorityJobs(tiers.priorityJobs);

            // 6. Analytics and notifications
            AnalyticsService.JobSearchReport report = AnalyticsService.generateReport(
                    allJobs, scoredJobs,
                    tiers.priorityJobs.stream().map(ApplicationJob::getJob).collect(Collectors.toList()),
                    resume.getName()
            );
            sendSmartNotification(tiers, report, timestamp);
            sendEmailNotification(tiers, timestamp);

            // 7. Console summary
            printApplicationSummary(tiers, report);

        } catch (Exception e) {
            System.err.println("❌ Error in Smart Application Workflow: " + e.getMessage());
            e.printStackTrace();
        } finally {
            jobFetcherFactory.shutdown();
            AppConfig.getInstance().cleanup();
        }
    }

    private void applyToPriorityJobs(List<ApplicationJob> priorityJobs) {
        if (priorityJobs.isEmpty()) {
            System.out.println("\n🤖 No priority jobs to apply to automatically.");
            return;
        }

        System.out.println("\n🤖 Starting automated applications for " + priorityJobs.size() + " priority jobs...");
        int appliedCount = 0;
        for (ApplicationJob appJob : priorityJobs) {
            boolean success = jobApplicationService.apply(appJob.getJob(), resume);
            if (success) {
                appliedCount++;
            }
            // Optional: Add a delay between applications
            try {
                Thread.sleep(3000); // 3-second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("\n🤖 Automated application process complete. Applied to " + appliedCount + " jobs.");
    }

    private ApplicationTiers categorizeJobsByScore(Map<Job, Integer> scoredJobs) {
        ApplicationTiers tiers = new ApplicationTiers();
        for (Map.Entry<Job, Integer> entry : scoredJobs.entrySet()) {
            ApplicationJob appJob = new ApplicationJob(entry.getKey(), entry.getValue());
            if (entry.getValue() >= 80) tiers.priorityJobs.add(appJob);
            else if (entry.getValue() >= 70) tiers.highPotentialJobs.add(appJob);
            else if (entry.getValue() >= 60) tiers.goodMatchJobs.add(appJob);
        }
        // Sort all tiers by score
        tiers.priorityJobs.sort((a, b) -> Integer.compare(b.score, a.score));
        tiers.highPotentialJobs.sort((a, b) -> Integer.compare(b.score, a.score));
        tiers.goodMatchJobs.sort((a, b) -> Integer.compare(b.score, a.score));
        return tiers;
    }

    private void generateApplicationFiles(ApplicationTiers tiers, String timestamp) throws IOException {
        Path filePath = Paths.get(outputDir, "job-applications-" + timestamp + ".md");
        StringBuilder md = new StringBuilder("# 🎯 Job Application Plan - ").append(timestamp).append("\n\n");
        md.append("**Candidate:** ").append(resume.getName()).append("\n\n");

        if (!tiers.priorityJobs.isEmpty()) {
            md.append("## 🔥 PRIORITY APPLICATIONS (80%+ Match)\n");
            for (ApplicationJob job : tiers.priorityJobs) md.append(formatJobForMarkdown(job));
        }
        if (!tiers.highPotentialJobs.isEmpty()) {
            md.append("\n## ⭐ HIGH POTENTIAL (70-79% Match)\n");
            for (ApplicationJob job : tiers.highPotentialJobs) md.append(formatJobForMarkdown(job));
        }

        Files.write(filePath, md.toString().getBytes());
        System.out.println("📝 Generated application plan: " + filePath);
    }

    private void generateQuickApplyHTML(ApplicationTiers tiers, String timestamp) throws IOException {
        Path filePath = Paths.get(outputDir, "quick-apply-" + timestamp + ".html");
        StringBuilder html = new StringBuilder("<!DOCTYPE html><html><head><title>Quick Apply</title><style>");
        html.append("body{font-family:sans-serif;background:#f4f4f9;margin:20px} .container{max-width:1000px;margin:auto;background:white;padding:20px;border-radius:8px}");
        html.append(".job{border:1px solid #ddd;padding:15px;margin-bottom:15px;border-radius:5px} .priority{border-left:5px solid #ff4500}");
        html.append(".high-potential{border-left:5px solid #ff8c00} .score{float:right;font-weight:bold} a{text-decoration:none;color:#007bff}");
        html.append("</style></head><body><div class='container'><h1>Quick Apply Dashboard</h1>");

        if (!tiers.priorityJobs.isEmpty()) {
            html.append("<h2>🔥 Priority Applications</h2>");
            for (ApplicationJob job : tiers.priorityJobs) html.append(formatJobForHTML(job, "priority"));
        }
        if (!tiers.highPotentialJobs.isEmpty()) {
            html.append("<h2>⭐ High Potential</h2>");
            for (ApplicationJob job : tiers.highPotentialJobs) html.append(formatJobForHTML(job, "high-potential"));
        }

        html.append("</div></body></html>");
        Files.write(filePath, html.toString().getBytes());
        System.out.println("🌐 Generated Quick Apply dashboard: " + filePath);
    }

    private String formatJobForMarkdown(ApplicationJob job) {
        return String.format("### %s at %s (%d%%)\n- **Apply Here:** [%s](%s)\n\n",
                job.job.getTitle(), job.job.getCompany(), job.score, job.job.getUrl(), job.job.getUrl());
    }

    private String formatJobForHTML(ApplicationJob job, String cssClass) {
        return String.format("<div class='job %s'><span class='score'>%d%%</span><h3>%s</h3><p>%s</p><a href='%s' target='_blank'>Apply Now</a></div>",
                cssClass, job.score, job.job.getTitle(), job.job.getCompany(), job.job.getUrl());
    }

    private void sendSmartNotification(ApplicationTiers tiers, AnalyticsService.JobSearchReport report, String timestamp) {
        String discordWebhook = AppConfig.getInstance().getDiscordWebhookUrl();
        if (discordWebhook != null && !discordWebhook.trim().isEmpty() && !discordWebhook.equals("bruh")) {
            StringBuilder message = new StringBuilder("🎯 **Smart Job Run Complete**\n");
            message.append(String.format("Analyzed: %d jobs, Avg Score: %.1f%%\n", report.getTotalJobsFetched(), report.getAverageScore()));
            message.append(String.format("🔥 Priority Jobs: %d\n", tiers.priorityJobs.size()));
            if (!tiers.priorityJobs.isEmpty()) {
                message.append("\n**Top Priority Jobs:**\n");
                for (int i = 0; i < Math.min(3, tiers.priorityJobs.size()); i++) {
                    ApplicationJob topJob = tiers.priorityJobs.get(i);
                    message.append(String.format("%d. %s at %s (%d%%) - %s\n", i + 1, topJob.job.getTitle(), topJob.job.getCompany(), topJob.score, topJob.job.getUrl()));
                }
            }
            notifierService.sendDiscordNotification(discordWebhook, message.toString());
        }
    }


    private void sendEmailNotification(ApplicationTiers tiers, String timestamp) {
        String toEmail = AppConfig.getInstance().getEmailTo();
        if (toEmail != null && !toEmail.trim().isEmpty()) {
            String subject = "🎯 Job Application Plan - " + timestamp;
            String htmlBody = generateEmailHtml(tiers, timestamp);
            notifierService.sendHtmlEmail(AppConfig.getInstance().getEmailFrom(), AppConfig.getInstance().getEmailPassword(), toEmail, subject, htmlBody);
        }
    }

    private String generateEmailHtml(ApplicationTiers tiers, String timestamp) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h1>Job Application Plan - ").append(timestamp).append("</h1>");
        if (!tiers.priorityJobs.isEmpty()) {
            html.append("<h2>🔥 Priority Applications</h2>");
            for (ApplicationJob job : tiers.priorityJobs) {
                html.append(String.format("<p><b>%s at %s (%d%%)</b><br><a href=\"%s\">Apply Now</a></p>",
                        job.job.getTitle(), job.job.getCompany(), job.score, job.job.getUrl()));
            }
        }
        if (!tiers.highPotentialJobs.isEmpty()) {
            html.append("<h2>⭐ High Potential</h2>");
            for (ApplicationJob job : tiers.highPotentialJobs) {
                html.append(String.format("<p><b>%s at %s (%d%%)</b><br><a href=\"%s\">Apply Now</a></p>",
                        job.job.getTitle(), job.job.getCompany(), job.score, job.job.getUrl()));
            }
        }
        html.append("</body></html>");
        return html.toString();
    }

    private void printApplicationSummary(ApplicationTiers tiers, AnalyticsService.JobSearchReport report) {
        System.out.println("\n✅ Workflow Complete!");
        System.out.println("====================");
        System.out.println("📊 Jobs Analyzed: " + report.getTotalJobsFetched());
        System.out.println("🔥 Priority Jobs Found: " + tiers.priorityJobs.size());
        System.out.println("⭐ High Potential Jobs: " + tiers.highPotentialJobs.size());
        System.out.println("\n💡 Next Steps:");
        System.out.println("1. Review the generated reports in the '" + outputDir + "' directory.");
        System.out.println("2. Check Discord for a summary notification.");
    }

    // Data classes
    public static class ApplicationTiers {
        public List<ApplicationJob> priorityJobs = new ArrayList<>();
        public List<ApplicationJob> highPotentialJobs = new ArrayList<>();
        public List<ApplicationJob> goodMatchJobs = new ArrayList<>();
    }

    public static class ApplicationJob {
        public final Job job;
        public final int score;

        public ApplicationJob(Job job, int score) {
            this.job = job;
            this.score = score;
        }

        public Job getJob() { return job; }
    }

    /**
     * Main method for running the Smart Application Workflow from command line or CI/CD
     */
    public static void main(String[] args) {
        System.out.println("🚀 Starting Smart Job Application Workflow...");
        
        try {
            // Initialize configuration
            AppConfig config = AppConfig.getInstance();
            
            // Debug configuration loading
            System.out.println("🔧 Configuration loaded:");
            System.out.println("   - Name: '" + config.getJobHunterName() + "'");
            System.out.println("   - Email: '" + config.getJobHunterEmail() + "'");
            System.out.println("   - Resume Path: '" + config.getResumePath() + "'");
            System.out.println("   - Discord Webhook: " + (config.getDiscordWebhookUrl().isEmpty() ? "Not configured" : "Configured"));
            
            // Create output directory
            String outputDir = "output";
            
            // Parse resume - use a fallback for CI/CD environments
            Resume resume;
            try {
                com.jobhunter.parser.ResumeParser parser = new com.jobhunter.parser.ResumeParser();
                String resumeUrl = config.getResumeUrl();
                if (resumeUrl != null && !resumeUrl.isEmpty()) {
                    System.out.println("📄 Attempting to parse resume from: " + resumeUrl);
                    resume = parser.parseFromUrl(resumeUrl);
                    System.out.println("✅ Resume parsed successfully");
                } else {
                    System.out.println("⚠️ No resume URL configured, using fallback profile");
                    resume = createFallbackResume(config);
                }
            } catch (Exception e) {
                System.out.println("⚠️ Resume parsing failed: " + e.getMessage());
                System.out.println("🔄 Using fallback profile with configured information");
                resume = createFallbackResume(config);
            }
            
            // Ensure resume has the configured information even after parsing
            if (resume.getName() == null || resume.getName().isEmpty()) {
                resume.setName(config.getJobHunterName());
            }
            if (resume.getEmail() == null || resume.getEmail().isEmpty()) {
                resume.setEmail(config.getJobHunterEmail());
            }
            
            System.out.println("✅ Final resume profile: " + resume.getName() + " (" + resume.getEmail() + ")");
            
            // Create browser factory and get driver (headless for CI/CD)
            WebDriver driver = null;
            
            try {
                // Check if we're in a headless environment
                boolean isHeadless = System.getenv("headless") != null || System.getenv("CI") != null || System.getenv("GITHUB_ACTIONS") != null;
                
                if (isHeadless) {
                    // Create headless Chrome driver for CI/CD
                    org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
                    options.addArguments("--headless=new");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--remote-debugging-port=9222");
                    options.addArguments("--window-size=1920,1080");
                    driver = new org.openqa.selenium.chrome.ChromeDriver(options);
                } else {
                    driver = com.jobhunter.BrowserFactory.getDriver("chrome");
                }
                
                // Initialize required services
                AutofillService autofillService = new AutofillService(driver);
                com.jobhunter.storage.JobRepository jobRepository = new com.jobhunter.storage.H2JobRepository("./jobhunter_db");
                String resumePath = config.getResumePath() != null ? config.getResumePath() : "resume.pdf";
                
                JobApplicationService jobApplicationService = new JobApplicationService(autofillService, jobRepository, resumePath);
                
                // Create and run workflow
                SmartApplicationWorkflow workflow = new SmartApplicationWorkflow(resume, jobApplicationService, outputDir, driver);
                workflow.run();
                
                System.out.println("✅ Smart Application Workflow completed successfully!");
                
            } finally {
                if (driver != null) {
                    try {
                        driver.quit();
                    } catch (Exception e) {
                        System.err.println("Warning: Error closing driver: " + e.getMessage());
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Smart Application Workflow failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Creates a fallback resume for CI/CD environments where resume parsing might fail
     */
    private static Resume createFallbackResume(AppConfig config) {
        Resume resume = new Resume();
        
        // Use config values if available, otherwise use defaults
        String name = config.getJobHunterName();
        String email = config.getJobHunterEmail();
        
        resume.setName(name != null && !name.isEmpty() ? name : "Job Hunter");
        resume.setEmail(email != null && !email.isEmpty() ? email : "jobhunter@example.com");
        
        // Add comprehensive skills for better job matching
        resume.getSkills().add("Java");
        resume.getSkills().add("Spring Boot");
        resume.getSkills().add("Software Engineering");
        resume.getSkills().add("Backend Development");
        resume.getSkills().add("API Development");
        resume.getSkills().add("Database");
        resume.getSkills().add("REST API");
        resume.getSkills().add("Microservices");
        resume.getSkills().add("JavaScript");
        resume.getSkills().add("Python");
        resume.getSkills().add("React");
        resume.getSkills().add("Node.js");
        resume.getSkills().add("SQL");
        resume.getSkills().add("MongoDB");
        resume.getSkills().add("Docker");
        resume.getSkills().add("Kubernetes");
        resume.getSkills().add("AWS");
        resume.getSkills().add("Git");
        resume.getSkills().add("DevOps");
        resume.getSkills().add("Full Stack");
        resume.getSkills().add("Frontend");
        resume.getSkills().add("Machine Learning");
        resume.getSkills().add("Data Science");
        
        System.out.println("🔄 Using fallback resume with " + resume.getSkills().size() + " skills for better job matching");
        
        return resume;
    }
}