package org.example.game;

import com.sun.javafx.application.PlatformImpl;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD tests for BuildingTile.
 *
 * BuildingTile is a child of SceneryTile that adds Collidable behaviour
 * for the side building overlays. Background SceneryTile no longer implements
 * Collidable (MC's architecture decision), so collision detection only fires
 * on building tiles, not the road surface.
 *
 * Covered:
 *  - Inherits position and scrolling from SceneryTile
 *  - Implements Collidable: getBounds(), updatePosition(), onCollision()
 *  - onCollision() fires without throwing
 *  - IS-A SceneryTile (GameScene can store it in the same tile list)
 *  - IS-A Collidable (GameScene collision loop picks it up correctly)
 *  - SceneryTile itself is NOT Collidable (confirms the fix)
 *
 * @author Emmanuel Ayobanjo
 */
public class BuildingTileTest {

    private BuildingTile buildingTile;
    private Image testImage;

    @BeforeAll
    static void initToolkit() throws Exception {
        PlatformImpl.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        testImage = new WritableImage(100, 320);
        buildingTile = new BuildingTile(testImage, 0, 100, 3.0);
    }

    // -----------------------------------------------------------------------
    // Inheritance — BuildingTile IS-A SceneryTile
    // -----------------------------------------------------------------------

    @Test
    void testBuildingTileIsInstanceOfSceneryTile() {
        assertTrue(buildingTile instanceof SceneryTile);
    }

    @Test
    void testBuildingTileIsInstanceOfGameObject() {
        assertTrue(buildingTile instanceof GameObject);
    }

    @Test
    void testInheritedInitialPosition() {
        assertEquals(0, buildingTile.getX());
        assertEquals(100, buildingTile.getY());
    }

    @Test
    void testInheritedScrollSpeedStored() {
        assertEquals(3.0, buildingTile.getScrollSpeed());
    }

    @Test
    void testInheritedUpdateScrollsTileDown() {
        double initialY = buildingTile.getY();
        buildingTile.update();
        assertEquals(initialY + 3.0, buildingTile.getY(), 0.001);
    }

    @Test
    void testInheritedUpdateSyncsSpriteY() {
        buildingTile.update();
        assertEquals(buildingTile.getY(), buildingTile.getSprite().getY(), 0.001);
    }

    @Test
    void testInheritedGetSpriteNotNull() {
        assertNotNull(buildingTile.getSprite());
    }

    @Test
    void testInheritedSetImageSwapsImage() {
        Image newImage = new WritableImage(100, 320);
        buildingTile.setImage(newImage);
        assertEquals(newImage, buildingTile.getSprite().getImage());
    }

    // -----------------------------------------------------------------------
    // Collidable contract — BuildingTile IS-A Collidable
    // -----------------------------------------------------------------------

    @Test
    void testBuildingTileImplementsCollidable() {
        assertTrue(buildingTile instanceof Collidable);
    }

    @Test
    void testGetBoundsReturnsNonNull() {
        Bounds bounds = buildingTile.getBounds();
        assertNotNull(bounds);
    }

    @Test
    void testOnCollisionDoesNotThrow() {
        // onCollision() must be callable without crashing
        assertDoesNotThrow(() ->
                buildingTile.onCollision(buildingTile));
    }

    @Test
    void testOnCollisionWithNullDoesNotThrow() {
        // Defensive: passing null should not cause an NPE
        assertDoesNotThrow(() ->
                buildingTile.onCollision(null));
    }

    @Test
    void testUpdatePositionSyncsSpriteToLogicalCoords() {
        buildingTile.setX(50);
        buildingTile.setY(200);
        buildingTile.updatePosition();
        assertEquals(50,  buildingTile.getSprite().getX(), 0.001);
        assertEquals(200, buildingTile.getSprite().getY(), 0.001);
    }

    // -----------------------------------------------------------------------
    // Architecture check — plain SceneryTile must NOT be Collidable
    // -----------------------------------------------------------------------

    @Test
    void testPlainSceneryTileIsNotCollidable() {
        SceneryTile plain = new SceneryTile(testImage, 0, 0, 3.0);
        assertFalse(plain instanceof Collidable,
                "SceneryTile must not implement Collidable — only BuildingTile should");
    }
}