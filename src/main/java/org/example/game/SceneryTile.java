package org.example.game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a single scenery tile in the scrolling background.
 * Each tile holds one ImageView and tracks its own position.
 *
 * @author Miguel Cofre, Emmanuel Ayobanjo
 */
public class SceneryTile {

    private ImageView sprite;
    private double x;
    private double y;

    /**
     * Creates a SceneryTile at the given position using the provided image.
     *
     * @param spriteImage the tile image (one of 16 preloaded variants)
     * @param startX      initial X position
     * @param startY      initial Y position
     */
    public SceneryTile(Image spriteImage, double startX, double startY) {
        this.sprite = new ImageView(spriteImage);
        this.x = startX;
        this.y = startY;
        this.sprite.setX(x);
        this.sprite.setY(y);
    }

    /**
     * Swaps the tile's displayed image (used when recycling tiles with a new variant).
     *
     * @param newImage the new image to display
     */
    public void setImage(Image newImage) {
        this.sprite.setImage(newImage);
    }

    // Getters and setters
    public ImageView getSprite() {
        return sprite;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        sprite.setX(x);
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        sprite.setY(y);
    }

    /**
     * Returns the height of the tile image, used by SceneryManager
     * to calculate tile stacking and offscreen detection.
     */

    public double getHeight() {
        if (sprite.getImage() == null) return 192; // default tile height from guidelines
        return sprite.getImage().getHeight();
    }
}