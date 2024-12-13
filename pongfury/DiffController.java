package pongfury;

import java.io.IOException;
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

public class DiffController implements Initializable {
    @FXML
    private ImageView easy;
    @FXML
    private ImageView med;
    @FXML
    private ImageView hard;
    
    // Variabel untuk menyimpan difficulty yang dipilih
    private GameDifficulty selectedDifficulty;
    
    public static MediaPlayer clk;
    public static final Media click = new Media(FXMLDocumentController.class.getResource("/pongfury/sounds/click.mp3").toExternalForm());

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Tambahkan event handler untuk setiap ImageView
        easy.setOnMouseClicked(this::tekanEasy);
        med.setOnMouseClicked(this::tekanMed);
        hard.setOnMouseClicked(this::tekanHard);
        
        clk = new MediaPlayer(click);
        clk.setVolume(1.0);
    }    

    @FXML
    private void tekanEasy(MouseEvent event) {
        clk.play();
        selectedDifficulty = GameDifficulty.EASY;
        memulaiPermainan();
    }

    @FXML
    private void tekanMed(MouseEvent event) {
        clk.play();
        selectedDifficulty = GameDifficulty.MEDIUM;
        memulaiPermainan();
    }

    @FXML
    private void tekanHard(MouseEvent event) {
        clk.play();
        selectedDifficulty = GameDifficulty.HARD;
        memulaiPermainan();
    }

    private void memulaiPermainan() {
        try {
            // Muat FXML untuk layar permainan utama
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent root = loader.load();
            
            // Dapatkan controller untuk permainan
            FXMLDocumentController gameController = loader.getController();
            
            // Atur difficulty di controller permainan menggunakan enum
            gameController.setDifficulty(selectedDifficulty);
            
            // Dapatkan stage saat ini
            Stage stage = (Stage) easy.getScene().getWindow();
            
            // Buat scene baru dan tampilkan
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Gagal memuat layar permainan: " + e.getMessage());
        }
    }
}