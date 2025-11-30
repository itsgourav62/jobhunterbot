package com.jobhunter.model;

public record JobPosting(
    String id,
    String title,
    String company,
    String location,
    String description,
    String applyUrl
) {}
