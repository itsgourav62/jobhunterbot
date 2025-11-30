package com.jobhunter.matcher;

import com.jobhunter.dto.CandidateDto;
import com.jobhunter.dto.JobDto;
import com.jobhunter.dto.JobMatchDto;
import com.jobhunter.entity.Candidate;
import com.jobhunter.entity.Job;
import com.jobhunter.entity.JobMatch;
import com.jobhunter.service.SkillNormalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobMatchingEngine {
    
    @Autowired
    private SkillNormalizationService skillNormalizationService;
    
    @Value("${jobhunter.matching.skill-weight:0.4}")
    private double skillWeight;
    
    @Value("${jobhunter.matching.experience-weight:0.25}")
    private double experienceWeight;
    
    @Value("${jobhunter.matching.title-weight:0.2}")
    private double titleWeight;
    
    @Value("${jobhunter.matching.location-weight:0.1}")
    private double locationWeight;
    
    @Value("${jobhunter.matching.recency-weight:0.05}")
    private double recencyWeight;
    
    public JobMatchDto calculateMatch(CandidateDto candidate, JobDto job) {
        JobMatchDto match = new JobMatchDto();
        match.setCandidateId(candidate.getId());
        match.setCandidateName(candidate.getName());
        match.setJobId(job.getId());
        match.setJobTitle(job.getTitle());
        match.setCompany(job.getCompany());
        match.setLocation(job.getLocation());
        match.setJobUrl(job.getJobUrl());
        match.setApplyUrl(job.getApplyUrl());
        
        // Calculate component scores
        double skillScore = calculateSkillScore(candidate, job);
        double expScore = calculateExperienceScore(candidate, job);
        double titleScore = calculateTitleScore(candidate, job);
        double locScore = calculateLocationScore(candidate, job);
        double recScore = calculateRecencyScore(job);
        
        // Set component scores
        match.setSkillScore(skillScore);
        match.setExperienceScore(expScore);
        match.setTitleScore(titleScore);
        match.setLocationScore(locScore);
        match.setRecencyScore(recScore);
        
        // Calculate overall weighted score
        double overallScore = (skillScore * skillWeight) +
                             (expScore * experienceWeight) +
                             (titleScore * titleWeight) +
                             (locScore * locationWeight) +
                             (recScore * recencyWeight);
        
        match.setOverallScore(Math.round(overallScore * 100.0) / 100.0);
        
        // Calculate matched and missing skills
        SkillMatchResult skillResult = calculateSkillMatch(candidate, job);
        match.setMatchedSkills(skillResult.getMatchedSkills());
        match.setMissingRequiredSkills(skillResult.getMissingSkills());
        
        // Generate explanation
        match.setExplanation(generateExplanation(match, skillResult));
        
        return match;
    }
    
    public List<JobMatchDto> calculateMatches(CandidateDto candidate, List<JobDto> jobs) {
        return jobs.stream()
                .map(job -> calculateMatch(candidate, job))
                .sorted((m1, m2) -> Double.compare(m2.getOverallScore(), m1.getOverallScore()))
                .collect(Collectors.toList());
    }
    
    private double calculateSkillScore(CandidateDto candidate, JobDto job) {
        List<String> candidateSkills = candidate.getSkills() != null ? 
                candidate.getSkills() : new ArrayList<>();
        List<String> requiredSkills = job.getRequiredSkills() != null ? 
                job.getRequiredSkills() : new ArrayList<>();
        List<String> preferredSkills = job.getPreferredSkills() != null ? 
                job.getPreferredSkills() : new ArrayList<>();
        
        if (requiredSkills.isEmpty()) {
            return 0.5; // Neutral score if no requirements
        }
        
        // Normalize all skills
        Set<String> normalizedCandidateSkills = new HashSet<>(
                skillNormalizationService.normalizeSkills(candidateSkills));
        Set<String> normalizedRequiredSkills = new HashSet<>(
                skillNormalizationService.normalizeSkills(requiredSkills));
        Set<String> normalizedPreferredSkills = new HashSet<>(
                skillNormalizationService.normalizeSkills(preferredSkills));
        
        // Calculate required skills match
        long matchedRequired = normalizedRequiredSkills.stream()
                .mapToLong(skill -> normalizedCandidateSkills.contains(skill) ? 1 : 0)
                .sum();
        
        double requiredScore = (double) matchedRequired / normalizedRequiredSkills.size();
        
        // Calculate preferred skills bonus (max 0.2 additional)
        if (!normalizedPreferredSkills.isEmpty()) {
            long matchedPreferred = normalizedPreferredSkills.stream()
                    .mapToLong(skill -> normalizedCandidateSkills.contains(skill) ? 1 : 0)
                    .sum();
            double preferredBonus = Math.min(0.2, 
                    (double) matchedPreferred / normalizedPreferredSkills.size() * 0.2);
            requiredScore += preferredBonus;
        }
        
        return Math.min(1.0, requiredScore);
    }
    
    private double calculateExperienceScore(CandidateDto candidate, JobDto job) {
        Integer candidateExp = candidate.getYearsExperience();
        Integer minExp = job.getMinExperience();
        Integer maxExp = job.getMaxExperience();
        
        if (candidateExp == null) {
            return 0.5; // Neutral if unknown
        }
        
        if (minExp == null && maxExp == null) {
            return 0.5; // Neutral if job has no requirements
        }
        
        // Under-qualified
        if (minExp != null && candidateExp < minExp) {
            int gap = minExp - candidateExp;
            return Math.max(0.1, 1.0 - (gap * 0.15)); // Penalty for each missing year
        }
        
        // Over-qualified  
        if (maxExp != null && candidateExp > maxExp) {
            int excess = candidateExp - maxExp;
            return Math.max(0.6, 1.0 - (excess * 0.05)); // Smaller penalty for over-qualification
        }
        
        // Perfect fit
        return 1.0;
    }
    
    private double calculateTitleScore(CandidateDto candidate, JobDto job) {
        List<String> candidateTitles = candidate.getJobTitles();
        String jobTitle = job.getTitle();
        
        if (candidateTitles == null || candidateTitles.isEmpty() || jobTitle == null) {
            return 0.5;
        }
        
        String jobTitleLower = jobTitle.toLowerCase();
        
        return candidateTitles.stream()
                .mapToDouble(title -> calculateStringSimilarity(title.toLowerCase(), jobTitleLower))
                .max()
                .orElse(0.0);
    }
    
    private double calculateLocationScore(CandidateDto candidate, JobDto job) {
        String candidateLocation = candidate.getLocation();
        String jobLocation = job.getLocation();
        Boolean remoteOk = job.getRemoteOk();
        
        // Remote job gets high location score
        if (Boolean.TRUE.equals(remoteOk) || 
            (jobLocation != null && jobLocation.toLowerCase().contains("remote"))) {
            return 1.0;
        }
        
        if (candidateLocation == null || jobLocation == null) {
            return 0.5;
        }
        
        return calculateStringSimilarity(candidateLocation.toLowerCase(), jobLocation.toLowerCase());
    }
    
    private double calculateRecencyScore(JobDto job) {
        LocalDateTime postedAt = job.getPostedAt();
        if (postedAt == null) {
            return 0.5; // Neutral if unknown
        }
        
        long daysAgo = ChronoUnit.DAYS.between(postedAt, LocalDateTime.now());
        
        if (daysAgo <= 1) return 1.0;
        if (daysAgo <= 7) return 0.9;
        if (daysAgo <= 14) return 0.8;
        if (daysAgo <= 30) return 0.6;
        if (daysAgo <= 60) return 0.4;
        
        return 0.2; // Very old postings
    }
    
    private double calculateStringSimilarity(String str1, String str2) {
        if (str1.equals(str2)) {
            return 1.0;
        }
        
        if (str1.contains(str2) || str2.contains(str1)) {
            return 0.8;
        }
        
        // Jaccard similarity based on words
        Set<String> words1 = Set.of(str1.split("\\s+"));
        Set<String> words2 = Set.of(str2.split("\\s+"));
        
        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);
        
        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);
        
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }
    
    private SkillMatchResult calculateSkillMatch(CandidateDto candidate, JobDto job) {
        List<String> candidateSkills = candidate.getSkills() != null ? 
                candidate.getSkills() : new ArrayList<>();
        List<String> requiredSkills = job.getRequiredSkills() != null ? 
                job.getRequiredSkills() : new ArrayList<>();
        
        Set<String> normalizedCandidateSkills = new HashSet<>(
                skillNormalizationService.normalizeSkills(candidateSkills));
        Set<String> normalizedRequiredSkills = new HashSet<>(
                skillNormalizationService.normalizeSkills(requiredSkills));
        
        List<String> matched = normalizedRequiredSkills.stream()
                .filter(normalizedCandidateSkills::contains)
                .collect(Collectors.toList());
        
        List<String> missing = normalizedRequiredSkills.stream()
                .filter(skill -> !normalizedCandidateSkills.contains(skill))
                .collect(Collectors.toList());
        
        return new SkillMatchResult(matched, missing);
    }
    
    private String generateExplanation(JobMatchDto match, SkillMatchResult skillResult) {
        StringBuilder explanation = new StringBuilder();
        
        // Overall score assessment
        double score = match.getOverallScore();
        if (score >= 0.8) {
            explanation.append("Excellent match! ");
        } else if (score >= 0.6) {
            explanation.append("Good match. ");
        } else if (score >= 0.4) {
            explanation.append("Moderate match. ");
        } else {
            explanation.append("Weak match. ");
        }
        
        // Skill match details
        if (!skillResult.getMatchedSkills().isEmpty()) {
            explanation.append("Matched skills: ")
                    .append(String.join(", ", skillResult.getMatchedSkills().subList(0, 
                            Math.min(4, skillResult.getMatchedSkills().size()))));
            if (skillResult.getMatchedSkills().size() > 4) {
                explanation.append("...");
            }
            explanation.append(". ");
        }
        
        if (!skillResult.getMissingSkills().isEmpty()) {
            explanation.append("Missing: ")
                    .append(String.join(", ", skillResult.getMissingSkills().subList(0,
                            Math.min(3, skillResult.getMissingSkills().size()))));
            if (skillResult.getMissingSkills().size() > 3) {
                explanation.append("...");
            }
            explanation.append(". ");
        }
        
        // Experience assessment
        if (match.getExperienceScore() >= 0.8) {
            explanation.append("Experience level fits well. ");
        } else if (match.getExperienceScore() < 0.4) {
            explanation.append("Experience level may not align. ");
        }
        
        return explanation.toString().trim();
    }
    
    private static class SkillMatchResult {
        private final List<String> matchedSkills;
        private final List<String> missingSkills;
        
        public SkillMatchResult(List<String> matchedSkills, List<String> missingSkills) {
            this.matchedSkills = matchedSkills;
            this.missingSkills = missingSkills;
        }
        
        public List<String> getMatchedSkills() { return matchedSkills; }
        public List<String> getMissingSkills() { return missingSkills; }
    }
}