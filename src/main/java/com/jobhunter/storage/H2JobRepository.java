package com.jobhunter.storage;

import java.sql.*;

public class H2JobRepository implements JobRepository {

    private final Connection connection;

    public H2JobRepository(String dbPath) {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:" + dbPath, "sa", "");
            createTableIfNotExists();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to initialize H2 database", e);
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS applied_jobs (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "url VARCHAR(255) NOT NULL UNIQUE," +
                     "title VARCHAR(255)," +
                     "company VARCHAR(255)," +
                     "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                     ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    @Override
    public boolean hasApplied(String url) {
        String sql = "SELECT COUNT(*) FROM applied_jobs WHERE url = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, url);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if job has been applied: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void markAsApplied(String url, String title, String company) {
        String sql = "INSERT INTO applied_jobs (url, title, company) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, url);
            ps.setString(2, title);
            ps.setString(3, company);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (!e.getSQLState().equals("23505")) { // Ignore unique constraint violation
                System.err.println("Error marking job as applied: " + e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
