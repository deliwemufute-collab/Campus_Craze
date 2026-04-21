package org.example.game;

/**
 * This class Manages the score, lives, and coins during gameplay.
 * Connects to HUDManager to update the screen display
 * whenever the player earns points, collects coins, or loses a life.
 *
 * @author Victoria Adeosun
 */

public class ScoreManager {
    private int score;
    private int coins;
    private int lives;
    private HUDManager hudManager;
    private Runnable onGameOver;

    // set up the score manager with a reference to the HUD so we can update it
    public ScoreManager(HUDManager hudManager) {
        this.hudManager = hudManager;
        reset();
    }

    // reset everything back to the start for a new game
    public void reset() {
        score = 0;
        coins = 0;
        lives = 3;
        hudManager.updateScore(score);
        hudManager.updateCoins(coins);
        hudManager.updateLives(lives);
    }

    // call it every frame to increase score over time
    public void incrementScore() {
        score++;
        hudManager.updateScore(score);
    }

    // when player picks up a coin it updates
    public void addCoin() {
        coins++;
        hudManager.updateCoins(coins);
    }

    // when player hits an obstacle lives decrease
    public void loseLife() {
        lives = Math.max(0, lives - 1);
        hudManager.updateLives(lives);
        // trigger game over if no lives left
        if (lives == 0 && onGameOver != null) {
            onGameOver.run();
        }
    }

    // lets GameScene pass in what to do when game over happens
    public void setOnGameOver(Runnable onGameOver) {
        this.onGameOver = onGameOver;
    }

    // getters for testing
    public int getScore() { return score; }
    public int getCoins() { return coins; }
    public int getLives() { return lives; }
}