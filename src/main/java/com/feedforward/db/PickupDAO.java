package com.feedforward.db;

import com.feedforward.model.PickupSchedule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PickupDAO {

    private Connection conn;

    public PickupDAO() {
        this.conn = DBConnection.getConnection();
    }

    public boolean schedulePickup(PickupSchedule pickup) {
        String sql = "INSERT INTO pickup_schedule (listingID, userID, pickupDate, pickupTime, transportMethod, status) VALUES (?, ?, ?, ?, ?, 'Scheduled')";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, pickup.getListingID());
            ps.setInt(2, pickup.getUserID());
            ps.setDate(3, Date.valueOf(pickup.getPickupDate()));
            ps.setTime(4, Time.valueOf(pickup.getPickupTime()));
            ps.setString(5, pickup.getTransportMethod());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error scheduling pickup: " + e.getMessage());
        }
        return false;
    }

    public boolean updatePickupStatus(int pickupID, String status) {
        String sql = "UPDATE pickup_schedule SET status = ? WHERE pickupID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, pickupID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error updating pickup status: " + e.getMessage());
        }
        return false;
    }

    public List<PickupSchedule> getPickupsByUser(int userID) {
        List<PickupSchedule> list = new ArrayList<>();
        String sql = "SELECT ps.*, fl.foodName FROM pickup_schedule ps JOIN food_listings fl ON ps.listingID = fl.listingID WHERE ps.userID = ? ORDER BY ps.pickupDate DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapPickup(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching pickups: " + e.getMessage());
        }
        return list;
    }

    public List<PickupSchedule> getPickupsByListing(int listingID) {
        List<PickupSchedule> list = new ArrayList<>();
        String sql = "SELECT ps.*, fl.foodName, u.name as scheduledByName FROM pickup_schedule ps JOIN food_listings fl ON ps.listingID = fl.listingID JOIN users u ON ps.userID = u.userID WHERE ps.listingID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, listingID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PickupSchedule p = mapPickup(rs);
                p.setScheduledByName(rs.getString("scheduledByName"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching pickups by listing: " + e.getMessage());
        }
        return list;
    }

    public List<PickupSchedule> getAllPickups() {
        List<PickupSchedule> list = new ArrayList<>();
        String sql = "SELECT ps.*, fl.foodName, u.name as scheduledByName FROM pickup_schedule ps JOIN food_listings fl ON ps.listingID = fl.listingID JOIN users u ON ps.userID = u.userID ORDER BY ps.pickupDate DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PickupSchedule p = mapPickup(rs);
                p.setScheduledByName(rs.getString("scheduledByName"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all pickups: " + e.getMessage());
        }
        return list;
    }

    private PickupSchedule mapPickup(ResultSet rs) throws SQLException {
        PickupSchedule p = new PickupSchedule();
        p.setPickupID(rs.getInt("pickupID"));
        p.setListingID(rs.getInt("listingID"));
        p.setUserID(rs.getInt("userID"));
        p.setPickupDate(rs.getDate("pickupDate").toLocalDate());
        p.setPickupTime(rs.getTime("pickupTime").toLocalTime());
        p.setTransportMethod(rs.getString("transportMethod"));
        p.setStatus(rs.getString("status"));
        p.setFoodName(rs.getString("foodName"));
        return p;
    }
}