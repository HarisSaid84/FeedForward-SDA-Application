package com.feedforward.business;

import com.feedforward.db.DBConnection;
import com.feedforward.db.NotificationDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AIMatchingService {

    private NotificationDAO notificationDAO;

    public AIMatchingService() {
        this.notificationDAO = new NotificationDAO();
    }

    // Core AI matching algorithm
    // Matches NGOs based on location similarity and service region
    public void runMatching(String foodName, String listingLocation) {
        List<int[]> matches = findMatchingNGOs(listingLocation);

        for (int[] match : matches) {
            int ngoUserID = match[0];
            float score = match[1];
            String message = "AI Match Found! New food listing available: '"
                    + foodName + "' near " + listingLocation
                    + " (Match Score: " + score + "%)";
            notificationDAO.sendNotification(ngoUserID, message);
        }
    }

    private List<int[]> findMatchingNGOs(String location) {
        List<int[]> matches = new ArrayList<>();
        String sql = "SELECT u.userID, n.serviceRegion FROM users u " +
                "JOIN ngo_representative n ON u.userID = n.userID " +
                "WHERE u.accountStatus = 'active'";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int ngoID = rs.getInt("userID");
                String region = rs.getString("serviceRegion");
                int score = calculateMatchScore(location, region);
                if (score > 0) {
                    matches.add(new int[]{ngoID, score});
                }
            }
        } catch (SQLException e) {
            System.out.println("AI Matching error: " + e.getMessage());
        }
        return matches;
    }

    // Scoring algorithm based on location string matching
    private int calculateMatchScore(String listingLocation, String ngoRegion) {
        if (listingLocation == null || ngoRegion == null) return 30;

        String loc = listingLocation.toLowerCase().trim();
        String region = ngoRegion.toLowerCase().trim();

        if (loc.equals(region)) return 95;

        String[] locWords = loc.split("\\s+");
        String[] regionWords = region.split("\\s+");

        int matchCount = 0;
        for (String lw : locWords) {
            for (String rw : regionWords) {
                if (lw.equals(rw)) matchCount++;
            }
        }

        if (matchCount > 0) return 60 + (matchCount * 10);
        return 30; // Base score - notify all NGOs with low confidence
    }

    public List<String[]> getMatchResults(String location) {
        List<String[]> results = new ArrayList<>();
        String sql = "SELECT u.name, n.serviceRegion, n.contactNumber FROM users u " +
                "JOIN ngo_representative n ON u.userID = n.userID " +
                "WHERE u.accountStatus = 'active'";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String ngoName = rs.getString("name");
                String region = rs.getString("serviceRegion");
                String contact = rs.getString("contactNumber");
                int score = calculateMatchScore(location, region);
                results.add(new String[]{ngoName, region, contact, score + "%"});
            }
        } catch (SQLException e) {
            System.out.println("Error fetching match results: " + e.getMessage());
        }
        return results;
    }
}