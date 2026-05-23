package com.feedforward.model;

public class Volunteer extends User implements INotifiable {

    private String availabilityStatus;
    private String transportType;
    private String contactNumber;

    public Volunteer() {}

    public Volunteer(String name, String email, String password,
                     String availabilityStatus, String transportType, String contactNumber) {
        super(name, email, password, "volunteer");
        this.availabilityStatus = availabilityStatus;
        this.transportType = transportType;
        this.contactNumber = contactNumber;
    }

    public Volunteer(int userID, String name, String email, String password,
                     String accountStatus, String availabilityStatus,
                     String transportType, String contactNumber) {
        super(userID, name, email, password, "volunteer", accountStatus);
        this.availabilityStatus = availabilityStatus;
        this.transportType = transportType;
        this.contactNumber = contactNumber;
    }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public String getTransportType() { return transportType; }
    public String getContactNumber() { return contactNumber; }

    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    public void setTransportType(String transportType) { this.transportType = transportType; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    @Override
    public String getDashboardTitle() { return "Volunteer Dashboard"; }

    @Override
    public void receiveNotification(String msg) {
        System.out.println("Notification for volunteer " + name + ": " + msg);
    }

    @Override
    public String getContactInfo() {
        return contactNumber;
    }

    // Volunteer schedules a pickup
    public String schedulePickup(int listingID, String date,
                                 String time, String transport) {
        return "Pickup scheduled by volunteer: " + name
                + " for listing: " + listingID;
    }

    // Volunteer confirms a pickup
    public boolean confirmPickup(int pickupID) {
        System.out.println("Volunteer " + name
                + " confirming pickup: " + pickupID);
        return true;
    }

    // Volunteer views pickup status
    public String viewPickupStatus(int pickupID) {
        return "Checking pickup status: " + pickupID
                + " by volunteer: " + name;
    }
}