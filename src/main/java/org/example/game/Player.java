package org.example.game;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents the player character in the game.
 * Inherits position/size from GameObject and implements Collidable.
 */

public class Player extends GameObject implements Collidable {

    private final ImageView sprite;

    // exact sprite dimensions from player_dummy.png
    public static final double SPRITE_W = 64.0;
    public static final double SPRITE_H = 128.0;

    // hit-box shrink factor (fraction of each dimension)
    private static final double INSET_FRAC = 0.10;

    // Movement flags
    private boolean movingLeft = false;
    private boolean movingRight = false;

    // Speed in pixels per frame
    private double speed = 5.0;

    // Screen boundaries
    private double minX;
    private double maxX;

    // score manager reference to update lives on collision
    private ScoreManager scoreManager;

    /**
     * Constructor.
     *
     * @param spriteImage Image for the player
     * @param startX      initial X position
     * @param startY      initial Y position
     * @param screenWidth screen width for boundary clamping
     */

    public Player(Image spriteImage, double startX, double startY, double screenWidth) {
        // x = left edge, width/height = actual sprite size
        super(startX, startY, SPRITE_W, SPRITE_H);
        this.sprite = new ImageView(spriteImage);
        this.sprite.setX(startX);
        this.sprite.setY(startY);
        this.maxX = screenWidth - 340.0 - SPRITE_W; // right road boundary
        this.minX = 340.0;              // left road boundary
        sprite.setFitWidth(SPRITE_W);
        sprite.setFitHeight(SPRITE_H);
        sprite.setPreserveRatio(true);
    }

    // -------------------------------
    // Movement controls
    // -------------------------------
    public void moveLeft() { movingLeft = true; }
    public void moveRight() { movingRight = true; }
    public void stopLeft() { movingLeft = false; }
    public void stopRight() { movingRight = false; }

    // -------------------------------
    // Collidable / GameObject methods
    // -------------------------------

    @Override
    public void update() {
        // This is the abstract method from GameObject
        updatePosition();
    }

    @Override
    public void updatePosition() {
        if (movingLeft) x -= speed;
        if (movingRight) x += speed;

        // Clamp to screen boundaries
        x = Math.max(minX, Math.min(maxX, x));

        sprite.setX(x);
        sprite.setY(y);
    }

    /**
     * Manual BoundingBox — no layout pass needed. x = LEFT EDGE.
     * 10 % inset on all sides trims transparent PNG edges.
     */
    @Override
    public Bounds getBounds() {
        double inX = width  * INSET_FRAC;
        double inY = height * INSET_FRAC;
        return new BoundingBox(x + inX, y + inY, width - inX * 2, height - inY * 2);
    }

    @Override
    public void onCollision(Collidable other) {
        System.out.println("[Player] hit " + other.getClass().getSimpleName());
        if (other instanceof Obstacle && scoreManager != null) {
            scoreManager.loseLife();
        }
    }

    // -------------------------------
    // Getter for sprite
    // -------------------------------
    public ImageView getSprite() {
        return sprite;
    }

    // it lets GameScene pass in the score manager
    public void setScoreManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }
}