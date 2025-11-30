package com.jobhunter.demo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple demonstration of JobHunter MVP functionality without Spring Boot
 */
public class JobHunterDemo {
    
    private static final Map<String, Map<String, Object>> profiles = new HashMap<>();
    private static final List<Map<String, Object>> jobs = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("=== JobHunter Bot MVP Demo ===");
        
        // Demo 1: Parse a sample resume
        String sampleResume = """
            John Doe
            john.doe@email.com
            Location: San Francisco, CA
            
            Senior Software Engineer with 5+ years of experience
            
            EXPERIENCE:
            - Led development of microservices using Java and Spring Boot
            - Built responsive web applications with React and TypeScript
            - Implemented CI/CD pipelines using Docker and AWS
            
            SKILLS: Java, Spring Boot, React, JavaScript, Docker, AWS, MySQL, Git
            """;
        
        System.out.println("\n1. Resume Parsing Demo:");
        Map<String, Object> profile = parseResume(sampleResume);
        String profileId = (String) profile.get("profileId");
        profiles.put(profileId, profile);
        
        System.out.println("Parsed Profile:");
        System.out.println("- Name: " + profile.get("name"));
        System.out.println("- Email: " + profile.get("email"));
        System.out.println("- Experience: " + profile.get("experience") + " years");
        System.out.println("- Skills: " + profile.get("skills"));
        
        // Demo 2: Add sample jobs
        System.out.println("\n2. Job Ingestion Demo:");
        addJob("Senior Java Developer", "TechCorp", Arrays.asList("Java", "Spring Boot", "MySQL"), 120000);
        addJob("Full Stack Engineer", "StartupInc", Arrays.asList("JavaScript", "React", "Node.js"), 110000);
        addJob("DevOps Engineer", "CloudCorp", Arrays.asList("Docker", "AWS", "Kubernetes"), 130000);
        
        System.out.println("Added " + jobs.size() + " jobs to the system");
        
        // Demo 3: Match candidate to jobs
        System.out.println("\n3. Job Matching Demo:");
        List<Map<String, Object>> matches = findMatches(profileId, 5);
        
        System.out.println("Top job matches for " + profile.get("name") + ":");
        for (int i = 0; i < matches.size(); i++) {
            Map<String, Object> match = matches.get(i);
            System.out.println((i + 1) + ". " + match.get("title") + " at " + match.get("company"));
            System.out.println("   Score: " + match.get("score") + " - " + match.get("reason"));
        }
        
        // Demo 4: Show statistics
        System.out.println("\n4. System Statistics:");
        System.out.println("- Total profiles: " + profiles.size());
        System.out.println("- Total jobs: " + jobs.size());
        System.out.println("- Profile ID: " + profileId);
        
        System.out.println("\n=== Demo Complete ===");
        System.out.println("This demonstrates the core JobHunter MVP functionality:");
        System.out.println("✅ Resume parsing (name, email, skills, experience)");
        System.out.println("✅ Job ingestion with required skills");
        System.out.println("✅ Intelligent job matching with scoring");
        System.out.println("✅ Explainable match results");
    }
    
    private static Map<String, Object> parseResume(String resumeText) {
        Map<String, Object> profile = new HashMap<>();
        
        profile.put("profileId", UUID.randomUUID().toString().substring(0, 8));
        profile.put("name", extractName(resumeText));
        profile.put("email", extractEmail(resumeText));
        profile.put("experience", extractExperience(resumeText));
        profile.put("skills", extractSkills(resumeText));
        profile.put("rawText", resumeText);
        
        return profile;
    }
    
    private static String extractName(String text) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() > 3 && trimmed.length() < 50 && 
                trimmed.matches("^[A-Za-z\\s.'-]+$") &&
                !trimmed.toLowerCase().contains("resume")) {
                return trimmed;
            }
        }
        return "Unknown";
    }
    
    private static String extractEmail(String text) {
        Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group() : "";
    }
    
    private static Integer extractExperience(String text) {
        Pattern pattern = Pattern.compile("(\\d+)\\+?\\s*(?:years?|yrs?)\\s+(?:of\\s+)?(?:experience|exp)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }
    
    private static List<String> extractSkills(String text) {
        String[] skillKeywords = {"Java", "Python", "JavaScript", "React", "Angular", "Vue", "Spring", "Spring Boot",
                                "Node.js", "Express", "Django", "Flask", "SQL", "MySQL", "PostgreSQL", "MongoDB",
                                "Docker", "Kubernetes", "AWS", "Azure", "GCP", "Git", "Jenkins", "CI/CD"};
        
        List<String> foundSkills = new ArrayList<>();
        String textLower = text.toLowerCase();
        
        for (String skill : skillKeywords) {
            if (textLower.contains(skill.toLowerCase())) {
                foundSkills.add(skill);
            }
        }
        
        return foundSkills;
    }
    
    private static void addJob(String title, String company, List<String> requiredSkills, int salary) {
        Map<String, Object> job = new HashMap<>();
        job.put("jobId", UUID.randomUUID().toString().substring(0, 8));
        job.put("title", title);
        job.put("company", company);
        job.put("requiredSkills", requiredSkills);
        job.put("salary", salary);
        jobs.add(job);
    }
    
    private static List<Map<String, Object>> findMatches(String profileId, int limit) {
        Map<String, Object> profile = profiles.get(profileId);
        if (profile == null) return new ArrayList<>();
        
        List<String> candidateSkills = (List<String>) profile.get("skills");
        List<Map<String, Object>> matches = new ArrayList<>();
        
        for (Map<String, Object> job : jobs) {
            List<String> requiredSkills = (List<String>) job.get("requiredSkills");
            
            // Calculate match score
            List<String> matchedSkills = new ArrayList<>();
            for (String skill : candidateSkills) {
                if (requiredSkills.contains(skill)) {
                    matchedSkills.add(skill);
                }
            }
            
            double score = requiredSkills.isEmpty() ? 0.5 : (double) matchedSkills.size() / requiredSkills.size();
            
            Map<String, Object> match = new HashMap<>();
            match.put("jobId", job.get("jobId"));
            match.put("title", job.get("title"));
            match.put("company", job.get("company"));
            match.put("score", Math.round(score * 100.0) / 100.0);
            match.put("matchedSkills", matchedSkills);
            match.put("reason", String.format("Matched %d/%d required skills: %s", 
                     matchedSkills.size(), requiredSkills.size(), 
                     String.join(", ", matchedSkills)));
            
            matches.add(match);
        }
        
        // Sort by score descending
        matches.sort((a, b) -> Double.compare((Double) b.get("score"), (Double) a.get("score")));
        
        return matches.stream().limit(limit).toList();
    }
}