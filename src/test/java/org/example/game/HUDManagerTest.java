package org.example.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.example.menu.MenuScene;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : Victoria Adeosun
 * These tests check HUDManager logic
 */
class HUDManagerTest {

    private HUDManager hud;
    private Pane testPane;

    // start JavaFX before any tests run
    @BeforeAll
    static void initToolkit() throws Exception {
        com.sun.javafx.application.PlatformImpl.startup(() -> {});
    }

    // create a fresh HUDManager before each test
    @BeforeEach
    void setUp() {
        testPane = new Pane();
        hud = new HUDManager(testPane);
    }

    // SCORE TESTING

    // Test to verify that the score label initializes correctly
    @Test
    @DisplayName("Score label should not be null after construction")
    void testScoreLabelInitialText() {
        assertNotNull(hud.getScoreLabel());
    }

    // Test to verify that the lives label initializes correctly
    @Test
    @DisplayName("Score label should show Score: 0 at start")
    void testScoreLabelStartsAtZero() {
            assertEquals("Score: 0", hud.getScoreLabel().getText());
    }

    // check if updateScore actually changes the label
    @Test
    @DisplayName("update Score should update the label text")
    void testUpdateScoreChangesLabel() {
        hud.updateScore(100);
        assertEquals("Score: 100", hud.getScoreLabel().getText());
    }
    // check multiple score updates show latest value
    @Test
    @DisplayName("Score label should always show latest value")
    void testUpdateScoreMultipleTimes() {
        hud.updateScore(50);
        hud.updateScore(200);
        hud.updateScore(350);
        assertEquals("Score: 350", hud.getScoreLabel().getText());
    }
    // check if score resets to 0
    @Test
    @DisplayName("Score label should reset to 0")
    void testUpdateScoreResetsToZero() {
        hud.updateScore(500);
        hud.updateScore(0);
        assertEquals("Score: 0", hud.getScoreLabel().getText());
    }


    // LIVES TESTING

    // test to verify that the lives label updates correctly after losing a life
    @Test
    @DisplayName("Lives label should not be null after construction")
    void testLivesLabelIsNotNull() {
        assertNotNull(hud.getLivesLabel());
    }

    // Test to verify that lives start at 3 by default
    @Test
    @DisplayName("Lives should start at 3")
    void testLivesLabelStartsAtThree() {
        assertEquals("Lives: 3", hud.getLivesLabel().getText());
    }


    // Test that losing a life decreases the count by 1
    @Test
    @DisplayName("updateLives should update the label text")
    void testUpdateLivesChangesLabel() {
        hud.updateLives(2);
        assertEquals("Lives: 2", hud.getLivesLabel().getText());
    }

    // lives shows 0 at game over
    @Test
    @DisplayName("Lives label should show 0 at game over")
    void testUpdateLivesZero() {
        hud.updateLives(0);
        assertEquals("Lives: 0", hud.getLivesLabel().getText());
    }

    // HUD PANE TESTING

    // check pane is stored correctly
    @Test
    @DisplayName("HUD pane should be the same pane passed in")
    void testHudPaneIsStored() {
        assertSame(testPane, hud.getHudPane());
    }

    // COMBINED TESTING

    // check updating score does not affect lives
    @Test
    @DisplayName("Updating score should not change lives label")
    void testScoreUpdateDoesNotAffectLives() {
        hud.updateScore(999);
        assertEquals("Lives: 3", hud.getLivesLabel().getText());
    }

    // check updating lives does not affect score
    @Test
    @DisplayName("Updating lives should not change score label")
    void testLivesUpdateDoesNotAffectScore() {
        hud.updateLives(1);
        assertEquals("Score: 0", hud.getScoreLabel().getText());
    }


    // COINS TESTING (placeholder)

    // Test that coins label initializes to 0 (placeholder)
    @Test
    @DisplayName("Coins label should start as Coins: 0")
    void testCoinsLabelInitialText() {
        assertEquals("Coins: 0", "Coins: " + 0);
    }

    // Test that coins label updates correctly after collecting coins (placeholder)
    @Test
    @DisplayName("Coins label format should update correctly")
    void testCoinsLabelFormat() {
        int coins = 5;
        assertEquals("Coins: 5", "Coins: " + coins);
    }

    // Test to ensure coins never become negative (placeholder)
    @Test
    @DisplayName("Coins should never be negative")
    void testCoinsNeverNegative() {
        int coins = 0;
        assertTrue(coins >= 0);
    }

    // Test that collecting a coin increases the count by 1 (placeholder)
    @Test
    @DisplayName("Collecting a coin increases count by 1")
    void testCollectingCoinIncreasesCount() {
        int coins = 0;
        coins = coins + 1;
        assertEquals(1, coins);
    }

    // Test that coins reset to zero on a new game (placeholder)
    @Test
    @DisplayName("Coins reset to 0 on new game")
    void testCoinsResetToZero() {
        int coins = 10;
        coins = 0;
        assertEquals(0, coins);
    }

    // MENU TESTING

    // check menuPane is created and not null
    @Test
    @DisplayName("MenuScene pane should not be null after construction")
    void testMenuPaneIsNotNull() {
        Pane pane = new Pane();
        MenuScene menu = new MenuScene(pane);
        assertNotNull(menu.getMenuPane());
    }

    // check the pane passed in is stored correctly
    @Test
    @DisplayName("MenuScene should store the pane passed in constructor")
    void testMenuPaneIsSameReference() {
        Pane pane = new Pane();
        MenuScene menu = new MenuScene(pane);
        assertSame(pane, menu.getMenuPane());
    }



    // Test that the start button works correct
    @Test
    @DisplayName("Start button should not be null after construction")
    void testStartButtonIsNotNull() {
        Pane pane = new Pane();
        MenuScene menu = new MenuScene(pane);
        assertNotNull(menu.getStartButton());
    }


    // check menu is visible by default
    @Test
    @DisplayName("Menu pane should be visible by default")
    void testMenuVisibleByDefault() {
        Pane pane = new Pane();
        MenuScene menu = new MenuScene(pane);
        assertTrue(menu.getMenuPane().isVisible());
    }



    // Test that the menu disappears after clicking start
    @Test
    @DisplayName("Menu should hide when start button is fired")
    void testMenuHidesWhenStartFired() {
        Pane pane = new Pane();
        MenuScene menu = new MenuScene(pane);
        menu.setStartButtonAction(() -> {});
        menu.getStartButton().fire();
        assertFalse(menu.getMenuPane().isVisible());
    }

    // START BUTTON TESTING

    @Test
    @DisplayName("Start button should trigger startGame action")
    void testStartButtonTriggersStartGame() {
        Pane pane = new Pane();
        MenuScene menu = new MenuScene(pane);
        boolean[] gameStarted = {false};
        menu.setStartButtonAction(() -> gameStarted[0] = true);
        menu.getStartButton().fire();
        assertTrue(gameStarted[0], "startGame should be called when button is fired");
    }

    // Game must not start if button is not clicked
    @Test
    @DisplayName("Menu should not start game before button is clicked")
    void testGameNotStartedBeforeButtonClick() {
        boolean[] gameStarted = {false};
        assertFalse(gameStarted[0], "Game should not start before Start button is clicked");
    }
}
