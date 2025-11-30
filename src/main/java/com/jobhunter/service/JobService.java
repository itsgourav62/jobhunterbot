package com.jobhunter.service;

import com.jobhunter.model.JobPosting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    // This is the placeholder for the real API URL.
    // It can be configured in application.properties.
    @Value("${jobprovider.api.url:}")
    private String apiUrl;

    private final List<JobPosting> FAKE_JOBS = Arrays.asList(
            new JobPosting("1", "Backend Engineer", "Acme Corp", "Bangalore", "Expert in Java, Spring, and SQL needed.", "https://example.com/apply/1"),
            new JobPosting("2", "Senior Java Developer", "Globex Inc.", "Remote", "Seeking a senior Java expert with Spring Boot.", "https://example.com/apply/2"),
            new JobPosting("3", "DevOps Engineer", "Soylent Corp", "Pune", "Looking for experience with Docker, AWS, and Kubernetes.", "https://example.com/apply/3"),
            new JobPosting("4", "Cloud Engineer", "Stark Industries", "Remote", "Work with AWS, Docker, and Kubernetes.", "https://example.com/apply/7")
    );

    public List<JobPosting> searchJobs(List<String> skills) {
        // If a real API URL is configured, you would use it here.
        // For now, we use the fake provider.
        if (apiUrl != null && !apiUrl.isEmpty()) {
            System.out.println("INFO: A real API URL is configured. In a real app, you would call: " + apiUrl);
            // Here you would use HttpClient or WebClient to call the real API.
        }

        // --- FAKE PROVIDER LOGIC ---
        return FAKE_JOBS.stream()
                .filter(job -> skills.stream().anyMatch(skill -> job.description().toLowerCase().contains(skill.toLowerCase())))
                .collect(Collectors.toList());
    }
}
