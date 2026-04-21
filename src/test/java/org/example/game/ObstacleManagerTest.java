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
    void setElapsedSeconds_selectsCityForFirstHundredSeconds() {
        ObstacleManager om = new ObstacleManager(
                new Pane(), new WritableImage(1, 1), new WritableImage(1, 1));
        om.setElapsedSeconds(0);
        assertEquals(Obstacle.Theme.CITY, om.getCurrentTheme());
        om.setElapsedSeconds(99.9);
        assertEquals(Obstacle.Theme.CITY, om.getCurrentTheme());
    }

    @Test
    void setElapsedSeconds_selectsParkForSecondHundredSeconds() {
        ObstacleManager om = new ObstacleManager(
                new Pane(), new WritableImage(1, 1), new WritableImage(1, 1));
        om.setElapsedSeconds(100);
        assertEquals(Obstacle.Theme.PARK, om.getCurrentTheme());
        om.setElapsedSeconds(199.9);
        assertEquals(Obstacle.Theme.PARK, om.getCurrentTheme());
    }

    @Test
    void setElapsedSeconds_selectsSubwayAfterTwoHundredSeconds() {
        ObstacleManager om = new ObstacleManager(
                new Pane(), new WritableImage(1, 1), new WritableImage(1, 1));
        om.setElapsedSeconds(200);
        assertEquals(Obstacle.Theme.SUBWAY, om.getCurrentTheme());
        om.setElapsedSeconds(500);
        assertEquals(Obstacle.Theme.SUBWAY, om.getCurrentTheme());
    }
}
