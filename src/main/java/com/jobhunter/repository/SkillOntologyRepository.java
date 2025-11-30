package com.jobhunter.repository;

import com.jobhunter.entity.SkillOntology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface SkillOntologyRepository extends JpaRepository<SkillOntology, Long> {
    
    Optional<SkillOntology> findByCanonicalSkillIgnoreCase(String canonicalSkill);
    
    @Query("SELECT s FROM SkillOntology s JOIN s.aliases a WHERE LOWER(a) = LOWER(:alias)")
    Optional<SkillOntology> findByAlias(@Param("alias") String alias);
    
    @Query("SELECT s.canonicalSkill FROM SkillOntology s WHERE LOWER(s.canonicalSkill) = LOWER(:skill) " +
           "OR :skill MEMBER OF s.aliases")
    Optional<String> findCanonicalSkillByNameOrAlias(@Param("skill") String skill);
    
    List<SkillOntology> findByCategory(String category);
    
    List<SkillOntology> findByCategoryAndSubcategory(String category, String subcategory);
    
    @Query("SELECT s FROM SkillOntology s ORDER BY s.popularityScore DESC")
    List<SkillOntology> findAllOrderByPopularityDesc();
}