package com.feedforward.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class PickupSchedule {

    private int pickupID;
    private int listingID;
    private int userID;
    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private String transportMethod;
    private String status;
    private String foodName;
    private String scheduledByName;

    public PickupSchedule() {}

    public PickupSchedule(int listingID, int userID, LocalDate pickupDate,
                          LocalTime pickupTime, String transportMethod) {
        this.listingID = listingID;
        this.userID = userID;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.transportMethod = transportMethod;
        this.status = "Scheduled";
    }

    public int getPickupID() { return pickupID; }
    public int getListingID() { return listingID; }
    public int getUserID() { return userID; }
    public LocalDate getPickupDate() { return pickupDate; }
    public LocalTime getPickupTime() { return pickupTime; }
    public String getTransportMethod() { return transportMethod; }
    public String getStatus() { return status; }
    public String getFoodName() { return foodName; }
    public String getScheduledByName() { return scheduledByName; }

    public void setPickupID(int pickupID) { this.pickupID = pickupID; }
    public void setListingID(int listingID) { this.listingID = listingID; }
    public void setUserID(int userID) { this.userID = userID; }
    public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }
    public void setPickupTime(LocalTime pickupTime) { this.pickupTime = pickupTime; }
    public void setTransportMethod(String transportMethod) { this.transportMethod = transportMethod; }
    public void setStatus(String status) { this.status = status; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public void setScheduledByName(String scheduledByName) { this.scheduledByName = scheduledByName; }

    // PickupSchedule saves itself
    public boolean saveSchedule() {
        System.out.println("Saving pickup schedule for listing: " + listingID);
        return true;
    }

    // PickupSchedule updates its own status
    public void updateStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("Pickup status updated to: " + newStatus);
    }

    // Creator — PickupSchedule creates donor notification
    public Notification createDonorNotification() {
        return new Notification(userID,
                "Your food pickup has been scheduled for: " + pickupDate);
    }

    // Auto cancel if expired
    public void autoCancelIfExpired() {
        if (pickupDate != null && pickupDate.isBefore(java.time.LocalDate.now())) {
            this.status = "Cancelled";
            System.out.println("Pickup auto-cancelled due to expiry.");
        }
    }

    // Flag for admin review
    public void flagForReview() {
        this.status = "Flagged";
        System.out.println("Pickup flagged for admin review: " + pickupID);
    }
}