package com.jobhunter.service;

import com.jobhunter.model.CandidateProfile;
import com.jobhunter.model.JobPosting;
import com.jobhunter.model.MatchedJob;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MatchingService {

    private static final double SKILL_WEIGHT = 0.7;
    private static final double TITLE_WEIGHT = 0.3;

    public List<MatchedJob> matchAndRank(CandidateProfile profile, List<JobPosting> jobs) {
        List<MatchedJob> matchedJobs = new ArrayList<>();

        for (JobPosting job : jobs) {
            double skillScore = calculateSkillScore(profile, job);
            double titleScore = calculateTitleScore(profile, job);

            double totalScore = (skillScore * SKILL_WEIGHT) + (titleScore * TITLE_WEIGHT);

            if (totalScore > 0.1) {
                String reason = generateReason(skillScore, titleScore);
                matchedJobs.add(new MatchedJob(job, totalScore, reason));
            }
        }

        Collections.sort(matchedJobs);
        return matchedJobs;
    }

    private double calculateSkillScore(CandidateProfile profile, JobPosting job) {
        if (profile.skills().isEmpty() || job.description() == null) return 0;
        long matchCount = profile.skills().stream()
                .filter(skill -> job.description().toLowerCase().contains(skill.toLowerCase()))
                .count();
        return (double) matchCount / profile.skills().size();
    }

    private double calculateTitleScore(CandidateProfile profile, JobPosting job) {
        if (job.title() == null) return 0.0;
        return profile.targetTitles().stream()
                .anyMatch(title -> job.title().toLowerCase().contains(title.toLowerCase())) ? 1.0 : 0.0;
    }

    private String generateReason(double skillScore, double titleScore) {
        List<String> reasons = new ArrayList<>();
        if (skillScore > 0.5) reasons.add(String.format("Strong skill match (%.0f%%)", skillScore * 100));
        if (titleScore > 0) reasons.add("Relevant title");
        return "Reason: " + String.join(", ", reasons);
    }
}
