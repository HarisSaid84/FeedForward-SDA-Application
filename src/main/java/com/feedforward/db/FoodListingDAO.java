package com.feedforward.db;

import com.feedforward.model.FoodListing;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FoodListingDAO {

    private Connection conn;

    public FoodListingDAO() {
        this.conn = DBConnection.getConnection();
    }

    public boolean addListing(FoodListing listing) {
        String sql = "INSERT INTO food_listings (userID, foodName, quantity, expiryTime, location, photo, status, datePosted) VALUES (?, ?, ?, ?, ?, ?, 'available', NOW())";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, listing.getUserID());
            ps.setString(2, listing.getFoodName());
            ps.setInt(3, listing.getQuantity());
            ps.setTimestamp(4, Timestamp.valueOf(listing.getExpiryTime()));
            ps.setString(5, listing.getLocation());
            ps.setString(6, listing.getPhoto());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding listing: " + e.getMessage());
        }
        return false;
    }

    public List<FoodListing> getAllAvailableListings() {
        List<FoodListing> list = new ArrayList<>();
        String sql = "SELECT fl.*, u.name as donorName FROM food_listings fl JOIN users u ON fl.userID = u.userID WHERE fl.status = 'available' ORDER BY fl.datePosted DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapListing(rs));
        } catch (SQLException e) {
            System.out.println("Error fetching listings: " + e.getMessage());
        }
        return list;
    }

    public List<FoodListing> getListingsByDonor(int userID) {
        List<FoodListing> list = new ArrayList<>();
        String sql = "SELECT fl.*, u.name as donorName FROM food_listings fl JOIN users u ON fl.userID = u.userID WHERE fl.userID = ? ORDER BY fl.datePosted DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapListing(rs));
        } catch (SQLException e) {
            System.out.println("Error fetching donor listings: " + e.getMessage());
        }
        return list;
    }


    public List<FoodListing> searchListings(String keyword, String location) {
        List<FoodListing> list = new ArrayList<>();
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasLocation = location != null && !location.trim().isEmpty();

        String sql;
        if (!hasKeyword && !hasLocation) {
            // No filters — return everything available
            sql = "SELECT fl.*, u.name as donorName FROM food_listings fl " +
                  "JOIN users u ON fl.userID = u.userID " +
                  "WHERE fl.status = 'available' ORDER BY fl.datePosted DESC";
        } else if (hasKeyword && !hasLocation) {
            // Keyword only
            sql = "SELECT fl.*, u.name as donorName FROM food_listings fl " +
                  "JOIN users u ON fl.userID = u.userID " +
                  "WHERE fl.status = 'available' AND fl.foodName LIKE ? " +
                  "ORDER BY fl.datePosted DESC";
        } else if (!hasKeyword) {
            // Location only
            sql = "SELECT fl.*, u.name as donorName FROM food_listings fl " +
                  "JOIN users u ON fl.userID = u.userID " +
                  "WHERE fl.status = 'available' AND fl.location LIKE ? " +
                  "ORDER BY fl.datePosted DESC";
        } else {
            // Both keyword AND location
            sql = "SELECT fl.*, u.name as donorName FROM food_listings fl " +
                  "JOIN users u ON fl.userID = u.userID " +
                  "WHERE fl.status = 'available' AND fl.foodName LIKE ? AND fl.location LIKE ? " +
                  "ORDER BY fl.datePosted DESC";
        }

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            if (hasKeyword && hasLocation) {
                ps.setString(1, "%" + keyword.trim() + "%");
                ps.setString(2, "%" + location.trim() + "%");
            } else if (hasKeyword) {
                ps.setString(1, "%" + keyword.trim() + "%");
            } else if (hasLocation) {
                ps.setString(1, "%" + location.trim() + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapListing(rs));
        } catch (SQLException e) {
            System.out.println("Error searching listings: " + e.getMessage());
        }
        return list;
    }

    public List<FoodListing> getAllListings() {
        List<FoodListing> list = new ArrayList<>();
        String sql = "SELECT fl.*, u.name as donorName FROM food_listings fl JOIN users u ON fl.userID = u.userID ORDER BY fl.datePosted DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapListing(rs));
        } catch (SQLException e) {
            System.out.println("Error fetching all listings: " + e.getMessage());
        }
        return list;
    }

    public boolean updateListingStatus(int listingID, String status) {
        String sql = "UPDATE food_listings SET status = ? WHERE listingID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, listingID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error updating listing status: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteListing(int listingID) {
        String sql = "DELETE FROM food_listings WHERE listingID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, listingID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting listing: " + e.getMessage());
        }
        return false;
    }

    private FoodListing mapListing(ResultSet rs) throws SQLException {
        FoodListing fl = new FoodListing();
        fl.setListingID(rs.getInt("listingID"));
        fl.setUserID(rs.getInt("userID"));
        fl.setFoodName(rs.getString("foodName"));
        fl.setQuantity(rs.getInt("quantity"));
        Timestamp ts = rs.getTimestamp("expiryTime");
        fl.setExpiryTime(ts != null ? ts.toLocalDateTime() : LocalDateTime.now().plusDays(1));
        fl.setLocation(rs.getString("location"));
        fl.setPhoto(rs.getString("photo"));
        fl.setStatus(rs.getString("status"));
        Timestamp dp = rs.getTimestamp("datePosted");
        fl.setDatePosted(dp != null ? dp.toLocalDateTime() : LocalDateTime.now());
        fl.setDonorName(rs.getString("donorName"));
        return fl;
    }
}
