package com.jobhunter.matcher;

import com.jobhunter.model.Job;
import com.jobhunter.model.Resume;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatcherService {

    public Map<Job, Integer> matchJobs(List<Job> jobs, Resume resume) {
        Map<Job, Integer> matchedJobs = new HashMap<>();
        System.out.println("🧮 Analyzing " + jobs.size() + " jobs against your profile...");

        for (Job job : jobs) {
            int score = calculateMatchScore(job, resume);
            if (score > 50) { // Minimum threshold
                matchedJobs.put(job, score);
            }
        }

        System.out.println("📈 Job matching completed:");
        System.out.println("   - Average match score: " + matchedJobs.values().stream().mapToInt(Integer::intValue).average().orElse(0.0));
        System.out.println("   - High-potential jobs (≥70%): " + matchedJobs.values().stream().filter(s -> s >= 70).count());

        return matchedJobs;
    }

    private int calculateMatchScore(Job job, Resume resume) {
        int score = 0;

        String jobDescription = job.getDescription() != null ? job.getDescription().toLowerCase() : "";
        String jobTitle = job.getTitle() != null ? job.getTitle().toLowerCase() : "";

        // 1. Keyword matching (60% of score)
        // Simple keyword matching for skills
        int keywordScore = 0;
        for (String skill : resume.getSkills()) {
            if (jobDescription.contains(skill.toLowerCase()) ||
                jobTitle.contains(skill.toLowerCase())) {
                keywordScore += 15; // Each skill match adds to score
            }
        }
        score += Math.min(60, keywordScore); // Cap keyword score at 60

        // 2. Experience matching (20% of score)
        // Check if any experience keyword from resume is in job description
        boolean experienceMatch = resume.getExperiences().stream()
                .anyMatch(exp -> jobDescription.contains(exp.toLowerCase()));
        if (experienceMatch) {
            score += 20;
        }

        // 3. Education matching (20% of score)
        // Check if any education keyword from resume is in job description
        boolean educationMatch = resume.getEducation().stream()
                .anyMatch(edu -> jobDescription.contains(edu.toLowerCase()));
        if (educationMatch) {
            score += 20;
        }

        // Bonus for title match (if primary skill is in title)
        if (!resume.getSkills().isEmpty() && jobTitle.contains(resume.getSkills().get(0).toLowerCase())) {
            score += 10;
        }

        return Math.min(100, score); // Cap score at 100
    }
}

