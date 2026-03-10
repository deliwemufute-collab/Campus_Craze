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

    // Constructor
    public HUDManager(Pane pane) {
        this.hudPane = pane;

        // Initialize placeholders
        this.scoreLabel = new Label("Score: 0");
        this.livesLabel = new Label("Lives: 3");
    }

    // Setup HUD layout
    public void setupHUD() {
        // To be implemented: position labels, add to hudPane
    }

    // Update HUD values (skeleton)
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateLives(int lives) {
        livesLabel.setText("Lives: " + lives);
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
}