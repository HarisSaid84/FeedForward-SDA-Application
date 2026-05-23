package com.feedforward.model;

public class NGORepresentative extends User implements INotifiable  {

    private String ngoName;
    private String serviceRegion;
    private String contactNumber;

    public NGORepresentative() {}

    public NGORepresentative(String name, String email, String password,
                             String ngoName, String serviceRegion, String contactNumber) {
        super(name, email, password, "ngo");
        this.ngoName = ngoName;
        this.serviceRegion = serviceRegion;
        this.contactNumber = contactNumber;
    }

    public NGORepresentative(int userID, String name, String email, String password,
                             String accountStatus, String ngoName,
                             String serviceRegion, String contactNumber) {
        super(userID, name, email, password, "ngo", accountStatus);
        this.ngoName = ngoName;
        this.serviceRegion = serviceRegion;
        this.contactNumber = contactNumber;
    }

    public String getNgoName() { return ngoName; }
    public String getServiceRegion() { return serviceRegion; }
    public String getContactNumber() { return contactNumber; }

    public void setNgoName(String ngoName) { this.ngoName = ngoName; }
    public void setServiceRegion(String serviceRegion) { this.serviceRegion = serviceRegion; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    @Override
    public String getDashboardTitle() { return "NGO Representative Dashboard"; }

    @Override
    public void receiveNotification(String msg) {
        System.out.println("Notification for NGO " + name + ": " + msg);
    }

    @Override
    public String getContactInfo() {
        return contactNumber;
    }

    // NGO searches for available food listings
    public String searchFoodListings(String keyword, String foodType,
                                     String location) {
        return "Searching listings for NGO: " + ngoName
                + " keyword: " + keyword
                + " location: " + location;
    }

    // NGO schedules a pickup
    public String schedulePickup(int listingID, String date,
                                 String time, String transport) {
        return "Pickup scheduled by NGO: " + ngoName
                + " for listing: " + listingID;
    }

    // NGO views pickup status
    public String viewPickupStatus(int pickupID) {
        return "Checking status for pickup: " + pickupID;
    }

    // NGO opens analytics dashboard
    public AnalyticsDashboard openAnalyticsDashboard() {
        return new AnalyticsDashboard();
    }
}