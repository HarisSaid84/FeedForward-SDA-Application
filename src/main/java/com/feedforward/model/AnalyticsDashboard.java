package com.feedforward.model;

import com.feedforward.db.AnalyticsDAO;
import java.time.LocalDateTime;

/**
 * AnalyticsDashboard Model Class
 * Information Expert — aggregates and owns system-wide statistics.
 * Creator — creates Report objects (GRASP Creator pattern).
 * Monitored by Admin (see class diagram).
 *
 * Architecture note: this model class owns the statistics data and the
 * report-creation responsibility. The AnalyticsService in the business layer
 * coordinates DB retrieval and delegates to this model for report generation,
 * keeping the model as the single source of truth for analytics state.
 */
public class AnalyticsDashboard implements IReportable {

    private int dashboardID;
    private int foodSaved;
    private int wasteReduced;
    private int pickupsCompleted;
    private int totalListings;
    private int totalUsers;
    private LocalDateTime lastUpdated;

    /**
     * Default constructor — used when creating an empty dashboard shell.
     * Call updateAnalyticsData() to populate from DB.
     */
    public AnalyticsDashboard() {
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Full constructor — used when creating a populated dashboard.
     */
    public AnalyticsDashboard(int dashboardID, int foodSaved,
                              int wasteReduced, int pickupsCompleted) {
        this.dashboardID = dashboardID;
        this.foodSaved = foodSaved;
        this.wasteReduced = wasteReduced;
        this.pickupsCompleted = pickupsCompleted;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Information Expert — the dashboard owns and computes its own statistics.
     * Pulls fresh data from the DB via AnalyticsDAO and stores it in this model.
     * This wires the model to live database state, matching the class diagram.
     */
    public void updateAnalyticsData() {
        AnalyticsDAO dao = new AnalyticsDAO();
        this.foodSaved       = dao.getTotalFoodSaved();
        this.pickupsCompleted = dao.getTotalPickupsCompleted();
        this.totalListings   = dao.getTotalListings();
        this.totalUsers      = dao.getTotalUsers();
        this.wasteReduced    = this.foodSaved; // waste reduced tracks food redistributed
        this.lastUpdated     = LocalDateTime.now();
        System.out.println("AnalyticsDashboard updated at: " + lastUpdated);
    }

    /**
     * Information Expert — dashboard computes aggregate statistics from its own data.
     */
    public void computeWasteStatistics() {
        System.out.println("=== Waste Statistics ===");
        System.out.println("Food Saved      : " + foodSaved);
        System.out.println("Waste Reduced   : " + wasteReduced);
        System.out.println("Pickups Done    : " + pickupsCompleted);
        System.out.println("Total Listings  : " + totalListings);
        System.out.println("Total Users     : " + totalUsers);
    }

    /**
     * Apply filters to analytics data — re-queries DB with filter parameters.
     */
    public void applyFilters(String dateRange, String region, String foodType) {
        System.out.println("Applying filters - Date: " + dateRange
                + ", Region: " + region + ", FoodType: " + foodType);
        // Future: pass filters through to AnalyticsDAO for filtered queries
        updateAnalyticsData();
    }

    /**
     * Update charts display — signals the UI layer to refresh visual components.
     */
    public void updateCharts() {
        System.out.println("Updating analytics charts with latest data...");
    }

    /**
     * Creator — AnalyticsDashboard creates Report objects (GRASP Creator).
     * It has the initializing data needed to construct a Report.
     */
    @Override
    public Report generateReport(String type, String range) {
        String content = "========================================\n"
                + "       FEEDFORWARD ANALYTICS REPORT     \n"
                + "========================================\n"
                + "Report Type     : " + type + "\n"
                + "Date Range      : " + range + "\n"
                + "Generated       : " + LocalDateTime.now() + "\n"
                + "----------------------------------------\n"
                + "Food Saved      : " + foodSaved + "\n"
                + "Waste Reduced   : " + wasteReduced + "\n"
                + "Pickups Done    : " + pickupsCompleted + "\n"
                + "Total Listings  : " + totalListings + "\n"
                + "Total Users     : " + totalUsers + "\n"
                + "========================================\n";
        return new Report(type, range, "Text", content);
    }

    @Override
    public void exportReport(String format) {
        System.out.println("Exporting analytics report as: " + format);
    }

    // Getters
    public int getDashboardID()    { return dashboardID; }
    public int getFoodSaved()      { return foodSaved; }
    public int getWasteReduced()   { return wasteReduced; }
    public int getPickupsCompleted() { return pickupsCompleted; }
    public int getTotalListings()  { return totalListings; }
    public int getTotalUsers()     { return totalUsers; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }

    // Setters
    public void setDashboardID(int dashboardID)           { this.dashboardID = dashboardID; }
    public void setFoodSaved(int foodSaved)               { this.foodSaved = foodSaved; }
    public void setWasteReduced(int wasteReduced)         { this.wasteReduced = wasteReduced; }
    public void setPickupsCompleted(int pickupsCompleted) { this.pickupsCompleted = pickupsCompleted; }
    public void setTotalListings(int totalListings)       { this.totalListings = totalListings; }
    public void setTotalUsers(int totalUsers)             { this.totalUsers = totalUsers; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}