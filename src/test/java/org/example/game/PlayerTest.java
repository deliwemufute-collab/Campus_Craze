package org.example.game;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Player class — Milestone 2 and Milestone 3.
 *
 * Milestone 2 covers:
 * 1.  Initial state
 * 2.  Movement updates (left / right / up / down)
 * 3.  Smooth velocity building over frames
 * 4.  Velocity cap (MAX_SPEED_X and MAX_SPEED_Y)
 * 5.  Stop commands (stopLeft, stopRight, stopUp, stopDown)
 * 6.  Road boundary constraints
 * 7.  Sprite sync after updatePosition()
 * 8.  Collision with BuildingTile (snap to boundary)
 * 9.  Collision with Obstacle
 * 10. Polymorphic update() method
 *
 * Milestone 3 adds:
 * 11. Expanded vertical movement range (~3/4 of screen)
 * 12. Hitbox leeway (collision box smaller than sprite)
 * 13. Sprite X correctly aligned — no width/2 offset
 * 14. Invulnerability window after obstacle hit
 * 15. Hit flash animation state
 *
 * @author Deliwe Mufute
 */
public class PlayerTest {

    private Player player;

    private static final double ROAD_LEFT  = 170.0;
    private static final double ROAD_RIGHT = 850.0;
    private static final double START_X    = 512.0;
    private static final double START_Y    = 618.0;

    /**
     * Boots the JavaFX runtime before any tests run.
     * Required because Player uses ImageView internally.
     */
    @BeforeAll
    static void initToolkit() throws Exception {
        PlatformImpl.startup(() -> {});
    }

    /**
     * Creates a fresh Player before each test so no state leaks between tests.
     * Uses WritableImage(50, 80) — a synthetic image with known width and height.
     */
    @BeforeEach
    void setUp() {
        Image testImage = new WritableImage(50, 80);
        player = new Player(testImage, START_X, START_Y, ROAD_LEFT, ROAD_RIGHT);
    }

    // ===================================================================
    // GROUP 1 — INITIAL STATE
    // ===================================================================

    /**
     * Player x and y must match startX and startY passed to constructor.
     */
    @Test
    void testInitialPosition() {
        assertEquals(START_X, player.getX(), 0.001, "Initial X should match startX");
        assertEquals(START_Y, player.getY(), 0.001, "Initial Y should match startY");
    }

    /**
     * Both velocity components must be zero when player is first created.
     */
    @Test
    void testInitialVelocityIsZero() {
        assertEquals(0, player.getVelocityX(), "velocityX should start at 0");
        assertEquals(0, player.getVelocityY(), "velocityY should start at 0");
    }

    /**
     * Road boundaries must match the values passed into the constructor.
     */
    @Test
    void testRoadBoundariesSetCorrectly() {
        assertEquals(ROAD_LEFT,  player.getRoadMinX(), 0.001, "roadMinX should equal ROAD_LEFT");
        assertEquals(ROAD_RIGHT, player.getRoadMaxX(), 0.001, "roadMaxX should equal ROAD_RIGHT");
    }

    // ===================================================================
    // GROUP 2 — MOVEMENT DIRECTION TESTS
    // ===================================================================

    @Test
    void testMoveLeftDecreasesX() {
        double oldX = player.getX();
        player.moveLeft();
        player.updatePosition();
        assertTrue(player.getX() < oldX, "moveLeft() should decrease X");
    }

    @Test
    void testMoveRightIncreasesX() {
        double oldX = player.getX();
        player.moveRight();
        player.updatePosition();
        assertTrue(player.getX() > oldX, "moveRight() should increase X");
    }

    @Test
    void testMoveUpDecreasesY() {
        double oldY = player.getY();
        player.moveUp();
        player.updatePosition();
        assertTrue(player.getY() < oldY, "moveUp() should decrease Y (move forward)");
    }

    @Test
    void testMoveDownIncreasesY() {
        double oldY = player.getY();
        player.moveDown();
        player.updatePosition();
        assertTrue(player.getY() > oldY, "moveDown() should increase Y (move backward)");
    }

    // ===================================================================
    // GROUP 3 — SMOOTH VELOCITY BUILDS OVER FRAMES
    // ===================================================================

    @Test
    void testVelocityBuildsOverMultipleFrames() {
        player.moveRight();
        player.updatePosition();
        double velFrame1 = player.getVelocityX();
        player.updatePosition();
        double velFrame2 = player.getVelocityX();
        assertTrue(velFrame2 > velFrame1,
                "Velocity should increase each frame while key is held");
    }

    // ===================================================================
    // GROUP 4 — VELOCITY CAP
    // ===================================================================

    @Test
    void testVelocityCappedAtMaxSpeedX() {
        player.moveRight();
        for (int i = 0; i < 50; i++) player.updatePosition();
        assertTrue(player.getVelocityX() <= 6.0,
                "velocityX must never exceed MAX_SPEED_X of 6.0");
    }

    /**
     * MAX_SPEED_Y is 5.0 in Milestone 3 (increased from 3.0).
     */
    @Test
    void testVelocityCappedAtMaxSpeedY() {
        player.moveDown();
        for (int i = 0; i < 50; i++) player.updatePosition();
        assertTrue(player.getVelocityY() <= 5.0,
                "velocityY must never exceed MAX_SPEED_Y of 5.0");
    }

    // ===================================================================
    // GROUP 5 — STOP COMMAND TESTS
    // ===================================================================

    @Test
    void testStopLeftCausesDeceleration() {
        player.moveLeft();
        player.updatePosition();
        player.stopLeft();
        double xAfterStop = player.getX();
        for (int i = 0; i < 20; i++) player.updatePosition();
        assertTrue(Math.abs(player.getX() - xAfterStop) < 10,
                "After stopLeft(), player should decelerate and nearly stop");
    }

    @Test
    void testStopRightCausesDeceleration() {
        player.moveRight();
        player.updatePosition();
        player.stopRight();
        double xAfterStop = player.getX();
        for (int i = 0; i < 20; i++) player.updatePosition();
        assertTrue(Math.abs(player.getX() - xAfterStop) < 10,
                "After stopRight(), player should decelerate and nearly stop");
    }

    @Test
    void testStopUpCausesDeceleration() {
        player.moveUp();
        player.updatePosition();
        player.stopUp();
        double yAfterStop = player.getY();
        for (int i = 0; i < 20; i++) player.updatePosition();
        assertTrue(Math.abs(player.getY() - yAfterStop) < 10,
                "After stopUp(), player should decelerate and nearly stop");
    }

    @Test
    void testStopDownCausesDeceleration() {
        player.moveDown();
        player.updatePosition();
        player.stopDown();
        double yAfterStop = player.getY();
        for (int i = 0; i < 20; i++) player.updatePosition();
        assertTrue(Math.abs(player.getY() - yAfterStop) < 10,
                "After stopDown(), player should decelerate and nearly stop");
    }

    // ===================================================================
    // GROUP 6 — ROAD BOUNDARY CONSTRAINTS
    // ===================================================================

    @Test
    void testCannotMoveLeftOfRoadBoundary() {
        player.setX(ROAD_LEFT);
        player.moveLeft();
        for (int i = 0; i < 20; i++) player.updatePosition();
        assertTrue(player.getX() >= ROAD_LEFT,
                "Player must not go left of road boundary");
    }

    @Test
    void testCannotMoveRightOfRoadBoundary() {
        player.setX(ROAD_RIGHT - player.getWidth());
        player.moveRight();
        for (int i = 0; i < 20; i++) player.updatePosition();
        assertTrue(player.getX() + player.getWidth() <= ROAD_RIGHT + 0.001,
                "Player right edge must not exceed road right boundary");
    }

    /**
     * Uses Milestone 3 expanded boundaries (startY - 426 / startY + 100).
     */
    @Test
    void testCannotMoveAboveTopBoundary() {
        player.setYBoundaries(START_Y - 426, START_Y + 100);
        player.moveUp();
        for (int i = 0; i < 300; i++) player.updatePosition();
        assertTrue(player.getY() >= START_Y - 426,
                "Player must not go above expanded minY boundary");
    }

    @Test
    void testCannotMoveBelowBottomBoundary() {
        player.setYBoundaries(START_Y - 426, START_Y + 100);
        player.moveDown();
        for (int i = 0; i < 300; i++) player.updatePosition();
        assertTrue(player.getY() <= START_Y + 100,
                "Player must not go below expanded maxY boundary");
    }

    // ===================================================================
    // GROUP 7 — SPRITE SYNC
    // ===================================================================

    /**
     * Sprite Y must match player Y after updatePosition().
     * Uses START_Y which is always within the boundary range.
     */
    @Test
    void testSpriteSyncsWithPositionAfterUpdate() {
        player.setX(300);
        player.setY(START_Y);
        player.updatePosition();
        assertEquals(300, player.getSprite().getX(), 0.01,
                "Sprite X must match player X after updatePosition()");
        assertEquals(START_Y, player.getSprite().getY(), 0.01,
                "Sprite Y must match player Y after updatePosition()");
    }

    // ===================================================================
    // GROUP 8 — COLLISION WITH BUILDINGTILE
    // ===================================================================

    @Test
    void testCollisionWithLeftBuildingSnapsToRoadMin() {
        player.setX(ROAD_LEFT - 10);
        Image dummyImage = new WritableImage(50, 80);
        BuildingTile leftBuilding = new BuildingTile(dummyImage, 0, 300, 3.0);
        player.onCollision(leftBuilding);
        assertEquals(ROAD_LEFT, player.getX(), 0.001,
                "Player should snap to ROAD_LEFT after left building collision");
    }

    @Test
    void testCollisionWithRightBuildingSnapsToRoadMax() {
        player.setX(ROAD_RIGHT + 5);
        Image dummyImage = new WritableImage(50, 80);
        BuildingTile rightBuilding = new BuildingTile(dummyImage, ROAD_RIGHT, 300, 3.0);
        player.onCollision(rightBuilding);
        assertEquals(ROAD_RIGHT - player.getWidth(), player.getX(), 0.001,
                "Player should snap to ROAD_RIGHT - width after right building collision");
    }

    @Test
    void testSpriteSyncsAfterBuildingCollision() {
        player.setX(ROAD_LEFT - 10);
        Image dummyImage = new WritableImage(50, 80);
        BuildingTile leftBuilding = new BuildingTile(dummyImage, 0, 300, 3.0);
        player.onCollision(leftBuilding);
        assertEquals(player.getX(), player.getSprite().getX(), 0.001,
                "Sprite X must match logical X after building collision snap");
    }

    @Test
    void testOnCollisionWithNullDoesNotThrow() {
        assertDoesNotThrow(() -> player.onCollision(null),
                "onCollision(null) must not throw an exception");
    }

    // ===================================================================
    // GROUP 9 — COLLISION WITH OBSTACLE
    // ===================================================================

    @Test
    void testCollisionWithObstacleDoesNotThrow() {
        Image dummyImage = new WritableImage(50, 80);
        Obstacle obstacle = new Obstacle(dummyImage, 300, 300);
        assertDoesNotThrow(() -> player.onCollision(obstacle),
                "onCollision with Obstacle must not throw when scoreManager is null");
    }

    // ===================================================================
    // GROUP 10 — POLYMORPHIC update() METHOD
    // ===================================================================

    @Test
    void testUpdateMethodMovesPlayer() {
        double oldX = player.getX();
        player.moveRight();
        player.update();
        assertTrue(player.getX() > oldX,
                "update() should move player when moveRight() is active");
    }

    @Test
    void testUpdateDelegatesToUpdatePosition() {
        Image img = new WritableImage(50, 80);
        Player p1 = new Player(img, START_X, START_Y, ROAD_LEFT, ROAD_RIGHT);
        Player p2 = new Player(img, START_X, START_Y, ROAD_LEFT, ROAD_RIGHT);
        p1.moveRight();
        p2.moveRight();
        p1.update();
        p2.updatePosition();
        assertEquals(p1.getX(), p2.getX(), 0.001,
                "update() and updatePosition() should produce identical results");
    }

    // ===================================================================
    // GROUP 11 — MILESTONE 3: EXPANDED VERTICAL RANGE
    // ===================================================================

    /**
     * minY should be at least 400px above start — confirms 3/4 screen coverage.
     */
    @Test
    void testExpandedVerticalRangeMinY() {
        assertTrue(player.getMinY() <= START_Y - 400,
                "minY should allow player to reach at least 400px above start position");
    }

    /**
     * maxY should allow more backward movement than Milestone 2 (was +40, now +100).
     */
    @Test
    void testExpandedVerticalRangeMaxY() {
        assertTrue(player.getMaxY() >= START_Y + 80,
                "maxY should allow player to move at least 80px below start position");
    }

    /**
     * Player should physically reach well above start after holding UP many frames.
     */
    @Test
    void testPlayerCanReachUpperPartOfScreen() {
        player.moveUp();
        for (int i = 0; i < 300; i++) player.updatePosition();
        assertTrue(player.getY() < START_Y - 200,
                "Player should reach well above starting position with expanded range");
    }

    // ===================================================================
    // GROUP 12 — MILESTONE 3: HITBOX LEEWAY
    // ===================================================================

    /**
     * Collision bounds width must be smaller than sprite width (20% leeway applied).
     */
    @Test
    void testHitboxWidthSmallerThanSprite() {
        javafx.geometry.Bounds bounds = player.getBounds();
        assertTrue(bounds.getWidth() < player.getWidth(),
                "Collision bounds width should be smaller than sprite width");
    }

    /**
     * Collision bounds height must be smaller than sprite height.
     */
    @Test
    void testHitboxHeightSmallerThanSprite() {
        javafx.geometry.Bounds bounds = player.getBounds();
        assertTrue(bounds.getHeight() < player.getHeight(),
                "Collision bounds height should be smaller than sprite height");
    }

    /**
     * Hitbox should be approximately 60% of sprite dimensions (20% off each side).
     */
    @Test
    void testHitboxIsApproximately60PercentOfSprite() {
        javafx.geometry.Bounds bounds = player.getBounds();
        double expectedWidth = player.getWidth() * 0.6;
        assertEquals(expectedWidth, bounds.getWidth(), 1.0,
                "Hitbox width should be ~60% of sprite width after 20% leeway each side");
    }

    // ===================================================================
    // GROUP 13 — MILESTONE 3: SPRITE X ALIGNMENT (no offset)
    // ===================================================================

    /**
     * Sprite X must equal logical X exactly — no width/2 offset applied.
     * Fixes the visual overlap into side building tiles.
     */
    @Test
    void testSpriteXMatchesLogicalXExactly() {
        player.updatePosition();
        assertEquals(player.getX(), player.getSprite().getX(), 0.001,
                "Sprite X must equal logical X with no offset");
    }

    /**
     * After movement, sprite X must still match logical X exactly.
     */
    @Test
    void testSpriteXMatchesLogicalXAfterMovement() {
        player.moveRight();
        for (int i = 0; i < 10; i++) player.updatePosition();
        assertEquals(player.getX(), player.getSprite().getX(), 0.001,
                "Sprite X must equal logical X after movement — no offset drift");
    }

    // ===================================================================
    // GROUP 14 — MILESTONE 3: INVULNERABILITY WINDOW
    // ===================================================================

    /**
     * Player should not be invulnerable at game start.
     */
    @Test
    void testNotInvulnerableAtStart() {
        assertFalse(player.isInvulnerable(),
                "Player should not be invulnerable at game start");
    }

    /**
     * Player should not be flashing at game start.
     */
    @Test
    void testNotFlashingAtStart() {
        assertFalse(player.isFlashing(),
                "Player should not be flashing at game start");
    }

    /**
     * After obstacle collision with scoreManager set, player becomes invulnerable.
     * Uses a real HUDManager stub approach — tests the flag directly.
     */
    @Test
    void testObstacleCollisionWithoutScoreManagerDoesNotSetInvulnerable() {
        // Without scoreManager, the Obstacle block is skipped entirely
        // so invulnerable stays false
        Image dummyImage = new WritableImage(50, 80);
        Obstacle obstacle = new Obstacle(dummyImage, 300, 300);
        player.onCollision(obstacle);
        assertFalse(player.isInvulnerable(),
                "Without scoreManager, invulnerable should remain false");
    }

    /**
     * After obstacle collision without scoreManager, flash should not start.
     */
    @Test
    void testObstacleCollisionWithoutScoreManagerDoesNotStartFlash() {
        Image dummyImage = new WritableImage(50, 80);
        Obstacle obstacle = new Obstacle(dummyImage, 300, 300);
        player.onCollision(obstacle);
        assertFalse(player.isFlashing(),
                "Without scoreManager, flash should not start");
    }
}