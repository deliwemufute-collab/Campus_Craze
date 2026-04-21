package org.example.game;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Collectible coin. Extends GameObject, implements Collidable.
 * x = CENTRE. getBounds() is a manual BoundingBox.
 */
public class Coin extends GameObject implements Collidable {

    public static final double COIN_W = 32.0;
    public static final double COIN_H = 32.0;

    private final ImageView sprite;
    private boolean collected = false;

    public Coin(Image spriteImage, double centreX, double startY) {
        super(centreX, startY, COIN_W, COIN_H);
        this.sprite = new ImageView(spriteImage);
        sprite.setFitWidth(COIN_W);
        sprite.setFitHeight(COIN_H);
        sprite.setPreserveRatio(true);
        syncSprite();
    }

    @Override
    public void update() {
        y += 3.0;  // default scroll speed
        syncSprite();
    }
    @Override public void updatePosition() { syncSprite(); }

    @Override
    public Bounds getBounds() {
        return new BoundingBox(x - width / 2 + 2, y + 2, width - 4, height - 4);
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Player && !collected) {
            collected = true;
            sprite.setVisible(false);
        }
    }

    @Override public void setX(double x) { super.setX(x); syncSprite(); }
    @Override public void setY(double y) { super.setY(y); syncSprite(); }

    public void scrollDown(double speed) { y += speed; syncSprite(); }
    public boolean isOffscreen(double h) { return y > h; }
    public void reset() { collected = false; sprite.setVisible(true); }

    private void syncSprite() {
        sprite.setX(x - width / 2);
        sprite.setY(y);
    }

    public boolean   isCollected() { return collected; }
    public ImageView getSprite()   { return sprite; }
}