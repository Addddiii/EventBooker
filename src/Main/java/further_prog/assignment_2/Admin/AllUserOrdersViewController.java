package further_prog.assignment_2.Admin;

import further_prog.assignment_2.Dashboard.Booked;
import further_prog.assignment_2.Main.Session;
import further_prog.assignment_2.Order.OrderHistoryManager;
import further_prog.assignment_2.Order.OrderHistoryManager.Order;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AllUserOrdersViewController {

    @FXML
    private Accordion userOrdersAccordion;

    private static final String USER_ORDERS_DIR_PATH = "src/main/java/further_prog/assignment_2/FileManipulators/user_orders/";

    @FXML
    public void initialize() {
        loadAllUserOrders();
    }

    // Load and display orders for all users from order files
    private void loadAllUserOrders() {
        userOrdersAccordion.getPanes().clear();
        File userOrdersDir = new File(USER_ORDERS_DIR_PATH);
        File[] orderFiles = userOrdersDir.listFiles((dir, name) -> name.startsWith("orders_") && name.endsWith(".dat"));

        if (orderFiles == null || orderFiles.length == 0) {
            TitledPane noOrdersPane = new TitledPane("No Users", new Label("No user order files found."));
            userOrdersAccordion.getPanes().add(noOrdersPane);
            return;
        }

        OrderHistoryManager ohm = OrderHistoryManager.getInstance();

        for (File orderFile : orderFiles) {
            String filename = orderFile.getName();
            // get username from filename like "orders_user1.dat" -> "user1" need to change after advanced req
            String username = filename.substring("orders_".length(), filename.lastIndexOf(".dat"));

            ArrayList<Order> orders = ohm.loadOrdersForSpecificUser(username);

            if (orders != null && !orders.isEmpty()) {
                VBox ordersVBox = new VBox(5);
                ordersVBox.setStyle("-fx-padding: 10px;");
                for (Order order : orders) {
                    VBox orderDetailBox = new VBox(2);
                    orderDetailBox.setStyle("-fx-border-color: #dfe6e9; -fx-border-width: 0 0 1 0; -fx-padding: 5 0 5 0;");
                    Label orderNumLabel = new Label("Order #: " + order.getOrderNumber() + " (" + order.getFormattedDateTime() + ")");
                    orderNumLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
                    Label totalLabel = new Label("Total: " + order.getFormattedPrice() + " ("+ order.getTotalSeats() + " seats)");

                    VBox itemsBox = new VBox(2);
                    for(Booked item : order.getOrderedItems()){
                        Label itemLabel = new Label(String.format("  - %s at %s (%s) Qty: %d", item.getName(), item.getVenue(), item.getDay(), item.getQuantity()));
                        itemsBox.getChildren().add(itemLabel);
                    }
                    orderDetailBox.getChildren().addAll(orderNumLabel, itemsBox, totalLabel);
                    ordersVBox.getChildren().add(orderDetailBox);
                }
                TitledPane userPane = new TitledPane("Orders for " + username + " (" + orders.size() + ")", new ScrollPane(ordersVBox));
                userOrdersAccordion.getPanes().add(userPane);
            } else {
                TitledPane noOrdersUserPane = new TitledPane("Orders for " + username + " (0)", new Label("No orders found for this user."));
                userOrdersAccordion.getPanes().add(noOrdersUserPane);
            }
        }
        if (userOrdersAccordion.getPanes().isEmpty()){
            TitledPane noOrdersPane = new TitledPane("No Orders", new Label("No orders found across all users."));
            userOrdersAccordion.getPanes().add(noOrdersPane);
        }
    }

    @FXML
    private void handleBackToAdminDashboard() {
        try {
            Session.getInstance().changeScene("/further_prog/assignment_2/Admin/AdminDashboard.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}