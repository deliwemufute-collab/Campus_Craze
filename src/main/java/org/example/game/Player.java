package org.example.game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// This will represent the player in game
public class Player {

    // Player sprite. Will create assets for testing
    private ImageView sprite;

    // Player position attributes. To be updated during gameplay
    private double x;
    private double y;

    // Player size. Let's use the images sizes with getImage().
    // But we could play with this as a reaction to power ups??
    private double width;
    private double height;

    // Movement flags
    private boolean movingLeft = false;
    private boolean movingRight = false;

    // Speed
    private double speed = 5;

    // Screen boundaries
    private double minX = 0;
    private double maxX = 1024;

    // Constructor
    public Player(Image spriteImage, double startX, double startY) {
        this.sprite = new ImageView(spriteImage);
        this.x = startX;
        this.y = startY;
        this.sprite.setX(x);
        this.sprite.setY(y);

        this.width = sprite.getImage().getWidth();
        this.height = sprite.getImage().getHeight();
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
    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }

    // Placeholder methods for movement
    public void moveLeft() {
        movingLeft = true;
    }

    public void moveRight() {
        movingRight = true;

    }
    public void stopLeft() {
        movingLeft = false;
    }

    public void stopRight() {
        movingRight = false;
    }

    public void updatePosition() {
        // Update sprite position on screen if needed. Handled by DELIWE MUFUTE
        if (movingLeft) {
            x -= speed;
        }

        if (movingRight) {
            x += speed;
        }

        // Keep player inside screen
        if (x < minX) {
            x = minX;
        }

        if (x > maxX - width) {
            x = maxX - width;
        }

        sprite.setX(x);
        sprite.setY(y);
    }
}