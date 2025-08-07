package further_prog.assignment_2.Admin;

import further_prog.assignment_2.Dashboard.Event;
import further_prog.assignment_2.FileManipulators.EventReader;
import further_prog.assignment_2.FileManipulators.EventWriter;
import further_prog.assignment_2.Main.Session;
import further_prog.assignment_2.Main.Users;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private VBox adminEventDisplayBox;
    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        Users currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.isAdmin()) {
            welcomeLabel.setText("Welcome, " + currentUser.getName() + " (Admin)");
        }

        // Load events from file if session list is empty
        if (Session.getInstance().getEventList() == null || Session.getInstance().getEventList().isEmpty()) {
            System.out.println("AdminDashboardController: Event list in session is empty or null. Attempting to load from file.");
            EventReader eventReader = new EventReader();
            eventReader.eventReader("src/main/java/further_prog/assignment_2/FileManipulators/events.dat");
        }

        loadAndDisplayAdminEvents();
    }

    // Display all events grouped by name in the admin interface
    private void loadAndDisplayAdminEvents() {
        ArrayList<Event> rawEvents = Session.getInstance().getEventList();
        ObservableList<Event> localObservableEvents = FXCollections.observableArrayList(rawEvents);

        adminEventDisplayBox.getChildren().clear();

        if (localObservableEvents.isEmpty()) {
            adminEventDisplayBox.getChildren().add(new Label("No events found in the system. Add new events."));
            updateStatus("No events loaded or system is empty.");
            return;
        }

        // Group events by name for display
        Map<String, List<Event>> groupedEvents = localObservableEvents.stream()
                .collect(Collectors.groupingBy(Event::getName));

        for (Map.Entry<String, List<Event>> entry : groupedEvents.entrySet()) {
            VBox eventGroupVBox = new VBox(5);
            eventGroupVBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-padding: 10px; -fx-background-color: #fdfefe;");

            Label eventNameLabel = new Label(entry.getKey());
            eventNameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            eventGroupVBox.getChildren().add(eventNameLabel);

            for (Event eventInstance : entry.getValue()) {
                eventGroupVBox.getChildren().add(createAdminEventRow(eventInstance));
            }
            adminEventDisplayBox.getChildren().add(eventGroupVBox);
            VBox.setMargin(eventGroupVBox, new Insets(0, 0, 10, 0));
        }
        updateStatus("Events loaded. " + localObservableEvents.size() + " total event instances displayed.");
    }

    // Create a single row displaying event details with admin controls
    private HBox createAdminEventRow(Event event) {
        HBox row = new HBox(10);
        row.setPadding(new Insets(5));
        row.setStyle(event.isEnabled() ? "-fx-background-color: #e8f8f5;" : "-fx-background-color: #f5e7e6;");

        Label detailsLabel = new Label(String.format("Venue: %s, Day: %s, Price: $%.2f, Sold: %d, Total: %d, Available: %d",
                event.getVenue(), event.getDay(), event.getPrice(), event.getSold(), event.getTotal(), event.getAvailableSeats()));
        detailsLabel.setWrapText(true);
        HBox.setHgrow(detailsLabel, Priority.ALWAYS);

        Button toggleEnableButton = new Button(event.isEnabled() ? "Disable" : "Enable");
        toggleEnableButton.setOnAction(e -> toggleEventEnabled(event));

        Button modifyButton = new Button("Modify");
        modifyButton.setOnAction(e -> handleModifyEventDialog(event));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> handleDeleteEvent(event));

        row.getChildren().addAll(detailsLabel, toggleEnableButton, modifyButton, deleteButton);
        return row;
    }

    // Toggle event enabled/disabled status
    private void toggleEventEnabled(Event eventToToggle) {
        eventToToggle.setEnabled(!eventToToggle.isEnabled());
        saveEventsToFile();
        loadAndDisplayAdminEvents();
        updateStatus("Event '" + eventToToggle.getName() + " - " + eventToToggle.getVenue() + " (" + eventToToggle.getDay() + ")' " + (eventToToggle.isEnabled() ? "enabled." : "disabled."));
    }

    // Show dialog to add a new event
    @FXML
    private void handleAddEventDialog() {
        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Add New Event");
        dialog.setHeaderText("Enter details for the new event.");

        ButtonType addButtonType = new ButtonType("Add Event", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(); nameField.setPromptText("Event Name");
        TextField venueField = new TextField(); venueField.setPromptText("Venue");
        TextField dayField = new TextField(); dayField.setPromptText("Day (e.g., Mon, Tue)");
        TextField priceField = new TextField(); priceField.setPromptText("Price (e.g., 25.50)");
        TextField totalSeatsField = new TextField(); totalSeatsField.setPromptText("Total Seats");

        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Venue:"), 0, 1); grid.add(venueField, 1, 1);
        grid.add(new Label("Day:"), 0, 2); grid.add(dayField, 1, 2);
        grid.add(new Label("Price:"), 0, 3); grid.add(priceField, 1, 3);
        grid.add(new Label("Total Seats:"), 0, 4); grid.add(totalSeatsField, 1, 4);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(nameField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText().trim();
                    String venue = venueField.getText().trim();
                    String day = dayField.getText().trim();
                    if (name.isEmpty() || venue.isEmpty() || day.isEmpty()) {
                        throw new IllegalArgumentException("Name, Venue, and Day cannot be empty.");
                    }
                    double price = Double.parseDouble(priceField.getText().trim());
                    int totalSeats = Integer.parseInt(totalSeatsField.getText().trim());
                    if (price < 0 || totalSeats <= 0) {
                        throw new IllegalArgumentException("Price must be non-negative and Total Seats must be positive.");
                    }
                    return new Event(name, venue, day, price, totalSeats);
                } catch (NumberFormatException e) {
                    showErrorAlert("Invalid Input", "Price and Total Seats must be valid numbers."); return null;
                } catch (IllegalArgumentException e) {
                    showErrorAlert("Invalid Input", e.getMessage()); return null;
                }
            }
            return null;
        });

        Optional<Event> result = dialog.showAndWait();
        result.ifPresent(newEvent -> {
            boolean isDuplicate = Session.getInstance().getEventList().stream()
                    .anyMatch(ev -> ev.getName().equalsIgnoreCase(newEvent.getName()) &&
                            ev.getVenue().equalsIgnoreCase(newEvent.getVenue()) &&
                            ev.getDay().equalsIgnoreCase(newEvent.getDay()));
            if (isDuplicate) {
                showErrorAlert("Duplicate Event", "An event with the same name, venue, and day already exists.");
            } else {
                Session.getInstance().getEventList().add(newEvent);
                saveEventsToFile();
                loadAndDisplayAdminEvents();
                updateStatus("New event '" + newEvent.getName() + "' added.");
            }
        });
    }

    // Show dialog to modify an existing event
    private void handleModifyEventDialog(Event eventToModify) {
        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Modify Event");

        TextField nameField = new TextField(eventToModify.getName());
        TextField venueField = new TextField(eventToModify.getVenue());
        TextField dayField = new TextField(eventToModify.getDay());
        TextField priceField = new TextField(String.valueOf(eventToModify.getPrice()));
        TextField totalSeatsField = new TextField(String.valueOf(eventToModify.getTotal()));
        Label soldSeatsLabel = new Label(String.valueOf(eventToModify.getSold()));
        CheckBox enabledCheckBox = new CheckBox("Enabled");
        enabledCheckBox.setSelected(eventToModify.isEnabled());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Venue:"), 0, 1); grid.add(venueField, 1, 1);
        grid.add(new Label("Day:"), 0, 2); grid.add(dayField, 1, 2);
        grid.add(new Label("Price:"), 0, 3); grid.add(priceField, 1, 3);
        grid.add(new Label("Total Seats:"), 0, 4); grid.add(totalSeatsField, 1, 4);
        grid.add(new Label("Sold Seats:"), 0, 5); grid.add(soldSeatsLabel, 1, 5);
        grid.add(new Label("Status:"), 0, 6); grid.add(enabledCheckBox, 1, 6);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String newName = nameField.getText().trim();
                    String newVenue = venueField.getText().trim();
                    String newDay = dayField.getText().trim();
                    if (newName.isEmpty() || newVenue.isEmpty() || newDay.isEmpty()) {
                        throw new IllegalArgumentException("Name, Venue, and Day cannot be empty.");
                    }
                    double newPrice = Double.parseDouble(priceField.getText().trim());
                    int newTotalSeats = Integer.parseInt(totalSeatsField.getText().trim());
                    if (newPrice < 0 || newTotalSeats <= 0) {
                        throw new IllegalArgumentException("Price must be non-negative and Total Seats must be positive.");
                    }

                    Event tempEvent = new Event(newName, newVenue, newDay, 0, 0);
                    boolean isDuplicate = Session.getInstance().getEventList().stream()
                            .filter(ev -> ev != eventToModify)
                            .anyMatch(ev -> ev.getName().equalsIgnoreCase(tempEvent.getName()) &&
                                    ev.getVenue().equalsIgnoreCase(tempEvent.getVenue()) &&
                                    ev.getDay().equalsIgnoreCase(tempEvent.getDay()));
                    if (isDuplicate) {
                        throw new IllegalArgumentException("Another event with the same new name, venue, and day already exists.");
                    }
                    return eventToModify;
                } catch (NumberFormatException e) {
                    showErrorAlert("Invalid Input", "Price and Total Seats must be valid numbers."); return null;
                } catch (IllegalArgumentException e) {
                    showErrorAlert("Invalid Input", e.getMessage()); return null;
                }
            }
            return null;
        });

        Optional<Event> result = dialog.showAndWait();
        result.ifPresent(signalEvent -> {
            eventToModify.setName(nameField.getText().trim());
            eventToModify.setVenue(venueField.getText().trim());
            eventToModify.setDay(dayField.getText().trim());
            eventToModify.setPrice(Double.parseDouble(priceField.getText().trim()));
            eventToModify.setTotal(Integer.parseInt(totalSeatsField.getText().trim()));
            eventToModify.setEnabled(enabledCheckBox.isSelected());

            saveEventsToFile();
            loadAndDisplayAdminEvents();
            updateStatus("Event '" + eventToModify.getName() + "' updated.");
        });
    }

    // Delete an event after confirmation
    private void handleDeleteEvent(Event eventToDelete) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Event");
        confirmation.setHeaderText("Are you sure you want to delete this event?");
        confirmation.setContentText(eventToDelete.getName() + " at " + eventToDelete.getVenue() + " on " + eventToDelete.getDay() +
                "\nSold tickets: " + eventToDelete.getSold() + ". This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Session.getInstance().getEventList().remove(eventToDelete);
            saveEventsToFile();
            loadAndDisplayAdminEvents();
            updateStatus("Event '" + eventToDelete.getName() + "' deleted.");
        }
    }

    // Save current event list to file
    private void saveEventsToFile() {
        try {
            EventWriter writer = new EventWriter();
            writer.writeEventsToFile("src/main/java/further_prog/assignment_2/FileManipulators/events.dat",
                    Session.getInstance().getEventList());
        } catch (IOException e) {
            showErrorAlert("File Save Error", "Could not save event data: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewAllOrders() {
        try {
            System.out.println("Admin: View All User Orders button clicked.");
            Session.getInstance().changeScene("/further_prog/assignment_2/Admin/AllUserOrdersView.fxml");
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not open 'View All Orders' screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Session.getInstance().clearSession();
            Session.getInstance().changeScene("/further_prog/assignment_2/Main/LoginPage.fxml");
        } catch (IOException e) {
            showErrorAlert("Logout Error", "Failed to logout: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }
}