package pongfury;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PongFury extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Memuat file FXML
        Parent root = FXMLLoader.load(getClass().getResource("mainmenu.fxml"));
        
        // Membuat scene dan menambahkan ke stage
        Scene scene = new Scene(root);

        stage.setTitle("Pong Game");
        stage.setScene(scene);
        stage.show();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
