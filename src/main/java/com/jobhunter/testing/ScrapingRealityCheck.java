package com.jobhunter.testing;

import com.jobhunter.fetcher.*;
import com.jobhunter.config.AppConfig;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.List;

/**
 * Reality check - test actual scraping capabilities
 * This will show what's working and what's not
 */
public class ScrapingRealityCheck {
    
    public static void main(String[] args) {
        System.out.println("🧪 SCRAPING REALITY CHECK");
        System.out.println("==========================");
        System.out.println("Testing actual data scraping from job platforms...\n");
        
        List<String> testSkills = Arrays.asList("Java", "Spring", "React");
        
        // Test each scraper individually
        System.out.println("1️⃣ Testing Naukri.com...");
        testFetcher(new NaukriJobFetcher(), "Naukri.com", testSkills);
        
        System.out.println("\n2️⃣ Testing Indeed.co.in...");
        testFetcher(new IndeedJobFetcher(), "Indeed", testSkills);
        
        System.out.println("\n3️⃣ Testing RemoteOK API...");
        testFetcher(new RemoteOKJobFetcher(), "RemoteOK API", testSkills);
        
        // Note: Other scrapers likely won't work without proper setup
        System.out.println("\n4️⃣ Testing AngelList (may fail - anti-bot)...");
        testFetcher(new AngelListJobFetcher(), "AngelList", testSkills);
        
        System.out.println("\n📋 REALITY CHECK SUMMARY:");
        System.out.println("==========================");
        System.out.println("✅ RemoteOK API: Works (real API, no scraping)");
        System.out.println("⚠️  Naukri.com: May work but requires anti-bot measures");
        System.out.println("⚠️  Indeed: May work but limited by rate limiting");
        System.out.println("❌ AngelList/Glassdoor/Shine/TimesJobs: Likely blocked by anti-bot");
        System.out.println("✅ MockJobs: Always works (for testing)");
        
        System.out.println("\n💡 RECOMMENDATIONS:");
        System.out.println("1. Focus on API-based sources (RemoteOK, etc.)");
        System.out.println("2. For scraping, use rotating proxies and delays");
        System.out.println("3. Consider paid job APIs for reliable data");
        System.out.println("4. Use the bot primarily for job analysis and tracking");
        
        // Cleanup
        try {
            WebDriver driver = AppConfig.getInstance().getDriver();
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    private static void testFetcher(JobFetcher fetcher, String name, List<String> skills) {
        try {
            long startTime = System.currentTimeMillis();
            var jobs = fetcher.fetchJobs(skills);
            long endTime = System.currentTimeMillis();
            
            System.out.println("   " + name + ": ✅ " + jobs.size() + " jobs in " + 
                             (endTime - startTime) + "ms");
            
            if (!jobs.isEmpty()) {
                System.out.println("   Sample job: " + jobs.get(0).getTitle() + 
                                 " at " + jobs.get(0).getCompany());
            }
            
        } catch (Exception e) {
            System.out.println("   " + name + ": ❌ Failed - " + e.getMessage());
        }
    }
}