package com.jobhunter.fetcher;

import com.jobhunter.model.Job;

import java.util.List;

public interface JobFetcher {
    /**
     * Fetch jobs based on a list of skills.
     *
     * @param skills List of skills to search for
     * @return List of Job objects
     */
    List<Job> fetchJobs(List<String> skills);
}
