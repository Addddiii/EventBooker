package further_prog.assignment_2.FileManipulators;

import further_prog.assignment_2.Dashboard.Event;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList; // If taking list as parameter

public class EventWriter {
    // Method to write events from Session
    public void writeEventsToFile(String filePath, ArrayList<Event> events) throws IOException {
        if (events == null) {
            System.err.println("no events. Cannot write to file.");
            return;
        }
        writeEvents(filePath, events);
    }

    private void writeEvents(String filePath, ArrayList<Event> events) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) { // false to overwrite
            for (Event event : events) {
                bw.write(event.getName() + ";" +
                        event.getVenue() + ";" +
                        event.getDay() + ";" +
                        event.getPrice() + ";" +
                        event.getSold() + ";" +
                        event.getTotal() + ";" +
                        event.isEnabled()); // Save the isEnabled status had to edit original event.dat file to accomodate this another way imo would be way too complex
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing events to file: " + filePath + " - " + e.getMessage());
            throw e; // Re-throw for the caller to handle if necessary
        }
    }
}