package org.example.game;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ObstacleManager} theme timing and wiring.
 */
class ObstacleManagerTest {

    @BeforeAll
    static void initToolkit() {
        PlatformImpl.startup(() -> {});
    }

    @Test
    void setElapsedSeconds_selectsCityBefore52Seconds() {
        ObstacleManager om = new ObstacleManager(
                new Pane(), new WritableImage(1, 1), new WritableImage(1, 1));
        om.setElapsedSeconds(0);
        assertEquals(Obstacle.Theme.CITY, om.getCurrentTheme());
        om.setElapsedSeconds(51.9);
        assertEquals(Obstacle.Theme.CITY, om.getCurrentTheme());
    }

    @Test
    void setElapsedSeconds_selectsParkFrom52To104Seconds() {
        ObstacleManager om = new ObstacleManager(
                new Pane(), new WritableImage(1, 1), new WritableImage(1, 1));
        om.setElapsedSeconds(52);
        assertEquals(Obstacle.Theme.PARK, om.getCurrentTheme());
        om.setElapsedSeconds(103.9);
        assertEquals(Obstacle.Theme.PARK, om.getCurrentTheme());
    }

    @Test
    void setElapsedSeconds_selectsSubwayAtOrAfter104Seconds() {
        ObstacleManager om = new ObstacleManager(
                new Pane(), new WritableImage(1, 1), new WritableImage(1, 1));
        om.setElapsedSeconds(104);
        assertEquals(Obstacle.Theme.SUBWAY, om.getCurrentTheme());
        om.setElapsedSeconds(500);
        assertEquals(Obstacle.Theme.SUBWAY, om.getCurrentTheme());
    }
}
