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
                new WritableImage(1024, 320),
                new WritableImage(1024, 320),
                new WritableImage(1024, 320)
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

}