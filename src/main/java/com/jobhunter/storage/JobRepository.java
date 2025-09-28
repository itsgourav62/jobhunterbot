package com.jobhunter.storage;

public interface JobRepository {

    boolean hasApplied(String url);

    void markAsApplied(String url, String title, String company);

    void close();
}

