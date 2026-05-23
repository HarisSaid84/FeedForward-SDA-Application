package com.feedforward.ui;

import com.feedforward.business.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import static com.feedforward.ui.UIComponents.*;

public class RegisterScreen {
    private Stage stage;
    private UserService userService;
    public RegisterScreen(Stage stage) { this.stage=stage; this.userService=new UserService(); }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:"+BG+";");

        // Top bar
        HBox topBar = new HBox(12); topBar.setPadding(new Insets(16,28,16,28)); topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color:"+BG_CARD+";-fx-border-color:"+BORDER+";-fx-border-width:0 0 1 0;");
        Label logo = new Label("🍱"); logo.setFont(Font.font(18));
        Text appName = new Text("FeedForward"); appName.setFont(Font.font("Arial", FontWeight.BOLD,15)); appName.setFill(Color.web(GREEN));
        Text slash = new Text(" / "); slash.setFont(Font.font("Arial",14)); slash.setFill(Color.web(GREY));
        Text create = new Text("Create Account"); create.setFont(Font.font("Arial",14)); create.setFill(Color.web(GREY));
        topBar.getChildren().addAll(logo, appName, slash, create);
        root.setTop(topBar);

        VBox content = new VBox(0); content.setAlignment(Pos.TOP_CENTER); content.setPadding(new Insets(40,0,40,0));

        VBox card = new VBox(18); card.setMaxWidth(540); card.setPadding(new Insets(36));
        card.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:14;-fx-border-color:"+BORDER+";-fx-border-radius:14;-fx-border-width:1;");

        Text heading = new Text("Create your account"); heading.setFont(Font.font("Arial", FontWeight.BOLD,24)); heading.setFill(Color.web(WHITE));
        Text sub = new Text("Join FeedForward and help reduce food waste"); sub.setFont(Font.font("Arial",13)); sub.setFill(Color.web(GREY));

        // Role selector — big bold toggle buttons
        Label roleLabel = new Label("I am a"); roleLabel.setTextFill(Color.web(GREY)); roleLabel.setFont(Font.font("Arial", FontWeight.BOLD,12));
        HBox roleBox = new HBox(10); roleBox.setAlignment(Pos.CENTER_LEFT);
        ToggleGroup tg = new ToggleGroup();
        ToggleButton donorBtn = roleToggle("🍽  Food Donor",  tg, GREEN);
        ToggleButton ngoBtn   = roleToggle("🏢  NGO Rep",    tg, PURPLE);
        ToggleButton volBtn   = roleToggle("🤝  Volunteer",  tg, AMBER);
        donorBtn.setSelected(true);
        roleBox.getChildren().addAll(donorBtn, ngoBtn, volBtn);

        VBox nameGrp  = makeFieldGroup("Full Name",      "Your full name");
        VBox emailGrp = makeFieldGroup("Email Address",  "you@example.com");
        VBox passGrp  = new VBox(7);
        Label plbl = new Label("Password"); plbl.setTextFill(Color.web(GREY)); plbl.setFont(Font.font("Arial", FontWeight.BOLD,12));
        PasswordField pf = new PasswordField(); pf.setPromptText("Min 6 characters"); pf.setPrefWidth(476); pf.setPrefHeight(42);
        String ps="-fx-background-color:"+BG_INPUT+";-fx-text-fill:"+WHITE+";-fx-border-color:"+BORDER2+";-fx-border-radius:8;-fx-background-radius:8;-fx-prompt-text-fill:#404040;-fx-padding:10 14;-fx-font-size:13;";
        pf.setStyle(ps); pf.focusedProperty().addListener((o,ov,nv)->pf.setStyle(ps+(nv?"-fx-border-color:"+GREEN+";":"")));
        passGrp.getChildren().addAll(plbl, pf);

        TextField nameField  = (TextField) nameGrp.getChildren().get(1);
        TextField emailField = (TextField) emailGrp.getChildren().get(1);

        VBox extra = new VBox(16);
        TextField org=makeField("Organization"), contact=makeField("Contact #"), loc=makeField("City / Area");
        TextField ngoName=makeField("NGO Name"), region=makeField("Service Region"), ngoC=makeField("Contact #");
        ComboBox<String> avail=combo("Available","Busy","Weekends Only"), trans=combo("Motorcycle","Car","Van","Bicycle","Walking");
        TextField volC=makeField("Contact #");

        loadDonor(extra,org,contact,loc);
        donorBtn.setOnAction(e->{if(donorBtn.isSelected())loadDonor(extra,org,contact,loc);});
        ngoBtn.setOnAction(e->{if(ngoBtn.isSelected())loadNGO(extra,ngoName,region,ngoC);});
        volBtn.setOnAction(e->{if(volBtn.isSelected())loadVol(extra,avail,trans,volC);});

        Label errLabel = makeFeedback();
        Button regBtn = makePrimaryBtn("Create Account"); regBtn.setPrefWidth(476);
        Button backBtn = new Button("← Back to Login");
        backBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:"+GREEN+";-fx-cursor:hand;-fx-border-color:transparent;-fx-font-size:12;-fx-padding:0;-fx-font-weight:bold;");
        backBtn.setOnAction(e->new LoginScreen(stage).show());
        HBox backRow = new HBox(backBtn); backRow.setAlignment(Pos.CENTER);

        regBtn.setOnAction(e->{
            String name=nameField.getText().trim(), email=emailField.getText().trim(), pass=pf.getText().trim(), result;
            if(donorBtn.isSelected()) result=userService.registerDonor(name,email,pass,org.getText().trim(),contact.getText().trim(),loc.getText().trim());
            else if(ngoBtn.isSelected()) result=userService.registerNGO(name,email,pass,ngoName.getText().trim(),region.getText().trim(),ngoC.getText().trim());
            else result=userService.registerVolunteer(name,email,pass,avail.getValue(),trans.getValue(),volC.getText().trim());
            if(result.equals("success")){
                Alert a=new Alert(Alert.AlertType.INFORMATION); a.setTitle("Done!"); a.setHeaderText(null); a.setContentText("Account created! Please sign in."); a.showAndWait();
                new LoginScreen(stage).show();
            } else setError(errLabel, result);
        });

        Separator sep = new Separator(); sep.setStyle("-fx-background-color:"+BORDER+";");
        card.getChildren().addAll(heading, sub, sep, roleLabel, roleBox, nameGrp, emailGrp, passGrp, extra, errLabel, regBtn, backRow);
        content.getChildren().add(card);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setStyle("-fx-background-color:"+BG+";-fx-background:"+BG+";"); scroll.setFitToWidth(true);
        root.setCenter(scroll);
        stage.setScene(new Scene(root,720,730)); stage.setTitle("FeedForward — Register"); stage.setResizable(false); stage.show();
    }

    private void loadDonor(VBox b, TextField o, TextField c, TextField l){b.getChildren().setAll(wrap("Organization",o),wrap("Contact Number",c),wrap("Location / City",l));}
    private void loadNGO(VBox b, TextField n, TextField r, TextField c){b.getChildren().setAll(wrap("NGO Name",n),wrap("Service Region",r),wrap("Contact Number",c));}
    private void loadVol(VBox b, ComboBox<String> a, ComboBox<String> t, TextField c){b.getChildren().setAll(wrapCB("Availability",a),wrapCB("Transport",t),wrap("Contact Number",c));}

    private VBox wrap(String lbl, TextField tf){VBox b=new VBox(7);Label l=new Label(lbl);l.setTextFill(Color.web(GREY));l.setFont(Font.font("Arial",FontWeight.BOLD,12));b.getChildren().addAll(l,tf);return b;}
    private VBox wrapCB(String lbl, ComboBox<String> cb){VBox b=new VBox(7);Label l=new Label(lbl);l.setTextFill(Color.web(GREY));l.setFont(Font.font("Arial",FontWeight.BOLD,12));cb.setPrefWidth(476);cb.setPrefHeight(42);cb.setStyle("-fx-background-color:"+BG_INPUT+";-fx-border-color:"+BORDER2+";-fx-border-radius:8;-fx-background-radius:8;");b.getChildren().addAll(l,cb);return b;}
    private ComboBox<String> combo(String... items){ComboBox<String> cb=new ComboBox<>();cb.getItems().addAll(items);cb.setValue(items[0]);cb.setStyle("-fx-background-color:"+BG_INPUT+";-fx-border-color:"+BORDER2+";-fx-border-radius:8;-fx-background-radius:8;");return cb;}

    private ToggleButton roleToggle(String text, ToggleGroup tg, String color){
        ToggleButton btn=new ToggleButton(text); btn.setToggleGroup(tg); btn.setPrefWidth(155);
        String base="-fx-background-color:"+BG_INPUT+";-fx-text-fill:"+GREY+";-fx-border-color:"+BORDER2+";-fx-border-radius:8;-fx-background-radius:8;-fx-font-size:12;-fx-cursor:hand;-fx-padding:10 12;";
        String sel ="-fx-background-color:"+color+"22;-fx-text-fill:"+color+";-fx-border-color:"+color+";-fx-border-radius:8;-fx-background-radius:8;-fx-font-size:12;-fx-font-weight:bold;-fx-cursor:hand;-fx-padding:10 12;-fx-effect:dropshadow(gaussian,"+color+",8,0.2,0,0);";
        btn.setStyle(base); btn.selectedProperty().addListener((o,ov,nv)->btn.setStyle(nv?sel:base));
        return btn;
    }
}
