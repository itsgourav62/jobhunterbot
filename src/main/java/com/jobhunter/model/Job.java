package com.jobhunter.model;

public class Job {
    private String id;
    private String title;
    private String description;
    private String url;
    private String company;

    public Job(String id, String title, String description, String url, String company) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
        this.company = company;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
}
