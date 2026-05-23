package com.feedforward.ui;

import com.feedforward.business.SystemController;
import com.feedforward.business.FoodListingService;
import com.feedforward.db.NotificationDAO;
import com.feedforward.model.*;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.List;
import static com.feedforward.ui.UIComponents.*;

public class DonorDashboard {
    private Stage stage;
    private SystemController controller;
    private FoodListingService listingService;
    private NotificationDAO notificationDAO;
    private User currentUser;
    private BorderPane root;
    private StackPane rootStack;   // root stack for overlays
    private Button activeBtn;

    public DonorDashboard(Stage stage) {
        this.stage = stage;
        this.controller = SystemController.getInstance();
        this.listingService = new FoodListingService();
        this.notificationDAO = new NotificationDAO();
        this.currentUser = Session.getCurrentUser();
    }

    public void show() {
        root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setTop(makeNavBar("Donor Dashboard", currentUser.getName(), () -> { Session.logout(); new LoginScreen(stage).show(); }));

        VBox sb = makeSidebar(null);
        Button b1 = makeSidebarBtn("🏠   Home");
        Button b2 = makeSidebarBtn("🍽   List Surplus Food");
        Button b3 = makeSidebarBtn("📋   My Listings");
        Button b4 = makeSidebarBtn("🤖   AI Matching");

        // Notification badge
        int unread = notificationDAO.getNotificationsForUser(currentUser.getUserID()).size();
        StackPane b5Wrap = makeSidebarBtnWithBadge("🔔   Notifications", unread);
        Button b5 = (Button) b5Wrap.getChildren().get(0);

        b1.setOnAction(e -> go(makeHomePanel(), b1));
        b2.setOnAction(e -> go(makeListPanel(), b2));
        b3.setOnAction(e -> go(makeMyListings(), b3));
        b4.setOnAction(e -> go(makeAIPanel(), b4));
        b5.setOnAction(e -> go(makeNotifs(), b5));

        sb.getChildren().addAll(b1, b2, b3, b4, b5Wrap);
        root.setLeft(sb);
        go(makeHomePanel(), b1);

        // Wrap in StackPane for overlay support
        rootStack = new StackPane(root);
        Scene scene = new Scene(rootStack, 1180, 740);
        applyGlobalCSS(scene);
        stage.setScene(scene);
        stage.setTitle("FeedForward — Donor Dashboard");
        stage.setResizable(true);
        stage.show();
    }

    private void go(javafx.scene.Node n, Button btn) {
        if (activeBtn != null) clearActiveBtn(activeBtn);
        if (btn != null) { setActiveBtn(btn); activeBtn = btn; }
        ScrollPane sp = new ScrollPane(n);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:" + BG + ";-fx-background:" + BG + ";");
        root.setCenter(sp);
    }

    // ── HOME — personal stats card (UC-03, UC-08 summary) ─────────────────────
    private VBox makeHomePanel() {
        VBox panel = new VBox(24); panel.setPadding(new Insets(34));

        // Welcome block
        VBox welcome = new VBox(6);
        Label tag = new Label("● FEEDFORWARD"); tag.setTextFill(Color.web(GREEN)); tag.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        Text greeting = new Text("Hello, " + currentUser.getName() + " 👋");
        greeting.setFont(Font.font("Arial", FontWeight.BOLD, 28)); greeting.setFill(Color.web(WHITE));
        Text sub = new Text("Here's your donation activity at a glance.");
        sub.setFont(Font.font("Arial", 13)); sub.setFill(Color.web(GREY));
        welcome.getChildren().addAll(tag, greeting, sub);
        panel.getChildren().add(welcome);

        // Stats from FoodListingService
        List<FoodListing> myListings = listingService.getDonorListings(currentUser.getUserID());
        long totalListings = myListings.size();
        long available = myListings.stream().filter(l -> "Available".equalsIgnoreCase(l.getStatus())).count();
        long collected = myListings.stream().filter(l -> "Collected".equalsIgnoreCase(l.getStatus())).count();

        FlowPane stats = new FlowPane(14, 14);
        stats.getChildren().addAll(
                makeStatCard("Total Listed",   String.valueOf(totalListings), GREEN),
                makeStatCard("Available Now",  String.valueOf(available),     AMBER),
                makeStatCard("Collected",      String.valueOf(collected),     BLUE),
                makeStatCard("AI Matches Sent","On Submit",                   PURPLE));
        panel.getChildren().add(stats);

        // Quick actions
        Text actTitle = new Text("Quick Actions");
        actTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16)); actTitle.setFill(Color.web(WHITE));

        HBox actions = new HBox(14);
        Button listNowBtn = makePrimaryBtn("🍽  List Food Now");
        Button viewListBtn = makeSecondaryBtn("📋  View My Listings");
        Button aiBtn = makeSecondaryBtn("🤖  Run AI Match");
        listNowBtn.setOnAction(e -> go(makeListPanel(), null));
        viewListBtn.setOnAction(e -> go(makeMyListings(), null));
        aiBtn.setOnAction(e -> go(makeAIPanel(), null));
        actions.getChildren().addAll(listNowBtn, viewListBtn, aiBtn);

        panel.getChildren().addAll(actTitle, actions);

        // Recent listings preview
        if (!myListings.isEmpty()) {
            Text recentTitle = new Text("Recent Listings");
            recentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16)); recentTitle.setFill(Color.web(WHITE));
            panel.getChildren().add(recentTitle);

            VBox recentCards = new VBox(10);
            int limit = Math.min(3, myListings.size());
            for (int i = 0; i < limit; i++) {
                FoodListing fl = myListings.get(myListings.size() - 1 - i); // most recent
                HBox card = new HBox(16); card.setPadding(new Insets(14, 18, 14, 18)); card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle("-fx-background-color:" + BG_CARD + ";-fx-background-radius:10;-fx-border-color:" + BORDER + ";-fx-border-radius:10;");
                VBox info = new VBox(4); HBox.setHgrow(info, Priority.ALWAYS);
                Label name = new Label(fl.getFoodName()); name.setFont(Font.font("Arial", FontWeight.BOLD, 14)); name.setTextFill(Color.web(WHITE));
                Label detail = new Label("Qty: " + fl.getQuantity() + "   ·   " + fl.getLocation());
                detail.setFont(Font.font("Arial", 12)); detail.setTextFill(Color.web(GREY));
                info.getChildren().addAll(name, detail);
                Label expiryPill = makeExpiryPill(fl.getExpiryTime() != null ? fl.getExpiryTime().toString() : "");
                Label statusBadge = makeStatusBadge(fl.getStatus() != null ? fl.getStatus() : "Available");
                HBox right = new HBox(10, expiryPill, statusBadge); right.setAlignment(Pos.CENTER_RIGHT);
                card.getChildren().addAll(info, right);
                recentCards.getChildren().add(card);
            }
            panel.getChildren().add(recentCards);
        }
        return panel;
    }

    // ── UC-03 List Food ────────────────────────────────────────────────────────
    private VBox makeListPanel() {
        VBox panel = new VBox(24); panel.setPadding(new Insets(34));
        panel.getChildren().add(makePageHeader("List Surplus Food", "Submit food for redistribution — AI matching triggers automatically."));

        HBox cols = new HBox(20);
        VBox card = makeFormCard();
        VBox nameGrp   = makeFieldGroup("Food Name *",          "e.g. Biryani, Bread, Rice");
        VBox qtyGrp    = makeFieldGroup("Quantity (units) *",   "e.g. 50");
        VBox expiryGrp = makeFieldGroup("Expiry Date & Time *", "yyyy-MM-dd HH:mm  e.g. 2026-05-10 18:00");
        VBox locGrp    = makeFieldGroup("Pickup Location *",    "e.g. F-7 Islamabad");
        TextField nameF   = (TextField) nameGrp.getChildren().get(1);
        TextField qtyF    = (TextField) qtyGrp.getChildren().get(1);
        TextField expiryF = (TextField) expiryGrp.getChildren().get(1);
        TextField locF    = (TextField) locGrp.getChildren().get(1);
        Label feedback = makeFeedback();
        Button submitBtn = makePrimaryBtn("🍱  Submit Listing");
        Button clearBtn  = makeSecondaryBtn("Clear");
        HBox btnRow = new HBox(10, submitBtn, clearBtn); btnRow.setAlignment(Pos.CENTER_LEFT);

        submitBtn.setOnAction(e -> {
            String result = controller.handleListFood(currentUser.getUserID(),
                    nameF.getText().trim(), qtyF.getText().trim(),
                    expiryF.getText().trim(), locF.getText().trim());
            if (result.equals("success")) {
                String foodName = nameF.getText().trim();
                nameF.clear(); qtyF.clear(); expiryF.clear(); locF.clear();
                // Animated success overlay
                showSuccessOverlay(rootStack, "\"" + foodName + "\" listed!\nAI is notifying matching NGOs.", null);
            } else { setError(feedback, result); }
        });
        clearBtn.setOnAction(e -> { nameF.clear(); qtyF.clear(); expiryF.clear(); locF.clear(); feedback.setText(""); });

        card.getChildren().addAll(nameGrp, qtyGrp, expiryGrp, locGrp, feedback, btnRow);

        // Tips sidebar
        VBox tips = new VBox(14); tips.setPrefWidth(240); tips.setPadding(new Insets(24));
        tips.setStyle("-fx-background-color:" + BG_CARD + ";-fx-background-radius:14;-fx-border-color:" + BORDER + ";-fx-border-radius:14;");
        Text tipsTitle = new Text("💡 Tips"); tipsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15)); tipsTitle.setFill(Color.web(GREEN));
        VBox tipItems = new VBox(12);
        for (String tip : new String[]{"Set accurate expiry — NGOs prioritize fresh food", "Be specific about location for faster matching", "Higher quantities match more NGOs", "Add food type for better AI scoring"}) {
            HBox row = new HBox(10); row.setAlignment(Pos.TOP_LEFT);
            Label dot = new Label("→"); dot.setTextFill(Color.web(GREEN)); dot.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            Label t = new Label(tip); t.setTextFill(Color.web(GREY)); t.setFont(Font.font("Arial", 12)); t.setWrapText(true); HBox.setHgrow(t, Priority.ALWAYS);
            row.getChildren().addAll(dot, t); tipItems.getChildren().add(row);
        }
        tips.getChildren().addAll(tipsTitle, tipItems);
        HBox.setHgrow(card, Priority.ALWAYS); cols.getChildren().addAll(card, tips);
        panel.getChildren().add(cols);
        return panel;
    }

    // ── My Listings — with expiry countdown pills ──────────────────────────────
    private VBox makeMyListings() {
        VBox panel = new VBox(20); panel.setPadding(new Insets(34));
        panel.getChildren().add(makePageHeader("My Food Listings", "All listings you have submitted."));

        List<FoodListing> listings = listingService.getDonorListings(currentUser.getUserID());
        if (listings.isEmpty()) {
            panel.getChildren().add(makeEmptyState("🍽", "No listings yet. List your first food item!"));
            return panel;
        }

        // Cards with expiry pills
        VBox listCards = new VBox(12);
        for (FoodListing fl : listings) {
            HBox card = new HBox(16); card.setPadding(new Insets(16, 20, 16, 20)); card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color:" + BG_CARD + ";-fx-background-radius:12;-fx-border-color:" + BORDER + ";-fx-border-radius:12;-fx-border-width:1;");
            card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:" + BG_CARD2 + ";-fx-background-radius:12;-fx-border-color:" + GREEN_MID + ";-fx-border-radius:12;-fx-border-width:1;"));
            card.setOnMouseExited(e  -> card.setStyle("-fx-background-color:" + BG_CARD + ";-fx-background-radius:12;-fx-border-color:" + BORDER + ";-fx-border-radius:12;-fx-border-width:1;"));

            VBox info = new VBox(5); HBox.setHgrow(info, Priority.ALWAYS);
            Label name = new Label(fl.getFoodName()); name.setFont(Font.font("Arial", FontWeight.BOLD, 15)); name.setTextFill(Color.web(WHITE));
            Label detail = new Label("Qty: " + fl.getQuantity() + "   ·   📍 " + fl.getLocation());
            detail.setFont(Font.font("Arial", 12)); detail.setTextFill(Color.web(GREY));
            info.getChildren().addAll(name, detail);

            VBox right = new VBox(8); right.setAlignment(Pos.CENTER_RIGHT);
            Label expiryPill = makeExpiryPill(fl.getExpiryTime() != null ? fl.getExpiryTime().toString() : "");
            Label statusBadge = makeStatusBadge(fl.getStatus() != null ? fl.getStatus() : "Available");
            right.getChildren().addAll(expiryPill, statusBadge);

            card.getChildren().addAll(info, right);
            listCards.getChildren().add(card);
        }

        Button refreshBtn = makeSecondaryBtn("↻  Refresh");
        refreshBtn.setOnAction(e -> go(makeMyListings(), null));
        panel.getChildren().addAll(refreshBtn, listCards);
        return panel;
    }

    // ── UC-08 AI Matching — NGO cards with score rings ─────────────────────────
    private VBox makeAIPanel() {
        VBox panel = new VBox(22); panel.setPadding(new Insets(34));

        VBox hero = new VBox(14); hero.setPadding(new Insets(28));
        hero.setStyle("-fx-background-color:" + GREEN_DIM + ";-fx-background-radius:16;-fx-border-color:" + GREEN_MID + ";-fx-border-radius:16;-fx-border-width:1;");
        Text aiTitle = new Text("🤖  AI Donor-Recipient Matching");
        aiTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22)); aiTitle.setFill(Color.web(GREEN));
        aiTitle.setStyle("-fx-effect:dropshadow(gaussian," + GREEN + ",14,0.3,0,0);");
        Text aiSub = new Text("The AI scores every NGO by service region proximity.\nResults show as ranked cards with animated confidence score rings.");
        aiSub.setFont(Font.font("Arial", 13)); aiSub.setFill(Color.web(GREY));
        HBox inputRow = new HBox(12); inputRow.setAlignment(Pos.CENTER_LEFT);
        TextField locInput = makeField("Enter location — e.g. F-7 Islamabad"); locInput.setPrefWidth(380);
        Button matchBtn = makePrimaryBtn("🤖  Run AI Match");
        inputRow.getChildren().addAll(locInput, matchBtn);
        hero.getChildren().addAll(aiTitle, aiSub, inputRow);

        Label statusLbl = makeFeedback();

        HBox thinkingRow = new HBox(10); thinkingRow.setAlignment(Pos.CENTER_LEFT); thinkingRow.setVisible(false);
        Label spinner = new Label("⬤ ⬤ ⬤"); spinner.setTextFill(Color.web(GREEN)); spinner.setFont(Font.font("Arial", 14));
        Label thinkLbl = new Label("AI is scanning NGO regions and computing confidence scores...");
        thinkLbl.setTextFill(Color.web(GREY)); thinkLbl.setFont(Font.font("Arial", 13));
        thinkingRow.getChildren().addAll(spinner, thinkLbl);

        VBox resultsBox = new VBox(12); resultsBox.setVisible(false);

        matchBtn.setOnAction(e -> {
            String loc = locInput.getText().trim();
            if (loc.isEmpty()) { setError(statusLbl, "Please enter a location."); return; }
            matchBtn.setDisable(true); thinkingRow.setVisible(true); resultsBox.setVisible(false); statusLbl.setText("");

            PauseTransition pause = new PauseTransition(Duration.millis(950));
            pause.setOnFinished(ev -> {
                List<String[]> results = controller.handleAIMatching(loc);
                resultsBox.getChildren().clear();
                if (results.isEmpty()) {
                    resultsBox.getChildren().add(makeEmptyState("🔍", "No NGOs matched this location."));
                } else {
                    Label hdrLbl = new Label(results.size() + " NGOs matched — ranked by confidence score");
                    hdrLbl.setTextFill(Color.web(GREEN)); hdrLbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    resultsBox.getChildren().add(hdrLbl);

                    String[] ringColors = {GREEN, PURPLE, AMBER, RED, BLUE};
                    for (int i = 0; i < results.size(); i++) {
                        String[] r = results.get(i);
                        double score; try { score = Double.parseDouble(r[3].replace("%", "").trim()); } catch (Exception ex) { score = 30; }
                        // NGO card with circular score ring
                        resultsBox.getChildren().add(makeNGOCard(i + 1, r[0], r[1], r[2], score, ringColors[i % ringColors.length]));
                    }
                    setSuccess(statusLbl, results.size() + " matching NGO(s) found for " + loc + ".");
                }
                thinkingRow.setVisible(false); resultsBox.setVisible(true); matchBtn.setDisable(false);
            });
            pause.play();
        });

        panel.getChildren().addAll(hero, thinkingRow, statusLbl, resultsBox);
        return panel;
    }

    // ── Notifications ──────────────────────────────────────────────────────────
    private VBox makeNotifs() {
        VBox panel = new VBox(20); panel.setPadding(new Insets(34));
        panel.getChildren().add(makePageHeader("Notifications", "Recent alerts and activity."));
        List<Notification> notifs = notificationDAO.getNotificationsForUser(currentUser.getUserID());
        VBox list = new VBox(10);
        if (notifs.isEmpty()) list.getChildren().add(makeEmptyState("🔔", "No notifications yet."));
        else for (Notification n : notifs) list.getChildren().add(makeNotifCard(n.getMessage(), n.getDateSent().toString()));
        panel.getChildren().add(list); return panel;
    }
}
