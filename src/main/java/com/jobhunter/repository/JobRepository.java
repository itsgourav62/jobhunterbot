package com.jobhunter.repository;

import com.jobhunter.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    
    List<Job> findByCompanyIgnoreCase(String company);
    
    @Query("SELECT j FROM Job j WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Job> findByTitleContainingIgnoreCase(@Param("title") String title);
    
    @Query("SELECT j FROM Job j WHERE j.remoteOk = true")
    List<Job> findRemoteJobs();
    
    @Query("SELECT j FROM Job j WHERE LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Job> findByLocationContainingIgnoreCase(@Param("location") String location);
    
    @Query("SELECT j FROM Job j JOIN j.requiredSkills s WHERE LOWER(s) = LOWER(:skill)")
    List<Job> findByRequiredSkill(@Param("skill") String skill);
    
    @Query("SELECT j FROM Job j WHERE j.postedAt >= :since ORDER BY j.postedAt DESC")
    List<Job> findRecentJobs(@Param("since") LocalDateTime since);
    
    @Query("SELECT j FROM Job j WHERE j.sourcePlatform = :platform")
    List<Job> findBySourcePlatform(@Param("platform") String platform);
    
    @Query("SELECT j FROM Job j WHERE " +
           "(:minExp IS NULL OR j.minExperience IS NULL OR j.minExperience <= :minExp) AND " +
           "(:maxExp IS NULL OR j.maxExperience IS NULL OR j.maxExperience >= :maxExp)")
    List<Job> findByExperienceRange(@Param("minExp") Integer minExp, @Param("maxExp") Integer maxExp);
}