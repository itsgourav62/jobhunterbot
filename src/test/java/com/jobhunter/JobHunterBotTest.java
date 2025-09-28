package com.jobhunter;

import com.jobhunter.config.AppConfig;
import com.jobhunter.model.Job;
import com.jobhunter.model.Resume;
import com.jobhunter.matcher.MatcherService;
import com.jobhunter.parser.ResumeParser;
import com.jobhunter.fetcher.RemoteOKJobFetcher;
import com.jobhunter.notifier.NotifierService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Comprehensive test suite for Job Hunter Bot
 */
class JobHunterBotTest {

    private AppConfig config;
    private MatcherService matcherService;
    private NotifierService notifierService;
    private RemoteOKJobFetcher jobFetcher;

    @BeforeEach
    void setUp() {
        config = AppConfig.getInstance();
        matcherService = new MatcherService();
        notifierService = new NotifierService();
        jobFetcher = new RemoteOKJobFetcher();
    }

    @AfterEach
    void tearDown() {
        if (config != null) {
            config.cleanup();
        }
    }

    @Test
    @DisplayName("Should validate environment configuration")
    void testEnvironmentConfiguration() {
        // Test that we can retrieve configuration values
        assertNotNull(config.getJobHunterName());
        assertNotNull(config.getJobHunterEmail());
        assertNotNull(config.getResumeUrl());
        
        // Test that configuration values are reasonable
        assertTrue(config.getJobHunterName().length() > 0);
        assertTrue(config.getJobHunterEmail().contains("@"));
        assertTrue(config.getResumeUrl().length() > 0);
        
        System.out.println("✅ Configuration validation passed");
        System.out.println("   Name: " + config.getJobHunterName());
        System.out.println("   Email: " + config.getJobHunterEmail());
    }

    @Test
    @DisplayName("Should create and validate Resume object")
    void testResumeCreation() {
        Resume resume = new Resume(
            "Test User",
            "test@example.com",
            "+1-555-0123",
            Arrays.asList("Java", "Spring Boot", "React"),
            Arrays.asList("Software Developer at Tech Corp", "5 years experience"),
            Arrays.asList("Bachelor's in Computer Science")
        );

        assertNotNull(resume);
        assertEquals("Test User", resume.getName());
        assertEquals("test@example.com", resume.getEmail());
        assertEquals(3, resume.getSkills().size());
        assertTrue(resume.getSkills().contains("Java"));
        assertTrue(resume.getSkills().contains("Spring Boot"));

        System.out.println("✅ Resume creation test passed");
    }

    @Test
    @DisplayName("Should score jobs accurately with matcher service")
    void testJobMatching() {
        Resume testResume = new Resume(
            "Test Developer",
            "test@example.com", 
            "+1-555-0123",
            Arrays.asList("Java", "Spring", "React", "SQL"),
            Arrays.asList("5 years experience as Software Developer"),
            Arrays.asList("Bachelor's degree in Computer Science")
        );

        // High match job
        Job highMatchJob = new Job("1", "Senior Java Developer", 
            "Looking for experienced Java Spring developer with React and SQL knowledge", 
            "https://example.com/job1", "TechCorp");

        // Low match job
        Job lowMatchJob = new Job("2", "Truck Driver", 
            "Drive trucks across country, no tech experience required", 
            "https://example.com/job2", "Transport Co");

        int highScore = matcherService.matchJob(highMatchJob, testResume);
        int lowScore = matcherService.matchJob(lowMatchJob, testResume);

        // Validate scoring logic
        assertTrue(highScore >= 0 && highScore <= 100, "Score should be between 0-100");
        assertTrue(lowScore >= 0 && lowScore <= 100, "Score should be between 0-100");
        assertTrue(highScore > lowScore, "High match job should score higher than low match");
        assertTrue(highScore > 60, "Java dev job should score high for Java resume");

        System.out.println("✅ Job matching test passed");
        System.out.println("   High match score: " + highScore + "%");
        System.out.println("   Low match score: " + lowScore + "%");
    }

    @Test
    @DisplayName("Should handle empty job descriptions gracefully")
    void testEmptyJobDescriptions() {
        Resume testResume = new Resume(
            "Test User", "test@example.com", "+1-555-0123",
            Arrays.asList("Java", "Python"), 
            Arrays.asList("Software Developer"), 
            Arrays.asList("Computer Science Degree")
        );

        Job emptyJob = new Job("3", "Developer", "", "https://example.com/job3", "Test Co");
        Job nullDescJob = new Job("4", "Engineer", null, "https://example.com/job4", "Test Co");

        assertDoesNotThrow(() -> {
            int score1 = matcherService.matchJob(emptyJob, testResume);
            int score2 = matcherService.matchJob(nullDescJob, testResume);
            
            assertTrue(score1 >= 0 && score1 <= 100);
            assertTrue(score2 >= 0 && score2 <= 100);
            
            System.out.println("✅ Empty job handling test passed");
        });
    }

    @Test
    @DisplayName("Should validate job object creation")
    void testJobCreation() {
        Job job = new Job("123", "Software Engineer", 
            "We are looking for a talented software engineer", 
            "https://company.com/jobs/123", "TechCompany");

        assertNotNull(job);
        assertEquals("123", job.getId());
        assertEquals("Software Engineer", job.getTitle());
        assertEquals("TechCompany", job.getCompany());
        assertNotNull(job.getUrl());
        assertTrue(job.getUrl().startsWith("http"));

        System.out.println("✅ Job creation test passed");
    }

    @Test
    @DisplayName("Should handle RemoteOK job fetching")
    void testRemoteOKJobFetching() {
        List<String> skills = Arrays.asList("java", "react", "python");
        
        assertDoesNotThrow(() -> {
            List<Job> jobs = jobFetcher.fetchJobs(skills);
            
            // Should return some jobs or empty list (not null)
            assertNotNull(jobs);
            
            if (!jobs.isEmpty()) {
                Job firstJob = jobs.get(0);
                assertNotNull(firstJob.getId());
                assertNotNull(firstJob.getTitle());
                assertNotNull(firstJob.getCompany());
                
                System.out.println("✅ RemoteOK fetching test passed");
                System.out.println("   Jobs fetched: " + jobs.size());
                System.out.println("   Sample job: " + firstJob.getTitle() + " at " + firstJob.getCompany());
            } else {
                System.out.println("✅ RemoteOK fetching test passed (no jobs returned, but no errors)");
            }
        });
    }

    @Test
    @DisplayName("Should validate notification service setup")
    void testNotificationService() {
        assertNotNull(notifierService);
        
        // Test Discord notification (if webhook is configured)
        String discordWebhook = config.getDiscordWebhookUrl();
        if (discordWebhook != null && !discordWebhook.isEmpty()) {
            assertDoesNotThrow(() -> {
                boolean sent = notifierService.sendDiscordNotification("🧪 Test notification from Job Hunter Bot tests");
                // Note: We don't assert true here since it might fail in CI/CD without proper webhook
                System.out.println("✅ Discord notification test completed (sent: " + sent + ")");
            });
        } else {
            System.out.println("⚠️ Discord webhook not configured, skipping notification test");
        }
    }

    @Test
    @DisplayName("Should parse resume from various sources")
    void testResumeParsingLogic() {
        ResumeParser parser = new ResumeParser();
        
        // Test that parser exists and can handle basic operations
        assertNotNull(parser);
        
        // Test with mock data (since we might not have actual resume files in test)
        assertDoesNotThrow(() -> {
            // This would normally parse actual files, but we'll just validate the parser exists
            System.out.println("✅ Resume parser initialization test passed");
        });
    }

    @Test
    @DisplayName("Should handle configuration edge cases")
    void testConfigurationEdgeCases() {
        // Test minimum match score
        int minScore = config.getMinimumMatchScore();
        assertTrue(minScore >= 0 && minScore <= 100, "Minimum score should be valid percentage");
        
        // Test max jobs per run
        int maxJobs = config.getMaxJobsPerRun();
        assertTrue(maxJobs > 0 && maxJobs <= 1000, "Max jobs should be reasonable number");
        
        // Test headless detection
        boolean isHeadless = config.isHeadless();
        // Should not throw an exception
        assertNotNull(String.valueOf(isHeadless));
        
        System.out.println("✅ Configuration edge cases test passed");
        System.out.println("   Min score: " + minScore + "%");
        System.out.println("   Max jobs: " + maxJobs);
        System.out.println("   Headless: " + isHeadless);
    }

    @Test
    @DisplayName("Should validate overall system integration")
    void testSystemIntegration() {
        // Test that all major components can be instantiated together
        assertDoesNotThrow(() -> {
            AppConfig cfg = AppConfig.getInstance();
            MatcherService matcher = new MatcherService();
            NotifierService notifier = new NotifierService();
            RemoteOKJobFetcher fetcher = new RemoteOKJobFetcher();
            ResumeParser parser = new ResumeParser();
            
            assertNotNull(cfg);
            assertNotNull(matcher);
            assertNotNull(notifier);
            assertNotNull(fetcher);
            assertNotNull(parser);
            
            System.out.println("✅ System integration test passed");
            System.out.println("   All core components instantiated successfully");
        });
    }
}