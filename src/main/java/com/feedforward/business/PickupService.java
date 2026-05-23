package com.feedforward.business;

import com.feedforward.db.FoodListingDAO;
import com.feedforward.db.NotificationDAO;
import com.feedforward.db.PickupDAO;
import com.feedforward.model.PickupSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PickupService {

    private PickupDAO pickupDAO;
    private FoodListingDAO listingDAO;
    private NotificationDAO notificationDAO;

    public PickupService() {
        this.pickupDAO = new PickupDAO();
        this.listingDAO = new FoodListingDAO();
        this.notificationDAO = new NotificationDAO();
    }

    public String schedulePickup(int listingID, int userID, String dateStr,
                                 String timeStr, String transportMethod) {
        if (dateStr.isEmpty() || timeStr.isEmpty() || transportMethod.isEmpty())
            return "All fields are required!";

        LocalDate date;
        LocalTime time;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            return "Invalid date! Use format: yyyy-MM-dd";
        }
        try {
            time = LocalTime.parse(timeStr);
        } catch (Exception e) {
            return "Invalid time! Use format: HH:mm";
        }

        if (date.isBefore(LocalDate.now())) return "Pickup date must be today or in the future!";

        PickupSchedule pickup = new PickupSchedule(listingID, userID, date, time, transportMethod);
        boolean saved = pickupDAO.schedulePickup(pickup);
        if (!saved) return "Failed to schedule pickup!";

        listingDAO.updateListingStatus(listingID, "Pickup Scheduled");
        return "success";
    }

    public String confirmPickup(int pickupID, int donorUserID, int scheduledByUserID) {
        boolean updated = pickupDAO.updatePickupStatus(pickupID, "Confirmed");
        if (!updated) return "Failed to confirm pickup!";

        notificationDAO.sendNotification(scheduledByUserID,
                "Your pickup request has been CONFIRMED by the donor!");
        notificationDAO.sendNotification(donorUserID,
                "You confirmed a food pickup. Thank you for your contribution!");
        return "success";
    }

    public String cancelPickup(int pickupID, int listingID, int scheduledByUserID) {
        boolean updated = pickupDAO.updatePickupStatus(pickupID, "Cancelled");
        if (!updated) return "Failed to cancel pickup!";

        listingDAO.updateListingStatus(listingID, "available");
        notificationDAO.sendNotification(scheduledByUserID,
                "Your pickup request has been cancelled. The listing is available again.");
        return "success";
    }

    public String completePickup(int pickupID, int listingID, int donorUserID) {
        boolean updated = pickupDAO.updatePickupStatus(pickupID, "Completed");
        if (!updated) return "Failed to complete pickup!";

        listingDAO.updateListingStatus(listingID, "collected");
        notificationDAO.sendNotification(donorUserID,
                "Food pickup completed successfully! Thank you for reducing food waste.");
        return "success";
    }

    public List<PickupSchedule> getUserPickups(int userID) {
        return pickupDAO.getPickupsByUser(userID);
    }

    public List<PickupSchedule> getPickupsByListing(int listingID) {
        return pickupDAO.getPickupsByListing(listingID);
    }

    public List<PickupSchedule> getAllPickups() {
        return pickupDAO.getAllPickups();
    }
}