package com.feedforward.business;

import com.feedforward.db.UserDAO;
import com.feedforward.db.NotificationDAO;
import com.feedforward.model.*;

public class UserService {

    private UserDAO userDAO;
    private NotificationDAO notificationDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.notificationDAO = new NotificationDAO();
    }

    public String registerDonor(String name, String email, String password,
                                String orgName, String contact, String location) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) return "All fields are required!";
        if (!email.contains("@")) return "Invalid email address!";
        if (password.length() < 6) return "Password must be at least 6 characters!";
        if (userDAO.emailExists(email)) return "Email already registered!";

        int userID = userDAO.registerUser(name, email, password, "donor");
        if (userID == -1) return "Registration failed!";

        userDAO.registerDonor(userID, orgName, contact, location);
        notificationDAO.sendNotification(userID, "Welcome to FeedForward! Your donor account is ready.");
        return "success";
    }

    public String registerNGO(String name, String email, String password,
                              String ngoName, String region, String contact) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) return "All fields are required!";
        if (!email.contains("@")) return "Invalid email address!";
        if (password.length() < 6) return "Password must be at least 6 characters!";
        if (userDAO.emailExists(email)) return "Email already registered!";

        int userID = userDAO.registerUser(name, email, password, "ngo");
        if (userID == -1) return "Registration failed!";

        userDAO.registerNGO(userID, ngoName, region, contact);
        notificationDAO.sendNotification(userID, "Welcome to FeedForward! Your NGO account is ready.");
        return "success";
    }

    public String registerVolunteer(String name, String email, String password,
                                    String availability, String transport, String contact) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) return "All fields are required!";
        if (!email.contains("@")) return "Invalid email address!";
        if (password.length() < 6) return "Password must be at least 6 characters!";
        if (userDAO.emailExists(email)) return "Email already registered!";

        int userID = userDAO.registerUser(name, email, password, "volunteer");
        if (userID == -1) return "Registration failed!";

        userDAO.registerVolunteer(userID, availability, transport, contact);
        notificationDAO.sendNotification(userID, "Welcome to FeedForward! Your volunteer account is ready.");
        return "success";
    }

    public User login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) return null;
        return userDAO.login(email, password);
    }

    public boolean suspendUser(int userID) {
        return userDAO.updateAccountStatus(userID, "suspended");
    }

    public boolean activateUser(int userID) {
        return userDAO.updateAccountStatus(userID, "active");
    }

    public boolean deleteUser(int userID) {
        return userDAO.deleteUser(userID);
    }

    public java.sql.ResultSet getAllUsers() {
        return userDAO.getAllUsers();
    }
}