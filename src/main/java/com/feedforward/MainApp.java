package com.feedforward;

import com.feedforward.db.DBConnection;
import com.feedforward.ui.LoginScreen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // Test DB connection on startup
        if (DBConnection.getConnection() == null) {
            System.out.println("WARNING: Database connection failed! Make sure XAMPP MySQL is running.");
        }

        // Launch login screen
        LoginScreen loginScreen = new LoginScreen(stage);
        loginScreen.show();
    }

    @Override
    public void stop() {
        DBConnection.closeConnection();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}