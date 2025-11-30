package com.jobhunter.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SystemController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "JobHunter Backend System");
        info.put("version", "2.0.0");
        info.put("status", "RUNNING");
        info.put("features", List.of(
                "Resume Parsing",
                "Skill Normalization", 
                "Job Fetching from APIs",
                "Intelligent Job Matching",
                "Explainable Results",
                "Safety First (Auto-apply disabled by default)"
        ));
        info.put("endpoints", Map.of(
                "candidates", "/api/candidates",
                "jobs", "/api/jobs", 
                "h2-console", "/h2-console"
        ));
        
        return ResponseEntity.ok(createSuccessResponse("JobHunter Backend System Ready", info));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        return ResponseEntity.ok(createSuccessResponse("System healthy", 
                Map.of("status", "UP", "timestamp", System.currentTimeMillis())));
    }

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }
}