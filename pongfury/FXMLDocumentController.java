package pongfury;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXMLDocumentController implements Initializable {


    private GameManager gameManager;
    private SoundManager soundManager;
    private GraphicsContext gc;
    @FXML
    private ImageView menu;
    @FXML
    private Canvas gameCanvas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gameCanvas.setWidth(GameSettings.SCREEN_WIDTH);
        gameCanvas.setHeight(GameSettings.SCREEN_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();

        soundManager = new SoundManager();
        gameManager = new GameManager(gc, soundManager);

        setupGameLoop();
        setupControls();
    }

    private void setupGameLoop() {
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), e -> gameManager.run()));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
    }

    private void setupControls() {
        gameCanvas.setOnMouseMoved(e -> gameManager.updatePlayerPosition(e.getY()));
        gameCanvas.setOnMouseClicked(e -> gameManager.startGame());
    }

    @FXML
    private void tekanSaya(MouseEvent event) {
        soundManager.stopSoundtrack();
        soundManager.playClickSound();
        navigateToMainMenu(event);
    }

    private void navigateToMainMenu(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainmenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Game Scene");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setDifficulty(GameDifficulty difficulty) {
        gameManager.setDifficulty(difficulty);
    }
    
}
