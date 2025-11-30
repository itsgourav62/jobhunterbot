package com.jobhunter.service.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jobhunter.model.JobPosting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class YCombinatorJobProvider implements JobProvider {

    private final WebClient webClient;
    private final int maxJobs;

    public YCombinatorJobProvider(WebClient.Builder webClientBuilder,
                                  @Value("${job.provider.yc.url}") String apiUrl,
                                  @Value("${job.provider.yc.max_jobs:100}") int maxJobs) {
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .build();
        this.maxJobs = maxJobs;
    }

    @Override
    public List<JobPosting> getJobs(String... keywords) {
        System.out.println("YC provider: getJobs called with keywords=" + Arrays.toString(keywords));

        try {
            // 1) Fetch job story IDs as array
            Long[] jobIdsArray = webClient.get()
                    .uri("/jobstories.json")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Long[].class)
                    .block();

            if (jobIdsArray == null || jobIdsArray.length == 0) {
                System.err.println("WARN: YC/HN jobstories list was null or empty.");
                return Collections.emptyList();
            }

            System.out.println("YC provider: fetched " + jobIdsArray.length + " job IDs.");

            List<Long> jobIds = Arrays.asList(jobIdsArray);

            // 2) Limit how many we fetch
            List<Long> limitedIds = jobIds.stream()
                    .limit(maxJobs)
                    .collect(Collectors.toList());

            // 3) Fetch ALL jobs first
            List<JobPosting> allJobs = limitedIds.stream()
                    .map(this::fetchItem)                                      // HN item
                    .filter(item -> item != null && "job".equalsIgnoreCase(item.type()))
                    .map(this::toJobPosting)                                   // -> JobPosting
                    .collect(Collectors.toList());

            System.out.println("YC provider: mapped " + allJobs.size() + " jobs before filtering.");

            // 4) Normalize keywords
            String[] loweredKeywords = normalizeKeywords(keywords);

            // If there is no "context" (no keywords) -> list all jobs
            if (loweredKeywords.length == 0) {
                System.out.println("YC provider: no keywords provided, returning all jobs.");
                return allJobs;
            }

            // 5) Filter by keywords
            List<JobPosting> filtered = allJobs.stream()
                    .filter(job -> matchesKeywords(job, loweredKeywords))
                    .collect(Collectors.toList());

            System.out.println("YC provider: filtered down to " + filtered.size() + " jobs.");

            // 6) If filtering killed everything, fall back to all jobs
            if (filtered.isEmpty()) {
                System.out.println("YC provider: no jobs matched keywords, falling back to all jobs.");
                return allJobs;
            }

            return filtered;

        } catch (WebClientResponseException e) {
            System.err.println("ERROR: YC/HN HTTP error " + e.getRawStatusCode() +
                    " when calling " + e.getRequest().getURI() +
                    " body=" + e.getResponseBodyAsString());
            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch jobs from YC/HackerNews API: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private String[] normalizeKeywords(String... keywords) {
        if (keywords == null || keywords.length == 0) {
            return new String[0];
        }

        return Arrays.stream(keywords)
                .map(k -> k == null ? "" : k.toLowerCase(Locale.ROOT).trim())
                .filter(k -> !k.isEmpty())
                .toArray(String[]::new);
    }

    private boolean matchesKeywords(JobPosting job, String[] loweredKeywords) {
        if (loweredKeywords.length == 0) {
            return true; // No keywords -> accept all
        }

        String haystack = (job.title() + " " + job.description())
                .toLowerCase(Locale.ROOT);

        for (String kw : loweredKeywords) {
            if (haystack.contains(kw)) {
                return true;
            }
        }
        return false;
    }

    private HnItem fetchItem(Long id) {
        try {
            HnItem item = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/item/{id}.json")
                            .build(id))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(HnItem.class)
                    .block();

            if (item == null) {
                System.err.println("WARN: HN item id=" + id + " returned null.");
            }

            return item;
        } catch (WebClientResponseException e) {
            System.err.println("WARN: HN HTTP error " + e.getRawStatusCode() +
                    " for item id=" + id + " body=" + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.err.println("WARN: Failed to fetch HN item id=" + id + ": " + e.getMessage());
            return null;
        }
    }

    private JobPosting toJobPosting(HnItem raw) {
        String id = raw.id() != null ? String.valueOf(raw.id()) : "(no-id)";
        String title = raw.title() != null ? raw.title() : "(No title)";
        String companyOrAuthor = raw.by() != null ? raw.by() : "HN job poster";
        String description = raw.text() != null ? raw.text() : "";

        // If the post links out, use that URL; else link to the HN discussion
        String url = raw.url() != null
                ? raw.url()
                : "https://news.ycombinator.com/item?id=" + raw.id();

        // No structured location in HN job posts; treat as unknown/remote
        String location = "Remote/Unknown";

        return new JobPosting(
                id,
                title,
                companyOrAuthor,
                location,
                description,
                url
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record HnItem(
            Long id,
            String type,
            String by,
            Long time,
            String text,
            String url,
            String title
    ) {}
}
