package org.example.game;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SceneryManager.
 *
 * Milestone 1 tests: spawn, scroll, recycle, speed getters — retained.
 * Milestone 2 tests (new): recycle triggered by update(), overlay tile
 *                           support, theme switching.
 *
 * @author Emmanuel Ayobanjo
 */
public class SceneryManagerTest {

    private SceneryManager manager;
    private Pane testPane;
    private Image[] testVariants;
    private Image[] altVariants;

    @BeforeAll
    static void initToolkit() throws Exception {
        PlatformImpl.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        testPane = new Pane();
        manager = new SceneryManager(testPane, 3.0, 768, 1024);

        testVariants = new Image[]{
                new Image(SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_start.png")),
                new Image(SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_mid_1.png")),
                new Image(SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_mid_2.png"))
        };

        // Alternate set used for theme-switch tests
        altVariants = new Image[]{
                new Image(SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_mid_3.png")),
                new Image(SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_mid_4.png")),
                new Image(SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_end.png"))
        };

        manager.setSceneryVariants(testVariants);
    }

    // -----------------------------------------------------------------------
    // Milestone 1 — retained
    // -----------------------------------------------------------------------

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
        manager.spawnTile(767);
        manager.scrollTiles();
        assertTrue(manager.getTiles().get(0).getY() < 0);
    }

    @Test
    void testTileCountUnchangedAfterRecycle() {
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
        manager.setSceneryVariants(new Image[0]);
        assertDoesNotThrow(() -> manager.spawnTile(0));
    }

    // -----------------------------------------------------------------------
    // Milestone 2 — recycle triggered by update()
    // -----------------------------------------------------------------------

    @Test
    void testUpdateRecyclesTileWhenOffscreen() {
        // Tile at y=767, one update() call should recycle it above the screen
        manager.spawnTile(767);
        manager.update();
        assertTrue(manager.getTiles().get(0).getY() < 0,
                "Tile should be recycled to above screen after update()");
    }

    @Test
    void testUpdateDoesNotRecycleTileStillOnScreen() {
        // Tile at y=0, one update() should just scroll it down, not recycle
        manager.spawnTile(0);
        manager.update();
        double y = manager.getTiles().get(0).getY();
        assertTrue(y > 0 && y < 768,
                "Tile should still be on screen after one update()");
    }

    @Test
    void testUpdateScrollsAllTiles() {
        manager.spawnTile(0);
        manager.spawnTile(200);
        double y0 = manager.getTiles().get(0).getY();
        double y1 = manager.getTiles().get(1).getY();
        manager.update();
        assertEquals(y0 + 3.0, manager.getTiles().get(0).getY(), 0.001);
        assertEquals(y1 + 3.0, manager.getTiles().get(1).getY(), 0.001);
    }

    // -----------------------------------------------------------------------
    // Milestone 2 — overlay tile support
    // -----------------------------------------------------------------------

    @Test
    void testInitialOverlayListIsEmpty() {
        assertTrue(manager.getOverlayTiles().isEmpty());
    }

    @Test
    void testSpawnOverlayTileAddsToOverlayList() {
        Image overlayImage = new Image(
                SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_mid_1.png")
        );
        manager.spawnOverlayTile(overlayImage, 0, 0);
        assertEquals(1, manager.getOverlayTiles().size());
    }

    @Test
    void testSpawnOverlayTileAddsSpriteToPane() {
        Image overlayImage = new Image(
                SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_mid_1.png")
        );
        int paneChildrenBefore = testPane.getChildren().size();
        manager.spawnOverlayTile(overlayImage, 0, 0);
        assertEquals(paneChildrenBefore + 1, testPane.getChildren().size());
    }

    @Test
    void testOverlayTilesScrollAtSameSpeedAsBackground() {
        Image overlayImage = new Image(
                SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_mid_1.png")
        );
        manager.spawnOverlayTile(overlayImage, 0, 0);
        double initialY = manager.getOverlayTiles().get(0).getY();
        manager.update();
        assertEquals(initialY + 3.0, manager.getOverlayTiles().get(0).getY(), 0.001);
    }

    @Test
    void testOffscreenOverlayTileIsRecycled() {
        Image overlayImage = new Image(
                SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_mid_1.png")
        );
        manager.spawnOverlayTile(overlayImage, 0, 767);
        manager.update();
        assertTrue(manager.getOverlayTiles().get(0).getY() < 0,
                "Offscreen overlay tile should be recycled above screen");
    }

    @Test
    void testOverlayCountUnchangedAfterRecycle() {
        Image overlayImage = new Image(
                SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_mid_1.png")
        );
        manager.spawnOverlayTile(overlayImage, 0, 767);
        manager.update();
        assertEquals(1, manager.getOverlayTiles().size());
    }

    @Test
    void testBackgroundAndOverlayTilesAreIndependentLists() {
        // Spawning a background tile should not affect the overlay list and vice versa
        manager.spawnTile(0);
        Image overlayImage = new Image(
                SceneryManagerTest.class.getResourceAsStream("/Tiles/dummy_tile_mid_1.png")
        );
        manager.spawnOverlayTile(overlayImage, 0, 0);

        assertEquals(1, manager.getTiles().size());
        assertEquals(1, manager.getOverlayTiles().size());
    }

    // -----------------------------------------------------------------------
    // Milestone 2 — theme switching
    // -----------------------------------------------------------------------

    @Test
    void testSetThemeVariantsChangesVariantArray() {
        // After switching variants, the new array should be returned
        manager.setSceneryVariants(altVariants);
        // Spawn a tile and confirm it comes from the new set — check by spawning
        // and ensuring the manager doesn't crash with the new array
        assertDoesNotThrow(() -> manager.spawnTile(0));
    }

    @Test
    void testRecycledTilePicksUpNewVariantAfterThemeSwitch() {
        // Spawn a tile at y=767 (about to go offscreen), switch variants,
        // then update() — the recycled tile should get an image from altVariants
        manager.spawnTile(767);
        manager.setSceneryVariants(altVariants);
        manager.update();

        // Tile was recycled — its image should now be one from altVariants
        Image recycledImage = manager.getTiles().get(0).getSprite().getImage();
        boolean imageFromAltSet = false;
        for (Image alt : altVariants) {
            if (alt == recycledImage) {
                imageFromAltSet = true;
                break;
            }
        }
        assertTrue(imageFromAltSet,
                "Recycled tile should use an image from the new variant set");
    }

    @Test
    void testSwitchingToNullVariantsDoesNotCrashOnUpdate() {
        // Defensive: if variants are set to null, update() must not throw
        manager.spawnTile(0);
        manager.setSceneryVariants(null);
        assertDoesNotThrow(() -> manager.update());
    }
}