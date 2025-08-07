package further_prog.assignment_2.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserControl {

    //  Private static list accessible from other classes for encapsulation
    private static final List<Users> users = new ArrayList<>();

    // Static initializer block runs once even before main
    static {
        users.addAll(Arrays.asList(
                new Users("user1", "password1", "Standard User"), // Added "Standard User" as type
                new Users("user2", "password2", "Standard User"), // Added "Standard User" as type
                new Users("user3", "password3", "Standard User")  // Added "Standard User" as type
        ));

    }

    // Public getter for accessing the users list
    public static List<Users> getUsers() {
        return users;
    }
}