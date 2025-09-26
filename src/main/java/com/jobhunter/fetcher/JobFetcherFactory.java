package com.jobhunter.fetcher;

import com.jobhunter.model.Job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class JobFetcherFactory {

    private List<JobFetcher> scrapingFetchers;
    private List<JobFetcher> apiFetchers;
    private ExecutorService executorService;

    public JobFetcherFactory() {
        this.scrapingFetchers = new ArrayList<>();
        this.apiFetchers = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(6); // Parallel execution

        // Register scraping-based fetchers (slower but more data)
        this.scrapingFetchers.add(new NaukriJobFetcher());
        this.scrapingFetchers.add(new IndeedJobFetcher());
        this.scrapingFetchers.add(new AngelListJobFetcher());
        this.scrapingFetchers.add(new GlassdoorJobFetcher());
        this.scrapingFetchers.add(new ShineJobFetcher());        // Added Shine.com
        this.scrapingFetchers.add(new TimesJobsJobFetcher());    // Added TimesJobs.com
        // LinkedInJobFetcher REMOVED due to Terms of Service violations

        // Register API-based fetchers (faster, TOS-compliant)
        this.apiFetchers.add(new RemoteOKJobFetcher());
        this.apiFetchers.add(new MockJobFetcher()); // For testing and demonstration

        System.out.println("🔧 JobFetcherFactory initialized with " + 
                         (scrapingFetchers.size() + apiFetchers.size()) + " job sources");
        System.out.println("🌐 Scraping sources: Naukri, Indeed, AngelList, Glassdoor, Shine, TimesJobs");
        System.out.println("📡 API sources: RemoteOK, MockJobs (for testing)");
    }

    /**
     * Fetch jobs from all registered platforms CONCURRENTLY for better performance
     */
    public List<Job> fetchAllJobs(List<String> skills) {
        List<Job> allJobs = new ArrayList<>();
        List<CompletableFuture<List<Job>>> futures = new ArrayList<>();

        System.out.println("🚀 Starting parallel job fetching from " + 
                         (scrapingFetchers.size() + apiFetchers.size()) + " sources...");

        // Submit API fetchers first (faster)
        for (JobFetcher fetcher : apiFetchers) {
            CompletableFuture<List<Job>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    String fetcherName = fetcher.getClass().getSimpleName();
                    System.out.println("📡 Fetching from " + fetcherName + "...");
                    return fetcher.fetchJobs(skills);
                } catch (Exception e) {
                    System.out.println("⚠️ Error in " + fetcher.getClass().getSimpleName() + ": " + e.getMessage());
                    return new ArrayList<>();
                }
            }, executorService);
            futures.add(future);
        }

        // Submit scraping fetchers with delays
        for (int i = 0; i < scrapingFetchers.size(); i++) {
            final JobFetcher fetcher = scrapingFetchers.get(i);
            final int delay = i * 2000; // 2-second delays between scraping starts
            
            CompletableFuture<List<Job>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(delay); // Stagger requests to avoid rate limiting
                    String fetcherName = fetcher.getClass().getSimpleName();
                    System.out.println("🌐 Scraping from " + fetcherName + "...");
                    return fetcher.fetchJobs(skills);
                } catch (Exception e) {
                    System.out.println("⚠️ Error in " + fetcher.getClass().getSimpleName() + ": " + e.getMessage());
                    return new ArrayList<>();
                }
            }, executorService);
            futures.add(future);
        }

        // Collect all results with timeout
        try {
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            
            // Wait up to 2 minutes for all fetchers to complete
            allFutures.get(120, TimeUnit.SECONDS);
            
            // Collect results
            for (CompletableFuture<List<Job>> future : futures) {
                List<Job> jobs = future.get();
                if (jobs != null && !jobs.isEmpty()) {
                    allJobs.addAll(jobs);
                }
            }
            
        } catch (TimeoutException e) {
            System.out.println("⏰ Some job fetchers timed out, collecting available results...");
            for (CompletableFuture<List<Job>> future : futures) {
                if (future.isDone() && !future.isCancelled()) {
                    try {
                        List<Job> jobs = future.get();
                        if (jobs != null && !jobs.isEmpty()) {
                            allJobs.addAll(jobs);
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error during parallel job fetching: " + e.getMessage());
        }

        System.out.println("✅ Total jobs fetched from all sources: " + allJobs.size());
        return allJobs;
    }

    /**
     * Fetch jobs from API sources only (faster, more reliable)
     */
    public List<Job> fetchJobsFromAPIs(List<String> skills) {
        List<Job> allJobs = new ArrayList<>();
        for (JobFetcher fetcher : apiFetchers) {
            try {
                List<Job> jobs = fetcher.fetchJobs(skills);
                if (jobs != null && !jobs.isEmpty()) {
                    allJobs.addAll(jobs);
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error fetching API jobs from " + fetcher.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        return allJobs;
    }

    /**
     * Add new fetcher dynamically
     */
    public void registerScrapingFetcher(JobFetcher fetcher) {
        this.scrapingFetchers.add(fetcher);
        System.out.println("➕ Registered new scraping fetcher: " + fetcher.getClass().getSimpleName());
    }
    
    public void registerAPIFetcher(JobFetcher fetcher) {
        this.apiFetchers.add(fetcher);
        System.out.println("➕ Registered new API fetcher: " + fetcher.getClass().getSimpleName());
    }

    /**
     * Shutdown executor service
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
