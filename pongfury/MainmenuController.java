package pongfury;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class MainmenuController implements Initializable {

    @FXML
    private ImageView start;
    
    public static MediaPlayer clk;
    public static final Media click = new Media(FXMLDocumentController.class.getResource("/pongfury/sounds/click.mp3").toExternalForm());
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clk = new MediaPlayer(click);
        clk.setVolume(1.0);
    }
    
    @FXML
    private void tekanSaya(MouseEvent event) {
        clk.play();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("diff.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Game Scene");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
