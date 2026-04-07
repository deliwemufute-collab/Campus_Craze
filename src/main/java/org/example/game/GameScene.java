package org.example.game;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the gameplay screen.
 * Integrates SceneryManager for vertical background scrolling,
 * runs the main game loop, and stores all GameObjects in a list.
 *
 * Milestone 2 Architecture: uses GameObject parent class
 * and Collidable interface where applicable.
 */
public class GameScene {

    // --- Constants ---
    private final double screenWidth;
    private final double screenHeight;
    private static final double SCROLL_SPEED = 3.0;   // pixels per frame
    private static final int TILE_COUNT = 3;          // tiles needed to fill screen

    // --- Core pane ---
    private final Pane gamePane;

    // --- Game objects ---
    private Player player;
    private ObstacleManager obstacleManager;
    private SceneryManager sceneryManager;
    private HUDManager hudManager;
    private ScoreManager scoreManager;

    // --- Centralized game object list ---
    private final List<GameObject> gameObjects = new ArrayList<>();

    // --- Game loop ---
    private AnimationTimer gameLoop;

    // ===============================
    // Constructor
    // ===============================
    public GameScene(Pane pane, double width, double height) {
        this.gamePane = pane;
        this.screenWidth = width;
        this.screenHeight = height;
    }

    // ===============================
    // Initialization
    // ===============================
    public void initializeGameObjects() {
        setupScenery();
        setupPlayer();
        setupObstacles();
        setupHUD();
    }

    private void setupScenery() {
        Image[] sceneryVariants = new Image[]{
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_start.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_mid_1.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_mid_2.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_mid_3.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_mid_4.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_end.png"))
        };

        sceneryManager = new SceneryManager(gamePane, SCROLL_SPEED, screenHeight, screenWidth);
        sceneryManager.setSceneryVariants(sceneryVariants);

        double tileHeight = sceneryVariants[0].getHeight();
        for (int i = 0; i < TILE_COUNT; i++) {
            SceneryTile tile = sceneryManager.spawnTile(i * tileHeight);
            gameObjects.add(tile);
        }

        System.out.println("GameScene: scenery tiles spawned.");
    }

    private void setupPlayer() {
        Image playerImage = new Image(getClass().getResourceAsStream("/Player/player_dummy.png"));
        player = new Player(playerImage, screenWidth / 2, screenHeight - 150, screenWidth);
        gamePane.getChildren().add(player.getSprite());
        gameObjects.add(player);
    }

    private void setupObstacles() {
        obstacleManager = new ObstacleManager(gamePane);

        // Initial dummy obstacles
        Obstacle o1 = obstacleManager.spawnObstacle(380, 150);
        Obstacle o2 = obstacleManager.spawnObstacle(512, -100);
        Obstacle o3 = obstacleManager.spawnObstacle(640, -350);

        // Add to GameObjects
        gameObjects.add(o1);
        gameObjects.add(o2);
        gameObjects.add(o3);

        System.out.println("GameScene: dummy obstacles spawned.");
    }

    private void setupHUD() {
        hudManager = new HUDManager(gamePane);
        hudManager.setupHUD();

        // connected score manager to the HUD so it can update the labels
        scoreManager = new ScoreManager(hudManager);
    }

    // ===============================
    // Game Loop
    // ===============================
    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();

                // increase the score every second based on time survived
                if (now % 60 == 0) {
                    scoreManager.incrementScore();
                }
            }
        };
        gameLoop.start();
        System.out.println("GameScene: Game loop started.");
    }

    public void setupKeyboard(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (player == null) return;

            switch (e.getCode()) {
                case LEFT -> player.moveLeft();
                case RIGHT -> player.moveRight();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (player == null) return;

            switch (e.getCode()) {
                case LEFT -> player.stopLeft();
                case RIGHT -> player.stopRight();
            }
        });
    }

// ===============================
// Update / Frame Handling
// ===============================
    public void update() {
        // -------------------------------
        // Polymorphic update for all game objects
        // -------------------------------
        for (GameObject obj : gameObjects) {
            obj.update(); // Each GameObject handles its own logic (movement, scrolling, etc.)
        }

        // -------------------------------
        // Collision detection
        // -------------------------------
        for (int i = 0; i < gameObjects.size(); i++) {
            if (!(gameObjects.get(i) instanceof Collidable)) continue;
            Collidable c1 = (Collidable) gameObjects.get(i);

            for (int j = i + 1; j < gameObjects.size(); j++) {
                if (!(gameObjects.get(j) instanceof Collidable)) continue;
                Collidable c2 = (Collidable) gameObjects.get(j);

                // Check bounding rectangles
                if (c1.getBounds().intersects(c2.getBounds())) {
                    c1.onCollision(c2);
                    c2.onCollision(c1);
                    // if player hits an obstacle it loses a life
                    if ((gameObjects.get(i) instanceof Player && gameObjects.get(j) instanceof Obstacle) ||(gameObjects.get(j) instanceof Player && gameObjects.get(i) instanceof Obstacle)) {
                        scoreManager.loseLife();
                    }
                }
            }
        }
    }

    // ===============================
    // Getters
    // ===============================
    public Pane getGamePane() {
        return gamePane;
    }

    public Player getPlayer() {
        return player;
    }

    public SceneryManager getSceneryManager() {
        return sceneryManager;
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }
}