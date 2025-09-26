package com.jobhunter.matcher;

import com.jobhunter.model.Job;
import com.jobhunter.model.Resume;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatcherService {

    /**
     * Match a resume against a single job.
     * Returns a score (0-100) based on how well the resume fits.
     */
    public int matchJob(Job job, Resume resume) {
        int score = 0;
        String jobTitle = job.getTitle().toLowerCase();
        String jobDescription = job.getDescription().toLowerCase();

        // Simple example: count matching skills
        List<String> skills = resume.getSkills();
        if (skills != null && !skills.isEmpty()) {
            int matchedSkills = 0;
            for (String skill : skills) {
                if (jobDescription.contains(skill.toLowerCase()) || jobTitle.contains(skill.toLowerCase())) {
                    matchedSkills++;
                }
            }
            score = (int) ((matchedSkills / (double) skills.size()) * 100);
        }

        // TODO: Add experience & education matching
        return score;
    }

    /**
     * Match multiple jobs and return a map of Job -> score
     */
    public Map<Job, Integer> matchJobs(List<Job> jobs, Resume resume) {
        Map<Job, Integer> results = new HashMap<>();
        for (Job job : jobs) {
            int score = matchJob(job, resume);
            results.put(job, score);
        }
        return results;
    }
}

