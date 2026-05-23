package com.feedforward.model;

import java.util.List;

/**
 * AIMatching Model Class
 * Information Expert — owns and executes the matching algorithm
 * Implements IMatchable interface
 */
public class AIMatching implements IMatchable {

    private int matchingID;
    private float confidenceScore;
    private int ranking;

    public AIMatching() {}

    public AIMatching(int matchingID, float confidenceScore, int ranking) {
        this.matchingID = matchingID;
        this.confidenceScore = confidenceScore;
        this.ranking = ranking;
    }

    // Extract food details from a listing to prepare for matching
    public void extractFoodDetails(FoodListing listing) {
        System.out.println("Extracting food details from listing: "
                + listing.getFoodName()
                + " at " + listing.getLocation());
    }

    @Override
    public void runMatchingAlgorithm() {
        System.out.println("Running AI matching algorithm with confidence: "
                + confidenceScore);
    }

    @Override
    public List<?> getRankedRecipients() {
        System.out.println("Returning ranked recipients for matching ID: "
                + matchingID);
        return new java.util.ArrayList<>();
    }

    // Create match notifications for ranked recipients
    public List<Notification> createMatchNotifications(List<Integer> recipientIDs,
                                                       String foodName,
                                                       String location) {
        List<Notification> notifications = new java.util.ArrayList<>();
        for (int id : recipientIDs) {
            Notification n = new Notification(id,
                    "AI Match Found! Food listing available: '"
                            + foodName + "' near " + location
                            + " (Score: " + confidenceScore + "%)");
            notifications.add(n);
        }
        return notifications;
    }

    // Mark listing as unmatched if no NGOs found
    public void markAsUnmatched() {
        this.confidenceScore = 0;
        System.out.println("Listing marked as unmatched.");
    }

    // Getters
    public int getMatchingID() { return matchingID; }
    public float getConfidenceScore() { return confidenceScore; }
    public int getRanking() { return ranking; }

    // Setters
    public void setMatchingID(int matchingID) { this.matchingID = matchingID; }
    public void setConfidenceScore(float confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    public void setRanking(int ranking) { this.ranking = ranking; }
}