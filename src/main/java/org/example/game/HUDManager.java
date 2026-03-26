package org.example.game;

import javafx.scene.layout.Pane;
import javafx.scene.control.Label;

// This object is going to handle the info
// displayed to player (Lifes, score, etc..)
// VICTORIA ADEOSUN realm too!
public class HUDManager {

    private Pane hudPane;

    // UI elements placeholders
    private Label scoreLabel;
    private Label livesLabel;
    private Label coinsLabel;

    // Constructor
    public HUDManager(Pane pane) {
        this.hudPane = pane;

        // Initialize placeholders
        this.scoreLabel = new Label("Score: 0");
        this.livesLabel = new Label("Lives: 3");
        this.coinsLabel = new Label("Coins: 0");
    }

    // Setup HUD layout
    public void setupHUD() {
        scoreLabel.setLayoutX(10);
        scoreLabel.setLayoutY(10);
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        livesLabel.setLayoutX(10);
        livesLabel.setLayoutY(35);
        livesLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");


        coinsLabel.setLayoutX(10);
        coinsLabel.setLayoutY(60);
        coinsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        hudPane.getChildren().addAll(scoreLabel, livesLabel, coinsLabel);
    }

    // Update HUD values
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateLives(int lives) {
        livesLabel.setText("Lives: " + lives);
    }

    public void updateCoins(int coins) { coinsLabel.setText("Coins: " + coins); }

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