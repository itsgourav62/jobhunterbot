package com.jobhunter.service.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jobhunter.model.JobPosting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RemotiveJobProvider implements JobProvider {

    private final WebClient webClient;

    public RemotiveJobProvider(WebClient.Builder webClientBuilder,
                               @Value("${job.provider.remotive.url}") String apiUrl) {
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .build();
    }

    @Override
    public List<JobPosting> getJobs(String... keywords) {
        try {
            String keywordParam = (keywords == null || keywords.length == 0)
                    ? ""
                    : String.join(" ", keywords).trim();

            if (keywordParam.isEmpty()) {
                System.err.println("WARN: No keywords provided for Remotive job search. Skipping Remotive.");
                return Collections.emptyList();
            }

            RemotiveApiResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/remote-jobs")
                            .queryParam("search", keywordParam)
                            .queryParam("limit", 10)            // align with Jooble's "10" rows
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(RemotiveApiResponse.class)
                    .block();

            if (response != null && response.jobs() != null) {
                return response.jobs().stream()
                        .map(this::toJobPosting)
                        .collect(Collectors.toList());
            }

        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch jobs from Remotive API: " + e.getMessage());
        }

        return Collections.emptyList();
    }

    private JobPosting toJobPosting(RemotiveJob raw) {
        return new JobPosting(
                String.valueOf(raw.id()),
                raw.title(),
                raw.company_name(),
                raw.candidate_required_location(),
                raw.description(),
                raw.url()
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record RemotiveApiResponse(List<RemotiveJob> jobs) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record RemotiveJob(
            long id,
            String url,
            String title,
            String company_name,
            String description,
            String candidate_required_location
    ) {}
}
