package com.jobhunter.autofill;

import com.jobhunter.model.Job;
import com.jobhunter.model.Resume;
import com.jobhunter.storage.JobRepository;

public class JobApplicationService {

    private final AutofillService autofillService;
    private final JobRepository jobRepository;
    private final String resumePath;

    public JobApplicationService(AutofillService autofillService, JobRepository jobRepository, String resumePath) {
        this.autofillService = autofillService;
        this.jobRepository = jobRepository;
        this.resumePath = resumePath;
    }

    public boolean apply(Job job, Resume resume) {
        if (jobRepository.hasApplied(job.getUrl())) {
            System.out.println("⏩ Already applied to: " + job.getTitle() + " at " + job.getCompany());
            return false;
        }

        System.out.println("\n🚀 Applying to: " + job.getTitle() + " at " + job.getCompany());
        System.out.println("   🔗 " + job.getUrl());

        try {
            autofillService.applyToJob(job.getUrl(), resume, resumePath);
            jobRepository.markAsApplied(job.getUrl(), job.getTitle(), job.getCompany());

            System.out.println("✅ Successfully applied and marked in database.");
            // In a real scenario, you might wait for confirmation before returning true
            return true;

        } catch (Exception e) {
            System.err.println("❌ Failed to apply to: " + job.getTitle());
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        autofillService.closeBrowser();
    }
}
