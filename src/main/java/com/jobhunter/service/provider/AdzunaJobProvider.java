package com.jobhunter.service.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jobhunter.model.JobPosting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdzunaJobProvider implements JobProvider {

    private final WebClient webClient;
    private final String appId;
    private final String appKey;

    public AdzunaJobProvider(WebClient.Builder webClientBuilder,
                             @Value("${job.provider.adzuna.url}") String apiUrl,
                             @Value("${job.provider.adzuna.app_id}") String appId,
                             @Value("${job.provider.adzuna.app_key}") String appKey) {

        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .build();

        this.appId = appId;
        this.appKey = appKey;
    }

    @Override
    public List<JobPosting> getJobs(String... keywords) {
        if (appId == null || appId.isBlank() || appKey == null || appKey.isBlank()) {
            System.err.println("WARN: Adzuna API credentials not configured. Skipping Adzuna job search.");
            return Collections.emptyList();
        }

        String keywordParam = (keywords == null || keywords.length == 0)
                ? ""
                : String.join(" ", keywords).trim();

        if (keywordParam.isEmpty()) {
            System.err.println("WARN: No keywords provided for Adzuna job search. Skipping Adzuna.");
            return Collections.emptyList();
        }

        try {
            AdzunaApiResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/1") // /search/{page}
                            .queryParam("app_id", appId)
                            .queryParam("app_key", appKey)
                            .queryParam("results_per_page", 10)
                            .queryParam("what", keywordParam)
                            .build())
                    .retrieve()
                    .bodyToMono(AdzunaApiResponse.class)
                    .block();

            if (response != null && response.results() != null) {
                return response.results().stream()
                        .map(this::toJobPosting)
                        .collect(Collectors.toList());
            }

        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch jobs from Adzuna API: " + e.getMessage());
        }

        return Collections.emptyList();
    }

    private JobPosting toJobPosting(AdzunaJob raw) {
        return new JobPosting(
                raw.id(),
                raw.title(),
                raw.company() != null ? raw.company().display_name() : "Unknown",
                raw.location() != null ? raw.location().display_name() : "Unknown",
                raw.description(),
                raw.redirect_url()
        );
    }

    // DTO mapping classes

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AdzunaApiResponse(List<AdzunaJob> results) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AdzunaJob(
            String id,
            String title,
            AdzunaCompany company,
            AdzunaLocation location,
            String description,
            String redirect_url
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AdzunaCompany(String display_name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AdzunaLocation(String display_name) {}
}
