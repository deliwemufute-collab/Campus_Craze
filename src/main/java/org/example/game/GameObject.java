package org.example.game;

import javafx.geometry.Bounds;

/**
 * Abstract parent class for all game objects.
 * Provides basic position and size properties,
 * as well as update() method to be implemented by subclasses.
 * Implements common functionality for Milestone 2 architecture.
 */
public abstract class GameObject {

    // --- Position and size ---
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    // --- Constructor ---
    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // --- Getters and Setters ---
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    // --- Abstract methods ---

    /**
     * Updates the state of the game object each frame.
     * Must be implemented by subclasses (movement, animation, etc.).
     */
    public abstract void update();

    /**
     * Returns the bounding box of this object for collision detection.
     * Subclasses must implement based on their graphical representation.
     *
     * @return Bounds object representing the collision area
     */
    public abstract Bounds getBounds();
}