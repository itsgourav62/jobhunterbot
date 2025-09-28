package com.jobhunter.fetcher;

import com.jobhunter.model.Job;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class JobFetcherFactory {

    private final List<JobFetcher> fetchers = new ArrayList<>();

    public JobFetcherFactory() {
        fetchers.add(new IndeedJobFetcher());
        fetchers.add(new GlassdoorJobFetcher());
        fetchers.add(new ShineJobFetcher());
        fetchers.add(new AngelListJobFetcher());
        fetchers.add(new TimesJobsJobFetcher());
        fetchers.add(new NaukriJobFetcher());
        fetchers.add(new RemoteOKJobFetcher());
        fetchers.add(new MockJobFetcher());
    }

    public List<Job> fetchAllJobs(List<String> skills, WebDriver driver) {
        List<Job> allJobs = new ArrayList<>();
        for (JobFetcher fetcher : fetchers) {
            if (fetcher instanceof ApiJobFetcher) {
                allJobs.addAll(fetcher.fetchJobs(skills));
            } else {
                allJobs.addAll(fetcher.fetchJobs(skills, driver));
            }
        }
        return allJobs;
    }

    public void shutdown() {
        // No-op for now
    }
}
