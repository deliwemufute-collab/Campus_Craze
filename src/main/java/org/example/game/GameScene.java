package org.example.game;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class GameScene {

    // --- Constants ---
    private final double screenWidth;
    private final double screenHeight;
    private static final double ROAD_LEFT_X  = 130.0; // Playable area
    private static final double ROAD_RIGHT_X = 890.0; // Playable area
    private static final double SCROLL_SPEED = 3.0;

    private static final double TILE_H       = 320.0;  // actual tile height from assets

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
    private long lastScoreTime = 0;

    // --- Theme assets ---
    // City (opening theme — no dummy tiles in production)
    private Image cityStart;
    private Image[] cityMids;
    private Image cityTop;

    // Park
    private Image parkStart;
    private Image[] parkMids;
    private Image parkTop;

    // Subway
    private Image subwayStart;
    private Image[] subwayMids;
    private Image subwayTop;

    // --- Theme transition flags ---
    private boolean switchedToPark   = false;
    private boolean switchedToSubway = false;

    // timing
    private long   gameStartNano  = 0;
    private double elapsedSeconds = 0;
    private long   lastScoreNano  = 0;

    //Collision Feedback
    private CollisionFeedback feedback;


    // ===============================
    // Constructor
    // ===============================
    public GameScene(Pane pane, double width, double height) {
        this.gamePane     = pane;
        this.screenWidth  = width;
        this.screenHeight = height;
    }

    // ===============================
    // Initialization
    // ===============================

    public void initializeGameObjects() {
        setupScenery();
        setupPlayer();
        setupHUD();
        setupObstacles();

    }

    private void setupScenery() {
        // --- Load city tiles (opening theme) ---
        cityStart = new Image(getClass().getResourceAsStream("/Tiles/city_tile_start.jpg"));
        cityMids  = new Image[]{
                new Image(getClass().getResourceAsStream("/Tiles/city_tile_mid_1.jpg")),
                new Image(getClass().getResourceAsStream("/Tiles/city_tile_mid_2.jpg")),
                new Image(getClass().getResourceAsStream("/Tiles/city_tile_mid_3.jpg")),
                new Image(getClass().getResourceAsStream("/Tiles/city_tile_mid_4.jpg"))
        };
        cityTop = new Image(getClass().getResourceAsStream("/Tiles/city_tile_top.jpg"));

        // --- Load park tiles ---
        parkStart = new Image(getClass().getResourceAsStream("/Tiles/park_tile_start.jpg"));
        parkMids  = new Image[]{
                new Image(getClass().getResourceAsStream("/Tiles/park_tile_mid_1.jpg")),
                new Image(getClass().getResourceAsStream("/Tiles/park_tile_mid_2.jpg")),
                new Image(getClass().getResourceAsStream("/Tiles/park_tile_mid_3.jpg")),
                new Image(getClass().getResourceAsStream("/Tiles/park_tile_mid_4.jpg"))
        };
        parkTop = new Image(getClass().getResourceAsStream("/Tiles/park_tile_top.jpg"));

        // --- Load subway tiles ---
        subwayStart = new Image(getClass().getResourceAsStream("/Tiles/subway_tile_start.jpg"));
        subwayMids  = new Image[]{
                new Image(getClass().getResourceAsStream("/Tiles/subway_tile_mid_1.jpg")),
                new Image(getClass().getResourceAsStream("/Tiles/subway_tile_mid_2.jpg")),
                new Image(getClass().getResourceAsStream("/Tiles/subway_tile_mid_3.jpg")),
                new Image(getClass().getResourceAsStream("/Tiles/subway_tile_mid_4.jpg"))
        };
        subwayTop = new Image(getClass().getResourceAsStream("/Tiles/subway_tile_top.jpg"));

        // --- Set up SceneryManager with shuffled city mids as the rolling pool ---
        sceneryManager = new SceneryManager(gamePane, SCROLL_SPEED, screenHeight, screenWidth);
        sceneryManager.setSceneryVariants(cityMids); // shuffled mids are the default pool

        // --- Spawn initial tiles: tile[0] = cityStart, rest = shuffled mids ---
        // Use cityStart height as the reference tile size
        double tileHeight = cityStart.getHeight();
        int tilesNeeded = (int) Math.ceil(screenHeight / tileHeight) + 1;

        // Tiles scroll downward — the player sees the bottom of the screen first.
        // cityStart goes on the last tile (highest Y, closest to the player) and
        // shuffled mids fill everything above it.
        for (int i = 0; i < tilesNeeded; i++) {
            double yPos = i * tileHeight;
            SceneryTile tile;
            if (i == tilesNeeded - 1) {
                // Last tile sits at the bottom — this is what the player sees first
                tile = sceneryManager.spawnTileWithImage(cityStart, yPos);
            } else {
                // Everything above is shuffled mids (scroll into view after start)
                tile = sceneryManager.spawnTile(yPos);
            }
            gameObjects.add(tile);
        }
        System.out.println("GameScene: city scenery tiles spawned (shuffled mids + start at bottom).");
    }

    private void setupPlayer() {
        Image playerImage = new Image(getClass().getResourceAsStream("/Player/player_dummy.png"));
        player = new Player(playerImage,
                screenWidth / 2,
                screenHeight - 150,
                ROAD_LEFT_X,
                ROAD_RIGHT_X);
        player.setScoreManager(scoreManager);
        gamePane.getChildren().add(player.getSprite());
        gameObjects.add(player);
    }

    private void setupObstacles() {
        obstacleManager = new ObstacleManager(gamePane);

        if (scoreManager == null) {
            throw new IllegalStateException("ScoreManager not initialized before obstacles");
        }

        obstacleManager.setScoreManager(scoreManager);
        obstacleManager.setFeedback(feedback);

        if (feedback == null) {
            System.out.println("WARNING: feedback is null (screen shake disabled)");
        }

        List<GameObject> spawned = obstacleManager.spawnInitialObjects();
        gameObjects.addAll(spawned);

        System.out.println("GameScene: obstacles spawned via manager.");

        System.out.println("Spawned objects:");
        for (GameObject g : spawned) {
            System.out.println(g.getClass().getSimpleName());
        }
    }

    private void setupHUD() {
        hudManager   = new HUDManager(gamePane);
        hudManager.setupHUD();

        scoreManager = new ScoreManager(hudManager);

        player.setScoreManager(scoreManager);

        // if obstacleManager already exists, re-inject
        if (obstacleManager != null) {
            obstacleManager.setScoreManager(scoreManager);
        }
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

                // Increment score once per second (now is in nanoseconds)
                if (lastScoreTime == 0) lastScoreTime = now;
                if (now - lastScoreTime >= 1_000_000_000L) {
                    scoreManager.incrementScore();
                    lastScoreTime = now;
                }
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
                case LEFT  -> player.moveLeft();
                case RIGHT -> player.moveRight();
                case UP    -> player.moveUp();
                case DOWN  -> player.moveDown();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (player == null) return;
            switch (e.getCode()) {
                case LEFT  -> player.stopLeft();
                case RIGHT -> player.stopRight();
                case UP    -> player.stopUp();
                case DOWN  -> player.stopDown();

            }
        });
    }

    // ===============================
    // Update / Frame Handling
    // ===============================
    public void update() {
        // --- Theme transition checks ---
        // City is the opening theme. Transitions fire on score milestones.
        // switchTheme(outgoingTop, incomingStart, incomingMids) — the outgoing
        // top tile plays once on the next recycle before the new start tile appears.

        int score = scoreManager.getScore();

        if (!switchedToPark && score >= 100) {
            switchedToPark = true;
            sceneryManager.switchTheme(cityTop, parkStart, parkMids);
        } else if (!switchedToSubway && score >= 200) {
            switchedToSubway = true;
            sceneryManager.switchTheme(parkTop, subwayStart, subwayMids);
        }

        // Scroll and recycle scenery
        sceneryManager.update();

        // Polymorphic update for non-scenery objects only
        for (GameObject obj : gameObjects) {
            if (obj instanceof SceneryTile) continue;
            obj.update();
        }

        obstacleManager.scrollObstacles(screenHeight);

        double newSpeed = obstacleManager.getScrollSpeed();

        // Update ALL scenery tiles
        for (GameObject obj : gameObjects) {
            if (obj instanceof SceneryTile tile) {
                tile.setScrollSpeed(newSpeed);
            }
        }

        // --- Collision detection ---
        for (int i = 0; i < gameObjects.size(); i++) {
            if (!(gameObjects.get(i) instanceof Collidable)) continue;

            for (int j = i + 1; j < gameObjects.size(); j++) {
                if (!(gameObjects.get(j) instanceof Collidable)) continue;

                if (isManagedPair(gameObjects.get(i), gameObjects.get(j))) continue;

                Collidable c1 = (Collidable) gameObjects.get(i);
                Collidable c2 = (Collidable) gameObjects.get(j);

                if (c1.getBounds().intersects(c2.getBounds())) {

                    System.out.println("COLLISION: "
                            + c1.getClass().getSimpleName()
                            + " <-> "
                            + c2.getClass().getSimpleName());

                    c1.onCollision(c2);
                    c2.onCollision(c1);

                    if ((c1 instanceof Player && c2 instanceof Obstacle) ||
                            (c2 instanceof Player && c1 instanceof Obstacle)) {

                        System.out.println("PLAYER vs OBSTACLE CHECK");

                        System.out.println("Player bounds: " +
                                (c1 instanceof Player ? c1.getBounds() : c2.getBounds()));

                        System.out.println("Obstacle bounds: " +
                                (c1 instanceof Obstacle ? c1.getBounds() : c2.getBounds()));
                    }
                }
            }
        }
    }

    /** True for pairs already handled by ObstacleManager — prevents double collision. */
    private boolean isManagedPair(GameObject a, GameObject b) {

        // Ignore Obstacle <-> Coin collisions
        if ((a instanceof Obstacle && b instanceof Coin) ||
                (a instanceof Coin && b instanceof Obstacle)) {
            return true;
        }

        return false;
    }

    // ===============================
    // Getters
    // ===============================
    public Pane getGamePane()               { return gamePane; }
    public Player getPlayer()               { return player; }
    public SceneryManager getSceneryManager() { return sceneryManager; }
    public List<GameObject> getGameObjects() { return gameObjects; }

    // ===============================
    // Setters
    // ===============================
    // Used by tests to inject a dummy ScoreManager and SceneryManager
    void setScoreManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }

    void setSceneryManager(SceneryManager sceneryManager) {
        this.sceneryManager = sceneryManager;
    }

    public void setObstacleManager(ObstacleManager manager) {
        this.obstacleManager = obstacleManager;
    }

    public ObstacleManager  getObstacleManager() {
        return obstacleManager;
    }

    public void setFeedback(CollisionFeedback feedback) {
        this.feedback = feedback;
    }

}