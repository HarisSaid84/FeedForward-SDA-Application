package com.feedforward.model;

public class FoodDonor extends User implements INotifiable, IReportable {

    private String organizationName;
    private String contactNumber;
    private String location;

    public FoodDonor() {}

    public FoodDonor(String name, String email, String password,
                     String organizationName, String contactNumber, String location) {
        super(name, email, password, "donor");
        this.organizationName = organizationName;
        this.contactNumber = contactNumber;
        this.location = location;
    }

    public FoodDonor(int userID, String name, String email, String password,
                     String accountStatus, String organizationName,
                     String contactNumber, String location) {
        super(userID, name, email, password, "donor", accountStatus);
        this.organizationName = organizationName;
        this.contactNumber = contactNumber;
        this.location = location;
    }

    // Getters
    public String getOrganizationName() { return organizationName; }
    public String getContactNumber() { return contactNumber; }
    public String getLocation() { return location; }

    // Setters
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public String getDashboardTitle() {
        return "Food Donor Dashboard";
    }

    @Override
    public void receiveNotification(String msg) {
        System.out.println("Notification for donor " + name + ": " + msg);
    }

    @Override
    public String getContactInfo() {
        return contactNumber;
    }
    // Information Expert — FoodDonor manages its own food listing creation
    public FoodListing listSurplusFood(String foodName, int qty,
                                       java.time.LocalDateTime expiry,
                                       String location, String photo) {
        return new FoodListing(this.userID, foodName, qty, expiry, location, photo);
    }

    // FoodDonor views its own listings
    public String viewMyListings() {
        return "Fetching listings for donor: " + this.name;
    }

    // IReportable behaviour
    public Report generateReport(String type, String range) {
        return new Report(type, range, "Text",
                "Donor Report for: " + this.name);
    }

    public void exportReport(String format) {
        System.out.println("Exporting donor report as: " + format);
    }
}