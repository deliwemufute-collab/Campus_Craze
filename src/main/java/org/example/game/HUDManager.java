package org.example.game;

import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// This object is going to handle the info
// displayed to player (Lifes, score, etc..)
// VICTORIA ADEOSUN realm too!
public class HUDManager {

    private Pane hudPane;

    // UI elements placeholders
    private Label scoreLabel;
    private Label livesLabel;
    private Label coinsLabel;

    // heart images for lives display
    private ImageView heart1;
    private ImageView heart2;
    private ImageView heart3;
    private ImageView heart4;
    private ImageView heart5;

    private Runnable onMute;
    private Runnable onPause;
    private ImageView muteBtn;
    private ImageView pauseBtn;

    // Constructor
    public HUDManager(Pane pane) {
        this.hudPane = pane;

        // Initialize placeholders
        this.scoreLabel = new Label(String.format("Score: %06d", 0));
        this.livesLabel = new Label("Lives: 5");
        this.coinsLabel = new Label("Coins: 0");
    }

    // Setup HUD layout
    public void setupHUD() {
        scoreLabel.setLayoutX(10);
        scoreLabel.setLayoutY(10);
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        coinsLabel.setLayoutX(10);
        coinsLabel.setLayoutY(35);
        coinsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // load the heart image and create 5 hearts for the 5 lives
        Image heartImage = new Image(getClass().getResourceAsStream("/Menu_assets/heart.png"));

        heart1 = new ImageView(heartImage);
        heart1.setFitWidth(30);
        heart1.setFitHeight(30);
        heart1.setLayoutX(10);
        heart1.setLayoutY(65);

        heart2 = new ImageView(heartImage);
        heart2.setFitWidth(30);
        heart2.setFitHeight(30);
        heart2.setLayoutX(45);
        heart2.setLayoutY(65);

        heart3 = new ImageView(heartImage);
        heart3.setFitWidth(30);
        heart3.setFitHeight(30);
        heart3.setLayoutX(80);
        heart3.setLayoutY(65);

        heart4 = new ImageView(heartImage);
        heart4.setFitWidth(30);
        heart4.setFitHeight(30);
        heart4.setLayoutX(115);
        heart4.setLayoutY(65);

        heart5 = new ImageView(heartImage);
        heart5.setFitWidth(30);
        heart5.setFitHeight(30);
        heart5.setLayoutX(150);
        heart5.setLayoutY(65);

        // load mute button for in game
        Image muteImage = new Image(getClass().getResourceAsStream("/Menu_assets/mute_button_game.png"));
        muteBtn = new ImageView(muteImage);
        muteBtn.setFitWidth(40);
        muteBtn.setFitHeight(40);
        muteBtn.setLayoutX(970);
        muteBtn.setLayoutY(10);
        muteBtn.setOnMouseEntered(e -> muteBtn.setOpacity(0.8));
        muteBtn.setOnMouseExited(e -> muteBtn.setOpacity(1.0));

        // load pause button for in game
        Image pauseImage = new Image(getClass().getResourceAsStream("/Menu_assets/pause_button.png"));
        pauseBtn = new ImageView(pauseImage);
        pauseBtn.setFitWidth(40);
        pauseBtn.setFitHeight(40);
        pauseBtn.setLayoutX(920);
        pauseBtn.setLayoutY(10);
        pauseBtn.setOnMouseEntered(e -> pauseBtn.setOpacity(0.8));
        pauseBtn.setOnMouseExited(e -> pauseBtn.setOpacity(1.0));

        hudPane.getChildren().addAll(scoreLabel, coinsLabel, heart1, heart2, heart3, heart4, heart5, muteBtn, pauseBtn);
    }

    // Update HUD values
    // format score with leading zeros like an arcade game
    public void updateScore(int score) {
        scoreLabel.setText(String.format("Score: %06d", score));
    }

    // hide hearts based on how many lives are left
    public void updateLives(int lives) {
        livesLabel.setText("Lives: " + lives);
        if (heart1 != null) heart1.setVisible(lives >= 1);
        if (heart2 != null) heart2.setVisible(lives >= 2);
        if (heart3 != null) heart3.setVisible(lives >= 3);
        if (heart4 != null) heart4.setVisible(lives >= 4);
        if (heart5 != null) heart5.setVisible(lives >= 5);
    }


    public void updateCoins(int coins) { coinsLabel.setText("Coins: " + coins); }


    //Setters

    // lets GameScene pass in what happens when mute is clicked
    public void setOnMute(Runnable onMute) {
        this.onMute = onMute;
        muteBtn.setOnMouseClicked(e -> {
            if (onMute != null) onMute.run();
        });
    }

    // lets GameScene pass in what happens when pause is clicked
    public void setOnPause(Runnable onPause) {
        this.onPause = onPause;
        pauseBtn.setOnMouseClicked(e -> {
            if (onPause != null) onPause.run();
        });
    }

    // Getters
    public Pane getHudPane() {
        return hudPane;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    public Label getLivesLabel() {
        return livesLabel;
    }

    public Label getCoinsLabel() {
        return coinsLabel;
    }
}