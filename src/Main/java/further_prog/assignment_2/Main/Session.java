package further_prog.assignment_2.Main;

import further_prog.assignment_2.Dashboard.Booked;
import further_prog.assignment_2.Dashboard.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Session {

    private static Session instance;
    private Stage primaryStage;
    private Users currentUser;
    private ArrayList<Booked> userCart;
    private ArrayList<Event> eventList;

    private Session() {
        userCart = new ArrayList<>();
        eventList = new ArrayList<>();
    }

    public static synchronized Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public Stage getStage() {
        return primaryStage;
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Users currentUser) {
        this.currentUser = currentUser;
    }

    public ArrayList<Booked> getUserCart() {
        return userCart;
    }

    public void setUserCart(ArrayList<Booked> cart) {
        this.userCart = (cart != null) ? cart : new ArrayList<>();
    }

    public void addToCart(Booked item) {
        if (item == null) return;

        boolean found = false;
        for (Booked cartItem : userCart) {
            if (cartItem.getName().equals(item.getName()) &&
                    cartItem.getVenue().equals(item.getVenue()) &&
                    cartItem.getDay().equals(item.getDay())) {
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                found = true;
                break;
            }
        }
        if (!found) {
            userCart.add(item);
        }
    }

    public void removeFromCart(Booked item) {
        if (item != null) {
            userCart.remove(item);
        }
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<Event> events) {
        this.eventList = (events != null) ? events : new ArrayList<>();
    }

    public void addToEventList(Event event) {
        if (event != null) {
            this.eventList.add(event);
        }
    }

    public void updateSoldCountForEvent(Event eventToUpdate, int quantityChange) {
        if (eventToUpdate == null) return;

        for (Event e : this.eventList) {
            if (e.getName().equals(eventToUpdate.getName()) &&
                    e.getVenue().equals(eventToUpdate.getVenue()) &&
                    e.getDay().equals(eventToUpdate.getDay())) {

                if (quantityChange > 0) {
                    for (int i = 0; i < quantityChange; i++) {
                        if (e.getSold() < e.getTotal()) {
                            e.setSold(e.getSold() + 1);
                        }
                    }
                } else if (quantityChange < 0) {
                    e.setSold(Math.max(0, e.getSold() + quantityChange));
                }
                break;
            }
        }
    }

    public void changeScene(String fxmlPath) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("PrimaryStage is not set.");
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        if (loader.getLocation() == null) {
            throw new IOException("FXML file not found: " + fxmlPath);
        }

        Parent pane = loader.load();

        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(pane));
        } else {
            primaryStage.getScene().setRoot(pane);
        }

        if (!primaryStage.isShowing()) {
            primaryStage.show();
        }
    }

    public void clearSession() {
        this.currentUser = null;
        if (this.userCart != null) {
            this.userCart.clear();
        }
        if (this.eventList != null) {
            this.eventList.clear();
        }
    }
}
