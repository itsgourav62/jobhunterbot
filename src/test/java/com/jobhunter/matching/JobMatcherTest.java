//package com.jobhunter.matching;
//
//import com.jobhunter.model.CandidateProfile;
//import com.jobhunter.model.JobPosting;
//import com.jobhunter.model.MatchedJob;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class JobMatcherTest {
//
//    private JobMatcher matcher;
//    private CandidateProfile testProfile;
//
//    @BeforeEach
//    void setUp() {
//        matcher = new JobMatcher();
//        testProfile = new CandidateProfile();
//        testProfile.setSkills(Arrays.asList("Java", "Spring", "AWS"));
//        testProfile.setTargetTitles(List.of("Backend Engineer"));
//        testProfile.setPreferredLocations(List.of("Remote"));
//    }
//
//    @Test
//    void testMatchAndRankOrder() {
//        JobPosting perfectMatch = new JobPosting("1", "Backend Engineer", "A", "Remote", "Requires Java, Spring, AWS.", "url1");
//        JobPosting partialSkillMatch = new JobPosting("2", "Java Developer", "B", "Remote", "Core Java skills needed.", "url2");
//        JobPosting noMatch = new JobPosting("3", "Frontend Developer", "C", "Office", "Needs React.", "url3");
//
//        List<MatchedJob> result = matcher.matchAndRank(testProfile, Arrays.asList(perfectMatch, noMatch, partialSkillMatch));
//
//        assertEquals(2, result.size());
//        assertEquals(1.0, result.get(0).score(), 0.01, "Perfect match should have a score near 1.0");
//        assertEquals("1", result.get(0).job().id(), "Perfect match should be ranked first");
//        assertEquals("2", result.get(1).job().id(), "Partial match should be ranked second");
//    }
//
//    @Test
//    void testScoreCalculation() {
//        JobPosting job = new JobPosting("1", "Backend Engineer", "Company", "Remote", "We use Java and AWS.", "url");
//
//        // Profile has 3 skills: Java, Spring, AWS. Job description has 2 of them.
//        // Skill score = 2/3 = 0.66
//        // Title score = 1.0
//        // Location score = 1.0
//        // Total = (0.66 * 0.6) + (1.0 * 0.3) + (1.0 * 0.1) = 0.396 + 0.3 + 0.1 = 0.796
//        double expectedScore = (2.0/3.0 * 0.6) + (1.0 * 0.3) + (1.0 * 0.1);
//
//        List<MatchedJob> result = matcher.matchAndRank(testProfile, List.of(job));
//
//        assertEquals(1, result.size());
//        assertEquals(expectedScore, result.get(0).score(), 0.01);
//        assertTrue(result.get(0).reason().contains("Strong skill match"));
//        assertTrue(result.get(0).reason().contains("Relevant title"));
//        assertTrue(result.get(0).reason().contains("Location match"));
//    }
//}