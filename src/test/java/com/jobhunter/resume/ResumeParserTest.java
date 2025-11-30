//package com.jobhunter.resume;
//
//import com.jobhunter.model.CandidateProfile;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ResumeParserTest {
//
//    private ResumeParser parser;
//
//    @BeforeEach
//    void setUp() {
//        parser = new ResumeParser();
//    }
//
//    @Test
//    void testParseExperienceAndSkills() {
//        String resumeText = "John Doe\n\nA Java developer with 8+ years of professional experience.\n\nCore Skills:\n- Java & Spring Boot\n- Docker and Kubernetes\n- SQL (PostgreSQL)";
//
//        CandidateProfile profile = parser.parse(resumeText);
//
//        assertEquals("John Doe", profile.getName());
//        assertEquals(8, profile.getYearsOfExperience());
//        assertTrue(profile.getSkills().containsAll(List.of("java", "spring boot", "docker", "kubernetes", "sql")));
//        assertFalse(profile.getSkills().contains("python"));
//    }
//
//    @Test
//    void testDefaultValuesWhenInfoIsMissing() {
//        String resumeText = "A junior developer.";
//
//        CandidateProfile profile = parser.parse(resumeText);
//
//        assertEquals("Default User", profile.getName());
//        assertEquals(3, profile.getYearsOfExperience()); // falls back to default
//        assertTrue(profile.getSkills().isEmpty());
//    }
//}