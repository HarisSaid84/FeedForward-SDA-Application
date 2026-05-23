package com.feedforward.ui;

import com.feedforward.business.*;
import com.feedforward.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.util.*;
import static com.feedforward.ui.UIComponents.*;

public class AdminDashboard {
    private Stage stage; private SystemController controller;
    private UserService userService; private FoodListingService listingService;
    private PickupService pickupService; private AnalyticsService analyticsService;
    private User currentUser; private BorderPane root; private StackPane rootStack; private Button activeBtn;

    public AdminDashboard(Stage stage){
        this.stage=stage; this.controller=SystemController.getInstance();
        this.userService=new UserService(); this.listingService=new FoodListingService();
        this.pickupService=new PickupService(); this.analyticsService=new AnalyticsService();
        this.currentUser=Session.getCurrentUser();
    }

    public void show(){
        root=new BorderPane(); root.setStyle("-fx-background-color:"+BG+";");
        root.setTop(makeNavBar("Admin Dashboard",currentUser.getName(),()->{Session.logout();new LoginScreen(stage).show();}));
        VBox sb=makeSidebar(null);
        Button b1=makeSidebarBtn("📊   Overview");
        Button b2=makeSidebarBtn("👥   Manage Users");
        Button b3=makeSidebarBtn("🍽   Manage Listings");
        Button b4=makeSidebarBtn("📦   All Pickups");
        Button b5=makeSidebarBtn("📈   Analytics");
        Button b6=makeSidebarBtn("📄   Generate Report");
        b1.setOnAction(e->go(makeOverview(),b1)); b2.setOnAction(e->go(makeUsers(),b2));
        b3.setOnAction(e->go(makeListings(),b3)); b4.setOnAction(e->go(makePickups(),b4));
        b5.setOnAction(e->go(makeAnalytics(),b5)); b6.setOnAction(e->go(makeReport(),b6));
        sb.getChildren().addAll(b1,b2,b3,b4,b5,b6); root.setLeft(sb); go(makeOverview(),b1);
        rootStack = new StackPane(root);
        Scene scene = new Scene(rootStack,1180,740);
        applyGlobalCSS(scene);
        stage.setScene(scene); stage.setTitle("FeedForward — Admin Dashboard"); stage.setResizable(true); stage.show();
    }

    private void go(javafx.scene.Node n,Button btn){
        if(activeBtn!=null)clearActiveBtn(activeBtn); if(btn!=null){setActiveBtn(btn);activeBtn=btn;}
        ScrollPane sp=new ScrollPane(n); sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:"+BG+";-fx-background:"+BG+";"); root.setCenter(sp);
    }

    // ── Overview — big bold stats ──────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox makeOverview(){
        VBox panel=new VBox(28); panel.setPadding(new Insets(34));
        panel.getChildren().add(makePageHeader("System Overview","Welcome back, "+currentUser.getName()+"! Here's today's snapshot."));

        // Big stat cards in a row
        FlowPane stats=new FlowPane(14,14);
        stats.getChildren().addAll(
                makeStatCard("Total Users",        String.valueOf(analyticsService.getTotalUsers()),            GREEN),
                makeStatCard("Total Listings",     String.valueOf(analyticsService.getTotalListings()),         AMBER),
                makeStatCard("Pickups Completed",  String.valueOf(analyticsService.getTotalPickupsCompleted()), PURPLE),
                makeStatCard("Food Units Saved",   String.valueOf(analyticsService.getTotalFoodSaved()),        GREEN),
                makeStatCard("NGOs",               String.valueOf(analyticsService.getTotalNGOs()),             RED),
                makeStatCard("Donors",             String.valueOf(analyticsService.getTotalDonors()),           BLUE));

        Text recentTitle=new Text("Recent Food Listings");
        recentTitle.setFont(Font.font("Arial",FontWeight.BOLD,17)); recentTitle.setFill(Color.web(WHITE));
        TableView<FoodListing> table=new TableView<>(); table.setPrefHeight(300); styleTable(table);
        table.getColumns().addAll(makeCol("Food","foodName",195),makeCol("Donor","donorName",155),makeCol("Location","location",155),makeCol("Status","status",115),makeCol("Posted","datePosted",175));
        table.setItems(FXCollections.observableArrayList(listingService.getAllListings()));
        panel.getChildren().addAll(stats,recentTitle,table); return panel;
    }

    // ── UC-11 Users ────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox makeUsers(){
        VBox panel=new VBox(20); panel.setPadding(new Insets(34));
        panel.getChildren().add(makePageHeader("Manage User Accounts","Activate, suspend, or delete users."));
        Label fb=makeFeedback();
        TableView<String[]> table=new TableView<>(); table.setPrefHeight(500); styleTable(table);
        TableColumn<String[],String> id=new TableColumn<>("ID"); id.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue()[0])); id.setPrefWidth(55);
        TableColumn<String[],String> name=new TableColumn<>("Name"); name.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue()[1])); name.setPrefWidth(175);
        TableColumn<String[],String> email=new TableColumn<>("Email"); email.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue()[2])); email.setPrefWidth(215);
        TableColumn<String[],String> role=new TableColumn<>("Role"); role.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue()[3])); role.setPrefWidth(90);
        TableColumn<String[],String> stat=new TableColumn<>("Status"); stat.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue()[4])); stat.setPrefWidth(90);
        TableColumn<String[],Void> act=new TableColumn<>("Actions"); act.setPrefWidth(260);
        act.setCellFactory(col->new TableCell<>(){
            final Button susp=makeWarningBtn("⏸ Suspend"); final Button actv=makeSuccessBtn("▶ Activate"); final Button del=makeDangerBtn("🗑 Delete");
            final HBox box=new HBox(7,susp,actv,del);
            {susp.setOnAction(e->{String[] r=getTableView().getItems().get(getIndex()); controller.handleManageUser(currentUser.getUserID(),Integer.parseInt(r[0]),"suspend"); setSuccess(fb,r[1]+" suspended."); refreshUsers(table);});
             actv.setOnAction(e->{String[] r=getTableView().getItems().get(getIndex()); controller.handleManageUser(currentUser.getUserID(),Integer.parseInt(r[0]),"activate"); setSuccess(fb,r[1]+" activated."); refreshUsers(table);});
             del.setOnAction(e->{String[] r=getTableView().getItems().get(getIndex()); Alert c=new Alert(Alert.AlertType.CONFIRMATION,"Delete "+r[1]+"?",ButtonType.OK,ButtonType.CANCEL); c.showAndWait().ifPresent(res->{if(res==ButtonType.OK){controller.handleManageUser(currentUser.getUserID(),Integer.parseInt(r[0]),"delete"); setSuccess(fb,r[1]+" deleted."); refreshUsers(table);}});});}
            @Override protected void updateItem(Void v,boolean empty){super.updateItem(v,empty);setGraphic(empty?null:box);}
        });
        table.getColumns().addAll(id,name,email,role,stat,act); refreshUsers(table);
        Button r=makeSecondaryBtn("↻  Refresh"); r.setOnAction(e->refreshUsers(table));
        panel.getChildren().addAll(fb,r,table); return panel;
    }
    private void refreshUsers(TableView<String[]> table){
        ObservableList<String[]> data=FXCollections.observableArrayList();
        try{ResultSet rs=userService.getAllUsers(); while(rs!=null&&rs.next()) data.add(new String[]{String.valueOf(rs.getInt("userID")),rs.getString("name"),rs.getString("email"),rs.getString("role"),rs.getString("accountStatus")});}catch(Exception e){System.out.println(e.getMessage());}
        table.setItems(data);
    }

    // ── UC-12 Listings ─────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox makeListings(){
        VBox panel=new VBox(20); panel.setPadding(new Insets(34));
        panel.getChildren().add(makePageHeader("Manage Food Listings","Flag, remove, or mark listings as collected."));
        Label fb=makeFeedback();
        TableView<FoodListing> table=new TableView<>(); table.setPrefHeight(500); styleTable(table);
        TableColumn<FoodListing,Void> act=new TableColumn<>("Actions"); act.setPrefWidth(255);
        act.setCellFactory(col->new TableCell<>(){
            final Button flag=makeWarningBtn("🚩 Flag"); final Button rem=makeDangerBtn("🗑 Remove"); final Button coll=makeSuccessBtn("✓ Collected");
            final HBox box=new HBox(7,flag,rem,coll);
            {flag.setOnAction(e->{FoodListing fl=getTableView().getItems().get(getIndex()); controller.handleManageListing(currentUser.getUserID(),fl.getListingID(),"flag"); setSuccess(fb,fl.getFoodName()+" flagged."); table.setItems(FXCollections.observableArrayList(listingService.getAllListings()));});
             rem.setOnAction(e->{FoodListing fl=getTableView().getItems().get(getIndex()); Alert c=new Alert(Alert.AlertType.CONFIRMATION,"Remove "+fl.getFoodName()+"?",ButtonType.OK,ButtonType.CANCEL); c.showAndWait().ifPresent(r->{if(r==ButtonType.OK){controller.handleManageListing(currentUser.getUserID(),fl.getListingID(),"remove"); setSuccess(fb,fl.getFoodName()+" removed."); table.setItems(FXCollections.observableArrayList(listingService.getAllListings()));}});});
             coll.setOnAction(e->{FoodListing fl=getTableView().getItems().get(getIndex()); controller.handleManageListing(currentUser.getUserID(),fl.getListingID(),"collected"); setSuccess(fb,fl.getFoodName()+" collected."); table.setItems(FXCollections.observableArrayList(listingService.getAllListings()));});}
            @Override protected void updateItem(Void v,boolean empty){super.updateItem(v,empty);setGraphic(empty?null:box);}
        });
        table.getColumns().addAll(makeCol("ID","listingID",55),makeCol("Food","foodName",180),makeCol("Donor","donorName",155),makeCol("Location","location",145),makeCol("Status","status",105),act);
        table.setItems(FXCollections.observableArrayList(listingService.getAllListings()));
        Button r=makeSecondaryBtn("↻  Refresh"); r.setOnAction(e->table.setItems(FXCollections.observableArrayList(listingService.getAllListings())));
        panel.getChildren().addAll(fb,r,table); return panel;
    }

    // ── All Pickups ────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox makePickups(){
        VBox panel=new VBox(20); panel.setPadding(new Insets(34));
        panel.getChildren().add(makePageHeader("All Pickup Schedules","System-wide pickups. Mark as completed here."));
        Label fb=makeFeedback();
        TableView<PickupSchedule> table=new TableView<>(); table.setPrefHeight(500); styleTable(table);

        // Explicit string columns to avoid unchecked warnings
        TableColumn<PickupSchedule,String> idCol=makeCol("ID","pickupID",55);
        TableColumn<PickupSchedule,String> foodCol=makeCol("Food","foodName",185);
        TableColumn<PickupSchedule,String> byCol=makeCol("By","scheduledByName",160);
        TableColumn<PickupSchedule,String> transCol=makeCol("Transport","transportMethod",120);
        TableColumn<PickupSchedule,String> statCol=makeCol("Status","status",115);

        // Date column — convert LocalDate to String
        TableColumn<PickupSchedule,String> dateCol=new TableColumn<>("Date"); dateCol.setPrefWidth(120);
        dateCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(
                d.getValue().getPickupDate()!=null?d.getValue().getPickupDate().toString():""));

        TableColumn<PickupSchedule,Void> act=new TableColumn<>("Action"); act.setPrefWidth(140);
        act.setCellFactory(col->new TableCell<>(){
            final Button btn=makeSuccessBtn("✓ Complete");
            {btn.setOnAction(e->{PickupSchedule p=getTableView().getItems().get(getIndex());
                pickupService.completePickup(p.getPickupID(),p.getListingID(),p.getUserID());
                setSuccess(fb,"Pickup #"+p.getPickupID()+" complete.");
                table.setItems(FXCollections.observableArrayList(pickupService.getAllPickups()));});}
            @Override protected void updateItem(Void v,boolean empty){super.updateItem(v,empty);setGraphic(empty?null:btn);}
        });
        table.getColumns().addAll(idCol,foodCol,byCol,dateCol,transCol,statCol,act);
        table.setItems(FXCollections.observableArrayList(pickupService.getAllPickups()));
        Button r=makeSecondaryBtn("↻  Refresh"); r.setOnAction(e->table.setItems(FXCollections.observableArrayList(pickupService.getAllPickups())));
        panel.getChildren().addAll(fb,r,table); return panel;
    }

    // ── UC-10 Analytics with charts ────────────────────────────────────────────
    private VBox makeAnalytics(){
        VBox panel=new VBox(28); panel.setPadding(new Insets(34));
        panel.getChildren().addAll(
            makePageHeader("Waste Analytics Dashboard","Real-time food redistribution statistics and charts."),
            makeHeroBanner("Reducing waste, one pickup at a time.","Live system statistics across all users and listings.", GREEN));

        FlowPane stats=new FlowPane(14,14);
        stats.getChildren().addAll(
                makeStatCard("Listings",    String.valueOf(analyticsService.getTotalListings()),          GREEN),
                makeStatCard("Pickups Done",String.valueOf(analyticsService.getTotalPickupsCompleted()),  PURPLE),
                makeStatCard("Food Saved",  String.valueOf(analyticsService.getTotalFoodSaved()),         AMBER),
                makeStatCard("Users",       String.valueOf(analyticsService.getTotalUsers()),             BLUE),
                makeStatCard("Donors",      String.valueOf(analyticsService.getTotalDonors()),            RED),
                makeStatCard("NGOs",        String.valueOf(analyticsService.getTotalNGOs()),              GREEN));

        // Line graph — pickups over time (mock from DB data)
        List<String[]> lineData = new ArrayList<>();
        try {
            java.sql.ResultSet rs = analyticsService.getListingsByLocation();
            int i=0;
            while(rs!=null&&rs.next()&&i<6) {
                lineData.add(new String[]{rs.getString("location").length()>8?rs.getString("location").substring(0,8):rs.getString("location"), String.valueOf(rs.getInt("count"))});
                i++;
            }
        } catch(Exception ignored){}
        if(lineData.isEmpty()) { lineData.add(new String[]{"Jan",String.valueOf(analyticsService.getTotalDonors())}); lineData.add(new String[]{"Feb",String.valueOf(analyticsService.getTotalNGOs())}); lineData.add(new String[]{"Total",String.valueOf(analyticsService.getTotalListings())}); lineData.add(new String[]{"Pickups",String.valueOf(analyticsService.getTotalPickupsCompleted())}); }
        VBox lineGraph = makeLineGraph("Activity Overview", lineData, GREEN);

        // Bar + donut row
        List<String[]> locationData=new ArrayList<>();
        try {
            java.sql.ResultSet rs=analyticsService.getListingsByLocation(); int lim=0;
            while(rs!=null&&rs.next()&&lim<6){locationData.add(new String[]{rs.getString("location"),String.valueOf(rs.getInt("count"))});lim++;}
        } catch(Exception ignored){}
        VBox bar=makeBarChart("Listings by Location",locationData,GREEN);

        Map<String,Integer> donutData=new LinkedHashMap<>();
        int completed=analyticsService.getTotalPickupsCompleted();
        int listings=analyticsService.getTotalListings();
        donutData.put("Completed",completed); donutData.put("Available",Math.max(0,listings-completed));
        donutData.put("Food Saved",analyticsService.getTotalFoodSaved());
        VBox donut=makeDonutChart("Breakdown",donutData);

        HBox chartsRow=new HBox(16,bar,donut); HBox.setHgrow(bar,Priority.ALWAYS);
        panel.getChildren().addAll(stats,lineGraph,chartsRow); return panel;
    }

    // ── UC-09 Report ───────────────────────────────────────────────────────────
    private VBox makeReport(){
        VBox panel=new VBox(26); panel.setPadding(new Insets(34));
        panel.getChildren().add(makePageHeader("Generate Report","Build, preview, and save system reports."));
        VBox card=makeFormCard(); card.setMaxWidth(680);
        ComboBox<String> typeBox=new ComboBox<>(); typeBox.getItems().addAll("Full System Report","Food Listings Report","Pickup Activity Report","User Activity Report"); typeBox.setValue("Full System Report");
        VBox tg=makeComboGroup("Report Type",typeBox);
        VBox dg=makeFieldGroup("Date Range","e.g. May 2026  or  2026-01-01 to 2026-05-01"); TextField df=(TextField)dg.getChildren().get(1);
        ComboBox<String> fmtBox=new ComboBox<>(); fmtBox.getItems().addAll("Text","PDF","CSV"); fmtBox.setValue("Text");
        VBox fg=makeComboGroup("Export Format",fmtBox);
        TextArea out=new TextArea("Report will appear here..."); out.setPrefHeight(300); out.setEditable(false);
        out.setStyle("-fx-background-color:"+BG_INPUT+";-fx-text-fill:"+GREEN+";-fx-border-color:"+BORDER+";-fx-border-radius:10;-fx-background-radius:10;-fx-font-family:monospace;-fx-font-size:12;");
        Label fb=makeFeedback();
        Button genBtn=makePrimaryBtn("📄  Generate Report");
        genBtn.setOnAction(e->{
            String range=df.getText().trim(); if(range.isEmpty())range="All Time";
            Report report=controller.handleGenerateReport(currentUser.getUserID(),typeBox.getValue(),range,fmtBox.getValue());
            out.setText(report.getContent()); setSuccess(fb,"Report generated and saved to database.");
        });
        card.getChildren().addAll(tg,dg,fg,genBtn,fb);
        panel.getChildren().addAll(card,out); return panel;
    }
}
