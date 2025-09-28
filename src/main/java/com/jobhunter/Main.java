package com.jobhunter;

import com.jobhunter.autofill.AutofillService;
import com.jobhunter.autofill.JobApplicationService;
import com.jobhunter.config.AppConfig;
import com.jobhunter.model.Resume;
import com.jobhunter.parser.ResumeParser;
import com.jobhunter.storage.H2JobRepository;
import com.jobhunter.storage.JobRepository;
import com.jobhunter.workflow.SmartApplicationWorkflow;
import org.openqa.selenium.WebDriver;
import org.apache.tika.exception.TikaException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("🦁 Job Hunter Bot v2.0");
        System.out.println("====================================");

        WebDriver driver = null;
        JobRepository jobRepository = null;

        try {
            // 1. Load Configuration
            AppConfig config = AppConfig.getInstance();

            // 2. Load Resume
            Resume resume = new ResumeParser().parseFromUrl(config.getResumeUrl());

            // 3. Initialize WebDriver
            driver = BrowserFactory.getDriver(config.getBrowser());

            // 4. Initialize Services
            jobRepository = new H2JobRepository(config.getDatabasePath());
            AutofillService autofillService = new AutofillService(driver);
            JobApplicationService jobApplicationService = new JobApplicationService(autofillService, jobRepository, config.getResumePath());

            // 5. Start the Workflow
            SmartApplicationWorkflow workflow = new SmartApplicationWorkflow(resume, jobApplicationService, config.getOutputDir(), driver);
            workflow.run();

        } catch (Exception e) {
            System.err.println("\n❌ A critical error occurred in the main application:");
            e.printStackTrace();
        } finally {
            // 6. Cleanup
            if (driver != null) {
                driver.quit();
            }
            if (jobRepository != null) {
                jobRepository.close();
            }
            System.out.println("\n✅ Application finished and resources are closed.");
            System.out.println("====================================");
        }
    }


}
