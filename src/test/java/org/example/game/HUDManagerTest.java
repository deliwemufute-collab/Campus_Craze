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

    // score label shows Score: 0 at start
    @Test
    @DisplayName("Score label should show Score: 000000 at start")
    void testScoreLabelStartsAtZero() {
            assertEquals("Score: 000000", hud.getScoreLabel().getText());
    }

    // check if updateScore actually changes the label
    @Test
    @DisplayName("update Score should update the label text")
    void testUpdateScoreChangesLabel() {
        hud.updateScore(100);
        assertEquals("Score: 000100", hud.getScoreLabel().getText());
    }
    // check multiple score updates show latest value
    @Test
    @DisplayName("Score label should always show latest value")
    void testUpdateScoreMultipleTimes() {
        hud.updateScore(50);
        hud.updateScore(200);
        hud.updateScore(350);
        assertEquals("Score: 000350", hud.getScoreLabel().getText());
    }
    // check if score resets to 0
    @Test
    @DisplayName("Score label should reset to 0")
    void testUpdateScoreResetsToZero() {
        hud.updateScore(500);
        hud.updateScore(0);
        assertEquals("Score: 000000", hud.getScoreLabel().getText());
    }
    // testing if large score values display correctly
    @Test
    @DisplayName("Score label should handle large values correctly")
    void testScoreLargeValue() {
        hud.updateScore(99999);
        assertEquals("Score: 099999", hud.getScoreLabel().getText());
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
    @DisplayName("Lives should start at 5")
    void testLivesLabelStartsAtThree() {
        assertEquals("Lives: 5", hud.getLivesLabel().getText());
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

    //  lives decrease correctly on collision
    @Test
    @DisplayName("Lives should decrease by 1 when player loses a life")
    void testLivesDecrementOnCollision() {
        hud.updateLives(3);
        hud.updateLives(2);
        assertEquals("Lives: 2", hud.getLivesLabel().getText());
    }

    // if the game is over state when lives reach 0
    @Test
    @DisplayName("Lives should reach 0 at game over")
    void testLivesReachZeroAtGameOver() {
        hud.updateLives(1);
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
        assertEquals("Lives: 5", hud.getLivesLabel().getText());
    }

    // check updating lives does not affect score
    @Test
    @DisplayName("Updating lives should not change score label")
    void testLivesUpdateDoesNotAffectScore() {
        hud.updateLives(1);
        assertEquals("Score: 000000", hud.getScoreLabel().getText());
    }

    // check if all three labels update independently
    @Test
    @DisplayName("All HUD labels should update independently")
    void testAllHUDLabelsUpdateIndependently() {
        hud.updateScore(100);
        hud.updateLives(2);
        hud.updateCoins(5);
        assertAll(() -> assertEquals("Score: 000100", hud.getScoreLabel().getText()), () -> assertEquals("Lives: 2", hud.getLivesLabel().getText()), () -> assertEquals("Coins: 5", hud.getCoinsLabel().getText()));
    }

    // checking if the HUDManager always starts with defaults
    @Test
    @DisplayName("HUDManager should always start with default values")
    void testNewHUDManagerStartsWithDefaults() {
        HUDManager freshHud = new HUDManager(new Pane());
        assertAll(() -> assertEquals("Score: 000000", freshHud.getScoreLabel().getText()), () -> assertEquals("Lives: 5", freshHud.getLivesLabel().getText()), () -> assertEquals("Coins: 0", freshHud.getCoinsLabel().getText()));
    }


    // COINS TESTING

    // Test that coins is created and not null
    @Test
    @DisplayName("Coins label should not be null after construction")
    void testCoinsLabelIsNotNull() {
        assertNotNull(hud.getCoinsLabel());
    }

    // check that the coins label starts at 0
    @Test
    @DisplayName("Coins label should show Coins: 0 at the start")
    void testCoinsLabelStartsAtZero() {
        assertEquals("Coins: 0", hud.getCoinsLabel().getText());
    }

    // updateCoins actually changes the label
    @Test
    @DisplayName("updateCoins should update the label text")
    void testUpdateCoinsChangesLabel() {
        hud.updateCoins(5);
        assertEquals("Coins: 5", hud.getCoinsLabel().getText());
    }

    // collecting multiple coins shows latest value
    @Test
    @DisplayName("Coins label should always show latest value")
    void testUpdateCoinsMultipleTimes() {
        hud.updateCoins(1);
        hud.updateCoins(2);
        hud.updateCoins(3);
        assertEquals("Coins: 3", hud.getCoinsLabel().getText());
    }

    // check coins can reset to 0 on new game
    @Test
    @DisplayName("Coins label should reset to 0")
    void testUpdateCoinsResetsToZero() {
        hud.updateCoins(10);
        hud.updateCoins(0);
        assertEquals("Coins: 0", hud.getCoinsLabel().getText());
    }

    // updating coins does not affect score
    @Test
    @DisplayName("Updating coins should not change score label")
    void testCoinsUpdateDoesNotAffectScore() {
        hud.updateCoins(5);
        assertEquals("Score: 000000", hud.getScoreLabel().getText());
    }

    // checking that updating coins does not affect lives
    @Test
    @DisplayName("Updating coins should not change lives label")
    void testCoinsUpdateDoesNotAffectLives() {
        hud.updateCoins(5);
        assertEquals("Lives: 5", hud.getLivesLabel().getText());
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
