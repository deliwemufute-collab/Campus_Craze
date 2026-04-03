package org.example.game;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a single scenery tile in the scrolling background.
 * Extends GameObject and implements Collidable for Milestone 2 architecture.
 */
public class SceneryTile extends GameObject implements Collidable {

    private final ImageView sprite;

    /**
     * Creates a SceneryTile at the given position using the provided image.
     *
     * @param spriteImage the tile image (one of preloaded variants)
     * @param startX      initial X position
     * @param startY      initial Y position
     */
    public SceneryTile(Image spriteImage, double startX, double startY) {
        super(startX, startY, spriteImage.getWidth(), spriteImage.getHeight());
        this.sprite = new ImageView(spriteImage);
        this.sprite.setX(x);
        this.sprite.setY(y);
    }

    // -------------------------------
    // GameObject / Collidable Methods
    // -------------------------------
    @Override
    public void update() {
        updatePosition();
    }

    @Override
    public void updatePosition() {
        sprite.setX(x);
        sprite.setY(y);
    }

    @Override
    public Bounds getBounds() {
        return sprite.getBoundsInParent();
    }

    @Override
    public void onCollision(Collidable other) {
        // Scenery tiles usually do not handle collisions
        // But included to satisfy Collidable interface
        // Now... in this milestone you will add the buildings
        // tiles on the sides. I think the best way to do that is
        // by extending from this class and then implementing
        // and onCollision on that child class.... up to you
    }

    // -------------------------------
    // Tile-specific methods
    // -------------------------------

    public void setImage(Image newImage) {
        sprite.setImage(newImage);
        this.width = newImage.getWidth();
        this.height = newImage.getHeight();
    }

    public double getHeight() {
        return sprite.getImage() != null ? sprite.getImage().getHeight() : 192;
    }

    public ImageView getSprite() {
        return sprite;
    }
}