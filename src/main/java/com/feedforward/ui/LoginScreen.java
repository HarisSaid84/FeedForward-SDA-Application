package com.feedforward.ui;

import com.feedforward.business.SystemController;
import com.feedforward.model.*;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import static com.feedforward.ui.UIComponents.*;

public class LoginScreen {
    private Stage stage;
    private SystemController controller;

    public LoginScreen(Stage stage) { this.stage=stage; this.controller=SystemController.getInstance(); }

    public void show() {
        HBox root = new HBox();
        root.setStyle("-fx-background-color:"+BG+";");
        root.getChildren().addAll(makeBrandPanel(), makeFormPanel());
        Scene scene = new Scene(root, 960, 620);
        stage.setTitle("FeedForward — Login"); stage.setScene(scene); stage.setResizable(false); stage.show();
    }

    private VBox makeBrandPanel() {
        VBox panel = new VBox(28);
        panel.setPrefWidth(420); panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(50,50,50,50));
        panel.setStyle("-fx-background-color:"+BG_CARD+";");

        // Actual logo image
        javafx.scene.image.Image logoImg = null;
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/logo.png");
            if (is != null) { logoImg = new javafx.scene.image.Image(is); }
        } catch (Exception ignored) {}

        VBox logoBlock = new VBox(0); logoBlock.setAlignment(Pos.CENTER);
        if (logoImg != null) {
            javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(logoImg);
            iv.setFitWidth(320); iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setStyle("-fx-effect: dropshadow(gaussian,"+GREEN+",30,0.15,0,0);");
            logoBlock.getChildren().add(iv);
        } else {
            // Fallback if image doesn't load
            Text name1 = new Text("Feed"); name1.setFont(Font.font("Arial", FontWeight.BOLD, 52)); name1.setFill(Color.web(WHITE));
            Text name2 = new Text("Forward"); name2.setFont(Font.font("Arial", FontWeight.BOLD, 52)); name2.setFill(Color.web(GREEN));
            logoBlock.getChildren().addAll(name1, name2);
        }

        // Tagline pill
        HBox pill = new HBox(8); pill.setAlignment(Pos.CENTER_LEFT);
        pill.setPadding(new Insets(8,16,8,16));
        pill.setStyle("-fx-background-color:"+GREEN_DIM+";-fx-background-radius:20;-fx-border-color:"+GREEN_MID+";-fx-border-radius:20;-fx-border-width:1;");
        Label pillText = new Label("AI-Based Food Redistribution");
        pillText.setTextFill(Color.web(GREEN)); pillText.setFont(Font.font("Arial", FontWeight.BOLD,12));
        pill.getChildren().add(pillText);

        // Feature list — bold, TODU-style
        VBox features = new VBox(16); features.setAlignment(Pos.CENTER_LEFT);
        String[][] items = {
            {"01","Smart AI donor-recipient matching"},
            {"02","Real-time pickup tracking"},
            {"03","Waste analytics & reports"}
        };
        for (String[] item : items) {
            HBox row = new HBox(14); row.setAlignment(Pos.CENTER_LEFT);
            Label num = new Label(item[0]);
            num.setFont(Font.font("Arial", FontWeight.BOLD,11)); num.setTextFill(Color.web(GREEN));
            num.setPadding(new Insets(3,8,3,8));
            num.setStyle("-fx-background-color:"+GREEN_DIM+";-fx-background-radius:4;");
            Label desc = new Label(item[1]); desc.setTextFill(Color.web(GREY)); desc.setFont(Font.font("Arial",13));
            row.getChildren().addAll(num, desc);
            features.getChildren().add(row);
        }

        // Bottom user count
        HBox countRow = new HBox(10); countRow.setAlignment(Pos.CENTER_LEFT);
        countRow.setPadding(new Insets(14,0,0,0));
        Label dot1 = new Label("●"); dot1.setTextFill(Color.web(GREEN)); dot1.setFont(Font.font(8));
        Label dot2 = new Label("●"); dot2.setTextFill(Color.web(GREEN)); dot2.setFont(Font.font(8)); dot2.setOpacity(0.6);
        Label dot3 = new Label("●"); dot3.setTextFill(Color.web(GREEN)); dot3.setFont(Font.font(8)); dot3.setOpacity(0.3);
        Label countLbl = new Label("Reducing food waste, one pickup at a time.");
        countLbl.setTextFill(Color.web(GREY)); countLbl.setFont(Font.font("Arial",11));
        countRow.getChildren().addAll(dot1, dot2, dot3, countLbl);

        panel.getChildren().addAll(logoBlock, pill, features, countRow);
        return panel;
    }

    private VBox makeFormPanel() {
        VBox form = new VBox(22);
        form.setAlignment(Pos.CENTER); form.setPadding(new Insets(70,70,70,70));
        HBox.setHgrow(form, Priority.ALWAYS);
        form.setStyle("-fx-background-color:"+BG+";");

        // Header
        Text welcome = new Text("Welcome back");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 30)); welcome.setFill(Color.web(WHITE));
        Text sub = new Text("Sign in to your account to continue");
        sub.setFont(Font.font("Arial",13)); sub.setFill(Color.web(GREY));

        // Email group
        VBox emailGrp = new VBox(7);
        Label elbl = new Label("Email address"); elbl.setTextFill(Color.web(GREY)); elbl.setFont(Font.font("Arial", FontWeight.BOLD,12));
        TextField emailField = makeField("you@example.com"); emailField.setPrefWidth(400);
        emailGrp.getChildren().addAll(elbl, emailField);

        // Password group
        VBox passGrp = new VBox(7);
        Label plbl = new Label("Password"); plbl.setTextFill(Color.web(GREY)); plbl.setFont(Font.font("Arial", FontWeight.BOLD,12));
        PasswordField passField = new PasswordField(); passField.setPromptText("••••••••");
        passField.setPrefWidth(400); passField.setPrefHeight(42);
        String ps = "-fx-background-color:"+BG_INPUT+";-fx-text-fill:"+WHITE+";-fx-border-color:"+BORDER2+";-fx-border-radius:8;-fx-background-radius:8;-fx-prompt-text-fill:#404040;-fx-padding:10 14;-fx-font-size:13;";
        passField.setStyle(ps);
        passField.focusedProperty().addListener((o,ov,nv)->passField.setStyle(ps+(nv?"-fx-border-color:"+GREEN+";-fx-effect:dropshadow(gaussian,"+GREEN+"44,8,0,0,0);":"")));
        passGrp.getChildren().addAll(plbl, passField);

        Label errorLabel = makeFeedback();

        // Login button — full width, bold green
        Button loginBtn = new Button("Sign In");
        loginBtn.setPrefWidth(400); loginBtn.setPrefHeight(44);
        String base  = "-fx-background-color:"+GREEN+";-fx-text-fill:#0c0c0c;-fx-font-weight:bold;-fx-font-size:15;-fx-background-radius:8;-fx-cursor:hand;";
        String hover = "-fx-background-color:"+GREEN2+";-fx-text-fill:#0c0c0c;-fx-font-weight:bold;-fx-font-size:15;-fx-background-radius:8;-fx-cursor:hand;";
        loginBtn.setStyle(base);
        loginBtn.setOnMouseEntered(e->loginBtn.setStyle(hover));
        loginBtn.setOnMouseExited(e->loginBtn.setStyle(base));

        // Register link
        HBox regRow = new HBox(6); regRow.setAlignment(Pos.CENTER);
        Label noAcc = new Label("Don't have an account?"); noAcc.setTextFill(Color.web(GREY)); noAcc.setFont(Font.font("Arial",12));
        Button regBtn = new Button("Create account");
        regBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:"+GREEN+";-fx-cursor:hand;-fx-border-color:transparent;-fx-font-size:12;-fx-padding:0;-fx-font-weight:bold;");
        regRow.getChildren().addAll(noAcc, regBtn);

        form.getChildren().addAll(welcome, sub, emailGrp, passGrp, errorLabel, loginBtn, regRow);

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim(), pass = passField.getText().trim();
            if (email.isEmpty()||pass.isEmpty()) { setError(errorLabel,"Please fill in all fields."); return; }
            User user = controller.handleLogin(email, pass);
            if (user==null) { setError(errorLabel,"Invalid email or password."); passField.clear(); return; }
            navigateToDashboard(user);
        });
        passField.setOnAction(e->loginBtn.fire());
        regBtn.setOnAction(e->new RegisterScreen(stage).show());
        return form;
    }

    private void navigateToDashboard(User user) {
        switch(user.getRole()) {
            case "donor":     new DonorDashboard(stage).show();     break;
            case "ngo":       new NGODashboard(stage).show();       break;
            case "volunteer": new VolunteerDashboard(stage).show(); break;
            case "admin":     new AdminDashboard(stage).show();     break;
        }
    }
}
