package com.feedforward.db;

import com.feedforward.model.*;
import java.sql.*;

public class UserDAO {

    private Connection conn;

    public UserDAO() {
        this.conn = DBConnection.getConnection();
    }

    // Register a new user - returns generated userID or -1 on fail
    public int registerUser(String name, String email, String password, String role) {
        String sql = "INSERT INTO users (name, email, password, role, accountStatus) VALUES (?, ?, ?, ?, 'active')";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
        return -1;
    }

    public boolean registerDonor(int userID, String orgName, String contact, String location) {
        String sql = "INSERT INTO food_donor (userID, organizationName, contactNumber, location) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ps.setString(2, orgName);
            ps.setString(3, contact);
            ps.setString(4, location);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error registering donor: " + e.getMessage());
        }
        return false;
    }

    public boolean registerNGO(int userID, String ngoName, String region, String contact) {
        String sql = "INSERT INTO ngo_representative (userID, ngoName, serviceRegion, contactNumber) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ps.setString(2, ngoName);
            ps.setString(3, region);
            ps.setString(4, contact);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error registering NGO: " + e.getMessage());
        }
        return false;
    }

    public boolean registerVolunteer(int userID, String availability, String transport, String contact) {
        String sql = "INSERT INTO volunteer (userID, availabilityStatus, transportType, contactNumber) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ps.setString(2, availability);
            ps.setString(3, transport);
            ps.setString(4, contact);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error registering volunteer: " + e.getMessage());
        }
        return false;
    }

    // Login - returns User object or null
    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND accountStatus = 'active'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userID = rs.getInt("userID");
                String name = rs.getString("name");
                String role = rs.getString("role");
                String status = rs.getString("accountStatus");

                switch (role) {
                    case "donor":
                        return getDonor(userID, name, email, password, status);
                    case "ngo":
                        return getNGO(userID, name, email, password, status);
                    case "volunteer":
                        return getVolunteer(userID, name, email, password, status);
                    case "admin":
                        return new Admin(userID, name, email, password, status, "super", null);
                }
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }

    private FoodDonor getDonor(int userID, String name, String email, String password, String status) {
        String sql = "SELECT * FROM food_donor WHERE userID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new FoodDonor(userID, name, email, password, status,
                        rs.getString("organizationName"),
                        rs.getString("contactNumber"),
                        rs.getString("location"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching donor: " + e.getMessage());
        }
        return new FoodDonor(userID, name, email, password, status, "", "", "");
    }

    private NGORepresentative getNGO(int userID, String name, String email, String password, String status) {
        String sql = "SELECT * FROM ngo_representative WHERE userID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new NGORepresentative(userID, name, email, password, status,
                        rs.getString("ngoName"),
                        rs.getString("serviceRegion"),
                        rs.getString("contactNumber"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching NGO: " + e.getMessage());
        }
        return new NGORepresentative(userID, name, email, password, status, "", "", "");
    }

    private Volunteer getVolunteer(int userID, String name, String email, String password, String status) {
        String sql = "SELECT * FROM volunteer WHERE userID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Volunteer(userID, name, email, password, status,
                        rs.getString("availabilityStatus"),
                        rs.getString("transportType"),
                        rs.getString("contactNumber"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching volunteer: " + e.getMessage());
        }
        return new Volunteer(userID, name, email, password, status, "", "", "");
    }

    public boolean emailExists(String email) {
        String sql = "SELECT userID FROM users WHERE email = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking email: " + e.getMessage());
        }
        return false;
    }

    public boolean updateAccountStatus(int userID, String status) {
        String sql = "UPDATE users SET accountStatus = ? WHERE userID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, userID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error updating status: " + e.getMessage());
        }
        return false;
    }

    public ResultSet getAllUsers() {
        String sql = "SELECT * FROM users WHERE role != 'admin'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteUser(int userID) {
        String sql = "DELETE FROM users WHERE userID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
        return false;
    }
}