package org.example.game;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.image.Image;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class SceneryTileTest {

    private SceneryTile tile;
    private Image testImage;

    // Boot the JavaFX runtime before tests run, as JUnit does not start it automatically
    @BeforeAll
    static void initToolkit() throws Exception {
        PlatformImpl.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        // Use a real local asset so image dimensions are available in tests
        testImage = new Image(
                SceneryTileTest.class.getResourceAsStream("/Tiles/dummy_tile_start.png")
        );
        tile = new SceneryTile(testImage, 50, 100);
    }

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
        // Real local image should have a positive height
        assertTrue(tile.getHeight() > 0);
    }

    @Test
    void testGetHeightFallbackWhenImageNull() {
        // If image is null, getHeight() should return the default 192
        tile.setImage(null);
        assertEquals(192, tile.getHeight());
    }
}