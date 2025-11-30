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
public class RemoteOKJobProvider implements JobProvider {

    private final WebClient webClient;

    public RemoteOKJobProvider(WebClient.Builder webClientBuilder,
                               @Value("${job.provider.remoteok.url}") String apiUrl) {
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                // Increase buffer so the large JSON response doesn't trigger DataBufferLimitException
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(1024 * 1024))
                .build();
    }


    @Override
    public List<JobPosting> getJobs(String... keywords) {
        System.out.println("RemoteOK provider: getJobs called with keywords=" + Arrays.toString(keywords));

        try {
            RemoteOKJob[] response = webClient.get()
                    .uri("/api")                                 // main API endpoint
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(RemoteOKJob[].class)
                    .block();

            if (response == null || response.length == 0) {
                System.err.println("WARN: RemoteOK returned null or empty response.");
                return Collections.emptyList();
            }

            // First element is metadata → skip it; also limit to avoid huge lists
            List<JobPosting> allJobs = Arrays.stream(response)
                    .skip(1)            // skip metadata object
                    .limit(200)         // keep at most 200 jobs in memory
                    .map(this::toJobPosting)
                    .collect(Collectors.toList());

            System.out.println("RemoteOK provider: mapped " + allJobs.size() + " jobs.");

            String[] loweredKeywords = normalizeKeywords(keywords);

            // No keywords? Return all
            if (loweredKeywords.length == 0) {
                System.out.println("RemoteOK provider: no keywords, returning all jobs.");
                return allJobs;
            }

            // Filter matches
            List<JobPosting> filtered = allJobs.stream()
                    .filter(job -> matchesKeywords(job, loweredKeywords))
                    .collect(Collectors.toList());

            System.out.println("RemoteOK provider: filtered down to " + filtered.size() + " jobs.");

            // If filtering finds nothing, fall back to all jobs
            if (filtered.isEmpty()) {
                System.out.println("RemoteOK provider: no jobs matched keywords, returning all jobs.");
                return allJobs;
            }

            return filtered;

        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch jobs from RemoteOK API: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private String[] normalizeKeywords(String... keywords) {
        return Arrays.stream(keywords == null ? new String[0] : keywords)
                .filter(k -> k != null && !k.isBlank())
                .map(k -> k.toLowerCase(Locale.ROOT).trim())
                .toArray(String[]::new);
    }

    private boolean matchesKeywords(JobPosting job, String[] loweredKeywords) {
        if (loweredKeywords.length == 0) {
            return true;
        }

        String haystack = (job.title() + " " +
                job.company() + " " +
                job.description())
                .toLowerCase(Locale.ROOT);

        for (String kw : loweredKeywords) {
            if (haystack.contains(kw)) return true;
        }
        return false;
    }

    private JobPosting toJobPosting(RemoteOKJob raw) {
        String id = raw.id() != null ? String.valueOf(raw.id()) :
                (raw.slug() != null ? raw.slug() : "(no-id)");
        String title = raw.position() != null ? raw.position() : "(No title)";
        String company = raw.company() != null ? raw.company() : "Unknown";
        String location = raw.location() != null ? raw.location() : "Worldwide/Remote";
        String description = raw.description() != null ? raw.description() : "";
        String url = raw.apply_url() != null ? raw.apply_url() : raw.url();

        return new JobPosting(
                id,
                title,
                company,
                location,
                description,
                url
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record RemoteOKJob(
            Long id,
            String slug,
            String company,
            String position,
            String description,
            String location,
            String url,
            String apply_url
    ) {}
}
