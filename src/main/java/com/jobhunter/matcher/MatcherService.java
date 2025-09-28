package com.jobhunter.matcher;

import com.jobhunter.model.Job;
import com.jobhunter.model.Resume;

import java.util.*;
import java.util.stream.Collectors;

public class MatcherService {

    // Enhanced keywords for better matching
    private static final Map<String, List<String>> SKILL_SYNONYMS = Map.of(
        "java", Arrays.asList("java", "jvm", "spring", "springboot", "maven", "gradle"),
        "javascript", Arrays.asList("javascript", "js", "node", "nodejs", "react", "angular", "vue"),
        "python", Arrays.asList("python", "django", "flask", "fastapi", "pandas", "numpy"),
        "sql", Arrays.asList("sql", "mysql", "postgresql", "oracle", "database", "rdbms"),
        "react", Arrays.asList("react", "reactjs", "jsx", "redux", "nextjs"),
        "spring", Arrays.asList("spring", "springboot", "spring boot", "microservices")
    );

    private static final List<String> POSITIVE_KEYWORDS = Arrays.asList(
        "remote", "flexible", "competitive salary", "benefits", "growth", "startup",
        "senior", "lead", "architect", "full-time", "permanent"
    );

    private static final List<String> NEGATIVE_KEYWORDS = Arrays.asList(
        "unpaid", "intern", "temporary", "contract", "part-time", "volunteer",
        "entry level", "fresher", "trainee"
    );

    /**
     * Enhanced job matching with multiple scoring factors
     */
    public int matchJob(Job job, Resume resume) {
        int totalScore = 0;
        String jobTitle = job.getTitle() != null ? job.getTitle().toLowerCase() : "";
        String jobDescription = job.getDescription() != null ? job.getDescription().toLowerCase() : "";
        String combinedJobText = (jobTitle + " " + jobDescription).toLowerCase();

        // 1️⃣ Skill matching (60% weight)
        int skillScore = calculateSkillScore(combinedJobText, resume.getSkills());
        totalScore += (skillScore * 0.6);

        // 2️⃣ Experience matching (25% weight)
        int experienceScore = calculateExperienceScore(combinedJobText, resume.getExperiences());
        totalScore += (experienceScore * 0.25);

        // 3️⃣ Positive/Negative keyword analysis (15% weight)
        int keywordScore = calculateKeywordScore(combinedJobText);
        totalScore += (keywordScore * 0.15);

        return Math.min(100, Math.max(0, totalScore)); // Clamp between 0-100
    }

    private int calculateSkillScore(String jobText, List<String> skills) {
        if (skills == null || skills.isEmpty()) return 0;

        int matchedSkills = 0;
        int totalImportantMatches = 0;

        for (String skill : skills) {
            String skillLower = skill.toLowerCase();
            
            // Direct skill match
            if (jobText.contains(skillLower)) {
                matchedSkills++;
                totalImportantMatches += 2; // Direct match = 2 points
            }
            
            // Synonym matching for better accuracy
            List<String> synonyms = SKILL_SYNONYMS.get(skillLower);
            if (synonyms != null) {
                for (String synonym : synonyms) {
                    if (jobText.contains(synonym.toLowerCase())) {
                        totalImportantMatches += 1; // Synonym match = 1 point
                        break; // Count synonym only once per skill
                    }
                }
            }
        }

        // Calculate score: prioritize important matches but also consider coverage
        double coverageScore = (matchedSkills / (double) skills.size()) * 50;
        double importanceScore = Math.min(50, totalImportantMatches * 5);
        
        return (int) (coverageScore + importanceScore);
    }

    private int calculateExperienceScore(String jobText, List<String> experiences) {
        if (experiences == null || experiences.isEmpty()) return 50; // Neutral if no experience data

        // Look for experience-related keywords
        String[] expKeywords = {"experience", "years", "senior", "lead", "expert", "advanced"};
        String[] juniorKeywords = {"junior", "entry", "fresher", "trainee", "graduate"};

        boolean hasExpKeywords = Arrays.stream(expKeywords)
                .anyMatch(keyword -> jobText.contains(keyword));
        
        boolean hasJuniorKeywords = Arrays.stream(juniorKeywords)
                .anyMatch(keyword -> jobText.contains(keyword));

        // Simple experience scoring logic
        if (hasExpKeywords && !hasJuniorKeywords) return 80; // Senior role
        if (hasJuniorKeywords) return 30; // Junior role
        return 60; // Neutral/mid-level
    }

    private int calculateKeywordScore(String jobText) {
        int score = 50; // Start neutral

        // Add points for positive keywords
        for (String keyword : POSITIVE_KEYWORDS) {
            if (jobText.contains(keyword)) {
                score += 5;
            }
        }

        // Subtract points for negative keywords
        for (String keyword : NEGATIVE_KEYWORDS) {
            if (jobText.contains(keyword)) {
                score -= 8;
            }
        }

        return Math.min(100, Math.max(0, score));
    }

    /**
     * Batch job matching with performance optimization
     */
    public Map<Job, Integer> matchJobs(List<Job> jobs, Resume resume) {
        Map<Job, Integer> results = new HashMap<>();
        
        System.out.println("🧮 Analyzing " + jobs.size() + " jobs against your profile...");
        
        for (Job job : jobs) {
            int score = matchJob(job, resume);
            results.put(job, score);
        }

        // Print some analytics
        double avgScore = results.values().stream().mapToInt(Integer::intValue).average().orElse(0);
        long highScoreCount = results.values().stream().mapToInt(Integer::intValue).filter(score -> score >= 70).count();
        
        System.out.println("📈 Job matching completed:");
        System.out.println("   - Average match score: " + String.format("%.1f", avgScore) + "%");
        System.out.println("   - High-potential jobs (≥70%): " + highScoreCount);

        return results;
    }

    /**
     * Get top N jobs sorted by score
     */
    public List<Map.Entry<Job, Integer>> getTopJobs(Map<Job, Integer> scoredJobs, int limit) {
        return scoredJobs.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}

