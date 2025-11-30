package com.jobhunter.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "candidates")
public class Candidate {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String location;
    private String phone;
    private String linkedinUrl;
    
    @Column(name = "years_experience")
    private Integer yearsExperience;
    
    @ElementCollection
    @CollectionTable(name = "candidate_skills", joinColumns = @JoinColumn(name = "candidate_id"))
    @Column(name = "skill")
    private List<String> skills;
    
    @ElementCollection
    @CollectionTable(name = "candidate_titles", joinColumns = @JoinColumn(name = "candidate_id"))
    @Column(name = "title")
    private List<String> jobTitles;
    
    @Column(columnDefinition = "TEXT")
    private String resumeText;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Candidate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Candidate(String name, String email) {
        this();
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; this.updatedAt = LocalDateTime.now(); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; this.updatedAt = LocalDateTime.now(); }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; this.updatedAt = LocalDateTime.now(); }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; this.updatedAt = LocalDateTime.now(); }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; this.updatedAt = LocalDateTime.now(); }

    public Integer getYearsExperience() { return yearsExperience; }
    public void setYearsExperience(Integer yearsExperience) { this.yearsExperience = yearsExperience; this.updatedAt = LocalDateTime.now(); }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; this.updatedAt = LocalDateTime.now(); }

    public List<String> getJobTitles() { return jobTitles; }
    public void setJobTitles(List<String> jobTitles) { this.jobTitles = jobTitles; this.updatedAt = LocalDateTime.now(); }

    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; this.updatedAt = LocalDateTime.now(); }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}