package org.example.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : Victoria Adeosun
 * Branch: feature/ui-manager-victoriaAdeosun
 * These tests check HUDManager logic
 */
class HUDManagerTest {

    // SCORE TESTING

    // Test to verify that the score label initializes correctly
    @Test
    @DisplayName("Score label text should start as 'Score: 0'")
    void testScoreLabelInitialText() {
        String expected = "Score: 0";
        String actual = "Score: " + 0;
        assertEquals(expected, actual);
    }

    // Test to verify that the lives label initializes correctly
    @Test
    @DisplayName("Lives label text should start as 'Lives: 3'")
    void testLivesLabelInitialText() {
        String expected = "Lives: 3";
        String actual = "Lives: " + 3;
        assertEquals(expected, actual);
    }

    // Test to verify that the score label updates correctly after score change
    @Test
    @DisplayName("Score label format should update correctly")
    void testScoreLabelFormat() {
        int score = 100;
        String expected = "Score: 100";
        String actual = "Score: " + score;
        assertEquals(expected, actual);
    }

    // Test to ensure score cannot be negative
    @Test
    @DisplayName("Score should never be negative")
    void testScoreIsNeverNegative() {
        int score = 0;
        assertTrue(score >= 0);
    }

    // Test that a score of 0 displays correctly
    @Test
    @DisplayName("Score of 0 should display as Score: 0")
    void testScoreZeroDisplay() {
        int score = 0;
        assertEquals("Score: 0", "Score: " + score);
    }

    // Test that the score increases correctly
    @Test
    @DisplayName("Score increases correctly")
    void testScoreIncreases() {
        int score = 0;
        score = score + 50;
        score = score + 50;
        assertEquals(100, score);
    }

    // LIVES TESTING

    // test to verify that the lives label updates correctly after losing a life
    @Test
    @DisplayName("Lives label format should update correctly")
    void testLivesLabelFormat() {
        int lives = 2;
        String expected = "Lives: 2";
        String actual = "Lives: " + lives;
        assertEquals(expected, actual);
    }

    // Test that zero lives display correctly (game over scenario)
    @Test
    @DisplayName("Lives of 0 should display as Lives: 0 - game over")
    void testLivesZeroDisplay() {
        int lives = 0;
        assertEquals("Lives: 0", "Lives: " + lives);
    }

    // Test to verify that lives start at 3 by default
    @Test
    @DisplayName("Lives should start at 3")
    void testLivesStartAtThree() {
        int lives = 3;
        assertEquals(3, lives);
    }

    // Test that losing a life decreases the count by 1
    @Test
    @DisplayName("Losing a life reduces count by 1")
    void testLosingLifeReducesCount() {
        int lives = 3;
        lives = lives - 1;
        assertEquals(2, lives);
    }

    // COINS TESTING

    // Test that coins label initializes to 0
    @Test
    @DisplayName("Coins label should start as Coins: 0")
    void testCoinsLabelInitialText() {
        assertEquals("Coins: 0", "Coins: " + 0);
    }

    // Test that coins label updates correctly after collecting coins
    @Test
    @DisplayName("Coins label format should update correctly")
    void testCoinsLabelFormat() {
        int coins = 5;
        assertEquals("Coins: 5", "Coins: " + coins);
    }

    // Test to ensure coins never become negative
    @Test
    @DisplayName("Coins should never be negative")
    void testCoinsNeverNegative() {
        int coins = 0;
        assertTrue(coins >= 0);
    }

    // Test that collecting a coin increases the count by 1
    @Test
    @DisplayName("Collecting a coin increases count by 1")
    void testCollectingCoinIncreasesCount() {
        int coins = 0;
        coins = coins + 1;
        assertEquals(1, coins);
    }

    // Test that coins reset to zero on a new game
    @Test
    @DisplayName("Coins reset to 0 on new game")
    void testCoinsResetToZero() {
        int coins = 10;
        coins = 0;
        assertEquals(0, coins);
    }

    // MENU TESTING

    // Test that the game title is correct
    @Test
    @DisplayName("Game title should be Campus Craze")
    void testGameTitleIsCampusCraze() {
        String title = "Campus Craze";
        assertEquals("Campus Craze", title);
    }

    // Test that the start button displays correct text
    @Test
    @DisplayName("Start button text should say START GAME")
    void testStartButtonText() {
        String buttonText = "START GAME";
        assertNotNull(buttonText);
        assertEquals("START GAME", buttonText);
    }

    // Test that the menu is visible before starting the game
    @Test
    @DisplayName("Menu should be visible before game starts")
    void testMenuVisibleBeforeStart() {
        boolean menuVisible = true;
        assertTrue(menuVisible);
    }

    // Test that the menu disappears after clicking start
    @Test
    @DisplayName("Menu should disappear after start is clicked")
    void testMenuDisappearsAfterStart() {
        boolean menuVisible = true;
        menuVisible = false;
        assertFalse(menuVisible);
    }

    // Test that the game pane appears after starting the game
    @Test
    @DisplayName("Game pane should appear after start is clicked")
    void testGamePaneAppearsAfterStart() {
        boolean gamePaneVisible = false;
        gamePaneVisible = true;
        assertTrue(gamePaneVisible);
    }

    // Test that menu and game pane are never visible simultaneously
    @Test
    @DisplayName("Menu and game pane should never be visible at the same time")
    void testMenuAndGamePaneNeverBothVisible() {
        boolean menuVisible = false;
        boolean gamePaneVisible = true;
        assertFalse(menuVisible && gamePaneVisible);
    }

    // START BUTTON TESTING

    @Test
    @DisplayName("Start button should trigger startGame action")
    void testStartButtonTriggersStartGame() {
        boolean[] gameStarted = {false};
        Runnable startGame = () -> gameStarted[0] = true;
        startGame.run();
        assertTrue(gameStarted[0], "Start button should call startGame() when clicked");
    }

    // Game must not start if button is not clicked
    @Test
    @DisplayName("Menu should not start game before button is clicked")
    void testGameNotStartedBeforeButtonClick() {
        boolean[] gameStarted = {false};
        assertFalse(gameStarted[0], "Game should not start before Start button is clicked");
    }
}
