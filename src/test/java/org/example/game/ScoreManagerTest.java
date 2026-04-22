package org.example.game;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import javafx.scene.layout.Pane;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScoreManager.
 * Tests score increment logic, lives decrement on collisions,
 * and HUD updates after player actions.
 *
 * @author Victoria Adeosun
 */
class ScoreManagerTest {

    private ScoreManager scoreManager;
    private HUDManager hud;

    // start JavaFX before any tests run
    @BeforeAll
    static void initToolkit() throws Exception {
        com.sun.javafx.application.PlatformImpl.startup(() -> {});
    }

    // create a fresh ScoreManager before each test
    @BeforeEach
    void setUp() {
        hud = new HUDManager(new Pane());
        scoreManager = new ScoreManager(hud);
    }

    // SCORE TESTS

    // the score should be 0 when the game first starts
    @Test
    @DisplayName("Score should start at 0")
    void testScoreStartsAtZero() {
        assertEquals(0, scoreManager.getScore());
    }

    // every time the game loop runs score goes up by 1
    @Test
    @DisplayName("incrementScore should increase score by 1")
    void testIncrementScoreIncreasesScore() {
        scoreManager.incrementScore();
        assertEquals(1, scoreManager.getScore());
    }

    // calling increment multiple times should keep adding up
    @Test
    @DisplayName("Score should reflect multiple increments")
    void testIncrementScoreMultipleTimes() {
        scoreManager.incrementScore();
        scoreManager.incrementScore();
        scoreManager.incrementScore();
        assertEquals(3, scoreManager.getScore());
    }

    // when score changes the HUD label should show the new value
    @Test
    @DisplayName("incrementScore should update the HUD score label")
    void testScoreUpdatesHUD() {
        scoreManager.incrementScore();
        assertEquals("Score: 000001", hud.getScoreLabel().getText());
    }

    // LIVES TESTS

    // player should start with 3 lives
    @Test
    @DisplayName("Lives should start at 3")
    void testLivesStartAtThree() {
        assertEquals(5, scoreManager.getLives());
    }

    // hitting an obstacle should take away one life
    @Test
    @DisplayName("loseLife should decrease lives by 1")
    void testLoseLifeDecreasesLives() {
        scoreManager.loseLife();
        assertEquals(4, scoreManager.getLives());
    }

    // lives should stop at 0 even if player keeps hitting obstacles
    @Test
    @DisplayName("Lives should not go below 0")
    void testLivesDoNotGoBelowZero() {
        scoreManager.loseLife();
        scoreManager.loseLife();
        scoreManager.loseLife();
        scoreManager.loseLife();
        scoreManager.loseLife();
        assertEquals(0, scoreManager.getLives());
    }

    // when the lives change the HUD label should show the new value
    @Test
    @DisplayName("loseLife should update the HUD lives label")
    void testLivesUpdatesHUD() {
        scoreManager.loseLife();
        assertEquals("Lives: 4", hud.getLivesLabel().getText());
    }

    // COINS TESTS

    // coins should be 0 when the game first starts
    @Test
    @DisplayName("Coins should start at 0")
    void testCoinsStartAtZero() {
        assertEquals(0, scoreManager.getCoins());
    }

    // picking up a coin should increase the count by 1
    @Test
    @DisplayName("addCoin should increase coins by 1")
    void testAddCoinIncreasesCoins() {
        scoreManager.addCoin();
        assertEquals(1, scoreManager.getCoins());
    }

    // when coins change the HUD label should show the new value
    @Test
    @DisplayName("addCoin should update the HUD coins label")
    void testCoinsUpdatesHUD() {
        scoreManager.addCoin();
        assertEquals("Coins: 1", hud.getCoinsLabel().getText());
    }

    // RESET TESTS

    // after reset score should go back to 0
    @Test
    @DisplayName("reset should set score back to 0")
    void testResetSetsScoreToZero() {
        scoreManager.incrementScore();
        scoreManager.reset();
        assertEquals(0, scoreManager.getScore());
    }

    // after reset lives should go back to 5
    @Test
    @DisplayName("reset should set lives back to 3")
    void testResetSetsLivesToThree() {
        scoreManager.loseLife();
        scoreManager.reset();
        assertEquals(5, scoreManager.getLives());
    }

    // after reset coins should go back to 0
    @Test
    @DisplayName("reset should set coins back to 0")
    void testResetSetsCoinsToZero() {
        scoreManager.addCoin();
        scoreManager.reset();
        assertEquals(0, scoreManager.getCoins());
    }

    // after reset all three hud labels should show starting values
    @Test
    @DisplayName("reset should update all HUD labels")
    void testResetUpdatesAllHUDLabels() {
        scoreManager.incrementScore();
        scoreManager.loseLife();
        scoreManager.addCoin();
        scoreManager.reset();
        assertAll(() -> assertEquals("Score: 000000", hud.getScoreLabel().getText()), () -> assertEquals("Lives: 5", hud.getLivesLabel().getText()), () -> assertEquals("Coins: 0", hud.getCoinsLabel().getText()));


    }
}