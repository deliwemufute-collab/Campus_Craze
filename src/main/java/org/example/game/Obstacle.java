package org.example.game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Obstacle {

    private final ImageView sprite;
    private double x;
    private double y;

    public Obstacle(Image spriteImage, double startX, double startY) {
        this.sprite = new ImageView(spriteImage);
        this.x = startX;
        this.y = startY;

        sprite.setFitWidth(80);
        sprite.setFitHeight(80);
        sprite.setPreserveRatio(true);

        sprite.setX(x - 40);
        sprite.setY(y);
    }

    public ImageView getSprite() {
        return sprite;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        sprite.setX(x - 40);
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        sprite.setY(y);
    }

    public void scrollDown(double speed) {
        setY(y + speed);
    }

    public boolean isOffscreen(double screenHeight) {
        return y > screenHeight;
    }
}