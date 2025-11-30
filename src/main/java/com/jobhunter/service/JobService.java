package com.jobhunter.service;

import com.jobhunter.dto.JobDto;
import com.jobhunter.entity.Job;
import com.jobhunter.repository.JobRepository;
import com.jobhunter.connector.JobConnector;
import com.jobhunter.connector.RemoteOkConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class JobService {

    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private SkillNormalizationService skillNormalizationService;
    
    @Autowired
    private RemoteOkConnector remoteOkConnector;

    public List<JobDto> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public JobDto getJobById(UUID id) {
        return jobRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    public JobDto createJob(JobDto jobDto) {
        // Normalize skills before saving
        if (jobDto.getRequiredSkills() != null) {
            jobDto.setRequiredSkills(skillNormalizationService.normalizeSkills(jobDto.getRequiredSkills()));
        }
        if (jobDto.getPreferredSkills() != null) {
            jobDto.setPreferredSkills(skillNormalizationService.normalizeSkills(jobDto.getPreferredSkills()));
        }
        
        Job job = convertToEntity(jobDto);
        Job saved = jobRepository.save(job);
        return convertToDto(saved);
    }

    public List<JobDto> createJobs(List<JobDto> jobDtos) {
        List<Job> jobs = jobDtos.stream()
                .map(dto -> {
                    // Normalize skills
                    if (dto.getRequiredSkills() != null) {
                        dto.setRequiredSkills(skillNormalizationService.normalizeSkills(dto.getRequiredSkills()));
                    }
                    if (dto.getPreferredSkills() != null) {
                        dto.setPreferredSkills(skillNormalizationService.normalizeSkills(dto.getPreferredSkills()));
                    }
                    return convertToEntity(dto);
                })
                .collect(Collectors.toList());
                
        List<Job> saved = jobRepository.saveAll(jobs);
        return saved.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public JobDto updateJob(JobDto jobDto) {
        if (jobDto.getId() == null) {
            throw new IllegalArgumentException("Job ID is required for update");
        }
        
        return jobRepository.findById(jobDto.getId())
                .map(existing -> {
                    updateEntityFromDto(existing, jobDto);
                    Job updated = jobRepository.save(existing);
                    return convertToDto(updated);
                })
                .orElse(null);
    }

    public boolean deleteJob(UUID id) {
        if (jobRepository.existsById(id)) {
            jobRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Async
    public CompletableFuture<List<JobDto>> fetchJobsFromPlatform(String platform, String query, int limit) {
        JobConnector connector = getConnector(platform);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown platform: " + platform);
        }
        
        List<JobDto> jobs = connector.fetchJobs(query, limit);
        
        // Save fetched jobs to database
        List<JobDto> savedJobs = createJobs(jobs);
        
        return CompletableFuture.completedFuture(savedJobs);
    }

    public List<JobDto> searchJobs(String query, String location, Integer minExp, Integer maxExp) {
        List<Job> jobs = jobRepository.findAll();
        
        return jobs.stream()
                .filter(job -> matchesQuery(job, query))
                .filter(job -> matchesLocation(job, location))
                .filter(job -> matchesExperience(job, minExp, maxExp))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<JobDto> getRecentJobs(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return jobRepository.findRecentJobs(since).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private JobConnector getConnector(String platform) {
        switch (platform.toLowerCase()) {
            case "remoteok":
                return remoteOkConnector;
            default:
                return null;
        }
    }

    private boolean matchesQuery(Job job, String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }
        
        String queryLower = query.toLowerCase();
        String title = job.getTitle() != null ? job.getTitle().toLowerCase() : "";
        String description = job.getDescription() != null ? job.getDescription().toLowerCase() : "";
        String company = job.getCompany() != null ? job.getCompany().toLowerCase() : "";
        
        return title.contains(queryLower) || description.contains(queryLower) || company.contains(queryLower);
    }

    private boolean matchesLocation(Job job, String location) {
        if (location == null || location.trim().isEmpty()) {
            return true;
        }
        
        String jobLocation = job.getLocation();
        return jobLocation != null && jobLocation.toLowerCase().contains(location.toLowerCase());
    }

    private boolean matchesExperience(Job job, Integer minExp, Integer maxExp) {
        if (minExp == null && maxExp == null) {
            return true;
        }
        
        Integer jobMinExp = job.getMinExperience();
        Integer jobMaxExp = job.getMaxExperience();
        
        if (jobMinExp == null && jobMaxExp == null) {
            return true; // Job has no experience requirements
        }
        
        // Check if there's overlap in experience ranges
        int candidateMin = minExp != null ? minExp : 0;
        int candidateMax = maxExp != null ? maxExp : 50;
        int jobMin = jobMinExp != null ? jobMinExp : 0;
        int jobMax = jobMaxExp != null ? jobMaxExp : 50;
        
        return candidateMax >= jobMin && candidateMin <= jobMax;
    }

    private JobDto convertToDto(Job job) {
        JobDto dto = new JobDto();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setCompany(job.getCompany());
        dto.setCompanyUrl(job.getCompanyUrl());
        dto.setLocation(job.getLocation());
        dto.setRemoteOk(job.getRemoteOk());
        dto.setDescription(job.getDescription());
        dto.setRequiredSkills(job.getRequiredSkills());
        dto.setPreferredSkills(job.getPreferredSkills());
        dto.setMinExperience(job.getMinExperience());
        dto.setMaxExperience(job.getMaxExperience());
        dto.setSalaryMin(job.getSalaryMin());
        dto.setSalaryMax(job.getSalaryMax());
        dto.setSalaryCurrency(job.getSalaryCurrency());
        dto.setJobUrl(job.getJobUrl());
        dto.setApplyUrl(job.getApplyUrl());
        dto.setJobType(job.getJobType());
        dto.setSourcePlatform(job.getSourcePlatform());
        dto.setPostedAt(job.getPostedAt());
        dto.setExpiresAt(job.getExpiresAt());
        return dto;
    }

    private Job convertToEntity(JobDto dto) {
        Job job = new Job();
        updateEntityFromDto(job, dto);
        return job;
    }

    private void updateEntityFromDto(Job job, JobDto dto) {
        job.setTitle(dto.getTitle());
        job.setCompany(dto.getCompany());
        job.setCompanyUrl(dto.getCompanyUrl());
        job.setLocation(dto.getLocation());
        job.setRemoteOk(dto.getRemoteOk());
        job.setDescription(dto.getDescription());
        job.setRequiredSkills(dto.getRequiredSkills());
        job.setPreferredSkills(dto.getPreferredSkills());
        job.setMinExperience(dto.getMinExperience());
        job.setMaxExperience(dto.getMaxExperience());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setSalaryCurrency(dto.getSalaryCurrency());
        job.setJobUrl(dto.getJobUrl());
        job.setApplyUrl(dto.getApplyUrl());
        job.setJobType(dto.getJobType());
        job.setSourcePlatform(dto.getSourcePlatform());
        job.setPostedAt(dto.getPostedAt());
        job.setExpiresAt(dto.getExpiresAt());
    }
}