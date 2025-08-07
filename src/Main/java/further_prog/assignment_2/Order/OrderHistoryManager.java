package further_prog.assignment_2.Order;

import further_prog.assignment_2.Dashboard.Booked;
import further_prog.assignment_2.Main.Session;
import further_prog.assignment_2.Main.Users;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;


public class OrderHistoryManager {
    private static final String USER_ORDERS_DIR = "src/main/java/further_prog/assignment_2/FileManipulators/user_orders/";
    private static final String ORDER_COUNTER_FILE = "src/main/java/further_prog/assignment_2/FileManipulators/order_counter.dat";


    private static OrderHistoryManager instance;
    private int orderCounter;


    public static class Order implements Serializable {
        private static final long serialVersionUID = 1L; // For serialization
        private String orderNumber;
        private LocalDateTime orderDateTime;
        private ArrayList<Booked> orderedItems;
        private double totalPrice;
        private String customerUsername;

        public Order(String orderNumber, LocalDateTime orderDateTime, ArrayList<Booked> orderedItems, double totalPrice, String customerUsername) {
            this.orderNumber = orderNumber;
            this.orderDateTime = orderDateTime;
            this.orderedItems = new ArrayList<>(orderedItems); // Create a copy of items
            this.totalPrice = totalPrice;
            this.customerUsername = customerUsername;
        }

        // Getters for order details
        public String getOrderNumber() { return orderNumber; }
        public LocalDateTime getOrderDateTime() { return orderDateTime; }
        public ArrayList<Booked> getOrderedItems() { return new ArrayList<>(orderedItems); }


        public String getFormattedDateTime() {
            if (this.orderDateTime == null) return "N/A";
            return this.orderDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        public String getFormattedPrice() {
            return String.format("$%.2f", totalPrice);
        }

        public int getTotalSeats() {
            int total = 0;
            if (orderedItems != null) {
                for (Booked item : orderedItems) {
                    total += item.getQuantity();
                }
            }
            return total;
        }

        @Override
        public String toString() {
            return "Order #" + orderNumber + " by " + customerUsername + " at " + getFormattedDateTime();
        }
    }

    // Private constructor for Singleton pattern
    private OrderHistoryManager() {
        File mainOrdersDir = new File(USER_ORDERS_DIR);
        if (!mainOrdersDir.exists()) {
            mainOrdersDir.mkdirs(); // Create directory if it doesn't exist
        }
        loadOrderCounter(); // Load the last order number
    }

    // Gets the single instance of OrderHistoryManager
    public static synchronized OrderHistoryManager getInstance() {
        if (instance == null) {
            instance = new OrderHistoryManager();
        }
        return instance;
    }

    // Generates the file path for a user's orders
    private String getUserOrderFilePath(String username) {
        String safeUsername = username.replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9_.-]", "");
        return USER_ORDERS_DIR + "orders_" + safeUsername + ".dat";
    }

    // Creates a new order and saves it
    public Order createOrder(ArrayList<Booked> cartItems, double totalPrice, String customerUsername) {
        if (customerUsername == null || customerUsername.trim().isEmpty() || cartItems == null || cartItems.isEmpty()) {
            System.err.println("OrderHistoryManager: Invalid parameters for createOrder.");
            return null;
        }

        String orderNumber = generateOrderNumber();
        LocalDateTime orderDateTime = LocalDateTime.now();

        ArrayList<Booked> orderSpecificItems = new ArrayList<>();
        for (Booked item : cartItems) {
            Booked orderItem = new Booked(item.getName(), item.getVenue(), item.getDay(), item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderSpecificItems.add(orderItem);
        }

        Order newOrder = new Order(orderNumber, orderDateTime, orderSpecificItems, totalPrice, customerUsername);

        ArrayList<Order> userOrdersList = loadOrdersForUser(customerUsername);
        userOrdersList.add(newOrder);
        userOrdersList.sort(Comparator.comparing(Order::getOrderDateTime, Comparator.nullsLast(Comparator.reverseOrder())));

        saveOrdersForUser(customerUsername, userOrdersList);
        saveOrderCounter();

        System.out.println("OrderHistoryManager: Created order " + orderNumber + " for " + customerUsername);
        return newOrder;
    }

    // Gets orders for the currently logged-in user
    public ArrayList<Order> getUserOrders() {
        Users currentUser = Session.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getName() == null || currentUser.getName().trim().isEmpty()) {
            System.err.println("OrderHistoryManager: No current user to get orders for.");
            return new ArrayList<>();
        }
        return loadOrdersForUser(currentUser.getName());
    }


    public ArrayList<Order> loadOrdersForSpecificUser(String username) {
        return loadOrdersForUser(username);
    }


    private ArrayList<Order> loadOrdersForUser(String username) {
        String filePath = getUserOrderFilePath(username);
        File file = new File(filePath);
        ArrayList<Order> orders = new ArrayList<>();

        if (!file.exists()) {
            return orders;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof ArrayList<?>) {
                @SuppressWarnings("unchecked")
                ArrayList<Order> loadedOrders = (ArrayList<Order>) obj;
                orders = loadedOrders;
            }
        } catch (InvalidClassException e) {
            System.err.println("OrderHistoryManager: Load error for '" + username + "'. Old data file? " + e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("OrderHistoryManager: Load error for '" + username + "': " + e.getMessage());
        }
        orders.sort(Comparator.comparing(Order::getOrderDateTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return orders;
    }

    // Saves a list of orders for a specific user to their file
    private void saveOrdersForUser(String username, ArrayList<Order> userOrdersList) {
        String filePath = getUserOrderFilePath(username);
        File file = new File(filePath);

        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(userOrdersList);
            }
        } catch (IOException e) {
            System.err.println("OrderHistoryManager: Save error for '" + username + "': " + e.getMessage());
        }
    }

    // Generates the next 4-digit order number
    private String generateOrderNumber() {
        orderCounter++;
        return String.format("%04d", orderCounter);
    }

    // Loads the order counter from its file
    private void loadOrderCounter() {
        File counterFile = new File(ORDER_COUNTER_FILE);
        File counterParentDir = counterFile.getParentFile();
        if (counterParentDir != null && !counterParentDir.exists()) {
            counterParentDir.mkdirs();
        }

        if (!counterFile.exists()) {
            orderCounter = 0;
            saveOrderCounter(); // Create file if it doesn't exist
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(counterFile))) {
            String line = reader.readLine();
            orderCounter = (line != null && !line.trim().isEmpty()) ? Integer.parseInt(line.trim()) : 0;
        } catch (IOException | NumberFormatException e) {
            System.err.println("OrderHistoryManager: Error loading order counter. Resetting to 0. " + e.getMessage());
            orderCounter = 0;
        }
    }

    // Saves the current order counter to its file
    private void saveOrderCounter() {
        File counterFile = new File(ORDER_COUNTER_FILE);
        File counterParentDir = counterFile.getParentFile();
        if (counterParentDir != null && !counterParentDir.exists()) {
            counterParentDir.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(counterFile))) {
            writer.write(String.valueOf(orderCounter));
        } catch (IOException e) {
            System.err.println("OrderHistoryManager: Error saving order counter: " + e.getMessage());
        }
    }
}