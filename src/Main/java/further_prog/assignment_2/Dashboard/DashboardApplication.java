package further_prog.assignment_2.Dashboard;

import further_prog.assignment_2.Main.Session;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class DashboardApplication extends Application {

    private static Stage stg;
    @Override
    public void start(Stage stage) throws IOException {
        Session.getInstance().setStage(stage);
//        stage.setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(DashboardApplication.class.getResource("Dashboard.fxml"));
       DashboardController dc = new DashboardController();

        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Event booker");
        stage.setScene(scene);
        stage.show();

    }


    public static void main(String[] args) {
        launch();
    }
}
