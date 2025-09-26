package com.jobhunter.fetcher;

import com.jobhunter.model.Job;

import java.util.Collections;
import java.util.List;

// Implementation of JobFetcher for Indeed (or Adzuna/JSearch)
public class IndeedJobFetcher implements JobFetcher {
    @Override
    public List<Job> fetchJobs(List<String> skills) {
        // TODO: Implement job fetching logic for Indeed
        return Collections.emptyList();
    }
}

