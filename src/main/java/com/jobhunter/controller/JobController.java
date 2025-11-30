package com.jobhunter.controller;

import com.jobhunter.dto.JobDto;
import com.jobhunter.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllJobs(@RequestParam(required = false) String query,
                                                         @RequestParam(required = false) String location,
                                                         @RequestParam(required = false) Integer minExp,
                                                         @RequestParam(required = false) Integer maxExp) {
        try {
            List<JobDto> jobs;
            if (query != null || location != null || minExp != null || maxExp != null) {
                jobs = jobService.searchJobs(query, location, minExp, maxExp);
            } else {
                jobs = jobService.getAllJobs();
            }
            
            return ResponseEntity.ok(createSuccessResponse("Jobs retrieved successfully", 
                    Map.of("jobs", jobs, "count", jobs.size())));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to retrieve jobs: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getJob(@PathVariable UUID id) {
        try {
            JobDto job = jobService.getJobById(id);
            if (job != null) {
                return ResponseEntity.ok(createSuccessResponse("Job found", job));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to retrieve job: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createJob(@Valid @RequestBody JobDto jobDto) {
        try {
            JobDto created = jobService.createJob(jobDto);
            return ResponseEntity.ok(createSuccessResponse("Job created successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Failed to create job: " + e.getMessage()));
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> createJobs(@Valid @RequestBody List<JobDto> jobDtos) {
        try {
            List<JobDto> created = jobService.createJobs(jobDtos);
            return ResponseEntity.ok(createSuccessResponse("Jobs created successfully", 
                    Map.of("jobs", created, "count", created.size())));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Failed to create jobs: " + e.getMessage()));
        }
    }

    @PostMapping("/fetch/{platform}")
    public ResponseEntity<Map<String, Object>> fetchJobsFromPlatform(@PathVariable String platform,
                                                                    @RequestParam(required = false) String query,
                                                                    @RequestParam(defaultValue = "20") int limit) {
        try {
            CompletableFuture<List<JobDto>> futureJobs = jobService.fetchJobsFromPlatform(platform, query, limit);
            List<JobDto> jobs = futureJobs.get(); // Wait for completion
            
            return ResponseEntity.ok(createSuccessResponse("Jobs fetched successfully from " + platform, 
                    Map.of("jobs", jobs, "count", jobs.size(), "platform", platform)));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to fetch jobs from " + platform + ": " + e.getMessage()));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentJobs(@RequestParam(defaultValue = "7") int days) {
        try {
            List<JobDto> jobs = jobService.getRecentJobs(days);
            return ResponseEntity.ok(createSuccessResponse("Recent jobs retrieved", 
                    Map.of("jobs", jobs, "count", jobs.size(), "days", days)));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to retrieve recent jobs: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateJob(@PathVariable UUID id, 
                                                        @Valid @RequestBody JobDto jobDto) {
        try {
            jobDto.setId(id);
            JobDto updated = jobService.updateJob(jobDto);
            if (updated != null) {
                return ResponseEntity.ok(createSuccessResponse("Job updated successfully", updated));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to update job: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteJob(@PathVariable UUID id) {
        try {
            boolean deleted = jobService.deleteJob(id);
            if (deleted) {
                return ResponseEntity.ok(createSuccessResponse("Job deleted successfully", null));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to delete job: " + e.getMessage()));
        }
    }

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
}