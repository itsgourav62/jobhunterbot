package com.jobhunter.service.provider;

import com.jobhunter.model.JobPosting;

import java.util.List;

/**
 * Interface for a pluggable job provider. Each implementation will be responsible
 * for fetching jobs from a specific API or source.
 */
public interface JobProvider {

    List<JobPosting> getJobs(String... keywords);
}