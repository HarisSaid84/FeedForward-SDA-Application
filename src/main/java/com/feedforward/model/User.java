package com.feedforward.model;

public abstract class User {

    protected int userID;
    protected String name;
    protected String email;
    protected String password;
    protected String role;
    protected String accountStatus;

    public User() {}

    public User(int userID, String name, String email, String password, String role, String accountStatus) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.accountStatus = accountStatus;
    }

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.accountStatus = "active";
    }

    // Getters
    public int getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getAccountStatus() { return accountStatus; }

    // Setters
    public void setUserID(int userID) { this.userID = userID; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    // Abstract method - every user type must implement this
    public abstract String getDashboardTitle();

    @Override
    public String toString() {
        return "User{" + "userID=" + userID + ", name=" + name + ", role=" + role + "}";
    }
}