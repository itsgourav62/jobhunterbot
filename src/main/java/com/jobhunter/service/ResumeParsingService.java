package com.jobhunter.service;

import com.jobhunter.dto.CandidateDto;
import com.jobhunter.entity.Candidate;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeParsingService {

    @Autowired
    private SkillNormalizationService skillNormalizationService;

    private final Tika tika = new Tika();

    public CandidateDto parseResumeFromFile(MultipartFile file) throws Exception {
        String resumeText = tika.parseToString(file.getInputStream());
        return parseResumeFromText(resumeText);
    }

    public CandidateDto parseResumeFromText(String resumeText) {
        CandidateDto candidate = new CandidateDto();
        
        candidate.setName(extractName(resumeText));
        candidate.setEmail(extractEmail(resumeText));
        candidate.setPhone(extractPhone(resumeText));
        candidate.setLocation(extractLocation(resumeText));
        candidate.setLinkedinUrl(extractLinkedInUrl(resumeText));
        candidate.setYearsExperience(extractYearsOfExperience(resumeText));
        candidate.setJobTitles(extractJobTitles(resumeText));
        candidate.setSkills(extractAndNormalizeSkills(resumeText));
        candidate.setResumeText(resumeText);
        
        return candidate;
    }

    private String extractName(String resumeText) {
        String[] lines = resumeText.split("\\n");
        
        for (int i = 0; i < Math.min(5, lines.length); i++) {
            String line = lines[i].trim();
            
            // Skip obviously non-name lines
            if (line.isEmpty() || 
                line.toLowerCase().contains("resume") ||
                line.toLowerCase().contains("curriculum vitae") ||
                line.toLowerCase().contains("cv") ||
                line.contains("@") ||
                line.matches(".*\\d{3}.*")) {
                continue;
            }
            
            // Look for name-like patterns
            if (line.matches("^[A-Za-z\\s.'-]{2,50}$") && 
                line.split("\\s+").length >= 2 && 
                line.split("\\s+").length <= 5) {
                return line;
            }
        }
        
        return "Unknown";
    }

    private String extractEmail(String resumeText) {
        Pattern emailPattern = Pattern.compile(
            "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = emailPattern.matcher(resumeText);
        return matcher.find() ? matcher.group() : "";
    }

    private String extractPhone(String resumeText) {
        Pattern phonePattern = Pattern.compile(
            "(\\+?\\d{1,4}[\\s.-]?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = phonePattern.matcher(resumeText);
        return matcher.find() ? matcher.group() : "";
    }

    private String extractLocation(String resumeText) {
        // Look for location patterns
        Pattern locationPattern = Pattern.compile(
            "(?i)(location|address|based\\s+in|city|residing\\s+in)[:\\s]*([^\\n\\r]{1,100})",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = locationPattern.matcher(resumeText);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        
        // Look for city, state/country patterns
        Pattern cityStatePattern = Pattern.compile(
            "\\b([A-Z][a-zA-Z\\s]+),\\s*([A-Z]{2,3}|[A-Z][a-zA-Z\\s]+)\\b"
        );
        matcher = cityStatePattern.matcher(resumeText);
        if (matcher.find()) {
            return matcher.group().trim();
        }
        
        return "";
    }

    private String extractLinkedInUrl(String resumeText) {
        Pattern linkedInPattern = Pattern.compile(
            "linkedin\\.com/in/[A-Za-z0-9-]+",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = linkedInPattern.matcher(resumeText);
        if (matcher.find()) {
            return "https://www." + matcher.group();
        }
        return "";
    }

    private Integer extractYearsOfExperience(String resumeText) {
        // Pattern for "X years of experience"
        Pattern expPattern1 = Pattern.compile(
            "(\\d+)\\+?\\s*(?:years?|yrs?)\\s+(?:of\\s+)?(?:experience|exp)",
            Pattern.CASE_INSENSITIVE
        );
        
        // Pattern for "X+ years experience"
        Pattern expPattern2 = Pattern.compile(
            "(\\d+)\\+?\\s*(?:years?|yrs?)\\s+(?:experience|exp)",
            Pattern.CASE_INSENSITIVE
        );
        
        for (Pattern pattern : Arrays.asList(expPattern1, expPattern2)) {
            Matcher matcher = pattern.matcher(resumeText);
            if (matcher.find()) {
                try {
                    return Integer.parseInt(matcher.group(1));
                } catch (NumberFormatException e) {
                    // Continue to next pattern
                }
            }
        }
        
        // Fallback: count job positions and estimate
        return estimateExperienceFromPositions(resumeText);
    }

    private Integer estimateExperienceFromPositions(String resumeText) {
        // Simple heuristic: count years in date ranges
        Pattern yearRangePattern = Pattern.compile(
            "(20\\d{2})\\s*[-–—to]\\s*(20\\d{2}|present|current)",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = yearRangePattern.matcher(resumeText);
        int totalYears = 0;
        
        while (matcher.find()) {
            try {
                int startYear = Integer.parseInt(matcher.group(1));
                String endYearStr = matcher.group(2).toLowerCase();
                
                int endYear;
                if (endYearStr.equals("present") || endYearStr.equals("current")) {
                    endYear = java.time.Year.now().getValue();
                } else {
                    endYear = Integer.parseInt(endYearStr);
                }
                
                totalYears += Math.max(0, endYear - startYear);
            } catch (NumberFormatException e) {
                // Skip invalid year ranges
            }
        }
        
        return Math.min(totalYears, 50); // Cap at 50 years
    }

    private List<String> extractJobTitles(String resumeText) {
        List<String> titles = new ArrayList<>();
        
        // Common title keywords and patterns
        String[] titleKeywords = {
            "Software Engineer", "Developer", "Programmer", "Analyst", "Manager", 
            "Architect", "Lead", "Senior", "Junior", "Principal", "Staff",
            "Director", "Vice President", "VP", "CTO", "CEO", "Consultant",
            "Specialist", "Coordinator", "Administrator", "Designer"
        };
        
        String[] lines = resumeText.split("\\n");
        
        for (String line : lines) {
            line = line.trim();
            
            // Skip obviously non-title lines
            if (line.length() > 100 || line.isEmpty()) {
                continue;
            }
            
            // Check if line contains title keywords
            for (String keyword : titleKeywords) {
                if (line.toLowerCase().contains(keyword.toLowerCase()) && 
                    !titles.contains(line) && 
                    titles.size() < 10) {
                    titles.add(line);
                    break;
                }
            }
        }
        
        return titles;
    }

    private List<String> extractAndNormalizeSkills(String resumeText) {
        Set<String> extractedSkills = new HashSet<>();
        
        // Technical skills to look for
        String[] technicalSkills = {
            // Programming Languages
            "Java", "Python", "JavaScript", "TypeScript", "C++", "C#", "Go", "Rust", 
            "PHP", "Ruby", "Swift", "Kotlin", "Scala", "R", "Matlab", "Perl",
            
            // Web Technologies
            "React", "Angular", "Vue", "Node.js", "Express", "Django", "Flask", 
            "Spring", "Spring Boot", "Laravel", "Rails", "ASP.NET",
            
            // Frontend
            "HTML", "CSS", "SASS", "LESS", "Bootstrap", "Tailwind", "jQuery",
            
            // Databases
            "MySQL", "PostgreSQL", "MongoDB", "Redis", "Oracle", "SQLite", 
            "Cassandra", "DynamoDB", "Neo4j", "Elasticsearch",
            
            // Cloud & DevOps
            "AWS", "Azure", "GCP", "Docker", "Kubernetes", "Jenkins", "GitLab CI",
            "Terraform", "Ansible", "Chef", "Puppet", "Nginx", "Apache",
            
            // Data & Analytics
            "Hadoop", "Spark", "Kafka", "Airflow", "Tableau", "Power BI",
            "TensorFlow", "PyTorch", "Pandas", "NumPy", "Scikit-learn",
            
            // Tools & Methodologies
            "Git", "SVN", "Jira", "Confluence", "Agile", "Scrum", "Kanban",
            "REST", "GraphQL", "Microservices", "API", "JSON", "XML"
        };
        
        String resumeLower = resumeText.toLowerCase();
        
        for (String skill : technicalSkills) {
            if (resumeLower.contains(skill.toLowerCase())) {
                extractedSkills.add(skill);
            }
        }
        
        // Normalize skills using the skill ontology
        return skillNormalizationService.normalizeSkills(new ArrayList<>(extractedSkills));
    }
}