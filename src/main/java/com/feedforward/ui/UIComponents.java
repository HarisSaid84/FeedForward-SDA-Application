package com.feedforward.ui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.util.Duration;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class UIComponents {

    // ── Design tokens — toned down, clean ─────────────────────────────────────
    public static final String BG        = "#0c0c0c";
    public static final String BG_CARD   = "#141414";
    public static final String BG_CARD2  = "#1c1c1c";
    public static final String BG_INPUT  = "#1e1e1e";
    public static final String BG_ROW    = "#161616";

    // Green — muted lime, not neon
    public static final String GREEN     = "#8fcf4d";
    public static final String GREEN2    = "#6aad28";
    public static final String GREEN_DIM = "#8fcf4d14";
    public static final String GREEN_MID = "#8fcf4d30";
    public static final String GREEN_SOFT= "#8fcf4d88";

    public static final String WHITE     = "#ececec";
    public static final String GREY      = "#6b7280";
    public static final String GREY2     = "#2e2e2e";
    public static final String BORDER    = "#242424";
    public static final String BORDER2   = "#2c2c2c";

    public static final String RED       = "#e05252";
    public static final String RED_DIM   = "#e0525214";
    public static final String AMBER     = "#d48e22";
    public static final String AMBER_DIM = "#d48e2214";
    public static final String BLUE      = "#4f9de8";
    public static final String BLUE_DIM  = "#4f9de814";
    public static final String PURPLE    = "#9f72d4";
    public static final String PURPLE_DIM= "#9f72d414";

    // Aliases
    public static final String ACCENT    = GREEN;
    public static final String ACCENT2   = GREEN2;
    public static final String ACCENT_DIM= GREEN_DIM;
    public static final String TEXT_PRI  = WHITE;
    public static final String TEXT_SEC  = GREY;
    public static final String ORANGE    = AMBER;
    public static final String CORAL     = RED;
    public static final String TEAL      = GREEN;
    public static final String TEAL_DIM  = GREEN_DIM;
    public static final String TEAL_MID  = GREEN2;

    // ── CSS for dark tables ────────────────────────────────────────────────────
    private static final String TABLE_CSS =
        ".table-view{-fx-background-color:#141414;-fx-border-color:#242424;-fx-border-radius:10;-fx-background-radius:10;}" +
        ".table-view .column-header-background{-fx-background-color:#1c1c1c;-fx-border-color:#242424;}" +
        ".table-view .column-header,.table-view .filler{-fx-background-color:#1c1c1c;-fx-border-color:#242424;-fx-size:44px;}" +
        ".table-view .column-header .label{-fx-text-fill:#6b7280;-fx-font-weight:bold;-fx-font-size:11px;}" +
        ".table-view .table-row-cell{-fx-background-color:#141414;-fx-border-color:#1e1e1e;-fx-table-cell-border-color:#1e1e1e;}" +
        ".table-view .table-row-cell:odd{-fx-background-color:#161616;}" +
        ".table-view .table-row-cell:hover{-fx-background-color:#1a2614;}" +
        ".table-view .table-row-cell:selected{-fx-background-color:#8fcf4d1a;}" +
        ".table-view .table-cell{-fx-text-fill:#ececec;-fx-font-size:13px;-fx-padding:0 12;}" +
        ".scroll-bar .thumb{-fx-background-color:#2c2c2c;-fx-background-radius:4;}" +
        ".scroll-bar .track{-fx-background-color:#141414;}";

    public static void applyGlobalCSS(javafx.scene.Scene scene) {
        try {
            java.io.File tmp = java.io.File.createTempFile("ff_table", ".css");
            tmp.deleteOnExit();
            java.nio.file.Files.writeString(tmp.toPath(), TABLE_CSS);
            scene.getStylesheets().add(tmp.toURI().toURL().toExternalForm());
        } catch (Exception e) { System.out.println("CSS warning: " + e.getMessage()); }
    }

    // ── Nav bar ────────────────────────────────────────────────────────────────
    public static HBox makeNavBar(String roleTitle, String userName, Runnable onLogout) {
        HBox nav = new HBox(); nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPadding(new Insets(0,28,0,28)); nav.setPrefHeight(58); nav.setSpacing(14);
        nav.setStyle("-fx-background-color:"+BG_CARD+";-fx-border-color:"+BORDER+";-fx-border-width:0 0 1 0;");

        HBox logoPill = new HBox(8); logoPill.setAlignment(Pos.CENTER);
        logoPill.setPadding(new Insets(5,14,5,8));
        logoPill.setStyle("-fx-background-color:"+GREEN_DIM+";-fx-background-radius:20;-fx-border-color:"+GREEN_MID+";-fx-border-radius:20;-fx-border-width:1;");
        try {
            InputStream is = UIComponents.class.getResourceAsStream("/logo.png");
            if (is != null) {
                ImageView iv = new ImageView(new Image(is));
                iv.setFitHeight(24); iv.setPreserveRatio(true); iv.setSmooth(true);
                Text t = new Text("FeedForward"); t.setFont(Font.font("Arial", FontWeight.BOLD,14)); t.setFill(Color.web(GREEN));
                logoPill.getChildren().addAll(iv,t);
            } else throw new Exception();
        } catch (Exception e) {
            Label fb = new Label("🍱"); fb.setFont(Font.font(15));
            Text t = new Text("FeedForward"); t.setFont(Font.font("Arial",FontWeight.BOLD,14)); t.setFill(Color.web(GREEN));
            logoPill.getChildren().addAll(fb,t);
        }

        Text div = new Text("/"); div.setFont(Font.font("Arial",13)); div.setFill(Color.web(GREY2));
        Label roleLbl = new Label(roleTitle); roleLbl.setTextFill(Color.web(GREY)); roleLbl.setFont(Font.font("Arial",13));
        Region spacer = new Region(); HBox.setHgrow(spacer,Priority.ALWAYS);

        HBox chip = new HBox(8); chip.setAlignment(Pos.CENTER);
        chip.setPadding(new Insets(6,14,6,10));
        chip.setStyle("-fx-background-color:"+BG_INPUT+";-fx-background-radius:20;-fx-border-color:"+BORDER2+";-fx-border-radius:20;-fx-border-width:1;");
        Label av = new Label(userName.substring(0,1).toUpperCase());
        av.setFont(Font.font("Arial",FontWeight.BOLD,11)); av.setTextFill(Color.web(GREEN));
        av.setPadding(new Insets(2,6,2,6)); av.setStyle("-fx-background-color:"+GREEN_MID+";-fx-background-radius:10;");
        Label uname = new Label(userName); uname.setTextFill(Color.web(WHITE)); uname.setFont(Font.font("Arial",12));
        chip.getChildren().addAll(av,uname);

        Button logoutBtn = new Button("Sign out");
        String lo="-fx-background-color:transparent;-fx-text-fill:"+GREY+";-fx-border-color:"+BORDER2+";-fx-border-radius:6;-fx-background-radius:6;-fx-font-size:12;-fx-cursor:hand;-fx-padding:6 14;";
        String loh="-fx-background-color:"+RED_DIM+";-fx-text-fill:"+RED+";-fx-border-color:"+RED+";-fx-border-radius:6;-fx-background-radius:6;-fx-font-size:12;-fx-cursor:hand;-fx-padding:6 14;";
        logoutBtn.setStyle(lo); logoutBtn.setOnMouseEntered(e->logoutBtn.setStyle(loh)); logoutBtn.setOnMouseExited(e->logoutBtn.setStyle(lo));
        logoutBtn.setOnAction(e->onLogout.run());
        nav.getChildren().addAll(logoPill,div,roleLbl,spacer,chip,logoutBtn);
        return nav;
    }

    // ── Sidebar ────────────────────────────────────────────────────────────────
    public static VBox makeSidebar(String[] ignored) {
        VBox sb = new VBox(2); sb.setPrefWidth(215); sb.setPadding(new Insets(22,10,22,10));
        sb.setStyle("-fx-background-color:"+BG_CARD+";-fx-border-color:"+BORDER+";-fx-border-width:0 1 0 0;");
        Label lbl = new Label("MENU"); lbl.setTextFill(Color.web(GREY)); lbl.setFont(Font.font("Arial",FontWeight.BOLD,10)); lbl.setPadding(new Insets(0,0,12,12));
        sb.getChildren().add(lbl); return sb;
    }

    public static Button makeSidebarBtn(String text) {
        Button btn = new Button(text); btn.setPrefWidth(195); btn.setPrefHeight(42); btn.setAlignment(Pos.CENTER_LEFT);
        String base  ="-fx-background-color:transparent;-fx-text-fill:"+GREY+";-fx-font-size:13;-fx-cursor:hand;-fx-background-radius:8;-fx-padding:9 14;";
        String hover ="-fx-background-color:"+BG_INPUT+";-fx-text-fill:"+WHITE+";-fx-font-size:13;-fx-cursor:hand;-fx-background-radius:8;-fx-padding:9 14;";
        String active="-fx-background-color:"+GREEN_DIM+";-fx-text-fill:"+GREEN+";-fx-font-size:13;-fx-font-weight:bold;-fx-cursor:hand;-fx-background-radius:8;-fx-padding:9 14;-fx-border-color:transparent transparent transparent "+GREEN+";-fx-border-width:0 0 0 2;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e->{if(!btn.getStyle().contains(GREEN_DIM))btn.setStyle(hover);});
        btn.setOnMouseExited(e->{if(!btn.getStyle().contains(GREEN_DIM))btn.setStyle(base);});
        btn.setUserData(new String[]{base,hover,active}); return btn;
    }

    public static StackPane makeSidebarBtnWithBadge(String text, int count) {
        Button btn = makeSidebarBtn(text); StackPane wrap = new StackPane(btn); wrap.setAlignment(Pos.TOP_RIGHT);
        if (count>0) {
            Label badge = new Label(count>9?"9+":String.valueOf(count));
            badge.setFont(Font.font("Arial",FontWeight.BOLD,9)); badge.setTextFill(Color.WHITE);
            badge.setPadding(new Insets(1,5,1,5)); badge.setStyle("-fx-background-color:"+RED+";-fx-background-radius:8;");
            badge.setTranslateX(-10); badge.setTranslateY(4); wrap.getChildren().add(badge);
        }
        return wrap;
    }

    public static void setActiveBtn(Button b){String[] s=(String[])b.getUserData();if(s!=null)b.setStyle(s[2]);}
    public static void clearActiveBtn(Button b){String[] s=(String[])b.getUserData();if(s!=null)b.setStyle(s[0]);}

    // ── Page header ────────────────────────────────────────────────────────────
    public static VBox makePageHeader(String title, String subtitle) {
        VBox v = new VBox(6); v.setPadding(new Insets(0,0,22,0));
        Label tag = new Label("● FEEDFORWARD"); tag.setTextFill(Color.web(GREEN)); tag.setFont(Font.font("Arial",FontWeight.BOLD,10));
        Text t = new Text(title); t.setFont(Font.font("Arial",FontWeight.BOLD,24)); t.setFill(Color.web(WHITE));
        Text s = new Text(subtitle); s.setFont(Font.font("Arial",13)); s.setFill(Color.web(GREY));
        v.getChildren().addAll(tag,t,s); return v;
    }

    // ── Hero banner with illustrated food icons ────────────────────────────────
    public static HBox makeHeroBanner(String headline, String sub, String accentColor) {
        HBox banner = new HBox(); banner.setPrefHeight(130);
        banner.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:16;-fx-border-color:"+BORDER+";-fx-border-radius:16;-fx-border-width:1;");

        VBox text = new VBox(8); text.setAlignment(Pos.CENTER_LEFT); text.setPadding(new Insets(24,30,24,30));
        HBox.setHgrow(text,Priority.ALWAYS);
        Text h = new Text(headline); h.setFont(Font.font("Arial",FontWeight.BOLD,20)); h.setFill(Color.web(WHITE));
        Text s = new Text(sub); s.setFont(Font.font("Arial",13)); s.setFill(Color.web(GREY)); s.setWrappingWidth(450);
        text.getChildren().addAll(h,s);

        // Illustrated food icons using Canvas
        Canvas art = new Canvas(180,130);
        GraphicsContext gc = art.getGraphicsContext2D();
        drawFoodArt(gc, accentColor);
        banner.getChildren().addAll(text,art); return banner;
    }

    private static void drawFoodArt(GraphicsContext gc, String color) {
        // Draw decorative food-themed geometric art
        gc.setGlobalAlpha(0.12);
        gc.setFill(Color.web(color));
        gc.fillOval(100,10,90,90); gc.fillOval(140,50,60,60); gc.fillOval(20,40,70,70);
        gc.setGlobalAlpha(0.22);
        gc.fillOval(130,25,50,50);
        gc.setGlobalAlpha(1.0);

        // Bowl icon
        gc.setStroke(Color.web(color)); gc.setLineWidth(2.5);
        gc.setGlobalAlpha(0.7);
        gc.strokeOval(55,30,70,50);
        gc.strokeLine(55,55,125,55);
        // Steam lines
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.strokeLine(75,28,72,18); gc.strokeLine(90,25,90,14); gc.strokeLine(105,28,108,18);
        // Leaf
        gc.setFill(Color.web(color)); gc.setGlobalAlpha(0.6);
        gc.fillOval(130,60,18,28);
        gc.setStroke(Color.web(color)); gc.setLineWidth(1.5); gc.setGlobalAlpha(0.9);
        gc.strokeLine(139,60,139,88);
        // Spoon
        gc.strokeOval(150,25,20,25); gc.strokeLine(160,50,160,85);
        gc.setGlobalAlpha(1.0);
    }

    // ── Stat card ──────────────────────────────────────────────────────────────
    public static VBox makeStatCard(String label, String value, String color) {
        VBox card = new VBox(8); card.setPrefWidth(170); card.setPrefHeight(95);
        card.setPadding(new Insets(18)); card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color:"+BG_CARD2+";-fx-background-radius:12;-fx-border-color:"+BORDER+";-fx-border-radius:12;-fx-border-width:1;");
        Text val = new Text(value); val.setFont(Font.font("Arial",FontWeight.BOLD,30)); val.setFill(Color.web(color));
        Label lbl = new Label(label); lbl.setTextFill(Color.web(GREY)); lbl.setFont(Font.font("Arial",11)); lbl.setWrapText(true);
        Rectangle line = new Rectangle(32,2); line.setFill(Color.web(color)); line.setArcWidth(2); line.setArcHeight(2);
        card.getChildren().addAll(val,lbl,line); return card;
    }

    // ── Form card ──────────────────────────────────────────────────────────────
    public static VBox makeFormCard() {
        VBox card = new VBox(18); card.setPadding(new Insets(28)); card.setMaxWidth(590);
        card.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:14;-fx-border-color:"+BORDER+";-fx-border-radius:14;-fx-border-width:1;");
        return card;
    }

    // ── Field ──────────────────────────────────────────────────────────────────
    public static TextField makeField(String prompt) {
        TextField tf = new TextField(); tf.setPromptText(prompt); tf.setPrefWidth(460); tf.setPrefHeight(42);
        String base="-fx-background-color:"+BG_INPUT+";-fx-text-fill:"+WHITE+";-fx-border-color:"+BORDER2+";-fx-border-radius:8;-fx-background-radius:8;-fx-prompt-text-fill:#3a3a3a;-fx-padding:10 14;-fx-font-size:13;";
        tf.setStyle(base);
        tf.focusedProperty().addListener((o,ov,nv)->tf.setStyle(base+(nv?"-fx-border-color:"+GREEN+";":"")));
        return tf;
    }

    public static VBox makeFieldGroup(String label, String prompt) {
        VBox b = new VBox(7); Label l = new Label(label); l.setTextFill(Color.web(GREY)); l.setFont(Font.font("Arial",FontWeight.BOLD,12));
        b.getChildren().addAll(l,makeField(prompt)); return b;
    }

    public static VBox makeComboGroup(String label, ComboBox<String> cb) {
        VBox b = new VBox(7); Label l = new Label(label); l.setTextFill(Color.web(GREY)); l.setFont(Font.font("Arial",FontWeight.BOLD,12));
        cb.setPrefWidth(460); cb.setPrefHeight(42);
        cb.setStyle("-fx-background-color:"+BG_INPUT+";-fx-border-color:"+BORDER2+";-fx-border-radius:8;-fx-background-radius:8;");
        b.getChildren().addAll(l,cb); return b;
    }

    // ── Buttons ────────────────────────────────────────────────────────────────
    public static Button makePrimaryBtn(String text) {
        Button btn = new Button(text); btn.setPrefHeight(42);
        String base ="-fx-background-color:"+GREEN+";-fx-text-fill:#0c0c0c;-fx-font-weight:bold;-fx-font-size:13;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;";
        String hover="-fx-background-color:"+GREEN2+";-fx-text-fill:#0c0c0c;-fx-font-weight:bold;-fx-font-size:13;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;";
        btn.setStyle(base); btn.setOnMouseEntered(e->btn.setStyle(hover)); btn.setOnMouseExited(e->btn.setStyle(base));
        return btn;
    }

    public static Button makeSecondaryBtn(String text) {
        Button btn = new Button(text); btn.setPrefHeight(42);
        String base ="-fx-background-color:"+BG_INPUT+";-fx-text-fill:"+WHITE+";-fx-border-color:"+BORDER2+";-fx-border-radius:8;-fx-background-radius:8;-fx-font-size:13;-fx-cursor:hand;-fx-padding:10 18;";
        String hover="-fx-background-color:"+BG_CARD2+";-fx-text-fill:"+WHITE+";-fx-border-color:"+GREY+";-fx-border-radius:8;-fx-background-radius:8;-fx-font-size:13;-fx-cursor:hand;-fx-padding:10 18;";
        btn.setStyle(base); btn.setOnMouseEntered(e->btn.setStyle(hover)); btn.setOnMouseExited(e->btn.setStyle(base));
        return btn;
    }

    public static Button makeDangerBtn(String t)  { return makeColorBtn(t,RED,RED_DIM);     }
    public static Button makeSuccessBtn(String t)  { return makeColorBtn(t,GREEN,GREEN_DIM); }
    public static Button makeWarningBtn(String t)  { return makeColorBtn(t,AMBER,AMBER_DIM); }
    public static Button makePurpleBtn(String t)   { return makeColorBtn(t,PURPLE,PURPLE_DIM);}

    private static Button makeColorBtn(String text, String color, String dim) {
        Button btn = new Button(text); btn.setPrefHeight(32);
        String base ="-fx-background-color:"+dim+";-fx-text-fill:"+color+";-fx-border-color:"+color+"44;-fx-border-radius:6;-fx-background-radius:6;-fx-font-size:11;-fx-cursor:hand;-fx-padding:5 12;";
        String hover="-fx-background-color:"+color+"28;-fx-text-fill:"+color+";-fx-border-color:"+color+";-fx-border-radius:6;-fx-background-radius:6;-fx-font-size:11;-fx-cursor:hand;-fx-padding:5 12;";
        btn.setStyle(base); btn.setOnMouseEntered(e->btn.setStyle(hover)); btn.setOnMouseExited(e->btn.setStyle(base));
        return btn;
    }

    // ── Status badge ───────────────────────────────────────────────────────────
    public static Label makeStatusBadge(String text) {
        Label l = new Label(" "+text+" "); l.setFont(Font.font("Arial",FontWeight.BOLD,10)); l.setPadding(new Insets(3,8,3,8));
        String c,bg;
        switch(text.toLowerCase()){
            case "available":case "active":case "completed":case "collected":c=GREEN;bg=GREEN_DIM;break;
            case "scheduled":c=AMBER;bg=AMBER_DIM;break;
            case "confirmed":c=BLUE;bg=BLUE_DIM;break;
            case "cancelled":case "flagged":case "suspended":c=RED;bg=RED_DIM;break;
            case "pickup scheduled":c=PURPLE;bg=PURPLE_DIM;break;
            default:c=GREY;bg=BG_INPUT;
        }
        l.setTextFill(Color.web(c)); l.setStyle("-fx-background-color:"+bg+";-fx-background-radius:4;-fx-border-color:"+c+"44;-fx-border-radius:4;-fx-border-width:1;");
        return l;
    }

    // ── Table ──────────────────────────────────────────────────────────────────
    public static <T> void styleTable(TableView<T> table) {
        table.setStyle("-fx-background-color:"+BG_CARD+";-fx-border-color:"+BORDER+";-fx-border-radius:10;-fx-background-radius:10;-fx-table-cell-border-color:"+BORDER+";");
        table.setFixedCellSize(46);
    }

    public static <S,T> TableColumn<S,T> makeCol(String title, String property, double width) {
        TableColumn<S,T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property)); col.setPrefWidth(width); col.setStyle("-fx-alignment:CENTER-LEFT;");
        return col;
    }

    // ── Expiry countdown pill ──────────────────────────────────────────────────
    public static Label makeExpiryPill(String expiryStr) {
        Label l = new Label();
        try {
            LocalDateTime expiry;
            try { expiry = LocalDateTime.parse(expiryStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); }
            catch(Exception e2) { expiry = LocalDateTime.parse(expiryStr.replace("T"," ").substring(0,19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); }
            long hours = LocalDateTime.now().until(expiry,ChronoUnit.HOURS);
            if(hours<0){l.setText("Expired");l.setTextFill(Color.web(RED));l.setStyle("-fx-background-color:"+RED_DIM+";-fx-background-radius:4;-fx-padding:2 8;");}
            else if(hours<6){l.setText("Expires in "+hours+"h");l.setTextFill(Color.web(RED));l.setStyle("-fx-background-color:"+RED_DIM+";-fx-background-radius:4;-fx-padding:2 8;");}
            else if(hours<24){l.setText("Expires in "+hours+"h");l.setTextFill(Color.web(AMBER));l.setStyle("-fx-background-color:"+AMBER_DIM+";-fx-background-radius:4;-fx-padding:2 8;");}
            else{long days=hours/24;l.setText("Expires in "+days+"d");l.setTextFill(Color.web(GREEN));l.setStyle("-fx-background-color:"+GREEN_DIM+";-fx-background-radius:4;-fx-padding:2 8;");}
        } catch(Exception e){l.setText("—");l.setTextFill(Color.web(GREY));}
        l.setFont(Font.font("Arial",FontWeight.BOLD,10)); return l;
    }

    // ── Food listing card (visual, not table row) ──────────────────────────────
    public static HBox makeFoodCard(com.feedforward.model.FoodListing fl, String btnLabel, Runnable onAction) {
        HBox card = new HBox(16); card.setPadding(new Insets(16,20,16,20)); card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:12;-fx-border-color:"+BORDER+";-fx-border-radius:12;-fx-border-width:1;");
        card.setOnMouseEntered(e->card.setStyle("-fx-background-color:"+BG_CARD2+";-fx-background-radius:12;-fx-border-color:"+BORDER2+";-fx-border-radius:12;-fx-border-width:1;"));
        card.setOnMouseExited(e->card.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:12;-fx-border-color:"+BORDER+";-fx-border-radius:12;-fx-border-width:1;"));

        // Food icon area
        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(52,52); iconBox.setMinSize(52,52);
        iconBox.setStyle("-fx-background-color:"+GREEN_DIM+";-fx-background-radius:12;");
        Label icon = new Label("🍱"); icon.setFont(Font.font(22));
        // Color based on food name keyword
        String fn = fl.getFoodName() != null ? fl.getFoodName().toLowerCase() : "";
        if(fn.contains("rice")||fn.contains("biryani")) icon.setText("🍚");
        else if(fn.contains("bread")||fn.contains("roti")) icon.setText("🍞");
        else if(fn.contains("fruit")) icon.setText("🍎");
        else if(fn.contains("veg")||fn.contains("sabzi")) icon.setText("🥦");
        else if(fn.contains("meat")||fn.contains("chicken")) icon.setText("🍗");
        else if(fn.contains("milk")||fn.contains("dairy")) icon.setText("🥛");
        iconBox.getChildren().add(icon);

        VBox info = new VBox(5); HBox.setHgrow(info,Priority.ALWAYS);
        Label name = new Label(fl.getFoodName()); name.setFont(Font.font("Arial",FontWeight.BOLD,15)); name.setTextFill(Color.web(WHITE));
        HBox meta = new HBox(14);
        Label qty = new Label("📦 "+fl.getQuantity()+" units"); qty.setFont(Font.font("Arial",12)); qty.setTextFill(Color.web(GREY));
        Label loc = new Label("📍 "+fl.getLocation()); loc.setFont(Font.font("Arial",12)); loc.setTextFill(Color.web(GREY));
        Label donor = new Label("👤 "+(fl.getDonorName()!=null?fl.getDonorName():"N/A")); donor.setFont(Font.font("Arial",12)); donor.setTextFill(Color.web(GREY));
        meta.getChildren().addAll(qty,loc,donor);
        info.getChildren().addAll(name,meta);

        VBox right = new VBox(8); right.setAlignment(Pos.CENTER_RIGHT);
        Label expiryPill = makeExpiryPill(fl.getExpiryTime()!=null?fl.getExpiryTime().toString():"");
        Label statusBadge = makeStatusBadge(fl.getStatus()!=null?fl.getStatus():"Available");
        right.getChildren().addAll(expiryPill,statusBadge);
        if (btnLabel != null && onAction != null) {
            Button btn = makeSuccessBtn(btnLabel); btn.setOnAction(e->onAction.run()); right.getChildren().add(btn);
        }
        card.getChildren().addAll(iconBox,info,right); return card;
    }

    // ── Pickup action card (for confirm/cancel) ────────────────────────────────
    public static VBox makePickupActionCard(com.feedforward.model.PickupSchedule p,
                                             Runnable onConfirm, Runnable onCancel) {
        VBox card = new VBox(12); card.setPadding(new Insets(18,20,18,20));
        card.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:14;-fx-border-color:"+BORDER+";-fx-border-radius:14;-fx-border-width:1;");

        HBox top = new HBox(12); top.setAlignment(Pos.CENTER_LEFT);
        Label idBadge = new Label("#"+p.getPickupID()); idBadge.setFont(Font.font("Arial",FontWeight.BOLD,11));
        idBadge.setTextFill(Color.web(GREY)); idBadge.setPadding(new Insets(2,8,2,8));
        idBadge.setStyle("-fx-background-color:"+BG_INPUT+";-fx-background-radius:4;");
        Label foodName = new Label(p.getFoodName()!=null?p.getFoodName():"Pickup #"+p.getPickupID());
        foodName.setFont(Font.font("Arial",FontWeight.BOLD,16)); foodName.setTextFill(Color.web(WHITE));
        Region sp = new Region(); HBox.setHgrow(sp,Priority.ALWAYS);
        Label statusBadge = makeStatusBadge(p.getStatus()!=null?p.getStatus():"Scheduled");
        top.getChildren().addAll(idBadge,foodName,sp,statusBadge);

        HBox details = new HBox(20);
        String dateStr  = p.getPickupDate()  != null ? p.getPickupDate().toString()  : "";
        String timeStr  = p.getPickupTime()  != null ? p.getPickupTime().toString()  : "";
        String transStr = p.getTransportMethod() != null ? p.getTransportMethod() : "N/A";
        for(String[] d : new String[][]{{"📅",dateStr},{"🕐",timeStr},{"🚗",transStr}}) {
            if(!d[1].isEmpty()) {
                HBox item = new HBox(5); item.setAlignment(Pos.CENTER_LEFT);
                Label ico = new Label(d[0]); ico.setFont(Font.font(12));
                Label val = new Label(d[1]); val.setTextFill(Color.web(GREY)); val.setFont(Font.font("Arial",12));
                item.getChildren().addAll(ico,val); details.getChildren().add(item);
            }
        }

        HBox actions = new HBox(10); actions.setAlignment(Pos.CENTER_LEFT);
        if(onConfirm!=null){ Button conf=makeSuccessBtn("✓  Confirm Pickup"); conf.setPrefHeight(36); conf.setOnAction(e->onConfirm.run()); actions.getChildren().add(conf); }
        if(onCancel!=null) { Button canc=makeDangerBtn("✕  Cancel");          canc.setPrefHeight(36); canc.setOnAction(e->onCancel.run());  actions.getChildren().add(canc); }

        card.getChildren().addAll(top,details,actions); return card;
    }

    private static String getStatusColor(String status) {
        if(status==null) return AMBER;
        switch(status.toLowerCase()) {
            case "confirmed": return BLUE; case "completed": case "collected": return GREEN;
            case "cancelled": return RED; default: return AMBER;
        }
    }

    // ── NGO card with animated score ring ─────────────────────────────────────
    public static HBox makeNGOCard(int rank, String name, String region, String contact, double score, String color) {
        HBox card = new HBox(18); card.setPadding(new Insets(16,20,16,20)); card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:12;-fx-border-color:"+BORDER+";-fx-border-radius:12;-fx-border-width:1;");
        card.setOnMouseEntered(e->card.setStyle("-fx-background-color:"+BG_CARD2+";-fx-background-radius:12;-fx-border-color:"+color+"33;-fx-border-radius:12;-fx-border-width:1;"));
        card.setOnMouseExited(e->card.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:12;-fx-border-color:"+BORDER+";-fx-border-radius:12;-fx-border-width:1;"));

        Label rankLbl = new Label("#"+rank); rankLbl.setFont(Font.font("Arial",FontWeight.BOLD,13)); rankLbl.setTextFill(Color.web(color)); rankLbl.setPrefWidth(28);

        Canvas ring = makeScoreRing(score,color);

        VBox info = new VBox(5); HBox.setHgrow(info,Priority.ALWAYS);
        Label nameLbl = new Label(name); nameLbl.setFont(Font.font("Arial",FontWeight.BOLD,15)); nameLbl.setTextFill(Color.web(WHITE));
        Label regionLbl = new Label("📍 "+region); regionLbl.setFont(Font.font("Arial",12)); regionLbl.setTextFill(Color.web(GREY));
        Label contactLbl = new Label("📞 "+contact); contactLbl.setFont(Font.font("Arial",12)); contactLbl.setTextFill(Color.web(GREY));
        info.getChildren().addAll(nameLbl,regionLbl,contactLbl);

        card.getChildren().addAll(rankLbl,ring,info);
        card.setOpacity(0); card.setTranslateY(8);
        FadeTransition ft = new FadeTransition(Duration.millis(380),card); ft.setToValue(1); ft.setDelay(Duration.millis(rank*110));
        TranslateTransition tt = new TranslateTransition(Duration.millis(380),card); tt.setToY(0); tt.setDelay(Duration.millis(rank*110));
        ft.play(); tt.play();
        return card;
    }

    private static Canvas makeScoreRing(double score, String color) {
        Canvas c = new Canvas(64,64); GraphicsContext gc = c.getGraphicsContext2D();
        final double[] cur = {0};
        Timeline anim = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            cur[0] = Math.min(cur[0]+score/35.0, score);
            gc.clearRect(0,0,64,64);
            gc.setStroke(Color.web(BORDER2)); gc.setLineWidth(4); gc.strokeOval(8,8,48,48);
            gc.setStroke(Color.web(color)); gc.setLineWidth(4);
            gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
            gc.strokeArc(8,8,48,48,90,-(cur[0]/100.0)*360,javafx.scene.shape.ArcType.OPEN);
            gc.setFill(Color.web(WHITE)); gc.setFont(javafx.scene.text.Font.font("Arial",FontWeight.BOLD,12));
            gc.fillText((int)cur[0]+"%",cur[0]>=100?10:15,38);
        }));
        anim.setCycleCount(35); anim.play(); return c;
    }

    // ── Kanban board ───────────────────────────────────────────────────────────
    public static VBox makeKanbanBoard(List<com.feedforward.model.PickupSchedule> pickups) {
        // Use a VBox wrapper with a TilePane inside so columns fill width and wrap cleanly
        VBox wrapper = new VBox(0);

        // Top-level grid: 2 columns × 2 rows if narrow, else 4 columns
        TilePane tile = new TilePane(); tile.setHgap(12); tile.setVgap(12);
        tile.setPrefColumns(4); tile.setTileAlignment(Pos.TOP_LEFT);
        String[][] cols = {{"Scheduled",AMBER},{"Confirmed",BLUE},{"Completed",GREEN},{"Cancelled",RED}};
        for(String[] col : cols) {
            String status=col[0], color=col[1];
            VBox column = new VBox(10); column.setPrefWidth(240); column.setMinWidth(240); column.setPadding(new Insets(16));
            column.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:12;-fx-border-color:"+BORDER+";-fx-border-radius:12;");

            HBox header = new HBox(8); header.setAlignment(Pos.CENTER_LEFT);
            Circle dot = new Circle(4); dot.setFill(Color.web(color));
            Label title = new Label(status.toUpperCase()); title.setFont(Font.font("Arial",FontWeight.BOLD,11)); title.setTextFill(Color.web(color));
            long count = pickups.stream().filter(p->status.equalsIgnoreCase(p.getStatus())).count();
            Label countLbl = new Label(String.valueOf(count)); countLbl.setFont(Font.font("Arial",FontWeight.BOLD,10)); countLbl.setTextFill(Color.web(color));
            countLbl.setPadding(new Insets(1,7,1,7)); countLbl.setStyle("-fx-background-color:"+color+"1a;-fx-background-radius:8;-fx-border-color:"+color+"33;-fx-border-radius:8;");
            Region sp = new Region(); HBox.setHgrow(sp,Priority.ALWAYS);
            header.getChildren().addAll(dot,title,sp,countLbl);

            Separator sep = new Separator(); sep.setStyle("-fx-background-color:"+BORDER+";");
            column.getChildren().addAll(header,sep);

            List<com.feedforward.model.PickupSchedule> filtered = new ArrayList<>();
            for(com.feedforward.model.PickupSchedule p:pickups) if(status.equalsIgnoreCase(p.getStatus())) filtered.add(p);

            if(filtered.isEmpty()) {
                Label empty = new Label("No pickups"); empty.setTextFill(Color.web(GREY)); empty.setFont(Font.font("Arial",11)); empty.setPadding(new Insets(12));
                column.getChildren().add(empty);
            } else {
                for(int i=0;i<filtered.size();i++) {
                    com.feedforward.model.PickupSchedule p = filtered.get(i);
                    VBox pCard = new VBox(6); pCard.setPadding(new Insets(12));
                    pCard.setStyle("-fx-background-color:"+BG_CARD2+";-fx-background-radius:8;-fx-border-color:"+BORDER2+";-fx-border-radius:8;");
                    pCard.setOnMouseEntered(e->pCard.setStyle("-fx-background-color:"+BG_INPUT+";-fx-background-radius:8;-fx-border-color:"+color+"44;-fx-border-radius:8;"));
                    pCard.setOnMouseExited(e->pCard.setStyle("-fx-background-color:"+BG_CARD2+";-fx-background-radius:8;-fx-border-color:"+BORDER2+";-fx-border-radius:8;"));

                    // Colored left accent strip
                    HBox strip = new HBox(); strip.setPrefHeight(3); strip.setPrefWidth(200);
                    strip.setStyle("-fx-background-color:"+color+";-fx-background-radius:2;"); strip.setOpacity(0.6);

                    Label foodLbl = new Label(p.getFoodName()!=null?p.getFoodName():"Pickup #"+p.getPickupID());
                    foodLbl.setFont(Font.font("Arial",FontWeight.BOLD,12)); foodLbl.setTextFill(Color.web(WHITE)); foodLbl.setWrapText(true);
                    Label dateLbl = new Label("📅 "+(p.getPickupDate()!=null?p.getPickupDate().toString():"")+" "+(p.getPickupTime()!=null?p.getPickupTime().toString():""));
                    dateLbl.setFont(Font.font("Arial",10)); dateLbl.setTextFill(Color.web(GREY));
                    Label transLbl = new Label("🚗 "+(p.getTransportMethod()!=null?p.getTransportMethod():"N/A"));
                    transLbl.setFont(Font.font("Arial",10)); transLbl.setTextFill(Color.web(GREY));
                    pCard.getChildren().addAll(strip,foodLbl,dateLbl,transLbl);

                    pCard.setOpacity(0); pCard.setTranslateY(6);
                    FadeTransition ft = new FadeTransition(Duration.millis(300),pCard); ft.setToValue(1); ft.setDelay(Duration.millis(i*70));
                    TranslateTransition tt = new TranslateTransition(Duration.millis(300),pCard); tt.setToY(0); tt.setDelay(Duration.millis(i*70));
                    ft.play(); tt.play();
                    column.getChildren().add(pCard);
                }
            }
            tile.getChildren().add(column);
        }
        wrapper.getChildren().add(tile);
        return wrapper;
    }

    // ── Line graph (pure Canvas) ───────────────────────────────────────────────
    public static VBox makeLineGraph(String title, List<String[]> dataPoints, String color) {
        VBox chart = new VBox(14); chart.setPadding(new Insets(22));
        chart.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:14;-fx-border-color:"+BORDER+";-fx-border-radius:14;");
        Text t = new Text(title); t.setFont(Font.font("Arial",FontWeight.BOLD,15)); t.setFill(Color.web(WHITE));
        chart.getChildren().add(t);
        if(dataPoints.isEmpty()) { chart.getChildren().add(makeEmptyState("📈","No data yet")); return chart; }

        int W=560, H=180, padL=50, padB=30, padT=20, padR=20;
        Canvas canvas = new Canvas(W,H+padB+padT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double maxVal = dataPoints.stream().mapToDouble(d->Double.parseDouble(d[1])).max().orElse(1);
        double minVal = 0;
        int n = dataPoints.size();
        double xStep = (W-padL-padR)/(double)Math.max(n-1,1);

        // Grid lines
        gc.setStroke(Color.web(BORDER)); gc.setLineWidth(0.5);
        for(int i=0;i<=4;i++) {
            double y = padT + (H*(4-i)/4.0);
            gc.strokeLine(padL,y,W-padR,y);
            gc.setFill(Color.web(GREY)); gc.setFont(javafx.scene.text.Font.font("Arial",10));
            gc.fillText(String.valueOf((int)(maxVal*i/4)),2,y+4);
        }

        // Build points
        double[] xs = new double[n], ys = new double[n];
        for(int i=0;i<n;i++) {
            xs[i] = padL + i*xStep;
            ys[i] = padT + H - H*(Double.parseDouble(dataPoints.get(i)[1])-minVal)/(maxVal-minVal);
        }

        // Fill area under line
        gc.beginPath(); gc.moveTo(xs[0],padT+H);
        for(int i=0;i<n;i++) gc.lineTo(xs[i],ys[i]);
        gc.lineTo(xs[n-1],padT+H); gc.closePath();
        gc.setFill(Color.web(color+"18")); gc.fill();

        // Animated line draw
        final int[] step = {1};
        Timeline anim = new Timeline(new KeyFrame(Duration.millis(30), e -> {
            if(step[0]>=n) return;
            step[0]++;
            // Redraw line up to current step
            gc.clearRect(padL-1,0,W,H+padB+padT);
            // Regrid
            gc.setStroke(Color.web(BORDER)); gc.setLineWidth(0.5);
            for(int i=0;i<=4;i++) {
                double y=padT+(H*(4-i)/4.0); gc.strokeLine(padL,y,W-padR,y);
                gc.setFill(Color.web(GREY)); gc.setFont(javafx.scene.text.Font.font("Arial",10));
                gc.fillText(String.valueOf((int)(maxVal*i/4)),2,y+4);
            }
            // Fill
            gc.beginPath(); gc.moveTo(xs[0],padT+H);
            for(int i=0;i<step[0];i++) gc.lineTo(xs[i],ys[i]);
            gc.lineTo(xs[step[0]-1],padT+H); gc.closePath();
            gc.setFill(Color.web(color+"18")); gc.fill();
            // Line
            gc.setStroke(Color.web(color)); gc.setLineWidth(2.5);
            gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
            gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
            gc.beginPath(); gc.moveTo(xs[0],ys[0]);
            for(int i=1;i<step[0];i++) gc.lineTo(xs[i],ys[i]);
            gc.stroke();
            // Dots
            gc.setFill(Color.web(color));
            for(int i=0;i<step[0];i++) gc.fillOval(xs[i]-3,ys[i]-3,6,6);
            // X labels
            gc.setFill(Color.web(GREY)); gc.setFont(javafx.scene.text.Font.font("Arial",9));
            for(int i=0;i<n;i++) gc.fillText(dataPoints.get(i)[0],xs[i]-15,padT+H+15);
        }));
        anim.setCycleCount(n); anim.play();

        // Draw initial state
        gc.setStroke(Color.web(BORDER)); gc.setLineWidth(0.5);
        for(int i=0;i<=4;i++) {
            double y=padT+(H*(4-i)/4.0); gc.strokeLine(padL,y,W-padR,y);
            gc.setFill(Color.web(GREY)); gc.setFont(javafx.scene.text.Font.font("Arial",10));
            gc.fillText(String.valueOf((int)(maxVal*i/4)),2,y+4);
        }
        gc.setFill(Color.web(GREY)); gc.setFont(javafx.scene.text.Font.font("Arial",9));
        for(int i=0;i<n;i++) gc.fillText(dataPoints.get(i)[0],xs[i]-15,padT+H+15);

        chart.getChildren().add(canvas); return chart;
    }

    // ── Bar chart ──────────────────────────────────────────────────────────────
    public static VBox makeBarChart(String title, List<String[]> data, String color) {
        VBox chart = new VBox(14); chart.setPadding(new Insets(22));
        chart.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:14;-fx-border-color:"+BORDER+";-fx-border-radius:14;");
        Text t = new Text(title); t.setFont(Font.font("Arial",FontWeight.BOLD,15)); t.setFill(Color.web(WHITE));
        chart.getChildren().add(t);
        if(data.isEmpty()){chart.getChildren().add(makeEmptyState("📊","No data yet"));return chart;}
        double max = data.stream().mapToDouble(d->Double.parseDouble(d[1])).max().orElse(1);
        for(String[] row:data) {
            double pct = Double.parseDouble(row[1])/max;
            HBox barRow = new HBox(12); barRow.setAlignment(Pos.CENTER_LEFT);
            Label lbl = new Label(row[0]); lbl.setTextFill(Color.web(GREY)); lbl.setFont(Font.font("Arial",12)); lbl.setPrefWidth(140); lbl.setWrapText(true);
            StackPane track = new StackPane(); track.setAlignment(Pos.CENTER_LEFT); track.setPrefWidth(300);
            Rectangle bgR = new Rectangle(300,18); bgR.setFill(Color.web(BG_INPUT)); bgR.setArcWidth(6); bgR.setArcHeight(6);
            Rectangle fillR = new Rectangle(0,18); fillR.setFill(Color.web(color)); fillR.setArcWidth(6); fillR.setArcHeight(6);
            track.getChildren().addAll(bgR,fillR);
            Timeline anim = new Timeline(new KeyFrame(Duration.ZERO,new KeyValue(fillR.widthProperty(),0)),
                    new KeyFrame(Duration.millis(600),new KeyValue(fillR.widthProperty(),pct*300,Interpolator.EASE_OUT)));
            anim.setDelay(Duration.millis(80)); anim.play();
            Label valLbl = new Label(row[1]); valLbl.setTextFill(Color.web(color)); valLbl.setFont(Font.font("Arial",FontWeight.BOLD,12));
            barRow.getChildren().addAll(lbl,track,valLbl); chart.getChildren().add(barRow);
        }
        return chart;
    }

    // ── Donut chart ────────────────────────────────────────────────────────────
    public static VBox makeDonutChart(String title, Map<String,Integer> data) {
        VBox outer = new VBox(14); outer.setPadding(new Insets(22));
        outer.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:14;-fx-border-color:"+BORDER+";-fx-border-radius:14;");
        Text t = new Text(title); t.setFont(Font.font("Arial",FontWeight.BOLD,15)); t.setFill(Color.web(WHITE));
        String[] colors={GREEN,PURPLE,AMBER,RED,BLUE};
        Canvas canvas = new Canvas(130,130); GraphicsContext gc = canvas.getGraphicsContext2D();
        double total = data.values().stream().mapToInt(Integer::intValue).sum();
        double startAngle=90; int ci=0;
        for(Map.Entry<String,Integer> entry:data.entrySet()) {
            double sweep=(entry.getValue()/total)*360.0;
            gc.setFill(Color.web(colors[ci%colors.length]));
            gc.beginPath(); gc.moveTo(65,65); gc.arc(65,65,55,55,startAngle,sweep); gc.closePath(); gc.fill();
            startAngle+=sweep; ci++;
        }
        gc.setFill(Color.web(BG_CARD)); gc.fillOval(32,32,66,66);
        gc.setFill(Color.web(WHITE)); gc.setFont(javafx.scene.text.Font.font("Arial",FontWeight.BOLD,15));
        gc.fillText(String.valueOf((int)total),50,72);
        VBox legend = new VBox(8); ci=0;
        for(Map.Entry<String,Integer> entry:data.entrySet()) {
            String c=colors[ci%colors.length]; HBox row = new HBox(8); row.setAlignment(Pos.CENTER_LEFT);
            Rectangle dot = new Rectangle(10,10); dot.setFill(Color.web(c)); dot.setArcWidth(3); dot.setArcHeight(3);
            Label lbl = new Label(entry.getKey()+" — "+entry.getValue()); lbl.setTextFill(Color.web(GREY)); lbl.setFont(Font.font("Arial",12));
            row.getChildren().addAll(dot,lbl); legend.getChildren().add(row); ci++;
        }
        HBox content = new HBox(24); content.setAlignment(Pos.CENTER_LEFT); content.getChildren().addAll(canvas,legend);
        outer.getChildren().addAll(t,content); return outer;
    }

    // ── Map view ───────────────────────────────────────────────────────────────
    public static Node makeMapView(String locationName) {
        VBox container = new VBox(0); container.setPrefHeight(200); container.setMaxWidth(580);
        container.setStyle("-fx-background-color:"+BG_INPUT+";-fx-background-radius:12;-fx-border-color:"+BORDER+";-fx-border-radius:12;");
        Canvas mapCanvas = new Canvas(580,190); GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.setFill(Color.web(BG_CARD2)); gc.fillRoundRect(0,0,580,190,12,12);
        gc.setStroke(Color.web(BORDER2)); gc.setLineWidth(0.5);
        for(int x=0;x<580;x+=30) gc.strokeLine(x,0,x,190);
        for(int y=0;y<190;y+=30) gc.strokeLine(0,y,580,y);
        gc.setStroke(Color.web(BORDER)); gc.setLineWidth(2.5);
        gc.strokeLine(0,95,580,95); gc.strokeLine(290,0,290,190);
        gc.setLineWidth(2); gc.strokeLine(0,55,580,55); gc.strokeLine(0,135,580,135);
        gc.strokeLine(145,0,145,190); gc.strokeLine(435,0,435,190);
        // Pin
        double cx=290,cy=95;
        gc.setFill(Color.web(GREEN+"44")); gc.fillOval(cx-22,cy-22,44,44);
        gc.setFill(Color.web(GREEN)); gc.fillOval(cx-9,cy-9,18,18);
        gc.setFill(Color.web(WHITE)); gc.fillOval(cx-4,cy-4,8,8);
        gc.setStroke(Color.web(GREEN)); gc.setLineWidth(2); gc.strokeLine(cx,cy+9,cx,cy+20);
        // Label pill
        gc.setFill(Color.web(BG_CARD+"dd")); gc.fillRoundRect(cx-80,cy-46,160,22,8,8);
        gc.setFill(Color.web(GREEN)); gc.setFont(javafx.scene.text.Font.font("Arial",FontWeight.BOLD,11));
        gc.fillText("📍 "+locationName,cx-74,cy-30);
        container.getChildren().add(mapCanvas); return container;
    }

    // ── Animated success overlay ───────────────────────────────────────────────
    public static void showSuccessOverlay(Pane root, String message, Runnable afterDone) {
        StackPane overlay = new StackPane(); overlay.setPrefSize(root.getWidth(),root.getHeight());
        overlay.setStyle("-fx-background-color:#0c0c0cdd;"); overlay.setOpacity(0);
        VBox content = new VBox(18); content.setAlignment(Pos.CENTER);
        Canvas check = new Canvas(100,100); GraphicsContext gc = check.getGraphicsContext2D();
        final double[] progress={0};
        Timeline drawCheck = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            progress[0]=Math.min(progress[0]+0.04,1.0);
            gc.clearRect(0,0,100,100);
            gc.setStroke(Color.web(GREEN)); gc.setLineWidth(3.5);
            gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
            gc.strokeArc(8,8,84,84,90,-(progress[0]*360),javafx.scene.shape.ArcType.OPEN);
            if(progress[0]>0.5) {
                double p2=(progress[0]-0.5)*2;
                double x1=28,y1=52,xm=44,ym=68,x2=72,y2=32;
                double midX=x1+(xm-x1)*Math.min(p2*2,1), midY=y1+(ym-y1)*Math.min(p2*2,1);
                gc.strokeLine(x1,y1,midX,midY);
                if(p2>0.5){double endX=xm+(x2-xm)*((p2-0.5)*2),endY=ym+(y2-ym)*((p2-0.5)*2); gc.strokeLine(xm,ym,endX,endY);}
            }
        }));
        drawCheck.setCycleCount(25);
        Label msgLbl = new Label(message); msgLbl.setFont(Font.font("Arial",FontWeight.BOLD,17)); msgLbl.setTextFill(Color.web(WHITE)); msgLbl.setWrapText(true); msgLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        Label subLbl = new Label("Click anywhere to continue"); subLbl.setFont(Font.font("Arial",12)); subLbl.setTextFill(Color.web(GREY));
        content.getChildren().addAll(check,msgLbl,subLbl);
        overlay.getChildren().add(content); root.getChildren().add(overlay);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200),overlay); fadeIn.setToValue(1);
        fadeIn.setOnFinished(e->drawCheck.play());
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e->{ FadeTransition fo=new FadeTransition(Duration.millis(250),overlay); fo.setToValue(0);
            fo.setOnFinished(ev->{root.getChildren().remove(overlay);if(afterDone!=null)afterDone.run();}); fo.play();});
        drawCheck.setOnFinished(e->pause.play());
        overlay.setOnMouseClicked(e->{pause.stop();root.getChildren().remove(overlay);if(afterDone!=null)afterDone.run();});
        fadeIn.play();
    }

    // ── Feedback / Notif card / Info card / Empty state ───────────────────────
    public static Label makeFeedback(){Label l=new Label("");l.setFont(Font.font("Arial",12));l.setWrapText(true);return l;}
    public static void setSuccess(Label l,String msg){l.setTextFill(Color.web(GREEN));l.setText("✓  "+msg);}
    public static void setError(Label l,String msg){l.setTextFill(Color.web(RED));l.setText("✕  "+msg);}

    public static HBox makeNotifCard(String message, String time) {
        HBox card=new HBox(14);card.setPadding(new Insets(14,16,14,16));
        card.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:10;-fx-border-color:"+BORDER+";-fx-border-radius:10;-fx-border-width:1;");
        VBox dotBox=new VBox();dotBox.setAlignment(Pos.TOP_CENTER);dotBox.setPadding(new Insets(4,0,0,0));
        Circle dot=new Circle(4);dot.setFill(Color.web(GREEN));dotBox.getChildren().add(dot);
        VBox textBox=new VBox(5);Label msg=new Label(message);msg.setTextFill(Color.web(WHITE));msg.setFont(Font.font("Arial",13));msg.setWrapText(true);
        Label ts=new Label(time);ts.setTextFill(Color.web(GREY));ts.setFont(Font.font("Arial",10));
        textBox.getChildren().addAll(msg,ts);HBox.setHgrow(textBox,Priority.ALWAYS);
        card.getChildren().addAll(dotBox,textBox);return card;
    }

    public static HBox makeInfoCard(String content) {
        HBox card=new HBox(12);card.setPadding(new Insets(14,18,14,18));card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color:"+GREEN_DIM+";-fx-background-radius:10;-fx-border-color:"+GREEN_MID+";-fx-border-radius:10;-fx-border-width:1;");
        Label icon=new Label("ℹ");icon.setTextFill(Color.web(GREEN));icon.setFont(Font.font("Arial",FontWeight.BOLD,15));
        Label lbl=new Label(content);lbl.setTextFill(Color.web(WHITE));lbl.setFont(Font.font("Arial",13));lbl.setWrapText(true);HBox.setHgrow(lbl,Priority.ALWAYS);
        card.getChildren().addAll(icon,lbl);return card;
    }

    public static VBox makeEmptyState(String icon, String message) {
        VBox box=new VBox(14);box.setAlignment(Pos.CENTER);box.setPadding(new Insets(60));
        Label ic=new Label(icon);ic.setFont(Font.font(36));Label msg=new Label(message);msg.setTextFill(Color.web(GREY));msg.setFont(Font.font("Arial",14));
        box.getChildren().addAll(ic,msg);return box;
    }

    // ── Score bar (fallback) ───────────────────────────────────────────────────
    public static VBox makeScoreBar(String label, double score, String color) {
        VBox box=new VBox(7);box.setPadding(new Insets(14,16,14,16));
        box.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:10;-fx-border-color:"+BORDER+";-fx-border-radius:10;");
        HBox row=new HBox();row.setAlignment(Pos.CENTER_LEFT);
        Label lbl=new Label(label);lbl.setTextFill(Color.web(WHITE));lbl.setFont(Font.font("Arial",FontWeight.BOLD,13));
        Region sp=new Region();HBox.setHgrow(sp,Priority.ALWAYS);
        Label pct=new Label((int)score+"%");pct.setTextFill(Color.web(color));pct.setFont(Font.font("Arial",FontWeight.BOLD,13));
        row.getChildren().addAll(lbl,sp,pct);
        StackPane track=new StackPane();track.setAlignment(Pos.CENTER_LEFT);
        Rectangle bg=new Rectangle(500,8);bg.setFill(Color.web(BORDER2));bg.setArcWidth(8);bg.setArcHeight(8);
        Rectangle fill=new Rectangle(0,8);fill.setFill(Color.web(color));fill.setArcWidth(8);fill.setArcHeight(8);
        track.getChildren().addAll(bg,fill);
        Timeline anim=new Timeline(new KeyFrame(Duration.ZERO,new KeyValue(fill.widthProperty(),0)),
                new KeyFrame(Duration.millis(700),new KeyValue(fill.widthProperty(),score/100.0*500,Interpolator.EASE_OUT)));
        anim.play();box.getChildren().addAll(row,track);return box;
    }
}
