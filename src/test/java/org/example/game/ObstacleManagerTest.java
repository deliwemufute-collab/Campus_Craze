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
        return new Player(new WritableImage(64, 128), leftX, y, 1024.0, 128.0);
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

    // ── NEW tests ─────────────────────────────────────────────────────────

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