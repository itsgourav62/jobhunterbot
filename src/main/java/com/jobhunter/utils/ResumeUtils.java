package com.jobhunter.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for handling resume files from various sources
 * Supports local files, Google Drive links, and other cloud storage
 */
public class ResumeUtils {

    private static final String TEMP_RESUME_DIR = System.getProperty("java.io.tmpdir") + "/jobhunter/";
    
    /**
     * Convert Google Drive view link to direct download link
     * Input:  https://drive.google.com/file/d/FILE_ID/view
     * Output: https://drive.google.com/uc?id=FILE_ID&export=download
     */
    public static String convertGoogleDriveLink(String originalLink) {
        if (originalLink == null || !originalLink.contains("drive.google.com")) {
            return originalLink;
        }
        
        // Extract file ID from various Google Drive URL formats
        String fileId = null;
        
        if (originalLink.contains("/file/d/")) {
            // Format: https://drive.google.com/file/d/FILE_ID/view
            int startIndex = originalLink.indexOf("/file/d/") + "/file/d/".length();
            int endIndex = originalLink.indexOf("/", startIndex);
            if (endIndex == -1) {
                endIndex = originalLink.length();
            }
            fileId = originalLink.substring(startIndex, endIndex);
        } else if (originalLink.contains("id=")) {
            // Format: https://drive.google.com/uc?id=FILE_ID&export=download
            int startIndex = originalLink.indexOf("id=") + 3;
            int endIndex = originalLink.indexOf("&", startIndex);
            if (endIndex == -1) {
                endIndex = originalLink.length();
            }
            fileId = originalLink.substring(startIndex, endIndex);
        }
        
        if (fileId != null && !fileId.isEmpty()) {
            return "https://drive.google.com/uc?id=" + fileId + "&export=download";
        }
        
        return originalLink;
    }
    
    /**
     * Download resume from URL to local temp file
     * Returns path to downloaded file
     */
    public static String downloadResumeIfNeeded(String resumePath) {
        if (resumePath == null || resumePath.isEmpty()) {
            System.out.println("⚠️ No resume path provided");
            return null;
        }
        
        // If it's already a local file path, return as-is
        if (!resumePath.startsWith("http")) {
            Path localPath = Paths.get(resumePath);
            if (Files.exists(localPath)) {
                System.out.println("✅ Using local resume: " + resumePath);
                return resumePath;
            } else {
                System.out.println("❌ Local resume file not found: " + resumePath);
                return null;
            }
        }
        
        // Convert Google Drive view links
        String downloadUrl = convertGoogleDriveLink(resumePath);
        System.out.println("📥 Downloading resume from: " + downloadUrl);
        
        try {
            // Create temp directory
            Path tempDir = Paths.get(TEMP_RESUME_DIR);
            Files.createDirectories(tempDir);
            
            // Download file
            URL url = URI.create(downloadUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "JobHunterBot/1.0");
            
            String filename = "resume_" + System.currentTimeMillis() + ".pdf";
            Path tempFile = tempDir.resolve(filename);
            
            try (InputStream inputStream = connection.getInputStream()) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
            String downloadedPath = tempFile.toAbsolutePath().toString();
            System.out.println("✅ Resume downloaded to: " + downloadedPath);
            return downloadedPath;
            
        } catch (Exception e) {
            System.out.println("❌ Failed to download resume: " + e.getMessage());
            System.out.println("💡 Tip: Make sure your Google Drive link is publicly accessible");
            System.out.println("💡 Original link: " + resumePath);
            System.out.println("💡 Converted link: " + downloadUrl);
            return null;
        }
    }
    
    /**
     * Validate resume file accessibility
     */
    public static boolean validateResumeAccess(String resumePath) {
        String localPath = downloadResumeIfNeeded(resumePath);
        return localPath != null && Files.exists(Paths.get(localPath));
    }
    
    /**
     * Clean up temp resume files
     */
    public static void cleanupTempFiles() {
        try {
            Path tempDir = Paths.get(TEMP_RESUME_DIR);
            if (Files.exists(tempDir)) {
                Files.walk(tempDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("resume_"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            System.out.println("🗑️ Cleaned up temp file: " + path.getFileName());
                        } catch (IOException e) {
                            // Ignore cleanup errors
                        }
                    });
            }
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }
}