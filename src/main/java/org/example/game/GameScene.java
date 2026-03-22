package org.example.game;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import org.example.utils.AssetLoader;

/**
 * Manages the gameplay screen.
 * Integrates SceneryManager for vertical background scrolling
 * and runs a dummy game loop to verify scrolling behaviour.
 *
 * @author Miguel Cofre      — GameScene structure
 * @author Emmanuel Ayobanjo — SceneryManager integration & dummy loop test
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
    private Obstacle[] obstacles;    // Set by Paakhi Agrawala

    // --- Scenery (Emmanuel Ayobanjo) ---
    private SceneryManager sceneryManager;
    private AssetLoader assetLoader;

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
        this.obstacles = new Obstacle[0]; // To be set by Paakhi Agrawala
    }

    /**
     * Initialises all game objects before the loop starts.
     * Loads assets, sets up SceneryManager, and spawns initial tiles.
     *
     * Victoria Adeosun will expand this with HUD + additional object setup.
     */
    public void initializeGameObjects() {
        // --- Load all 16 scenery asset variants ---
        assetLoader = new AssetLoader();
        assetLoader.loadSceneryAssets();

        // --- Set up SceneryManager ---
        sceneryManager = new SceneryManager(
                gamePane,
                SCROLL_SPEED,
                screenHeight,
                screenWidth
        );
        sceneryManager.setSceneryVariants(assetLoader.getSceneryVariants());

        // --- Spawn initial tiles to fill the screen top-to-bottom ---
        // Tiles are stacked so there is no gap when scrolling starts.
        // Tile height is read from the first loaded image.
        double tileHeight = assetLoader.getSceneryVariants()[0] != null
                ? assetLoader.getSceneryVariants()[0].getHeight()
                : screenHeight / TILE_COUNT;

        for (int i = 0; i < TILE_COUNT; i++) {
            sceneryManager.spawnTile(i * tileHeight);
        }

        System.out.println("GameScene: " + TILE_COUNT + " initial scenery tiles spawned.");
        //   Player Object Created
        Image playerImage = new Image("https://via.placeholder.com/60");

        player = new Player(playerImage, screenWidth / 2, screenHeight - 150);

        gamePane.getChildren().add(player.getSprite());
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

    /**
     * Called every frame by the AnimationTimer.
     * Delegates scrolling to SceneryManager; player/collision TBD.
     */
    public void update() {
        // Scroll the background scenery
        sceneryManager.scrollTiles();

        // Player movement
        // if (player != null) player.updatePosition();
        player.updatePosition();

        // Collision detection — to be implemented by Paakhi Agrawala
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