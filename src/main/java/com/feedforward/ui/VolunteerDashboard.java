package com.feedforward.ui;

import com.feedforward.business.SystemController;
import com.feedforward.business.PickupService;
import com.feedforward.db.NotificationDAO;
import com.feedforward.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.List;
import static com.feedforward.ui.UIComponents.*;

public class VolunteerDashboard {
    private Stage stage; private SystemController controller; private PickupService pickupService;
    private NotificationDAO notificationDAO; private User currentUser;
    private BorderPane root; private StackPane rootStack;
    private Button activeBtn; private Timeline autoRefresh;

    public VolunteerDashboard(Stage stage) {
        this.stage = stage; this.controller = SystemController.getInstance();
        this.pickupService = new PickupService(); this.notificationDAO = new NotificationDAO();
        this.currentUser = Session.getCurrentUser();
    }

    public void show() {
        root = new BorderPane(); root.setStyle("-fx-background-color:" + BG + ";");
        root.setTop(makeNavBar("Volunteer Dashboard", currentUser.getName(), () -> { stopAR(); Session.logout(); new LoginScreen(stage).show(); }));

        VBox sb = makeSidebar(null);
        Button b1 = makeSidebarBtn("🔍   Search Food");
        Button b2 = makeSidebarBtn("📦   My Pickups");
        Button b3 = makeSidebarBtn("📍   Track Status");
        int unread = notificationDAO.getNotificationsForUser(currentUser.getUserID()).size();
        StackPane b4Wrap = makeSidebarBtnWithBadge("🔔   Notifications", unread);
        Button b4 = (Button) b4Wrap.getChildren().get(0);

        b1.setOnAction(e -> { stopAR(); go(makeSearch(), b1); });
        b2.setOnAction(e -> { stopAR(); go(makeMyPickups(), b2); });
        b3.setOnAction(e -> go(makeTrack(), b3));
        b4.setOnAction(e -> { stopAR(); go(makeNotifs(), b4); });

        sb.getChildren().addAll(b1, b2, b3, b4Wrap);
        root.setLeft(sb); go(makeSearch(), b1);

        rootStack = new StackPane(root);
        Scene scene = new Scene(rootStack, 1180, 740);
        applyGlobalCSS(scene);
        stage.setScene(scene); stage.setTitle("FeedForward — Volunteer Dashboard"); stage.setResizable(true); stage.show();
    }

    private void go(javafx.scene.Node n, Button btn) {
        if (activeBtn != null) clearActiveBtn(activeBtn);
        if (btn != null) { setActiveBtn(btn); activeBtn = btn; }
        ScrollPane sp = new ScrollPane(n); sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:" + BG + ";-fx-background:" + BG + ";"); root.setCenter(sp);
    }
    private void stopAR() { if (autoRefresh != null) { autoRefresh.stop(); autoRefresh = null; } }

    // ── UC-04 Search ───────────────────────────────────────────────────────────
    private VBox makeSearch() {
        VBox panel = new VBox(22); panel.setPadding(new Insets(34));
        panel.getChildren().addAll(
            makePageHeader("Search Food Listings", "Find food near you and schedule a pickup."),
            makeHeroBanner("Every pickup makes a difference.", "Browse available listings and volunteer for a food pickup today.", AMBER)
        );

        HBox searchBar = new HBox(10); searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(14, 20, 14, 20));
        searchBar.setStyle("-fx-background-color:" + BG_CARD + ";-fx-background-radius:12;-fx-border-color:" + BORDER + ";-fx-border-radius:12;");
        TextField kwField  = makeField("Search food name..."); kwField.setPrefWidth(260);
        TextField locField = makeField("Filter by location..."); locField.setPrefWidth(260);
        Button searchBtn = makePrimaryBtn("🔍  Search");
        Button allBtn    = makeSecondaryBtn("All");
        searchBar.getChildren().addAll(kwField, locField, searchBtn, allBtn);

        VBox listHolder = new VBox(12);
        Runnable loadListings = () -> {
            listHolder.getChildren().clear();
            List<com.feedforward.model.FoodListing> listings = controller.handleSearchListings(
                    kwField.getText().trim(), "", locField.getText().trim());
            if (listings.isEmpty()) { listHolder.getChildren().add(makeEmptyState("🔍", "No listings found.")); return; }
            for (com.feedforward.model.FoodListing fl : listings)
                listHolder.getChildren().add(makeFoodCard(fl, "📅 Schedule", () -> go(makeSchedule(fl), null)));
        };
        loadListings.run();
        searchBtn.setOnAction(e -> loadListings.run());
        allBtn.setOnAction(e -> { kwField.clear(); locField.clear(); loadListings.run(); });

        panel.getChildren().addAll(searchBar, listHolder); return panel;
    }

    // ── UC-05 Schedule — with map ──────────────────────────────────────────────
    private VBox makeSchedule(FoodListing listing) {
        VBox panel = new VBox(20); panel.setPadding(new Insets(34));
        panel.getChildren().add(makePageHeader("Schedule Food Pickup", "Set pickup details for this listing."));

        HBox infoCard = new HBox(20); infoCard.setPadding(new Insets(20, 24, 20, 24)); infoCard.setAlignment(Pos.CENTER_LEFT);
        infoCard.setStyle("-fx-background-color:" + BG_CARD + ";-fx-background-radius:14;-fx-border-color:" + GREEN_MID + ";-fx-border-radius:14;-fx-border-width:1;");
        VBox infoLeft = new VBox(6);
        Label foodLbl = new Label(listing.getFoodName()); foodLbl.setTextFill(Color.web(GREEN)); foodLbl.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        Label details = new Label("Qty: " + listing.getQuantity() + "   ·   📍 " + listing.getLocation());
        details.setTextFill(Color.web(GREY)); details.setFont(Font.font("Arial", 13));
        infoLeft.getChildren().addAll(foodLbl, details); infoCard.getChildren().add(infoLeft);

        Label mapLabel = new Label("📍 Pickup Location"); mapLabel.setTextFill(Color.web(GREY)); mapLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        javafx.scene.Node mapView = makeMapView(listing.getLocation());

        VBox card = makeFormCard();
        VBox dg = makeFieldGroup("Pickup Date *", "yyyy-MM-dd  e.g. 2026-05-10");
        VBox tg = makeFieldGroup("Pickup Time *", "HH:mm  e.g. 14:30");
        TextField df = (TextField) dg.getChildren().get(1);
        TextField tf = (TextField) tg.getChildren().get(1);
        ComboBox<String> transBox = new ComboBox<>();
        transBox.getItems().addAll("Motorcycle", "Car", "Van", "Bicycle", "Walking"); transBox.setValue("Motorcycle");
        VBox transGrp = makeComboGroup("Transport Method *", transBox);
        Label fb = makeFeedback();
        Button schedBtn = makePrimaryBtn("📅  Confirm Schedule");
        Button back = makeSecondaryBtn("← Back"); back.setOnAction(e -> go(makeSearch(), null));

        schedBtn.setOnAction(e -> {
            String r = controller.handleSchedulePickup(currentUser.getUserID(), listing.getListingID(),
                    df.getText().trim(), tf.getText().trim(), transBox.getValue());
            if (r.equals("success")) {
                showSuccessOverlay(rootStack, "Pickup scheduled! 🚗\nTrack it in Track Status.", () -> go(makeTrack(), null));
            } else { setError(fb, r); }
        });

        card.getChildren().addAll(dg, tg, transGrp, fb, new HBox(10, schedBtn, back));
        panel.getChildren().addAll(infoCard, mapLabel, mapView, card);
        return panel;
    }

    // ── My Pickups ─────────────────────────────────────────────────────────────
    private VBox makeMyPickups() {
        VBox panel = new VBox(20); panel.setPadding(new Insets(34));
        panel.getChildren().add(makePageHeader("My Pickups", "All your scheduled pickups."));
        List<PickupSchedule> pickups = pickupService.getUserPickups(currentUser.getUserID());
        panel.getChildren().add(makeKanbanBoard(pickups));
        return panel;
    }

    // ── UC-07 Track — Kanban + live refresh ───────────────────────────────────
    private VBox makeTrack() {
        VBox panel = new VBox(22); panel.setPadding(new Insets(34));
        panel.getChildren().add(makePageHeader("Track Pickup Status", "Live kanban board — refreshes every 3 seconds."));

        HBox livePill = new HBox(8); livePill.setAlignment(Pos.CENTER_LEFT);
        livePill.setPadding(new Insets(8, 16, 8, 16));
        livePill.setStyle("-fx-background-color:" + GREEN_DIM + ";-fx-background-radius:20;-fx-border-color:" + GREEN_MID + ";-fx-border-radius:20;-fx-border-width:1;");
        Circle liveDot = new Circle(5); liveDot.setFill(Color.web(GREEN));
        liveDot.setStyle("-fx-effect:dropshadow(gaussian," + GREEN + ",8,0.6,0,0);");
        Label liveLbl = new Label("LIVE — Auto-refreshing every 3 seconds");
        liveLbl.setTextFill(Color.web(GREEN)); liveLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        livePill.getChildren().addAll(liveDot, liveLbl);
        panel.getChildren().add(livePill);

        VBox[] holder = { new VBox() };
        List<PickupSchedule> pickups = pickupService.getUserPickups(currentUser.getUserID());
        holder[0].getChildren().add(makeKanbanBoard(pickups));
        panel.getChildren().add(holder[0]);

        stopAR();
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            holder[0].getChildren().clear();
            List<PickupSchedule> fresh = pickupService.getUserPickups(currentUser.getUserID());
            holder[0].getChildren().add(makeKanbanBoard(fresh));
        }));
        autoRefresh.setCycleCount(Timeline.INDEFINITE); autoRefresh.play();
        return panel;
    }

    private VBox makeNotifs() {
        VBox panel = new VBox(20); panel.setPadding(new Insets(34));
        panel.getChildren().add(makePageHeader("Notifications", "Recent alerts."));
        List<Notification> notifs = notificationDAO.getNotificationsForUser(currentUser.getUserID());
        VBox list = new VBox(10);
        if (notifs.isEmpty()) list.getChildren().add(makeEmptyState("🔔", "No notifications yet."));
        else for (Notification n : notifs) list.getChildren().add(makeNotifCard(n.getMessage(), n.getDateSent().toString()));
        panel.getChildren().add(list); return panel;
    }
}
