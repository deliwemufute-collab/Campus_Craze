package org.example.game;

import org.example.menu.GameOverScene;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import javafx.scene.layout.Pane;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameOverScene.
 * Tests pane setup, final stats display, restart callback.
 *
 * @author Victoria Adeosun
 */
class GameOverSceneTest {

    private GameOverScene gameOverScene;
    private Pane pane;

    @BeforeAll
    static void initToolkit() throws Exception {
        com.sun.javafx.application.PlatformImpl.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        pane = new Pane();
        gameOverScene = new GameOverScene(pane);
    }

    @Test
    @DisplayName("GameOver pane should not be null after construction")
    void testGameOverPaneIsNotNull() {
        assertNotNull(gameOverScene.getGameOverPane());
    }

    @Test
    @DisplayName("GameOver pane should be the same pane passed in")
    void testGameOverPaneIsSameReference() {
        assertSame(pane, gameOverScene.getGameOverPane());
    }

    @Test
    @DisplayName("GameOver pane should have children after construction")
    void testGameOverPaneHasChildren() {
        assertTrue(pane.getChildren().size() > 0);
    }

    @Test
    @DisplayName("setFinalStats should not throw with normal values")
    void testSetFinalStatsDoesNotThrow() {
        assertDoesNotThrow(() -> gameOverScene.setFinalStats(100, 5));
    }

    @Test
    @DisplayName("setFinalStats should work with zero values")
    void testSetFinalStatsWithZeroValues() {
        assertDoesNotThrow(() -> gameOverScene.setFinalStats(0, 0));
    }

    @Test
    @DisplayName("setFinalStats should work with large values")
    void testSetFinalStatsWithLargeValues() {
        assertDoesNotThrow(() -> gameOverScene.setFinalStats(99999, 999));
    }

    @Test
    @DisplayName("setOnRestart should not throw")
    void testSetOnRestartDoesNotThrow() {
        assertDoesNotThrow(() -> gameOverScene.setOnRestart(() -> {}));
    }

    @Test
    @DisplayName("setOnRestart with null should not throw")
    void testNullRestartActionDoesNotThrow() {
        assertDoesNotThrow(() -> gameOverScene.setOnRestart(null));
    }

    @Test
    @DisplayName("ScoreManager should trigger game over when lives reach 0")
    void testScoreManagerTriggersGameOver() {
        HUDManager hud = new HUDManager(new Pane());
        ScoreManager scoreManager = new ScoreManager(hud);
        boolean[] gameOverFired = {false};
        scoreManager.setOnGameOver(() -> gameOverFired[0] = true);
        scoreManager.loseLife();
        scoreManager.loseLife();
        scoreManager.loseLife();
        assertTrue(gameOverFired[0]);
    }

    @Test
    @DisplayName("ScoreManager should not trigger game over if lives remain")
    void testScoreManagerDoesNotTriggerGameOverEarly() {
        HUDManager hud = new HUDManager(new Pane());
        ScoreManager scoreManager = new ScoreManager(hud);
        boolean[] gameOverFired = {false};
        scoreManager.setOnGameOver(() -> gameOverFired[0] = true);
        scoreManager.loseLife();
        scoreManager.loseLife();
        assertFalse(gameOverFired[0]);
    }
}