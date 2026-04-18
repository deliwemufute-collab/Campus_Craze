package org.example.game;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.image.Image;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Player collision with side BuildingTiles and playable-area boundaries.
 *
 * Covers the remaining Milestone 2 checklist items:
 *   ✓ Handle collisions with Player  (BuildingTile.onCollision / Player.onCollision)
 *   ✓ Define playable area boundaries (roadMinX / roadMaxX clamping)
 *   ✓ Test Player collision with side buildings
 *
 * @author Emmanuel Ayobanjo
 */
public class PlayerBuildingCollisionTest {

    private static final double SCREEN_WIDTH   = 1024;
    private static final double SCREEN_HEIGHT  = 768;
    private static final double OVERLAY_WIDTH  = 100;  // simulated building column width
    private static final double SCROLL_SPEED   = 3.0;

    private Player      player;
    private BuildingTile leftBuilding;
    private BuildingTile rightBuilding;
    private Image        dummyImage;

    @BeforeAll
    static void initToolkit() throws Exception {
        PlatformImpl.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        // Use a small synthetic image so width is known and fits inside road bounds
        dummyImage = new javafx.scene.image.WritableImage(50, 80);

        player = new Player(dummyImage,
                SCREEN_WIDTH / 2, SCREEN_HEIGHT - 150,
                OVERLAY_WIDTH,
                SCREEN_WIDTH - OVERLAY_WIDTH);

        leftBuilding  = new BuildingTile(new javafx.scene.image.WritableImage(50, 80),
                0, 300, SCROLL_SPEED);
        rightBuilding = new BuildingTile(new javafx.scene.image.WritableImage(50, 80),
                SCREEN_WIDTH - OVERLAY_WIDTH, 300, SCROLL_SPEED);
    }

    // -----------------------------------------------------------------------
    // Road boundary — playable area clamping
    // -----------------------------------------------------------------------

    @Test
    void testRoadMinXIsOverlayWidth() {
        assertEquals(OVERLAY_WIDTH, player.getRoadMinX(), 0.001,
                "Road left edge should equal overlay width");
    }

    @Test
    void testRoadMaxXIsScreenWidthMinusOverlayWidth() {
        assertEquals(SCREEN_WIDTH - OVERLAY_WIDTH, player.getRoadMaxX(), 0.001,
                "Road right edge should equal screenWidth - overlayWidth");
    }

    @Test
    void testPlayerCannotMoveLeftOfRoadBoundary() {
        // Force the player hard left beyond the road edge
        player.setX(0);
        player.updatePosition();
        assertTrue(player.getX() >= player.getRoadMinX(),
                "Player X must not go left of roadMinX after updatePosition()");
    }

    @Test
    void testPlayerCannotMoveRightOfRoadBoundary() {
        // Force the player hard right beyond the road edge
        player.setX(SCREEN_WIDTH);
        player.updatePosition();
        assertTrue(player.getX() + player.getWidth() <= player.getRoadMaxX() + 0.001,
                "Player right edge must not exceed roadMaxX after updatePosition()");
    }

    @Test
    void testPlayerStartsInsideRoadBoundaries() {
        assertTrue(player.getX() >= player.getRoadMinX(),
                "Player should start inside road left boundary");
        assertTrue(player.getX() + player.getWidth() <= player.getRoadMaxX() + 0.001,
                "Player should start inside road right boundary");
    }

    // -----------------------------------------------------------------------
    // Collision with left BuildingTile — player snaps to road min
    // -----------------------------------------------------------------------

    @Test
    void testPlayerCollisionWithLeftBuildingDoesNotThrow() {
        assertDoesNotThrow(() -> player.onCollision(leftBuilding));
    }

    @Test
    void testPlayerSnapsToRoadMinAfterLeftBuildingCollision() {
        // Simulate player drifting into the left building
        player.setX(OVERLAY_WIDTH - 10);  // partially overlapping
        player.onCollision(leftBuilding);
        assertEquals(player.getRoadMinX(), player.getX(), 0.001,
                "Player should snap to roadMinX after colliding with left building");
    }

    @Test
    void testPlayerSpriteXMatchesLogicalXAfterLeftCollision() {
        player.setX(OVERLAY_WIDTH - 10);
        player.onCollision(leftBuilding);
        assertEquals(player.getX(), player.getSprite().getX(), 0.001,
                "Sprite X must stay in sync with logical X after left-building collision");
    }

    // -----------------------------------------------------------------------
    // Collision with right BuildingTile — player snaps to road max
    // -----------------------------------------------------------------------

    @Test
    void testPlayerCollisionWithRightBuildingDoesNotThrow() {
        assertDoesNotThrow(() -> player.onCollision(rightBuilding));
    }

    @Test
    void testPlayerSnapsToRoadMaxAfterRightBuildingCollision() {
        // Simulate player drifting into the right building
        player.setX(SCREEN_WIDTH - OVERLAY_WIDTH + 5);  // partially overlapping
        player.onCollision(rightBuilding);
        double expectedMax = player.getRoadMaxX() - player.getWidth();
        assertEquals(expectedMax, player.getX(), 0.001,
                "Player right edge should snap to roadMaxX after colliding with right building");
    }

    @Test
    void testPlayerSpriteXMatchesLogicalXAfterRightCollision() {
        player.setX(SCREEN_WIDTH - OVERLAY_WIDTH + 5);
        player.onCollision(rightBuilding);
        assertEquals(player.getX(), player.getSprite().getX(), 0.001,
                "Sprite X must stay in sync with logical X after right-building collision");
    }

    // -----------------------------------------------------------------------
    // BuildingTile.onCollision — does not throw, handles null safely
    // -----------------------------------------------------------------------

    @Test
    void testBuildingTileOnCollisionWithPlayerDoesNotThrow() {
        assertDoesNotThrow(() -> leftBuilding.onCollision(player));
    }

    @Test
    void testBuildingTileOnCollisionWithNullDoesNotThrow() {
        assertDoesNotThrow(() -> leftBuilding.onCollision(null));
    }
}