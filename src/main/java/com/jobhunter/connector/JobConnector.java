package com.jobhunter.connector;

import com.jobhunter.dto.JobDto;

import java.util.List;

public interface JobConnector {
    
    String getPlatformName();
    
    List<JobDto> fetchJobs(String query, int limit);
    
    default boolean isEnabled() {
        return true;
    }
}