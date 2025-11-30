package com.jobhunter.service;

import com.jobhunter.model.CandidateProfile;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private final Tika tika = new Tika();

    private static final Set<String> SKILL_KEYWORDS = Set.of(
            "java", "spring", "spring boot", "sql", "postgresql", "mysql", "docker", "aws", "kubernetes", "python",
            "javascript", "react", "go", "microservices", "rest", "api", "git", "maven", "gradle", "jenkins", "kafka"
    );

    public CandidateProfile parse(MultipartFile resumeFile) throws IOException, TikaException {
        String text = tika.parseToString(resumeFile.getInputStream());
        return parseText(text);
    }

    public CandidateProfile parse(String resumeText) {
        return parseText(resumeText);
    }

    private CandidateProfile parseText(String text) {
        return new CandidateProfile(
                extractName(text),
                extractYearsOfExperience(text),
                extractSkills(text),
                extractTargetTitles(text)
        );
    }

    private String extractName(String text) {
        Pattern p = Pattern.compile("^([A-Z][a-z]+(?:\s[A-Z][a-z]+)+)", Pattern.MULTILINE);
        Matcher m = p.matcher(text);
        return m.find() ? m.group(1).trim() : "Resume User";
    }

    private int extractYearsOfExperience(String text) {
        Pattern p = Pattern.compile("(\\d+)\\s*\\+?\\s*years", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        return m.find() ? Integer.parseInt(m.group(1)) : 3; // Default
    }

    private List<String> extractSkills(String text) {
        String lowerCaseText = text.toLowerCase();
        return SKILL_KEYWORDS.stream()
                .filter(lowerCaseText::contains)
                .collect(Collectors.toList());
    }

    private List<String> extractTargetTitles(String text) {
        String lowerCaseText = text.toLowerCase();
        if (lowerCaseText.contains("backend") || lowerCaseText.contains("java developer")) {
            return Arrays.asList("Backend Engineer", "Software Engineer", "Java Developer");
        }
        return List.of("Software Engineer");
    }
}
