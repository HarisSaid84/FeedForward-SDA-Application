package com.feedforward.business;

import com.feedforward.model.*;
import java.util.List;

/**
 * GRASP Controller Pattern
 * SystemController is the central controller that receives all system events
 * from the UI layer and delegates to appropriate domain objects.
 * This keeps UI decoupled from business logic.
 */
public class SystemController {

    private static SystemController instance = null;

    private UserService userService;
    private FoodListingService foodListingService;
    private PickupService pickupService;
    private AnalyticsService analyticsService;
    private AIMatchingService aiMatchingService;

    // Singleton pattern - only one controller exists
    private SystemController() {
        this.userService = new UserService();
        this.foodListingService = new FoodListingService();
        this.pickupService = new PickupService();
        this.analyticsService = new AnalyticsService();
        this.aiMatchingService = new AIMatchingService();
    }

    public static SystemController getInstance() {
        if (instance == null) {
            instance = new SystemController();
        }
        return instance;
    }

    // UC-01 Register
    public boolean handleRegister(String name, String email,
                                  String password, String role) {
        String result = "";
        switch (role) {
            case "donor":
                result = userService.registerDonor(name, email, password, "", "", "");
                break;
            case "ngo":
                result = userService.registerNGO(name, email, password, "", "", "");
                break;
            case "volunteer":
                result = userService.registerVolunteer(name, email, password, "", "", "");
                break;
        }
        return result.equals("success");
    }

    // UC-02 Login
    public User handleLogin(String email, String password) {
        User user = userService.login(email, password);
        if (user != null) {
            Session.setCurrentUser(user);
        }
        return user;
    }

    // UC-03 List Surplus Food
    public String handleListFood(int donorID, String foodName,
                                 String quantity, String expiry, String location) {
        return foodListingService.addListing(donorID, foodName, quantity, expiry, location);
    }

    // UC-04 Search Listings
    public List<FoodListing> handleSearchListings(String keyword,
                                                  String foodType, String location) {
        return foodListingService.searchListings(keyword, location);
    }

    // UC-05 Schedule Pickup
    public String handleSchedulePickup(int userID, int listingID,
                                       String date, String time, String transport) {
        return pickupService.schedulePickup(listingID, userID, date, time, transport);
    }

    // UC-06 Confirm Pickup
    public boolean handleConfirmPickup(int pickupID) {
        String result = pickupService.confirmPickup(
                pickupID,
                Session.getCurrentUserID(),
                Session.getCurrentUserID()
        );
        return result.equals("success");
    }

    // UC-07 Track Pickup
    public String handleTrackPickup(int pickupID) {
        List<PickupSchedule> pickups = pickupService.getUserPickups(
                Session.getCurrentUserID());
        for (PickupSchedule p : pickups) {
            if (p.getPickupID() == pickupID) {
                return p.getStatus();
            }
        }
        return "Not Found";
    }

    // UC-08 AI Matching
    public List<String[]> handleAIMatching(String location) {
        return aiMatchingService.getMatchResults(location);
    }

    // UC-09 Generate Report
    public Report handleGenerateReport(int userID, String type,
                                       String range, String format) {
        String content = analyticsService.buildReportText(type, range);
        analyticsService.generateReport(userID, type, range, format);
        return new Report(type, range, format, content);
    }

    // UC-10 Analytics Dashboard
    public int[] handleOpenDashboard() {
        return new int[]{
                analyticsService.getTotalListings(),
                analyticsService.getTotalPickupsCompleted(),
                analyticsService.getTotalFoodSaved(),
                analyticsService.getTotalUsers(),
                analyticsService.getTotalDonors(),
                analyticsService.getTotalNGOs()
        };
    }

    // UC-11 Manage Users
    public boolean handleManageUser(int adminID, int userID, String action) {
        switch (action) {
            case "suspend": return userService.suspendUser(userID);
            case "activate": return userService.activateUser(userID);
            case "delete": return userService.deleteUser(userID);
            default: return false;
        }
    }

    // UC-12 Manage Listings
    public boolean handleManageListing(int adminID, int listingID, String action) {
        switch (action) {
            case "remove": return foodListingService.removeListing(listingID);
            case "flag": return foodListingService.flagListing(listingID);
            case "collected": return foodListingService.markCollected(listingID);
            default: return false;
        }
    }
}