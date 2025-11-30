package com.jobhunter.repository;

import com.jobhunter.entity.JobMatch;
import com.jobhunter.entity.Candidate;
import com.jobhunter.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobMatchRepository extends JpaRepository<JobMatch, UUID> {
    
    List<JobMatch> findByCandidateOrderByOverallScoreDesc(Candidate candidate);
    
    List<JobMatch> findByJobOrderByOverallScoreDesc(Job job);
    
    @Query("SELECT jm FROM JobMatch jm WHERE jm.candidate.id = :candidateId AND jm.overallScore >= :minScore ORDER BY jm.overallScore DESC")
    List<JobMatch> findTopMatchesForCandidate(@Param("candidateId") UUID candidateId, @Param("minScore") Double minScore);
    
    @Query("SELECT jm FROM JobMatch jm WHERE jm.job.id = :jobId AND jm.overallScore >= :minScore ORDER BY jm.overallScore DESC")
    List<JobMatch> findTopCandidatesForJob(@Param("jobId") UUID jobId, @Param("minScore") Double minScore);
    
    boolean existsByCandidateAndJob(Candidate candidate, Job job);
}