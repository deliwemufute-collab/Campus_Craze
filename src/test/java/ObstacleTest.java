package org.example.game;

import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObstacleTest {

    @Test
    void constructorSetsPositionAndSpritePropertiesCorrectly() {
        Obstacle obstacle = new Obstacle(new WritableImage(20, 20), 380.0, -50.0);

        assertEquals(380.0, obstacle.getX());
        assertEquals(-50.0, obstacle.getY());
        assertEquals(340.0, obstacle.getSprite().getX());
        assertEquals(-50.0, obstacle.getSprite().getY());
        assertEquals(80.0, obstacle.getSprite().getFitWidth());
        assertEquals(80.0, obstacle.getSprite().getFitHeight());
    }

    @Test
    void setXUpdatesLogicalXAndSpriteX() {
        Obstacle obstacle = new Obstacle(new WritableImage(20, 20), 380.0, 0.0);

        obstacle.setX(512.0);

        assertEquals(512.0, obstacle.getX());
        assertEquals(472.0, obstacle.getSprite().getX());
    }

    @Test
    void setYUpdatesLogicalYAndSpriteY() {
        Obstacle obstacle = new Obstacle(new WritableImage(20, 20), 380.0, 0.0);

        obstacle.setY(150.0);

        assertEquals(150.0, obstacle.getY());
        assertEquals(150.0, obstacle.getSprite().getY());
    }

    @Test
    void scrollDownMovesObstacleVerticallyByGivenSpeed() {
        Obstacle obstacle = new Obstacle(new WritableImage(20, 20), 380.0, 10.0);

        obstacle.scrollDown(7.0);

        assertEquals(17.0, obstacle.getY());
        assertEquals(17.0, obstacle.getSprite().getY());
    }

    @Test
    void isOffscreenReturnsFalseWhenObstacleIsWithinScreenHeight() {
        Obstacle obstacle = new Obstacle(new WritableImage(20, 20), 380.0, 300.0);

        assertFalse(obstacle.isOffscreen(300.0));
    }

    @Test
    void isOffscreenReturnsTrueWhenObstacleIsPastScreenHeight() {
        Obstacle obstacle = new Obstacle(new WritableImage(20, 20), 380.0, 301.0);

        assertTrue(obstacle.isOffscreen(300.0));
    }
}