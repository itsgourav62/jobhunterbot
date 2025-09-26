package com.jobhunter.analytics;

import com.jobhunter.model.Job;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Analytics service for job search performance and insights
 */
public class AnalyticsService {

    /**
     * Generate detailed job search report
     */
    public static JobSearchReport generateReport(List<Job> allJobs, Map<Job, Integer> scoredJobs, 
                                               List<Job> topJobs, String resumeName) {
        return new JobSearchReport(allJobs, scoredJobs, topJobs, resumeName);
    }

    /**
     * Job search report data class
     */
    public static class JobSearchReport {
        private final LocalDateTime timestamp;
        private final String candidateName;
        private final int totalJobsFetched;
        private final int highScoreJobs; // >= 70%
        private final int mediumScoreJobs; // 50-69%
        private final int lowScoreJobs; // < 50%
        private final double averageScore;
        private final List<String> topCompanies;
        private final List<String> topSkillsInDemand;
        private final String bestMatchedJob;
        private final int bestScore;

        public JobSearchReport(List<Job> allJobs, Map<Job, Integer> scoredJobs, 
                              List<Job> topJobs, String candidateName) {
            this.timestamp = LocalDateTime.now();
            this.candidateName = candidateName;
            this.totalJobsFetched = allJobs.size();
            
            // Score distribution
            this.highScoreJobs = (int) scoredJobs.values().stream().filter(score -> score >= 70).count();
            this.mediumScoreJobs = (int) scoredJobs.values().stream().filter(score -> score >= 50 && score < 70).count();
            this.lowScoreJobs = (int) scoredJobs.values().stream().filter(score -> score < 50).count();
            
            // Average score
            this.averageScore = scoredJobs.values().stream().mapToInt(Integer::intValue).average().orElse(0);
            
            // Top companies
            this.topCompanies = topJobs.stream()
                .map(Job::getCompany)
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
            
            // Skills in demand (simplified - could be more sophisticated)
            this.topSkillsInDemand = List.of("Java", "React", "Python", "Node.js", "Spring Boot");
            
            // Best match
            var bestMatch = scoredJobs.entrySet().stream()
                .max(Map.Entry.comparingByValue());
            
            if (bestMatch.isPresent()) {
                this.bestMatchedJob = bestMatch.get().getKey().getTitle() + " at " + bestMatch.get().getKey().getCompany();
                this.bestScore = bestMatch.get().getValue();
            } else {
                this.bestMatchedJob = "No jobs found";
                this.bestScore = 0;
            }
        }

        /**
         * Generate formatted text report
         */
        public String generateTextReport() {
            StringBuilder report = new StringBuilder();
            
            report.append("📊 **JOB HUNTER ANALYTICS REPORT**\n");
            report.append("=====================================\n\n");
            
            report.append("👤 **Candidate**: ").append(candidateName).append("\n");
            report.append("📅 **Date**: ").append(LocalDate.now()).append("\n");
            report.append("🕐 **Time**: ").append(timestamp.toLocalTime().toString().substring(0, 5)).append("\n\n");
            
            report.append("📈 **SEARCH METRICS**\n");
            report.append("• Total Jobs Analyzed: **").append(totalJobsFetched).append("**\n");
            report.append("• Average Match Score: **").append(String.format("%.1f", averageScore)).append("%**\n\n");
            
            report.append("🎯 **SCORE DISTRIBUTION**\n");
            report.append("• 🔥 High Potential (70%+): **").append(highScoreJobs).append(" jobs**\n");
            report.append("• 📊 Good Match (50-69%): **").append(mediumScoreJobs).append(" jobs**\n");
            report.append("• 📉 Low Match (<50%): **").append(lowScoreJobs).append(" jobs**\n\n");
            
            report.append("🏆 **BEST MATCH**\n");
            report.append("• ").append(bestMatchedJob).append("\n");
            report.append("• Match Score: **").append(bestScore).append("%**\n\n");
            
            report.append("🏢 **TOP COMPANIES**\n");
            for (int i = 0; i < Math.min(5, topCompanies.size()); i++) {
                report.append("• ").append(topCompanies.get(i)).append("\n");
            }
            
            report.append("\n💡 **INSIGHTS**\n");
            if (highScoreJobs > 5) {
                report.append("✅ Excellent! You have many high-potential opportunities\n");
            } else if (highScoreJobs > 0) {
                report.append("👍 Good! You have some strong matches to focus on\n");
            } else {
                report.append("💪 Consider expanding your skill set or adjusting search criteria\n");
            }
            
            return report.toString();
        }

        /**
         * Generate compact Discord-friendly report
         */
        public String generateDiscordReport() {
            StringBuilder report = new StringBuilder();
            
            report.append("🤖 **Job Hunt Summary** - ").append(LocalDate.now()).append("\n");
            report.append("👤 ").append(candidateName).append(" | 📊 ").append(totalJobsFetched).append(" jobs analyzed\n\n");
            
            report.append("**🎯 Results:**\n");
            report.append("🔥 ").append(highScoreJobs).append(" high-potential\n");
            report.append("📊 ").append(mediumScoreJobs).append(" good matches\n");
            report.append("📈 ").append(String.format("%.1f", averageScore)).append("% avg score\n\n");
            
            report.append("**🏆 Best Match (").append(bestScore).append("%):**\n");
            report.append(bestMatchedJob.length() > 100 ? bestMatchedJob.substring(0, 97) + "..." : bestMatchedJob);
            
            return report.toString();
        }

        // Getters
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getCandidateName() { return candidateName; }
        public int getTotalJobsFetched() { return totalJobsFetched; }
        public int getHighScoreJobs() { return highScoreJobs; }
        public double getAverageScore() { return averageScore; }
        public String getBestMatchedJob() { return bestMatchedJob; }
        public int getBestScore() { return bestScore; }
    }
}