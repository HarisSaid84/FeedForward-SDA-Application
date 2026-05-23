package com.feedforward.db;

import com.feedforward.model.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    private Connection conn;

    public NotificationDAO() {
        this.conn = DBConnection.getConnection();
    }

    public boolean sendNotification(int userID, String message) {
        String sql = "INSERT INTO notification (userID, message, dateSent, status) VALUES (?, ?, NOW(), 'unread')";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ps.setString(2, message);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error sending notification: " + e.getMessage());
        }
        return false;
    }

    public List<Notification> getNotificationsForUser(int userID) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE userID = ? ORDER BY dateSent DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification n = new Notification();
                n.setNotificationID(rs.getInt("notificationID"));
                n.setUserID(rs.getInt("userID"));
                n.setMessage(rs.getString("message"));
                n.setDateSent(rs.getTimestamp("dateSent").toLocalDateTime());
                n.setStatus(rs.getString("status"));
                list.add(n);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
        }
        return list;
    }

    public boolean markAsRead(int notificationID) {
        String sql = "UPDATE notification SET status = 'read' WHERE notificationID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, notificationID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error marking notification: " + e.getMessage());
        }
        return false;
    }
}