package com.feedforward.business;

import com.feedforward.db.FoodListingDAO;
import com.feedforward.db.NotificationDAO;
import com.feedforward.db.UserDAO;
import com.feedforward.model.FoodListing;
import java.time.LocalDateTime;
import java.util.List;

public class FoodListingService {

    private FoodListingDAO listingDAO;
    private NotificationDAO notificationDAO;
    private AIMatchingService aiMatchingService;

    public FoodListingService() {
        this.listingDAO = new FoodListingDAO();
        this.notificationDAO = new NotificationDAO();
        this.aiMatchingService = new AIMatchingService();
    }

    public String addListing(int userID, String foodName, String quantityStr,
                             String expiryStr, String location) {
        if (foodName.isEmpty() || quantityStr.isEmpty() || expiryStr.isEmpty() || location.isEmpty())
            return "All fields are required!";

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) return "Quantity must be greater than 0!";
        } catch (NumberFormatException e) {
            return "Quantity must be a number!";
        }

        LocalDateTime expiry;
        try {
            expiry = LocalDateTime.parse(expiryStr.replace(" ", "T"));
        } catch (Exception e) {
            return "Invalid expiry format! Use: yyyy-MM-dd HH:mm";
        }

        if (expiry.isBefore(LocalDateTime.now())) return "Expiry time must be in the future!";

        FoodListing listing = new FoodListing(userID, foodName, quantity, expiry, location, "");
        boolean saved = listingDAO.addListing(listing);
        if (!saved) return "Failed to save listing!";

        // Trigger AI Matching after listing is saved
        aiMatchingService.runMatching(foodName, location);
        return "success";
    }

    public List<FoodListing> getAvailableListings() {
        return listingDAO.getAllAvailableListings();
    }

    public List<FoodListing> getDonorListings(int userID) {
        return listingDAO.getListingsByDonor(userID);
    }

    public List<FoodListing> searchListings(String keyword, String location) {
        return listingDAO.searchListings(keyword, location);
    }

    public List<FoodListing> getAllListings() {
        return listingDAO.getAllListings();
    }

    public boolean removeListing(int listingID) {
        return listingDAO.deleteListing(listingID);
    }

    public boolean flagListing(int listingID) {
        return listingDAO.updateListingStatus(listingID, "flagged");
    }

    public boolean markCollected(int listingID) {
        return listingDAO.updateListingStatus(listingID, "collected");
    }
}