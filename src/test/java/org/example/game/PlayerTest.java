package org.example.game;

import javafx.scene.image.Image;
import org.junit.jupiter.api.*;
import com.sun.javafx.application.PlatformImpl;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private Player player;


    // Boot the JavaFX runtime before tests run, as JUnit does not start it automatically
    @BeforeAll
    static void initToolkit() throws Exception {
        PlatformImpl.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        Image testImage = new Image(
                PlayerTest.class.getResourceAsStream("/Player/player_dummy.png"));
        player = new Player(testImage, 100, 200, 0.0, 500.0);
    }

    @Test
    void testInitialPosition() {
        assertEquals(100, player.getX());
        assertEquals(200, player.getY());
    }

    @Test
    void testMoveLeft() {
        double oldX = player.getX();

        player.moveLeft();      // sets flag
        player.updatePosition(); // applies movement

        assertTrue(player.getX() < oldX);
    }

    @Test
    void testMoveRight() {
        double oldX = player.getX();

        player.moveRight();
        player.updatePosition();

        assertTrue(player.getX() > oldX);
    }

    @Test
    void testStopLeft() {
        player.moveLeft();
        player.stopLeft();

        double oldX = player.getX();
        player.updatePosition();

        assertEquals(oldX, player.getX());
    }

    @Test
    void testStopRight() {
        player.moveRight();
        player.stopRight();

        double oldX = player.getX();
        player.updatePosition();

        assertEquals(oldX, player.getX());
    }

    @Test
    void testLeftBoundary() {
        player.setX(0);

        player.moveLeft();
        player.updatePosition();

        assertEquals(0, player.getX()); // should not go below 0
    }

    @Test
    void testRightBoundary() {
        double maxX = 500 - player.getWidth();

        player.setX(maxX);

        player.moveRight();
        player.updatePosition();

        assertEquals(maxX, player.getX()); // should not exceed boundary
    }

    @Test
    void testUpdatePositionSyncsSprite() {
        player.setX(150);
        player.setY(250);

        player.updatePosition();

        assertEquals(150, player.getSprite().getX());
        assertEquals(250, player.getSprite().getY());
    }

    // -----------------------------------------------------------------------
// Playable area — road boundary clamping (Milestone 2)
// -----------------------------------------------------------------------

    @Test
    void testPlayerClampedAtRoadLeftBoundary() {
        // Create player with explicit road boundaries
        Image img = new Image(
                PlayerTest.class.getResourceAsStream("/Player/player_dummy.png"));
        Player roadPlayer = new Player(img, 512, 618, 170.0, 850.0);

        // Force position left of road edge then update
        roadPlayer.setX(0);
        roadPlayer.updatePosition();

        assertTrue(roadPlayer.getX() >= 170.0,
                "Player must not go left of roadLeft (170)");
    }

    @Test
    void testPlayerClampedAtRoadRightBoundary() {
        Image img = new Image(
                PlayerTest.class.getResourceAsStream("/Player/player_dummy.png"));
        Player roadPlayer = new Player(img, 512, 618, 170.0, 850.0);

        // Force position right of road edge then update
        roadPlayer.setX(900);
        roadPlayer.updatePosition();

        assertTrue(roadPlayer.getX() + roadPlayer.getWidth() <= 850.0,
                "Player right edge must not exceed roadRight (850)");
    }

    @Test
    void testPlayerStartsInsideRoadBoundaries() {
        Image img = new Image(
                PlayerTest.class.getResourceAsStream("/Player/player_dummy.png"));
        Player roadPlayer = new Player(img, 512, 618, 170.0, 850.0);

        assertTrue(roadPlayer.getX() >= 170.0,
                "Player should start inside road left boundary");
        assertTrue(roadPlayer.getX() + roadPlayer.getWidth() <= 850.0,
                "Player should start inside road right boundary");
    }
}