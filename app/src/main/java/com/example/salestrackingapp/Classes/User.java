package com.example.salestrackingapp.Classes;

public class User {
    private String userId;
    private String email;
    private String companyName;
    private  String password;
    private String role; // New field for user role
    private String companyId;

    private String fullName;

    // Empty constructor required for Firestore
    public User() {
    }



    public User(String userId, String email, String companyName, String role, String password, String fullName) {
        this.userId = userId;
        this.email = email;
        this.companyName = companyName;
        this.role = role;
        this.password = password;
        this.fullName = fullName;
    }

// Getters and setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
