package com.jobhunter.autofill;

import java.util.Map;

public class JobApplicationService {

    private final AutofillService autofillService;

    public JobApplicationService(AutofillService autofillService) {
        this.autofillService = autofillService;
    }

    public void applyToJob(String jobUrl, Map<String, String> personalDetails, String resumePath) {
        autofillService.applyToJob(jobUrl, personalDetails, resumePath);
    }

    public void close() {
        autofillService.closeBrowser();
    }
}
