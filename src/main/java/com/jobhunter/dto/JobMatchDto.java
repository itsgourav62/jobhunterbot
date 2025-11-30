package com.jobhunter.dto;

import java.util.List;
import java.util.UUID;

public class JobMatchDto {
    
    private UUID id;
    private UUID candidateId;
    private String candidateName;
    private UUID jobId;
    private String jobTitle;
    private String company;
    private String location;
    
    private Double overallScore;
    private Double skillScore;
    private Double experienceScore;
    private Double titleScore;
    private Double locationScore;
    private Double recencyScore;
    
    private List<String> matchedSkills;
    private List<String> missingRequiredSkills;
    private String explanation;
    
    private String jobUrl;
    private String applyUrl;

    // Constructors
    public JobMatchDto() {}

    public JobMatchDto(UUID candidateId, UUID jobId, Double overallScore) {
        this.candidateId = candidateId;
        this.jobId = jobId;
        this.overallScore = overallScore;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCandidateId() { return candidateId; }
    public void setCandidateId(UUID candidateId) { this.candidateId = candidateId; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public UUID getJobId() { return jobId; }
    public void setJobId(UUID jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

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

    public String getJobUrl() { return jobUrl; }
    public void setJobUrl(String jobUrl) { this.jobUrl = jobUrl; }

    public String getApplyUrl() { return applyUrl; }
    public void setApplyUrl(String applyUrl) { this.applyUrl = applyUrl; }
}