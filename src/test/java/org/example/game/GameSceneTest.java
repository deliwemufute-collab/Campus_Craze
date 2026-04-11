package org.example.game;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class GameSceneTest {

    // Boot JavaFX runtime before tests
    @BeforeAll
    static void initToolkit() {
        PlatformImpl.startup(() -> {});
    }

    // -------------------------------
    // Helper: create dummy GameScene
    // -------------------------------
    private GameScene createDummyGameScene() {
        Pane pane = new Pane();
        GameScene scene = new GameScene(pane, 1024, 768);

        // Wire up a minimal HUD and ScoreManager so update() doesn't NPE
        HUDManager hud = new HUDManager(pane);
        hud.setupHUD();
        scene.setScoreManager(new ScoreManager(hud));

        // Manually wire up only what the tests need using dummy assets
        // that exist in the test classpath — avoids NPE from missing themed tiles
        Image[] dummyTiles = new Image[]{
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_start.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_mid_1.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_mid_2.png"))
        };

        SceneryManager sceneryManager = new SceneryManager(pane, 3.0, 768, 1024);
        sceneryManager.setSceneryVariants(dummyTiles);
        scene.setSceneryManager(sceneryManager);
        ObstacleManager manager = new ObstacleManager(
                new Pane(),
                new WritableImage(1, 1),
                new WritableImage(1, 1)
        );

        double tileHeight = dummyTiles[0].getHeight();
        int tilesNeeded = (int) Math.ceil(768 / tileHeight) + 1;
        for (int i = 0; i < tilesNeeded; i++) {
            SceneryTile tile = sceneryManager.spawnTile(i * tileHeight);
            scene.getGameObjects().add(tile);
        }

        Image playerImg = new Image(getClass().getResourceAsStream("/Player/player_dummy.png"));
        Player player = new Player(playerImg, 512, 618, 170.0, 850.0);
        pane.getChildren().add(player.getSprite());
        scene.getGameObjects().add(player);

        Image obstacleImg = new Image(getClass().getResourceAsStream("/Obstacles/obstacle_dummy.png"));
        scene.getGameObjects().add(new Obstacle(obstacleImg, 380, 150));
        scene.getGameObjects().add(new Obstacle(obstacleImg, 512, -100));

        return scene;
    }

    // -------------------------------
    // Test 1: Polymorphic update() loop
    // -------------------------------
    @Test
    void testPolymorphicUpdateLoop() throws Exception {
        GameScene scene = createDummyGameScene();

        ObstacleManager manager = new ObstacleManager(
                new Pane(),
                new WritableImage(1,1),
                new WritableImage(1,1)
        );

        // 🔥 inject into private field
        Field field = GameScene.class.getDeclaredField("obstacleManager");
        field.setAccessible(true);
        field.set(scene, manager);

        // ensure objects exist
        scene.getGameObjects().addAll(manager.spawnInitialObjects());

        scene.update();

        assertFalse(scene.getGameObjects().isEmpty(), "GameObjects list should not be empty");

        for (GameObject obj : scene.getGameObjects()) {
            assertNotNull(obj.getX(), "Object X should not be null");
            assertNotNull(obj.getY(), "Object Y should not be null");
        }
    }
    // -------------------------------
    // Test 2: Integration of Player, Obstacles, SceneryTiles
    // -------------------------------
    @Test
    void testIntegrationObjects() {
        GameScene scene = createDummyGameScene();

        boolean playerExists = false;
        boolean obstacleExists = false;
        boolean sceneryExists = false;

        for (GameObject obj : scene.getGameObjects()) {
            if (obj instanceof Player) playerExists = true;
            if (obj instanceof Obstacle) obstacleExists = true;
            if (obj instanceof SceneryTile) sceneryExists = true;
        }

        assertTrue(playerExists, "Player should exist in gameObjects");
        assertTrue(obstacleExists, "Obstacles should exist in gameObjects");
        assertTrue(sceneryExists, "SceneryTiles should exist in gameObjects");
    }

    // -------------------------------
    // Test 3: Collision detection loop
    // -------------------------------
    @Test
    void testCollisionDetectionLoop() throws Exception {
        GameScene scene = createDummyGameScene();

        ObstacleManager manager = new ObstacleManager(
                new Pane(),
                new WritableImage(1,1),
                new WritableImage(1,1)
        );

        // inject obstacleManager (same fix as other test)
        Field field = GameScene.class.getDeclaredField("obstacleManager");
        field.setAccessible(true);
        field.set(scene, manager);

        Image obstacleImg = new Image(getClass().getResourceAsStream("/Obstacles/obstacle_dummy.png"));

        class CollisionRecorder extends Obstacle {
            boolean collided = false;

            public CollisionRecorder(Image img, double x, double y) {
                super(img, x, y, Obstacle.Theme.CITY, Obstacle.Size.SMALL);
            }

            @Override
            public void onCollision(Collidable other) {
                collided = true;
            }
        }

        CollisionRecorder cr1 = new CollisionRecorder(obstacleImg, 500, 500);
        CollisionRecorder cr2 = new CollisionRecorder(obstacleImg, 500, 500);

        scene.getGameObjects().add(cr1);
        scene.getGameObjects().add(cr2);

        scene.update();

        assertTrue(cr1.collided, "cr1 should have registered collision");
        assertTrue(cr2.collided, "cr2 should have registered collision");
    }

    // -----------------------------------------------------------------------
    // Overlay / BuildingTile integration tests
    // -----------------------------------------------------------------------

    @Test
    void testBuildingTilesExistInGameObjects() {
        Pane pane = new Pane();
        GameScene scene = new GameScene(pane, 1024, 768);
        HUDManager hud = new HUDManager(pane);
        hud.setupHUD();
        scene.setScoreManager(new ScoreManager(hud));
        scene.initializeGameObjects();

        long buildingCount = scene.getGameObjects().stream()
                .filter(obj -> obj instanceof BuildingTile)
                .count();

        assertTrue(buildingCount > 0,
                "Expected at least one BuildingTile in gameObjects. " +
                        "Check that city_overlay_left/right assets exist in test classpath.");
    }

    @Test
    void testOverlayTilesExistInSceneryManager() {
        Pane pane = new Pane();
        GameScene scene = new GameScene(pane, 1024, 768);
        HUDManager hud = new HUDManager(pane);
        hud.setupHUD();
        scene.setScoreManager(new ScoreManager(hud));
        scene.initializeGameObjects();

        assertFalse(scene.getSceneryManager().getOverlayTiles().isEmpty(),
                "SceneryManager overlay list should not be empty. " +
                        "Check that registerOverlayTile() is being called in setupOverlays().");
    }

    @Test
    void testOverlayTilesAreNotClustered() {
        // No two consecutive overlay tiles should share the same Y position,
        // because the MIN_GAP rule ensures at least OVERLAY_MIN_GAP road tiles
        // sit between any two spawned buildings.
        Pane pane = new Pane();
        GameScene scene = new GameScene(pane, 1024, 768);
        HUDManager hud = new HUDManager(pane);
        hud.setupHUD();
        scene.setScoreManager(new ScoreManager(hud));
        scene.initializeGameObjects();

        java.util.List<SceneryTile> overlays = scene.getSceneryManager().getOverlayTiles();

        // Overlays are spawned in left/right pairs — compare every other pair
        // Left tiles are at even indices, right tiles at odd indices.
        // Check that no two left-side tiles share identical Y (would mean same road tile)
        java.util.List<Double> leftYPositions = new java.util.ArrayList<>();
        for (int i = 0; i < overlays.size(); i += 2) {
            double y = overlays.get(i).getY();
            assertFalse(leftYPositions.contains(y),
                    "Two overlay tiles share the same Y — buildings are clustered at y=" + y);
            leftYPositions.add(y);
        }
    }

    @Test
    void testBuildingTilesAreCollidable() {
        Pane pane = new Pane();
        GameScene scene = new GameScene(pane, 1024, 768);
        HUDManager hud = new HUDManager(pane);
        hud.setupHUD();
        scene.setScoreManager(new ScoreManager(hud));
        scene.initializeGameObjects();

        scene.getGameObjects().stream()
                .filter(obj -> obj instanceof BuildingTile)
                .forEach(obj -> assertTrue(obj instanceof Collidable,
                        "Every BuildingTile in gameObjects must implement Collidable"));
    }

    @Test
    void testOverlayCountIsWithinReasonableBounds() {
        // With MIN_GAP=2 and 1-in-3 chance, we expect roughly 1-3 buildings
        // across the ~5 initial tiles. Pairs mean 2-6 overlay tiles total.
        // We allow a generous range to avoid flakiness from randomness.
        Pane pane = new Pane();
        GameScene scene = new GameScene(pane, 1024, 768);
        HUDManager hud = new HUDManager(pane);
        hud.setupHUD();
        scene.setScoreManager(new ScoreManager(hud));
        scene.initializeGameObjects();

        int overlayCount = scene.getSceneryManager().getOverlayTiles().size();

        assertTrue(overlayCount >= 2,
                "Expected at least one building pair (2 overlay tiles), got " + overlayCount);
        assertTrue(overlayCount <= 20,
                "Unexpectedly large overlay count — possible clustering: " + overlayCount);
    }
}