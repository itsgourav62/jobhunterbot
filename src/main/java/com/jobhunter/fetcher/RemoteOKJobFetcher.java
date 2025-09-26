package com.jobhunter.fetcher;

import com.jobhunter.model.Job;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * RemoteOK API-based job fetcher (no scraping, uses their API)
 * Good for remote jobs - API friendly!
 */
public class RemoteOKJobFetcher implements JobFetcher {

    private static final int MAX_JOBS = 20;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RemoteOKJobFetcher() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Job> fetchJobs(List<String> skills) {
        List<Job> jobs = new ArrayList<>();

        try {
            // RemoteOK API endpoint (updated URL)
            String apiUrl = "https://remoteok.com/api";
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("User-Agent", "JobHunterBot/1.0 (+https://github.com/yourrepo/jobhunterbot)")
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode jsonArray = objectMapper.readTree(response.body());
                
                int jobsAdded = 0;
                for (JsonNode jobNode : jsonArray) {
                    if (jobsAdded >= MAX_JOBS) break;
                    
                    try {
                        // Skip if it's not a job object (first item is usually metadata)
                        if (!jobNode.has("position")) continue;

                        String title = jobNode.get("position").asText();
                        String company = jobNode.has("company") ? jobNode.get("company").asText() : "Remote Company";
                        String description = jobNode.has("description") ? jobNode.get("description").asText() : "";
                        String url = jobNode.has("url") ? jobNode.get("url").asText() : "https://remoteok.io";
                        
                        // Simple skill matching
                        boolean skillMatch = skills.stream().anyMatch(skill -> 
                            title.toLowerCase().contains(skill.toLowerCase()) ||
                            description.toLowerCase().contains(skill.toLowerCase())
                        );
                        
                        if (skillMatch || skills.isEmpty()) {
                            jobs.add(new Job("remoteok_job" + (jobsAdded + 1), title, description, url, company));
                            jobsAdded++;
                        }
                        
                    } catch (Exception e) {
                        System.out.println("⚠️ Error parsing RemoteOK job: " + e.getMessage());
                    }
                }
                
                System.out.println("✅ Scraped " + jobs.size() + " jobs from RemoteOK API");
                
            } else {
                System.out.println("❌ RemoteOK API returned status: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("❌ Error fetching RemoteOK jobs: " + e.getMessage());
        }

        return jobs;
    }
}