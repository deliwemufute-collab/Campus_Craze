package org.example.game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// This class will represent any obstacle on the game
public class Obstacle {

    // Obstacle sprite. Will create a dummy obstacle
    private ImageView sprite;

    // Obstacle position. To be used to move obstacles during gameplay.
    private double x;
    private double y;

    // Constructor
    public Obstacle(Image spriteImage, double startX, double startY) {
        this.sprite = new ImageView(spriteImage);
        this.x = startX;
        this.y = startY;
        this.sprite.setX(x);
        this.sprite.setY(y);
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

    // Placeholder for obstacle-specific logic
    public void update() {
        // To be implemented by PAAKHI AGRAWALA
    }
}