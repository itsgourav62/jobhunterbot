package com.jobhunter.autofill;

import java.util.Arrays;
import java.util.List;

public enum FormField {
    FIRST_NAME("First Name", "firstName", "first_name", "given-name"),
    LAST_NAME("Last Name", "lastName", "last_name", "family-name"),
    FULL_NAME("Full Name", "fullName", "full_name", "name"),
    EMAIL("Email", "email", "email_address"),
    PHONE("Phone", "phone", "phone_number", "tel"),
    LINKEDIN("LinkedIn", "linkedin", "linkedin_url"),
    PORTFOLIO("Portfolio", "portfolio", "website", "url"),
    RESUME_UPLOAD("Resume", "resume", "resume_upload", "file", "attachment"),
    COVER_LETTER_UPLOAD("Cover Letter", "cover_letter", "coverLetter"),
    SUBMIT("Submit", "submit", "apply");

    private final String displayName;
    private final List<String> identifiers;

    FormField(String displayName, String... identifiers) {
        this.displayName = displayName;
        this.identifiers = Arrays.asList(identifiers);
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }
}
