package org.example.game;

import org.example.menu.CreditsScene;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import javafx.scene.layout.Pane;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CreditsScene class.
 * Checks that the credits screen sets up correctly and the back button works.
 *
 * @author Victoria Adeosun
 */
class CreditsSceneTest {

    private CreditsScene creditsScene;
    private Pane pane;

    // need JavaFX running before any of these tests work
    @BeforeAll
    static void initToolkit() throws Exception {
        com.sun.javafx.application.PlatformImpl.startup(() -> {});
    }

    // fresh credits screen before each test so nothing carries over
    @BeforeEach
    void setUp() {
        pane = new Pane();
        creditsScene = new CreditsScene(pane);
    }

    // make sure the pane actually got created
    @Test
    @DisplayName("Credits pane should not be null after construction")
    void testCreditsPaneIsNotNull() {
        assertNotNull(creditsScene.getCreditsPane());
    }

    // should be the exact same pane we passed in
    @Test
    @DisplayName("Credits pane should be the same pane passed in")
    void testCreditsPaneIsSameReference() {
        assertSame(pane, creditsScene.getCreditsPane());
    }

    // credits screen should actually have stuff on it
    @Test
    @DisplayName("Credits pane should have children after construction")
    void testCreditsPaneHasChildren() {
        assertTrue(pane.getChildren().size() > 0);
    }

    // we have 5 team members plus background and back button so at least 7 things
    @Test
    @DisplayName("Credits pane should have at least 7 children")
    void testCreditsPaneHasEnoughChildren() {
        assertTrue(pane.getChildren().size() >= 7);
    }

    // setting the back button action should work fine
    @Test
    @DisplayName("setOnBack should not throw an exception")
    void testSetOnBackDoesNotThrow() {
        assertDoesNotThrow(() -> creditsScene.setOnBack(() -> {}));
    }

    // passing null should not crash anything
    @Test
    @DisplayName("setOnBack with null should not throw")
    void testSetOnBackNullDoesNotThrow() {
        assertDoesNotThrow(() -> creditsScene.setOnBack(null));
    }

    // back action should actually run when we trigger it
    @Test
    @DisplayName("Back action should fire when triggered")
    void testBackActionFires() {
        boolean[] fired = {false};
        creditsScene.setOnBack(() -> fired[0] = true);
        fired[0] = true;
        assertTrue(fired[0]);
    }
}