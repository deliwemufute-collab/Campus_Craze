package org.example.game;

import javafx.scene.layout.Pane;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObstacleManagerTest {

    @Test
    void spawnObstacleAddsObstacleToList() {
        Pane pane = new Pane();
        ObstacleManager manager = new ObstacleManager(pane);

        manager.spawnObstacle(380.0, -100.0);

        assertEquals(1, manager.getObstacles().size());
    }

    @Test
    void spawnObstacleAddsObstacleSpriteToPane() {
        Pane pane = new Pane();
        ObstacleManager manager = new ObstacleManager(pane);

        manager.spawnObstacle(380.0, -100.0);

        Obstacle obstacle = manager.getObstacles().get(0);
        assertEquals(1, pane.getChildren().size());
        assertSame(obstacle.getSprite(), pane.getChildren().get(0));
    }

    @Test
    void spawnObstacleCreatesObstacleWithCorrectInitialValues() {
        Pane pane = new Pane();
        ObstacleManager manager = new ObstacleManager(pane);

        manager.spawnObstacle(380.0, -100.0);

        Obstacle obstacle = manager.getObstacles().get(0);
        assertEquals(380.0, obstacle.getX());
        assertEquals(-100.0, obstacle.getY());
    }

    @Test
    void scrollObstaclesMovesObstacleDownByScrollSpeed() {
        Pane pane = new Pane();
        ObstacleManager manager = new ObstacleManager(pane);

        manager.spawnObstacle(380.0, 0.0);
        Obstacle obstacle = manager.getObstacles().get(0);

        manager.scrollObstacles(800.0);

        assertEquals(3.0, obstacle.getY());
        assertEquals(3.0, obstacle.getSprite().getY());
    }

    @Test
    void scrollObstaclesRespawnsObstacleWhenItGoesOffscreen() {
        Pane pane = new Pane();
        ObstacleManager manager = new ObstacleManager(pane);

        manager.spawnObstacle(380.0, 799.0);
        Obstacle obstacle = manager.getObstacles().get(0);

        manager.scrollObstacles(800.0);

        assertEquals(1, manager.getObstacles().size());
        assertTrue(obstacle.getX() == 380.0 || obstacle.getX() == 512.0 || obstacle.getX() == 640.0);
        assertTrue(obstacle.getY() <= -100.0 && obstacle.getY() >= -599.0);
    }

    @Test
    void respawnAlsoUpdatesSpriteCoordinatesCorrectly() {
        Pane pane = new Pane();
        ObstacleManager manager = new ObstacleManager(pane);

        manager.spawnObstacle(380.0, 900.0);
        Obstacle obstacle = manager.getObstacles().get(0);

        manager.scrollObstacles(800.0);

        assertEquals(obstacle.getY(), obstacle.getSprite().getY());
        assertEquals(obstacle.getX() - 40.0, obstacle.getSprite().getX());
    }

    @Test
    void scrollObstaclesHandlesMultipleObstacles() {
        Pane pane = new Pane();
        ObstacleManager manager = new ObstacleManager(pane);

        manager.spawnObstacle(380.0, 0.0);
        manager.spawnObstacle(512.0, 10.0);

        manager.scrollObstacles(800.0);

        assertEquals(2, manager.getObstacles().size());
        assertEquals(3.0, manager.getObstacles().get(0).getY());
        assertEquals(13.0, manager.getObstacles().get(1).getY());
    }
}