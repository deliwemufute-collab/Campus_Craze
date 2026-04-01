package org.example.game;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents an obstacle in the game.
 * Extends GameObject and implements Collidable.
 */
public class Obstacle extends GameObject implements Collidable {

    private final ImageView sprite;

    /**
     * Constructor.
     *
     * @param spriteImage Image of the obstacle
     * @param startX      initial X position
     * @param startY      initial Y position
     */
    public Obstacle(Image spriteImage, double startX, double startY) {
        super(startX, startY, 80, 80); // using fixed width/height
        this.sprite = new ImageView(spriteImage);
        sprite.setFitWidth(width);
        sprite.setFitHeight(height);
        sprite.setPreserveRatio(true);
        sprite.setX(x - width / 2);
        sprite.setY(y);
    }

    // -------------------------------
    // GameObject / Collidable methods
    // -------------------------------
    @Override
    public void update() {
        updatePosition();
    }

    @Override
    public void updatePosition() {
        // Example: simple vertical scroll (called from GameScene)
        sprite.setX(x - width / 2);
        sprite.setY(y);
    }

    @Override
    public Bounds getBounds() {
        return sprite.getBoundsInParent();
    }

    @Override
    public void onCollision(Collidable other) {
        System.out.println("Obstacle collided with: " + other.getClass().getSimpleName());
        // TODO: define collision behavior if needed
    }

    // -------------------------------
    // Specific to Obstacle
    // -------------------------------
    public void scrollDown(double speed) {
        y += speed;
        updatePosition();
    }

    public boolean isOffscreen(double screenHeight) {
        return y > screenHeight;
    }

    // -------------------------------
    // Getter for sprite
    // -------------------------------
    public ImageView getSprite() {
        return sprite;
    }
}