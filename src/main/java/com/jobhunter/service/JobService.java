package com.jobhunter.service;

import com.jobhunter.model.JobPosting;
import com.jobhunter.service.provider.JobProvider;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class   JobService {

    private final List<JobProvider> jobProviders;

    // Spring will automatically inject all beans that implement the JobProvider interface
    public JobService(List<JobProvider> jobProviders) {
        this.jobProviders = jobProviders;
        System.out.println("INFO: Loaded " + jobProviders.size() + " job provider(s).");
    }

    public List<JobPosting> searchJobs(List<String> skills) {
        String[] keywords = skills.toArray(new String[0]);
        
        // Fetch jobs from all providers in parallel and collect them into one list
        return jobProviders.parallelStream()
                .map(provider -> provider.getJobs(keywords))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
