package com.jobhunter.fetcher;

import com.jobhunter.model.Job;

import java.util.ArrayList;
import java.util.List;

public class NaukriJobFetcher implements JobFetcher {

    @Override
    public List<Job> fetchJobs(List<String> skills) {
        // For now, return dummy jobs. Later, this will involve web scraping.
        List<Job> jobs = new ArrayList<>();

        jobs.add(new Job(
                "job1",
                "Senior Java Developer",
                "We are looking for a Senior Java Developer with experience in Spring and SQL.",
                "https://www.naukri.com/job-example1",
                "Tech Solutions Inc."
        ));

        jobs.add(new Job(
                "job2",
                "Frontend Developer",
                "Experience with React and modern JavaScript frameworks is required.",
                "https://www.naukri.com/job-example2",
                "Creative Minds LLC"
        ));

        jobs.add(new Job(
                "job3",
                "Full Stack Engineer",
                "This role requires a strong understanding of both Java and React.",
                "https://www.naukri.com/job-example3",
                "Innovate Corp"
        ));

        return jobs;
    }
}

