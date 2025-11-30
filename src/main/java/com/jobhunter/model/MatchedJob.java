package com.jobhunter.model;

public record MatchedJob(
    JobPosting job,
    double score,
    String reason
) implements Comparable<MatchedJob> {
    @Override
    public int compareTo(MatchedJob other) {
        return Double.compare(other.score, this.score);
    }
}
