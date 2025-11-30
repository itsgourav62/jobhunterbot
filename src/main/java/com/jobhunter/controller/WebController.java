package com.jobhunter.controller;

import com.jobhunter.model.CandidateProfile;
import com.jobhunter.model.JobPosting;
import com.jobhunter.model.MatchedJob;
import com.jobhunter.service.JobService;
import com.jobhunter.service.MatchingService;
import com.jobhunter.service.ResumeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class WebController {

    private final ResumeService resumeService;
    private final JobService jobService;
    private final MatchingService matchingService;

    public WebController(ResumeService resumeService, JobService jobService, MatchingService matchingService) {
        this.resumeService = resumeService;
        this.jobService = jobService;
        this.matchingService = matchingService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/match-jobs")
    public String matchJobs(@RequestParam(value = "resumeFile", required = false) MultipartFile resumeFile,
                            @RequestParam(value = "resumeText", required = false) String resumeText,
                            Model model) {
        try {
            CandidateProfile profile;
            if (resumeFile != null && !resumeFile.isEmpty()) {
                profile = resumeService.parse(resumeFile);
            } else if (resumeText != null && !resumeText.isBlank()) {
                profile = resumeService.parse(resumeText);
            } else {
                model.addAttribute("error", "Please upload a resume file or paste resume text.");
                return "index";
            }

            // Use the skills from the parsed profile to search for jobs
            List<JobPosting> jobs = jobService.searchJobs(profile.skills());
            List<MatchedJob> matchedJobs = matchingService.matchAndRank(profile, jobs);

            model.addAttribute("profile", profile);
            model.addAttribute("matchedJobs", matchedJobs);

        } catch (IOException e) {
            model.addAttribute("error", "Error parsing resume file: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
        }

        return "index";
    }
}