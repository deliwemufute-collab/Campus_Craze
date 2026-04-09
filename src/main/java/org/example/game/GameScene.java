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
    private static final double SCROLL_SPEED = 3.0;

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
        setupObstacles();
        setupHUD();
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
        player = new Player(playerImage, screenWidth / 2, screenHeight - 150, screenWidth);
        gamePane.getChildren().add(player.getSprite());
        gameObjects.add(player);
    }

    private void setupObstacles() {
        obstacleManager = new ObstacleManager(gamePane);

        Obstacle o1 = obstacleManager.spawnObstacle(380, 150);
        Obstacle o2 = obstacleManager.spawnObstacle(512, -100);
        Obstacle o3 = obstacleManager.spawnObstacle(640, -350);

        gameObjects.add(o1);
        gameObjects.add(o2);
        gameObjects.add(o3);

        System.out.println("GameScene: dummy obstacles spawned.");
    }

    private void setupHUD() {
        hudManager   = new HUDManager(gamePane);
        hudManager.setupHUD();
        scoreManager = new ScoreManager(hudManager);

        // Give the player a reference to ScoreManager so it can lose lives on collision
        player.setScoreManager(scoreManager);
    }

    // ===============================
    // Game Loop
    // ===============================
    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
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
        System.out.println("GameScene: Game loop started.");
    }

    public void setupKeyboard(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (player == null) return;
            switch (e.getCode()) {
                case LEFT  -> player.moveLeft();
                case RIGHT -> player.moveRight();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (player == null) return;
            switch (e.getCode()) {
                case LEFT  -> player.stopLeft();
                case RIGHT -> player.stopRight();
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

        // --- Collision detection ---
        for (int i = 0; i < gameObjects.size(); i++) {
            if (!(gameObjects.get(i) instanceof Collidable)) continue;
            Collidable c1 = (Collidable) gameObjects.get(i);

            for (int j = i + 1; j < gameObjects.size(); j++) {
                if (!(gameObjects.get(j) instanceof Collidable)) continue;
                Collidable c2 = (Collidable) gameObjects.get(j);

                if (c1.getBounds().intersects(c2.getBounds())) {
                    c1.onCollision(c2);
                    c2.onCollision(c1);

                    if ((gameObjects.get(i) instanceof Player && gameObjects.get(j) instanceof Obstacle)
                            || (gameObjects.get(j) instanceof Player && gameObjects.get(i) instanceof Obstacle)) {
                        scoreManager.loseLife();
                    }
                }
            }
        }
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
}