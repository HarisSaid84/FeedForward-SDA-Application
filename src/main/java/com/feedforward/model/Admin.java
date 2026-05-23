package com.feedforward.model;

import java.time.LocalDateTime;

public class Admin extends User implements INotifiable {

    private String adminLevel;
    private LocalDateTime lastLogin;

    public Admin() {}

    public Admin(String name, String email, String password, String adminLevel) {
        super(name, email, password, "admin");
        this.adminLevel = adminLevel;
    }

    public Admin(int userID, String name, String email, String password,
                 String accountStatus, String adminLevel, LocalDateTime lastLogin) {
        super(userID, name, email, password, "admin", accountStatus);
        this.adminLevel = adminLevel;
        this.lastLogin = lastLogin;
    }

    public String getAdminLevel() { return adminLevel; }
    public LocalDateTime getLastLogin() { return lastLogin; }

    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    @Override
    public String getDashboardTitle() { return "Admin Dashboard"; }

    @Override
    public void receiveNotification(String msg) {
        System.out.println("Notification for admin " + name + ": " + msg);
    }

    @Override
    public String getContactInfo() {
        return "admin@feedforward.com";
    }

    // Admin manages user accounts
    public boolean manageUserAccount(int userID, String action) {
        System.out.println("Admin " + name
                + " performing action: " + action
                + " on user: " + userID);
        return true;
    }

    // Admin manages food listings
    public boolean manageListing(int listingID, String action) {
        System.out.println("Admin " + name
                + " performing action: " + action
                + " on listing: " + listingID);
        return true;
    }

    // Admin generates reports
    public Report generateReport(String type, String range) {
        return new Report(type, range, "Text",
                "Admin Report by: " + this.name);
    }

    // Admin exports reports
    public void exportReport(String format) {
        System.out.println("Admin exporting report as: " + format);
    }

    // Admin opens analytics dashboard
    public AnalyticsDashboard openAnalyticsDashboard() {
        return new AnalyticsDashboard();
    }
}