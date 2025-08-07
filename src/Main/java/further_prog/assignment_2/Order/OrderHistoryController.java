package further_prog.assignment_2.Order;

import further_prog.assignment_2.Dashboard.Booked;
import further_prog.assignment_2.Main.Session;
import further_prog.assignment_2.Order.OrderHistoryManager.Order;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class OrderHistoryController {
    @FXML
    private ScrollPane orderScrollPane;
    @FXML
    private VBox orderHistoryContainer;
    @FXML
    private Label noOrdersLabel;
    @FXML
    private Button backToDashboardButton;
    @FXML
    private Button exportOrdersButton;

    private ArrayList<Order> userOrders;

    @FXML
    public void initialize() {
        loadUserOrders(); // Load current user's orders
        displayOrders();  // Show them in the UI
        if (exportOrdersButton != null) {
            exportOrdersButton.setDisable(userOrders == null || userOrders.isEmpty());
        }
    }

    private void loadUserOrders() {
        try {
            userOrders = OrderHistoryManager.getInstance().getUserOrders();
        } catch (Exception e) {
            userOrders = new ArrayList<>();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Loading Error");
            errorAlert.setHeaderText("Failed to load order history");
            errorAlert.setContentText("There was an issue loading your order history. Please try again later.");
            errorAlert.showAndWait();
        }
    }

    private void displayOrders() {
        orderHistoryContainer.getChildren().clear();

        if (userOrders == null || userOrders.isEmpty()) {
            showNoOrdersMessage();
            if (exportOrdersButton != null) exportOrdersButton.setDisable(true);
            return;
        }

        if (exportOrdersButton != null) exportOrdersButton.setDisable(false);

        if (noOrdersLabel != null) {
            noOrdersLabel.setVisible(false);
            noOrdersLabel.setManaged(false);
        }

        createHeaderRow();

        for (Order order : userOrders) {
            createOrderRow(order);
        }
    }

    private void showNoOrdersMessage() {
        if (noOrdersLabel != null) {
            noOrdersLabel.setVisible(true);
            noOrdersLabel.setManaged(true);
            noOrdersLabel.setText("You haven't placed any orders yet.");
        } else {
            Label messageLabel = new Label("You haven't placed any orders yet.");
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666; -fx-padding: 50px;");
            orderHistoryContainer.getChildren().add(messageLabel);
        }
    }

    private void createHeaderRow() {
        HBox headerRow = new HBox();
        headerRow.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 10px; -fx-spacing: 10px;");

        Label orderNumberHeader = new Label("Order #");
        orderNumberHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        orderNumberHeader.setPrefWidth(100);

        Label dateTimeHeader = new Label("Date & Time");
        dateTimeHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        dateTimeHeader.setPrefWidth(180);

        Label eventsHeader = new Label("Events Booked (Quantity)");
        eventsHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        eventsHeader.setPrefWidth(350);

        Label totalPriceHeader = new Label("Total Price");
        totalPriceHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        totalPriceHeader.setPrefWidth(120);

        headerRow.getChildren().addAll(orderNumberHeader, dateTimeHeader, eventsHeader, totalPriceHeader);
        orderHistoryContainer.getChildren().add(headerRow);
    }

    private void createOrderRow(Order order) {
        HBox orderRow = new HBox();
        orderRow.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 1 0; -fx-padding: 10px; -fx-spacing: 10px; -fx-alignment: center-left;");

        Label orderNumber = new Label(order.getOrderNumber());
        orderNumber.setStyle("-fx-font-size: 12px;");
        orderNumber.setPrefWidth(100);

        Label dateTime = new Label(order.getFormattedDateTime());
        dateTime.setStyle("-fx-font-size: 12px;");
        dateTime.setPrefWidth(180);

        VBox eventsBox = new VBox();
        eventsBox.setStyle("-fx-spacing: 3px;");
        eventsBox.setPrefWidth(350);
        if (order.getOrderedItems() != null && !order.getOrderedItems().isEmpty()) {
            for (Booked item : order.getOrderedItems()) {
                Label eventLabel = new Label(String.format("• %s (%s) - Qty: %d",
                        item.getName(), item.getVenue(), item.getQuantity()));
                eventLabel.setStyle("-fx-font-size: 11px;");
                eventLabel.setWrapText(true);
                eventsBox.getChildren().add(eventLabel);
            }
        } else {
            Label noItemsLabel = new Label("(No items)");
            noItemsLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
            eventsBox.getChildren().add(noItemsLabel);
        }

        Label totalPrice = new Label(order.getFormattedPrice());
        totalPrice.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        totalPrice.setPrefWidth(120);

        orderRow.getChildren().addAll(orderNumber, dateTime, eventsBox, totalPrice);

        orderRow.setOnMouseEntered(e -> orderRow.setStyle(orderRow.getStyle() + " -fx-background-color: #f9f9f9;"));
        orderRow.setOnMouseExited(e -> orderRow.setStyle(orderRow.getStyle().replace(" -fx-background-color: #f9f9f9;", "")));

        orderHistoryContainer.getChildren().add(orderRow);
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            Session.getInstance().changeScene("/further_prog/assignment_2/Dashboard/Dashboard.fxml");
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Navigation Error");
            errorAlert.setHeaderText("Failed to return to dashboard");
            errorAlert.setContentText("There was an issue navigating back: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

}
