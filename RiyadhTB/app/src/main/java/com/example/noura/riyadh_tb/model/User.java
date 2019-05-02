package com.example.noura.riyadh_tb.model;

public class User {

    private String name;
    private String username;
    private String email;
    private String DOB;
    private String photoUrl;
    private String gender;
    private int TimeCredit;
    private String Interest;
    private String Skills;
    private String Experience;
    private int Rate;
    private String Comments;
    private boolean isAdmin;

    public User() {

    }

    public User(String name, String username, String email, String DOB, String photoUrl, String gender) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.DOB = DOB;
        this.photoUrl = photoUrl;
        this.gender = gender;
        TimeCredit = 3;
        Interest = "";
        Skills = "";
        Experience = "";
        Rate = 0;
        Comments = "";
        this.isAdmin = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getTimeCredit() {
        return TimeCredit;
    }

    public void setTimeCredit(int timeCredit) {
        TimeCredit = timeCredit;
    }

    public String getInterest() {
        return Interest;
    }

    public void setInterest(String interest) {
        Interest = interest;
    }

    public String getSkills() {
        return Skills;
    }

    public void setSkills(String skills) {
        Skills = skills;
    }

    public String getExperience() {
        return Experience;
    }

    public void setExperience(String experience) {
        Experience = experience;
    }

    public int getRate() {
        return Rate;
    }

    public void setRate(int rate) {
        Rate = rate;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
