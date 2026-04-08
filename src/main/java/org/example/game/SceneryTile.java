package org.example.game;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a single background scenery tile in the scrolling road.
 *
 * Milestone 2 changes:
 *  - Removed Collidable implementation; background tiles are pure scenery
 *    and must not participate in gameplay collision detection.
 *  - Added scrollSpeed field so each tile moves itself inside update(),
 *    fitting the polymorphic GameObject game-loop pattern in GameScene.
 *
 * @author Emmanuel Ayobanjo
 */
public class SceneryTile extends GameObject {

    private final ImageView sprite;

    // Pixels per frame this tile scrolls downward
    private double scrollSpeed;

    /**
     * Creates a SceneryTile at the given position.
     *
     * @param spriteImage the tile image (one of preloaded variants)
     * @param startX      initial X position
     * @param startY      initial Y position
     * @param scrollSpeed pixels per frame the tile moves downward
     */
    public SceneryTile(Image spriteImage, double startX, double startY, double scrollSpeed) {
        super(startX, startY, spriteImage.getWidth(), spriteImage.getHeight());
        this.scrollSpeed = scrollSpeed;
        this.sprite = new ImageView(spriteImage);
        this.sprite.setX(x);
        this.sprite.setY(y);
    }

    // -----------------------------------------------------------------------
    // GameObject contract
    // -----------------------------------------------------------------------

    /**
     * Moves the tile downward by scrollSpeed each frame, then syncs the sprite.
     * Called once per game loop frame by GameScene's polymorphic update loop.
     */
    @Override
    public void update() {
        y += scrollSpeed;
        updatePosition();
    }

    /**
     * Syncs the ImageView position to the logical x/y.
     * Called by update() and by SceneryManager when recycling a tile.
     */
    public void updatePosition() {
        sprite.setX(x);
        sprite.setY(y);
    }

    @Override
    public Bounds getBounds() {
        return sprite.getBoundsInParent();
    }

    // -----------------------------------------------------------------------
    // Tile-specific methods
    // -----------------------------------------------------------------------

    /**
     * Swaps the tile's image and updates width/height to match.
     * Used by SceneryManager when recycling a tile with a new variant.
     *
     * @param newImage the replacement image; if null the sprite is cleared
     *                 and height falls back to the default 192px
     */
    public void setImage(Image newImage) {
        sprite.setImage(newImage);
        if (newImage != null) {
            this.width  = newImage.getWidth();
            this.height = newImage.getHeight();
        }
    }

    /**
     * Returns the height of the current image, or 192 if none is loaded.
     * Used by SceneryManager to calculate recycle position.
     */
    @Override
    public double getHeight() {
        return sprite.getImage() != null ? sprite.getImage().getHeight() : 192;
    }

    public ImageView getSprite() {
        return sprite;
    }

    // -----------------------------------------------------------------------
    // Scroll speed
    // -----------------------------------------------------------------------

    public double getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(double scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }
}