package org.example.game;

import javafx.geometry.Bounds;

/**
 * Interface for all objects that can collide in the game world.
 * Ensures implementing classes provide bounding information and react to collisions.
 */
public interface Collidable {

    /**
     * Returns the bounding box of the object for collision detection.
     *
     * @return Bounds representing the collision area
     */
    Bounds getBounds();

    /**
     * Updates the object's position each frame.
     * Useful for moving objects like Player, Obstacles, SceneryTile.
     */
    void updatePosition();

    /**
     * Called when this object collides with another Collidable.
     * Each implementing class defines its own reaction (e.g., stop movement, reduce lives).
     *
     * @param other the Collidable this object collided with
     */
    void onCollision(Collidable other);
}