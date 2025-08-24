import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
public class App extends Application {
    private static CityRepository db;
    private SmartGrid grid;
    public static void setDatabase(CityRepository db) { App.db = db; }
    public App() {
        super();
    }

    @Override
    public void start(Stage stage) throws Exception {
        signin(stage);
    }

    private void signin(Stage stage) {
        grid = new SmartGrid();
        VBox root = new VBox(10);
        root.getStyleClass().add("form-root");

        Label emailLabel = new Label("Email address");
        emailLabel.getStyleClass().add("form-label");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");
        emailField.getStyleClass().add("form-input");

        Label emailHelp = new Label("We'll never share your email with anyone else.");
        emailHelp.getStyleClass().add("form-help");

        VBox emailBox = new VBox(5, emailLabel, emailField, emailHelp);
        emailBox.getStyleClass().add("form-group");

        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("form-label");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.getStyleClass().add("form-input");

        VBox passwordBox = new VBox(5, passwordLabel, passwordField);
        passwordBox.getStyleClass().add("form-group");

        ToggleGroup group = new ToggleGroup();
        RadioButton adminOption = new RadioButton("Admin");
        adminOption.setToggleGroup(group);
        adminOption.setSelected(true);

        RadioButton userOption = new RadioButton("User");
        userOption.setToggleGroup(group);

        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("button-primary");
        submitButton.setOnAction(event -> {
            System.out.println("Form submitted");
            if (adminOption.isSelected()) {
                Consumer user = db.signin("admin", emailField.getText(), passwordField.getText());
                adminPortal(stage, user);
            } else if (userOption.isSelected()) {
                Consumer user = db.signin("user", emailField.getText(), passwordField.getText());
                userPortal(stage, user);
            }
        });
        Hyperlink signupLink = new Hyperlink("Don’t have an account? Sign up");
        signupLink.getStyleClass().add("hyperlink");
        signupLink.setOnAction(e -> {
            signup(stage);
        });

        root.getChildren().addAll(emailBox, passwordBox, adminOption, userOption, submitButton, signupLink);
        Scene scene = new Scene(root, 300, 300);
        scene.getStylesheets().add("file:///C:/Users/Qazi/Documents/Object-Oriented%20Programming/Lab Final/src/main/resources/App.css");
        stage.setScene(scene);
        stage.show();
    }

    private void signup(Stage stage) {
        VBox registerRoot = new VBox(10);
        registerRoot.getStyleClass().add("form-root");

        Label heading = new Label("Register New Account");
        heading.getStyleClass().add("form-label");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");
        nameField.getStyleClass().add("form-input");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");
        emailField.getStyleClass().add("form-input");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.getStyleClass().add("form-input");

        Hyperlink signinLink = new Hyperlink("Already have an account? Sign in");
        signinLink.getStyleClass().add("hyperlink");
        signinLink.setOnAction(e -> {
            signin(stage);
        });

        ToggleGroup group = new ToggleGroup();
        RadioButton adminOption = new RadioButton("Admin");
        adminOption.setToggleGroup(group);
        adminOption.setSelected(true);
        RadioButton userOption = new RadioButton("User");
        userOption.setToggleGroup(group);

        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("button-primary");
        submitButton.setOnAction((actionEvent) -> {
            System.out.println("Form submitted");
            System.out.println("Account created for: " + nameField.getText());
            if (adminOption.isSelected()) {
                Consumer user = db.signup("admin", nameField.getText(), emailField.getText(), passwordField.getText());
                adminPortal(stage, user);
            } else if (userOption.isSelected()) {
                Consumer user = db.signup("user", nameField.getText(), emailField.getText(), passwordField.getText());
                userPortal(stage, user);
            }
        });

        registerRoot.getChildren().addAll(heading, nameField, emailField, passwordField, adminOption, userOption, submitButton, signinLink);
        Scene registerScene = new Scene(registerRoot, 300, 300);
        registerScene.getStylesheets().add("file:///C:/Users/Qazi/Documents/Object-Oriented%20Programming/Lab Final/src/main/resources/App.css");
        stage.setScene(registerScene);
    }

    public void userPortal(Stage stage, Consumer user) {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid-centered");
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(30));
        gridPane.setAlignment(Pos.TOP_CENTER);
        Label title = new Label("Welcome to the User Portal");
        title.getStyleClass().add("portal-title");
        gridPane.add(title, 0, 0, 2, 1);
        Button powerStation = new Button("Connect Power");
        Button travel       = new Button("Buy Ticket");
        Button emergency    = new Button("Call Emergency");
        Button logout       = new Button("Log Out");
        powerStation.getStyleClass().add("button-primary");
        powerStation.setOnAction(e -> showEnergyForm(stage, user));
        travel.getStyleClass().add("button-primary");
        travel.setOnAction(e -> showTravelForm(stage, user));
        emergency.getStyleClass().add("button-primary");
        emergency.setOnAction(e -> showEmergencyForm(stage));
        logout.getStyleClass().add("button-secondary");
        logout.setOnAction(e -> signin(stage));
        gridPane.add(powerStation, 0, 1);
        gridPane.add(travel,       1, 1);
        gridPane.add(emergency,    0, 2);
        gridPane.add(logout,       1, 2);
        Scene scene = new Scene(gridPane, 450, 350);
        scene.getStylesheets().add("file:///C:/Users/Qazi/Documents/Object-Oriented%20Programming/Lab Final/src/main/resources/App.css");
        stage.setScene(scene);
        stage.show();
    }

    private void showEmergencyForm(Stage stage) {
        VBox root = new VBox(10);
        root.getStyleClass().add("form-root");
        Label zoneLabel = new Label("Select your City Zone:");
        zoneLabel.getStyleClass().add("form-label");
        ComboBox<CityZone> zoneBox = new ComboBox<>(
            FXCollections.observableArrayList(CityResource.getCityZones())
        );
        zoneBox.getStyleClass().add("form-input");
        Label policeLabel = new Label("Select Police Unit:");
        policeLabel.getStyleClass().add("form-label");
        ComboBox<Police> policeBox = new ComboBox<>();
        policeBox.getStyleClass().add("form-input");
        policeBox.setDisable(true);
        Label fireLabel = new Label("Select FireFighter Unit:");
        fireLabel.getStyleClass().add("form-label");
        ComboBox<FireFighter> fireBox = new ComboBox<>();
        fireBox.getStyleClass().add("form-input");
        fireBox.setDisable(true);
        zoneBox.setOnAction(e -> {
            CityZone cityZone = zoneBox.getValue();
            if (cityZone != null) {
            policeBox.setItems(FXCollections.observableArrayList(
                ((EmergencyHub) cityZone.getEmergencyHub()).getPoliceUnits()));
            fireBox.setItems(FXCollections.observableArrayList(
                ((EmergencyHub) cityZone.getEmergencyHub()).getFireFighterUnits()));
            policeBox.setDisable(false);
            fireBox.setDisable(false);
            }
        });
        policeBox.setOnAction(e -> {
            if (policeBox.getValue() != null) {
                fireBox.getSelectionModel().clearSelection();
                fireBox.setDisable(true);
            } else {
                fireBox.setDisable(false);
            }
        });
        fireBox.setOnAction(e -> {
            if (fireBox.getValue() != null) {
                policeBox.getSelectionModel().clearSelection();
                policeBox.setDisable(true);
            } else {
                policeBox.setDisable(false);
            }
        });
        Label showOutput = new Label();
        Button dispatch = new Button("Dispatch");
        dispatch.getStyleClass().add("button-primary");
        dispatch.setOnAction(evt -> {
            CityZone zone = zoneBox.getValue();
            Police  police  = policeBox.getValue();
            FireFighter fireFighter = fireBox.getValue();
            if (zone == null || (police == null && fireFighter == null)) {
                showOutput.setText("Please select a zone and a unit.");
                return;
            }
            if (police != null) {
                police.setStatus("Dispatched");
                police.sendEmergencyAlert(zone);
                showOutput.setText("Dispatched " + police.getName() + " to " + zone.getName());
            }
            else if (fireFighter != null) {
                fireFighter.setStatus("Dispatched");
                fireFighter.sendEmergencyAlert(zone);
                showOutput.setText("Dispatched " + fireFighter.getName() + " to " + zone.getName());
            }
        });
        root.getChildren().addAll(
            zoneLabel, zoneBox,
            policeLabel, policeBox,
            fireLabel, fireBox,
            dispatch, showOutput
        );

        Scene scene = new Scene(root, 350, 300);
        scene.getStylesheets().add("file:///C:/Users/Qazi/Documents/Object-Oriented%20Programming/Lab Final/src/main/resources/App.css");
        stage.setScene(scene);
        stage.show();
    }
    private void showEnergyForm(Stage stage, Consumer user) {
        VBox root = new VBox(10);
        root.getStyleClass().add("form-root");
        Label zoneLabel = new Label("Select your City Zone:");
        zoneLabel.getStyleClass().add("form-label");
        ComboBox<CityZone> zoneBox = new ComboBox<>(FXCollections.observableArrayList(CityResource.getCityZones()));
        zoneBox.getStyleClass().add("form-input");
        Label powerStationLabel = new Label("Select a Power Station:");
        powerStationLabel.getStyleClass().add("form-label");
        ComboBox<PowerStation> powerStationBox = new ComboBox<>();
        powerStationBox.getStyleClass().add("form-input");
        powerStationBox.setDisable(true);
        zoneBox.setOnAction(e -> {
            CityZone zone = zoneBox.getValue();
            if (zone != null) {
            powerStationBox.setItems(FXCollections.observableArrayList(zone.getEnergyHub().getResources()));
            powerStationBox.setDisable(false);
            }
        });
        Label showOutput = new Label("");
        Button connect = new Button("Connect & Start Supply");
        connect.getStyleClass().add("button-primary");
        connect.setOnAction(e -> {
            CityZone zone = zoneBox.getValue();
            PowerStation powerStation = powerStationBox.getValue();
            if (zone == null || powerStation == null) {
                showOutput.setText("Please select both a zone and a power station.");
                return;
            }
            energyTracker(stage, user, powerStation);
        });

        root.getChildren().addAll(
        zoneLabel, zoneBox,
        powerStationLabel, powerStationBox,
        connect, showOutput
        );

        Scene scene = new Scene(root, 350, 250);
        scene.getStylesheets().add("file:///C:/Users/Qazi/Documents/Object-Oriented%20Programming/Lab Final/src/main/resources/App.css");
        stage.setScene(scene);
        stage.show();
    }
    public void energyTracker(Stage stage, Consumer user, PowerStation powerStation) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (s)");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Energy Consumed (kWh)");
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Live Energy Consumption");
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Energy");

        lineChart.getData().add(series);
        Timeline timeline = new Timeline();
        final int[] time = {0};
        grid.supplyEnergy(user, powerStation);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            double currentEnergy = SmartGrid.getEnergyConsumed();
            series.getData().add(new XYChart.Data<>(time[0]++, currentEnergy));
            if (!grid.getEnergySupplier().isAlive()) userPortal(stage, user);
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        VBox root = new VBox(lineChart);
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add("file:///C:/Users/Qazi/Documents/Object-Oriented%20Programming/Lab Final/src/main/resources/App.css");

        stage.setScene(scene);
        stage.setTitle("Energy Usage Tracker");
        stage.show();
    }
    private void showTravelForm(Stage stage, Consumer user) {
        VBox root = new VBox(10);
        Label zone1Label = new Label("Select your current city: ");
        ComboBox<CityZone> zone1Box = new ComboBox<>(FXCollections.observableArrayList(CityResource.getCityZones()));
        Label zone2Label = new Label("Select your destination city: ");
        ComboBox<CityZone> zone2Box = new ComboBox<>(FXCollections.observableArrayList(CityResource.getCityZones()));
        Label busLabel = new Label("Select your bus (if preferred travel method is bus): ");
        ComboBox<Bus> busBox = new ComboBox<>();
        busBox.setDisable(true);
        Label trainLabel = new Label("Select your train (if preferred travel method is train): ");
        ComboBox<Train> trainBox = new ComboBox<>();
        trainBox.setDisable(true);
        Button go = new Button("Purchase Ticket");
        zone1Box.setOnAction(evt -> {
            CityZone selected = zone1Box.getValue();
            if (selected != null) {
                busBox.setItems(FXCollections.observableArrayList(((TransportationHub) zone1Box.getValue().getTransportHub()).getBusses()));
                trainBox.setItems(FXCollections.observableArrayList(((TransportationHub) zone1Box.getValue().getTransportHub()).getTrains()));
                busBox.setDisable(false);
                trainBox.setDisable(false);
            }
        });
        busBox.setOnAction(event->{
            if (busBox.getValue() != null) {
            trainBox.setDisable(true);
            trainBox.getSelectionModel().clearSelection();
            } else trainBox.setDisable(false);
        });
        trainBox.setOnAction(event->{
            if (trainBox.getValue() != null) {
            busBox.setDisable(true);
            busBox.getSelectionModel().clearSelection();
            } else busBox.setDisable(false);
        });
        go.setOnAction(evt -> {
            CityZone zone1 = zone1Box.getValue();
            CityZone zone2 = zone2Box.getValue();
            Bus bus  = busBox.getValue();
            Train train = trainBox.getValue();
            TransportUnit unit = null;
            if (bus != null) {
                bus.setStatus("En Route");
                for (TransportUnit transportUnit: zone1.getTransportHub().getResources()) 
                if (transportUnit.getLocation() == bus.getLocation()) unit = transportUnit;
            } else if(train != null) {
                train.setStatus("En Route");
                for (TransportUnit transportUnit: zone1.getTransportHub().getResources()) 
                if (transportUnit.getLocation() == train.getLocation()) unit = transportUnit;
            } else unit = new TransportUnit();
            double distance = zone1.getDistance(zone2);
            unit.setTravel(distance);
            unit.startJourney();
            showJourneyTracker(stage, unit, distance, user);
        });
        root.getChildren().addAll(zone1Label, zone1Box, zone2Label, zone2Box, busLabel, busBox, trainLabel, trainBox, go);
        stage.setScene(new Scene(root, 300, 250));
        }
    private void showJourneyTracker(Stage stage, TransportUnit unit, double distance, Consumer user) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (s)");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Distance Covered (km)");
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Live Journey Tracker");
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(unit.getName());
        lineChart.getData().add(series);
        Timeline ticker = new Timeline();
        final int[] seconds = {0};
        ticker.getKeyFrames().add(new KeyFrame(Duration.seconds(1), ev -> {
            double current = unit.getDistanceCovered();
            series.getData().add(new XYChart.Data<>(seconds[0]++, current));
            if (series.getData().size() > 60) {
                series.getData().remove(0);
            }
            if (current >= distance) {
                ticker.stop();
                unit.endJourney();
                userPortal(stage, user);
            }
        }));

        ticker.setCycleCount(Timeline.INDEFINITE);
        ticker.play();

        VBox root = new VBox(lineChart);
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        scene.getStylesheets().add("file:///C:/Users/Qazi/Documents/Object-Oriented%20Programming/Lab Final/src/main/resources/App.css");
        stage.setTitle("Journey Tracker - " + unit.getName());
        stage.show();
    }
    public void adminPortal(Stage stage, Consumer user) {
    GridPane gridPane = new GridPane();
    gridPane.getStyleClass().add("grid-centered");
    gridPane.setHgap(20);
    gridPane.setVgap(20);
    gridPane.setPadding(new Insets(30));
    gridPane.setAlignment(Pos.TOP_CENTER);

    Label title = new Label("Welcome to the Admin Portal");
    title.getStyleClass().add("portal-title");
    gridPane.add(title, 0, 0, 2, 1);

    Button addCityZone     = new Button("Register City Zone");
    Button addPowerStation = new Button("Register Power Station");
    Button addBus          = new Button("Add Bus");
    Button addTrain        = new Button("Add Train");
    Button addPolice       = new Button("Add Police");
    Button addFireFighter  = new Button("Add FireFighter");
    Button logout          = new Button("Log Out");
    Button offline         = new Button("Offline a resource");

    addCityZone.setOnAction(e -> addCityZone(stage, user));
    addPowerStation.setOnAction(e -> createResource(stage, PowerStation.class, user));
    addBus.setOnAction(e -> createResource(stage, Bus.class, user));
    addTrain.setOnAction(e -> createResource(stage, Train.class, user));
    addPolice.setOnAction(e -> createResource(stage, Police.class, user));
    addFireFighter.setOnAction(e -> createResource(stage, FireFighter.class, user));
    logout.setOnAction(e -> signin(stage));

    for (Button btn : new Button[]{addCityZone, addPowerStation, addBus, addTrain, addPolice, addFireFighter, offline}) {
        btn.getStyleClass().add("button-primary");
    }
    logout.getStyleClass().add("button-secondary");

    offline.setOnAction(event -> {
        Stage offlineStage = new Stage();
        offlineStage.setTitle("Offline Resource");

        GridPane offlinePane = new GridPane();
        offlinePane.setHgap(10);
        offlinePane.setVgap(10);
        offlinePane.setPadding(new Insets(20));
        offlinePane.setAlignment(Pos.CENTER);

        Label zoneLabel = new Label("Select your City Zone:");
        zoneLabel.getStyleClass().add("form-label");
        ComboBox<CityZone> zoneBox = new ComboBox<>(FXCollections.observableArrayList(CityResource.getCityZones()));

        ComboBox<Police> policeBox = new ComboBox<>();
        ComboBox<FireFighter> fireBox = new ComboBox<>();
        ComboBox<Bus> busBox = new ComboBox<>();
        ComboBox<Train> trainBox = new ComboBox<>();
        ComboBox<PowerStation> powerStationBox = new ComboBox<>();
        policeBox.setDisable(true);
        fireBox.setDisable(true);
        busBox.setDisable(true);
        trainBox.setDisable(true);
        powerStationBox.setDisable(true);

        zoneBox.setOnAction(e -> {
            CityZone selected = zoneBox.getValue();
            if (selected != null) {
                EmergencyHub eh = (EmergencyHub) selected.getEmergencyHub();
                TransportationHub th = (TransportationHub) selected.getTransportHub();
                EnergyHub energyHub = (EnergyHub) selected.getEnergyHub();
                policeBox.setItems(FXCollections.observableArrayList(eh.getPoliceUnits()));
                fireBox.setItems(FXCollections.observableArrayList(eh.getFireFighterUnits()));
                busBox.setItems(FXCollections.observableArrayList(th.getBusses()));
                trainBox.setItems(FXCollections.observableArrayList(th.getTrains()));
                powerStationBox.setItems(FXCollections.observableArrayList(energyHub.getPowerStations()));

                policeBox.setDisable(false);
                fireBox.setDisable(false);
                busBox.setDisable(false);
                trainBox.setDisable(false);
                powerStationBox.setDisable(false);
            }
        });

        Label statusLabel = new Label();
        Button offlineBtn = new Button("Offline");
        offlineBtn.getStyleClass().add("button-primary");

        offlineBtn.setOnAction(e -> {
            if (policeBox.getValue() != null) policeBox.getValue().setStatus("Offline");
            if (fireBox.getValue() != null) fireBox.getValue().setStatus("Offline");
            if (busBox.getValue() != null) busBox.getValue().setStatus("Offline");
            if (trainBox.getValue() != null) trainBox.getValue().setStatus("Offline");
            if (powerStationBox.getValue() != null) powerStationBox.getValue().setStatus("Offline");
            statusLabel.setText("Selected resources set to offline.");
        });

        offlinePane.add(zoneLabel, 0, 0);         offlinePane.add(zoneBox, 1, 0);
        offlinePane.add(new Label("Police:"), 0, 1);      offlinePane.add(policeBox, 1, 1);
        offlinePane.add(new Label("FireFighter:"), 0, 2); offlinePane.add(fireBox, 1, 2);
        offlinePane.add(new Label("Bus:"), 0, 3);         offlinePane.add(busBox, 1, 3);
        offlinePane.add(new Label("Train:"), 0, 4);       offlinePane.add(trainBox, 1, 4);
        offlinePane.add(new Label("Power Station:"), 0, 5); offlinePane.add(powerStationBox, 1, 5);
        offlinePane.add(offlineBtn, 0, 6, 2, 1);
        offlinePane.add(statusLabel, 0, 7, 2, 1);

        Scene offlineScene = new Scene(offlinePane, 400, 350);
        offlineStage.setScene(offlineScene);
        offlineStage.show();
    });
    gridPane.add(addCityZone,    0, 1);
    gridPane.add(addPowerStation,1, 1);
    gridPane.add(addBus,         0, 2);
    gridPane.add(addTrain,       1, 2);
    gridPane.add(addPolice,      0, 3);
    gridPane.add(addFireFighter, 1, 3);
    gridPane.add(offline,        1, 4);
    gridPane.add(logout,         0, 4);

    Scene scene = new Scene(gridPane, 500, 450);
    scene.getStylesheets().add("file:///C:/Users/Qazi/Documents/Object-Oriented%20Programming/Lab Final/src/main/resources/App.css");
    stage.setScene(scene);
    stage.show();
}


public <T extends CityResource> void createResource(Stage stage, Class<T> resourceClass, Consumer user) {
        VBox root = new VBox(10);
        root.getStyleClass().add("form-root");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.getStyleClass().add("form-input");
        ComboBox<CityZone> zoneDropdown = new ComboBox<>();
        zoneDropdown.setItems(FXCollections.observableArrayList(CityResource.getCityZones()));

        TextField statusField = new TextField();
        statusField.setPromptText("Status");
        statusField.getStyleClass().add("form-input");

        Button submit = new Button("Create");
        submit.getStyleClass().add("button-primary");
        submit.setOnAction(e -> {
            db.createResource(nameField.getText(), zoneDropdown.getValue(), statusField.getText(), resourceClass);
            adminPortal(stage, user);
        });
        root.getChildren().addAll(nameField, zoneDropdown, statusField, submit);
        Scene scene = new Scene(root, 350, 300);
        scene.getStylesheets().add("file:///C:/Users/Qazi/Documents/Object-Oriented%20Programming/Lab Final/src/main/resources/App.css");
        stage.setScene(scene);
        stage.show();
    }

    private void addCityZone(Stage stage, Consumer user) {
        VBox root = new VBox();
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.getStyleClass().add("form-input");
        Button submit = new Button("Create");
        submit.getStyleClass().add("button-primary");
        submit.setOnAction(e -> {
            CityZone city = new CityZone(nameField.getText());
            CityResource.addCityZones(city);
            adminPortal(stage, user);
        });
        root.getChildren().addAll(nameField, submit);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("file:///C:/Users/Qazi/Documents/Object-Oriented%20Programming/Lab Final/src/main/resources/App.css");
        stage.setScene(scene);
        stage.show();
    }
}
