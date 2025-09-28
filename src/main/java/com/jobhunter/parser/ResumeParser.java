package com.jobhunter.parser;

import com.jobhunter.model.Resume;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResumeParser {

    public Resume parse(String filePath) throws IOException, TikaException {
        try (InputStream stream = Files.newInputStream(Paths.get(filePath))) {
            Tika tika = new Tika();
            String text = tika.parseToString(stream);
            return extractResumeFromText(text);
        }
    }

    public Resume parseFromUrl(String fileUrl) throws IOException, TikaException {
        try (InputStream stream = new URL(fileUrl).openStream()) {
            Tika tika = new Tika();
            String text = tika.parseToString(stream);
            return extractResumeFromText(text);
        }
    }

    private Resume extractResumeFromText(String text) {
        String name = extractName(text);
        String email = extractEmail(text);
        String phone = extractPhone(text);
        List<String> skills = extractSkills(text);
        List<String> experience = extractExperience(text);
        List<String> education = extractEducation(text);

        return new Resume(name, email, phone, skills, experience, education);
    }

    private String extractName(String text) {
        String[] lines = text.split("\\n");
        if (lines.length > 0) {
            return lines[0].trim();
        }
        return "";
    }

    private String extractEmail(String text) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    private String extractPhone(String text) {
        Pattern pattern = Pattern.compile("\\+?[0-9. ()-]{10,}");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    private List<String> extractSkills(String text) {
        Pattern pattern = Pattern.compile("Skills[:\\s]+([A-Za-z0-9, ]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Arrays.asList(matcher.group(1).split(","));
        }
        return List.of();
    }

    private List<String> extractExperience(String text) {
        Pattern pattern = Pattern.compile("Experience[:\\s]+([A-Za-z0-9, ]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Arrays.asList(matcher.group(1).split(","));
        }
        return List.of();
    }

    private List<String> extractEducation(String text) {
        Pattern pattern = Pattern.compile("Education[:\\s]+([A-Za-z0-9, ]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Arrays.asList(matcher.group(1).split(","));
        }
        return List.of();
    }
}
