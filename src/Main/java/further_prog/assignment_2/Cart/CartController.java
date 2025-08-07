package further_prog.assignment_2.Cart;

import further_prog.assignment_2.Dashboard.Booked;
import further_prog.assignment_2.Dashboard.Event;
import further_prog.assignment_2.FileManipulators.EventWriter;
import further_prog.assignment_2.Main.Users;
import further_prog.assignment_2.Util.DateUtil;
import further_prog.assignment_2.Order.OrderHistoryManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import further_prog.assignment_2.Main.Session;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;
import javafx.scene.control.TextInputDialog;

public class CartController {
    @FXML
    private VBox cartItemBox;
    @FXML
    private Label totalCostLabel;
    @FXML
    private Button checkoutButton;
    @FXML
    private Button backToDashboardButton;
    @FXML
    private Button logoutButton;

    private ArrayList<Booked> cart;
    private boolean checkoutInProgress = false;

    private static final Pattern VALID_CODE_PATTERN = Pattern.compile("^\\d{6}$");
    private static final String[] DEMO_VALID_CODES = {"123456", "999999", "000000", "555555"};

    @FXML
    public void initialize() {
        cart = Session.getInstance().getUserCart();
        if (cart == null) {
            System.err.println("CartController: Session cart was null. Initializing new cart.");
            cart = new ArrayList<>();
            Session.getInstance().setUserCart(cart);
        }

        displayCartItems();
        updateTotalCost();
        validateCartAvailability();
    }

    private void displayCartItems() {
        cartItemBox.getChildren().clear();

        // Create header row
        HBox headerRow = new HBox();
        headerRow.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 10px;");
        Label eventHeader = new Label("Event"); eventHeader.setPrefWidth(100); eventHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label venueHeader = new Label("Venue"); venueHeader.setPrefWidth(100); venueHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label dayHeader = new Label("Day"); dayHeader.setPrefWidth(100); dayHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label quantityHeader = new Label("Qty"); quantityHeader.setPrefWidth(60); quantityHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label priceHeader = new Label("Price"); priceHeader.setPrefWidth(80); priceHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label totalHeader = new Label("Total"); totalHeader.setPrefWidth(80); totalHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label actionHeader = new Label("Action"); actionHeader.setPrefWidth(80); actionHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        headerRow.getChildren().addAll(eventHeader, venueHeader, dayHeader, quantityHeader, priceHeader, totalHeader, actionHeader);
        cartItemBox.getChildren().add(headerRow);

        if (cart == null || cart.isEmpty()) {
            Label emptyCartLabel = new Label("Your cart is empty.");
            emptyCartLabel.setStyle("-fx-padding: 20px; -fx-font-size: 16px; -fx-text-fill: #666;");
            cartItemBox.getChildren().add(emptyCartLabel);
            return;
        }

        for (Booked bookedItem : new ArrayList<>(cart)) {
            HBox row = new HBox();
            boolean isDateBookable = DateUtil.isEventBookable(bookedItem.getDay());
            boolean isStockSufficient = true;
            String unavailabilityReason = "";

            Event eventInSessionList = findEvent(bookedItem);
            if (eventInSessionList != null) {
                int availableSeats = eventInSessionList.getTotal() - eventInSessionList.getSold();
                isStockSufficient = bookedItem.getQuantity() <= availableSeats;
                if (!isStockSufficient) {
                    unavailabilityReason = "Not enough seats available. Requested: " + bookedItem.getQuantity() + ", Available: " + availableSeats;
                }
            } else {
                isStockSufficient = false;
                unavailabilityReason = "Event details no longer found in current event listing.";
            }

            if (!isDateBookable) {
                unavailabilityReason = (unavailabilityReason.isEmpty() ? "" : unavailabilityReason + " ") +
                        "Event day (" + bookedItem.getDay() + ") is no longer bookable this week.";
            }

            boolean isFullyAvailable = isDateBookable && isStockSufficient;

            // Style row based on availability
            if (isFullyAvailable) {
                row.setStyle("-fx-padding: 10px; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
            } else {
                row.setStyle("-fx-padding: 10px; -fx-border-color: #f44336; -fx-border-width: 2px; -fx-background-color: #ffebee;");
                if (!unavailabilityReason.isEmpty()) {
                    Tooltip.install(row, new Tooltip(unavailabilityReason));
                }
            }

            Label eventName = new Label(bookedItem.getName()); eventName.setPrefWidth(100); eventName.setStyle("-fx-font-size: 12px;");
            Label venueName = new Label(bookedItem.getVenue()); venueName.setPrefWidth(100); venueName.setStyle("-fx-font-size: 12px;");
            Label day = new Label(bookedItem.getDay()); day.setPrefWidth(100); day.setStyle("-fx-font-size: 12px;");
            Label quantity = new Label(String.valueOf(bookedItem.getQuantity())); quantity.setPrefWidth(60); quantity.setStyle("-fx-font-size: 12px; -fx-alignment: center;");
            Label price = new Label(String.format("$%.2f", bookedItem.getPrice())); price.setPrefWidth(80); price.setStyle("-fx-font-size: 12px;");
            Label total = new Label(String.format("$%.2f", bookedItem.getPrice() * bookedItem.getQuantity())); total.setPrefWidth(80); total.setStyle("-fx-font-size: 12px;");

            if (!isFullyAvailable) {
                quantity.setStyle("-fx-font-size: 12px; -fx-alignment: center; -fx-text-fill: #f44336; -fx-font-weight: bold;");
                if (!isDateBookable) {
                    day.setStyle(day.getStyle() + " -fx-strikethrough: true; -fx-text-fill: #f44336;");
                }
            }

            Button removeButton = new Button("Remove");
            removeButton.setPrefWidth(80); removeButton.setPrefHeight(25);
            removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2px;");

            removeButton.setOnAction(e -> {
                Event eventInSession = findEvent(bookedItem);
                int quantityBeingRemoved = 0;

                if (bookedItem.getQuantity() > 1) {
                    bookedItem.setQuantity(bookedItem.getQuantity() - 1);
                    quantityBeingRemoved = 1;
                } else {
                    quantityBeingRemoved = bookedItem.getQuantity();
                    cart.remove(bookedItem);
                }

                if (eventInSession != null) {
                    eventInSession.setSold(Math.max(0, eventInSession.getSold() - quantityBeingRemoved));
                    System.out.println("CartController: Decremented sold count for " + eventInSession.getName() + " by " + quantityBeingRemoved + ". New sold: " + eventInSession.getSold());
                } else {
                    System.err.println("CartController: Error! Could not find event " + bookedItem.getName() + " in session to update sold count on removal.");
                }
                displayCartItems();
                updateTotalCost();
                validateCartAvailability();
            });

            row.getChildren().addAll(eventName, venueName, day, quantity, price, total, removeButton);
            cartItemBox.getChildren().add(row);
        }
    }

    private void updateTotalCost() {
        double totalAmount = 0.0;
        if (cart != null) {
            for (Booked item : cart) {
                totalAmount += item.getPrice() * item.getQuantity();
            }
        }
        totalCostLabel.setText(String.format("$%.2f", totalAmount));
    }

    private boolean checkEventAvailability(Booked cartItem) {
        if (!DateUtil.isEventBookable(cartItem.getDay())) {
            return false;
        }
        Event eventInSessionList = findEvent(cartItem);
        if (eventInSessionList != null) {
            int availableSeats = eventInSessionList.getTotal() - eventInSessionList.getSold();
            return cartItem.getQuantity() <= availableSeats;
        }
        return false;
    }

    private void validateCartAvailability() {
        ArrayList<String> unavailableItemsMessages = new ArrayList<>();
        boolean hasUnavailableItems = false;
        boolean isCartEmpty = (cart == null || cart.isEmpty());

        if (!isCartEmpty) {
            for (Booked cartItem : new ArrayList<>(cart)) {
                if (!checkEventAvailability(cartItem)) {
                    hasUnavailableItems = true;
                    Event event = findEvent(cartItem);
                    String itemName = cartItem.getName() + " at " + cartItem.getVenue() + " on " + cartItem.getDay();
                    if (!DateUtil.isEventBookable(cartItem.getDay())) {
                        unavailableItemsMessages.add(String.format("%s: No longer bookable (event day %s has passed this week).",
                                itemName, cartItem.getDay()));
                    } else if (event != null) {
                        int available = event.getTotal() - event.getSold();
                        unavailableItemsMessages.add(String.format("%s: Requested %d, Available %d.",
                                itemName, cartItem.getQuantity(), available));
                    } else {
                        unavailableItemsMessages.add(String.format("%s: Event details not found or event is no longer bookable.",
                                itemName));
                    }
                }
            }
        }

        if (checkoutButton != null) {
            checkoutButton.setDisable(hasUnavailableItems || checkoutInProgress || isCartEmpty);
        }

        if (hasUnavailableItems) {
            showAvailabilityWarning(unavailableItemsMessages);
            displayCartItems();
            updateTotalCost();
        }
    }

    private Event findEvent(Booked cartItem) {
        ArrayList<Event> eventList = Session.getInstance().getEventList();
        if (eventList == null) return null;
        for (Event event : eventList) {
            if (event.getName().equals(cartItem.getName()) &&
                    event.getVenue().equals(cartItem.getVenue()) &&
                    event.getDay().equals(cartItem.getDay())) {
                return event;
            }
        }
        System.err.println("CartController: findEvent could not find matching event in session for: " + cartItem.getName());
        return null;
    }

    private void showAvailabilityWarning(ArrayList<String> unavailableItems) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Availability Warning");
        alert.setHeaderText("Some items in your cart have issues:");
        StringBuilder message = new StringBuilder();
        for (String item : unavailableItems) {
            message.append("• ").append(item).append("\n");
        }
        message.append("\nPlease adjust quantities or remove items before checkout.");
        alert.setContentText(message.toString());
        alert.showAndWait();
    }

    @FXML
    private void handleCheckout() {
        if (checkoutInProgress) return;
        if (cart == null || cart.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Empty Cart");
            alert.setHeaderText("Your cart is empty.");
            alert.setContentText("Please add items to your cart before checking out.");
            alert.showAndWait();
            return;
        }

        validateCartAvailability();
        if (checkoutButton.isDisabled() && !cart.isEmpty()) {
            resetCheckoutState();
            return;
        }

        checkoutInProgress = true;
        checkoutButton.setDisable(true);

        try {
            boolean canCheckout = true;
            ArrayList<String> finalUnavailableMessages = new ArrayList<>();
            for (Booked cartItem : new ArrayList<>(cart)) {
                if (!checkEventAvailability(cartItem)) {
                    canCheckout = false;
                    Event event = findEvent(cartItem);
                    if (!DateUtil.isEventBookable(cartItem.getDay())) {
                        finalUnavailableMessages.add(String.format("%s on %s: No longer bookable this week.", cartItem.getName(), cartItem.getDay()));
                    } else if (event != null) {
                        int available = event.getTotal() - event.getSold();
                        finalUnavailableMessages.add(String.format("%s: Requested %d, Available %d.", cartItem.getName(), cartItem.getQuantity(), available));
                    } else {
                        finalUnavailableMessages.add(String.format("%s: No longer available.", cartItem.getName()));
                    }
                }
            }

            if (!canCheckout) {
                showAvailabilityWarning(finalUnavailableMessages);
                resetCheckoutState();
                return;
            }

            double totalCost = getTotal();
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Checkout");
            confirmAlert.setHeaderText("Confirm your order");
            confirmAlert.setContentText(String.format("Total Cost: $%.2f\n\nProceed with checkout?", totalCost));

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    handlePaymentValidation(totalCost);
                } else {
                    resetCheckoutState();
                }
            });

        } catch (Exception e) {
            System.err.println("Error during handleCheckout: " + e.getMessage());
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "An unexpected error occurred during checkout: " + e.getMessage());
            errorAlert.showAndWait();
            resetCheckoutState();
        }
    }

    private void resetCheckoutState() {
        checkoutInProgress = false;
        validateCartAvailability();
    }

    private void handlePaymentValidation(double totalCost) {
        showPaymentDialog(totalCost, 1, 3);
    }

    private void showPaymentDialog(double totalCost, int attempt, int maxAttempts) {
        TextInputDialog paymentDialog = new TextInputDialog();
        paymentDialog.setTitle("Payment Confirmation");
        paymentDialog.setHeaderText(String.format("Payment Required: $%.2f (Attempt %d of %d)", totalCost, attempt, maxAttempts));
        paymentDialog.setContentText("Enter your 6-digit payment confirmation code:");
        paymentDialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*") || newValue.length() > 6) {
                paymentDialog.getEditor().setText(oldValue);
            }
        });
        paymentDialog.showAndWait().ifPresentOrElse(
                code -> processPaymentCode(code, totalCost, attempt, maxAttempts),
                () -> {
                    Alert cancelAlert = new Alert(Alert.AlertType.INFORMATION);
                    cancelAlert.setTitle("Payment Cancelled");
                    cancelAlert.setHeaderText("Payment was cancelled");
                    cancelAlert.setContentText("Your items remain in the cart.");
                    cancelAlert.showAndWait();
                    resetCheckoutState();
                }
        );
    }

    private void processPaymentCode(String code, double totalCost, int attempt, int maxAttempts) {
        if (!isValidConfirmationCode(code)) {
            showPaymentError("Invalid code format. Please enter exactly 6 digits.", attempt, maxAttempts, totalCost);
            return;
        }
        if (validatePaymentCode(code)) {
            processSuccessfulPayment(totalCost);
        } else {
            showPaymentError("Invalid confirmation code.", attempt, maxAttempts, totalCost);
        }
    }

    private void showPaymentError(String message, int attempt, int maxAttempts, double totalCost) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Payment Failed");
        errorAlert.setHeaderText("Invalid Confirmation Code");
        String fullMessage = message;
        if (attempt < maxAttempts) {
            fullMessage += String.format("\n\nAttempt %d of %d.", attempt, maxAttempts);
        } else {
            fullMessage += "\n\nMaximum attempts reached. Please try checkout again later.";
            resetCheckoutState();
        }
        fullMessage += "\n\nFor demo purposes, try: 123456, 999999, 000000, or 555555";
        errorAlert.setContentText(fullMessage);

        if (attempt < maxAttempts) {
            errorAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    showPaymentDialog(totalCost, attempt + 1, maxAttempts);
                }
            });
        } else {
            errorAlert.showAndWait();
        }
    }

    private boolean isValidConfirmationCode(String code) {
        return VALID_CODE_PATTERN.matcher(code).matches();
    }

    private boolean validatePaymentCode(String code) {
        for (String validCode : DEMO_VALID_CODES) {
            if (validCode.equals(code)) return true;
        }
        return false;
    }

    private void processSuccessfulPayment(double totalCost) {
        completeCheckout();

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Booking Confirmed");
        successAlert.setHeaderText("Your booking has been completed successfully!");
        String successMessage = String.format(
                "Booking Details:\n" +
                        "Total Amount: $%.2f\n" +
                        "Date: %s\n\n" +
                        "Thank you for your purchase!",
                totalCost,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        successAlert.setContentText(successMessage);
        successAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Session.getInstance().changeScene("/further_prog/assignment_2/Dashboard/Dashboard.fxml");
                } catch (IOException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Navigation Error");
                    errorAlert.setContentText("Could not return to dashboard: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
        });
    }

    private double getTotal() {
        double totalCost = 0.0;
        if (cart != null) {
            for (Booked item : cart) {
                totalCost += item.getPrice() * item.getQuantity();
            }
        }
        return totalCost;
    }

    public void completeCheckout() {
        System.out.println("Completing checkout. Persisting event data and creating order.");

        // Save event data
        ArrayList<Event> eventsToSave = Session.getInstance().getEventList();
        if (eventsToSave != null && !eventsToSave.isEmpty()) {
            try {
                EventWriter writer = new EventWriter();
                writer.writeEventsToFile("src/main/java/further_prog/assignment_2/FileManipulators/events.dat", eventsToSave);
                System.out.println("Event data successfully updated in events.dat.");
            } catch (Exception e) {
                System.err.println("Error updating events.dat file during checkout: " + e.getMessage());
                e.printStackTrace();
                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("File Update Warning");
                warningAlert.setHeaderText("Event data update issue");
                warningAlert.setContentText("There was an issue saving updated event availability. The booking will proceed, but please contact support if event listings appear incorrect later.");
                warningAlert.showAndWait();
            }
        } else {
            System.out.println("No events in session to save, or event list is empty.");
        }

        // Create order
        Users currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getName() != null && !currentUser.getName().trim().isEmpty() &&
                cart != null && !cart.isEmpty()) {

            ArrayList<Booked> itemsForOrder = new ArrayList<>();
            for (Booked cartItem : cart) {
                Booked orderItem = new Booked(cartItem.getName(), cartItem.getVenue(),
                        cartItem.getDay(), cartItem.getPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                itemsForOrder.add(orderItem);
            }
            double orderTotal = getTotal();

            OrderHistoryManager.getInstance().createOrder(itemsForOrder, orderTotal, currentUser.getName());
            System.out.println("CartController: Order processing complete for user " + currentUser.getName());

        } else {
            if (currentUser == null || currentUser.getName() == null || currentUser.getName().trim().isEmpty()) {
                System.err.println("CartController: Cannot save order - current user is not available or username is invalid.");
            }
            if (cart == null || cart.isEmpty()) {
                System.err.println("CartController: Cart is empty, no order to save.");
            }
        }

        // Clear cart
        if (Session.getInstance().getUserCart() != null) {
            Session.getInstance().getUserCart().clear();
        }
        if (this.cart != null) {
            this.cart.clear();
        }

        checkoutInProgress = false;
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            Session.getInstance().changeScene("/further_prog/assignment_2/Dashboard/Dashboard.fxml");
        } catch (IOException e) {
            System.err.println("CartController: Error navigating back to dashboard: " + e.getMessage());
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Navigation Error");
            errorAlert.setHeaderText("Failed to return to dashboard");
            errorAlert.setContentText("There was an issue navigating back: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    @FXML
    private void handleLogout() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Logout Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to logout?");
        confirmationAlert.setContentText("Any items in your cart will be cleared if not checked out.");

        ButtonType logoutButtonType = new ButtonType("Logout");
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());
        confirmationAlert.getButtonTypes().setAll(logoutButtonType, cancelButtonType);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == logoutButtonType) {
            try {
                Session session = Session.getInstance();
                session.clearSession();

                Alert logoutSuccessAlert = new Alert(Alert.AlertType.INFORMATION);
                logoutSuccessAlert.setTitle("Logout Successful");
                logoutSuccessAlert.setHeaderText("You have been logged out successfully");
                logoutSuccessAlert.setContentText("Thank you for using our system!");
                logoutSuccessAlert.showAndWait();

                session.changeScene("/further_prog/assignment_2/Main/LoginPage.fxml");

            } catch (IOException e) {
                System.err.println("CartController: Error during logout: " + e.getMessage());
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Logout Error");
                errorAlert.setHeaderText("Failed to logout properly");
                errorAlert.setContentText("There was an issue during logout: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }
}