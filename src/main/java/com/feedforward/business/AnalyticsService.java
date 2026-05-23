package com.feedforward.business;

import com.feedforward.db.AnalyticsDAO;
import java.sql.ResultSet;

public class AnalyticsService {

    private AnalyticsDAO analyticsDAO;

    public AnalyticsService() {
        this.analyticsDAO = new AnalyticsDAO();
    }

    public int getTotalFoodSaved() {
        return analyticsDAO.getTotalFoodSaved();
    }

    public int getTotalPickupsCompleted() {
        return analyticsDAO.getTotalPickupsCompleted();
    }

    public int getTotalListings() {
        return analyticsDAO.getTotalListings();
    }

    public int getTotalUsers() {
        return analyticsDAO.getTotalUsers();
    }

    public int getTotalDonors() {
        return analyticsDAO.getTotalDonors();
    }

    public int getTotalNGOs() {
        return analyticsDAO.getTotalNGOs();
    }

    public ResultSet getListingsByLocation() {
        return analyticsDAO.getListingsByLocation();
    }

    public boolean generateReport(int userID, String reportType, String dateRange, String format) {
        return analyticsDAO.saveReport(userID, reportType, dateRange, format);
    }

    public String buildReportText(String reportType, String dateRange) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("       FEEDFORWARD SOLUTIONS REPORT     \n");
        sb.append("========================================\n");
        sb.append("Report Type : ").append(reportType).append("\n");
        sb.append("Date Range  : ").append(dateRange).append("\n");
        sb.append("Generated   : ").append(java.time.LocalDateTime.now()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Total Listings     : ").append(getTotalListings()).append("\n");
        sb.append("Total Users        : ").append(getTotalUsers()).append("\n");
        sb.append("Total Donors       : ").append(getTotalDonors()).append("\n");
        sb.append("Total NGOs         : ").append(getTotalNGOs()).append("\n");
        sb.append("Pickups Completed  : ").append(getTotalPickupsCompleted()).append("\n");
        sb.append("Food Units Saved   : ").append(getTotalFoodSaved()).append("\n");
        sb.append("========================================\n");
        return sb.toString();
    }
}