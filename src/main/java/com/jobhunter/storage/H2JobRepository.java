package com.jobhunter.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class H2JobRepository implements JobRepository {

    private static final String DB_URL = "jdbc:h2:./jobhunter_db"; // file-based DB
    private static final String USER = "sa";
    private static final String PASS = "";

    public H2JobRepository() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            // Create table if not exists
            String sql = "CREATE TABLE IF NOT EXISTS applied_jobs (" +
                    "job_id VARCHAR(255) PRIMARY KEY, " +
                    "title VARCHAR(255), " +
                    "company VARCHAR(255), " +
                    "url VARCHAR(512), " +
                    "applied_at TIMESTAMP)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(AppliedJob job) {
        String sql = "MERGE INTO applied_jobs KEY(job_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, job.getJobId());
            pstmt.setString(2, job.getTitle());
            pstmt.setString(3, job.getCompany());
            pstmt.setString(4, job.getUrl());
            pstmt.setTimestamp(5, Timestamp.valueOf(job.getAppliedAt()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isAlreadyApplied(String jobId) {
        String sql = "SELECT COUNT(*) FROM applied_jobs WHERE job_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, jobId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<AppliedJob> getAllAppliedJobs() {
        List<AppliedJob> jobs = new ArrayList<>();
        String sql = "SELECT * FROM applied_jobs";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AppliedJob job = new AppliedJob(
                        rs.getString("job_id"),
                        rs.getString("title"),
                        rs.getString("company"),
                        rs.getString("url"),
                        rs.getTimestamp("applied_at").toLocalDateTime()
                );
                jobs.add(job);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobs;
    }
}
