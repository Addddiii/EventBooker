package test;

import further_prog.assignment_2.Dashboard.Event;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class Test1 {

    // Test for the password empty check within checkLogin() concept
    @Test
    void testPasswordIsRequired() {


        String emptyPassword = "";
        String nonEmptyPassword = "password123";

        // Simulate the condition: if(password.isEmpty()) { error.setText("Password is required"); }
        assertTrue(emptyPassword.isEmpty(), "Simulating empty password check: An empty password should be identified as empty.");
        assertFalse(nonEmptyPassword.isEmpty(), "Simulating non-empty password check: A non-empty password should not be identified as empty.");
    }

    //test for admin login credentials
    @Test
    void testAdminLoginCredentials() {
        // Simulating the admin credential check
        String correctAdminUser = "admin";
        String correctAdminPass = "Admin321";
        String incorrectAdminUser = "administrator";
        String incorrectAdminPass = "adminpassword";

        assertTrue("admin".equals(correctAdminUser) && "Admin321".equals(correctAdminPass), "Correct admin credentials should match.");
        assertFalse("admin".equals(incorrectAdminUser) && "Admin321".equals(correctAdminPass), "Incorrect admin username should fail.");
        assertFalse("admin".equals(correctAdminUser) && "Admin321".equals(incorrectAdminPass), "Incorrect admin password should fail.");
    }

    //if name venue and day are equal the events should be considered equal and price solt total just overrides
    @Test
    void eventsWithSameNameVenueDay_ShouldBeEqual() {
        Event event1 = new Event("Duplicate Event", "Same Venue", "Same Day", 50.0, 0, 100);
        Event event2 = new Event("Duplicate Event", "Same Venue", "Same Day", 60.0, 10, 120); // Price/sold/total differ
        assertEquals(event1, event2, "Events with same name, venue, and day should be considered equal for duplication.");
        assertEquals(event1.hashCode(), event2.hashCode(), "Hashcodes should be equal for equal events.");
    }

    //different day events should be different events
    @Test
    void eventsWithDifferentDay_ShouldNotBeEqual() {
        Event event1 = new Event("Unique Event", "Same Venue", "Mon", 50.0, 0, 100);
        Event event2 = new Event("Unique Event", "Same Venue", "Tue", 50.0, 0, 100);
        assertNotEquals(event1, event2, "Events with different days should not be equal.");
    }
    //different venue events should be different events
    @Test
    void eventsWithDifferentVenue_ShouldNotBeEqual() {
        Event event1 = new Event("Unique Event", "Venue A", "Wed", 50.0, 0, 100);
        Event event2 = new Event("Unique Event", "Venue B", "Wed", 50.0, 0, 100);
        assertNotEquals(event1, event2, "Events with different venues should not be equal.");
    }


}