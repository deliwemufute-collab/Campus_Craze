package org.example.game;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * Manages the gameplay screen.
 * Integrates SceneryManager for vertical background scrolling
 * and runs a dummy game loop to verify scrolling behaviour.
 *
 * @author Miguel Cofre      — GameScene structure
 * @author Emmanuel Ayobanjo — SceneryManager integration & dummy loop test
 * @author  Victoria Adeosun — HUDManager & UI
 */
public class GameScene {


    // --- Constants ---
    private final double screenWidth;
    private final double screenHeight;
    private static final double SCROLL_SPEED  = 3.0;   // pixels per frame
    private static final int    TILE_COUNT    = 3;      // tiles needed to fill screen

    // --- Core pane ---
    private Pane gamePane;

    // --- Game objects ---
    private Player player;           // Set by Deliwe Mufute
    private ObstacleManager obstacleManager;    // Set by Paakhi Agrawala
    private HUDManager hudManager; // Set by Victoria Adeosun

    // --- Scenery (Emmanuel Ayobanjo) ---
    private SceneryManager sceneryManager;

    // --- Game loop ---
    private AnimationTimer gameLoop;

    /**
     * Constructor: wires up the pane and initialises placeholders.
     * Resolution is recycled from Main (1024 x 768).
     *
     * @param pane   the JavaFX Pane this scene renders into
     * @param width  screen width passed from Main
     * @param height screen height passed from Main
     */
    public GameScene(Pane pane, double width, double height) {
        this.gamePane     = pane;
        this.screenWidth  = width;
        this.screenHeight = height;

        this.player    = null;            // To be set by Deliwe Mufute
    }

    /**
     * Initialises all game objects before the loop starts.
     * Loads assets, sets up SceneryManager, and spawns initial tiles.
     *
     * Victoria Adeosun will expand this with HUD + additional object setup.
     */
    public void initializeGameObjects() {

        // --- Load scenery images directly from resources ---
        Image[] sceneryVariants = new Image[]{
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_start.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_mid_1.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_mid_2.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_mid_3.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_mid_4.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_end.png"))
        };

        // --- Set up SceneryManager ---
        sceneryManager = new SceneryManager(
                gamePane,
                SCROLL_SPEED,
                screenHeight,
                screenWidth
        );
        sceneryManager.setSceneryVariants(sceneryVariants);

        // --- Spawn initial tiles to fill the screen top-to-bottom ---
        // Tiles are stacked so there is no gap when scrolling starts.
        // Tile height is read from the first loaded image.
        double tileHeight = sceneryVariants[0].getHeight();
        for (int i = 0; i < TILE_COUNT; i++) {
            sceneryManager.spawnTile(i * tileHeight);
        }

        System.out.println("GameScene: scenery tiles spawned.");

        // --- Set up Player ---
        Image playerImage = new Image(getClass().getResourceAsStream("/Player/player_dummy.png"));
        player = new Player(playerImage, screenWidth / 2, screenHeight - 150, screenWidth);
        gamePane.getChildren().add(player.getSprite());

        // --- Set up ObstacleManager (Paakhi Agrawala) ---
        obstacleManager = new ObstacleManager(gamePane);

// initial dummy obstacles
        obstacleManager.spawnObstacle(380, 150);
        obstacleManager.spawnObstacle(512, -100);
        obstacleManager.spawnObstacle(640, -350);

        System.out.println("GameScene: dummy obstacles spawned.");

        // set up HUD  by Victoria Adeosun
        hudManager = new HUDManager(gamePane);
        hudManager.setupHUD();
    }

    /**
     * Starts the JavaFX AnimationTimer game loop.
     * Each frame: scrollTiles() is called on SceneryManager.
     * Placeholders exist for player movement and collision once other
     * members integrate their work.
     */
    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();
        System.out.println("GameScene: game loop started.");
    }

    //...KeyBoard input
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

    /**
     * Called every frame by the AnimationTimer.
     * Delegates scrolling to SceneryManager; player/collision TBD.
     */
    public void update() {
        // Scroll the background scenery
        sceneryManager.scrollTiles();

        // Player movement
        player.updatePosition();

        // Scroll obstacles with scenery
        obstacleManager.scrollObstacles(screenHeight);
    }

    // --- Getters ---
    public Pane getGamePane() {
        return gamePane;
    }

    public Player getPlayer() {
        return player;
    }

    public SceneryManager getSceneryManager() {
        return sceneryManager;
    }
}