package com.jobhunter.model;

import java.util.List;

public class Resume {
    private String name;
    private String email;
    private String phone;
    private List<String> skills;
    private List<String> experiences;
    private List<String> education;

    // Constructors
    public Resume() {}

    public Resume(String name, String email, String phone,
                  List<String> skills, List<String> experiences, List<String> education) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
        this.experiences = experiences;
        this.education = education;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<String> getExperiences() { return experiences; }
    public void setExperiences(List<String> experiences) { this.experiences = experiences; }

    public List<String> getEducation() { return education; }
    public void setEducation(List<String> education) { this.education = education; }

    @Override
    public String toString() {
        return "Resume{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", skills=" + skills +
                ", experiences=" + experiences +
                ", education=" + education +
                '}';
    }
}
