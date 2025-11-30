package com.jobhunter.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "skill_ontology")
public class SkillOntology {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "canonical_skill", nullable = false, unique = true)
    private String canonicalSkill;
    
    @ElementCollection
    @CollectionTable(name = "skill_aliases", joinColumns = @JoinColumn(name = "skill_id"))
    @Column(name = "alias")
    private List<String> aliases;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "subcategory")
    private String subcategory;
    
    @Column(name = "popularity_score")
    private Integer popularityScore = 0;

    // Constructors
    public SkillOntology() {}

    public SkillOntology(String canonicalSkill, List<String> aliases, String category) {
        this.canonicalSkill = canonicalSkill;
        this.aliases = aliases;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCanonicalSkill() { return canonicalSkill; }
    public void setCanonicalSkill(String canonicalSkill) { this.canonicalSkill = canonicalSkill; }

    public List<String> getAliases() { return aliases; }
    public void setAliases(List<String> aliases) { this.aliases = aliases; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubcategory() { return subcategory; }
    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }

    public Integer getPopularityScore() { return popularityScore; }
    public void setPopularityScore(Integer popularityScore) { this.popularityScore = popularityScore; }
}