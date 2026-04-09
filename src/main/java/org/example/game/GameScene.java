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

    // -------------------------------------------------------
    // Constants
    // -------------------------------------------------------

    private static final double SCROLL_SPEED = 3.0;   // pixels per frame
    private static final int TILE_COUNT = 3;          // tiles needed to fill screen

    private static final double TILE_H       = 320.0;  // actual tile height from assets

    // -------------------------------------------------------
    // Fields
    // -------------------------------------------------------

    private final double screenWidth;
    private final double screenHeight;

    // --- Core pane ---
    private final Pane gamePane;

    // --- Game objects ---
    private Player player;
    private ObstacleManager obstacleManager;
    private SceneryManager sceneryManager;
    private HUDManager hudManager;
    private ScoreManager       scoreManager;
    // collision visual effects
    private CollisionFeedback collisionFeedback;

    // --- Centralized game object list ---
    private final List<GameObject> gameObjects = new ArrayList<>();

    // --- Game loop ---
    private AnimationTimer gameLoop;

    // timing
    private long   gameStartNano  = 0;
    private double elapsedSeconds = 0;
    private long   lastScoreNano  = 0;


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
        Image[] variants = loadSceneryVariants();
        // +2 buffer tiles so there's never a gap while recycling
        int tileCount = (int) Math.ceil(screenHeight / TILE_H) + 2;

        sceneryManager = new SceneryManager(gamePane, SCROLL_SPEED, screenHeight, screenWidth);
        sceneryManager.setSceneryVariants(variants);

        for (int i = 0; i < tileCount; i++) {
            SceneryTile tile = sceneryManager.spawnTile(-TILE_H + i * TILE_H);
            gameObjects.add(tile);
        }
        System.out.println("GameScene: " + tileCount + " tiles spawned (h=" + TILE_H + ").");
    }

    /**
     * Loads real JPG tiles grouped: city → park → subway.
     * Falls back to dummy_tile_mid_1.png for any missing asset.
     */
    private Image[] loadSceneryVariants() {
        String[] paths = {
                // city
                "/Tiles/city_tile_start.jpg",
                "/Tiles/city_tile_mid_1.jpg", "/Tiles/city_tile_mid_2.jpg",
                "/Tiles/city_tile_mid_3.jpg", "/Tiles/city_tile_mid_4.jpg",
                "/Tiles/city_tile_top.jpg",
                // park
                "/Tiles/park_tile_start.jpg",
                "/Tiles/park_tile_mid_1.jpg", "/Tiles/park_tile_mid_2.jpg",
                "/Tiles/park_tile_mid_3.jpg", "/Tiles/park_tile_mid_4.jpg",
                "/Tiles/park_tile_top.jpg",
                // subway
                "/Tiles/subway_tile_start.jpg",
                "/Tiles/subway_tile_mid_1.jpg", "/Tiles/subway_tile_mid_2.jpg",
                "/Tiles/subway_tile_mid_3.jpg", "/Tiles/subway_tile_mid_4.jpg",
                "/Tiles/subway_tile_top.jpg"
        };
        Image fallback = new Image(
                getClass().getResourceAsStream("/Tiles/dummy_tile_mid_1.png"));
        Image[] out = new Image[paths.length];
        for (int i = 0; i < paths.length; i++) {
            try {
                var s = getClass().getResourceAsStream(paths[i]);
                out[i] = (s != null) ? new Image(s) : fallback;
            } catch (Exception e) { out[i] = fallback; }
        }
        return out;
    }

    private void setupPlayer() {
        Image img = new Image(getClass().getResourceAsStream("/Player/player_dummy.png"));
        // x = LEFT EDGE; screenWidth/2 places the player visually centred
        player = new Player(img, screenWidth / 2, screenHeight - 320, screenWidth);
        gamePane.getChildren().add(player.getSprite());
        gameObjects.add(player);
    }

    private void setupObstacles() {
        obstacleManager = new ObstacleManager(gamePane);
        List<GameObject> spawned = obstacleManager.spawnInitialObjects();
        gameObjects.addAll(spawned);
        System.out.println("GameScene: " + spawned.size() + " obstacles/coins spawned.");
    }

    private void setupHUD() {
        hudManager = new HUDManager(gamePane);
        hudManager.setupHUD();

        scoreManager = new ScoreManager(hudManager);
        player.setScoreManager(scoreManager);

        /// CollisionFeedback must be created last so its flash rects
        // sit on top of HUD labels in the z-order
        collisionFeedback = new CollisionFeedback(gamePane, screenWidth, screenHeight);
        obstacleManager.setFeedback(collisionFeedback);
    }

    // ===============================
    // Game Loop
    // ===============================

    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameStartNano == 0) {
                    gameStartNano = now;
                    lastScoreNano = now;
                }

                elapsedSeconds = (now - gameStartNano) / 1_000_000_000.0;

                // increment score once per second
                if (now - lastScoreNano >= 1_000_000_000L) {
                    scoreManager.incrementScore();
                    lastScoreNano = now;
                }

                update();
            }
        };
        gameLoop.start();
        System.out.println("GameScene: loop started.");
    }

    public void stopGameLoop() {
        if (gameLoop != null) gameLoop.stop();
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
        // 1. Tell ObstacleManager how many seconds have passed
        //    — it uses this for theme switching and difficulty ramp
        obstacleManager.setElapsedSeconds(elapsedSeconds);

        // 2. Polymorphic update for all GameObjects (movement, sync)
        for (GameObject obj : gameObjects) {
            obj.update();
        }

        // 3. Scenery tile scrolling + recycling
        sceneryManager.scrollTiles();

        // 4. Obstacle + coin scrolling, respawning, coin patterns, difficulty
        obstacleManager.scrollObstacles(screenHeight);

        // 5. Collision detection — manual BoundingBox, works from frame 1
        obstacleManager.checkCollisions(player);

        // 6. Generic Collidable loop for future pairs (BuildingTile etc.)
        //    Guard prevents double-firing obstacle/coin hits
        for (int i = 0; i < gameObjects.size(); i++) {
            if (!(gameObjects.get(i) instanceof Collidable)) continue;
            for (int j = i + 1; j < gameObjects.size(); j++) {
                if (!(gameObjects.get(j) instanceof Collidable)) continue;
                if (isManagedPair(gameObjects.get(i), gameObjects.get(j))) continue;
                Collidable c1 = (Collidable) gameObjects.get(i);
                Collidable c2 = (Collidable) gameObjects.get(j);
                if (c1.getBounds().intersects(c2.getBounds())) {
                    c1.onCollision(c2);
                    c2.onCollision(c1);
                }
            }
        }
    }

    /** True for pairs already handled by ObstacleManager — prevents double collision. */
    private boolean isManagedPair(GameObject a, GameObject b) {
        boolean aM = a instanceof Obstacle || a instanceof Coin;
        boolean bM = b instanceof Obstacle || b instanceof Coin;
        boolean aP = a instanceof Player;
        boolean bP = b instanceof Player;
        return (aM && bP) || (aP && bM);
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

    public ObstacleManager  getObstacleManager() {
        return obstacleManager;
    }

}