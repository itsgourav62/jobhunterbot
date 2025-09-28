package com.jobhunter.parser;

import com.jobhunter.model.Resume;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResumeParser {

    private final Tika tika;
    private final HttpClient httpClient;

    public ResumeParser() {
        tika = new Tika();
        httpClient = HttpClient.newHttpClient();
    }

    /**
     * Parse resume from Google Drive URL or local file
     */
    public Resume parseResumeFromUrl(String resumePath) throws IOException, TikaException {
        if (resumePath.startsWith("http")) {
            // Download from URL (Google Drive, etc.)
            return parseResumeFromRemoteUrl(resumePath);
        } else {
            // Local file
            return parseResume(new File(resumePath));
        }
    }

    /**
     * Parse resume from remote URL (Google Drive)
     */
    private Resume parseResumeFromRemoteUrl(String urlString) throws IOException, TikaException {
        System.out.println("📄 Downloading resume from: " + urlString.substring(0, Math.min(50, urlString.length())) + "...");
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .header("User-Agent", "JobHunterBot/2.0")
                    .build();
            
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            
            if (response.statusCode() == 200) {
                // Create temporary file
                File tempFile = File.createTempFile("resume", ".pdf");
                tempFile.deleteOnExit();
                
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(response.body());
                }
                
                System.out.println("✅ Resume downloaded successfully (" + response.body().length + " bytes)");
                return parseResume(tempFile);
            } else {
                throw new IOException("Failed to download resume. Status code: " + response.statusCode());
            }
        } catch (InterruptedException e) {
            throw new IOException("Download interrupted", e);
        }
    }

    /**
     * Parse resume from local file
     */
    public Resume parseResume(File file) throws IOException, TikaException {
        System.out.println("📄 Parsing resume from: " + file.getName());
        
        // Extract text from any supported file type
        String content = tika.parseToString(file);
        
        if (content == null || content.trim().isEmpty()) {
            throw new IOException("Resume file is empty or could not be parsed");
        }
        
        System.out.println("✅ Resume text extracted (" + content.length() + " characters)");
        
        // Extract information using improved parsing
        String name = extractName(content);
        String email = extractEmail(content);
        String phone = extractPhone(content);
        List<String> skills = extractSkills(content);
        List<String> experiences = extractExperiences(content);
        List<String> education = extractEducation(content);

        System.out.println("📋 Parsed Resume Info:");
        System.out.println("   Name: " + name);
        System.out.println("   Email: " + email);
        System.out.println("   Skills: " + skills.size() + " found");
        System.out.println("   Experience: " + experiences.size() + " entries");

        return new Resume(name, email, phone, skills, experiences, education);
    }

    /**
     * Extract name from resume content
     */
    private String extractName(String content) {
        String[] lines = content.split("\\n");
        
        // Look for name patterns in first few lines
        for (int i = 0; i < Math.min(5, lines.length); i++) {
            String line = lines[i].trim();
            
            // Skip empty lines and common headers
            if (line.isEmpty() || line.toLowerCase().contains("resume") || 
                line.toLowerCase().contains("curriculum")) {
                continue;
            }
            
            // Name is usually 2-4 words, mostly alphabetic
            if (line.matches("^[A-Za-z\\s]{2,50}$") && line.split("\\s+").length <= 4) {
                return line;
            }
        }
        
        // Fallback: use environment variable or default
        String envName = System.getenv("JOB_HUNTER_NAME");
        return (envName != null && !envName.isEmpty()) ? envName : "Job Hunter";
    }

    /**
     * Extract email from resume content
     */
    private String extractEmail(String content) {
        Pattern emailPattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
        Matcher matcher = emailPattern.matcher(content);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        // Fallback: use environment variable
        String envEmail = System.getenv("JOB_HUNTER_EMAIL");
        return (envEmail != null && !envEmail.isEmpty()) ? envEmail : "jobhunter@example.com";
    }

    /**
     * Extract phone number from resume content
     */
    private String extractPhone(String content) {
        // Pattern for various phone number formats
        Pattern phonePattern = Pattern.compile("(\\+\\d{1,3}[\\s.-]?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}");
        Matcher matcher = phonePattern.matcher(content);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        // Fallback: use environment variable
        String envPhone = System.getenv("JOB_HUNTER_PHONE");
        return (envPhone != null && !envPhone.isEmpty()) ? envPhone : "+1-000-000-0000";
    }

    /**
     * Extract skills from resume content
     */
    private List<String> extractSkills(String content) {
        List<String> skills = new ArrayList<>();
        String lowerContent = content.toLowerCase();
        
        // Common tech skills to look for
        String[] techSkills = {
            "java", "python", "javascript", "react", "nodejs", "node.js", "angular", "vue",
            "spring", "springboot", "spring boot", "django", "flask", "express",
            "sql", "mysql", "postgresql", "mongodb", "redis", "elasticsearch",
            "docker", "kubernetes", "aws", "azure", "gcp", "git", "jenkins",
            "html", "css", "bootstrap", "tailwind", "sass", "less",
            "typescript", "kotlin", "scala", "go", "rust", "c++", "c#",
            "microservices", "rest", "graphql", "api", "json", "xml",
            "junit", "testing", "tdd", "agile", "scrum", "devops",
            "machine learning", "ai", "data science", "pandas", "numpy"
        };
        
        for (String skill : techSkills) {
            if (lowerContent.contains(skill.toLowerCase())) {
                skills.add(capitalizeFirst(skill));
            }
        }
        
        // Remove duplicates and sort
        skills = new ArrayList<>(new LinkedHashSet<>(skills));
        Collections.sort(skills);
        
        // If no skills found, add some defaults based on environment or assume Java developer
        if (skills.isEmpty()) {
            skills.addAll(Arrays.asList("Java", "Spring Boot", "React", "SQL", "Git"));
        }
        
        return skills;
    }

    /**
     * Extract work experience from resume content
     */
    private List<String> extractExperiences(String content) {
        List<String> experiences = new ArrayList<>();
        
        // Look for experience sections
        String[] lines = content.split("\\n");
        boolean inExperienceSection = false;
        
        for (String line : lines) {
            String lowerLine = line.toLowerCase().trim();
            
            // Detect experience section headers
            if (lowerLine.contains("experience") || lowerLine.contains("work") || 
                lowerLine.contains("employment") || lowerLine.contains("career")) {
                inExperienceSection = true;
                continue;
            }
            
            // Stop if we hit another section
            if (inExperienceSection && (lowerLine.contains("education") || 
                lowerLine.contains("skills") || lowerLine.contains("projects"))) {
                break;
            }
            
            // Extract experience entries
            if (inExperienceSection && line.trim().length() > 20) {
                // Look for job titles or company names
                if (line.matches(".*\\b(Developer|Engineer|Manager|Analyst|Consultant|Lead|Senior|Junior)\\b.*")) {
                    experiences.add(line.trim());
                }
            }
        }
        
        // Fallback: create default experience
        if (experiences.isEmpty()) {
            experiences.add("Software Developer - 3+ years experience");
            experiences.add("Full Stack Development");
        }
        
        return experiences;
    }

    /**
     * Extract education from resume content
     */
    private List<String> extractEducation(String content) {
        List<String> education = new ArrayList<>();
        String lowerContent = content.toLowerCase();
        
        // Look for degree keywords
        String[] degreeKeywords = {
            "bachelor", "master", "phd", "mba", "degree", "university", "college",
            "computer science", "engineering", "information technology", "software"
        };
        
        for (String keyword : degreeKeywords) {
            if (lowerContent.contains(keyword)) {
                education.add(capitalizeFirst(keyword));
                break; // Only add one education entry to avoid duplicates
            }
        }
        
        if (education.isEmpty()) {
            education.add("Bachelor's Degree in Computer Science");
        }
        
        return education;
    }

    /**
     * Capitalize first letter of a string
     */
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}