package com.jobhunter.parser;

import com.jobhunter.model.Resume;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ResumeParser {

    private final Tika tika;

    public ResumeParser() {
        tika = new Tika();
    }

    public Resume parseResume(File file) throws IOException, TikaException {
        // Extract text from any supported file type
        String content = tika.parseToString(file);

        // Dummy parsing logic (replace with real extraction later)
        String name = extractName(content);
        String email = extractEmail(content);
        String phone = extractPhone(content);
        List<String> skills = extractSkills(content);
        List<String> experiences = extractExperiences(content);
        List<String> education = extractEducation(content);

        return new Resume(name, email, phone, skills, experiences, education);
    }

    // Placeholder extraction methods
    private String extractName(String content) {
        return content.split("\\n")[0]; // first line as name (simple placeholder)
    }

    private String extractEmail(String content) {
        String[] words = content.split("\\s+");
        for (String word : words) {
            if (word.contains("@")) return word;
        }
        return "";
    }

    private String extractPhone(String content) {
        String[] words = content.split("\\s+");
        for (String word : words) {
            if (word.matches(".*\\d{10}.*")) return word;
        }
        return "";
    }

    private List<String> extractSkills(String content) {
        if (content.toLowerCase().contains("java")) return Arrays.asList("Java");
        return Arrays.asList();
    }

    private List<String> extractExperiences(String content) {
        return Arrays.asList();
    }

    private List<String> extractEducation(String content) {
        return Arrays.asList();
    }
}
