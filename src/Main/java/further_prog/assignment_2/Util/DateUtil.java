package further_prog.assignment_2.Util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {

    // Maps short day strings (e.g., "Mon") to DayOfWeek objects.
    private static final Map<String, DayOfWeek> dayStringMap = new HashMap<>();

    // Initializes dayStringMap.
    static {
        dayStringMap.put("Mon", DayOfWeek.MONDAY);
        dayStringMap.put("Tue", DayOfWeek.TUESDAY);
        dayStringMap.put("Wed", DayOfWeek.WEDNESDAY);
        dayStringMap.put("Thu", DayOfWeek.THURSDAY);
        dayStringMap.put("Fri", DayOfWeek.FRIDAY);
        dayStringMap.put("Sat", DayOfWeek.SATURDAY);
        dayStringMap.put("Sun", DayOfWeek.SUNDAY);
    }

    // Gets DayOfWeek from a short day string.
    public static DayOfWeek getDayOfWeekFromString(String dayString) {
        return dayStringMap.getOrDefault(dayString, null);
    }

    // An event is bookable if its day is today or later in the current week.
    public static boolean isEventBookable(String eventDayString) {
        LocalDate today = LocalDate.now();
        DayOfWeek currentDayOfWeek = today.getDayOfWeek();
        DayOfWeek eventDayOfWeek = getDayOfWeekFromString(eventDayString);

        if (eventDayOfWeek == null) {
            System.err.println("Unknown event day string: " + eventDayString + " - considering not bookable.");
            return false;
        }
        return eventDayOfWeek.getValue() >= currentDayOfWeek.getValue();
    }
}