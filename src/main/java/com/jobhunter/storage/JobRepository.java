package com.jobhunter.storage;

import java.util.List;

public interface JobRepository {
    void save(AppliedJob job);                // Save applied job
    boolean isAlreadyApplied(String jobId);   // Check if already applied
    List<AppliedJob> getAllAppliedJobs();     // Fetch all applied jobs
}
