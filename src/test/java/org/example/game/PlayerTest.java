package org.example.game;

import javafx.scene.image.Image;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        Image testImage = new Image("https://via.placeholder.com/50");
        player = new Player(testImage, 100, 200, 500); // screenWidth = 500
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
}