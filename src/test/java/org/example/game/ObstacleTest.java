package org.example.game;

import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObstacleTest {

    // ── EXISTING tests — kept passing exactly ─────────────────────────────

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
        assertFalse(new Obstacle(new WritableImage(20, 20), 380.0, 300.0).isOffscreen(300.0));
    }

    @Test
    void isOffscreenReturnsTrueWhenObstacleIsPastScreenHeight() {
        assertTrue(new Obstacle(new WritableImage(20, 20), 380.0, 301.0).isOffscreen(300.0));
    }

    // ── NEW tests ─────────────────────────────────────────────────────────

    @Test
    void fullConstructorUsesCorrectSizeDimensions() {
        Obstacle o = new Obstacle(new WritableImage(48, 60), 512, 0,
                Obstacle.Theme.CITY, Obstacle.Size.SMALL);
        assertEquals(Obstacle.Size.SMALL.w, o.getWidth());
        assertEquals(Obstacle.Size.SMALL.h, o.getHeight());
    }

    @Test
    void getBoundsIsNonEmpty() {
        Obstacle o = new Obstacle(new WritableImage(48, 60), 512, 100,
                Obstacle.Theme.CITY, Obstacle.Size.SMALL);
        var b = o.getBounds();
        assertTrue(b.getWidth() > 0 && b.getHeight() > 0);
    }

    @Test
    void onCollisionWithPlayerSetsFlag() {
        Obstacle o = new Obstacle(new WritableImage(48, 60), 512.0, 300.0,
                Obstacle.Theme.CITY, Obstacle.Size.SMALL);
        Player p = new Player(new WritableImage(64, 128), 480.0, 300.0, 1024.0, 128.0);
        o.onCollision(p);
        assertTrue(o.hasCollidedWithPlayer());
    }

    @Test
    void resetCollisionClearsFlag() {
        Obstacle o = new Obstacle(new WritableImage(48, 60), 512.0, 300.0,
                Obstacle.Theme.CITY, Obstacle.Size.SMALL);
        Player p = new Player(new WritableImage(64, 128), 480.0, 300.0, 1024.0, 128.0);
        o.onCollision(p);
        o.resetCollision();
        assertFalse(o.hasCollidedWithPlayer());
    }

    @Test
    void scrollUpDecreasesY() {
        Obstacle o = new Obstacle(new WritableImage(140, 200), 512.0, 300.0,
                Obstacle.Theme.CITY, Obstacle.Size.LARGE);
        o.scrollUp(5.0);
        assertEquals(295.0, o.getY(), 0.01);
    }
}