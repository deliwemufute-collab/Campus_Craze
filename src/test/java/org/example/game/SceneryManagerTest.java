package org.example.game;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class SceneryManagerTest {

    private SceneryManager manager;
    private Pane testPane;
    private Image[] testVariants;

    // Boot the JavaFX runtime before tests run, as JUnit does not start it automatically
    @BeforeAll
    static void initToolkit() throws Exception {
        PlatformImpl.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        testPane = new Pane();
        manager = new SceneryManager(testPane, 3.0, 768, 1024);

        // Use real local assets so image dimensions are available during scroll/recycle tests
        testVariants = new Image[]{
                new Image(SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_start.png")),
                new Image(SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_mid_1.png")),
                new Image(SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_mid_2.png"))
        };
        manager.setSceneryVariants(testVariants);
    }

    @Test
    void testInitialTileListIsEmpty() {
        assertTrue(manager.getTiles().isEmpty());
    }

    @Test
    void testSpawnTileAddsTileToList() {
        manager.spawnTile(0);
        assertEquals(1, manager.getTiles().size());
    }

    @Test
    void testSpawnTileAddsSpriteToPane() {
        manager.spawnTile(0);
        assertEquals(1, testPane.getChildren().size());
    }

    @Test
    void testSpawnMultipleTiles() {
        manager.spawnTile(0);
        manager.spawnTile(100);
        manager.spawnTile(200);
        assertEquals(3, manager.getTiles().size());
    }

    @Test
    void testScrollTilesMovesTilesDown() {
        manager.spawnTile(0);
        double initialY = manager.getTiles().get(0).getY();

        manager.scrollTiles();

        assertTrue(manager.getTiles().get(0).getY() > initialY);
    }

    @Test
    void testScrollTilesMovesDownByScrollSpeed() {
        manager.spawnTile(0);
        double initialY = manager.getTiles().get(0).getY();

        manager.scrollTiles();

        assertEquals(initialY + 3.0, manager.getTiles().get(0).getY());
    }

    @Test
    void testOffscreenTileIsRecycledToTop() {
        // Spawn a tile just below the screen so one scrollTiles() call triggers recycling
        manager.spawnTile(767);
        manager.scrollTiles();

        // After recycling, tile Y should be negative (above screen)
        assertTrue(manager.getTiles().get(0).getY() < 0);
    }

    @Test
    void testTileCountUnchangedAfterRecycle() {
        // Recycling should reuse the tile, not add a new one
        manager.spawnTile(767);
        manager.scrollTiles();

        assertEquals(1, manager.getTiles().size());
    }

    @Test
    void testGetScrollSpeed() {
        assertEquals(3.0, manager.getScrollSpeed());
    }

    @Test
    void testSetScrollSpeed() {
        manager.setScrollSpeed(5.0);
        assertEquals(5.0, manager.getScrollSpeed());
    }

    @Test
    void testSpawnWithNoVariantsDoesNotCrash() {
        // Should print an error but not throw an exception
        manager.setSceneryVariants(new Image[0]);
        assertDoesNotThrow(() -> manager.spawnTile(0));
    }
}