package further_prog.assignment_2.Dashboard;

import java.io.Serializable;

public class Event implements Serializable {
    private static final long serialVersionUID = 2L; // Increment if changing class structure
    private String name;
    private String venue;
    private String day;
    private double price;
    private Integer sold;
    private Integer total;
    private boolean isEnabled; // New field for admin enable/disable

    public Event(String name, String venue, String day, double price, Integer sold, Integer total) {
        this.name = name;
        this.venue = venue;
        this.day = day;
        this.price = price;
        this.sold = sold;
        this.total = total;
        this.isEnabled = true; // Default to enabled
    }

    // Constructor for adding new events (sold is 0)
    public Event(String name, String venue, String day, double price, Integer total) {
        this.name = name;
        this.venue = venue;
        this.day = day;
        this.price = price;
        this.sold = 0; // New events have 0 sold
        this.total = total;
        this.isEnabled = true; // Default to enabled
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getSold() {
        return sold;
    }

    public void setSold(Integer sold) {
        this.sold = Math.max(0, sold); // Ensure sold is not negative
    }



    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = Math.max(0, total); // Ensure total is not negative
        if (this.sold > this.total) { // Adjust sold if total is reduced below sold
            this.sold = this.total;
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public int getAvailableSeats() {
        return Math.max(0, this.total - this.sold);
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", venue='" + venue + '\'' +
                ", day='" + day + '\'' +
                ", price=" + price +
                ", sold=" + sold +
                ", total=" + total +
                ", isEnabled=" + isEnabled +
                '}';
    }

    // For duplicate checking and finding events
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return name.equals(event.name) &&
                venue.equals(event.venue) &&
                day.equals(event.day);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + venue.hashCode();
        result = 31 * result + day.hashCode();
        return result;
    }
}