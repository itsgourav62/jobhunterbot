package com.jobhunter.service;

import com.jobhunter.entity.SkillOntology;
import com.jobhunter.repository.SkillOntologyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillNormalizationService {

    @Autowired
    private SkillOntologyRepository skillOntologyRepository;

    private Map<String, String> normalizationCache = new HashMap<>();

    @PostConstruct
    public void initializeCache() {
        refreshNormalizationCache();
    }

    public void refreshNormalizationCache() {
        normalizationCache.clear();
        List<SkillOntology> skills = skillOntologyRepository.findAll();
        
        for (SkillOntology skill : skills) {
            String canonical = skill.getCanonicalSkill();
            
            // Add canonical skill to cache
            normalizationCache.put(canonical.toLowerCase(), canonical);
            
            // Add all aliases to cache
            if (skill.getAliases() != null) {
                for (String alias : skill.getAliases()) {
                    normalizationCache.put(alias.toLowerCase(), canonical);
                }
            }
        }
    }

    public List<String> normalizeSkills(List<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return new ArrayList<>();
        }

        return skills.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .map(this::normalizeSkill)
                .distinct()
                .collect(Collectors.toList());
    }

    public String normalizeSkill(String skill) {
        if (skill == null || skill.trim().isEmpty()) {
            return null;
        }

        String cleanSkill = skill.trim();
        String normalized = normalizationCache.get(cleanSkill.toLowerCase());
        
        return normalized != null ? normalized : cleanSkill;
    }

    public boolean isKnownSkill(String skill) {
        if (skill == null || skill.trim().isEmpty()) {
            return false;
        }
        return normalizationCache.containsKey(skill.trim().toLowerCase());
    }

    public List<String> getAllCanonicalSkills() {
        return skillOntologyRepository.findAll()
                .stream()
                .map(SkillOntology::getCanonicalSkill)
                .collect(Collectors.toList());
    }

    public List<SkillOntology> getSkillsByCategory(String category) {
        return skillOntologyRepository.findByCategory(category);
    }

    public SkillOntology addSkill(String canonicalSkill, List<String> aliases, String category) {
        SkillOntology skill = new SkillOntology(canonicalSkill, aliases, category);
        SkillOntology saved = skillOntologyRepository.save(skill);
        refreshNormalizationCache();
        return saved;
    }
}