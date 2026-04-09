package org.example.game;

import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObstacleManagerTest {

    private static final WritableImage OBS  = new WritableImage(48, 60);
    private static final WritableImage COIN = new WritableImage(32, 32);

    private ObstacleManager mgr() {
        return new ObstacleManager(new Pane(), OBS, COIN);
    }

    private Player player(double leftX, double y) {
        return new Player(new WritableImage(64, 128), leftX, y, 1024.0);
    }

    // ── EXISTING tests — kept passing ────────────────────────────────────

    @Test
    void spawnObstacleAddsObstacleToList() {
        ObstacleManager m = mgr();
        m.spawnObstacle(380.0, -100.0);
        assertEquals(1, m.getObstacles().size());
    }

    @Test
    void spawnObstacleAddsObstacleSpriteToPane() {
        Pane pane = new Pane();
        ObstacleManager m = new ObstacleManager(pane, OBS, COIN);
        m.spawnObstacle(380.0, -100.0);
        Obstacle obs = m.getObstacles().get(0);
        assertEquals(1, pane.getChildren().size());
        assertSame(obs.getSprite(), pane.getChildren().get(0));
    }

    @Test
    void spawnObstacleCreatesObstacleWithCorrectInitialValues() {
        ObstacleManager m = mgr();
        m.spawnObstacle(380.0, -100.0);
        assertEquals(380.0,  m.getObstacles().get(0).getX());
        assertEquals(-100.0, m.getObstacles().get(0).getY());
    }

    @Test
    void scrollObstaclesMovesObstacleDownByScrollSpeed() {
        ObstacleManager m = mgr();
        m.spawnObstacle(380.0, 0.0);
        double before = m.getObstacles().get(0).getY();
        m.scrollObstacles(800.0);
        assertTrue(m.getObstacles().get(0).getY() > before);
    }

    @Test
    void scrollObstaclesRespawnsObstacleWhenItGoesOffscreen() {
        ObstacleManager m = mgr();
        m.spawnObstacle(380.0, 799.0);
        Obstacle obs = m.getObstacles().get(0);
        m.scrollObstacles(800.0);
        assertEquals(1, m.getObstacles().size());
        assertTrue(obs.getY() < 0, "should be repositioned above screen");
    }

    @Test
    void respawnAlsoUpdatesSpriteCoordinatesCorrectly() {
        ObstacleManager m = mgr();
        m.spawnObstacle(380.0, 900.0);
        m.scrollObstacles(800.0);
        Obstacle obs = m.getObstacles().get(0);
        assertEquals(obs.getY(), obs.getSprite().getY(), 0.01);
        assertEquals(obs.getX() - obs.getWidth() / 2, obs.getSprite().getX(), 0.01);
    }

    @Test
    void scrollObstaclesHandlesMultipleObstacles() {
        ObstacleManager m = mgr();
        m.spawnObstacle(380.0, 0.0);
        m.spawnObstacle(512.0, 10.0);
        double y0 = m.getObstacles().get(0).getY();
        double y1 = m.getObstacles().get(1).getY();
        m.scrollObstacles(800.0);
        assertTrue(m.getObstacles().get(0).getY() > y0);
        assertTrue(m.getObstacles().get(1).getY() > y1);
        assertEquals(2, m.getObstacles().size());
    }

    // ── NEW tests ─────────────────────────────────────────────────────────

    @Test
    void checkCollisionsDetectsHitSameLane() {
        ObstacleManager m = mgr();
        m.spawnObstacle(512.0, 300.0);
        m.checkCollisions(player(480.0, 300.0));
        assertTrue(m.getObstacles().get(0).hasCollidedWithPlayer());
    }

    @Test
    void checkCollisionsNoHitWhenFarApart() {
        ObstacleManager m = mgr();
        m.spawnObstacle(512.0, 0.0);
        m.checkCollisions(player(480.0, 700.0));
        assertFalse(m.getObstacles().get(0).hasCollidedWithPlayer());
    }

    @Test
    void checkCollisionsNoHitDifferentLane() {
        ObstacleManager m = mgr();
        m.spawnObstacle(380.0, 300.0);
        m.checkCollisions(player(480.0, 300.0));
        assertFalse(m.getObstacles().get(0).hasCollidedWithPlayer());
    }

    @Test
    void spawnCoinAddsCoinToList() {
        ObstacleManager m = mgr();
        m.spawnCoin(512.0, -50.0);
        assertEquals(1, m.getCoins().size());
    }

    @Test
    void spawnCoinReturnsNullWhenOverlappingObstacle() {
        ObstacleManager m = mgr();
        m.spawnObstacle(512.0, 300.0);
        Coin result = m.spawnCoin(512.0, 300.0);
        assertNull(result, "coin should not spawn on top of obstacle");
    }

    @Test
    void coinCollectedWhenPlayerOverlaps() {
        ObstacleManager m = mgr();
        m.spawnCoin(512.0, 300.0);
        m.checkCollisions(player(480.0, 300.0));
        assertTrue(m.getCoins().get(0).isCollected());
    }

    @Test
    void themeIsCityByDefault() {
        assertEquals(Obstacle.Theme.CITY, mgr().getCurrentTheme());
    }

    @Test
    void setElapsedSecondsSwitchesToParkAt100() {
        ObstacleManager m = mgr();
        m.setElapsedSeconds(105.0);
        assertEquals(Obstacle.Theme.PARK, m.getCurrentTheme());
    }

    @Test
    void setElapsedSecondsSwitchesToSubwayAt200() {
        ObstacleManager m = mgr();
        m.setElapsedSeconds(205.0);
        assertEquals(Obstacle.Theme.SUBWAY, m.getCurrentTheme());
    }

    @Test
    void setElapsedSecondsStaysOnSubwayAfter300() {
        ObstacleManager m = mgr();
        m.setElapsedSeconds(305.0);
        assertEquals(Obstacle.Theme.SUBWAY, m.getCurrentTheme());
    }
}