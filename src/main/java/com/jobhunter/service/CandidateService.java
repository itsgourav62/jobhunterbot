package com.jobhunter.service;

import com.jobhunter.dto.CandidateDto;
import com.jobhunter.dto.JobMatchDto;
import com.jobhunter.entity.Candidate;
import com.jobhunter.repository.CandidateRepository;
import com.jobhunter.matcher.JobMatchingEngine;
import com.jobhunter.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private JobMatchingEngine jobMatchingEngine;

    public List<CandidateDto> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CandidateDto getCandidateById(UUID id) {
        return candidateRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    public CandidateDto getCandidateByEmail(String email) {
        return candidateRepository.findByEmail(email)
                .map(this::convertToDto)
                .orElse(null);
    }

    public CandidateDto createCandidate(CandidateDto candidateDto) {
        // Check if candidate with email already exists
        if (candidateDto.getEmail() != null && candidateRepository.findByEmail(candidateDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Candidate with email " + candidateDto.getEmail() + " already exists");
        }
        
        Candidate candidate = convertToEntity(candidateDto);
        Candidate saved = candidateRepository.save(candidate);
        return convertToDto(saved);
    }

    public CandidateDto updateCandidate(CandidateDto candidateDto) {
        if (candidateDto.getId() == null) {
            throw new IllegalArgumentException("Candidate ID is required for update");
        }
        
        return candidateRepository.findById(candidateDto.getId())
                .map(existing -> {
                    updateEntityFromDto(existing, candidateDto);
                    Candidate updated = candidateRepository.save(existing);
                    return convertToDto(updated);
                })
                .orElse(null);
    }

    public boolean deleteCandidate(UUID id) {
        if (candidateRepository.existsById(id)) {
            candidateRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<JobMatchDto> findJobMatches(UUID candidateId, int limit, double minScore) {
        CandidateDto candidate = getCandidateById(candidateId);
        if (candidate == null) {
            throw new IllegalArgumentException("Candidate not found with ID: " + candidateId);
        }
        
        // Get available jobs
        var jobs = jobService.getAllJobs();
        
        // Calculate matches
        List<JobMatchDto> matches = jobMatchingEngine.calculateMatches(candidate, jobs);
        
        // Filter and limit results
        return matches.stream()
                .filter(match -> match.getOverallScore() >= minScore)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<CandidateDto> findBySkill(String skill) {
        return candidateRepository.findBySkill(skill).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<CandidateDto> findByExperienceRange(Integer minExp, Integer maxExp) {
        return candidateRepository.findByExperienceRange(minExp, maxExp).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CandidateDto convertToDto(Candidate candidate) {
        CandidateDto dto = new CandidateDto();
        dto.setId(candidate.getId());
        dto.setName(candidate.getName());
        dto.setEmail(candidate.getEmail());
        dto.setLocation(candidate.getLocation());
        dto.setPhone(candidate.getPhone());
        dto.setLinkedinUrl(candidate.getLinkedinUrl());
        dto.setYearsExperience(candidate.getYearsExperience());
        dto.setSkills(candidate.getSkills());
        dto.setJobTitles(candidate.getJobTitles());
        dto.setResumeText(candidate.getResumeText());
        return dto;
    }

    private Candidate convertToEntity(CandidateDto dto) {
        Candidate candidate = new Candidate();
        updateEntityFromDto(candidate, dto);
        return candidate;
    }

    private void updateEntityFromDto(Candidate candidate, CandidateDto dto) {
        candidate.setName(dto.getName());
        candidate.setEmail(dto.getEmail());
        candidate.setLocation(dto.getLocation());
        candidate.setPhone(dto.getPhone());
        candidate.setLinkedinUrl(dto.getLinkedinUrl());
        candidate.setYearsExperience(dto.getYearsExperience());
        candidate.setSkills(dto.getSkills());
        candidate.setJobTitles(dto.getJobTitles());
        candidate.setResumeText(dto.getResumeText());
    }
}