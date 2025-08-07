package further_prog.assignment_2.FileManipulators;

import further_prog.assignment_2.Dashboard.Event;
import further_prog.assignment_2.Main.Session;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class EventReader {

    public void eventReader(String filePath) {
        ArrayList<Event> eventList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(";");
                if (attributes.length >= 7) { // Ensure all fields are present
                    try {
                        String name = attributes[0].trim();
                        String venue = attributes[1].trim();
                        String day = attributes[2].trim();
                        double price = Double.parseDouble(attributes[3].trim());
                        int sold = Integer.parseInt(attributes[4].trim());
                        int total = Integer.parseInt(attributes[5].trim());
                        boolean isEnabled = Boolean.parseBoolean(attributes[6].trim());

                        Event event = new Event(name, venue, day, price, sold, total);
                        event.setEnabled(isEnabled); // Set the loaded enabled status
                        eventList.add(event);

                    } catch (Exception e) {
                        System.err.println("error: " + line + " - " + e.getMessage());
                    }
                } else {
                    System.err.println("not enough attributes: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading events.dat: " + e.getMessage());
        }
        Session.getInstance().setEventList(eventList);
    }

}