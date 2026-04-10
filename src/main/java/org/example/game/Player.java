package org.example.game;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents the player character in the game.
 * Inherits position/size from GameObject and implements Collidable.
 */
public class Player extends GameObject implements Collidable {

    private ImageView sprite;

    // Movement flags
    private boolean movingLeft = false;
    private boolean movingRight = false;

    // Speed in pixels per frame
    private double speed = 5;

    // Screen boundaries
    private double minX = 0;
    private double maxX;

    // score manager reference to update lives on collision
    private ScoreManager scoreManager;

    // Cooldown to prevent collision firing every frame for the same contact
    private long lastBuildingCollisionTime = 0;
    private static final long BUILDING_COLLISION_COOLDOWN_MS = 500; // half a second

    /**
     * Constructs a new Player instance with specified sprite image, initial position,
     * and road boundaries.
     *
     * @param spriteImage the image used for the player's sprite
     * @param startX the initial X coordinate of the player's position
     * @param startY the initial Y coordinate of the player's position
     * @param roadLeft the left boundary of the road (minimum X position the player can occupy)
     * @param roadRight the right boundary of the road (maximum X position the player can occupy)
     */
    public Player(Image spriteImage, double startX, double startY,
                  double roadLeft, double roadRight) {
        super(startX, startY, spriteImage.getWidth(), spriteImage.getHeight());
        this.sprite = new ImageView(spriteImage);
        this.sprite.setX(startX);
        this.sprite.setY(startY);
        this.minX = roadLeft;
        this.maxX = roadRight;   // updatePosition() already does maxX - width, so this is correct

        if (this.x < minX) this.x = minX;
        if (this.x + width > maxX) this.x = maxX - width;
        sprite.setX(this.x);
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
        if (x < minX) x = minX;
        if (x > maxX - width) x = maxX - width;

        sprite.setX(x);
        sprite.setY(y);
    }

    @Override
    public Bounds getBounds() {
        return sprite.getBoundsInParent();
    }

    // Road boundary getters — used by tests and collision snap logic
    public double getRoadMinX() { return minX; }
    public double getRoadMaxX() { return maxX; }

    /**
     * Handles the collision event when the player collides with another
     * object in the game world. The specific collision behavior depends
     * on the type of the other object:
     * - If the collided object is an instance of {@code Obstacle}, the player
     *   loses a life via the {@code ScoreManager}.
     * - If the collided object is an instance of {@code BuildingTile},
     *   the player snaps back to ensure they remain within road boundaries.
     *
     * @param other the {@code Collidable} object that this player collided
     *              with. If {@code null}, no action is taken.
     */
    @Override
    public void onCollision(Collidable other) {
        if (other == null) return;

        if (other instanceof Obstacle && scoreManager != null) {
            scoreManager.loseLife();
        }

        // Snap to road boundary if colliding with a building
        if (other instanceof BuildingTile) {
            long now = System.currentTimeMillis();
            if (now - lastBuildingCollisionTime < BUILDING_COLLISION_COOLDOWN_MS) return;
            lastBuildingCollisionTime = now;

            // Only reaches here once per cooldown window
            System.out.println("Player collided with: " + other.getClass().getSimpleName());


            if (x < minX) {
                x = minX;
            } else if (x + width > maxX) {
                x = maxX - width;
            }
            sprite.setX(x);
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