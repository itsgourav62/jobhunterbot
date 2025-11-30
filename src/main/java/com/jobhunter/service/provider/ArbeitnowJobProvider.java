package com.jobhunter.service.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jobhunter.model.JobPosting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class ArbeitnowJobProvider implements JobProvider {

    private final WebClient webClient;

    public ArbeitnowJobProvider(WebClient.Builder webClientBuilder,
                                @Value("${job.provider.arbeitnow.url}") String apiUrl) {
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                // increase buffer so large JSON doesn't blow up with DataBufferLimitException
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
                .build();
    }

    @Override
    public List<JobPosting> getJobs(String... keywords) {
        try {
            ArbeitnowApiResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/job-board-api")
                            .queryParam("page", 1)   // first page
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(ArbeitnowApiResponse.class)
                    .block();

            if (response == null || response.data() == null) {
                System.err.println("WARN: Arbeitnow response or data list was null.");
                return Collections.emptyList();
            }

            List<JobPosting> jobs = response.data().stream()
                    .map(this::toJobPosting)
                    // optional: avoid returning too many jobs at once
                    .limit(100)
                    .collect(Collectors.toList());

            // If no keywords, just return the mapped list
            if (keywords == null || keywords.length == 0) {
                return jobs;
            }

            String[] loweredKeywords = normalizeKeywords(keywords);
            if (loweredKeywords.length == 0) {
                return jobs;
            }

            // Filter in-memory by title / company / location / description
            return jobs.stream()
                    .filter(job -> matchesKeywords(job, loweredKeywords))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch jobs from Arbeitnow API: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private String[] normalizeKeywords(String... keywords) {
        return Arrays.stream(keywords)
                .map(k -> k == null ? "" : k.toLowerCase(Locale.ROOT).trim())
                .filter(k -> !k.isEmpty())
                .toArray(String[]::new);
    }

    private boolean matchesKeywords(JobPosting job, String[] loweredKeywords) {
        if (loweredKeywords.length == 0) {
            return true;
        }

        String haystack = (job.title() + " " +
                job.company() + " " +
                job.location() + " " +
                job.description())
                .toLowerCase(Locale.ROOT);

        for (String kw : loweredKeywords) {
            if (haystack.contains(kw)) {
                return true;
            }
        }
        return false;
    }

    private JobPosting toJobPosting(ArbeitnowJob raw) {
        // Fallbacks to avoid NPEs
        String id = raw.slug() != null ? raw.slug() : raw.url();
        String company = raw.company_name() != null ? raw.company_name() : "Unknown";
        String location = raw.location() != null ? raw.location() : (raw.remote() ? "Remote" : "Unknown");
        String description = raw.description() != null ? raw.description() : "";
        String url = raw.url() != null ? raw.url() : "";

        return new JobPosting(
                id,
                raw.title(),
                company,
                location,
                description,
                url
        );
    }

    // === DTO records for Arbeitnow ===

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArbeitnowApiResponse(
            List<ArbeitnowJob> data
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArbeitnowJob(
            String slug,
            String company_name,
            String title,
            String description,
            boolean remote,
            String url,
            List<String> tags,
            List<String> job_types,
            String location,
            String created_at
    ) {}
}
