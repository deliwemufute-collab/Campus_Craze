package org.example.game;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents the player character in the game.
 * Inherits position/size from GameObject and implements Collidable.
 */
public class Player extends GameObject implements Collidable {

    private ImageView sprite;

    // Movement flags
    private boolean movingLeft = false;
    private boolean movingRight = false;

    // Speed in pixels per frame
    private double speed = 5;

    // Screen boundaries
    private double minX = 0;
    private double maxX;

    // score manager reference to update lives on collision
    private ScoreManager scoreManager;

    /**
     * Constructor.
     *
     * @param spriteImage Image for the player
     * @param startX      initial X position
     * @param startY      initial Y position
     * @param screenWidth screen width for boundary clamping
     */
    public Player(Image spriteImage, double startX, double startY, double screenWidth) {
        super(startX, startY, spriteImage.getWidth(), spriteImage.getHeight());
        this.sprite = new ImageView(spriteImage);
        this.sprite.setX(startX);
        this.sprite.setY(startY);
        this.maxX = screenWidth;
    }

    // -------------------------------
    // Movement controls
    // -------------------------------
    public void moveLeft() { movingLeft = true; }
    public void moveRight() { movingRight = true; }
    public void stopLeft() { movingLeft = false; }
    public void stopRight() { movingRight = false; }

    // -------------------------------
    // Collidable / GameObject methods
    // -------------------------------

    @Override
    public void update() {
        // This is the abstract method from GameObject
        updatePosition();
    }

    @Override
    public void updatePosition() {
        if (movingLeft) x -= speed;
        if (movingRight) x += speed;

        // Clamp to screen boundaries
        if (x < minX) x = minX;
        if (x > maxX - width) x = maxX - width;

        sprite.setX(x);
        sprite.setY(y);
    }

    @Override
    public Bounds getBounds() {
        return sprite.getBoundsInParent();
    }

    @Override
    public void onCollision(Collidable other) {
        if (other == null) return;

        System.out.println("Player collided with: " + other.getClass().getSimpleName());

        // If player hits an obstacle, lose a life
        if (other instanceof Obstacle && scoreManager != null) {
            scoreManager.loseLife();
        }
    }

    // -------------------------------
    // Getter for sprite
    // -------------------------------
    public ImageView getSprite() {
        return sprite;
    }

    // it lets GameScene pass in the score manager
    public void setScoreManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }
}