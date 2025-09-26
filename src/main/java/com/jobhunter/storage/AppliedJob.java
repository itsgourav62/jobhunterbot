package com.jobhunter.storage;

import java.time.LocalDateTime;

public class AppliedJob {
    private String jobId;       // Unique job ID from Naukri/Indeed
    private String title;
    private String company;
    private String url;
    private LocalDateTime appliedAt;

    public AppliedJob() {}

    public AppliedJob(String jobId, String title, String company, String url, LocalDateTime appliedAt) {
        this.jobId = jobId;
        this.title = title;
        this.company = company;
        this.url = url;
        this.appliedAt = appliedAt;
    }

    // Getters & Setters
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    @Override
    public String toString() {
        return "AppliedJob{" +
                "jobId='" + jobId + '\'' +
                ", title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", url='" + url + '\'' +
                ", appliedAt=" + appliedAt +
                '}';
    }
}
