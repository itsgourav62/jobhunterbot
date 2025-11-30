package com.jobhunter.model;

import java.util.List;

public record CandidateProfile(
    String name,
    int yearsOfExperience,
    List<String> skills,
    List<String> targetTitles
) {}
