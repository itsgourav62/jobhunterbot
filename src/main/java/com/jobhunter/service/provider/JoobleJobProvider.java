package com.jobhunter.service.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jobhunter.model.JobPosting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JoobleJobProvider implements JobProvider {

    private final WebClient webClient;
    private final String apiKey;

    public JoobleJobProvider(WebClient.Builder webClientBuilder,
                             @Value("${job.provider.jooble.url}") String apiUrl,        // should be https://jooble.org
                             @Value("${job.provider.jooble.api_key}") String apiKey) {  // your actual key
        this.webClient = webClientBuilder
                .baseUrl(apiUrl) // https://jooble.org
                .build();
        this.apiKey = apiKey;
    }

    @Override
    public List<JobPosting> getJobs(String... keywords) {
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("WARN: Jooble API key not configured. Skipping Jooble job search.");
            return Collections.emptyList();
        }

        try {
            String keywordParam = String.join(" ", keywords);

            // Jooble request body – NOTE: NO key field here
            JoobleRequest requestBody = new JoobleRequest(
                    keywordParam,
                    "India",  // or make this configurable
                    1,
                    10
            );

            JoobleApiResponse response = webClient.post()
                    .uri("/api/" + apiKey) // IMPORTANT: key in path
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JoobleApiResponse.class)
                    .block();

            if (response != null && response.jobs() != null) {
                return response.jobs().stream()
                        .map(this::toJobPosting)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch jobs from Jooble API: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private JobPosting toJobPosting(JoobleJob raw) {
        return new JobPosting(
                raw.id(),
                raw.title(),
                raw.company(),
                raw.location(),
                raw.snippet(), // Jooble provides a snippet, not full description
                raw.link()
        );
    }

    // Request body – NO key field
    private record JoobleRequest(
            String keywords,
            String location,
            int page,
            int rows
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record JoobleApiResponse(List<JoobleJob> jobs) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record JoobleJob(
            String id,
            String title,
            String company,
            String location,
            String snippet,
            String link
    ) {}
}
