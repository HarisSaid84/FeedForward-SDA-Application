package com.feedforward.db;

import java.sql.*;

public class AnalyticsDAO {

    private Connection conn;

    public AnalyticsDAO() {
        this.conn = DBConnection.getConnection();
    }

    public int getTotalFoodSaved() {
        String sql = "SELECT COALESCE(SUM(quantity), 0) as total FROM food_listings WHERE LOWER(status) = 'collected'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.out.println("Error fetching food saved: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalPickupsCompleted() {
        String sql = "SELECT COUNT(*) as total FROM pickup_schedule WHERE status = 'Completed'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.out.println("Error fetching pickups: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalListings() {
        String sql = "SELECT COUNT(*) as total FROM food_listings";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.out.println("Error fetching listings count: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) as total FROM users WHERE role != 'admin'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.out.println("Error fetching users count: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalDonors() {
        String sql = "SELECT COUNT(*) as total FROM users WHERE role = 'donor'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalNGOs() {
        String sql = "SELECT COUNT(*) as total FROM users WHERE role = 'ngo'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return 0;
    }

    public ResultSet getListingsByLocation() {
        String sql = "SELECT location, COUNT(*) as count FROM food_listings GROUP BY location ORDER BY count DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public boolean saveReport(int userID, String reportType, String dateRange, String format) {
        String sql = "INSERT INTO report (userID, reportType, dateRange, generatedDate, format) VALUES (?, ?, ?, NOW(), ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ps.setString(2, reportType);
            ps.setString(3, dateRange);
            ps.setString(4, format);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
        return false;
    }
}