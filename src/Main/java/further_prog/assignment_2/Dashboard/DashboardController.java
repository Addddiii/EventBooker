package further_prog.assignment_2.Dashboard;

import further_prog.assignment_2.Main.Session;
import further_prog.assignment_2.Main.Users;
import further_prog.assignment_2.Util.DateUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class DashboardController {
    private ArrayList<Event> displayableEvents = new ArrayList<>();
    Session session = Session.getInstance();

    @FXML
    private ScrollPane eventScrollPane;
    @FXML
    private VBox EventDisplayBox;
    @FXML
    private Label intro;

    @FXML
    public void initialize() {
        filterAndDisplayEvents();
        SetUser();
    }

    private void filterAndDisplayEvents() {
        ArrayList<Event> allEventsFromSession = session.getEventList();
        if (allEventsFromSession == null) {
            allEventsFromSession = new ArrayList<>();
        }

        // Filter for enabled events only
        this.displayableEvents = allEventsFromSession.stream()
                .filter(Event::isEnabled)
                .collect(Collectors.toCollection(ArrayList::new));

        // Save scroll position
        double scrollPosition = 0;
        if (eventScrollPane != null && eventScrollPane.getScene() != null && eventScrollPane.getContent() != null) {
            scrollPosition = eventScrollPane.getVvalue();
        }

        EventDisplayBox.getChildren().clear();

        if (this.displayableEvents.isEmpty()) {
            Label noEventsLabel = new Label("No events currently available.");
            noEventsLabel.setStyle("-fx-padding: 20px; -fx-font-size: 16px; -fx-text-fill: #666;");
            EventDisplayBox.getChildren().add(noEventsLabel);
        } else {
            for (Event event : this.displayableEvents) {
                HBox eventBox = createHbox(event);
                EventDisplayBox.getChildren().add(eventBox);
            }
        }

        // Restore scroll position
        if (eventScrollPane != null && eventScrollPane.getScene() != null && eventScrollPane.getContent() != null) {
            final double finalScrollPosition = scrollPosition;
            Platform.runLater(() -> {
                if (eventScrollPane.getContent() != null && ((VBox)eventScrollPane.getContent()).getChildren().size() > 0) {
                    eventScrollPane.setVvalue(finalScrollPosition);
                }
            });
        }
    }

    private HBox createHbox(Event event) {
        HBox hbox = new HBox();
        hbox.setSpacing(0);
        hbox.setPrefWidth(780);

        int availableSeats = event.getAvailableSeats();
        boolean isSoldOut = availableSeats <= 0;
        boolean isLowStock = availableSeats > 0 && availableSeats <= 5;
        boolean isBookableByDate = DateUtil.isEventBookable(event.getDay());

        String baseLabelStyle = "-fx-font-size: 13px; -fx-alignment: center-left;";
        String centerAlignStyle = "-fx-font-size: 13px; -fx-alignment: center;";
        String defaultTextFill = " -fx-text-fill: #2c3e50;";
        String unavailableDateTextFill = " -fx-text-fill: #aaaaaa;";
        String soldOutTextFill = " -fx-text-fill: #95a5a6;";

        Label name = new Label(event.getName());
        name.setPrefWidth(180);
        name.setStyle(baseLabelStyle + defaultTextFill);

        Label venue = new Label(event.getVenue());
        venue.setPrefWidth(150);
        venue.setStyle(baseLabelStyle + defaultTextFill);

        Label day = new Label(event.getDay());
        day.setPrefWidth(80);
        day.setStyle(centerAlignStyle + defaultTextFill);

        Label price = new Label(String.format("$%.2f", event.getPrice()));
        price.setPrefWidth(80);
        price.setStyle(centerAlignStyle + defaultTextFill);

        Label sold = new Label(String.valueOf(event.getSold()));
        sold.setPrefWidth(60);
        sold.setStyle(centerAlignStyle + defaultTextFill);

        Label total = new Label(String.valueOf(event.getTotal()));
        total.setPrefWidth(60);
        total.setStyle(centerAlignStyle + defaultTextFill);

        Label available = new Label(String.valueOf(availableSeats));
        available.setPrefWidth(80);
        available.setStyle(centerAlignStyle + defaultTextFill + " -fx-font-weight: bold;");

        Button addButton = new Button();
        addButton.setPrefWidth(80);
        addButton.setPrefHeight(30);

        // Style based on event state
        if (!isBookableByDate) {
            hbox.setStyle("-fx-padding: 8px 10px; -fx-background-color: #f0f0f0; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1px 0;");
            name.setStyle(baseLabelStyle + unavailableDateTextFill);
            venue.setStyle(baseLabelStyle + unavailableDateTextFill);
            day.setStyle(centerAlignStyle + unavailableDateTextFill);
            price.setStyle(centerAlignStyle + unavailableDateTextFill);
            sold.setStyle(centerAlignStyle + unavailableDateTextFill);
            total.setStyle(centerAlignStyle + unavailableDateTextFill);
            available.setStyle(centerAlignStyle + unavailableDateTextFill + " -fx-font-weight: normal;");
            addButton.setText("UNAVAILABLE");
            addButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-font-size: 10px; -fx-font-weight: bold; -fx-border-radius: 3px; -fx-background-radius: 3px;");
            addButton.setDisable(true);
        } else if (isSoldOut) {
            hbox.setStyle("-fx-padding: 8px 10px; -fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-border-width: 0 0 1px 0;");
            name.setStyle(baseLabelStyle + soldOutTextFill);
            venue.setStyle(baseLabelStyle + soldOutTextFill);
            day.setStyle(centerAlignStyle + soldOutTextFill);
            price.setStyle(centerAlignStyle + soldOutTextFill);
            sold.setStyle(centerAlignStyle + soldOutTextFill);
            total.setStyle(centerAlignStyle + soldOutTextFill);
            available.setStyle(centerAlignStyle + " -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            addButton.setText("SOLD OUT");
            addButton.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: #7f8c8d; -fx-font-size: 10px; -fx-font-weight: bold; -fx-border-radius: 3px; -fx-background-radius: 3px;");
            addButton.setDisable(true);
        } else {
            if (isLowStock) {
                hbox.setStyle("-fx-padding: 8px 10px; -fx-background-color: #fff8e1; -fx-border-color: #ffcc02; -fx-border-width: 0 0 1px 0;");
                available.setStyle(centerAlignStyle + " -fx-text-fill: #f39c12; -fx-font-weight: bold;");
                addButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-border-radius: 3px; -fx-background-radius: 3px;");
            } else {
                hbox.setStyle("-fx-padding: 8px 10px; -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1px 0;");
                available.setStyle(centerAlignStyle + " -fx-text-fill: #27ae60; -fx-font-weight: bold;");
                addButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-border-radius: 3px; -fx-background-radius: 3px;");
            }
            addButton.setText("ADD");
            addButton.setDisable(false);
            addButton.setOnAction(e -> {
                addToCart(event);
                Platform.runLater(this::refreshDisplay);
            });
        }

        hbox.getChildren().addAll(name, venue, day, price, sold, total, available, addButton);
        return hbox;
    }

    private void SetUser(){
        Users user = session.getCurrentUser();
        if (user != null) {
                this.intro.setText("Welcome: " + user.getName());
        } else {
            this.intro.setText("Welcome Guest");
        }
    }

    private void addToCart(Event eventFromDisplay) {
        if (!DateUtil.isEventBookable(eventFromDisplay.getDay())) {
            showAlert(Alert.AlertType.WARNING, "Booking Not Allowed", "Event Not Currently Bookable",
                    "Events for " + eventFromDisplay.getDay() + " cannot be booked at this time as the day has passed this week.");
            return;
        }

        // Find master event from session
        Event masterEvent = findEventInSession(eventFromDisplay);
        if (masterEvent == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Event Data Discrepancy", "Could not find the authoritative event details. Please refresh.");
            return;
        }

        int availableSeats = masterEvent.getAvailableSeats();
        if (availableSeats <= 0) {
            showAlert(Alert.AlertType.WARNING, "Event Sold Out", "Cannot add to cart",
                    masterEvent.getName() + " at " + masterEvent.getVenue() + " on " + masterEvent.getDay() + " is sold out!");
            return;
        }

        int currentCartQuantity = getCurrentCartQuantity(masterEvent);
        if (currentCartQuantity + 1 > availableSeats) {
            showAlert(Alert.AlertType.WARNING, "Insufficient Seats", "Cannot add more seats",
                    String.format("Only %d seats available for %s at %s on %s. You already have %d in your cart.",
                            availableSeats, masterEvent.getName(), masterEvent.getVenue(), masterEvent.getDay(), currentCartQuantity));
            return;
        }

        Booked newItemForCart = new Booked(masterEvent.getName(), masterEvent.getVenue(), masterEvent.getDay(), masterEvent.getPrice());
        newItemForCart.setQuantity(1);
        session.addToCart(newItemForCart);

        session.updateSoldCountForEvent(masterEvent, 1);

        showAlert(Alert.AlertType.INFORMATION, "Added to Cart", "Item added successfully",
                masterEvent.getName() + " has been added to your cart!\nAvailable seats now: " + (masterEvent.getAvailableSeats()));
    }

    // Find event in master session list
    private Event findEventInSession(Event eventToFind) {
        ArrayList<Event> masterEventList = session.getEventList();
        if (masterEventList == null) return null;

        for (Event e : masterEventList) {
            if (e.getName().equals(eventToFind.getName()) &&
                    e.getVenue().equals(eventToFind.getVenue()) &&
                    e.getDay().equals(eventToFind.getDay())) {
                return e;
            }
        }
        System.err.println("DashboardController: Could not find event " + eventToFind.getName() + " in master session list.");
        return null;
    }

    private int getCurrentCartQuantity(Event event) {
        if (session.getUserCart() == null) return 0;
        int quantity = 0;
        for (Booked booked : session.getUserCart()) {
            if (eventMatches(booked, event)) {
                quantity += booked.getQuantity();
            }
        }
        return quantity;
    }

    private boolean eventMatches(Booked booked, Event event) {
        return booked.getName().equals(event.getName()) &&
                booked.getVenue().equals(event.getVenue()) &&
                booked.getDay().equals(event.getDay());
    }



    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void cartPage(ActionEvent event) throws IOException {
        session.changeScene("/further_prog/assignment_2/Cart/Cart.fxml");
    }

    @FXML
    private void handleViewOrderHistory() {
        try {
            Session.getInstance().changeScene("/further_prog/assignment_2/Order/OrderHistory.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Logout Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to logout?");

        ButtonType logoutButtonType = new ButtonType("Logout");
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());
        confirmationAlert.getButtonTypes().setAll(logoutButtonType, cancelButtonType);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == logoutButtonType) {
            try {
                session.clearSession();
                Alert logoutSuccessAlert = new Alert(Alert.AlertType.INFORMATION);
                logoutSuccessAlert.setTitle("Logout Successful");
                logoutSuccessAlert.setHeaderText("You have been logged out successfully");
                logoutSuccessAlert.showAndWait();
                session.changeScene("/further_prog/assignment_2/Main/LoginPage.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Refresh display after changes
    public void refreshDisplay() {
        filterAndDisplayEvents();
        SetUser();
    }
}