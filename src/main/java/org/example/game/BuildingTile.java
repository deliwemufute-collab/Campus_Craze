package org.example.game;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;

/**
 * Represents a side building tile that lines the edges of the road.
 *
 * Extends SceneryTile to inherit scrolling behaviour, and implements
 * Collidable so that the collision detection loop in GameScene picks up
 * building contacts (e.g. player drifts into the kerb).
 *
 * Architecture rationale (MC + Emmanuel, Milestone 2):
 *   SceneryTile — background road surface, NOT Collidable.
 *   BuildingTile — side building overlay, IS Collidable.
 * This keeps the collision loop clean: only meaningful contacts fire.
 *
 * @author Emmanuel Ayobanjo
 */
public class BuildingTile extends SceneryTile implements Collidable {

    /**
     * Creates a BuildingTile at the given position.
     *
     * @param spriteImage the building image
     * @param startX      initial X position
     * @param startY      initial Y position
     * @param scrollSpeed pixels per frame this tile scrolls downward
     */
    public BuildingTile(Image spriteImage, double startX, double startY, double scrollSpeed) {
        super(spriteImage, startX, startY, scrollSpeed);
    }

    // -----------------------------------------------------------------------
    // Collidable contract
    // -----------------------------------------------------------------------

    /**
     * Returns the bounding box for collision detection.
     * Delegates to the inherited ImageView sprite.
     */
    @Override
    public Bounds getBounds() {
        return getSprite().getBoundsInParent();
    }

    /**
     * Syncs the sprite position to the logical x/y coordinates.
     * Called by the Collidable contract; delegates to SceneryTile.updatePosition().
     */
    @Override
    public void updatePosition() {
        super.updatePosition();
    }

    /**
     * Called when the player or an obstacle enters this building tile's bounds.
     * Currently logs the contact; gameplay consequence (bounce, slow, game over)
     * to be defined in a later milestone.
     *
     * @param other the Collidable that contacted this tile (may be null)
     */
    @Override
    public void onCollision(Collidable other) {
        if (other == null) return;
        // TODO: trigger player boundary reaction (Milestone 3+)
    }
}