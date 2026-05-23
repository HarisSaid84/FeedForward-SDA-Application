package com.feedforward.model;

import java.time.LocalDateTime;

public class Notification {

    private int notificationID;
    private int userID;
    private String message;
    private LocalDateTime dateSent;
    private String status;

    public Notification() {}

    public Notification(int userID, String message) {
        this.userID = userID;
        this.message = message;
        this.dateSent = LocalDateTime.now();
        this.status = "unread";
    }

    public int getNotificationID() { return notificationID; }
    public int getUserID() { return userID; }
    public String getMessage() { return message; }
    public LocalDateTime getDateSent() { return dateSent; }
    public String getStatus() { return status; }

    public void setNotificationID(int notificationID) { this.notificationID = notificationID; }
    public void setUserID(int userID) { this.userID = userID; }
    public void setMessage(String message) { this.message = message; }
    public void setDateSent(LocalDateTime dateSent) { this.dateSent = dateSent; }
    public void setStatus(String status) { this.status = status; }

    // Notification sends itself
    public boolean send() {
        System.out.println("Sending notification to user: "
                + userID + " — " + message);
        return true;
    }

    // Retry sending if failed
    public void retry() {
        System.out.println("Retrying notification delivery to user: " + userID);
        send();
    }

    // Mark notification as delivered
    public void markDelivered() {
        this.status = "read";
        System.out.println("Notification marked as delivered: " + notificationID);
    }
}