package com.jobhunter.controller;

import com.jobhunter.dto.CandidateDto;
import com.jobhunter.dto.JobMatchDto;
import com.jobhunter.service.CandidateService;
import com.jobhunter.service.ResumeParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin(origins = "*")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;
    
    @Autowired
    private ResumeParsingService resumeParsingService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCandidates() {
        try {
            List<CandidateDto> candidates = candidateService.getAllCandidates();
            return ResponseEntity.ok(createSuccessResponse("Candidates retrieved successfully", candidates));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to retrieve candidates: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCandidate(@PathVariable UUID id) {
        try {
            CandidateDto candidate = candidateService.getCandidateById(id);
            if (candidate != null) {
                return ResponseEntity.ok(createSuccessResponse("Candidate found", candidate));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to retrieve candidate: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCandidate(@Valid @RequestBody CandidateDto candidateDto) {
        try {
            CandidateDto created = candidateService.createCandidate(candidateDto);
            return ResponseEntity.ok(createSuccessResponse("Candidate created successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Failed to create candidate: " + e.getMessage()));
        }
    }

    @PostMapping("/parse-resume")
    public ResponseEntity<Map<String, Object>> parseAndCreateFromResume(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("No file uploaded"));
            }

            // Parse resume
            CandidateDto parsedCandidate = resumeParsingService.parseResumeFromFile(file);
            
            // Save to database
            CandidateDto savedCandidate = candidateService.createCandidate(parsedCandidate);
            
            return ResponseEntity.ok(createSuccessResponse("Resume parsed and candidate created successfully", savedCandidate));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to parse resume: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/matches")
    public ResponseEntity<Map<String, Object>> findJobMatches(@PathVariable UUID id, 
                                                            @RequestParam(defaultValue = "10") int limit,
                                                            @RequestParam(defaultValue = "0.3") double minScore) {
        try {
            List<JobMatchDto> matches = candidateService.findJobMatches(id, limit, minScore);
            return ResponseEntity.ok(createSuccessResponse("Job matches found", 
                    Map.of("matches", matches, "count", matches.size())));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to find job matches: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCandidate(@PathVariable UUID id, 
                                                             @Valid @RequestBody CandidateDto candidateDto) {
        try {
            candidateDto.setId(id);
            CandidateDto updated = candidateService.updateCandidate(candidateDto);
            if (updated != null) {
                return ResponseEntity.ok(createSuccessResponse("Candidate updated successfully", updated));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to update candidate: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCandidate(@PathVariable UUID id) {
        try {
            boolean deleted = candidateService.deleteCandidate(id);
            if (deleted) {
                return ResponseEntity.ok(createSuccessResponse("Candidate deleted successfully", null));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to delete candidate: " + e.getMessage()));
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