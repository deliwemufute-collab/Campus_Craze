package org.example.game;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.image.Image;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SceneryTile.
 *
 * Milestone 1 tests: position, sprite sync, image swap, height fallback.
 * Milestone 2 tests (new): self-scrolling via scrollSpeed field,
 *                           update() moves Y, overlay subclass hooks.
 *
 * @author Emmanuel Ayobanjo
 */
public class SceneryTileTest {

    private SceneryTile tile;
    private Image testImage;

    @BeforeAll
    static void initToolkit() throws Exception {
        PlatformImpl.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        testImage = new Image(
                SceneryTileTest.class.getResourceAsStream("/Tiles/dummy_tile_start.png")
        );
        // Milestone 2: constructor now accepts scrollSpeed
        tile = new SceneryTile(testImage, 50, 100, 3.0);
    }

    // -----------------------------------------------------------------------
    // Milestone 1 — retained
    // -----------------------------------------------------------------------

    @Test
    void testInitialPosition() {
        assertEquals(50, tile.getX());
        assertEquals(100, tile.getY());
    }

    @Test
    void testSetXUpdatesSpriteX() {
        tile.setX(200);
        assertEquals(200, tile.getX());
        assertEquals(200, tile.getSprite().getX());
    }

    @Test
    void testSetYUpdatesSpriteY() {
        tile.setY(300);
        assertEquals(300, tile.getY());
        assertEquals(300, tile.getSprite().getY());
    }

    @Test
    void testGetSpriteNotNull() {
        assertNotNull(tile.getSprite());
    }

    @Test
    void testSetImageSwapsImage() {
        Image newImage = new Image(
                SceneryTileTest.class.getResourceAsStream("/Tiles/dummy_tile_mid_1.png")
        );
        tile.setImage(newImage);
        assertEquals(newImage, tile.getSprite().getImage());
    }

    @Test
    void testGetHeightReturnsImageHeight() {
        assertTrue(tile.getHeight() > 0);
    }

    @Test
    void testGetHeightFallbackWhenImageNull() {
        tile.setImage(null);
        assertEquals(192, tile.getHeight());
    }

    // -----------------------------------------------------------------------
    // Milestone 2 — self-scrolling
    // -----------------------------------------------------------------------

    @Test
    void testScrollSpeedStoredCorrectly() {
        // The scrollSpeed passed in the constructor should be retrievable
        assertEquals(3.0, tile.getScrollSpeed());
    }

    @Test
    void testSetScrollSpeedUpdatesSpeed() {
        tile.setScrollSpeed(5.0);
        assertEquals(5.0, tile.getScrollSpeed());
    }

    @Test
    void testUpdateIncreasesYByScrollSpeed() {
        // update() must move the tile down by exactly scrollSpeed each frame
        double initialY = tile.getY();
        tile.update();
        assertEquals(initialY + 3.0, tile.getY(), 0.001);
    }

    @Test
    void testUpdateSyncsSpriteY() {
        // After update(), the sprite's Y must match the tile's logical Y
        tile.update();
        assertEquals(tile.getY(), tile.getSprite().getY(), 0.001);
    }

    @Test
    void testUpdateDoesNotChangeSpriteX() {
        // Scrolling is purely vertical — X must stay untouched
        double initialSpriteX = tile.getSprite().getX();
        tile.update();
        assertEquals(initialSpriteX, tile.getSprite().getX(), 0.001);
    }

    @Test
    void testMultipleUpdateCallsAccumulateCorrectly() {
        // Three frames at speed 3.0 should move tile 9px total
        double initialY = tile.getY();
        tile.update();
        tile.update();
        tile.update();
        assertEquals(initialY + 9.0, tile.getY(), 0.001);
    }

    @Test
    void testZeroScrollSpeedMeansTileDoesNotMove() {
        tile.setScrollSpeed(0.0);
        double initialY = tile.getY();
        tile.update();
        assertEquals(initialY, tile.getY(), 0.001);
    }
}