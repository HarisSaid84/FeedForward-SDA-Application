package com.feedforward.model;

public class Session {

    private static Session instance = null;
    private static User currentUser = null;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentRole() {
        if (currentUser == null) return null;
        return currentUser.getRole();
    }

    public static int getCurrentUserID() {
        if (currentUser == null) return -1;
        return currentUser.getUserID();
    }

    public static String getCurrentUserName() {
        if (currentUser == null) return null;
        return currentUser.getName();
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}