package com.jobhunter.connector;

import com.jobhunter.dto.JobDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class RemoteOkConnector implements JobConnector {
    
    private final WebClient webClient;
    
    public RemoteOkConnector() {
        this.webClient = WebClient.builder()
                .baseUrl("https://remoteok.io/api")
                .build();
    }
    
    @Override
    public String getPlatformName() {
        return "RemoteOK";
    }
    
    @Override
    public List<JobDto> fetchJobs(String query, int limit) {
        try {
            // RemoteOK API doesn't require auth for basic fetching
            Object[] response = webClient.get()
                    .uri("/")
                    .header("User-Agent", "JobHunterBot/1.0")
                    .retrieve()
                    .bodyToMono(Object[].class)
                    .block();
                    
            List<JobDto> jobs = new ArrayList<>();
            
            if (response != null) {
                for (int i = 1; i < Math.min(response.length, limit + 1); i++) { // Skip first element (metadata)
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> jobData = (Map<String, Object>) response[i];
                        
                        JobDto job = mapRemoteOkJob(jobData);
                        if (job != null && matchesQuery(job, query)) {
                            jobs.add(job);
                        }
                    } catch (Exception e) {
                        // Skip invalid job entries
                        continue;
                    }
                }
            }
            
            return jobs;
            
        } catch (Exception e) {
            System.err.println("Failed to fetch jobs from RemoteOK: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private JobDto mapRemoteOkJob(Map<String, Object> data) {
        try {
            JobDto job = new JobDto();
            
            job.setTitle((String) data.get("position"));
            job.setCompany((String) data.get("company"));
            job.setCompanyUrl((String) data.get("company_logo"));
            job.setDescription((String) data.get("description"));
            job.setJobUrl((String) data.get("url"));
            job.setApplyUrl((String) data.get("apply_url"));
            job.setRemoteOk(true);
            job.setSourcePlatform("RemoteOK");
            
            // Parse location
            String location = (String) data.get("location");
            job.setLocation(location != null ? location : "Remote");
            
            // Parse salary if available
            if (data.get("salary_min") != null) {
                job.setSalaryMin(Long.valueOf(data.get("salary_min").toString()));
            }
            if (data.get("salary_max") != null) {
                job.setSalaryMax(Long.valueOf(data.get("salary_max").toString()));
            }
            
            // Parse skills/tags
            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) data.get("tags");
            if (tags != null) {
                job.setRequiredSkills(new ArrayList<>(tags));
            }
            
            // Parse posted date
            if (data.get("date") != null) {
                try {
                    long timestamp = Long.parseLong(data.get("date").toString());
                    job.setPostedAt(LocalDateTime.ofEpochSecond(timestamp, 0, java.time.ZoneOffset.UTC));
                } catch (NumberFormatException e) {
                    job.setPostedAt(LocalDateTime.now());
                }
            }
            
            return job;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private boolean matchesQuery(JobDto job, String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }
        
        String queryLower = query.toLowerCase();
        String title = job.getTitle() != null ? job.getTitle().toLowerCase() : "";
        String description = job.getDescription() != null ? job.getDescription().toLowerCase() : "";
        
        return title.contains(queryLower) || description.contains(queryLower);
    }
}