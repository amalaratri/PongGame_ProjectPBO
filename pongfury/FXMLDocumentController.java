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

abstract class GameEntity {
    protected double xPos;
    protected double yPos;
    protected static final double WIDTH = 15;
    protected static final double HEIGHT = 100;

    public GameEntity(double xPos, double yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public abstract void update();
    public abstract void draw(GraphicsContext gc);
}

class Paddle extends GameEntity {
    final double screenHeight;
    private final PaddleType type;

    public enum PaddleType {
        PLAYER, COMPUTER
    }

    public Paddle(double xPos, double yPos, double screenHeight, PaddleType type) {
        super(xPos, yPos);
        this.screenHeight = screenHeight;
        this.type = type;
    }

    @Override
    public void update() {
        // Base update method, can be overridden by subclasses
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(xPos, yPos, WIDTH, HEIGHT);
    }

    public void moveTo(double newYPos) {
        yPos = Math.max(0, Math.min(screenHeight - HEIGHT, newYPos));
    }
}

class ComputerPaddle extends Paddle {
    public Ball ball;
    private final GameDifficulty difficulty;

    public ComputerPaddle(double xPos, double yPos, double screenHeight, Ball ball, GameDifficulty difficulty) {
        super(xPos, yPos, screenHeight, PaddleType.COMPUTER);
        this.ball = ball;
        this.difficulty = difficulty;
    }

  @Override
public void update() {
    // Hanya bergerak ketika bola berada di sebelah kanan layar
    if (ball.getXPos() > GameSettings.SCREEN_WIDTH / 2) {
        // Hitung posisi prediksi bola
        double predictedY = ball.getYPos() + 
            (xPos - ball.getXPos()) * (ball.getYSpeed() / ball.getXSpeed());
        
        // Pastikan prediksi y dalam batas layar
        predictedY = Math.max(0, Math.min(screenHeight - HEIGHT, predictedY));
        
        double centerPaddle = yPos + HEIGHT / 2;
        double movementSpeed;
        double accuracyFactor;

        // Sesuaikan kecepatan dan akurasi berdasarkan kesulitan
        switch (difficulty) {
            case EASY:
                movementSpeed = 0.1; // Gerakan lambat
                accuracyFactor = 0.5; // Kurang presisi
                break;
            case MEDIUM:
                movementSpeed = 0.2; // Gerakan sedang
                accuracyFactor = 0.7; // Cukup presisi
                break;
            case HARD:
                movementSpeed = 0.3; // Gerakan cepat
                accuracyFactor = 0.9; // Sangat presisi
                break;
            default:
                movementSpeed = 0.2;
                accuracyFactor = 0.7;
        }

        // Tambahkan sedikit gangguan untuk membuat gerakan tidak terlalu sempurna
        double randomOffset = (new Random().nextDouble() - 0.5) * (1.0 - accuracyFactor) * 50;
        
        // Hitung target y dengan offset
        double targetY = predictedY + randomOffset;
        
        // Hitung perpindahan paddle
        double movementDistance = (targetY - centerPaddle) * movementSpeed * accuracyFactor;
        
        // Perbaharui posisi paddle
        yPos += movementDistance;
        
        // Pastikan paddle tidak keluar dari batas layar
        yPos = Math.max(0, Math.min(screenHeight - HEIGHT, yPos));
    }
}

    private double calculatePredictedPosition() {
        double predictedY = ball.getYPos() + 
            (xPos - ball.getXPos()) * (ball.getYSpeed() / ball.getXSpeed());
        
        // Apply difficulty-based accuracy and movement
        return applyDifficultyModifiers(predictedY);
    }

    private double applyDifficultyModifiers(double predictedY) {
        double movementSpeed = difficulty.getMovementSpeed();
        double accuracyFactor = difficulty.getAccuracyFactor();
        
        double randomOffset = (new Random().nextDouble() - 0.5) * (1.0 - accuracyFactor) * 50;
        double centerPaddle = yPos + HEIGHT / 2;
        
        double targetY = predictedY + randomOffset;
        double movementDistance = (targetY - centerPaddle) * movementSpeed * accuracyFactor;
        
        return centerPaddle + movementDistance;
    }
}

class Ball extends GameEntity {
    private double xSpeed;
    private double ySpeed;
    public static final double RADIUS = 15;

    public Ball(double xPos, double yPos) {
        super(xPos, yPos);
        randomizeInitialSpeed();
    }

    private void randomizeInitialSpeed() {
        xSpeed = new Random().nextInt(2) == 0 ? 2 : -2;
        ySpeed = new Random().nextInt(2) == 0 ? 2 : -2;
    }

    @Override
    public void update() {
        xPos += xSpeed;
        yPos += ySpeed;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillOval(xPos, yPos, RADIUS, RADIUS);
    }

    // Getters and setters
    public double getXSpeed() { return xSpeed; }
    public double getYSpeed() { return ySpeed; }
    public double getXPos() { return xPos; }
    public double getYPos() { return yPos; }
    public void setXSpeed(double xSpeed) { this.xSpeed = xSpeed; }
    public void setYSpeed(double ySpeed) { this.ySpeed = ySpeed; }
    
}

class GameSettings {
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;
    public static final double MAX_BALL_SPEED_EASY = 6;
    public static final double MAX_BALL_SPEED_MEDIUM = 8;
    public static final double MAX_BALL_SPEED_HARD = 10;
}

enum GameDifficulty {
    EASY(0.1, 0.5),
    MEDIUM(0.2, 0.7),
    HARD(0.3, 0.9);

    private final double movementSpeed;
    private final double accuracyFactor;

    GameDifficulty(double movementSpeed, double accuracyFactor) {
        this.movementSpeed = movementSpeed;
        this.accuracyFactor = accuracyFactor;
    }

    public double getMovementSpeed() { return movementSpeed; }
    public double getAccuracyFactor() { return accuracyFactor; }
}
