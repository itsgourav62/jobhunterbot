package com.jobhunter.fetcher;

import com.jobhunter.model.Job;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Mock Job Fetcher for testing and demonstration
 * Generates realistic-looking job listings for development/testing
 */
public class MockJobFetcher implements JobFetcher {

    private static final int MAX_JOBS = 15;
    private final Random random = new Random();

    private static final String[] COMPANIES = {
        "TechCorp", "InnovateLabs", "DigitalSoft", "CloudTech Solutions", "StartupXYZ",
        "DataSystems Inc", "WebDev Pro", "MobileTech", "AI Innovations", "CyberSoft",
        "NextGen Technologies", "SmartCode", "DevOps Masters", "FullStack Corp", "CodeCrafters"
    };

    private static final String[] JOB_TITLES = {
        "Senior Java Developer", "Full Stack Developer", "React Developer", 
        "Python Software Engineer", "Backend Developer", "Frontend Developer",
        "DevOps Engineer", "Data Scientist", "Software Architect", "Lead Developer",
        "Spring Boot Developer", "Node.js Developer", "Machine Learning Engineer",
        "Cloud Engineer", "Database Developer"
    };

    private static final String[] LOCATIONS = {
        "Bangalore", "Mumbai", "Delhi", "Hyderabad", "Chennai", "Pune", "Remote", 
        "Gurgaon", "Noida", "Kolkata", "Ahmedabad", "Kochi"
    };

    @Override
    public List<Job> fetchJobs(List<String> skills) {
        List<Job> jobs = new ArrayList<>();
        
        System.out.println("🧪 Generating " + MAX_JOBS + " mock jobs for testing...");
        
        for (int i = 0; i < MAX_JOBS; i++) {
            String title = JOB_TITLES[random.nextInt(JOB_TITLES.length)];
            String company = COMPANIES[random.nextInt(COMPANIES.length)];
            String location = LOCATIONS[random.nextInt(LOCATIONS.length)];
            
            // Generate a realistic job description with skills
            String description = generateJobDescription(title, skills, location);
            String url = "https://example-jobs.com/job/" + (i + 1);
            
            jobs.add(new Job("mock_job_" + (i + 1), title, description, url, company));
        }
        
        System.out.println("✅ Generated " + jobs.size() + " mock jobs for testing");
        return jobs;
    }

    private String generateJobDescription(String title, List<String> userSkills, String location) {
        StringBuilder desc = new StringBuilder();
        
        desc.append("We are looking for an experienced ").append(title)
            .append(" to join our dynamic team in ").append(location).append(". ");
        
        desc.append("Key responsibilities include developing high-quality software solutions, ");
        desc.append("collaborating with cross-functional teams, and contributing to technical decisions. ");
        
        desc.append("Required skills: ");
        
        // Include some user skills to ensure matches
        List<String> requiredSkills = new ArrayList<>();
        if (userSkills != null && !userSkills.isEmpty()) {
            // Add 2-3 user skills
            for (int i = 0; i < Math.min(3, userSkills.size()); i++) {
                requiredSkills.add(userSkills.get(i));
            }
        }
        
        // Add some random technical skills
        String[] commonSkills = {"Git", "REST API", "Microservices", "Agile", "Docker", 
                               "Kubernetes", "CI/CD", "Unit Testing", "Problem Solving"};
        for (int i = 0; i < 3; i++) {
            requiredSkills.add(commonSkills[random.nextInt(commonSkills.length)]);
        }
        
        desc.append(String.join(", ", requiredSkills)).append(". ");
        
        // Add experience requirement
        int experience = 2 + random.nextInt(6); // 2-7 years
        desc.append("Experience: ").append(experience).append("+ years. ");
        
        // Add some benefits
        String[] benefits = {"Competitive salary", "Remote work options", "Health insurance", 
                           "Professional development", "Flexible hours", "Stock options"};
        desc.append("Benefits: ").append(benefits[random.nextInt(benefits.length)])
            .append(", ").append(benefits[random.nextInt(benefits.length)]).append(".");
        
        return desc.toString();
    }
}