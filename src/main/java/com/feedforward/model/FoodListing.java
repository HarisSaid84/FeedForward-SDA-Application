package com.feedforward.model;

import java.time.LocalDateTime;

public class FoodListing {

    private int listingID;
    private int userID;
    private String foodName;
    private int quantity;
    private LocalDateTime expiryTime;
    private String location;
    private String photo;
    private String status;
    private LocalDateTime datePosted;
    private String donorName;

    public FoodListing() {}

    public FoodListing(int userID, String foodName, int quantity,
                       LocalDateTime expiryTime, String location, String photo) {
        this.userID = userID;
        this.foodName = foodName;
        this.quantity = quantity;
        this.expiryTime = expiryTime;
        this.location = location;
        this.photo = photo;
        this.status = "available";
        this.datePosted = LocalDateTime.now();
    }

    public int getListingID() { return listingID; }
    public int getUserID() { return userID; }
    public String getFoodName() { return foodName; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getExpiryTime() { return expiryTime; }
    public String getLocation() { return location; }
    public String getPhoto() { return photo; }
    public String getStatus() { return status; }
    public LocalDateTime getDatePosted() { return datePosted; }
    public String getDonorName() { return donorName; }

    public void setListingID(int listingID) { this.listingID = listingID; }
    public void setUserID(int userID) { this.userID = userID; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }
    public void setLocation(String location) { this.location = location; }
    public void setPhoto(String photo) { this.photo = photo; }
    public void setStatus(String status) { this.status = status; }
    public void setDatePosted(LocalDateTime datePosted) { this.datePosted = datePosted; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    @Override
    public String toString() { return foodName + " (" + quantity + " units) - " + location; }

    // Information Expert — FoodListing knows how to save itself
    public boolean saveListing() {
        System.out.println("Saving listing: " + foodName);
        return true;
    }

    // Creator — FoodListing triggers AIMatching
    public AIMatching triggerAIMatching() {
        System.out.println("Triggering AI matching for listing: " + foodName);
        return new AIMatching();
    }

    // FoodListing checks for duplicates
    public boolean isDuplicate() {
        System.out.println("Checking for duplicate listing: " + foodName);
        return false;
    }

    // Information Expert — FoodListing returns its own details
    public FoodListing getDetails() {
        return this;
    }
}