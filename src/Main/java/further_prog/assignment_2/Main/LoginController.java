package further_prog.assignment_2.Main;

import further_prog.assignment_2.FileManipulators.EventReader;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    public void login(Event event) throws IOException {
        checkLogin();
    }

    public void initialize(){
        EventReader e1 = new EventReader();
        // eventReader method directly populates Session.getInstance().getEventList()
        e1.eventReader("src/main/java/further_prog/assignment_2/FileManipulators/events.dat");


    }

    @FXML
    private void checkLogin() {


        String username = this.usernameField.getText().trim();
        String password = this.passwordField.getText();


        // Special Admin Login
        if ("admin".equals(username) && "Admin321".equals(password)) {
            Users adminUser = new Users("admin", "Admin321", "Administrator");
            adminUser.setRole("ADMIN");
            Session.getInstance().setCurrentUser(adminUser);
            if (errorLabel != null) errorLabel.setVisible(false);

            try {
                Session.getInstance().changeScene("/further_prog/assignment_2/Admin/AdminDashboard.fxml");
            } catch (IOException e) {
                System.err.println("IOException during admin scene change: " + e.getMessage());
                e.printStackTrace();
                if (errorLabel != null) {
                    errorLabel.setText("Error loading admin dashboard.");
                    errorLabel.setVisible(true);
                }
            }
            return;
        }



        // Normal User Login
        List<Users> users = UserControl.getUsers();

        if (username.isEmpty() || password.isEmpty()) { // Combined check
            if (errorLabel != null) {
                errorLabel.setText(username.isEmpty() && password.isEmpty() ? "Username and password are required" :
                        username.isEmpty() ? "Username is required" : "Password is required");
                errorLabel.setVisible(true);
            }
            return;
        } else {
            if (errorLabel != null) errorLabel.setVisible(false); // Clear previous error if fields are now filled
        }

        boolean loggedIn = false;
        if (users != null) {
            for (Users user : users) {
                if (username.equals(user.getName()) && password.equals(user.getPassword())) {
                    user.setRole("USER");
                    Session.getInstance().setCurrentUser(user);
                    if (errorLabel != null) errorLabel.setVisible(false);
                    try {
                        Session.getInstance().changeScene("/further_prog/assignment_2/Dashboard/Dashboard.fxml");
                    } catch (IOException e) {
                        System.err.println("IOException during user scene change: " + e.getMessage());
                        e.printStackTrace();
                        if (errorLabel != null) {
                            errorLabel.setText("Error loading dashboard.");
                            errorLabel.setVisible(true);
                        }
                    } catch (IllegalStateException e) {
                        System.err.println("IllegalStateException during user scene change (Stage might be null): " + e.getMessage());
                        e.printStackTrace();
                        if (errorLabel != null) {
                            errorLabel.setText("Application setup error (stage).");
                            errorLabel.setVisible(true);
                        }
                    }
                    loggedIn = true;
                    break;
                }
            }
        }


        if (!loggedIn) {
            if (errorLabel != null) {
                errorLabel.setText("Invalid username or password");
                errorLabel.setVisible(true);
            }
        }
    }
}