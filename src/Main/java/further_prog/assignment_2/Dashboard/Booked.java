package further_prog.assignment_2.Dashboard;

import java.io.Serializable;

public class Booked implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String venue;
    private String day;
    private double price;
    private int quantity;


    public Booked(String name, String venue, String day, double price) {
        this.name = name;
        this.venue = venue;
        this.day = day;
        this.price = price;
        this.quantity = 1; // Default quantity when first added from dashboard
    }

    // Constructor for creating copies or specific quantities (e.g., for orders)
    public Booked(String name, String venue, String day, double price, int quantity) {
        this.name = name;
        this.venue = venue;
        this.day = day;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getVenue() {
        return venue;
    }

    public String getDay() {
        return day;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Booked{" +
                "name='" + name + '\'' +
                ", venue='" + venue + '\'' +
                ", day='" + day + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}