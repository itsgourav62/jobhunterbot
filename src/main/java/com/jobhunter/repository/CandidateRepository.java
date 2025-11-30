package com.jobhunter.repository;

import com.jobhunter.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, UUID> {
    
    Optional<Candidate> findByEmail(String email);
    
    @Query("SELECT c FROM Candidate c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Candidate> findByNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT c FROM Candidate c JOIN c.skills s WHERE LOWER(s) = LOWER(:skill)")
    List<Candidate> findBySkill(@Param("skill") String skill);
    
    @Query("SELECT c FROM Candidate c WHERE c.yearsExperience >= :minExp AND c.yearsExperience <= :maxExp")
    List<Candidate> findByExperienceRange(@Param("minExp") Integer minExp, @Param("maxExp") Integer maxExp);
}