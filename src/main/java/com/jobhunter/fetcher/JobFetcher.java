package com.jobhunter.fetcher;

import com.jobhunter.model.Job;
import org.openqa.selenium.WebDriver;

import java.util.Collections;
import java.util.List;

public interface JobFetcher {
    /**
     * Fetch jobs based on a list of skills.
     *
     * @param skills List of skills to search for
     * @param driver WebDriver instance to use for scraping
     * @return List of Job objects
     */
    default List<Job> fetchJobs(List<String> skills, WebDriver driver) {
        return Collections.emptyList();
    }

    /**
     * Fetch jobs based on a list of skills from an API.
     *
     * @param skills List of skills to search for
     * @return List of Job objects
     */
    default List<Job> fetchJobs(List<String> skills) {
        return Collections.emptyList();
    }

}
