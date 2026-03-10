package org.example.game;

import javafx.scene.layout.Pane;

// Manages the gameplay screen
public class GameScene {
    private Pane gamePane;

    // References to main game objects
    private Player player;
    private SceneryTile[] sceneryTiles;
    private Obstacle[] obstacles;

    // Constructor
    public GameScene(Pane pane) {
        this.gamePane = pane;

        // To be loaded when game loops begins
        this.player = null;          // To be set by DELIWEE MUFUTE
        this.sceneryTiles = new SceneryTile[0]; // EMMANUEL AYOBANJO
        this.obstacles = new Obstacle[0];      // PAAKHI AGRAWALA
    }

    // Initialize game objects
    public void initializeGameObjects() {
        // To be implemented by VICTORIA ADEOSUN
    }

    // Start game loop.. I suppose I can do this once everyone is done... (MC)
    public void startGameLoop() {
        // To be implemented
    }

    // Update method called each frame
    public void update() {
        // To be implemented (scrolling, player movement, collisions)
    }

    // Getters
    public Pane getGamePane() {
        return gamePane;
    }
    public Player getPlayer() {
        return player;
    }
}