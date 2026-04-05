package org.example.game;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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

        // Setup dummy scenery using classpath resources
        Image[] dummyTiles = new Image[]{
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_start.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_mid_1.png")),
                new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_mid_2.png"))
        };

        scene.initializeGameObjects(); // initializes managers
        scene.getSceneryManager().setSceneryVariants(dummyTiles);

        // Create a dummy player
        Image playerImg = new Image(getClass().getResourceAsStream("/Player/player_dummy.png"));
        Player player = new Player(playerImg, 100, 100, 1024);
        scene.getGameObjects().add(player);

        // Create dummy obstacles
        Image obstacleImg = new Image(getClass().getResourceAsStream("/Obstacles/obstacle_dummy.png"));
        Obstacle o1 = new Obstacle(obstacleImg, 200, 200);
        Obstacle o2 = new Obstacle(obstacleImg, 300, 300);
        scene.getGameObjects().add(o1);
        scene.getGameObjects().add(o2);

        return scene;
    }

    // -------------------------------
    // Test 1: Polymorphic update() loop
    // -------------------------------
    @Test
    void testPolymorphicUpdateLoop() {
        GameScene scene = createDummyGameScene();

        scene.update(); // call update

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
    void testCollisionDetectionLoop() {
        GameScene scene = createDummyGameScene();

        Image obstacleImg = new Image(getClass().getResourceAsStream("/Obstacles/obstacle_dummy.png"));

        // Create overlapping obstacles to force collision
        class CollisionRecorder extends Obstacle {
            boolean collided = false;
            public CollisionRecorder(Image img, double x, double y) { super(img, x, y); }
            @Override
            public void onCollision(Collidable other) { collided = true; }
        }

        CollisionRecorder cr1 = new CollisionRecorder(obstacleImg, 500, 500);
        CollisionRecorder cr2 = new CollisionRecorder(obstacleImg, 500, 500);
        scene.getGameObjects().add(cr1);
        scene.getGameObjects().add(cr2);

        scene.update(); // triggers collision check

        assertTrue(cr1.collided, "cr1 should have registered collision");
        assertTrue(cr2.collided, "cr2 should have registered collision");
    }
}