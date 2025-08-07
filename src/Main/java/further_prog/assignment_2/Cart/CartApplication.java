package further_prog.assignment_2.Cart;

import further_prog.assignment_2.Main.Session;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class CartApplication extends Application {

    private static Stage stg;
    @Override
    public void start(Stage stage) throws IOException {
        Session.getInstance().setStage(stage);
        stage.setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(CartApplication.class.getResource("Cart.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle("Event booker");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
