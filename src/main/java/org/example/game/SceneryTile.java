package org.example.game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// This will represent a SINGLE scenery tile. Create an instance per tile
public class SceneryTile {

    // Tile sprite. May create 3 dummy tiles for scrolling
    private ImageView sprite;

    // Tile position. Update by timeFrame on JavaFx
    private double x;
    private double y;

    // Constructor
    public SceneryTile(Image spriteImage, double startX, double startY) {
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

    // Placeholder for tile-specific logic
    public void update() {
        // To be implemented by EMMANUEL AYOBANJO
    }
}