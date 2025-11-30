package com.jobhunter.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "job_matches")
public class JobMatch {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    @Column(name = "overall_score", nullable = false)
    private Double overallScore;
    
    @Column(name = "skill_score")
    private Double skillScore;
    
    @Column(name = "experience_score")
    private Double experienceScore;
    
    @Column(name = "title_score")
    private Double titleScore;
    
    @Column(name = "location_score")
    private Double locationScore;
    
    @Column(name = "recency_score")
    private Double recencyScore;
    
    @ElementCollection
    @CollectionTable(name = "match_matched_skills", joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "skill")
    private List<String> matchedSkills;
    
    @ElementCollection
    @CollectionTable(name = "match_missing_skills", joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "skill")
    private List<String> missingRequiredSkills;
    
    @Column(name = "match_explanation", columnDefinition = "TEXT")
    private String explanation;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public JobMatch() {
        this.createdAt = LocalDateTime.now();
    }

    public JobMatch(Candidate candidate, Job job, Double overallScore) {
        this();
        this.candidate = candidate;
        this.job = job;
        this.overallScore = overallScore;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }

    public Double getSkillScore() { return skillScore; }
    public void setSkillScore(Double skillScore) { this.skillScore = skillScore; }

    public Double getExperienceScore() { return experienceScore; }
    public void setExperienceScore(Double experienceScore) { this.experienceScore = experienceScore; }

    public Double getTitleScore() { return titleScore; }
    public void setTitleScore(Double titleScore) { this.titleScore = titleScore; }

    public Double getLocationScore() { return locationScore; }
    public void setLocationScore(Double locationScore) { this.locationScore = locationScore; }

    public Double getRecencyScore() { return recencyScore; }
    public void setRecencyScore(Double recencyScore) { this.recencyScore = recencyScore; }

    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }

    public List<String> getMissingRequiredSkills() { return missingRequiredSkills; }
    public void setMissingRequiredSkills(List<String> missingRequiredSkills) { this.missingRequiredSkills = missingRequiredSkills; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}